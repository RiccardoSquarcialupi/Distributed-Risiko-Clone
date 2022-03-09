import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class ClientServerPart extends AbstractVerticle{
    private final HttpServer httpServer;
    private final Router router;

    private LobbyClient lobbyClient;

    public ClientServerPart(LobbyClient lobbyClient){
        this.httpServer = vertx.createHttpServer();
        this.router = Router.router(vertx);

        this.lobbyClient = lobbyClient;
    }
    public void start() {
        router
                .post("/client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    this.lobbyClient.addNewClient(routingContext.getBodyAsJson().mapTo(BaseClient.class));
                    routingContext.response().end();
                });

        router
                .delete("client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    this.lobbyClient.deleteClient(routingContext.getBodyAsJson().mapTo(BaseClient.class));
                    routingContext.response().end();
                });

        router
                .put("client/lobby/manager")
                .consumes("*/json")
                .handler(routingContext -> {
                    this.lobbyClient.updateManager(routingContext.getBodyAsJson().getInteger("manager_id"));
                    routingContext.response().end();
                });

        router
                .put("client/lobby/game")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    this.lobbyClient.gameStarted();
                    routingContext.response().end();
                });

        router
                .delete("client/lobby")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    this.lobbyClient.lobbyClosed();
                    routingContext.response().end();
                });

        router
                .get("manager/lobby/clients")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    if(this.lobbyClient instanceof ManagerClient){
                        ((ManagerClient)this.lobbyClient).getClientList();
                    }
                    routingContext.response().end();
                });

        httpServer.requestHandler(router).listen(8080);
    }

    public void stop(){
        this.httpServer.close();
    }
}
