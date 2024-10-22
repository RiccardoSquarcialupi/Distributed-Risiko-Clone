package app.game.comunication;

import app.Launcher;
import app.game.GameClient;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;
import com.google.common.hash.Hashing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameReceiver extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;
    private final GameClient gameClient;
    private boolean isRunning = false;

    public GameReceiver(GameClient gameClient) {
        this.httpServer = Launcher.getVertx().createHttpServer();
        this.gameClient = gameClient;
        this.router = Router.router(vertx);
    }

    public void start() {

        //GET NOTIFY A CLIENT END HIS TURN
        router
                .put("/client/game/turn/finish")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        gameClient.checkForMyTurn(body.toJsonArray().getString(0));
                    });

                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIFY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY
        router
                .put("/client/game/territory/armies")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territorySender = body.toJsonArray().getString(1);
                        var territoryReceiver = body.toJsonArray().getString(2);
                        var nArmiesChange = body.toJsonArray().getInteger(3);
                        System.out.println("Armies moved from sender " + territorySender + " to receiver " + territoryReceiver + ", nArmies " + nArmiesChange);
                        this.gameClient.updateEnemyTerritory(ip, Territory.fromString(territorySender), Territory.fromString(territoryReceiver), nArmiesChange);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIFY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY WITH A CONQUEROR
        router
                .put("/client/game/territory/armiesWithConqueror")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var winnerIp = body.toJsonArray().getString(0);
                        var territory = body.toJsonArray().getString(1);
                        var nArmies = body.toJsonArray().getInteger(2);
                        var loserIp = body.toJsonArray().getString(3);
                        System.out.println("Received armies with conqueror with winner " + winnerIp + ", territory " + territory + ", nArmies " + nArmies + ", loser " + loserIp);
                        this.gameClient.updateEnemyTerritoryWithConqueror(winnerIp, Territory.fromString(territory), nArmies, loserIp);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN A BONUS FROM STATE CARD HAVE BEEN USED
        router
                .put("/client/game/card")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var cardsList = (List<CardType>) body.toJsonArray().getJsonArray(1).getList();
                        var bonusArmies = body.toJsonArray().getInteger(2);
                        this.gameClient.someoneGetBonus(ip, cardsList, bonusArmies);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN THE LOBBY CLOSED
        router
                .put("/client/lobby")
                .handler(routingContext -> {
                    this.gameClient.closeConnection();
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN AN OFFENSIVE MOVE START
        router
                .put("/client/game/battle/offense")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ipClientAttack = body.toJsonArray().getString(0);
                        var ipClientDefend = body.toJsonArray().getString(1);
                        var diceATKResult = ((List<Integer>) body.toJsonArray().getJsonArray(2).getList().get(0));
                        var territoryEnemy = Territory.fromString(body.toJsonArray().getString(3));
                        var myTerritory = Territory.fromString(body.toJsonArray().getString(4));
                        this.gameClient.receiveAttackMsg(ipClientAttack, ipClientDefend, diceATKResult, territoryEnemy, myTerritory);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY OF THE DEFENSE RESPONSE
        router
                .put("/client/game/battle/defense")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ipClientAttack = body.toJsonArray().getString(0);
                        var ipClientDefend = body.toJsonArray().getString(1);
                        var diceDEFResult = ((List<Integer>) body.toJsonArray().getJsonArray(2).getList().get(0));
                        var myTerritory = Territory.fromString(body.toJsonArray().getString(3));
                        var enemyTerritory = Territory.fromString(body.toJsonArray().getString(4));
                        this.gameClient.receiveDefendMsg(ipClientAttack, ipClientDefend, diceDEFResult, myTerritory, enemyTerritory);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN A PLAYER WIN
        router
                .put("/client/game/win")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var goalCard = Goal.valueOf(body.toJsonArray().getString(1));
                        var playerDestroyed = body.toJsonArray().getString(2);
                        if(this.gameClient.someoneWin(ip, goalCard, playerDestroyed)){
                            routingContext.response().setStatusCode(200).end();
                        }
                        else{
                            //Client fake the win
                            routingContext.response().setStatusCode(403).end();
                        }
                    });

                });
        //RECEIVE TERRITORY INFO FROM THE OTHERS PLAYERS
        router
                .put("/client/game/territories")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territory = (List<Territory>) body.toJsonArray().getJsonArray(1).getList()
                                .stream().map(s -> Territory.fromString(s.toString())).collect(Collectors.toList());
                        System.out.println("Received terri: " + territory);
                        territory.forEach(t -> this.gameClient.setEnemyTerritory(ip, t));

                        if (this.gameClient.areTerritoriesReceived()) {
                            System.out.println("start place armies");
                            this.gameClient.placeFirstArmies();
                        }
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //RECEIVE STATE CARD FROM THE MANAGER AFTER CONQUER A TERRITORY
        router
                .put("/manager/game/territory")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });

        //HANDLE PLAYER LEFFT THE GAME
        router.put("/client/game/player").handler(routingContext -> {
            routingContext.request().bodyHandler(body -> {
                var ip = body.toJsonArray().getString(0);
                this.gameClient.playerLeft(ip);
            });
            routingContext.response().setStatusCode(200).end();
        });

        // RECEIVE ARMIES UPDATE FROM ENEMY.
        router
                .put("/client/game/armies/update")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var country = Territory.fromString(body.toJsonArray().getString(0));
                        var armies = body.toJsonArray().getInteger(1);
                        this.gameClient.updateEnemyArmies(country, armies);
                    });
                    routingContext.response().setStatusCode(200).end();
                });

        router.put("/client/game/order")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var order = body.toJsonArray().getJsonArray(1);
                        System.out.println("Received order: " + order);
                        //TRYING TO CONVERT ORDER IN A LIST<JSONCLIENT>
                        List<JSONClient> jsonClients = new ArrayList<>();
                        for (int i = 0; i < order.size(); i++) {
                            jsonClients.add(JSONClient.fromJson((JsonObject) order.getValue(i)));
                        }
                        System.out.println("Received order converted in List<JSONClient>: " + jsonClients);
                        this.gameClient.receiveRandomOrder(ip, jsonClients);
                    });
                    routingContext.response().setStatusCode(200).end();
                });

        Map<String, String> clientDiceHash = new HashMap<>();
        Map<String, Integer> clientDiceShare = new HashMap<>();
        AtomicInteger myDice = new AtomicInteger();
        // RECEIVE DICE THROW INIT.
        router
                .post("/client/game/dice/throw")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ipClient = body.toJsonArray().getString(0);
                        var hDice = body.toJsonArray().getString(1);
                        clientDiceHash.clear();
                        clientDiceShare.clear();
                        clientDiceHash.put(ipClient, hDice);
                        var resp = new JsonArray();
                        myDice.set((new Random()).nextInt(6) + 1);
                        resp.add(myDice.get());
                        ((GameClientImpl)Launcher.getCurrentClient()).sendDiceShare(myDice.get()).onSuccess(s ->{
                            routingContext.response().setStatusCode(200).end(resp.toBuffer());
                        });
                    });
                });

        // RECEIVE DICE SHARE.
        router
                .put("/client/game/dice/share")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body ->{
                       var ipClient = body.toJsonArray().getString(0);
                       var rDice = body.toJsonArray().getInteger(1);
                       clientDiceShare.put(ipClient, rDice);
                       routingContext.response().setStatusCode(200).end();
                    });
                });

        // RECEIVE DICE CONFIRM.
        router
                .put("/client/game/dice/confirm")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ipClient = body.toJsonArray().getString(0);
                        var rDice = body.toJsonArray().getInteger(1);
                        var kDice = Base64.getDecoder().decode(body.toJsonArray().getString(2));
                        var sDice = body.toJsonArray().getInteger(3);
                        var csDice = clientDiceShare.values().stream().mapToInt(Integer::valueOf).sum()
                                + myDice.get() + rDice;
                        String chDice = Hashing.hmacSha256(kDice).hashInt(rDice).toString();
                        if (chDice.equals(clientDiceHash.get(ipClient)) && sDice == csDice) {
                            routingContext.response().setStatusCode(200).end();
                        } else {
                            routingContext.response().setStatusCode(403).end();
                        }
                        clientDiceHash.clear();
                        clientDiceShare.clear();
                    });

                });

        //GET NOTIFY WHEN SOMEONE GET A CARD
        router
                .put("/client/game/stateCard")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body ->{
                        var ipClient = body.toJsonArray().getString(0);
                        var cardIndex = body.toJsonArray().getInteger(1);
                        this.gameClient.someoneDrawStateCard(ipClient, cardIndex);
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        httpServer.requestHandler(router).listen(5001);
        isRunning = true;
    }

    public void stop() {
        this.httpServer.close();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
