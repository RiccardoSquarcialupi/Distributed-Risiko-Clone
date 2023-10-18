package app.game;


import app.game.card.Territory;
import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;

import java.util.ArrayList;
import java.util.List;


public class GameClientImpl implements GameClient {
    private final ContextManagerParameters cltPar;
    private final GameSender gameSender = new GameSender();
    private final GameReceiver gameReceiver = new GameReceiver();

    private final List<Territory> myTerritories = new ArrayList<>();

    public GameClientImpl(ContextManagerParameters cltPar) {
        this.cltPar = cltPar;
        gameReceiver.start();
    }

    public void loop() {
        while (gameReceiver.isRunning()) {
            //TODO: GET CARD FROM MANAGER AND GOAL CARD
            //TODO: DISPOSE ARMIES

            //TODO: WAIT FOR MY TURN
                //TODO: WHILE WAITING SOMEONE IS ATTACKING ME: DEFENSE PART

            //TODO: START TURN

                //TODO: CHECK FOR BONUS CARD
                    //TODO: USE BONUS
                    //TODO: NOT USE BONUS
                //TODO: DISPOSE ARMIES

                //TODO: CYCLE ATTACK PHASE
                    //TODO SEND RESULT OF THE ATTACK TO THE OTHERS PLAYERS
                    //TODO WAIT FOR THE DEFENSE RESPONSE
                    //TODO COMPUTE THE RESULT OF THE ATTACK
                //TODO: CLOSE CYCLE
                //TODO IF I HAVE CONQUERED A COUNTRY DRAW A CARD

                //TODO: STRATEGIC MOVE PHASE
                //TODO: END TURN

            gameReceiver.stop();
        }

    }

    @Override
    public String getIP() {
        return this.cltPar.getIp();
    }

    @Override
    public String getNickname() {
        return this.cltPar.getNickname();
    }

    public List<JSONClient> getClientList() {
        return this.cltPar.getClientList();
    }

    public void addTerritory(Territory territory) {
        this.myTerritories.add(territory);
    }
}
