package cookiejar;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import cookiejar.gui.GUIManager;

//@author A0088447N

/**
 * This is the main class of CookieJar.<br> 
 * It is responsible for invoking GUI and logging.
 * @author Tien
 *
 */
public class Launcher {
		private GUIManager applicationGUI;
		private static Logger log = Logger.getLogger(Launcher.class.getName());
        
		/**
		 * Start the GUI and set it Visible
		 */
        private void startGUI(){
           SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
    				try {
    					setGUIVisible();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		});              
        }
        
        public static void log(String stringMessage) {
        	log.info(stringMessage);
        }
        
        /**
         * Prepare logger, add "cookiejar.xml" to logger
         */
        private void startLogger(){
        	try{
        		FileHandler hand = new FileHandler("cookiejar.xml", true);
        		log.addHandler(hand);
        	} catch (IOException e){
        	}
        }
        
        public void setGUIVisible() {
        	applicationGUI.setVisible(true);
		}
        
        /**
         * Run the program
         */
        public void run(){
        	try{
        		startLogger();
        		
        		applicationGUI = new GUIManager(this);
        		startGUI();
        		
        		log("Program starts");
        	} catch (Exception e){
        		GUIManager.showErrorMessageDialogForApplicationTermination(e.getMessage());
        		terminate();
        	}
        }
        
        /**
         * Terminate the program
         */
        public void terminate(){
        	log("Program terminates");
        	System.exit(0);
        }

        public static void main(String[] args) {
        	Launcher cookiejar = new Launcher();
        	cookiejar.run();
        }
}