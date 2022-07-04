package app.base;

import app.manager.client.Client;
import io.vertx.core.json.JsonObject;

public interface BaseClient extends Client {
    String getNickname();

    void joinLobby(String managerClientIp);

    JsonObject toJson();
}
