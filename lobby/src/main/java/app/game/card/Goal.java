package app.game.card;

import io.vertx.core.json.JsonObject;

public enum Goal implements Card {
    CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH,
    CONQUER_24_TERRITORIES,
    CONQUER_NORTH_AMERICA_AND_AFRICA,
    CONQUER_ASIA_AND_SOUTH_AMERICA,
    CONQUER_ASIA_AND_AFRICA,
    CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT,
    CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT,
    DESTROY_PLAYER_1,
    DESTROY_PLAYER_2,
    DESTROY_PLAYER_3,
    DESTROY_PLAYER_4,
    DESTROY_PLAYER_5,
    DESTROY_PLAYER_6,
    ;

    public static Goal fromJsonObject(JsonObject jsonObject) {
        return Goal.valueOf(jsonObject.getString("Goal"));
    }
}
