package app.manager.gui;

import app.manager.ClientWindow;
import app.base.GUIBase;
import app.lobby.GUILobby;
import app.lobby.GUIManagerGui;
import app.login.GUILogin;

import javax.swing.*;

public class GUIManagerImpl extends JFrame implements GUIManager {
    private GUI currentGUI;

    public GUIManagerImpl(ClientWindow firstGUI) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setGUIFromEnum(firstGUI);
    }

    private void setGUIFromEnum(ClientWindow window) {
        switch (window){
            case LOGIN:
                this.currentGUI = new GUILogin();
                break;
            case BASE:
                this.currentGUI = new GUIBase();
                break;
            case LOBBY:
                this.currentGUI = new GUILobby();
                break;
            case MANAGER:
                this.currentGUI = new GUIManagerGui();
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
    public void change(ClientWindow newGUI) {
        setGUIFromEnum(newGUI);
    }

    @Override
    public GUI getCurrentGUI() {
        return this.currentGUI;
    }
}
