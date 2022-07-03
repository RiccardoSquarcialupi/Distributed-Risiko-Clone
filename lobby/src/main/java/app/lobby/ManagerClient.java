package app.lobby;

import app.base.BaseClient;

import java.util.List;

public interface ManagerClient extends LobbyClient {
    void startGame();

    List<BaseClient> getClientList();

    void managerClientChange();
}
