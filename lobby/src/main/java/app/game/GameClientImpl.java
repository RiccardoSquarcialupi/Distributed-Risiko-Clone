package app.game;


import app.Launcher;
import app.game.GUI.GUIGame;
import app.game.card.Goal;
import app.game.card.Territory;
import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class GameClientImpl implements GameClient {
    private final ContextManagerParameters cltPar;
    public GameSender gameSender;
    public GameReceiver gameReceiver;

    private final List<Territory> myTerritories = new ArrayList<>();
    private GUIGame guiGame;

    public GameClientImpl(ContextManagerParameters cltPar) {
        this.cltPar = cltPar;
        this.gameSender = new GameSender();
        gameReceiver = new GameReceiver(this);
        guiGame = (GUIGame) Launcher.getCurrentGui();
    }

    public void loop() {
        while (gameReceiver.isRunning()) {
            //TODO: GET CARD FROM MANAGER AND GOAL CARD
            //TODO: DISPOSE ARMIES

            //TODO: WAIT FOR MY TURN
                //TODO: SOMEONE WIN
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

                //TODO: CHECK IF I WIN

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

    public String getGoal() {
        checkForGoalCard();
        switch(this.cltPar.getGoalCard()){
            case CONQUER_24_TERRITORIES:
                return "Conquer 24 territories";
            case CONQUER_ASIA_AND_AFRICA:
                return "Conquer Asia and Africa";
            case CONQUER_ASIA_AND_SOUTH_AMERICA:
                return "Conquer Asia and South America";
            case CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT:
                return "Conquer Europe and Oceania and a third continent";
            case CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT:
                return "Conquer Europe and South America and a third continent";
            case CONQUER_NORTH_AMERICA_AND_AFRICA:
                return "Conquer North America and Africa";
            case CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH:
                return "Conquer 18 territories with 2 armies each";
            case DESTROY_PLAYER_1:
                return "Destroy player 1: "+ this.getClientList().get(0).getNickname();
            case DESTROY_PLAYER_2:
                return "Destroy player 2: "+ this.getClientList().get(1).getNickname();
            case DESTROY_PLAYER_3:
                return "Destroy player 3: "+ this.getClientList().get(2).getNickname();
            case DESTROY_PLAYER_4:
                return "Destroy player 4: "+ this.getClientList().get(3).getNickname();
            case DESTROY_PLAYER_5:
                return "Destroy player 5: "+ this.getClientList().get(4).getNickname();
            case DESTROY_PLAYER_6:
                return "Destroy player 6: "+ this.getClientList().get(5).getNickname();
        }
        return null;
    }

    private void checkForGoalCard() {
        var a = this.cltPar.getGoalCard().ordinal();
        switch(a){
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                if(this.cltPar.getClientList().size()<(a-6) || this.getClientList().get(a-7).getIP().equals(this.getIP())){
                    this.cltPar.setGoalCard(Goal.CONQUER_24_TERRITORIES);
                }
                break;
            default:
                break;
        }
    }

    public void disableActions() {
        guiGame.disableActions();
    }

    public void checkforMyTurn(String clientIp) {
        for (int i=0;i<this.getClientList().size();i++){
            if(this.getClientList().get(i).getIP().equals(clientIp)){
                if(i+1 < this.getClientList().size() && this.getClientList().get(i+1).getIP().equals(this.getIP())){
                    guiGame.enableActions();
                }
                else if (i+1 >= this.getClientList().size() && this.getClientList().get(0).getIP().equals(this.getIP())){
                    guiGame.enableActions();
                }
            }
        }
    }

    public void endMyTurn(){
        this.gameSender.clientEndTurn(this.getIP());
    }

    public void updateTerritory(String ip, Territory territory, Integer nArmiesChange, Optional<String> conquerorIp) {
        if(conquerorIp.isPresent()){
            this.cltPar.updateEnemyTerritoryWithConqueror(this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0),territory,nArmiesChange,conquerorIp.get());
        }else {
            this.cltPar.updateEnemyTerritory(this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0),territory,nArmiesChange);
        }

    }
}
