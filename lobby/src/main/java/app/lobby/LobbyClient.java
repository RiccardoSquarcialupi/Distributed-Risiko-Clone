package app.lobby;

import app.common.Client;

public interface LobbyClient extends Client {
    String getIpManager();

    void start();

    void stop();

    void exitLobby();

    @Override
    String getNickname();
}
