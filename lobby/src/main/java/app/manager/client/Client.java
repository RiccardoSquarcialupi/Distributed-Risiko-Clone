package app.manager.client;

public interface Client {
    /**
     * Returns the user IP as a String.
     * @return user IP.
     */
    String getIP();

    /**
     * Provide the username to enter the game.
     * @param nickname username of the user.
     */
    void login(String nickname);
}
