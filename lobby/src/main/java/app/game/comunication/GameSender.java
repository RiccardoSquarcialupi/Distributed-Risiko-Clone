package app.game.comunication;

import app.Launcher;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;

import java.util.List;
import java.util.Optional;
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

    /*
     * Get notify a client finish his turn
     * */
    public  Future<Void> clientEndTurn(String ipClient) {
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
    public Future<Void> clientGetStateCard(String ipClient) {
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
    public Future<Void> clientChangeArmiesInTerritory(String ipClient, Territory territory, Integer armies, Optional<String> ipClientConqueror) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        if(ipClientConqueror.isPresent()){
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
        }else{
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/" + territory.ordinal() + "/armies")
                        .sendJson(jsonify(ipClient, territory.name(), armies))
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
                    .sendJson(jsonify(ipClient, listCardType, bonusArmies, extraBonusArmies))
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
    public Future<Void> clientAttackTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Integer numberOfDices) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/offense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, diceATKResult, numberOfDices))
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
    public Future<Void> clientDefendTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Integer numberOfDices) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/defense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, diceDEFResult, numberOfDices))
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
                    .sendJson(jsonify(ipClient, goalCard, territoryOwnedList))
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


}
