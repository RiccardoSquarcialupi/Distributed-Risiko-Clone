package app.lobbySelector;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class JSONClient {
    private final String ip;
    private final String nickname;

    public JSONClient(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
    }

    public static JSONClient fromJson(JsonObject bodyAsJson) {
        return new JSONClient(bodyAsJson.getString("ip"), bodyAsJson.getString("nickname"));
    }

    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.put("ip", this.ip);
        jo.put("nickname", this.nickname);
        return jo;
    }

    public static JSONClient fromBase(LobbySelectorClient bc) {
        return new JSONClient(bc.getIP(), bc.getNickname());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONClient that = (JSONClient) o;
        return Objects.equals(this.ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ip);
    }

    public String getIP() {
        return this.ip;
    }

    public String getNickname() {
        return this.nickname;
    }
}
