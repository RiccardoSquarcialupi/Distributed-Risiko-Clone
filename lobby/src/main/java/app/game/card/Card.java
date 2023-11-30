package app.game.card;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Card {
    static Card fromString(String card) {
        Card minCard = null;
        int minDistance = Integer.MAX_VALUE;

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        for (Card car : Stream.concat(
                Arrays.stream(Territory.values()),
                Stream.of(CardType.JOLLY,CardType.JOLLY)
            ).collect(Collectors.toList())
        ) {
            int distance = levenshteinDistance.apply(card.toLowerCase(), car.toString().toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                minCard = car;
            }
        }
        return minCard;
    }
}
