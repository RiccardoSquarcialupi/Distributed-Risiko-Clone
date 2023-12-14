package app.lobby.comunication;

import app.Launcher;
import app.game.card.Card;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobby.GUI.GUILobby;
import app.lobby.LobbyClient;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClient;
import app.lobby.ManagerClientImpl;
import app.lobbySelector.JSONClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

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
                        ((GUILobby) Launcher.getCurrentGui()).updateClientList(this.lobbyClient.getClientList());
                        if (this.lobbyClient.getClientList().size() == this.lobbyClient.getLobbyMaxPlayers()) {
                            ((GUILobby) Launcher.getCurrentGui()).enableStartButton();
                        }
                        int lobbyId = lobbyClient.getLobbyId();
                        JsonArray clientList = new JsonArray(lobbyClient.getClientList());
                        JsonObject body = new JsonObject()
                                .put("lobby_id", lobbyId)
                                .put("client_list", clientList)
                                .put("lobby_max_players", this.lobbyClient.getLobbyMaxPlayers());
                        routingContext.response().putHeader("Content-Type", "application/json")
                                .setStatusCode(200)
                                .send(body.toBuffer());
                    });

                });

        router
                .delete("/client/lobby/clients")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        System.out.println("Client: " + bh.toJsonObject() + "exit from lobby");
                        this.lobbyClient.deleteClient(JSONClient.fromJson(bh.toJsonObject()));
                        ((GUILobby) Launcher.getCurrentGui()).updateClientList(this.lobbyClient.getClientList());
                        ((GUILobby) Launcher.getCurrentGui()).disableStartButton();
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        router
                .put("/client/lobby/manager")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        routingContext.response().setStatusCode(200).end();
                        this.lobbyClient.updateManager(bh.toJsonObject().getString("manager_ip"));
                    });
                });

        router
                .put("/client/lobby/game")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh -> {
                        System.out.println(lobbyClient.getNickname() + ": " + bh.toJsonArray());
                        List<Territory> territory = new ArrayList<>();
                        List<CardType> deck = new ArrayList<>();
                        var arr = bh.toJsonArray();
                        arr.getJsonArray(2).getList().forEach(c -> deck.add(CardType.valueOf(c.toString())));
                        arr.getJsonArray(0).getList().forEach(t -> territory.add(Territory.fromString(t.toString())));
                        System.out.println(lobbyClient.getNickname() + " territories: " + territory);
                        routingContext.response().setStatusCode(200).end();
                        this.lobbyClient.gameStarted(territory, deck, Goal.fromJsonObject(arr.getJsonObject(1)));
                    });
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
                                .putHeader("Content-Type", "application/json").setStatusCode(200)
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
