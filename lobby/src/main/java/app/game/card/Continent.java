package app.game.card;

public enum Continent{
    NORTH_AMERICA("North America",5),
    SOUTH_AMERICA("South America",2),
    EUROPE("Europe",5),
    AFRICA("Africa",3),
    ASIA("Asia",7),
    AUSTRALIA("Australia",2);

    private final String name;
    private final int bonus;

    Continent(String name, int bonus) {
        this.name = name;
        this.bonus = bonus;
    }
}
