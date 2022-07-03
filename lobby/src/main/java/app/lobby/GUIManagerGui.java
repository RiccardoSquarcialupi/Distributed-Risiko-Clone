package app.lobby;

import app.gui.GUI;

import javax.swing.*;

public class GUIManagerGui extends JPanel implements GUI, GUIManagerActions {

    @Override
    public String getTitle() {
        return "Lobby manager";
    }
}
