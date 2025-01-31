package app.lobbySelector;

import app.Launcher;
import app.login.LoginClient;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LobbySelectorClientImpl extends LoginClient implements LobbySelectorClient {
    private final static int FLASK_SERVER_PORT = 5000;
    private final WebClient client;
    private final ContextManagerParameters cltPar;
    private final int serverPort = 5001;

    public LobbySelectorClientImpl(ContextManagerParameters cltPar) {
        super(cltPar);
        this.client = WebClient.create(Launcher.getVertx());
        this.cltPar = cltPar;
    }

    @Override
    public String getNickname() {
        return this.cltPar.getNickname();
    }

    @Override
    public Future<String> joinLobby(String managerClientIp) {
        List<Promise<Void>> prm = List.of(Promise.promise(), Promise.promise());
        this.client
                .post(serverPort, managerClientIp, "/client/lobby/clients")
                .sendJsonObject(JSONClient.fromBase(this).toJson())
                .onSuccess(r -> {
                    switch (r.statusCode()){
                        case 200:
                            System.out.println("Manager Client receive the joinLobby Notification");
                            prm.get(1).complete();
                            break;
                        case 500:
                            System.out.println("Manager Client is down");
                            prm.get(1).fail("Manager Client is down");
                            break;
                        case 405:
                            System.out.println("Manager Client response: lobby is full");
                            prm.get(1).fail("Manager Client response: lobby is full");
                            break;
                        default:
                            System.out.println("Something went wrong when sending joinLobby Notification to the server");
                            prm.get(1).fail("Something went wrong when sending joinLobby Notification to the server");
                            break;
                    }

                    prm.get(1).future().onSuccess(g -> {
                        this.cltPar.setIpManager(managerClientIp);

                        var lobbyId = r.bodyAsJsonObject().getInteger("lobby_id");
                        this.cltPar.setIdLobby(lobbyId);

                        var lobbyMaxPlayers = r.bodyAsJsonObject().getInteger("lobby_max_players");
                        this.cltPar.setMaxPlayer(lobbyMaxPlayers);

                        JsonArray clientList = r.bodyAsJsonObject().getJsonArray("client_list");
                        //Print EACH client
                        clientList.forEach(c -> this.cltPar.addClient(JSONClient.fromJson((JsonObject) c)));
                        this.cltPar.getClientList().forEach(c -> System.out.println("Client: " + c.getIP() + " - " + c.getNickname() + " is on the List of " + Launcher.getCurrentClient().getIP()));

                        //INFORM THE FLASK SERVER
                        this.client
                                .put(FLASK_SERVER_PORT, Launcher.serverIP, "server/lobby/" + lobbyId)
                                .send()
                                .onSuccess(s -> {
                                    switch(s.statusCode()){
                                        case 200:
                                            System.out.println("FLASK Server receive the join joinLobby Notification");
                                            prm.get(0).complete();
                                            break;
                                        case 500:
                                            System.out.println("FLASK Server Down");
                                            prm.get(0).fail("FLASK Server Down");
                                            break;
                                        case 404:
                                            System.out.println("FLASK Server r: Server Not Found");
                                            prm.get(0).fail("FLASK Server r: Server Not Found");
                                            break;
                                        case 403:
                                            System.out.println("FLASK Server response: Lobby id is not a number");
                                            prm.get(0).fail("FLASK Server response: Lobby id is not number");
                                            break;
                                        default:
                                            System.out.println("Something went wrong when sending joinLobby Notification to the FLASK server");
                                            prm.get(0).fail("Something went wrong when sending joinLobby Notification to the FLASK server");
                                    }
                                })
                                .onFailure(err -> System.out.println("Something went wrong when sending joinLobby Notification to the FLASK server" + err.getMessage()));
                    });
                })
                .onFailure(prm.get(1)::fail);
        Promise<String> ret = Promise.promise();
        Future.all(prm.stream().map(Promise::future).collect(Collectors.toList())).onSuccess(s -> ret.complete(managerClientIp)).onFailure(err -> {
            System.out.println("Error with joinLobby, Promise not completed" + err.getMessage());
            ret.fail(err);
        });
        return ret.future();
    }

    @Override
    public Future<HttpResponse<Buffer>> getLobbies() {
        return this.client.get(FLASK_SERVER_PORT, Launcher.serverIP, "/server/lobbies/").send().onSuccess(s -> {
            switch(s.statusCode()){
                case 200:
                    System.out.println("FLASK Server receive the getLobbies Notification");
                    break;
                case 500:
                    System.out.println("FLASK Server Down");
                    break;
                default:
                    System.out.println("Something went wrong when sending getLobbies Notification to the FLASK server");
            }
        }).onFailure(err -> System.out.println("Something went wrong when sending getLobbies to the FLASK server" + err.getMessage()));
    }

    @Override
    public Future<Void> createNewLobby(String name, int maxPlayers) {
        Promise<Void> prm = Promise.promise();
        this.client
                .post(FLASK_SERVER_PORT, Launcher.serverIP, "/server/lobbies")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(new JsonObject(Map.of("name", name, "max_players", maxPlayers)))
                .onSuccess(s -> {
                    switch(s.statusCode()){
                        case 200:
                            System.out.println("FLASK Server receive the join joinLobby Notification");
                            this.cltPar.setIpManager(this.cltPar.getIp());
                            this.cltPar.setMaxPlayer(maxPlayers);
                            this.cltPar.addClient(JSONClient.fromBase(this));
                            this.cltPar.setIdLobby(Integer.parseInt(s.bodyAsString().trim()));
                            prm.complete();
                            break;
                        case 500:
                            System.out.println("FLASK Server Down");
                            prm.fail("FLASK Server Down");
                            break;
                        case 400:
                            System.out.println("FLASK Server response: Incongruous lobby provided");
                            prm.fail("FLASK Server response: Incongruous lobby provided");
                            break;
                        default:
                            System.out.println("Something went wrong when sending createNewLobby Notification to the FLASK server");
                            prm.fail("Something went wrong when sending createNewLobby Notification to the FLASK server");
                    }
                })
                .onFailure(prm::fail);
        return prm.future();
    }


}
