package app.game.comunication;

import app.Launcher;
import app.game.GUI.GUIGame;
import app.game.GameClient;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.stream.Collectors;

public class GameReceiver extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;
    private boolean isRunning = false;
    private final GameClient gameClient;

    public GameReceiver(GameClient gameClient) {
        this.httpServer = Launcher.getVertx().createHttpServer();
        this.gameClient = gameClient;
        this.router = Router.router(vertx);
    }

    //TODO: THIS ARE ONLY SKELETONS, IMPLEMENT THEM
    public void start() {

        //GET NOTIFY A CLIENT START HIS TURN
        router
                .put("/client/game/turn/start")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        gameClient.disableActions();
                    });

                    routingContext.response().setStatusCode(200).end();
                });
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
                .put("/client/game/{id}/armies")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territorySender = body.toJsonArray().getString(1);
                        var territoryReceiver = body.toJsonArray().getString(2);
                        var nArmiesChange = body.toJsonArray().getInteger(2);
                        this.gameClient.updateEnemyTerritory(ip, Territory.fromName(territorySender), Territory.fromName(territoryReceiver), nArmiesChange);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIFY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY WITH A CONQUEROR
        router
                .put("/client/game/{id}/armiesWithConqueror")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territory = body.toJsonArray().getString(1);
                        var nArmiesChange = body.toJsonArray().getInteger(2);
                        var conquerorIp = body.toJsonArray().getString(3);
                        this.gameClient.updateEnemyTerritoryWithConqueror(ip, Territory.fromName(territory), nArmiesChange, conquerorIp);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN A BONUS FROM STATE CARD HAVE BEEN USED
        router
                .delete("/client/game/card")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var cardsList = (List<CardType>) body.toJsonArray().getJsonArray(1).getList();
                        var bonusArmies = body.toJsonArray().getInteger(2);
                        var extraBonusArmies = body.toJsonArray().getInteger(3);
                        this.gameClient.someoneGetBonus(ip, cardsList, bonusArmies, extraBonusArmies);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN THE LOBBY CLOSED
        router
                .put("/client/lobby")
                .handler(routingContext -> {
                    this.gameClient.lobbyClosed();
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN AN OFFENSIVE MOVE START
        router
                .put("/client/game/battle/offense")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ipClientAttack = body.toJsonArray().getString(0);
                        var ipClientDefend = body.toJsonArray().getString(1);
                        var diceATKResult = ((List<Integer>) body.toJsonArray().getJsonArray(2).getList());
                        var territory = Territory.fromName(body.toJsonArray().getString(3));
                        this.gameClient.receiveAttackMsg(ipClientAttack, ipClientDefend, diceATKResult, territory);
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
                        var diceDEFResult = ((List<Integer>) body.toJsonArray().getJsonArray(2).getList());
                        var territory = Territory.fromName(body.toJsonArray().getString(3));
                        this.gameClient.receiveDefendMsg(ipClientAttack, ipClientDefend, diceDEFResult, territory);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN SOMEONE GET A CARD
        router
                .put("/client/game/cards/stateCard")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        this.gameClient.someoneDrawStateCard(ip);
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
                        var listTerritories = ((List<Territory>) body.toJsonArray().getJsonArray(2).getList());
                        this.gameClient.someoneWin(ip, goalCard, listTerritories);
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //RECEIVE TERRITORY INFO FROM THE OTHERS PLAYERS
        router
                .put("/client/game/territories")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        System.out.println(body.toJsonArray());
                        var territory = (List<Territory>)body.toJsonArray().getJsonArray(1).getList()
                                .stream().map(s -> Territory.fromName(s.toString())).collect(Collectors.toList());
                        territory.forEach(t -> this.gameClient.setEnemyTerritory(ip, t));
                    });
                    if(this.gameClient.areTerritoriesReceived()){
                        ((GUIGame)Launcher.getCurrentGui()).enableActions();
                    }
                    routingContext.response().setStatusCode(200).end();
                });
        //RECEIVE STATE CARD FROM THE MANAGER AFTER CONQUER A TERRITORY
        router
                .put("/manager/game/territory")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
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
