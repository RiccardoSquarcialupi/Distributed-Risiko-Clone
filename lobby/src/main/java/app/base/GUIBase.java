package app.base;

import app.gui.GUI;

import javax.swing.*;

public class GUIBase extends JPanel implements GUI, GUIBaseActions{
    @Override
    public String getTitle() {
        return "Lobby selector";
    }
}
