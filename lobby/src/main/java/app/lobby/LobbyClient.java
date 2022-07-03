package app.lobby;

import app.base.BaseClient;

public interface LobbyClient extends BaseClient {
    String getIpManagerClient();

    void start();

    void stop();

    void exitLobby();
}
