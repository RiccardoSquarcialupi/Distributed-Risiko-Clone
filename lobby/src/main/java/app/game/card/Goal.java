package app.game.card;

import io.vertx.core.json.JsonObject;

public enum Goal implements Card {
    CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH("Conquer 18 territories with 2 armies each"),
    CONQUER_24_TERRITORIES("Conquer 24 territories"),
    CONQUER_NORTH_AMERICA_AND_AFRICA("Conquer North America and Africa"),
    CONQUER_ASIA_AND_SOUTH_AMERICA("Conquer Asia and South America"),
    CONQUER_ASIA_AND_AFRICA("Conquer Asia and Africa"),
    CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT("Conquer Europe and South America and a third continent"),
    CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT("Conquer Europe and Oceania and a third continent"),
    DESTROY_PLAYER_1("Destroy player 1"),
    DESTROY_PLAYER_2("Destroy player 2"),
    DESTROY_PLAYER_3("Destroy player 3"),
    DESTROY_PLAYER_4("Destroy player 4"),
    DESTROY_PLAYER_5("Destroy player 5"),
    DESTROY_PLAYER_6("Destroy player 6"),
    ;

    private final String name;

    Goal(String name) {
        this.name = name;
    }

    public static Goal fromJsonObject(JsonObject jsonObject) {
        return Goal.valueOf(jsonObject.getString("Goal"));
    }
}
