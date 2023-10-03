package app.lobby;

import app.lobbySelector.JSONClient;

import java.util.List;

public interface ManagerClient extends LobbyClient {
    void startGame();

    List<JSONClient> getClientList();

    void managerClientChange(String newManagerIP);

    void closeLobby();
}
