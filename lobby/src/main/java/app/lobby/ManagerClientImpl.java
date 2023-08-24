package app.lobby;

import app.base.JSONClient;
import app.game.Card;
import app.manager.client.ClientParameters;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final List<Card> cards;
    private ClientParameters cltPar;

    public ManagerClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.cards = new ArrayList<>(List.of(Card.values()));
    }

    @Override
    public List<JSONClient> getClientList() {
        return this.cltPar.getClientList();
    }

    @Override
    public void managerClientChange(String newManagerIP) {
        this.clientPart.managerClientChange(JsonObject.mapFrom(newManagerIP), this.cltPar.getClientList());
    }

    @Override
    public void startGame() {
        var deck = cards.subList(0, 41); //using first 42 card for assigning territory to players, last 2 cards are wilds cards
        Collections.shuffle(deck);
        for (int i = 0; i < this.cltPar.getMaxPlayer(); i++) {
            this.clientPart.gameHasStarted(JsonObject.mapFrom((deck.subList(0, deck.size() / (this.cltPar.getMaxPlayer() - i)))),this.cltPar.getClientList().get(i).getIP());
            deck.subList(0, deck.size() / (this.cltPar.getMaxPlayer() - i)).clear();
        }
    }
}
