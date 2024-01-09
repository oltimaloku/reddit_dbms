package controller;

import delegates.LoginWindowDelegate;
import database.DatabaseConnectionHandler;
import ui.LoginWindow;
import ui.UI;
import javax.xml.crypto.Data;
/**
 * The structure and some implementations in this class are based on or inspired by the CPSC 304 Java Bank Project.
 */
public class SocialMedia implements LoginWindowDelegate {
    private DatabaseConnectionHandler dbHandler = null;
    private LoginWindow loginWindow = null;
    private UI gui = null;

    public SocialMedia() {
        dbHandler = new DatabaseConnectionHandler();
    }

    public void start() {
        loginWindow = new LoginWindow();
        loginWindow.showFrame(this);
    }

    public void login(String username, String password) {
       boolean didConnect = dbHandler.login(username, password);

        // Opens GUI
        if (didConnect) {

            System.out.println("Connected!");
            gui = new UI(
                    dbHandler
            );
            if (loginWindow.shouldReset()) {
                gui.resetDb();
            }
            loginWindow.dispose();
            gui.createAndShowGUI();

        } else {
            loginWindow.handleLoginFailed();

            if (loginWindow.hasReachedMaxLoginAttempts()) {
                loginWindow.dispose();
                System.out.println("You have exceeded your number of allowed attempts");
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.start();

    }
}
