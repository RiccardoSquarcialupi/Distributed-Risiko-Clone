package app.lobby;

import app.manager.gui.GUI;

import javax.swing.*;

public class GUIManagerGui extends JPanel implements GUI, GUIManagerActions {

    @Override
    public String getTitle() {
        return "Lobby manager";
    }
}
