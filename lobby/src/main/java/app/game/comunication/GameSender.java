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

    WebClient client;

    public GameSender() {
        client = WebClient.create(Launcher.getVertx());
    }

    /*
     * Get notify a client starts his turn
     * */
    protected Future<Void> clientStartTurn(int idClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/turn/start")
                    .sendJson(jsonify(idClient))
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
    protected Future<Void> clientEndTurn(int idClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/turn/finish")
                    .sendJson(jsonify(idClient))
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
    protected Future<Void> clientGetStateCard(int idClient) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/stateCard")
                    .sendJson(jsonify(idClient))
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
    protected Future<Void> clientChangeArmiesInTerritory(int idClient, Territory territory, int armies, Optional<Integer> idClientConqueror) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/" + territory.ordinal() + "/armies")
                    .sendJson(jsonify(idClient, armies, idClientConqueror))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my armies in territory, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage()));
        }
        return prm.future();
    }

    /*
     * Get notify when bonus from State Card have been used
     * */

    protected Future<Void> clientUseStateCardBonus(int idClient, List<CardType> listCardType, int bonusArmies, int extraBonusArmies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .delete(5001, finalClientList.get(index).getIP(), "/client/game/card")
                    .sendJson(jsonify(idClient, listCardType, bonusArmies, extraBonusArmies))
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
    protected Future<Void> clientAttackTerritory(int idClientAttack, int idClientDefend, List<Integer> diceATKResult, int numberOfDices) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/offense")
                    .sendJson(jsonify(idClientAttack, idClientDefend, diceATKResult, numberOfDices))
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
    Get notify if an defense move is starting: attacker and defender are provided with the result of the dices roll
     */
    protected Future<Void> clientDefendTerritory(int idClientAttack, int idClientDefend, List<Integer> diceDEFResult, int numberOfDices) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/defense")
                    .sendJson(jsonify(idClientAttack, idClientDefend, diceDEFResult, numberOfDices))
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
    protected Future<Void> clientWin(int idClient, Goal goalCard, List<Territory> territoryOwnedList) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/win")
                    .sendJson(jsonify(idClient, goalCard, territoryOwnedList))
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
