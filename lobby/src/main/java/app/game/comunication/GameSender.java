package app.game.comunication;

import app.Launcher;
import app.common.Utils;
import app.game.GameClientImpl;
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my start turn: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my territories: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my turn is finished: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
        return prm.future();
    }

    /*
     * Get notify when armies number change(nArmies could be negative if armies moved between to adjacent
     * territories) in a specific territory(optional idClient is provided in case of obtained new territory
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
                        .put(5001, finalClientList.get(index).getIP(), "/client/game/territory/" + territory.ordinal() + "/armiesWithConqueror")
                        .sendJson(jsonify(ipClient, territory.name(), armies, ipClientLoser.get()))
                        .onSuccess(response -> {
                            System.out.println("Client " +
                                    finalClientList.get(index).getNickname() +
                                    " receive the info about my armies in territory, " + response.statusCode());
                            lpv.get(index).complete();
                        });

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
                        .onFailure(err -> {
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies in territory: " + err.getMessage());
                            lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                        });
            }
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my state card bonus: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
        return prm.future();
    }

    /*
    Get notify if an offense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientAttackTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory territoryFromToAttack, Territory territoryToAttack) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(ipClientAttack)).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/offense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceATKResult), territoryFromToAttack.name(), territoryToAttack.name()))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my attack, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my attack: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
        return prm.future();
    }

    /*
    Get notify if a defense move is starting: attacker and defender are provided with the result of the dices roll
     */
    public Future<Void> clientDefendTerritory(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory enemyTerritory, Territory myTerritory) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(ipClientDefend)).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/battle/defense")
                    .sendJson(jsonify(ipClientAttack, ipClientDefend, JsonArray.of(diceDEFResult), enemyTerritory.name(), myTerritory.name()))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about my defense, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my defense: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my win: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
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
                    .onSuccess(response -> {
                        sDice.addAndGet(response.body().toJsonArray().getInteger(0));
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
                        .sendJson(jsonify(
                                ipClient,
                                rDice,
                                Base64.getEncoder().encodeToString(kDice),
                                sDice.get()))
                        .onSuccess(response -> {
                            System.out.println("Dice status code: " + response.statusCode());
                            lp.get(index).complete();
                        })
                        .onFailure(err ->
                                System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the dice confirm: " + err.getMessage()));
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
        });
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
                    .onSuccess(response -> {
                        lpv.get(index).complete();
                    })
                    .onFailure(err -> {
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        Future.all(lpv.stream().map(Promise::future).collect(Collectors.toList()))
                .onSuccess(s -> prm.complete())
                .onFailure(s -> prm.fail(s.getMessage()));
        return prm.future();
    }

    public Future<Void> broadcastArmies(String country, Integer armies) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());
        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(index).getIP(), "/client/game/armies/update")
                    .sendJson(jsonify(country, armies))
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the info about armies update, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the info about my armies update: " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
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
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the random order, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't receive the random order " + err.getMessage());
                        lpv.get(index).fail("Error with" + finalClientList.get(index).getNickname());
                    });
        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());

        return prm.future();
    }
}
