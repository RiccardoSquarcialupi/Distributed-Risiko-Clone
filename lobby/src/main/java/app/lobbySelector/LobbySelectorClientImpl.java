package app.lobbySelector;

import app.Launcher;
import app.login.LoginClient;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

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
    public void joinLobby(String managerClientIp) {
        this.client
                .post(serverPort, managerClientIp, "/client/lobby/clients")
                .sendJsonObject(JSONClient.fromBase(this).toJson())
                .onSuccess(response -> {
                    this.cltPar.setIpManager(managerClientIp);

                    var lobbyId = response.bodyAsJsonObject().getInteger("lobby_id");
                    this.cltPar.setIdLobby(lobbyId);

                    var lobbyMaxPlayers = response.bodyAsJsonObject().getInteger("lobby_max_players");
                    this.cltPar.setMaxPlayer(lobbyMaxPlayers);

                    JsonArray clientList = response.bodyAsJsonObject().getJsonArray("client_list");
                    //Print EACH client
                    clientList.forEach(c -> this.cltPar.addClient(JSONClient.fromJson((JsonObject) c)));
                    this.cltPar.getClientList().forEach(c -> System.out.println("Client: " + c.getIP() + " - " + c.getNickname() + " is on the List of " + Launcher.getCurrentClient().getIP()));

                    //INFORM THE FLASK SERVER
                    this.client
                            .put(FLASK_SERVER_PORT, Launcher.serverIP, "server/lobby/" + lobbyId)
                            .send()
                            .onComplete(s -> System.out.println("FLASK Server receive the join joinLobby Notification"))
                            .onFailure(err -> System.out.println("Something went wrong when sending joinLobby Notification to the FLASK server" + err.getMessage()));
                    Launcher.lobbyJoinedSuccessfully();
                })
                .onFailure(System.out::println);
    }

    @Override
    public Future<HttpResponse<Buffer>> getFilteredLobbies(int maxPlayers) {
        return this.client.get(FLASK_SERVER_PORT, Launcher.serverIP, "/server/lobbies/").send();

    }

    @Override
    public void createNewLobby(String name, int maxPlayers) {
        this.client
                .post(FLASK_SERVER_PORT, Launcher.serverIP, "/server/lobbies")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(new JsonObject(Map.of("name", name, "max_players", maxPlayers)))
                .onSuccess(response -> {
                    this.cltPar.setIpManager(this.cltPar.getIp());
                    this.cltPar.setMaxPlayer(maxPlayers);
                    this.cltPar.addClient(JSONClient.fromBase(this));
                    this.cltPar.setIdLobby(Integer.parseInt(response.bodyAsString().trim()));
                    Launcher.lobbyCreatedSuccessfully();
                })
                .onFailure(System.out::println);
    }


}
