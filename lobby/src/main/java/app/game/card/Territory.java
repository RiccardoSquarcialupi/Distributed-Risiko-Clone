package app.game.card;

import java.util.List;
import org.apache.commons.text.similarity.LevenshteinDistance;

public enum Territory implements Card {
    ALASKA("Alaska", Continent.NORTH_AMERICA, List.of("Kamchatka", "Alberta", "Northwest territory")),
    ALBERTA("Alberta", Continent.NORTH_AMERICA, List.of("Alaska", "Northwest territory", "Ontario", "Western United States")),
    CENTRAL_AMERICA("Central America", Continent.NORTH_AMERICA, List.of("Western United States", "Eastern United States", "Venezuela")),
    EASTERN_UNITED_STATES("Eastern United States", Continent.NORTH_AMERICA, List.of("Central America", "Western United_States", "Ontario", "Quebec")),
    GREENLAND("Greenland", Continent.NORTH_AMERICA, List.of("Northwest territory", "Ontario", "Quebec", "Iceland")),
    NORTHWEST_TERRITORY("Northwest territory", Continent.NORTH_AMERICA, List.of("Alaska", "Alberta", "Ontario", "Greenland")),
    ONTARIO("Ontario", Continent.NORTH_AMERICA, List.of("Alberta", "Northwest territory", "Greenland", "Quebec", "Eastern United_States", "Western United States")),
    QUEBEC("Quebec", Continent.NORTH_AMERICA, List.of("Ontario", "Greenland", "Eastern United States")),
    WESTERN_UNITED_STATES("Western United States", Continent.NORTH_AMERICA, List.of("Alberta", "Ontario", "Eastern United States", "Central America")),
    ARGENTINA("Argentina", Continent.SOUTH_AMERICA, List.of("Peru", "Brazil")),
    BRAZIL("Brazil", Continent.SOUTH_AMERICA, List.of("Venezuela", "Peru", "Argentina", "North Africa")),
    PERU("Peru", Continent.SOUTH_AMERICA, List.of("Venezuela", "Brazil", "Argentina")),
    VENEZUELA("Venezuela", Continent.SOUTH_AMERICA, List.of("Central America", "Brazil", "Peru")),
    GREAT_BRITAIN("Great Britain", Continent.EUROPE, List.of("Iceland", "Scandinavia", "Northern Europe", "Western Europe")),
    ICELAND("Iceland", Continent.EUROPE, List.of("Greenland", "Great Britain", "Scandinavia")),
    NORTHERN_EUROPE("Northern Europe", Continent.EUROPE, List.of("Great Britain", "Scandinavia", "Ukraine", "Southern Europe", "Western Europe")),
    SCANDINAVIA("Scandinavia", Continent.EUROPE, List.of("Iceland", "Great Britain", "Northern Europe", "Ukraine")),
    SOUTHERN_EUROPE("Southern Europe", Continent.EUROPE, List.of("Northern Europe", "Ukraine", "Middle East", "Egypt", "North Africa", "Western Europe")),
    UKRAINE("Ukraine", Continent.EUROPE, List.of("Scandinavia", "Northern Europe", "Southern Europe", "Middle East", "Afghanistan", "Ural")),
    WESTERN_EUROPE("Western Europe", Continent.EUROPE, List.of("Great Britain", "Northern Europe", "Southern Europe", "North Africa")),
    CONGO("Congo", Continent.AFRICA, List.of("North Africa", "East_Africa", "South Africa")),
    EAST_AFRICA("East Africa", Continent.AFRICA, List.of("Egypt", "North Africa", "Congo", "South_Africa", "Madagascar", "Middle East")),
    EGYPT("Egypt", Continent.AFRICA, List.of("Southern Europe", "Middle East", "East Africa", "North Africa")),
    MADAGASCAR("Madagascar", Continent.AFRICA, List.of("South Africa", "East_Africa")),
    NORTH_AFRICA("North Africa", Continent.AFRICA, List.of("Brazil", "Western Europe", "Southern Europe", "Egypt", "East Africa", "Congo")),
    SOUTH_AFRICA("South Africa", Continent.AFRICA, List.of("Congo", "East Africa", "Madagascar")),
    AFGHANISTAN("Afghanistan", Continent.ASIA, List.of("Ukraine", "Ural", "China", "India", "Middle East")),
    CHINA("China", Continent.ASIA, List.of("Siberia", "Ural", "Afghanistan", "India", "Siam", "Mongolia")),
    INDIA("India", Continent.ASIA, List.of("Middle East", "Afghanistan", "China", "Siam")),
    IRKUTSK("Irkutsk", Continent.ASIA, List.of("Siberia", "Yakutsk", "Kamchatka", "Mongolia")),
    JAPAN("Japan", Continent.ASIA, List.of("Kamchatka", "Mongolia")),
    KAMCHATKA("Kamchatka", Continent.ASIA, List.of("Alaska", "Yakutsk", "Irkutsk", "Japan", "Mongolia")),
    MIDDLE_EAST("Middle East", Continent.ASIA, List.of("Southern Europe", "Ukraine", "Afghanistan", "India", "East Africa", "Egypt")),
    MONGOLIA("Mongolia", Continent.ASIA, List.of("Siberia", "Irkutsk", "Kamchatka", "Japan", "China")),
    SIAM("Siam", Continent.ASIA, List.of("India", "China", "Indonesia")),
    SIBERIA("Siberia", Continent.ASIA, List.of("Ural", "Yakutsk", "Irkutsk", "Mongolia", "China")),
    URAL("Ural", Continent.ASIA, List.of("Ukraine", "Siberia", "China", "Afghanistan")),
    YAKUTSK("Yakutsk", Continent.ASIA, List.of("Siberia", "Irkutsk", "Kamchatka")),
    EASTERN_AUSTRALIA("Eastern Australia", Continent.AUSTRALIA, List.of("New Guinea", "Western Australia")),
    INDONESIA("Indonesia", Continent.AUSTRALIA, List.of("Siam", "New Guinea", "Western Australia")),
    NEW_GUINEA("New Guinea", Continent.AUSTRALIA, List.of("Indonesia", "Eastern Australia", "Western Australia")),
    WESTERN_AUSTRALIA("Western Australia", Continent.AUSTRALIA, List.of("Indonesia", "New Guinea", "Eastern Australia"));

    private final String name;
    private final Continent continent;
    private final List<String> neighbours;
    private final CardType type;

    Territory(String name, Continent continent, List<String> neighbours) {
        this.name = name;
        this.continent = continent;
        this.neighbours = neighbours;
        this.type = Math.random() < 0.33 ? CardType.INFANTRY : Math.random() < 0.66 ? CardType.CAVALRY : CardType.ARTILLERY;
    }

    public static Territory fromName(String name) {
        for (Territory t : Territory.values()) {
            if (t.name().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static Territory fromString(String country) {
        Territory minTerritory = null;
        int minDistance = Integer.MAX_VALUE;

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        for (Territory ter : Territory.values()) {
            int distance = levenshteinDistance.apply(country.toLowerCase(), ter.name.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                minTerritory = ter;
            }
        }
        return minTerritory;
    }
}

