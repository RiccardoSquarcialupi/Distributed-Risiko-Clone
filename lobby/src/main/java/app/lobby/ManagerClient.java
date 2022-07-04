package app.lobby;

import app.base.BaseClient;
import app.base.JSONClient;

import java.util.List;

public interface ManagerClient extends LobbyClient {
    void startGame();

    List<JSONClient> getClientList();

    void managerClientChange(String newManagerIP);
}
