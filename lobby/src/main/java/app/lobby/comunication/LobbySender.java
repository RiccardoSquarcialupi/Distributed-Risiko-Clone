package app.lobby.comunication;

import app.lobbySelector.JSONClient;
import app.lobbySelector.JSONLobby;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import app.Launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LobbySender extends AbstractVerticle {
    private final WebClient client;

    public LobbySender() {
        this.client = WebClient.create(Launcher.getVertx());
    }

    public Future<Void> exitLobby(JSONClient client, int lobbyId, String managerIp) {
        Promise<Void> prm = Promise.promise();
        this.client
                .delete(5001, managerIp, "/client/lobby/clients")
                .sendJson(client.toJson())
                .onSuccess(response ->
                {
                    System.out.println("Received response with status code: " + response.statusCode());
                    this.client
                            .delete(5000,Launcher.serverIP,"/server/lobby/"+ lobbyId +"/numberOfPlayer")
                            .send()
                            .onSuccess(r -> {
                                prm.complete();
                            });
                })
                .onFailure(err ->
                        System.out.println("Something went wrong with " +
                                managerIp + " while exiting: " +
                                err.getMessage()));
        return prm.future();
    }

    public Future<Void> managerClientChange(JsonObject body, List<JSONClient> clientList,int lobbyId){
        Promise<Void> prm = Promise.promise();

        List<Promise> lpv = new ArrayList<>(clientList.stream().map(c -> Promise.promise()).collect(Collectors.toList()));
        for(int i = 0; i < clientList.size(); i++){
            final int index = i;
            this.client
                .put(5001, clientList.get(i).getIP(), "/client/lobby/manager")
                .sendJsonObject(body)
                .onSuccess(response -> {
                    System.out
                            .println("Received response from " +
                                    clientList.get(index).getNickname() +
                                    " with status code: " + response.statusCode());
                    lpv.get(index).complete();
                })
                .onFailure(err ->
                        System.out.println("Something went wrong with client " +
                                clientList.get(index).getNickname() + ": " +
                                err.getMessage()));
        }

        this.client.put(5000,Launcher.serverIP, "/server/lobby/"+lobbyId+"/managerClientIp")
                .sendJsonObject(new JsonObject().put("new_manager_client_ip",body.getString("manager_ip")))
                .onSuccess(response -> {
                    System.out
                            .println("Received response from server with status code: " + response.statusCode());
                    CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
                })
                .onFailure(err ->
                        System.out.println("Something went wrong with server: " + err.getMessage()));
        return prm.future();
    }

    public Future<Void> gameHasStarted(JsonObject body, String ip) {
        Promise<Void> prm = Promise.promise();
        this.client
                .put(5001, ip, "/client/lobby/game")
                .sendJsonObject(body.put("msg", "Game has Started"))
                .onSuccess(response -> {
                    System.out
                            .println("Received response with status code" + response.statusCode());
                    prm.complete();
                })
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));
        return prm.future();
    }


    public Future<Void> lobbyClosed(int lobby) {
        Promise<Void> prm = Promise.promise();
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
        this.client.delete(5000, Launcher.serverIP,"/server/lobby/"+lobby)
                    .send()
                    .onSuccess(s -> prm.complete());
        return prm.future();
    }

    public Future<Void> getClientInfo(JSONClient client) {
        Promise<Void> prm = Promise.promise();
        this.client
                .get(5001, client.getIP(), "/manager/lobby/clients")
                .send()
                .onSuccess(response -> {
                    System.out
                            .println("Received response with status code: " + response.statusCode());
                    prm.complete();
                })
                .onFailure(err ->
                        System.out.println("Something went wrong with " +
                                client.getNickname() + " info: " +
                                err.getMessage()));
        return prm.future();
    }

}
