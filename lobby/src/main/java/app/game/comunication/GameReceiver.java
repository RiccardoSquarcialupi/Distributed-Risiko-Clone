package app.game.comunication;

import app.game.GameClient;
import app.game.GameClientImpl;
import app.game.card.Territory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import app.Launcher;

import java.util.Optional;

public class GameReceiver extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;
    private boolean isRunning = false;
    private final GameClientImpl gameClient;

    public GameReceiver(GameClient gameClient) {
        this.httpServer = Launcher.getVertx().createHttpServer();
        this.gameClient = (GameClientImpl) gameClient;
        this.router = Router.router(vertx);
    }

    //TODO: THIS ARE ONLY SKELETONS, IMPLEMENT THEM
    public void start(){

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
                        gameClient.checkforMyTurn(body.toJsonArray().getString(0));
                    });

                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIFY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY
        router
                .put("/client/game/{id}/armies")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territory = body.toJsonArray().getString(1);
                        var nArmiesChange = body.toJsonArray().getInteger(2);
                        this.gameClient.updateTerritory(ip, Territory.fromName(territory), nArmiesChange, Optional.empty());
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIFY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY WITH A CONQUEROR
        router
                .put("/client/game/{id}/armies")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(body -> {
                        var ip = body.toJsonArray().getString(0);
                        var territory = body.toJsonArray().getString(1);
                        var nArmiesChange = body.toJsonArray().getInteger(2);
                        var conquerorIp = body.toJsonArray().getString(3);
                        this.gameClient.updateTerritory(ip, Territory.fromName(territory), nArmiesChange,Optional.of(conquerorIp));
                    });
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN A BONUS FROM STATE CARD HAVE BEEN USED
        router
                .delete("/client/game/card")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN THE LOBBY CLOSED
        router
                .put("/client/lobby")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN AN OFFENSIVE MOVE START
        router
                .put("/client/game/battle/offense")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY OF THE DEFENSE RESPONSE
        router
                .put("/client/game/battle/defense")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN SOMEONE GET A CARD
        router
                .put("/client/game/cards/stateCard")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY WHEN A PLAYER WIN
        router
                .put("/client/game/win")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //RECEIVE INITIAL TERRITORY FROM THE MANAGER
        router
                .put("/client/game/territories")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //RECEIVE STATE CARD FROM THE MANAGER AFTER CONQUER A TERRITORY
        router
                .put("/manager/game/territory")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });

        httpServer.requestHandler(router).listen(8080);
        isRunning = true;
    }
    public void stop(){
        this.httpServer.close();
        isRunning= false;
    }

    public boolean isRunning(){
        return isRunning;
    }

}
