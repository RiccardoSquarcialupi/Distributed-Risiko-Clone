package app.common;

public interface Client {
    /**
     * Returns the user IP as a String.
     * @return user IP.
     */
    String getIP();

    /**
     * Returns the nickname of the user.
     * @return user nickname.
     */
    String getNickname() throws IllegalAccessException;
}
