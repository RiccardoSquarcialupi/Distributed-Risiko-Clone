package app.lobby.GUI;

import app.manager.gui.GUI;

import javax.swing.*;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    @Override
    public String getTitle() {
        return "Lobby";
    }
}
