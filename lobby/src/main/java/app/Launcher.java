package app;

import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.lobby.LobbyClient;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClient;
import app.lobby.ManagerClientImpl;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Launcher {

    private static BaseClient client;

    public static void main(String[] args) throws IOException {
        client = new BaseClientImpl(Inet4Address.getLocalHost().getHostAddress(), "Nicki");

    }

    public static void gameStarted() {
        ((LobbyClient) client).stop();
        // client = new gameClient();
    }

    public static void lobbyClosed() {
        ((LobbyClient) client).stop();
        client = new BaseClientImpl(client.getIp(), client.getNickname());
    }

    public static void lobbyJoinedSuccessfully(String managerClientIp) {
        client = new LobbyClientImpl(client.getIp(), client.getNickname(), 0, managerClientIp);
        ((LobbyClient) client).start();
    }

    public static void lobbyCreatedSuccessfully() throws UnknownHostException {
        client = new ManagerClientImpl(client.getIp(), client.getNickname(), 0, 5);
        ((ManagerClient) client).start();
    }


}