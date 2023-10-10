package app.lobby.comunication;

import app.lobby.LobbyClient;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClient;
import app.lobby.ManagerClientImpl;
import app.lobbySelector.JSONClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import app.Launcher;

public class LobbyReceiver extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;
    private final LobbyClientImpl lobbyClient;

    public LobbyReceiver(LobbyClient lobbyClient) {
        this.httpServer = Launcher.getVertx().createHttpServer();
        this.lobbyClient = (LobbyClientImpl) lobbyClient;
        this.router = Router.router(vertx);
    }

    public void start() {
        router
                .post("/client/lobby/clients")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        this.lobbyClient.addNewClient(JSONClient.fromJson(bh.toJsonObject()));
                        int lobbyId = lobbyClient.getLobbyId();
                        JsonArray clientList = new JsonArray(lobbyClient.getClientList());
                        JsonObject body = new JsonObject().put("lobby_id",lobbyId).put("client_list", clientList);
                        routingContext.response().putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .send(body.toBuffer());
                    });

                });

        router
                .delete("/client/lobby/clients")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        System.out.println(bh.toJsonObject());
                        this.lobbyClient.deleteClient(JSONClient.fromJson(bh.toJsonObject()));
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        router
                .put("/client/lobby/manager")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        this.lobbyClient.updateManager(bh.toJsonObject().getString("manager_ip"));
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        router
                .put("/client/lobby/game")
                .handler(routingContext -> {
                    this.lobbyClient.gameStarted();
                    routingContext.response().setStatusCode(200).end();
                });

        router
                .delete("/client/lobby")
                .handler(routingContext -> {
                    this.lobbyClient.lobbyClosed();
                    routingContext.response().setStatusCode(200).end();
                });

        router
                .get("/manager/lobby/clients")
                .handler(routingContext -> {
                    if (this.lobbyClient instanceof ManagerClientImpl) {
                        JsonArray jarr = new JsonArray();
                        for (JSONClient bc : ((ManagerClient) this.lobbyClient).getClientList()) {
                            jarr.add(bc.toJson());
                        }
                        routingContext.response()
                                .putHeader("Content-Type", "application/json")
                                .end(jarr.toBuffer());

                    } else {
                        routingContext.response().end();
                    }
                });

        httpServer.requestHandler(router).listen(5001);
    }

    public void stop() {
        this.httpServer.close();
    }
}
