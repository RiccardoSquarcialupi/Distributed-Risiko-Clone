package app.game.comunication;

import app.Launcher;
import app.common.Utils;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import com.google.common.hash.Hashing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GameSender extends AbstractVerticle {

    private final WebClient client;

    public GameSender() {
        client = WebClient.create(Launcher.getVertx());
    }

    /*
     * Get notify a client starts his turn
     * */
    public Future<Void> clientStartTurn(String ipClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/turn/start")
                    .sendJson(jsonify(ipClient))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my start turn, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my start turn: " + err.getMessage()));
        }
        return prm.future();
    }

    public Future<Void> broadcastMyTerritories(String ipClient, List<Territory> territoryList) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/territories")
                    .sendJson(jsonify(ipClient, territoryList))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my territories, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my territories: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
     * Get notify a client finish his turn
     * */
    public Future<Void> clientEndTurn(String ipClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/turn/finish")
                    .sendJson(jsonify(ipClient))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my turn is finished, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my turn is finished: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
     * Get notify when someone get a State Card
     * */
    public Future<Void> getStateCard(String ipClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/stateCard")
                    .sendJson(jsonify(ipClient))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my state card, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
     * Get notify when armies number change(nArmies could be negative if armies moved between to adjacent
     * territories) in a specific territory(optional idClient is provided in case of obtained new territory
     * from a battle)
     * */
    public Future<Void> changeArmiesInTerritory(String ipClient, Territory territory, Optional<Territory> territoryReceiver, Integer armies, Optional<String> ipClientConqueror) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        if (ipClientConqueror.isPresent()) {
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/" + territory.ordinal() + "/armiesWithConqueror")
                        .sendJson(jsonify(ipClient, territory.name(), armies, ipClientConqueror.get()))
                        .onSuccess(response -> {
                            System.out.println("Client " +
                                    finalClientList.get(index).getNickname() +
                                    " receive the info about my armies in territory, " + response.statusCode());
                            lpv.get(index).complete();
                        })
                        .onFailure(err ->
                                System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage()));
            }
        } else {
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/" + territoryReceiver.get().ordinal() + "/armies")
                        .sendJson(jsonify(ipClient, territory.name(), territoryReceiver.get().name(), armies))
                        .onSuccess(response -> {
                            System.out.println("Client " +
                                    finalClientList.get(index).getNickname() +
                                    " receive the info about my armies in territory, " + response.statusCode());
                            lpv.get(index).complete();
                        })
                        .onFailure(err ->
                                System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage()));


            }

        }
        return prm.future();
    }

    /*
     * Get notify when bonus from State Card have been used
     * */

    public Future<Void> clientUseStateCardBonus(String ipClient, List<CardType> listCardType, Integer bonusArmies, Integer extraBonusArmies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .delete(5001, finalClientList.get(index).getIP(), "/client/game/card")
                    .sendJson(jsonify(ipClient, JsonArray.of(listCardType), bonusArmies, extraBonusArmies))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my state card bonus, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card bonus: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
    Get notify if an offense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientAttackTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory territory) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/offense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceATKResult), territory.name()))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my attack, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my attack: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
    Get notify if a defense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientDefendTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory territory) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/defense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceDEFResult), territory.name()))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my defense, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my defense: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
    Get notify if a Client has won the game. The winner is provided with the goal card and the list of territories owned
     */
    public Future<Void> clientWin(String ipClient, Goal goalCard, List<Territory> territoryOwnedList) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/win")
                    .sendJson(jsonify(ipClient, goalCard.name(), JsonArray.of(territoryOwnedList)))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my win, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my win: " + err.getMessage()));
        }
        return prm.future();
    }

    private JsonArray jsonify(Object... args) {
        JsonArray jsonArray = new JsonArray();
        for (Object o : args) {
            jsonArray.add(o);
        }
        return jsonArray;
    }


    public Future<List<Integer>> byzantineDiceLaunch(String ipClient, Integer nDices) {
        Promise<List<Integer>> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        var lpv = finalClientList.stream()
                .map(c -> Promise.promise()).collect(Collectors.toList());
        var rDice = (new Random()).nextInt(7);
        var kDice = Utils.intToByteArray((new Random()).nextInt());
        var hDice = Hashing.hmacSha256(kDice).hashInt(rDice).toString();
        var sDice = new AtomicInteger(rDice);
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .post(5001, finalClientList.get(index).getIP(), "/client/game/dice/throw")
                    .sendJson(jsonify(ipClient, hDice))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the dice throw, " + response.statusCode());
                        sDice.addAndGet(response.body().getInt(0));
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice throw: " + err.getMessage()));
        }
        var lp = finalClientList.stream()
                .map(c -> Promise.promise()).collect(Collectors.toList());
        Future.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/dice/confirm")
                        .sendJson(jsonify(ipClient, rDice, new String(kDice, StandardCharsets.UTF_8)))
                        .onSuccess(response -> {
                            System.out.println("Client " +
                                    finalClientList.get(index).getNickname() +
                                    " receive the dice confirm, " + response.statusCode());
                            lp.get(index).complete();
                        })
                        .onFailure(err ->
                                System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice confirm: " + err.getMessage()));
            }
        });
        Future.all(lp.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            List<Integer> diceResults = new ArrayList<>(List.of(sDice.get() % 7));
            if(nDices > 1){
                this.byzantineDiceLaunch(ipClient, nDices-1).onSuccess(ss->{
                   diceResults.addAll(ss);
                   prm.complete(diceResults);
                });
            } else {
                prm.complete(diceResults);
            }
        });
        return prm.future();
    }

    public Future<Void> broadcastArmies(String country, Integer armies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/armies/update")
                    .sendJson(jsonify(country, armies))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about armies update, " + response.statusCode());
                        prm.complete();
                    })
                    .onFailure(err ->{
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies update: " + err.getMessage());
                        prm.fail("Error");
                    });
        }
        return prm.future();
    }
}
