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
                    this.lobbyClient.addNewClient(BaseClient.fromJson(routingContext.getBodyAsJson()));
                    routingContext.response().end();
                });

        router
                .delete("/client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    this.lobbyClient.deleteClient(BaseClient.fromJson(routingContext.getBodyAsJson()));
                    routingContext.response().end();
                });

        router
                .put("/client/lobby/manager")
                .consumes("*/json")
                .handler(routingContext -> {
                    this.lobbyClient.updateManager(routingContext.getBodyAsJson().getString("manager_ip"));
                    routingContext.response().end();
                });

        router
                .put("/client/lobby/game")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    this.lobbyClient.gameStarted();
                    routingContext.response().end();
                });

        router
                .delete("/client/lobby")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    this.lobbyClient.lobbyClosed();
                    routingContext.response().end();
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
