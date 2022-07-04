package app.lobby;

import app.base.BaseClient;

public interface LobbyClient extends BaseClient {
    String getIpManager();

    void start();

    void stop();

    void exitLobby();
}
