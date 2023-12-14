package app.lobby;

import app.game.card.*;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final List<Territory> cards;
    private final List<Goal> goalCards;
    private final ContextManagerParameters cltPar;

    public ManagerClientImpl(ContextManagerParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        this.goalCards = new ArrayList<>(List.of(Goal.values()));
        this.cards = new ArrayList<>(List.of(Territory.values()));
    }

    @Override
    public Future<Void> managerClientChange(String newManagerIP) {
        this.cltPar.setIpManager(newManagerIP);
        return this.sender.managerClientChange(new JsonObject().put("manager_ip", newManagerIP), this.cltPar.getClientList(), this.cltPar.getIdLobby());
        //exit lobby for removing the manager from the list
        //exitLobby(new JSONClient(this.cltPar.getIp(), this.cltPar.getNickname()));
    }

    @Override
    public Future<Void> closeLobby() {
        return this.sender.lobbyClosed(this.cltPar.getIdLobby());
    }

    @Override
    public Future<Void> startGame() {
        Collections.shuffle(cards);
        Collections.shuffle(goalCards);

        List<CardType> deck = Stream.concat(cards.stream().map(Territory::getType), Stream.of(CardType.JOLLY,CardType.JOLLY)).collect(Collectors.toList());
        Collections.shuffle(deck);

        List<Promise<Void>> lpr = new ArrayList<>(this.cltPar.getMaxPlayer());

        for (int i = 0; i < this.cltPar.getMaxPlayer(); i++) {
            final int index = i;
            lpr.add(Promise.promise());
            // Move my CLIENT to be the last, so anyone will be informed before I close connection.
            if (this.cltPar.getClientList().get(i).getIP().equals(cltPar.getIp()) &&
                    i != this.cltPar.getMaxPlayer() - 1) {
                var tmp = this.cltPar.getClientList().get(i);
                this.cltPar.getClientList().remove(tmp);
                this.cltPar.getClientList().add(tmp);
            }

            this.sender.gameHasStarted(
                    JsonArray.of(
                            cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i)),
                            JsonObject.of("Goal", goalCards.get(0)),
                            deck
                    ),
                    this.cltPar.getClientList().get(i).getIP()
            ).onSuccess(s -> lpr.get(index).complete());
            cards.subList(0, cards.size() / (this.cltPar.getMaxPlayer() - i)).clear();
            goalCards.remove(0);
        }
        Promise<Void> prm = Promise.promise();
        Future.all(lpr.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s ->
            this.sender.lobbyClosed(this.cltPar.getIdLobby()).onSuccess(ss -> prm.complete())
        );
        return prm.future();
    }
}
