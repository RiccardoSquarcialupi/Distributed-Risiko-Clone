package app.lobby;

import app.common.Client;
import app.lobbySelector.JSONClient;

import java.util.List;

public interface LobbyClient extends Client {
    String getIpManager();

    void start();

    void stop();

    void exitLobby();

    List<JSONClient> getClientList();

    @Override
    String getNickname();
}
