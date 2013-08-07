package cookiejar.gui;

import java.util.HashSet;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


//@author A0088447N

/**
 * This class is used for global key hook. It provides operations to show the
 * GUI when key combo is performed.<br>
 * jnativehook library is required.
 * 
 * @author Tien
 * 
 */
public class WinKeyHook implements NativeKeyListener {
	private HashSet<Integer> currentKeyDown = new HashSet<Integer>();
	private GUIManager applicationGUI;
	private final int KEY_CODE_CTRL = 17;
	private final int KEY_CODE_BACK_SLASH = 92;

	public WinKeyHook(GUIManager GUI) {
		initGlobalScreen();
		applicationGUI = GUI;
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		currentKeyDown.add(e.getKeyCode());

		// Check if key combo is performed
		if (currentKeyDown.size() == 2) {
			if (currentKeyDown.contains(KEY_CODE_CTRL)
					&& currentKeyDown.contains(KEY_CODE_BACK_SLASH)) {
				applicationGUI.setVisible(true);
				disableGlobalKeyHook();
			}
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		currentKeyDown.remove(e.getKeyCode());
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public void registerGlobalKeyHook() {
		GlobalScreen.getInstance().addNativeKeyListener(this);
	}

	public void disableGlobalKeyHook() {
		GlobalScreen.getInstance().removeNativeKeyListener(this);
		currentKeyDown.clear();
	}

	public void initGlobalScreen() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
}
