//@author A0059827N
package cookiejar.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 * This class extends the JWindow and supports fading in, fading out and
 * fading in and out animations.
 */
@SuppressWarnings("serial")
public class FadableWindow extends JWindow {
	protected final float zeroOpacity = 0.0f;
	protected final float fullOpacity = 1.0f;
	
	private final Timer fadeInTimer = new Timer(0, null);
	private final Timer fadeOutTimer = new Timer(0, null);
	
	/**
	 * Creates a fadable window with no specified owner.
	 * 
	 * This window is not focusable.
	 */
	public FadableWindow() {
		super();
	}
	
	/**
	 * Creates a fadable window with the specified owner frame.
	 * 
	 * If owner is null, the shared owner will be used and this window will not
	 * be focusable. Also, this window will not be focusable unless its owner is
	 * showing on the screen.
	 * 
	 * @param owner
	 *            the frame from which the window is displayed
	 */
	public FadableWindow(JFrame owner) {
		super(owner);
	}
	
	/**
	 * Fades in the Window.
	 * 
	 * The Window's opacity is increased by <code>opacityStep</code> after every
	 * <code>intervalStep</code>.
	 * 
	 * @param intervalStep
	 *            the interval length
	 * @param opacityStep
	 *            the amount of opacity increased after an interval
	 */
	public void fadeIn(int intervalStep, float opacityStep) {
		stopAllTimers();
		fadeIn(0, intervalStep, opacityStep);
	}
	
	/**
	 * Fades out the Window.
	 * 
	 * Fading out only happens if the window is already feasible. The Window's
	 * opacity is decreased by <code>opacityStep</code> after every
	 * <code>intervalStep</code>.
	 * 
	 * @param intervalStep
	 *            the interval length
	 * @param opacityStep
	 *            the amount of opacity decreased after an interval
	 */
	public void fadeOut(int intervalStep, float opacityStep) {
		stopAllTimers();
		fadeOut(0, intervalStep, opacityStep);
	}
	
	/**
	 * Fades in and fades out the Window.
	 * 
	 * The Window fades in immediately and fades out after <code>duration</code>
	 * milliseconds.
	 * 
	 * @param duration
	 *            the duration this Windows is shown (including the fading-in
	 *            time) before fading out
	 * @param fadeInIntervalStep
	 *            the interval length of the fade in process
	 * @param fadeInOpacityStep
	 *            the amount of opacity increased after an interval during the
	 *            fade in process
	 * @param fadeOutIntervalStep
	 *            the interval length of the fade out process
	 * @param fadeOutOpacityStep
	 *            the amount of opacity decreased after an interval during the
	 *            fade out process
	 */
	public void fadeInAndOut(int duration,
			 int fadeInIntervalStep, float fadeInOpacityStep,
			 int fadeOutIntervalStep, float fadeOutOpacityStep) {
		stopAllTimers();
		fadeIn(0, fadeInIntervalStep, fadeInOpacityStep);
		fadeOut(duration, fadeOutIntervalStep, fadeOutOpacityStep);
	}
	
	/**
	 * Shows or hides this Window depending on the value of parameter <code>b</code>.
	 * 
	 * The opacity is set to <code>1.0f</code> when the Window is shown 
	 * and <code>0.0f</code> when it is hid.
	 * 
	 * @param b
	 *            if <code>true</code>, shows this component; otherwise, hides this component
	 */
	@Override
	public void setVisible(boolean b) {
		if (b == true) {
			setOpacity(fullOpacity);
		} else {
			setOpacity(zeroOpacity);
		}
		super.setVisible(b);
		stopAllTimers();
	}
	
	private void stopAllTimers() {
		fadeInTimer.stop();
		fadeOutTimer.stop();
	}
	
	private void fadeIn(int initialWait, int intervalStep, final float opacityStep) {
		final float initialOpacity = getOpacity();
		
		setVisible(true);
		setOpacity(initialOpacity);
		fadeInTimer.setInitialDelay(initialWait);
		fadeInTimer.setDelay(intervalStep);
		fadeInTimer.addActionListener(new ActionListener() {
			float curOpacity = initialOpacity;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				curOpacity += opacityStep;
				if (curOpacity > fullOpacity) {
					curOpacity = fullOpacity;
					fadeInTimer.stop();
					fadeInTimer.removeActionListener(this);
				}
				
				setOpacity(curOpacity);
			}
		});
		
		fadeInTimer.restart();
	}
	
	private void fadeOut(int initialWait, int intervalStep, final float opacityStep) {
		if (isVisible()) {
			final float initialOpacity = getOpacity(); 
			
			setOpacity(initialOpacity);
			fadeOutTimer.setInitialDelay(initialWait);
			fadeOutTimer.setDelay(intervalStep);
			fadeOutTimer.addActionListener(new ActionListener() {
				float curOpacity = initialOpacity;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					curOpacity -= opacityStep;
					if (curOpacity < zeroOpacity) {
						curOpacity = zeroOpacity;
						setVisible(false);
						fadeOutTimer.stop();
						fadeOutTimer.removeActionListener(this);
					}
					
					setOpacity(curOpacity);
				}
			});
			
			fadeOutTimer.restart();
		}
	}
}