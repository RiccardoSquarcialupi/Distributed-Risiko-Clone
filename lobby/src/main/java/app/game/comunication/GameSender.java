package app.game.comunication;

import app.Launcher;
import app.common.Utils;
import app.game.GameClientImpl;
import app.game.card.Card;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;
import com.google.common.hash.Hashing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameSender extends AbstractVerticle {

    private final WebClient client;

    public GameSender() {
        client = WebClient.create(Launcher.getVertx());
    }

    /*
    * Broadcast the list of territories to the others players in the game
    * */
    public Future<Void> broadcastMyTerritories(String ipClient, List<Territory> territoryList) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/territories")
                    .sendJson(jsonify(ipClient, territoryList))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my territories, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my territories to the server");
                                lpv.get(index).fail("Something went wrong when sending my territories to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my territories: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
     * Notify a client finish his turn
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
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my turn is finished, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my turn is finished to the server");
                                lpv.get(index).fail("Something went wrong when sending my turn is finished to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my turn is finished: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
     * Notify players when someone get a State Card
     * */
    public Future<Void> drawStateCard(String ipClient, int cardIndex) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/stateCard")
                    .sendJson(jsonify(ipClient, cardIndex))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my state card, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my state card to the server");
                                lpv.get(index).fail("Something went wrong when sending my state card to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
     * Notify players when armies number change (nArmies could be negative if armies moved between to adjacent
     * territories) in a specific territory (optional idClient is provided in case of obtained new territory
     * from a battle)
     * */
    public Future<Void> changeArmiesInTerritory(String ipClient, Territory territory, Optional<Territory> territoryReceiver, Integer armies, Optional<String> ipClientLoser) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        if (ipClientLoser.isPresent()) {
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/armiesWithConqueror")
                        .sendJson(jsonify(ipClient, territory.getName(), armies, ipClientLoser.get()))
                        .onSuccess(r -> {
                            switch (r.statusCode()) {
                                case 200:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " receive the info about my armies in territory, " + r.statusCode());
                                    lpv.get(index).complete();
                                    break;
                                case 500:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    break;
                                default:
                                    System.out.println("Something went wrong when sending my armies in territory to the server");
                                    lpv.get(index).fail("Something went wrong when sending my armies in territory to the server");
                                    break;
                            }
                        }).onFailure(err -> {
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage());
                            lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });

            }
        } else if(territoryReceiver.isPresent()){
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/armies")
                        .sendJson(jsonify(ipClient, territory.getName(), territoryReceiver.get().getName(), armies))
                        .onSuccess(r -> {
                            switch (r.statusCode()) {
                                case 200:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " receive the info about my armies in territory, " + r.statusCode());
                                    lpv.get(index).complete();
                                    break;
                                case 500:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    break;
                                default:
                                    System.out.println("Something went wrong when sending my armies in territory to the server");
                                    lpv.get(index).fail("Something went wrong when sending my armies in territory to the server");
                                    break;
                            }
                        })
                        .onFailure(err -> {
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage());
                            lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });
            }
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
     * Notify players when bonus from State Card have been used
     * */
    public Future<Void> clientUseStateCardsBonus(String ipClient, List<CardType> listCard, Integer bonusArmies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/card")
                    .sendJson(jsonify(ipClient, JsonArray.of(listCard), bonusArmies))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my state card bonus, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my state card bonus to the server");
                                lpv.get(index).fail("Something went wrong when sending my state card bonus to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card bonus: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
    Notify players if an offense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientAttackTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory territoryFromToAttack, Territory territoryToAttack) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(ipClientAttack)).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/offense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceATKResult),
                            territoryFromToAttack.getName(), territoryToAttack.getName()))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my attack, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my attack to the server");
                                lpv.get(index).fail("Something went wrong when sending my attack to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my attack: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
    Notify players if a defense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientDefendTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory enemyTerritory, Territory myTerritory) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(ipClientDefend)).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/defense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceDEFResult), enemyTerritory.getName(), myTerritory.getName()))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about my defense, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my defense to the server");
                                lpv.get(index).fail("Something went wrong when sending my defense to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my defense: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    /*
    Notify players if a Client has won the game. The winner is provided with the goal card and the list of territories owned
     */
    public Future<Void> clientWin(String ipClient, Goal goalCard, Optional<String> playerDestroyed) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            if(playerDestroyed.isPresent()){
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/win")
                        .sendJson(jsonify(ipClient, goalCard, playerDestroyed.get()))
                        .onSuccess(r -> {
                            switch (r.statusCode()) {
                                case 200:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " receive the info about my win, " + r.statusCode());
                                    lpv.get(index).complete();
                                    break;
                                case 500:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    break;
                                case 403:
                                        System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " try to fake the win");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            "try to fake the win");
                                    break;
                                default:
                                    System.out.println("Something went wrong when sending my win to the server");
                                    lpv.get(index).fail("Something went wrong when sending my win to the server");
                                    break;
                            }
                        })
                        .onFailure(err -> {
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my win: " + err.getMessage());
                            lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });
            }else{
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/win")
                        .sendJson(jsonify(ipClient, goalCard, ""))
                        .onSuccess(r -> {
                            switch (r.statusCode()) {
                                case 200:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " receive the info about my win, " + r.statusCode());
                                    lpv.get(index).complete();
                                    break;
                                case 500:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    break;
                                case 403:
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " try to fake the win");
                                    lpv.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            "try to fake the win");
                                    break;
                                default:
                                    System.out.println("Something went wrong when sending my win to the server");
                                    lpv.get(index).fail("Something went wrong when sending my win to the server");
                                    break;
                            }
                        })
                        .onFailure(err -> {
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my win: " + err.getMessage());
                            lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });
            }
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
        if(nDices < 1){
            prm.complete(List.of());
            return prm.future();
        }
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        var lpv = finalClientList.stream()
                .map(c -> Promise.promise()).collect(Collectors.toList());
        var rDice = (new Random()).nextInt(6) + 1;
        var kDice = Utils.intToByteArray((new Random()).nextInt());
        var hDice = Hashing.hmacSha256(kDice).hashInt(rDice).toString();
        var sDice = new AtomicInteger(rDice);
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .post(5001, finalClientList.get(index).getIP(), "/client/game/dice/throw")
                    .sendJson(jsonify(ipClient, hDice))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                sDice.addAndGet(r.body().toJsonArray().getInteger(0));
                                lpv.get(index).complete();
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the dice throw, " + r.statusCode());
                                break;
                            case 500:
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my dice throw to the server");
                                lpv.get(index).fail("Something went wrong when sending my dice throw to the server");
                                break;
                        }


                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice throw: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        var lp = finalClientList.stream()
                .map(c -> Promise.promise()).collect(Collectors.toList());
        Future.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            for (int i = 0; i < finalClientList.size(); i++) {
                final int index = i;
                this.client
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/dice/confirm")
                        .sendJson(jsonify(
                                ipClient,
                                rDice,
                                Base64.getEncoder().encodeToString(kDice),
                                sDice.get()))
                        .onSuccess(r -> {
                            switch (r.statusCode()) {
                                case 200:
                                    System.out.println("Dice status code: " + r.statusCode());
                                    lp.get(index).complete();
                                    System.out.println("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " receive the dice confirm, " + r.statusCode());
                                    break;
                                case 500:
                                    lp.get(index).fail("Client " +
                                            finalClientList.get(index).getNickname() +
                                            " is down");
                                    break;
                                case 403:
                                    lp.get(index).fail("Dice throw is not eligible.");
                                    System.out.println("Dice status code: " + r.statusCode());
                                    break;
                                    //throw new RuntimeException("Dice throw is not eligible.");
                                default:
                                    System.out.println("Something went wrong when sending my dice confirm to the server");
                                    lp.get(index).fail("Something went wrong when sending my dice confirm to the server");
                                    break;
                            }
                        })
                        .onFailure(err ->{
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice confirm: " + err.getMessage());
                            lp.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });
            }
        });
        Future.all(lp.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            List<Integer> diceResults = new ArrayList<>(List.of((sDice.get() % 6) + 1));
            if (nDices > 1) {
                this.byzantineDiceLaunch(ipClient, nDices - 1).onSuccess(ss -> {
                    diceResults.addAll(ss);
                    prm.complete(diceResults);
                });
            } else {
                prm.complete(diceResults);
            }
        }).onFailure(prm::fail);
        return prm.future();
    }

    public Future<Void> sendDiceShare(String ip, int rnd){
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        var lpv = finalClientList.stream()
                .map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/dice/share")
                    .sendJson(jsonify(ip, rnd))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                lpv.get(index).complete();
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the dice share, " + r.statusCode());
                                break;
                            case 500:
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my dice share to the server");
                                lpv.get(index).fail("Something went wrong when sending my dice share to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice share: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        Future.all(lpv.stream().map(Promise::future).collect(Collectors.toList()))
                .onSuccess(s -> prm.complete())
                .onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    public Future<Void> placeArmies(String country, Integer armies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/armies/update")
                    .sendJson(jsonify(country, armies))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about armies update, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending my armies update to the server");
                                lpv.get(index).fail("Something went wrong when sending my armies update to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies update: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    public Future<Void> sendRandomOrderForTurning(String ip, List<JSONClient> clientList) {
        Promise<Void> prm = Promise.promise();
        var jsonObjectClientList = clientList.stream().map(JSONClient::toJson).collect(Collectors.toList());
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/order")
                    .sendJson(jsonify(ip, jsonObjectClientList))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the random order, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending the random order to the server");
                                lpv.get(index).fail("Something went wrong when sending the random order to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the random order " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());

        return prm.future();
    }

    public Future<Void> playerLeft(String ip) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/player")
                    .sendJson(jsonify(ip))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the info about player left, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending the info about player left to the server");
                                lpv.get(index).fail("Something went wrong when sending the info about player left to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the random order " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(s -> prm.fail(s.getMessage()));

        return prm.future();
    }
}
