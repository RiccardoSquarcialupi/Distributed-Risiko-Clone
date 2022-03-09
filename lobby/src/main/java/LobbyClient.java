import java.util.ArrayList;
import java.util.List;

public class LobbyClient extends BaseClient {

    protected List<BaseClient> clientList;
    private int idLobby;
    private int idManagerClient;

    protected final ClientServerPart clientServerPart;

    public LobbyClient(int id, String ip, String nickname, int idLobby, int idManagerClient) {
        super(id, ip, nickname);
        this.idLobby = idLobby;
        this.idManagerClient = idManagerClient;

        this.clientList = new ArrayList<>();

        this.clientServerPart = new ClientServerPart(this);
        this.clientServerPart.start();
    }

    public void stop(){
        this.clientServerPart.stop();
    }

    // Qui ci vanno tutti i metodi che richiamerà ClientServerPart per aggiornare lo stato del client.
}
