import java.util.ArrayList;
import java.util.List;

public class LobbyClient extends BaseClient {

    protected List<BaseClient> clientList;
    private int idLobby;
    private int idManagerClient;

    public LobbyClient(int id, String ip, String nickname) {
        super(id, ip, nickname);
        clientList = new ArrayList<>();
    }

    public void setIdLobby(int idLobby) {
        this.idLobby=idLobby;
    }

    public void setIdManagerClient(int idManagerClient) {
        this.idManagerClient=idManagerClient;
    }
}
