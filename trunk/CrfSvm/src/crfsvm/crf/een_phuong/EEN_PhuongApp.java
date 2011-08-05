/*
 * EEN_PhuongApp.java
 */
//test-5.txt test-5-0.txt C:\EEN_Phuong_interfaceF\model (arguments)
package crfsvm.crf.een_phuong;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class EEN_PhuongApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new EEN_PhuongView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of EEN_PhuongApp
     */
    public static EEN_PhuongApp getApplication() {
        return Application.getInstance(EEN_PhuongApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(EEN_PhuongApp.class, args);
    }
}
