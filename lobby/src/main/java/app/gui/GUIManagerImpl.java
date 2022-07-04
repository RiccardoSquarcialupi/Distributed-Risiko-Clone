package app.gui;

import app.base.GUIBase;
import app.lobby.GUILobby;
import app.lobby.GUIManagerGui;
import app.login.GUILogin;

import javax.swing.*;

public class GUIManagerImpl extends JFrame implements GUIManager {
    private GUI currentGUI;

    public GUIManagerImpl(GUIWindow firstGUI) {
        setGUIFromEnum(firstGUI);
        setContentPane((JPanel)this.currentGUI);
        pack();
    }

    private void setGUIFromEnum(GUIWindow window) {
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
    }

    @Override
    public void open() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    @Override
    public void change(GUIWindow newGUI) {
        setGUIFromEnum(newGUI);
        setContentPane((JPanel) this.currentGUI);
        setTitle(this.currentGUI.getTitle());
        pack();
    }

    @Override
    public GUI getCurrentGUI() {
        return this.currentGUI;
    }
}
