package app.base;

import app.login.LoginClient;
import app.manager.client.Client;
import app.manager.client.ClientParameters;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.Objects;

import app.Launcher;

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
        this.client
                .post(8080, managerClientIp, "/client/lobby/clients")
                .sendJsonObject(this.toJson())
                .onSuccess(response -> {
                    System.out.println("Received response with status code" + response.statusCode());
                    this.cltPar.setIpManager(managerClientIp);
                    Launcher.lobbyJoinedSuccessfully();
                })
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

    @Override
    public JsonObject toJson(){
        JsonObject jo = new JsonObject();
        jo.put("ip", getIP());
        jo.put("nickname", this.cltPar.getNickname());
        return jo;
    }
}
