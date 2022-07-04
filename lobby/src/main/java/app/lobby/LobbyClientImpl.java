package app.lobby;

import app.Launcher;
import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.base.JSONClient;
import app.manager.client.Client;
import app.manager.client.ClientParameters;

import java.util.ArrayList;
import java.util.List;

public class LobbyClientImpl extends BaseClientImpl implements LobbyClient {
    private ClientParameters cltPar;
    protected ServerPart serverPart;
    protected ClientPart clientPart;

    public LobbyClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.clientPart = new ClientPart();
        this.serverPart = new ServerPart(this);
    }

    @Override
    public String getIpManager(){
        return this.cltPar.getIpManager();
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

    protected void addNewClient(JSONClient newClient) {
        this.cltPar.addClient(newClient);
    }

    protected void deleteClient(JSONClient toDeleteClient) {
        this.cltPar.deleteClient(toDeleteClient);
    }

    protected void updateManager(String newManagerIp) {
        this.cltPar.setIpManager(newManagerIp);
    }

    protected void gameStarted() {
        Launcher.gameStarted();
    }

    protected void lobbyClosed() {
        Launcher.lobbyClosed();
    }


}
