package app.lobby;

import app.Launcher;
import app.base.BaseClient;
import app.lobby.ClientServerPart;
import app.lobby.ClientWebClientPart;

import java.util.ArrayList;
import java.util.List;

public class LobbyClient extends BaseClient {

    protected List<BaseClient> clientList;
    private int idLobby;
    private String ipManagerClient;

    protected ClientServerPart clientServerPart;
    protected ClientWebClientPart clientWebClientPart;

    public LobbyClient(String ip, String nickname, int idLobby, String ipManagerClient) {
        super(ip, nickname);
        this.idLobby = idLobby;
        this.ipManagerClient = ipManagerClient;
        this.clientList = new ArrayList<>();
        this.clientWebClientPart = new ClientWebClientPart();
        this.clientServerPart = new ClientServerPart(this);
    }

    public String getIpManagerClient(){
        return this.ipManagerClient;
    }

    public void start() {
        this.clientServerPart.start();
    }

    public void stop() {
        this.clientServerPart.stop();
    }

    public void addNewClient(BaseClient newClient) {
        this.clientList.add(newClient);
    }

    public void deleteClient(BaseClient toDeleteClient) {
        this.clientList.remove(toDeleteClient);
    }

    public void updateManager(String newManagerIp) {
        this.ipManagerClient = newManagerIp;
    }

    public void gameStarted() {
        Launcher.gameStarted();
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    public void exitLobby(){
        this.clientWebClientPart.exitLobby();
    }
}
