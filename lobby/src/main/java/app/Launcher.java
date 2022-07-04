package app;

import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.manager.ClientWindow;
import app.manager.client.Client;
import app.manager.client.ClientManager;
import app.manager.client.ClientManagerImpl;
import app.manager.gui.GUI;
import app.manager.gui.GUIManager;
import app.manager.gui.GUIManagerImpl;
import app.lobby.*;

import java.io.IOException;
import java.net.Inet4Address;

public class Launcher {

    private static ClientManager clientManager;
    private static GUIManager guiManager;

    public static void main(String[] args) throws IOException {
        clientManager = new ClientManagerImpl(ClientWindow.LOGIN);
        guiManager = new GUIManagerImpl(ClientWindow.LOGIN);
        guiManager.open();
    }

    public static void userLoginned() {
        clientManager.change(ClientWindow.BASE);
        guiManager.change(ClientWindow.BASE);
    }

    public static void gameStarted() {
        // clientManager.change(ClientWindow.GAME);
        // guiManager.change(GUIWindow.GAME);
    }

    public static void lobbyClosed() {
        clientManager.change(ClientWindow.BASE);
        guiManager.change(ClientWindow.BASE);
    }

    public static void lobbyJoinedSuccessfully() {
        clientManager.change(ClientWindow.LOBBY);
        guiManager.change(ClientWindow.LOBBY);
    }

    public static void lobbyCreatedSuccessfully() {
        clientManager.change(ClientWindow.MANAGER);
        guiManager.change(ClientWindow.MANAGER);
    }

    public static Client getCurrentClient() {
        return clientManager.getCurrentClient();
    }

    public static GUI getCurrentGui() {
        return guiManager.getCurrentGUI();
    }
}