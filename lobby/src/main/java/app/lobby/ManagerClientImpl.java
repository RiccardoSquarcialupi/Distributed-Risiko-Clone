package app.lobby;

import app.base.BaseClient;
import app.base.JSONClient;
import app.manager.client.ClientParameters;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final List<Integer> cards;
    private ClientParameters cltPar;

    public ManagerClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.cards = new ArrayList<>(Stream.iterate(1, n -> n + 1).limit(42).collect(Collectors.toList()));
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
            Stream.iterate(0, n -> cards.remove(0)).limit(cards.size() / (this.cltPar.getMaxPlayer() - i));
        }
    }
}
