package delegates;

/**
 * This interface uses the delegation design pattern where instead of having
 * the LoginWindow class try to do everything, it will only focus on
 * handling the UI. The actual logic/operation will be delegated to the controller
 * class (in this case Bank).
 *
 * LoginWindow calls the methods that we have listed below but
 * Bank is the actual class that will implement the methods.
 *
 * The structure and some implementations in this class are based on or inspired by the CPSC 304 Java Bank Project.
 */
public interface LoginWindowDelegate {
    void login(String username, String password);
}