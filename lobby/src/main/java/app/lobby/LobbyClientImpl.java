package app.lobby;

import app.Launcher;
import app.lobby.comunication.LobbyReceiver;
import app.lobby.comunication.LobbySender;
import app.lobbySelector.JSONClient;
import app.lobbySelector.LobbySelectorClientImpl;
import app.manager.contextManager.ContextManagerParameters;

import java.util.List;

public class LobbyClientImpl extends LobbySelectorClientImpl implements LobbyClient {
    public LobbyReceiver lobbyReceiver;
    public LobbySender sender;
    private final ContextManagerParameters cltPar;

    public LobbyClientImpl(ContextManagerParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.sender = new LobbySender();
        this.lobbyReceiver = new LobbyReceiver(this);
    }

    @Override
    public String getIpManager() {
        return this.cltPar.getIpManager();
    }

    @Override
    public void start() {
        this.lobbyReceiver.start();
    }

    @Override
    public void stop() {
        this.lobbyReceiver.stop();
    }

    @Override
    public void exitLobby(JSONClient client){
        this.sender.exitLobby(client,cltPar.getIdLobby(),cltPar.getIpManager());
    }

    @Override
    public List<JSONClient> getClientList() {
        return this.cltPar.getClientList();
    }

    public void addNewClient(JSONClient newClient) {
        this.cltPar.addClient(newClient);
    }

    public void deleteClient(JSONClient toDeleteClient) {
        this.cltPar.deleteClient(toDeleteClient);
    }

    public void updateManager(String newManagerIp) {
        this.cltPar.setIpManager(newManagerIp);
        if(cltPar.getIp().equals(newManagerIp)){
            //LobbyClient become ManagerClient, update GUI and Client type
            Launcher.becomeManager();
        }


    }

    public void gameStarted() {
        Launcher.gameStarted();
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    public int getLobbyId() {
        return cltPar.getIdLobby();
    }
}
