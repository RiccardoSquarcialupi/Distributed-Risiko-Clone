package app.game.comunication;

import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

public class GameSender extends AbstractVerticle{

    WebClient client;

    public GameSender() {
        client = WebClient.create(Vertx.vertx());
    }
    //TODO: THOSE BELOW ARE ONLY SKELETONS, IMPLEMENT THEM
    protected void clientStartTurn(int idClient){
        jsonify(idClient);
    }

    protected void clientEndTurn(int idClient){
        jsonify(idClient);
    }

    protected void clientGetStateCard(int idClient){
        jsonify(idClient);
    }

    protected void clientChangeArmiesInTerritory(int idClient, Territory territory, int armies){
        jsonify(idClient, territory, armies);
    }

    protected void clientUseStateCardBonus(int idClient, List<CardType> listCardType, int bonusArmies, int extraBonusArmies){
        jsonify(idClient, listCardType, bonusArmies, extraBonusArmies);
    }


    protected void clientAttackTerritory(int idClientAttack, int idClientDefend, int diceATKResult, int numberofDices){
        jsonify(idClientAttack, idClientDefend, diceATKResult, numberofDices);
    }

    protected void clientDefendTerritory(int idClientAttack, int idClientDefend, int diceATKResult, int numberofDices){
        jsonify(idClientAttack, idClientDefend, diceATKResult, numberofDices);
    }


    protected void clientWin(int idClient, Goal goalCard, List<Territory> territoryOwnedList){
        jsonify(idClient, goalCard, territoryOwnedList);
    }

    private JsonArray jsonify(Object ... args){
        JsonArray jsonArray = new JsonArray();
        for ( Object o : args) {
            jsonArray.add(o);
        }
        return jsonArray;
    }


}
