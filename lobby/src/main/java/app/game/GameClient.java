package app.game;

import app.common.Client;
import app.common.Pair;
import app.game.card.Card;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;
import io.vertx.core.Future;

import java.util.List;
import java.util.Map;

public interface GameClient extends Client {

    void checkForMyTurn(String clientIp);

    void updateEnemyTerritory(String ip, Territory fromName, Territory fromName1, Integer nArmiesChange);

    void updateEnemyTerritoryWithConqueror(String winnerIp, Territory fromName, Integer nArmies, String loserIp);

    void someoneGetBonus(String ip, List<CardType> cardsList, Integer bonusArmies);

    void closeConnection();

    void someoneDrawStateCard(String ip, int cardIndex);

    boolean someoneWin(String ip, Goal goalCard, String playerDestroyed);

    void setEnemyTerritory(String ip, Territory t);

    boolean areTerritoriesReceived();

    boolean isMyTurn();

    void placeArmy(String clientIp, String country, Integer armies);

    void placeArmies(int nArmies);

    void placeFirstArmies();

    Pair<Integer, Integer> getPlacingState();

    Map<Pair<JSONClient, Territory>, Integer> getAllTerritories();

    void updateEnemyArmies(Territory country, Integer armies);

    void receiveRandomOrder(String ip, List<JSONClient> order);

    Future<List<Integer>> throwDices(int nDices);

    void sendAttackMsg(Territory territoryFromToAttack, Territory territoryToAttack, int nDicesToUse);

    void sendDefendMsg(Territory enemyTerritory, Territory myTerritory, int nDicesToUse);

    void receiveAttackMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory enemyTerritory, Territory myTerritory);

    void receiveDefendMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory myTerritory, Territory enemyTerritory);

    void playerLeft(String ip);

    void useBonusCards(List<CardType> cardList, Integer bonus);
}
