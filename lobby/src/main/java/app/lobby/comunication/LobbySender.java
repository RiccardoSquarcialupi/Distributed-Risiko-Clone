package app.lobby.comunication;

import app.lobbySelector.JSONClient;
import app.lobbySelector.JSONLobby;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import app.Launcher;

import java.util.List;

public class LobbySender extends AbstractVerticle {
    private final WebClient client;

    public LobbySender() {
        this.client = WebClient.create(Launcher.getVertx());
    }

    public void exitLobby(JSONClient client,int lobbyId) {
        this.client
                .delete(5001, "127.0.0.1", "/client/lobby/clients")
                .sendJson(client.toJson())
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));
        this.client
                .delete(5000,"127.0.0.1","/server/lobby/"+ lobbyId +"/numberOfPlayer").send();
    }

    public void managerClientChange(JsonObject body, List<JSONClient> clientList) {
        clientList.forEach(c -> this.client
                .put(5001, c.getIP(), "/client/lobby/manager")
                .sendJsonObject(body)
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage())));

    }

    public void gameHasStarted(JsonObject body, String ip) {
        this.client
                .put(5001, ip, "/client/lobby/game")
                .sendJsonObject(body.put("msg", "Game has Started"))
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }


    public void lobbyClosed(int lobby) {
        //send a message to all the clients in the lobby
        // BUT if there is a client we proceed with "MANAGER CLIENT CHANGE" so it will never works!!!
        /*this.client
                .delete(5001, "127.0.0.1", "/client/lobby")
                .sendJsonObject(new JsonObject("{\"msg\":\"Lobby is closed!\"}"))
                .onSuccess(response -> {
                        System.out.println("Received response with status code" + response.statusCode());
                        Launcher.lobbyClosed();
                })
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));*/
        //INSTEAD we just remove the lobby from the server
        this.client.delete(5000, "127.0.0.1","/server/lobby/"+lobby).send();

    }

    public void getClientInfo() {
        this.client
                .get(5001, "127.0.0.1", "/manager/lobby/clients")
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

}
