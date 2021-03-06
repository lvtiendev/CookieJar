/* JNativeHook: Global keyboard and mouse hooking for Java.
 * Copyright (C) 2006-2012 Alexander Barker.  All Rights Received.
 * http://code.google.com/p/jnativehook/
 *
 * JNativeHook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNativeHook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <pthread.h>
#include <sys/time.h>

#include <X11/Xlibint.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/extensions/record.h>

#include "NativeErrors.h"
#include "NativeGlobals.h"
#include "NativeHelpers.h"
#include "NativeThread.h"
#include "NativeToJava.h"
#include "XInputHelpers.h"
#include "XWheelCodes.h"

// For this struct, refer to libxnee.
typedef union {
	unsigned char		type;
	xEvent				event;
	xResourceReq		req;
	xGenericReply		reply;
	xError				error;
	xConnSetupPrefix	setup;
} XRecordDatum;

// Exception global for thread initialization.
static Exception thread_ex;

// Mouse globals.
static unsigned short click_count = 0;
static long click_time = 0;
static bool mouse_dragged = false;

// The pointer to the X11 display accessed by the callback.
static Display *disp_ctrl;
static XRecordContext context;

// Thread and hook handles.
#ifdef XRECORD_ASYNC
static volatile bool running;
#endif
static pthread_mutex_t hookRunningMutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_mutex_t hookControlMutex;
static pthread_t hookThreadId;

static void LowLevelProc(XPointer UNUSED(pointer), XRecordInterceptData *hook) {
	if (hook->category == XRecordFromServer || hook->category == XRecordFromClient) {
		JNIEnv *env = NULL;
		if (disp_ctrl != NULL && (*jvm)->GetEnv(jvm, (void **)(&env), jni_version) == JNI_OK) {
			// Check and make sure the thread is stull running to avoid the
			// potential crash associated with late event arrival.  This code is
			// guaranteed to run after all thread start.
			if (pthread_mutex_trylock(&hookRunningMutex) != 0) {
				// Get XRecord data.
				XRecordDatum *data = (XRecordDatum *) hook->data;

				// Native event data.
				int event_type = data->type;
				BYTE event_code = data->event.u.u.detail;
				int event_mask = data->event.u.keyButtonPointer.state;
				int event_root_x = data->event.u.keyButtonPointer.rootX;
				int event_root_y = data->event.u.keyButtonPointer.rootY;

				struct timeval  time_val;
				gettimeofday(&time_val, NULL);
				jlong event_time = (time_val.tv_sec * 1000) + (time_val.tv_usec / 1000);
				KeySym keysym;
				wchar_t keytxt;

				// Java event data.
				JKeyDatum jkey;
				jint jbutton;
				jint jscrollType, jscrollAmount, jwheelRotation;
				jint jmodifiers;

				// Java event object.
				jobject objKeyEvent, objMouseEvent, objMouseWheelEvent;

				switch (event_type) {
					case KeyPress:
						#ifdef DEBUG
						fprintf(stdout, "LowLevelProc(): Key pressed. (%i)\n", event_code);
						#endif

						keysym = KeyCodeToKeySym(event_code, event_mask);
						jkey = NativeToJKey(keysym);
						jmodifiers = NativeToJEventMask(event_mask);

						// Fire key pressed event.
						objKeyEvent = (*env)->NewObject(
												env,
												clsKeyEvent,
												idKeyEvent,
												org_jnativehook_keyboard_NativeKeyEvent_NATIVE_KEY_PRESSED,
												event_time,
												jmodifiers,
												event_code,
												jkey.keycode,
												org_jnativehook_keyboard_NativeKeyEvent_CHAR_UNDEFINED,
												jkey.location);
						(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objKeyEvent);

						// Check to make sure the key is printable.
						keytxt = KeySymToUnicode(keysym);
						if (keytxt != 0x0000) {
							// Fire key typed event.
							objKeyEvent = (*env)->NewObject(
													env,
													clsKeyEvent,
													idKeyEvent,
													org_jnativehook_keyboard_NativeKeyEvent_NATIVE_KEY_TYPED,
													event_time,
													jmodifiers,
													event_code,
													org_jnativehook_keyboard_NativeKeyEvent_VK_UNDEFINED,
													(jchar) keytxt,
													jkey.location);
							(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objKeyEvent);
						}
						break;

					case KeyRelease:
						#ifdef DEBUG
						fprintf(stdout, "LowLevelProc(): Key released. (%i)\n", event_code);
						#endif

						keysym = KeyCodeToKeySym(event_code, event_mask);
						jkey = NativeToJKey(keysym);
						jmodifiers = NativeToJEventMask(event_mask);

						// Fire key released event.
						objKeyEvent = (*env)->NewObject(
												env,
												clsKeyEvent,
												idKeyEvent,
												org_jnativehook_keyboard_NativeKeyEvent_NATIVE_KEY_RELEASED,
												event_time,
												jmodifiers,
												event_code,
												jkey.keycode,
												org_jnativehook_keyboard_NativeKeyEvent_CHAR_UNDEFINED,
												jkey.location);
						(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objKeyEvent);
						break;

					case ButtonPress:
						#ifdef DEBUG
						fprintf(stdout, "LowLevelProc(): Button pressed. (%i)\n", event_code);
						#endif

						// Track the number of clicks.
						if ((long) (event_time - click_time) <= GetMultiClickTime()) {
							click_count++;
						}
						else {
							click_count = 1;
						}
						click_time = event_time;

						// Convert native modifiers to java modifiers.
						jmodifiers = NativeToJEventMask(event_mask);

						/* This information is all static for X11, its up to the WM to
						* decide how to interpret the wheel events.
						*/
						// TODO Should use constants and a lookup table for button codes.
						if (event_code > 0 && (event_code <= 3 || event_code == 8 || event_code == 9)) {
							jbutton = NativeToJButton(event_code);

							// Fire mouse released event.
							objMouseEvent = (*env)->NewObject(
														env,
														clsMouseEvent,
														idMouseButtonEvent,
														org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_PRESSED,
														event_time,
														jmodifiers,
														(jint) event_root_x,
														(jint) event_root_y,
														(jint) click_count,
														jbutton);
							(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objMouseEvent);
						}
						else if (event_code == WheelUp || event_code == WheelDown) {
							/* Scroll wheel release events.
							* Scroll type: WHEEL_UNIT_SCROLL
							* Scroll amount: 3 unit increments per notch
							* Units to scroll: 3 unit increments
							* Vertical unit increment: 15 pixels
							*/

							/* X11 does not have an API call for acquiring the mouse scroll type.  This
							* maybe part of the XInput2 (XI2) extention but I will wont know until it
							* is available on my platform.  For the time being we will just use the
							* unit scroll value.
							*/
							jscrollType = (jint) org_jnativehook_mouse_NativeMouseWheelEvent_WHEEL_UNIT_SCROLL;

							/* Some scroll wheel properties are available via the new XInput2 (XI2)
							* extention.  Unfortunately the extention is not available on my
							* development platform at this time.  For the time being we will just
							* use the Windows default value of 3.
							*/
							jscrollAmount = (jint) 3;

							if (event_code == WheelUp) {
								// Wheel Rotated Up and Away.
								jwheelRotation = -1;
							}
							else { // event_code == WheelDown
								// Wheel Rotated Down and Towards.
								jwheelRotation = 1;
							}

							// Fire mouse wheel event.
							objMouseWheelEvent = (*env)->NewObject(
															env,
															clsMouseWheelEvent,
															idMouseWheelEvent,
															org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_WHEEL,
															event_time,
															jmodifiers,
															(jint) event_root_x,
															(jint) event_root_y,
															(jint) click_count,
															jscrollType,
															jscrollAmount,
															jwheelRotation);
							(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objMouseWheelEvent);
						}
						break;

					case ButtonRelease:
						#ifdef DEBUG
						fprintf(stdout, "LowLevelProc(): Button released. (%i)\n", event_code);
						#endif

						// TODO Should use constants for button codes.
						if (event_code > 0 && (event_code <= 3 || event_code == 8 || event_code == 9)) {
							// Handle button release events.
							jbutton = NativeToJButton(event_code);
							jmodifiers = NativeToJEventMask(event_mask);

							// Fire mouse released event.
							objMouseEvent = (*env)->NewObject(
														env,
														clsMouseEvent,
														idMouseButtonEvent,
														org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_RELEASED,
														event_time,
														jmodifiers,
														(jint) event_root_x,
														(jint) event_root_y,
														(jint) click_count,
														jbutton);
							(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objMouseEvent);

							if (mouse_dragged != true) {
								// Fire mouse clicked event.
								objMouseEvent = (*env)->NewObject(
															env,
															clsMouseEvent,
															idMouseButtonEvent,
															org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_CLICKED,
															event_time,
															jmodifiers,
															(jint) event_root_x,
															(jint) event_root_y,
															(jint) click_count,
															jbutton);
								(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objMouseEvent);
							}
						}
						break;

					case MotionNotify:
						#ifdef DEBUG
						fprintf(stdout, "LowLevelProc(): Motion Notified. (%i, %i)\n", event_root_x, event_root_y);
						#endif

						// Reset the click count.
						if (click_count != 0 && (long) (event_time - click_time) > GetMultiClickTime()) {
							click_count = 0;
						}
						jmodifiers = NativeToJEventMask(event_mask);

						// Set the mouse dragged flag.
						mouse_dragged = jmodifiers >> 4 > 0;

						// Check the upper half of java modifiers for non zero value.
						if (jmodifiers >> 4 > 0) {
							// Create Mouse Dragged event.
							objMouseEvent = (*env)->NewObject(
														env,
														clsMouseEvent,
														idMouseMotionEvent,
														org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_DRAGGED,
														event_time,
														jmodifiers,
														(jint) event_root_x,
														(jint) event_root_y,
														(jint) click_count);
						}
						else {
							// Create a Mouse Moved event.
							objMouseEvent = (*env)->NewObject(
														env,
														clsMouseEvent,
														idMouseMotionEvent,
														org_jnativehook_mouse_NativeMouseEvent_NATIVE_MOUSE_MOVED,
														event_time,
														jmodifiers,
														(jint) event_root_x,
														(jint) event_root_y,
														(jint) click_count);
						}

						// Fire mouse moved event.
						(*env)->CallVoidMethod(env, objGlobalScreen, idDispatchEvent, objMouseEvent);
						break;

					#ifdef DEBUG
					default:
						fprintf(stderr, "LowLevelProc(): Unhandled Event Type: 0x%X\n", event_type);
						break;
					#endif
				}
			}
			else {
				// Unlock the mutex incase trylock succeeded.
				pthread_mutex_unlock(&hookRunningMutex);
			}

			// Handle any possible JNI issue that may have occurred.
			if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
				#ifdef DEBUG
				fprintf(stderr, "LowLevelProc(): JNI error occurred!\n");
				(*env)->ExceptionDescribe(env);
				#endif
				(*env)->ExceptionClear(env);
			}
		}
	}

	XRecordFreeData(hook);
}

static void *ThreadProc(void *arg) {
	pthread_mutex_lock(&hookRunningMutex);

	int *status = (int *) arg;
	*status = RETURN_FAILURE;
	JNIEnv *env = NULL;

	// XRecord context for use later.
	context = 0;

	// Grab the default display.
	char *disp_name = XDisplayName(NULL);
	disp_ctrl = XOpenDisplay(disp_name);
	Display *disp_data = XOpenDisplay(disp_name);
	if (disp_ctrl != NULL && disp_data != NULL) {
		#ifdef DEBUG
		fprintf(stdout, "ThreadProc(): XOpenDisplay successful.\n");
		#endif

		// Check to make sure XRecord is installed and enabled.
		int major, minor;
		if (XRecordQueryVersion(disp_data, &major, &minor) != false) {
			#ifdef DEBUG
			fprintf(stdout, "ThreadProc(): XRecord version: %d.%d.\n", major, minor);
			#endif

			// Setup XRecord range.
			XRecordClientSpec clients = XRecordAllClients;
			XRecordRange *range = XRecordAllocRange();
			if (range != NULL) {
				#ifdef DEBUG
				fprintf(stdout, "ThreadProc(): XRecordAllocRange successful.\n");
				#endif

				// Create XRecord Context.
				range->device_events.first = KeyPress;
				range->device_events.last = MotionNotify;
				context = XRecordCreateContext(disp_data, 0, &clients, 1, &range, 1);
				XFree(range);
			}
			else {
				#ifdef DEBUG
				fprintf(stderr, "ThreadProc(): XRecordAllocRange failure!\n");
				#endif

				thread_ex.class = NATIVE_HOOK_EXCEPTION;
				thread_ex.message = "Failed to allocate XRecord range";
			}
		}
		else {
			#ifdef DEBUG
			fprintf (stderr, "ThreadProc(): XRecord is not currently available!\n");
			#endif

			thread_ex.class = NATIVE_HOOK_EXCEPTION;
			thread_ex.message = "Failed to locate the X record extension";
		}
	}
	else {
		#ifdef DEBUG
		fprintf(stderr, "ThreadProc(): XOpenDisplay failure!\n");
		#endif

		thread_ex.class = NATIVE_HOOK_EXCEPTION;
		thread_ex.message = "Failed to open X display";
	}


	if (context != 0) {
		if ((*jvm)->AttachCurrentThread(jvm, (void **)(&env), NULL) == JNI_OK) {
			#ifdef DEBUG
			fprintf(stdout, "ThreadProc(): Attached to JVM successful.\n");
			#endif

			// Callback and start native event dispatch thread
			(*env)->CallVoidMethod(env, objGlobalScreen, idStartEventDispatcher);

			// Set the exit status.
			*status = RETURN_SUCCESS;

			#ifdef DEBUG
			fprintf(stdout, "ThreadProc(): XRecordCreateContext successful.\n");
			#endif

			pthread_mutex_unlock(&hookControlMutex);

			#ifdef XRECORD_ASYNC
			// Async requires that we loop so that our thread does not return.
			XRecordEnableContextAsync(disp_data, context, LowLevelProc, NULL);
			while (running) {
				XRecordProcessReplies(disp_data);
			}
			XRecordDisableContext(disp_ctrl, context);
			XSync(disp_ctrl, true);
			#else
			// We should be using this but its broken upstream.
			XRecordEnableContext(disp_data, context, LowLevelProc, NULL);
			#endif

			// Lock back up until we are done processing the exit.
			pthread_mutex_lock(&hookControlMutex);
			XRecordFreeContext(disp_ctrl, context);
		}
		else {
			#ifdef DEBUG
			fprintf(stderr, "ThreadProc(): AttachCurrentThread() failed!\n");
			#endif

			thread_ex.class = NATIVE_HOOK_EXCEPTION;
			thread_ex.message = "Failed to attach the native thread to the virtual machine";
		}
	}
	#ifdef DEBUG
	else {
		fprintf(stderr, "ThreadProc(): XRecordCreateContext failure!\n");
	}
	#endif

	// Close down any open displays.
	if (disp_ctrl != NULL) {
		XCloseDisplay(disp_ctrl);
		disp_ctrl = NULL;
	}

	if (disp_data != NULL) {
		XCloseDisplay(disp_data);
		disp_data = NULL;
	}

	if ((*jvm)->GetEnv(jvm, (void **)(&env), jni_version) == JNI_OK) {
		// Callback and stop native event dispatch thread
		(*env)->CallVoidMethod(env, objGlobalScreen, idStopEventDispatcher);

		// Detach this thread from the JVM.
		(*jvm)->DetachCurrentThread(jvm);

		#ifdef DEBUG
		fprintf(stdout, "ThreadProc(): Detach from JVM successful.\n");
		#endif
	}

	#ifdef DEBUG
	fprintf(stdout, "ThreadProc(): complete.\n");
	#endif

	// Make sure we signal that we have passed any exception throwing code.
	pthread_mutex_unlock(&hookRunningMutex);
	pthread_mutex_unlock(&hookControlMutex);

	pthread_exit(status);
}

int StartNativeThread() {
	int status = RETURN_FAILURE;

	// Make sure the native thread is not already running.
	if (IsNativeThreadRunning() != true) {
		// Lock the mutex handle for the thread hook.
		pthread_mutex_init(&hookControlMutex, NULL);

		// Create all the global references up front to save time in the callback.
		if (CreateJNIGlobals() == RETURN_SUCCESS) {
			#ifdef XRECORD_ASYNC
			// Allow the thread loop to block.
			running = true;
			#endif

			// We shall use the default pthread attributes: thread is joinable
			// (not detached) and has default (non real-time) scheduling policy
			pthread_mutex_lock(&hookControlMutex);

			// Initialize Native Input Functions.
			LoadInputHelper();

			if (pthread_create(&hookThreadId, NULL, ThreadProc, malloc(sizeof(int))) == 0) {
				#ifdef DEBUG
				fprintf(stdout, "StartNativeThread(): start successful.\n");
				#endif

				// Wait for the thread to start up.
				if (pthread_mutex_lock(&hookControlMutex) == 0) {
					pthread_mutex_unlock(&hookControlMutex);
				}

				// Handle any possible JNI issue that may have occurred.
				if (IsNativeThreadRunning()) {
					#ifdef DEBUG
					fprintf(stdout, "StartNativeThread(): initialization successful.\n");
					#endif

					status = RETURN_SUCCESS;
				}
				else {
					#ifdef DEBUG
					fprintf(stderr, "StartNativeThread(): initialization failure!\n");
					#endif

					// Wait for the thread to die.
					void *thread_status;
					pthread_join(hookThreadId, (void *) &thread_status);
					status = *(int *) thread_status;

					#ifdef DEBUG
					fprintf(stderr, "StartNativeThread(): Thread Result (%i)\n", status);
					#endif

					if (thread_ex.class != NULL && thread_ex.message != NULL)  {
						ThrowException(thread_ex.class, thread_ex.message);
					}
				}
			}
			else {
				#ifdef XRECORD_ASYNC
				running = false;
				#endif

				#ifdef DEBUG
				fprintf(stderr, "StartNativeThread(): start failure!\n");
				#endif

				ThrowException(NATIVE_HOOK_EXCEPTION, "Native thread start failure");
			}
		}
		#ifdef DEBUG
		else {
			// We cant do a whole lot of anything if we cant create JNI globals.
			// Any exceptions are thrown by CreateJNIGlobals().
			fprintf(stderr, "StartNativeThread(): CreateJNIGlobals() failed!\n");
		}
		#endif
	}

	return status;
}

int StopNativeThread() {
	int status = RETURN_FAILURE;

	if (IsNativeThreadRunning() == true) {
		// Lock the thread.
		pthread_mutex_lock(&hookControlMutex);

		#ifdef XRECORD_ASYNC
		// Try to exit the thread naturally.
		running = false;
		#else
		// Try to exit the thread naturally.
		XRecordDisableContext(disp_ctrl, context);
		XSync(disp_ctrl, true);
		#endif

		// Must unlock to allow the thread to finish cleaning up.
		pthread_mutex_unlock(&hookControlMutex);

		// Wait for the thread to die.
		void *thread_status;
		pthread_join(hookThreadId, &thread_status);
		status = *(int *) thread_status;

		#ifdef DEBUG
		fprintf(stdout, "StopNativeThread(): Thread Result (%i)\n", status);
		#endif

		// Cleanup Native Input Functions.
		UnloadInputHelper();

		// Destroy all created globals.
		#ifdef DEBUG
		if (DestroyJNIGlobals() == RETURN_FAILURE) {
			fprintf(stderr, "StopNativeThread(): DestroyJNIGlobals() failed!\n");
		}
		#else
		DestroyJNIGlobals();
		#endif

		// Clean up the mutex.
		pthread_mutex_destroy(&hookControlMutex);
	}

	return status;
}

bool IsNativeThreadRunning() {
	bool isRunning = false;

	// Wait for a lock on the thread.
	if (pthread_mutex_lock(&hookControlMutex) == 0) {
		// Lock Successful.

		if (pthread_mutex_trylock(&hookRunningMutex) == 0) {
			// Lock Successful, we are not running.
			pthread_mutex_unlock(&hookRunningMutex);
		}
		else {
			isRunning = true;
		}

		#ifdef DEBUG
		fprintf(stdout, "IsNativeThreadRunning(): State (%i)\n", isRunning);
		#endif

		pthread_mutex_unlock(&hookControlMutex);
	}
	#ifdef DEBUG
	else {
		/* Lock Failure. This should always be caused by an invalid pointe
		 * and/or an uninitialized mutex.  This message is normal when the
		 * native thread is not running.
		 */
		fprintf(stderr, "IsNativeThreadRunning(): Failed to acquire control mutex lock!\n");
	}
	#endif

	return isRunning;
}
