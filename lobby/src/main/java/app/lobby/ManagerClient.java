package app.lobby;

import app.lobbySelector.JSONClient;
import io.vertx.core.Future;

import java.util.List;

public interface ManagerClient extends LobbyClient {
    void startGame();

    Future<Void> managerClientChange(String newManagerIP);

    Future<Void> closeLobby();
}
