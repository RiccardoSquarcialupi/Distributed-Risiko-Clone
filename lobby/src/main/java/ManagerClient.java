import java.util.ArrayList;
import java.util.List;

public class ManagerClient extends LobbyClient {
    private int idLobbyCreated;
    private int maxPlayer;

    public ManagerClient(int id, String ip, String nickname) {
        super(id, ip, nickname);
    }

    private void addPlayer(BaseClient client){
        super.clientList.add(client);
    }

    private void removePlayer(BaseClient client){
        super.clientList.remove(client);
    }

    private void startGame(){}
}
