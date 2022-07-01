package app;

import app.base.BaseClient;
import app.lobby.LobbyClient;
import app.lobby.ManagerClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Launcher {

    private static BaseClient client;

    public static void main(String[] args) throws IOException {
        client = new BaseClient(Inet4Address.getLocalHost().getHostAddress(), "Nicki");

    }

    public static void gameStarted() {
        ((LobbyClient) client).stop();
        // client = new gameClient();
    }

    public static void lobbyClosed() {
        ((LobbyClient) client).stop();
        client = new BaseClient(client.getIp(), client.getNickname());
    }

    public static void lobbyJoinedSuccessfully(String managerClientIp) {
        client = new LobbyClient(client.getIp(), client.getNickname(), 0, managerClientIp);
        ((LobbyClient) client).start();
    }

    public static void lobbyCreatedSuccessfully() throws UnknownHostException {
        client = new ManagerClient(client.getIp(), client.getNickname(), 0, 5);
        ((ManagerClient) client).start();
    }


}