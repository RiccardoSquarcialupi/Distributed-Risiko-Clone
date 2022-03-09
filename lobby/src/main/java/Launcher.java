import java.io.IOException;

public class Launcher {

    public static BaseClient client;

    public static void main(String[] args) throws IOException {
        BaseClient client = new BaseClient(0, "127.0.0.1", "Nicki");

        // Quando mi devo trasformare in LobbyClient:
        client = new LobbyClient(client.getId(), client.getIp(), client.getNickname(), 0, 0);
        ((LobbyClient)client).start();

        // Se mi trasformo in ManagerClient:
        client = new ManagerClient(client.getId(), client.getIp(), client.getNickname(), 0, 5);
        ((ManagerClient)client).start();
    }

    public static void gameStarted() {
        ((LobbyClient)client).stop();
        // client = new gameClient();
    }

    public static void lobbyClosed() {
        ((LobbyClient)client).stop();
        client = new BaseClient(client.getId(), client.getIp(), client.getNickname());
    }
}