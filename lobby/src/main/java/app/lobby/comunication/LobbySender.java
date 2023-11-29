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

    public Future<Void> broadcastClientIp() {
        Promise<Void> prm = Promise.promise();
        var list = ((LobbyClientImpl) Launcher.getCurrentClient()).getClientList()
                .stream()
                .filter(c -> !c.getIP().equals(Launcher.getCurrentClient().getIP()))
                .filter(c -> !c.getIP().equals(((LobbyClientImpl) Launcher.getCurrentClient()).getIpManager())).collect(Collectors.toList());
        List<Promise> lpv = list.stream().map(c -> Promise.promise()).collect(Collectors.toList());

        list.forEach(c -> {
            this.client
                    .post(5001, c.getIP(), "/client/lobby/clients")
                    .sendJsonObject(JSONClient.fromBase((LobbyClientImpl) Launcher.getCurrentClient()).toJson())
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        c.getNickname() +
                                        " receive the info about me: " + Launcher.getCurrentClient().getIP() + " , " + r.statusCode());
                                lpv.get(list.indexOf(c)).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        c.getNickname() +
                                        " is down");
                                lpv.get(list.indexOf(c)).fail("Client " +
                                        c.getNickname() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending info about me to the server");
                                lpv.get(list.indexOf(c)).fail("Client " +
                                        c.getNickname() +
                                        " is down");
                                break;
                        }
                        System.out.println("Client " +
                                c.getNickname() +
                                " receive the info about me: " + Launcher.getCurrentClient().getIP() + " , " + r.statusCode());
                    })
                    .onFailure(err -> {
                        System.out.println("Client " + c.getNickname() + " doesn't receive the info about me: " + err.getMessage());
                        lpv.get(list.indexOf(c)).fail("Client " + c.getNickname() + " doesn't receive the info about me: " + err.getMessage());
                    });
        });
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> prm.complete()).onFailure(prm::fail);
        return prm.future();
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
                    .onSuccess(r ->
                    {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getIP() +
                                        " receive the info about my exit, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getIP() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getIP() +
                                        " is down");
                                break;
                            default:
                                System.out.println("Something went wrong when sending info about my exit to the server");
                                lpv.get(index).fail("Something went wrong when sending info about my exit to the server");
                                break;
                        }
                    })
                    .onFailure(err -> {
                        System.out.println("Client ip: " + finalClientList.get(index).getIP() + " doesn't respond!Maybe Try again." +
                                err.getMessage());
                        lpv.get(index).fail(err.getMessage());
                    });

        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            this.client
                    .delete(5000, Launcher.serverIP, "/server/lobby/" + lobbyId + "/numberOfPlayer")
                    .send()
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Server receive the info about my exit, " + r.statusCode());
                                prm.complete();
                                break;
                            case 500:
                                System.out.println("Server Down");
                                prm.fail("Server Down");
                                break;
                            case 404:
                                System.out.println("Server response: Lobby Not Found");
                                prm.fail("Server response: Lobby Not Found");
                                break;
                            case 403:
                                System.out.println("Server response: Lobby id is not number");
                                prm.fail("Server response: Lobby id is not a number");
                                break;
                            default:
                                System.out.println("Something went wrong when sending info about my exit to the server");
                                prm.fail("Something went wrong when sending info about my exit to the server");
                        }
                    }).onFailure(prm::fail);
        }).onFailure(prm::fail);
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
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " receive the new manager ip, " + r.statusCode());
                                lpv.get(index).complete();
                                break;
                            case 500:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " is down");
                                break;
                            case 404:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " response: Lobby Not Found");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " response: Lobby Not Found");
                                break;
                            case 400:
                                System.out.println("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " response: New manager not set");
                                lpv.get(index).fail("Client " +
                                        finalClientList.get(index).getNickname() +
                                        " response: New manager not set");
                                break;
                            default:
                                System.out.println("Something went wrong when sending new manager ip to the server");
                                lpv.get(index).fail("Something went wrong when sending new manager ip to the server");
                                break;
                        }
                    })
                    .onFailure(err ->
                            System.out.println("Client " + finalClientList.get(index).getNickname() + "doesn't receive the new manager ip: " + err.getMessage()));

        }
        CompositeFuture.all(lpv.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> {
            this.client.put(5000, Launcher.serverIP, "/server/lobby/" + lobbyId + "/managerClientIp")
                    .sendJsonObject(new JsonObject().put("new_manager_client_ip", body.getString("manager_ip")))
                    .onSuccess(r -> {
                        switch (r.statusCode()) {
                            case 200:
                                System.out.println("Server receive the new manager ip, " + r.statusCode());
                                prm.complete();
                                break;
                            case 500:
                                System.out.println("Server Down");
                                prm.fail("Server Down");
                                break;
                            case 404:
                                System.out.println("Server response: Lobby Not Found");
                                prm.fail("Server response: Lobby Not Found");
                                break;
                            default:
                                System.out.println("Something went wrong when sending new manager ip to the server");
                                prm.fail("Something went wrong when sending new manager ip to the server");
                        }
                    })
                    .onFailure(prm::fail);
        }).onFailure(prm::fail);
        return prm.future();
    }

    public Future<Void> gameHasStarted(JsonArray body, String ip) {
        Promise<Void> prm = Promise.promise();
        this.client
                .put(5001, ip, "/client/lobby/game")
                .sendJson(body)
                .onSuccess(r -> {
                    switch (r.statusCode()) {
                        case 200:
                            System.out.println("Client " + ip + "receive the msg GAME HAS STARTED" + r.statusCode());
                            prm.complete();
                            break;
                        case 500:
                            System.out.println("Client " + ip + "is down");
                            prm.fail("Client " + ip + "is down");
                            break;
                        default:
                            System.out.println("Something went wrong when sending info about my exit to the server");
                            prm.fail("Something went wrong when sending info about my exit to the server");
                            break;
                    }
                })
                .onFailure(err -> {
                    System.out.println("Client " + ip + "DOESN'T receive the msg GAME HAS STARTED" + err.getMessage());
                    prm.fail("Client " + ip + "DOESN'T receive the msg GAME HAS STARTED" + err.getMessage());
                });
        return prm.future();
    }


    public Future<Void> lobbyClosed(int lobbyId) {
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
        this.client.delete(5000, Launcher.serverIP, "/server/lobby/" + lobbyId)
                .send()
                .onSuccess(s -> {
                    switch (s.statusCode()) {
                        case 200:
                            System.out.println("Server receive the msg: Lobby " + lobbyId + " is closed");
                            prm.complete();
                            break;
                        case 500:
                            System.out.println("Server Down");
                            prm.fail("Server Down");
                            break;
                        case 404:
                            System.out.println("Server response: Lobby Not Found");
                            prm.fail("Server response: Lobby Not Found");
                            break;
                        case 403:
                            System.out.println("Server response: Lobby id is not number");
                            prm.fail("Server response: Lobby id is not a number");
                            break;
                        default:
                            System.out.println("Something went wrong when sending info about my exit to the server");
                            prm.fail("Something went wrong when sending info about my exit to the server");
                    }
                }).onFailure(prm::fail);
        return prm.future();
    }
}
