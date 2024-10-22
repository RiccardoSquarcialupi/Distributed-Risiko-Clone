package app.lobby;

import app.Launcher;
import app.game.card.Card;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobby.comunication.LobbyReceiver;
import app.lobby.comunication.LobbySender;
import app.lobbySelector.JSONClient;
import app.lobbySelector.LobbySelectorClientImpl;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.swing.*;
import java.util.List;
import java.util.Timer;

public class LobbyClientImpl extends LobbySelectorClientImpl implements LobbyClient {
    private final ContextManagerParameters cltPar;
    public LobbyReceiver lobbyReceiver;
    public LobbySender sender;

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
    public Future<Void> exitLobby() {
        return this.sender.exitLobby(new JSONClient(cltPar.getIp(), cltPar.getNickname()), cltPar.getIdLobby(), cltPar.getClientList());
    }

    @Override
    public List<JSONClient> getClientList() {
        return this.cltPar.getClientList();
    }

    @Override
    public int getLobbyMaxPlayers() {
        return this.cltPar.getMaxPlayer();
    }

    public void addNewClient(JSONClient newClient) {
        this.cltPar.addClient(newClient);
    }

    public void deleteClient(JSONClient toDeleteClient) {
        this.cltPar.deleteClient(toDeleteClient);
    }

    public void updateManager(String newManagerIp) {
        this.cltPar.setIpManager(newManagerIp);
        if (cltPar.getIp().equals(newManagerIp)) {
            //LobbyClient become ManagerClient, update GUI and Client type
            Launcher.becomeManager();
        }
    }

    public void gameStarted(List<Territory> listTerritory, List<CardType> deck, Goal goalCard) {
        listTerritory.forEach(this.cltPar::addTerritory);
        this.cltPar.setDeck(deck);
        this.cltPar.setGoalCard(goalCard);
        Launcher.gameStarted();
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    public int getLobbyId() {
        return cltPar.getIdLobby();
    }

    public Future<Void> broadcastClientIp() {
        Promise<Void> prm = Promise.promise();
        this.sender.broadcastClientIp().onFailure(err -> {
            System.out.println("Error with broadcastClientIp" + err.getMessage());
            Launcher.getVertx().setTimer(2000, id -> this.broadcastClientIp().onSuccess(v -> prm.complete()));
        }).onSuccess(prm::complete);
        return prm.future();
    }
}
