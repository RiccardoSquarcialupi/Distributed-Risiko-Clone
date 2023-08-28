package app.lobby;

import app.Launcher;
import app.lobby.comunication.LobbySender;
import app.lobby.comunication.LobbyReceiver;
import app.lobbySelector.LobbySelectorClientImpl;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;

public class LobbyClientImpl extends LobbySelectorClientImpl implements LobbyClient {
    private ContextManagerParameters cltPar;
    public LobbyReceiver lobbyReceiver;
    public LobbySender sender;

    public LobbyClientImpl(ContextManagerParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.sender = new LobbySender();
        this.lobbyReceiver = new LobbyReceiver(this);
    }

    @Override
    public String getIpManager(){
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
    public void exitLobby(){
        this.sender.exitLobby();
    }

    public void addNewClient(JSONClient newClient) {
        this.cltPar.addClient(newClient);
    }

    public void deleteClient(JSONClient toDeleteClient) {
        this.cltPar.deleteClient(toDeleteClient);
    }

    public void updateManager(String newManagerIp) {
        this.cltPar.setIpManager(newManagerIp);
    }

    public void gameStarted() {
        Launcher.gameStarted();
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    public int getLobbyId() { return cltPar.getIdLobby(); }
}
