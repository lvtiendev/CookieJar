//@author A0059827N
package cookiejar.gui;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Timer;

/**
 * Adopted from http://tips4java.wordpress.com/2008/10/24/application-inactivity/
 * <p>
 * A class that monitors inactivity in an application.
 * <p>
 * It does this by using a Swing Timer and by listening for specified
 * AWT events. When an event is received the Timer is restarted.
 * If no event is received during the specified time interval then the
 * timer will fire and invoke the specified Action.
 * <p>
 * When creating the listener the inactivity interval is specified in
 * minutes. However, once the listener has been created you can reset
 * this value in milliseconds if you need to.
 * <p>
 * Some common event masks have be defined with the class:
 * <ul>
 * <li>KEY_EVENTS</li>
 * <li>MOUSE_EVENTS - which includes mouse motion events</li>
 * <li>USER_EVENTS - includes KEY_EVENTS and MOUSE_EVENT (this is the default)</li>
 * </ul>
 * <p>
 * The inactivity interval and event mask can be changed at any time,
 * however, they will not become effective until you stop and start
 * the listener.
 */
public class InactivityListener implements ActionListener, AWTEventListener {
	public final static long KEY_EVENTS = AWTEvent.KEY_EVENT_MASK;

	public final static long MOUSE_EVENTS = AWTEvent.MOUSE_MOTION_EVENT_MASK
			+ AWTEvent.MOUSE_EVENT_MASK;

	public final static long USER_EVENTS = KEY_EVENTS + MOUSE_EVENTS;

	private Action action;
	private int interval;
	private long eventMask;
	private Timer timer = new Timer(0, this);

	/**
	 * Constructs an inactivity listener with a default inactivity interval of 1
	 * minute and listen for USER_EVENTS.
	 * 
	 * @param action
	 *            the action to be invoked after the specified inactivity period
	 */
	public InactivityListener(Action action) {
		this(action, 1);
	}

	/**
	 * Constructs an inactivity listener with a specified inactivity interval
	 * (in minutes) and listen for USER_EVENTS.
	 * 
	 * @param action
	 *            the action to be invoked after the specified inactivity period
	 */
	public InactivityListener(Action action, int interval) {
		this(action, interval, USER_EVENTS);
	}

	/**
	 * Constructs an inactivity listener with a specified the inactivity
	 * interval and the events to listen for.
	 * 
	 * @param action
	 *            the action to be invoked after the specified inactivity period
	 */
	public InactivityListener(Action action, int minutes, long eventMask) {
		setAction(action);
		setInterval(minutes);
		setEventMask(eventMask);
	}

	/**
	 * Sets the action for the inactivity listener.
	 * 
	 * @param action
	 *            the action to be invoked after the specified inactivity period
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * Sets the interval in minutes before the action is invoked specified.
	 * 
	 * @param minutes
	 *            the interval in minutes before the action is invoked specified
	 */
	public void setInterval(int minutes) {
		setIntervalInMillis(minutes * 60000);
	}

	/**
	 * Sets the interval in milliseconds before the action is invoked specified.
	 * 
	 * @param minutes
	 *            the interval in milliseconds before the action is invoked
	 *            specified
	 */
	public void setIntervalInMillis(int interval) {
		this.interval = interval;
		timer.setInitialDelay(interval);
	}

	/**
	 * Sets the event mask of events that the inactivity listener is listening
	 * to.
	 * 
	 * @param eventMask
	 *            a mask specifying the events to be listened
	 */
	public void setEventMask(long eventMask) {
		this.eventMask = eventMask;
	}

	/**
	 * Starts listening for events.
	 */
	public void start() {
		timer.setInitialDelay(interval);
		timer.setRepeats(false);
		timer.start();
		Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
	}

	/**
	 * Stops listening for events.
	 */
	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		timer.stop();
	}
	
	/**
	 * Invoked when action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		action.actionPerformed(e);
	}
	
	/**
	 * Invoked when an event is dispatched in the AWT.
	 */
	@Override
	public void eventDispatched(AWTEvent e) {
		if (timer.isRunning())
			timer.restart();
	}
}