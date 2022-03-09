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
    }

    public  void start(){this.clientServerPart.start();}
    public void stop(){
        this.clientServerPart.stop();
    }

    public void addNewClient(BaseClient newClient) {
        this.clientList.add(newClient);
    }

    public void deleteClient(BaseClient toDeleteClient) {
        this.clientList.remove(toDeleteClient);
    }

    public void updateManager(int newManagerId) {
        this.idManagerClient = newManagerId;
    }

    public void gameStarted() {
        Launcher.gameStarted();
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }
}
