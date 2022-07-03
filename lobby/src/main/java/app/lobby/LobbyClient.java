package app.lobby;

import app.base.BaseClient;

public interface LobbyClient extends BaseClient {
    String getIpManagerClient();

    void start();

    void stop();

    void addNewClient(BaseClient newClient);

    void deleteClient(BaseClient toDeleteClient);

    void updateManager(String newManagerIp);

    void gameStarted();

    void lobbyClosed();

    void exitLobby();
}
