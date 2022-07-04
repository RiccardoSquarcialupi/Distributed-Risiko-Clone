package app;

import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.base.GUIBase;
import app.gui.GUIManager;
import app.gui.GUIManagerImpl;
import app.gui.GUIWindow;
import app.lobby.*;
import app.login.GUILogin;

import java.io.IOException;
import java.net.Inet4Address;

public class Launcher {

    private static BaseClient client;
    private static GUIManager guiManager;

    private static String ip;

    public static void main(String[] args) throws IOException {
        ip = Inet4Address.getLocalHost().getHostAddress();
        guiManager = new GUIManagerImpl(GUIWindow.LOGIN);
        guiManager.open();
    }

    public static void userLoginned(String nickname) {
        client = new BaseClientImpl(ip, nickname);
        guiManager.change(GUIWindow.BASE);
    }

    public static void gameStarted() {
        ((LobbyClient) client).stop();
        // client = new gameClient();
        // guiManager.change(GUIWindow.GAME);
    }

    public static void lobbyClosed() {
        ((LobbyClient) client).stop();
        client = new BaseClientImpl(client.getIp(), client.getNickname());
        guiManager.change(GUIWindow.BASE);
    }

    public static void lobbyJoinedSuccessfully(String managerClientIp) {
        client = new LobbyClientImpl(client.getIp(), client.getNickname(), 0, managerClientIp);
        ((LobbyClient) client).start();
        guiManager.change(GUIWindow.LOBBY);
    }

    public static void lobbyCreatedSuccessfully() {
        client = new ManagerClientImpl(client.getIp(), client.getNickname(), 0, 5);
        ((ManagerClient) client).start();
        guiManager.change(GUIWindow.MANAGER);
    }


}