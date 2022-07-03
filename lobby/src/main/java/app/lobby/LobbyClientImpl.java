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

    protected ServerPart serverPart;
    protected ClientPart clientPart;

    public LobbyClientImpl(String ip, String nickname, int idLobby, String ipManagerClient) {
        super(ip, nickname);
        this.idLobby = idLobby;
        this.ipManagerClient = ipManagerClient;
        this.clientList = new ArrayList<>();
        this.clientPart = new ClientPart();
        this.serverPart = new ServerPart(this);
    }

    @Override
    public String getIpManagerClient(){
        return this.ipManagerClient;
    }

    @Override
    public void start() {
        this.serverPart.start();
    }

    @Override
    public void stop() {
        this.serverPart.stop();
    }

    @Override
    public void exitLobby(){
        this.clientPart.exitLobby();
    }

    protected void addNewClient(BaseClient newClient) {
        this.clientList.add(newClient);
    }

    protected void deleteClient(BaseClient toDeleteClient) {
        this.clientList.remove(toDeleteClient);
    }

    protected void updateManager(String newManagerIp) {
        this.ipManagerClient = newManagerIp;
    }

    protected void gameStarted() {
        Launcher.gameStarted();
    }

    protected void lobbyClosed() {
        Launcher.lobbyClosed();
    }


}
