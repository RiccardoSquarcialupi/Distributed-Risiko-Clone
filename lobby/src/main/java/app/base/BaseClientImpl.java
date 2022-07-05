package app.base;

import app.login.LoginClient;
import app.manager.client.Client;
import app.manager.client.ClientParameters;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.Launcher;
import jdk.jshell.spi.ExecutionControl;

public class BaseClientImpl extends LoginClient implements BaseClient {
    private WebClient client;
    private ClientParameters cltPar;

    public BaseClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.client = WebClient.create(Vertx.vertx());
        this.cltPar = cltPar;
    }

    @Override
    public String getNickname(){return this.cltPar.getNickname();}

    @Override
    public void joinLobby(String managerClientIp) {
        // TODO: Missing server communication to inform.
        // TODO: Missing manager communication to get clients.
        this.client
                .post(8080, managerClientIp, "/client/lobby/clients")
                .sendJsonObject(JSONClient.fromBase(this).toJson())
                .onSuccess(response -> {
                    this.cltPar.setIpManager(managerClientIp);
                    Launcher.lobbyJoinedSuccessfully();
                })
                .onFailure(System.out::println);
    }

    @Override
    public Future<HttpResponse<Buffer>> getFilteredLobbies(int maxPlayers) {
        return this.client
                .get(8080, Launcher.serverIP, "/server/lobbies/" + maxPlayers)
                .send();
    }

    @Override
    public void createNewLobby(String name, int maxPlayers) {
        this.client
                .post(8080, Launcher.serverIP, "/server/lobbies")
                .sendJsonObject(new JsonObject(Map.of("name", name, "max_players", maxPlayers)))
                .onSuccess(response -> {
                    this.cltPar.setIpManager(this.cltPar.getIp());
                    this.cltPar.setMaxPlayer(maxPlayers);
                    this.cltPar.addClient(JSONClient.fromBase(this));
                    // TODO: lobby id.
                    Launcher.lobbyCreatedSuccessfully();
                })
                .onFailure(System.out::println);
    }


}
