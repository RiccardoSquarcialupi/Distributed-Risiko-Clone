package app.base;

import io.vertx.core.json.JsonObject;

public interface BaseClient {
    String getIp();

    String getNickname();

    void joinLobby(String managerClientIp);

    JsonObject toJson();
}
