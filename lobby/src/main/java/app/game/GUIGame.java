package app.game;

import app.base.GUIBaseActions;
import app.manager.gui.GUI;

import javax.swing.*;

public class GUIGame extends JPanel implements GUI, GUIGameActions {
    @Override
    public String getTitle() {
        return "Risiko! game";
    }
}
