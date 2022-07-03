package app.lobby;

import app.Launcher;
import app.base.BaseClient;
import app.base.BaseClientImpl;

import java.util.ArrayList;
import java.util.List;

public class LobbyClientImpl extends BaseClientImpl implements LobbyClient {

    protected List<BaseClient> clientList;
    private int idLobby;
    private String ipManagerClient;

    protected ClientServerPart clientServerPart;
    protected ClientWebClientPart clientWebClientPart;

    public LobbyClientImpl(String ip, String nickname, int idLobby, String ipManagerClient) {
        super(ip, nickname);
        this.idLobby = idLobby;
        this.ipManagerClient = ipManagerClient;
        this.clientList = new ArrayList<>();
        this.clientWebClientPart = new ClientWebClientPart();
        this.clientServerPart = new ClientServerPart(this);
    }

    @Override
    public String getIpManagerClient(){
        return this.ipManagerClient;
    }

    @Override
    public void start() {
        this.clientServerPart.start();
    }

    @Override
    public void stop() {
        this.clientServerPart.stop();
    }

    @Override
    public void addNewClient(BaseClient newClient) {
        this.clientList.add(newClient);
    }

    @Override
    public void deleteClient(BaseClient toDeleteClient) {
        this.clientList.remove(toDeleteClient);
    }

    @Override
    public void updateManager(String newManagerIp) {
        this.ipManagerClient = newManagerIp;
    }

    @Override
    public void gameStarted() {
        Launcher.gameStarted();
    }

    @Override
    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    @Override
    public void exitLobby(){
        this.clientWebClientPart.exitLobby();
    }
}
