package app.manager.gui;

import app.game.GUI.GUIGame;
import app.manager.Window;
import app.lobbySelector.GUI.GUILobbySelector;
import app.lobby.GUI.GUILobby;
import app.login.GUILogin;

import javax.swing.*;

public class GUIManagerImpl extends JFrame implements GUIManager {
    private GUI currentGUI;

    public GUIManagerImpl(Window firstGUI) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setGUIFromEnum(firstGUI);
    }

    private void setGUIFromEnum(Window window) {
        switch (window){
            case LOGIN:
                this.currentGUI = new GUILogin();
                break;
            case BASE:
                this.currentGUI = new GUILobbySelector();
                break;
            case LOBBY:
            case MANAGER:
                this.currentGUI = new GUILobby();
                break;
            case GAME:
                this.currentGUI = new GUIGame();
                break;
            default:
                this.currentGUI = () -> "This should not happen";
                break;
        }
        setContentPane((JPanel) this.currentGUI);
        setTitle(this.currentGUI.getTitle());
        pack();
    }

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    @Override
    public void change(Window newGUI) {
        setGUIFromEnum(newGUI);
    }

    @Override
    public GUI getCurrentGUI() {
        return this.currentGUI;
    }
}
