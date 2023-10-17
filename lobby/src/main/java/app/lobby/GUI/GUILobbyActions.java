package app.lobby.GUI;

import app.lobbySelector.JSONClient;

import java.util.List;

public interface GUILobbyActions {
    void disableStartButton();
    void enableStartButton();
    void updateClientList(List<JSONClient> clients);
}
