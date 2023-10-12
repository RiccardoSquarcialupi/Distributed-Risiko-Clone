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
import java.net.*;

public class Launcher {

    public static String serverIP = "";
    public static final int serverPort = 5000;
    private static ContextManager contextManager;
    private static GUIManager guiManager;
    private static Vertx vertx = null;

    public static void main(String[] args) throws IOException {
        searchForServer();
        contextManager = new ContextManagerImpl(Window.LOGIN);
        guiManager = new GUIManagerImpl(Window.LOGIN);
        guiManager.open();
    }

    private static void searchForServer() throws IOException {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Supposing I'm in a type C network ('/24' type networks...):");
        String ip = Inet4Address.getLocalHost().getHostAddress();
        System.out.println("My ip is " + ip);
        //get subnet from my ip
        String subnet = ip.substring(0, ip.lastIndexOf("."));
        System.out.println("My subnet is " + subnet.concat(".0"));
        System.out.println("Searching for server...");
        for (int i=1;i<255;i++){
            String host=subnet + "." + i;
            if (InetAddress.getByName(host).isReachable(500)){
                try {
                    Socket socket = new Socket(host, serverPort);
                    System.out.println("Server found at " + host);
                    socket.close();
                    serverIP=host;
                } catch (IOException e) {
                    System.out.println("Server not found at " + host);
                    continue;
                }
                break;
            }
        }


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

    public static void becomeManager() {
        System.out.println(getCurrentClient().getIP()+" says: I'm the new manager");
        contextManager.change(Window.MANAGER);
        guiManager.change(Window.MANAGER);
    }

    public static GUI getCurrentGui() {
        return guiManager.getCurrentGUI();
    }


}