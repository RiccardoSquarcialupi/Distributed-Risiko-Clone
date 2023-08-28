package app.game;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class GameActionReceiver extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;
    private boolean isRunning = false;

    public GameActionReceiver() {
        this.httpServer = Vertx.vertx().createHttpServer();
        this.router = Router.router(vertx);
    }

    //TODO: THIS ARE ONLY SKELETONS, IMPLEMENT THEM
    public void start(){

        //GET NOTIFY A CLIENT START HIS TURN
        router
                .put("/client/game/turn/start")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //GET NOTIFY A CLIENT END HIS TURN
        router
                .put("/client/game/turn/finish")
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(200).end();
                });
        //Get NOTIY WHEN A CLIENT NUMBER OF ARMIES CHANGE IN TERRITORY
        router
                .put("/client/game/{id}/armies")
                .handler(routingContext -> {
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

    protected boolean isRunning(){
        return isRunning;
    }

}
