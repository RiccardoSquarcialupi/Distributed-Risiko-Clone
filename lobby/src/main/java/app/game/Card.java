package app.game;

import java.util.List;

public enum Card {
    ALASKA("Alaska", "North America", List.of("Kamchatka", "Alberta", "Northwest_territory"),5),
    ALBERTA("Alberta", "North America", List.of("Alaska", "Northwest_territory", "Ontario", "Western_United_States"),5),
    CENTRAL_AMERICA("Central America", "North America", List.of("Western_United_States", "Eastern_United_States", "Venezuela"),5),
    EASTERN_UNITED_STATES("Eastern United States", "North America", List.of("Central_America", "Western_United_States", "Ontario", "Quebec"),5),
    GREENLAND("Greenland", "North America", List.of("Northwest_territory", "Ontario", "Quebec", "Iceland"),5),
    NORTHWEST_TERRITORY("Northwest Territory", "North America", List.of("Alaska", "Alberta", "Ontario", "Greenland"),5),
    ONTARIO("Ontario", "North America", List.of("Alberta", "Northwest_territory", "Greenland", "Quebec", "Eastern_United_States", "Western_United_States"),5),
    QUEBEC("Quebec", "North America", List.of("Ontario", "Eastern_United_States", "Greenland"),5),
    WESTERN_UNITED_STATES("Western United States", "North America", List.of("Alberta", "Ontario", "Eastern_United_States", "Central_America"),5),
    ARGENTINA("Argentina", "South America", List.of("Peru", "Brazil"),2),
    BRAZIL("Brazil", "South America", List.of("Venezuela", "Peru", "Argentina", "North_Africa"),2),
    PERU("Peru", "South America", List.of("Venezuela", "Brazil", "Argentina"),2),
    VENEZUELA("Venezuela", "South America", List.of("Central_America", "Brazil", "Peru"),2),
    GREAT_BRITAIN("Great Britain", "Europe", List.of("Iceland", "Scandinavia", "Northern_Europe", "Western_Europe"),5),
    ICELAND("Iceland", "Europe", List.of("Greenland", "Great_Britain", "Scandinavia"),5),
    NORTHERN_EUROPE("Northern Europe", "Europe", List.of("Great_Britain", "Scandinavia", "Ukraine", "Southern_Europe", "Western_Europe"),5),
    SCANDINAVIA("Scandinavia", "Europe", List.of("Iceland", "Great_Britain", "Northern_Europe", "Ukraine"),5),
    SOUTHERN_EUROPE("Southern Europe", "Europe", List.of("Western_Europe", "Northern_Europe", "Ukraine", "Middle_East", "Egypt", "North_Africa"),5),
    UKRAINE("Ukraine", "Europe", List.of("Scandinavia", "Northern_Europe", "Southern_Europe", "Middle_East", "Afghanistan", "Ural"),5),
    WESTERN_EUROPE("Western Europe", "Europe", List.of("Great_Britain", "Northern_Europe", "Southern_Europe", "North_Africa"),5),
    CONGO("Congo", "Africa", List.of("North_Africa", "East_Africa", "South_Africa"),3),
    EAST_AFRICA("East Africa", "Africa", List.of("Egypt", "North_Africa", "Congo", "South_Africa", "Madagascar", "Middle_East"),3),
    EGYPT("Egypt", "Africa", List.of("Southern_Europe", "Middle_East", "East_Africa", "North_Africa"),3),
    MADAGASCAR("Madagascar", "Africa", List.of("South_Africa", "East_Africa"),3),
    NORTH_AFRICA("North Africa", "Africa", List.of("Brazil", "Western_Europe", "Southern_Europe", "Egypt", "East_Africa", "Congo"),3),
    SOUTH_AFRICA("South Africa", "Africa", List.of("Congo", "East_Africa", "Madagascar"),3),
    AFGHANISTAN("Afghanistan", "Asia", List.of("Ukraine", "Ural", "China", "India", "Middle_East"),7),
    CHINA("China", "Asia", List.of("Siam", "India", "Afghanistan", "Ural", "Siberia", "Mongolia"),7),
    INDIA("India", "Asia", List.of("Middle_East", "Afghanistan", "China", "Siam"),7),
    IRKUTSK("Irkutsk", "Asia", List.of("Siberia", "Yakutsk", "Kamchatka", "Mongolia"),7),
    JAPAN("Japan", "Asia", List.of("Kamchatka", "Mongolia"),7),
    KAMCHATKA("Kamchatka", "Asia", List.of("Alaska", "Yakutsk", "Irkutsk", "Japan", "Mongolia"),7),
    MIDDLE_EAST("Middle East", "Asia", List.of("Southern_Europe", "Ukraine", "Afghanistan", "India", "Egypt", "East_Africa"),7),
    MONGOLIA("Mongolia", "Asia", List.of("Siberia", "Irkutsk", "Kamchatka", "Japan", "China"),7),
    SIAM("Siam", "Asia", List.of("India", "China", "Indonesia"),7),
    SIBERIA("Siberia", "Asia", List.of("Ural", "Yakutsk", "Irkutsk", "Mongolia", "China"),7),
    URAL("Ural", "Asia", List.of("Ukraine", "Siberia", "China", "Afghanistan"),7),
    YAKUTSK("Yakutsk", "Asia", List.of("Siberia", "Irkutsk", "Kamchatka"),7),
    EASTERN_AUSTRALIA("Eastern Australia", "Oceania", List.of("Western_Australia", "New_Guinea"),2),
    INDONESIA("Indonesia", "Oceania", List.of("Siam", "New_Guinea", "Western_Australia"),2),
    NEW_GUINEA("New Guinea", "Oceania", List.of("Indonesia", "Eastern_Australia", "Western_Australia"),2),
    WESTERN_AUSTRALIA("Western Australia", "Oceania", List.of("Indonesia", "New_Guinea", "Eastern_Australia"),2),
    WILD1("Wild1", "", List.of(),0),
    WILD2("Wild2", "", List.of(),0);

    private final String name;
    private final String continent;
    private final List<String> neighbours;
    private final CardType cardType;
    private final int bonus;

    Card(String name, String continent, List<String> neighbours, int bonus) {
        this.name = name;
        this.continent = continent;
        this.neighbours = neighbours;
        this.bonus = bonus;
        this.cardType = this.name.contains("1") ? CardType.WILD : this.name.contains("2") ? CardType.WILD : Math.random() < 0.33 ? CardType.INFANTRY : Math.random() < 0.66 ? CardType.CAVALRY : CardType.ARTILLERY;
    }

    public String getName() {
        return name;
    }

    public String getContinent() {
        return continent;
    }

    public List<String> getNeighbours() {
        return neighbours;
    }

    public int getBonus() {
        return bonus;
    }

    public CardType getCardType() {
        return cardType;
    }
}
