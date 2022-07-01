package app.base;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.Objects;

import app.Launcher;

public class BaseClient {
    private final String ip;
    private final String nickname;
    private WebClient client;

    public BaseClient(String ip, String nickname) {
        this.client = WebClient.create(Vertx.vertx());
        this.ip = ip;
        this.nickname = nickname;
    }

    public String getIp(){return this.ip;}
    public String getNickname(){return this.nickname;}

    public void joinLobby(BaseClient client, String managerClientIp) {
        this.client
                .post(8080, managerClientIp, "/client/lobby/clients")
                .sendJsonObject(client.toJson())
                .onSuccess(response -> {
                    System.out.println("Received response with status code" + response.statusCode());
                    Launcher.lobbyJoinedSuccessfully(managerClientIp);
                })
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

    public JsonObject toJson(){
        JsonObject jo = new JsonObject();
        jo.put("ip", ip);
        jo.put("nickname", nickname);
        return jo;
    }

    public static BaseClient fromJson(JsonObject bodyAsJson) {
        return new BaseClient(bodyAsJson.getString("ip"),bodyAsJson.getString("nickname"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseClient that = (BaseClient) o;
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }
}
