package app.lobby;

import app.game.card.Territory;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final List<Territory> cards;
    private final ContextManagerParameters cltPar;

    public ManagerClientImpl(ContextManagerParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.cards = new ArrayList<>(List.of(Territory.values()));
    }

    @Override
    public Future<Void> managerClientChange(String newManagerIP) {
        this.cltPar.setIpManager(newManagerIP);
        return this.sender.managerClientChange(new JsonObject().put("manager_ip",newManagerIP), this.cltPar.getClientList(),this.cltPar.getIdLobby());
        //exit lobby for removing the manager from the list
        //exitLobby(new JSONClient(this.cltPar.getIp(), this.cltPar.getNickname()));
    }

    @Override
    public Future<Void> closeLobby() {
        return this.sender.lobbyClosed(this.cltPar.getIdLobby());
    }

    @Override
    public void startGame() {
        Collections.shuffle(cards);

        for (int i = 0; i < this.cltPar.getMaxPlayer(); i++) {
            // Move my CLIENT to be the last, so anyone will be informed before i close connection.
            if(this.cltPar.getClientList().get(i).getIP().equals(cltPar.getIp()) &&
                i != this.cltPar.getMaxPlayer()-1){
                var tmp = this.cltPar.getClientList().get(i);
                this.cltPar.getClientList().remove(tmp);
                this.cltPar.getClientList().add(tmp);
            }
            this.sender.gameHasStarted(
                    JsonArray.of(cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i))),
                    this.cltPar.getClientList().get(i).getIP()
            );
            cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i)).clear();
        }
    }
}
