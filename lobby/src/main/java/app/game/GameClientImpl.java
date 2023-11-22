package app.game;


import app.Launcher;
import app.common.Pair;
import app.game.GUI.GUIGame;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class GameClientImpl implements GameClient {
    private final ContextManagerParameters cltPar;
    private final Map<JSONClient, List<JSONClient>> randomOrderList = new HashMap<>();
    public GameSender gameSender;
    public GameReceiver gameReceiver;
    private GUIGame guiGame;
    private boolean myTurn;
    private List<JSONClient> randomOrder = new ArrayList<>();
    private List<Integer> lastAttackDicesThrow;
    private List<Integer> lastDefenceDicesThrow;
    private boolean orderFound = false;

    public GameClientImpl(ContextManagerParameters cltPar) {
        this.cltPar = cltPar;
        this.gameSender = new GameSender();
        gameReceiver = new GameReceiver(this);
        gameReceiver.start();
        lastAttackDicesThrow = new ArrayList<>();
        lastDefenceDicesThrow = new ArrayList<>();
        this.myTurn = false;

        Launcher.getVertx().setTimer(TimeUnit.SECONDS.toMillis(2), tid -> {
            this.guiGame = ((GUIGame) Launcher.getCurrentGui());
        });

        Launcher.getVertx().setTimer(TimeUnit.SECONDS.toMillis(4), tid -> {
            broadcastTerritories();
        });
    }

    public List<JSONClient> getRandomOrder() {
        return randomOrder;
    }

    public void loop() {
        guiGame = (GUIGame) Launcher.getCurrentGui();
        while (gameReceiver.isRunning()) {
            //TODO: GET CARD FROM MANAGER AND GOAL CARD
            //TODO: DISPOSE ARMIES

            //TODO: WAIT FOR MY TURN
            //TODO: SOMEONE WIN
            //TODO: WHILE WAITING SOMEONE IS ATTACKING_SELECT_FIRST_COUNTRY ME: DEFENSE PART

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
        this.cltPar.addTerritory(territory);
    }

    public String getGoal() {
        checkForGoalCard();
        switch (this.cltPar.getGoalCard()) {
            case DESTROY_PLAYER_1:
                return "Destroy player 1: " + this.getClientList().get(0).getNickname();
            case DESTROY_PLAYER_2:
                return "Destroy player 2: " + this.getClientList().get(1).getNickname();
            case DESTROY_PLAYER_3:
                return "Destroy player 3: " + this.getClientList().get(2).getNickname();
            case DESTROY_PLAYER_4:
                return "Destroy player 4: " + this.getClientList().get(3).getNickname();
            case DESTROY_PLAYER_5:
                return "Destroy player 5: " + this.getClientList().get(4).getNickname();
            case DESTROY_PLAYER_6:
                return "Destroy player 6: " + this.getClientList().get(5).getNickname();
            default:
                return this.cltPar.getGoalCard().name();
        }
    }

    private void checkForGoalCard() {
        var a = this.cltPar.getGoalCard().ordinal();
        switch (a) {
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                if (this.cltPar.getClientList().size() < (a - 6) || this.getClientList().get(a - 7).getIP().equals(this.getIP())) {
                    this.cltPar.setGoalCard(Goal.CONQUER_24_TERRITORIES);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void checkForMyTurn(String clientIp) {
        for (int i = 0; i < this.randomOrder.size(); i++) {
            if (this.randomOrder.get(i).getIP().equals(clientIp)
                    && this.randomOrder.get((i + 1) % this.randomOrder.size()).getIP().equals(this.getIP())
            ) {
                this.myTurn = true;
                guiGame.startTurn();
            }
        }
    }

    public void endMyTurn() {
        this.myTurn = false;
        this.gameSender.clientEndTurn(this.getIP());
    }


    public void someoneGetBonus(String ip, List<CardType> cardsList, Integer bonusArmies, Integer extraBonusArmies) {
        guiGame.someoneGetBonus(ip, cardsList, bonusArmies, extraBonusArmies);
    }

    public void lobbyClosed() {
        Launcher.lobbyClosed();
    }

    public void someoneDrawStateCard(String ip) {
        guiGame.someoneDrawStateCard(ip);
    }

    public void someoneWin(String ip, Goal goalCard, List<Territory> listTerritories) {
        if (checkWin(ip, goalCard, listTerritories)) {
            guiGame.someoneWin(ip, goalCard, listTerritories);
        }
    }

    private boolean checkWin(String ip, Goal goalCard, List<Territory> listTerritories) {
        //TODO CHECK IF THE PLAYER WIN
        return true;
    }

    public void setEnemyTerritory(String ip, Territory t) {
        this.cltPar.setEnemyTerritory(this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0), t);
    }

    @Override
    public boolean areTerritoriesReceived() {
        return this.cltPar.getMaxPlayer() == this.cltPar.getNumberPlayerReceivedTerritories();
    }

    @Override
    public boolean isMyTurn() {
        return this.myTurn;
    }

    @Override
    public void placeArmy(String clientIp, String country, Integer deltaArmies) {
        var clt = this.cltPar.getClientList().stream().filter(c -> c.getIP().equals(clientIp)).collect(Collectors.toList()).get(0);
        var over = this.cltPar.addArmy(clt, country, deltaArmies);
        gameSender.broadcastArmies(country, this.cltPar.getAllTerritories().get(
                        new Pair<>(clt, Territory.fromString(country))))
                .onComplete(h -> {
                    this.guiGame.updateMapImage();
                    System.out.println("Received ok army placed");
                    if (over) {
                        System.out.println("All territory placed");
                        if (!orderFound) {
                            this.guiGame.orderingPhase();
                        } else if (this.myTurn) {
                            this.guiGame.playingPhase();
                        } else {
                            this.guiGame.waitingPhase();
                        }
                    }
                }).onFailure(h -> {
                    System.out.println("ERROR ON BROADCAST ARMIES");
                });
    }

    @Override
    public void placeFirstArmies() {
        this.cltPar.setPlaceableArmiesAtStart();
        this.guiGame.placeArmies();
    }

    @Override
    public void placeArmies(int nArmies) {
        this.cltPar.setPlaceableArmies(nArmies);
        this.guiGame.placeArmies();
    }

    @Override
    public Pair<Integer, Integer> getPlacingState() {
        return this.cltPar.getPlacingState();
    }

    public void broadcastTerritories() {
        this.gameSender.broadcastMyTerritories(this.getIP(), this.cltPar.getMyTerritories());
    }

    public void getStateCard() {
        //TODO how to draw the card?
        this.gameSender.getStateCard(this.getIP());
    }

    public void changeArmiesInMyTerritory(Territory territorySender, Territory territoryReceiver, Integer nArmiesChange) {
        this.gameSender.changeArmiesInTerritory(this.getIP(), territorySender, Optional.ofNullable(territoryReceiver), nArmiesChange, Optional.empty()).onSuccess(res -> {
            this.cltPar.updateMyTerritory(territorySender, territoryReceiver, nArmiesChange);
            this.guiGame.updateMapImage();
        });
    }

    public void updateEnemyTerritory(String ip, Territory territorySender, Territory territoryReceiver, Integer nArmiesChange) {
        this.cltPar.updateEnemyTerritories(this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0), territorySender, territoryReceiver, nArmiesChange);
        this.guiGame.updateMapImage();
    }

    public void updateEnemyTerritoryWithConqueror(String winnerIp, Territory territory, Integer nArmies, String loserIp) {
        this.cltPar.updateEnemyTerritoryWithConqueror(this.getClientList().stream().filter(c -> c.getIP().equals(winnerIp)).collect(Collectors.toList()).get(0), this.getClientList().stream().filter(c -> c.getIP().equals(loserIp)).collect(Collectors.toList()).get(0), territory, nArmies);
        this.guiGame.updateMapImage();
    }

    public Map<Pair<JSONClient, Territory>, Integer> getAllTerritories() {
        return this.cltPar.getAllTerritories();
    }

    @Override
    public void updateEnemyArmies(Territory country, Integer armies) {
        var clt = this.cltPar.getAllTerritories().keySet().stream()
                .filter(p -> p.getSecond().equals(country))
                .collect(Collectors.toList()).get(0).getFirst();
        this.cltPar.updateEnemyTerritoryAfterBroadcast(clt, country, armies);
        this.guiGame.updateMapImage();
    }

    @Override
    public void receiveRandomOrder(String ip, List<JSONClient> order) {
        if (!orderFound) {
            randomOrderList.put(this.cltPar.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0), order);
            System.out.println("SIZE OF RANDOM ORDER " + randomOrderList.size() + " SIZE OF CLIENT LIST " + this.cltPar.getClientList().size());
            if (randomOrderList.size() == this.cltPar.getClientList().size()) {
                //CHECK IF EVERY LIST IN THE MAP IS THE SAME OTHERWISE PICK ONE RANDOMLY, CLEAR THE RANDOM ORDER LIST AND REPEAT
                List<JSONClient> first = randomOrderList.get(new ArrayList<>(this.randomOrderList.keySet()).get(0));
                boolean allEquals = true;
                for (var entry : randomOrderList.entrySet()) {
                    if (!entry.getValue().equals(first)) {
                        allEquals = false;
                        break;
                    }
                }
                if (!allEquals) {
                    System.out.println("RANDOM ORDER NOT EQUALS cleaning and repeating");
                    Random rn = new Random();
                    int i = rn.nextInt(getClientList().size());
                    List<JSONClient> randomPick = randomOrderList.get(new ArrayList<>(this.randomOrderList.keySet()).get(i));
                    randomOrderList.clear();
                    sendRandomOrderForTurning(randomPick);
                } else {
                    //THIS IS THE ORDER
                    System.out.println("ORDER FOUND!!!");
                    this.orderFound = true;
                    System.out.println("Order:" + Arrays.toString(first.toArray()));
                    this.randomOrder = first;
                    //REPLYING THE ORDER FOUND TO THE OTHERS
                    sendRandomOrderForTurning(first);
                    if (this.guiGame.orderFoundAndTurnCheck()) {
                        System.out.println("ITS MY TURN, I'm the first player to play");
                        this.myTurn = true;
                        this.guiGame.startTurn();
                    } else {
                        this.guiGame.waitingPhase();
                    }
                }
            }
        }
    }

    @Override
    public Future<List<Integer>> throwDices(int nDices) {
        guiGame.addLogToTextArea(("Sending " + nDices + " throws"));
        return this.gameSender.byzantineDiceLaunch(this.getIP(), nDices);
    }

    @Override
    public void sendAttackMsg(Territory territoryFromToAttack, Territory territoryToAttack, int nDicesToUse) {
        throwDices(nDicesToUse).onSuccess(h -> {
            lastAttackDicesThrow = h.stream().sorted().collect(Collectors.toList());
            guiGame.addLogToTextArea("Sending attack with dices result" + lastAttackDicesThrow);
            this.gameSender.clientAttackTerritory(this.getIP(), getIpFromTerritory(territoryToAttack), lastAttackDicesThrow, territoryFromToAttack, territoryToAttack).onSuccess(
                    h1 -> guiGame.waitingPhase());
        });
    }

    @Override
    public void sendDefendMsg(Territory enemyTerritory, Territory myTerritory, int nDicesToUse) {
        throwDices(nDicesToUse).onSuccess(h -> {
            lastDefenceDicesThrow = h.stream().sorted().collect(Collectors.toList());;
            guiGame.addLogToTextArea("Sending defence with dices result" + lastDefenceDicesThrow);
            computeDefenderResult(myTerritory, enemyTerritory);

        });

    }

    @Override
    public void receiveAttackMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory enemyTerritory, Territory myTerritory) {
        System.out.println("RECEIVED ATTACK MSG");
        if (ipClientDefend.equals(this.getIP())) {
            System.out.println("RECEIVED ATTACK MSG - I'm the defender");
            lastAttackDicesThrow = diceATKResult.stream().sorted().collect(Collectors.toList());
            this.guiGame.receiveAttackMsg(ipClientAttack, ipClientDefend, lastAttackDicesThrow, enemyTerritory, myTerritory);
        }
    }

    @Override
    public void receiveDefendMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory myTerritory, Territory enemyTerritory) {
        System.out.println("RECEIVED DEFENCE MSG");
        if (ipClientAttack.equals(this.getIP())) {
            System.out.println("RECEIVED DEFENCE MSG - I'm the attacker");
            lastDefenceDicesThrow = diceDEFResult.stream().sorted().collect(Collectors.toList());
            this.guiGame.receiveDefendMsg(ipClientAttack, ipClientDefend, lastDefenceDicesThrow, myTerritory, enemyTerritory);
            computeAttackerResult(myTerritory, enemyTerritory);
        }
    }

    private List<Integer> commonComputeResult() {
        //order ascending
        var attackDices = lastAttackDicesThrow.stream().sorted().collect(Collectors.toList());
        var defenceDices = lastDefenceDicesThrow.stream().sorted().collect(Collectors.toList());
        //compute armies lost
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        while(attackDices.size() != defenceDices.size()){
            if (attackDices.size() > defenceDices.size()) {
                attackDices.remove(0);
            } else {
                defenceDices.remove(0);
            }
        }
        System.out.println("ATTACK DICES: " + attackDices);
        System.out.println("DEFENCE DICES: " + defenceDices);
        for (int i = 0; i < defenceDices.size(); i++) {
            if (attackDices.get(i) != null) {
                if (defenceDices.get(i) != null) {
                    result.add(new Pair<>(attackDices.get(i), defenceDices.get(i)));
                }
            }
        }
        Integer nArmiesLostByAttacker = 0;
        Integer nArmiesLostByDefender = 0;
        for (Pair<Integer, Integer> pair : result) {
            if (pair.getFirst().compareTo(pair.getSecond()) > 0) {
                nArmiesLostByDefender++;
            } else {
                nArmiesLostByAttacker++;
            }
        }
        return Arrays.asList(nArmiesLostByAttacker, nArmiesLostByDefender);
    }

    private void computeAttackerResult(Territory myTerritory, Territory enemyTerritory) {
        List<Integer> result = commonComputeResult();
        var enemyArmies = getAllTerritories().entrySet().stream().filter(p -> p.getKey().getSecond().equals(enemyTerritory)).collect(Collectors.toList()).get(0).getValue();
        placeArmy(this.getIP(), myTerritory.name(), -result.get(0));
        if ((enemyArmies - result.get(1)) <= 0) {
            //conquer
            this.gameSender.changeArmiesInTerritory(getIpFromTerritory(myTerritory), enemyTerritory, Optional.empty(), 0, Optional.ofNullable(getIpFromTerritory(enemyTerritory))).onSuccess(h -> {
                this.updateEnemyTerritoryWithConqueror(this.getIP(), enemyTerritory, 0, getIpFromTerritory(enemyTerritory));
                this.guiGame.movingPhaseAfterConquer(myTerritory, enemyTerritory);
            });
        }else{
            this.guiGame.updateMapImage();
        }
        this.guiGame.playingPhase();
    }

    private void computeDefenderResult(Territory myTerritory, Territory enemyTerritory) {
        List<Integer> result = commonComputeResult();
        var myArmies = getAllTerritories().entrySet().stream().filter(p -> p.getKey().getSecond().equals(myTerritory)).collect(Collectors.toList()).get(0).getValue();
        if ((myArmies - result.get(1)) > 0) {
            //not conquered
            placeArmy(this.getIP(), myTerritory.name(), -result.get(1));
        }else{
            //conquered, set nArmies to ZERO
            placeArmy(this.getIP(), myTerritory.name(), -myArmies);
        }
        this.gameSender.clientDefendTerritory(getIpFromTerritory(enemyTerritory), this.getIP(), lastDefenceDicesThrow, enemyTerritory, myTerritory).onSuccess(h1 -> {
            System.out.println("DEFENCE RESULT SENT WITH SUCCESS: " + lastDefenceDicesThrow);
        });

    }

    private String getIpFromTerritory(Territory territory) {
        return this.cltPar.getAllTerritories().keySet().stream().filter(p -> p.getSecond().equals(territory)).collect(Collectors.toList()).get(0).getFirst().getIP();
    }

    public Future<Void> sendDiceShare(int rd) {
        return this.gameSender.sendDiceShare(this.getIP(), rd);
    }

    public void sendRandomOrderForTurning(List<JSONClient> shuffledList) {
        randomOrderList.put(this.cltPar.getClientList().stream().filter(c -> c.getIP().equals(this.getIP())).collect(Collectors.toList()).get(0), shuffledList);
        this.gameSender.sendRandomOrderForTurning(this.getIP(), shuffledList);
    }

}
