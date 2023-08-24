package app.lobby;

import app.base.JSONClient;
import app.game.card.Territory;
import app.manager.client.ClientParameters;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final List<Territory> cards;
    private ClientParameters cltPar;

    public ManagerClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.cards = new ArrayList<>(List.of(Territory.values()));
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
        Collections.shuffle(cards);
        for (int i = 0; i < this.cltPar.getMaxPlayer(); i++) {
            this.clientPart.gameHasStarted(JsonObject.mapFrom((cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i)))),this.cltPar.getClientList().get(i).getIP());
            cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i)).clear();
        }
    }
}
