package app.lobby.comunication;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.lobbySelector.JSONClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class LobbySender extends AbstractVerticle {
    private final WebClient client;

    public LobbySender() {
        this.client = WebClient.create(Launcher.getVertx());
    }

    public void broadcast() {
        var list = ((LobbyClientImpl) Launcher.getCurrentClient()).getClientList()
                .stream()
                .filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP()))
                .filter(c -> !c.getIP().equals(((LobbyClientImpl) Launcher.getCurrentClient()).getIpManager())).collect(Collectors.toList());

        list.forEach(c -> {
            this.client
                    .post(5001, c.getIP(), "/client/lobby/clients")
                    .sendJsonObject(JSONClient.fromBase((LobbyClientImpl) Launcher.getCurrentClient()).toJson())
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                c.getNickname() +
                                " receive the info about me: " + Launcher.getCurrentClient().getIP() + " , " + response.statusCode());
                    })
                    .onFailure(err ->
                            System.out.println("Client " + c.getNickname() + " doesn't receive the info about me: " + err.getMessage()));
        });
    }

    public Future<Void> exitLobby(JSONClient client, int lobbyId, List<JSONClient> clientList) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = clientList.stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());

        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .delete(5001, finalClientList.get(i).getIP(), "/client/lobby/clients")
                    .sendJson(client.toJson())
                    .onSuccess(response ->
                    {
                        System.out.println("Client " +
                                finalClientList.get(index).getIP() +
                                " receive the info about my exit, " + response.statusCode());
                        lpv.get(index).complete();

                    })
                    .onFailure(err ->
                            System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't respond!Maybe Try again." +
                                    err.getMessage()));
        }
        this.client
                .delete(5000, Launcher.serverIP, "/server/lobby/" + lobbyId + "/numberOfPlayer")
                .send()
                .onSuccess(r -> {
                    CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
                });
        return prm.future();
    }

    public Future<Void> managerClientChange(JsonObject body, List<JSONClient> clientList, int lobbyId) {
        Promise<Void> prm = Promise.promise();
        var finalClientList = clientList.stream().filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP())).collect(Collectors.toList());
        List<Promise> lpv = finalClientList.stream().map(c -> Promise.promise()).collect(Collectors.toList());

        for (int i = 0; i < finalClientList.size(); i++) {
            final int index = i;
            this.client
                    .put(5001, finalClientList.get(i).getIP(), "/client/lobby/manager")
                    .sendJsonObject(body)
                    .onSuccess(response -> {
                        System.out.println("Client " +
                                finalClientList.get(index).getNickname() +
                                " receive the new manager ip, " + response.statusCode());
                        lpv.get(index).complete();
                    })
                    .onFailure(err ->
                            System.out.println("Client " + finalClientList.get(index).getNickname() + "doesn't receive the new manager ip: " + err.getMessage()));

        }

        this.client.put(5000, Launcher.serverIP, "/server/lobby/" + lobbyId + "/managerClientIp")
                .sendJsonObject(new JsonObject().put("new_manager_client_ip", body.getString("manager_ip")))
                .onSuccess(response -> {
                    System.out
                            .println("Main Server receive the new manager ip, " + response.statusCode());
                    CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete());
                })
                .onFailure(err ->
                        System.out.println("Main Server doesn't receive the new manager ip, " + err.getMessage()));

        return prm.future();
    }

    public Future<Void> gameHasStarted(JsonArray body, String ip) {
        Promise<Void> prm = Promise.promise();
        this.client
                .put(5001, ip, "/client/lobby/game")
                .sendJson(body)
                .onSuccess(response -> {
                    System.out
                            .println("Client " + ip + "receive the msg GAME HAS STARTED" + response.statusCode());
                    prm.complete();
                })
                .onFailure(err ->
                        System.out.println("Client " + ip + "DOESN'T receive the msg GAME HAS STARTED" + err.getMessage()));
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
        this.client.delete(5000, Launcher.serverIP, "/server/lobby/" + lobby)
                .send()
                .onSuccess(s -> {
                    System.out.println("Server receive the msg: Lobby " + lobby + " is closed");
                    prm.complete();
                });
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
