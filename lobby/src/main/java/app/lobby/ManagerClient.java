package app.lobby;

import io.vertx.core.Future;

public interface ManagerClient extends LobbyClient {
    Future<Void> startGame();

    Future<Void> managerClientChange(String newManagerIP);

    Future<Void> closeLobby();
}
