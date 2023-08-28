package app.lobbySelector;

import io.vertx.core.json.JsonObject;

public class JSONLobby {
    private final String name;
    private final int lobbyId;
    private final String managerClientIp;
    private final int maxPlayers;
    private final int playersInside;

    public JSONLobby(String name, int lobbyId, String managerClientIp, int maxPlayers, int playersInside) {
        this.name = name;
        this.lobbyId = lobbyId;
        this.managerClientIp = managerClientIp;
        this.maxPlayers = maxPlayers;
        this.playersInside = playersInside;
    }

    public static JSONLobby fromJson(JsonObject json) {
        return new JSONLobby(json.getString("name"),
                            json.getInteger("lobby_id"),
                            json.getString("manager_client_ip"),
                            json.getInteger("max_players"),
                            json.getInteger("players_inside"));
    }

    public String getName() {
        return name;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public String getManagerClientIp() {
        return managerClientIp;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersInside() {
        return playersInside;
    }
}
