package app.lobby;

import app.common.Client;
import app.lobbySelector.JSONClient;
import io.vertx.core.Future;

import java.util.List;

public interface LobbyClient extends Client {
    String getIpManager();

    void start();

    void stop();

    Future<Void> exitLobby();

    List<JSONClient> getClientList();

    int getLobbyMaxPlayers();

    @Override
    String getNickname();
}
