import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ClientServerPart extends AbstractVerticle {
    private final HttpServer httpServer;
    private final Router router;

    private final LobbyClient lobbyClient;

    public ClientServerPart(LobbyClient lobbyClient) {
        this.httpServer = Vertx.vertx().createHttpServer();
        this.lobbyClient = lobbyClient;
        this.router = Router.router(vertx);
    }

    public void start() {
        router
                .post("/client/lobby/clients")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh->{
                        this.lobbyClient.addNewClient(BaseClient.fromJson(bh.toJsonObject()));
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        router
                .delete("/client/lobby/clients")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh-> {
                        this.lobbyClient.deleteClient(BaseClient.fromJson(bh.toJsonObject()));
                        routingContext.response().setStatusCode(200).end();
                    });
                });

        router
                .put("/client/lobby/manager")
                .handler(routingContext -> {
                    routingContext.request().bodyHandler(bh-> {
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
                    if (this.lobbyClient instanceof ManagerClient) {
                        routingContext.response()
                                .putHeader("Content-Type", "application/json")
                                .end(JsonObject.mapFrom(((ManagerClient) this.lobbyClient).getClientList()).encode());

                    }
                    routingContext.response().end();
                });

        httpServer.requestHandler(router).listen(8080);
    }

    public void stop() {
        this.httpServer.close();
    }
}
