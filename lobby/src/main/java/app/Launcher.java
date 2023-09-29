package app;

import app.common.Client;
import app.manager.Window;
import app.manager.contextManager.ContextManager;
import app.manager.contextManager.ContextManagerImpl;
import app.manager.gui.GUI;
import app.manager.gui.GUIManager;
import app.manager.gui.GUIManagerImpl;
import io.vertx.core.Vertx;

import java.io.IOException;

public class Launcher {

    public static final String serverIP = "127.0.0.1";
    private static ContextManager contextManager;
    private static GUIManager guiManager;
    private static Vertx vertx = null;

    public static void main(String[] args) throws IOException {
        contextManager = new ContextManagerImpl(Window.LOGIN);
        guiManager = new GUIManagerImpl(Window.LOGIN);
        guiManager.open();
    }

    public static Vertx getVertx() {
        if(vertx == null){
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public static void userLogged() {
        contextManager.change(Window.BASE);
        guiManager.change(Window.BASE);
    }

    public static void gameStarted() {
        contextManager.change(Window.GAME);
        guiManager.change(Window.GAME);
    }

    public static void lobbyClosed() {
        contextManager.change(Window.BASE);
        guiManager.change(Window.BASE);
    }

    public static void lobbyJoinedSuccessfully() {
        contextManager.change(Window.LOBBY);
        guiManager.change(Window.LOBBY);
    }

    public static void lobbyCreatedSuccessfully() {
        contextManager.change(Window.MANAGER);
        guiManager.change(Window.MANAGER);
    }

    public static Client getCurrentClient() {
        return contextManager.getCurrentClient();
    }

    public static GUI getCurrentGui() {
        return guiManager.getCurrentGUI();
    }
}