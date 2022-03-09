import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        BaseClient client = new BaseClient(0, "127.0.0.1", "Nicki");

        // Quando mi devo trasformare in LobbyClient:
        client = new LobbyClient(client.getId(), client.getIp(), client.getNickname(), 0, 0);

        // Se mi trasformo in ManagerClient:
        client = new ManagerClient(client.getId(), client.getIp(), client.getNickname(), 0, 5);

        // Lo stesso se da Manager divento lobbyClient o il contrario, ricordando  però di stoppare i servizi.
        ((LobbyClient)client).stop();
    }
}