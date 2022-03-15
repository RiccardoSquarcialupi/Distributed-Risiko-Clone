import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

public class ClientWebClientPart extends AbstractVerticle {
    private final WebClient client;

    public ClientWebClientPart() {
        this.client = WebClient.create(Vertx.vertx());
    }

    public void joinLobby(JsonObject body) {
        this.client
                .post(8080, "127.0.0.1", "/client/lobby/clients")
                .sendJsonObject(body)
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

    public void exitLobby() {
        this.client
                .delete(8080, "127.0.0.1", "/client/lobby/game")
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));
    }

    public void managerClientChange(JsonObject body, List<BaseClient> clientList) {
        clientList.forEach(c -> this.client
                .put(8080, c.getIp(), "/client/lobby/clients")
                .sendJsonObject(body)
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage())));

    }

    public void gameHasStarted(JsonObject body) {
        this.client
                .put(8080, "127.0.0.1", "/client/lobby/clients")
                //TODO need to pass also card for each player
                .sendJsonObject(body.put("msg","Game has Started"))
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

    public void lobbyClosed() {
        this.client
                .delete(8080, "127.0.0.1", "/client/lobby")
                //TODO need to pass also card for each player
                .sendJsonObject(JsonObject.mapFrom("Lobby is closed"))
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

    public void getClientInfo() {
        this.client
                .get(8080, "127.0.0.1", "/manager/lobby/clients")
                //TODO need to pass also card for each player
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

}
