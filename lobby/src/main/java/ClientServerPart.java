import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class ClientServerPart extends AbstractVerticle{
    public static void main(final String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router
                .post("/client/lobby/clients")
                .consumes("*/json")
                .handler(routingContext -> {
                    routingContext.getBodyAsJson();
                    //TODO add new client info
                    HttpServerResponse response = routingContext.response();
                    response.end();
                });

        httpServer.requestHandler(router).listen(8080);
    }


}
