import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class ClientServerPart extends AbstractVerticle{

    private final Vertx vertx;
    private final HttpServer httpServer;
    private final Router router;

    private LobbyClient lobbyClient;

    public ClientServerPart(LobbyClient lobbyClient){
        this.vertx = Vertx.vertx();
        this.httpServer = vertx.createHttpServer();
        this.router = Router.router(vertx);

        this.lobbyClient = lobbyClient;
    }
    public void start() {
        router
                .post("/client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO add new client info
                    routingContext.response().end();
                });

        router
                .delete("client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO delete a client
                    routingContext.response().end();
                });

        router
                .put("client/lobby/manager")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO update manager client
                    routingContext.response().end();
                });

        router
                .put("client/lobby/game")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO game has started
                    routingContext.response().end();
                });

        router
                .delete("client/lobby")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO lobby got closed
                    routingContext.response().end();
                });

        router
                .get("manager/lobby/clients")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    if(this.lobbyClient instanceof ManagerClient){

                    }

                    //TODO return clients list json
                    routingContext.response().end();
                });

        httpServer.requestHandler(router).listen(8080);
    }

    public void stop(){
        this.httpServer.close();
    }
}
