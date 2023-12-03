package app.game;


import app.Launcher;
import app.common.Pair;
import app.game.GUI.GUIGame;
import app.game.card.CardType;
import app.game.card.Continent;
import app.game.card.Goal;
import app.game.card.Territory;
import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.Promise;

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
    private boolean cardIsDrawed = false;
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
                return this.cltPar.getGoalCard().getName();
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
                cardIsDrawed = false;
                guiGame.startTurn();
            }
        }
    }

    public void endMyTurn() {
        this.gameSender.clientEndTurn(this.getIP()).onSuccess(h -> {
            myTurn = false;
            this.guiGame.waitingPhase();
        });
    }

    public void playerLeft(String ip) {
        randomOrder = randomOrder.stream().filter(c -> !c.getIP().equals(ip)).collect(Collectors.toList());
        guiGame.addLogToTextArea("Player " + this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0).getNickname() + " left the game");
    }

    @Override
    public void useBonusCards(List<CardType> cardsList, Integer bonus) {
        this.gameSender.clientUseStateCardsBonus(this.getIP(), cardsList, bonus).onSuccess(h -> {
            this.cltPar.getBonusCards().removeAll(cardsList);
            this.cltPar.setPlaceableArmies(bonus);
        });
    }

    public void someoneGetBonus(String ip, List<CardType> cardsList, Integer bonusArmies) {
        guiGame.someoneGetBonus(ip, cardsList, bonusArmies);
    }

    public void closeConnection() {
        this.gameReceiver.stop();
        this.cltPar.resetGame();
        //SEND MSG TO THE OTHERS PLAYER TO REMOVE MYSELF FROM THE GAME
        if (isMyTurn()) {
            endMyTurn();
        }
        this.gameSender.playerLeft(this.getIP()).onSuccess(h -> {
            System.out.println("Player left msg sent");
            Launcher.lobbyClosed();
        });
    }

    public void someoneDrawStateCard(String ip, int cardIndex) {
        this.cltPar.getDeck().remove(cardIndex);
        guiGame.someoneDrawStateCard(ip);
    }

    public boolean someoneWin(String ip, Goal goalCard, String playerDestroyed) {
        if (checkWin(ip, goalCard, playerDestroyed)) {
            guiGame.someoneWin(ip, goalCard);
            return true;
        }
        return false;
    }

    private boolean checkWin(String ip, Goal goalCard, String playerDestroyed) {
        var winnerTerritories = this.cltPar.getAllTerritories().entrySet().stream().filter(e -> e.getKey().getFirst().getIP().equals(ip)).collect(Collectors.toList());
        var winnerTerritoriesList = winnerTerritories.stream().map(e -> e.getKey().getSecond()).collect(Collectors.toList());   //list of territories that the winner has
        System.out.println("DEBUGGING WINNER CHECK CONDITION");
        if (playerDestroyed.isEmpty()) {
            System.out.println("PLAYER DESTROYED IS EMPTY, GOAL IS CONQUER CONTINENT OR HAVING N TERRITORIES");
            switch (goalCard) {
                case CONQUER_24_TERRITORIES:
                    System.out.println("GOAL IS CONQUER 24 TERRITORIES");
                    if (winnerTerritoriesList.size() == 24) {
                        return true;
                    }
                    System.out.println("ERROR - THE WINNER DOESN'T HAVE ALL THE TERRITORIES THAT HE SEND");
                    break;
                case CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH:
                    System.out.println("GOAL IS CONQUER 18 TERRITORIES WITH 2 ARMIES EACH");
                    if (winnerTerritories.size() == 18) {
                        return true;
                    }
                    System.out.println("ERROR - THE WINNER DOESN'T HAVE ALL THE TERRITORIES THAT HE SEND");
                    break;
                case CONQUER_ASIA_AND_AFRICA:
                    System.out.println("GOAL IS CONQUER ASIA AND AFRICA");
                    if (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.ASIA)).count() >= 12
                            && winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.AFRICA)).count() >= 6)
                        return true;
                    break;
                case CONQUER_ASIA_AND_SOUTH_AMERICA:
                    System.out.println("GOAL IS CONQUER ASIA AND SOUTH AMERICA");
                    if (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.ASIA)).count() >= 12
                            && winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() >= 4)
                        return true;
                    break;
                case CONQUER_NORTH_AMERICA_AND_AFRICA:
                    System.out.println("GOAL IS CONQUER NORTH AMERICA AND AFRICA");
                    if (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() >= 9
                            && winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.AFRICA)).count() >= 6)
                        return true;
                    break;
                case CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT:
                    System.out.println("GOAL IS CONQUER EUROPE AND OCEANIA AND A THIRD CONTINENT");
                    if (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.EUROPE)).count() >= 7
                            && winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.OCEANIA)).count() >= 4
                            && (
                            (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.ASIA)).count() >= 12) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.AFRICA)).count() >= 6) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() >= 9) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() >= 6)))
                        return true;
                    break;
                case CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT:
                    System.out.println("GOAL IS CONQUER EUROPE AND SOUTH AMERICA AND A THIRD CONTINENT");
                    if (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.EUROPE)).count() >= 7
                            && winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() >= 4
                            && (
                            (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.ASIA)).count() >= 12) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.AFRICA)).count() >= 6) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() >= 9) ||
                                    (winnerTerritories.stream().filter(e -> e.getKey().getSecond().getContinent().equals(Continent.OCEANIA)).count() >= 4)))
                        return true;
                    break;
                default:
                    return false;
            }
        } else {
            System.out.println("PLAYER DESTROYED IS NOT EMPTY, GOAL IS DESTROY A PLAYER");
            if (this.getClientList().stream().anyMatch(j -> j.getNickname().equals(playerDestroyed))) {
                switch (goalCard) {
                    case DESTROY_PLAYER_1:
                        System.out.println("GOAL IS DESTROY PLAYER 1");
                        if (playerDestroyed.equals(this.getClientList().get(0).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    case DESTROY_PLAYER_2:
                        System.out.println("GOAL IS DESTROY PLAYER 2");
                        if (playerDestroyed.equals(this.getClientList().get(1).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    case DESTROY_PLAYER_3:
                        System.out.println("GOAL IS DESTROY PLAYER 3");
                        if (playerDestroyed.equals(this.getClientList().get(2).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    case DESTROY_PLAYER_4:
                        System.out.println("GOAL IS DESTROY PLAYER 4");
                        if (playerDestroyed.equals(this.getClientList().get(3).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    case DESTROY_PLAYER_5:
                        System.out.println("GOAL IS DESTROY PLAYER 5");
                        if (playerDestroyed.equals(this.getClientList().get(4).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    case DESTROY_PLAYER_6:
                        System.out.println("GOAL IS DESTROY PLAYER 6");
                        if (playerDestroyed.equals(this.getClientList().get(5).getNickname())) {
                            if (this.getAllTerritories().keySet().stream().noneMatch(e -> e.getFirst().getNickname().equals(playerDestroyed)))
                                return true;
                            break;
                        }
                        break;
                    default:
                        return false;
                }
            } else {
                return false;
            }

        }
        return false;
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
        gameSender.placeArmies(country, this.cltPar.getAllTerritories().get(
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

    public Future<Void> broadcastTerritories() {
        Promise<Void> prm = Promise.promise();
        this.gameSender.broadcastMyTerritories(this.getIP(), this.cltPar.getMyTerritories()).onFailure(f -> {
            System.out.println("Error with broadcastTerritories" + f.getMessage());
            Launcher.getVertx().setTimer(2000, id -> broadcastTerritories().onSuccess(v -> prm.complete()));
        }).onSuccess(s -> prm.complete());
        return prm.future();
    }

    public Future<Void> getStateCard() {
        int cardIndex = new Random().nextInt(this.cltPar.getDeck().size());
        this.cltPar.getBonusCards().add(this.cltPar.getDeck().remove(cardIndex));
        return this.gameSender.drawStateCard(this.getIP(), cardIndex);
    }

    public Future<Void> changeArmiesInMyTerritory(Territory territorySender, Territory territoryReceiver, Integer nArmiesChange) {
        return this.gameSender.changeArmiesInTerritory(this.getIP(), territorySender, Optional.ofNullable(territoryReceiver), nArmiesChange, Optional.empty()).onSuccess(res -> {
            this.cltPar.updateMyTerritory(territorySender, territoryReceiver, nArmiesChange);
            this.guiGame.playingPhase();
        });
    }

    public void updateEnemyTerritory(String ip, Territory territorySender, Territory territoryReceiver, Integer nArmiesChange) {
        this.cltPar.updateEnemyTerritories(this.getClientList().stream().filter(c -> c.getIP().equals(ip)).collect(Collectors.toList()).get(0), territorySender, territoryReceiver, nArmiesChange);
        this.guiGame.updateMapImage();
        this.guiGame.tacticalMoveNotification(ip, nArmiesChange, territorySender.getName(), territoryReceiver.getName());
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
        } else {
            Random rn = new Random();
            if (rn.nextInt(5) > 2) {
                System.out.println("ORDER FOUND!!!");
                System.out.println("Order:" + (randomOrder));
                sendRandomOrderForTurning(this.randomOrder);
            }
        }
    }

    @Override
    public Future<List<Integer>> throwDices(int nDices) {
        return this.gameSender.byzantineDiceLaunch(this.getIP(), nDices);
    }

    @Override
    public void sendAttackMsg(Territory territoryFromToAttack, Territory territoryToAttack, int nDicesToUse) {
        throwDices(nDicesToUse).onSuccess(h -> {
            lastAttackDicesThrow = h.stream().sorted().collect(Collectors.toList());
            guiGame.addLogToTextArea("Sending attack with dices result" + lastAttackDicesThrow);
            guiGame.displayAttackDiceResult(lastAttackDicesThrow);
            this.gameSender.clientAttackTerritory(this.getIP(), getIpFromTerritory(territoryToAttack), lastAttackDicesThrow, territoryFromToAttack, territoryToAttack).onSuccess(
                    h1 -> guiGame.waitingPhase());
        });
    }

    @Override
    public void sendDefendMsg(Territory enemyTerritory, Territory myTerritory, int nDicesToUse) {
        throwDices(nDicesToUse).onSuccess(h -> {
            lastDefenceDicesThrow = h.stream().sorted().collect(Collectors.toList());
            ;
            guiGame.addLogToTextArea("Sending defence with dices result" + lastDefenceDicesThrow + " - ");
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
        while (attackDices.size() != defenceDices.size()) {
            if (attackDices.size() > defenceDices.size()) {
                attackDices.remove(0);
            } else {
                defenceDices.remove(0);
            }
        }
        System.out.println("ATTACK DICES: " + attackDices);
        System.out.println("DEFENCE DICES: " + defenceDices);
        int nArmiesLostByAttacker = 0;
        int nArmiesLostByDefender = 0;
        for (int i = 0; i < defenceDices.size(); i++) {
            if (attackDices.get(i) != null) {
                if (defenceDices.get(i) != null) {
                    if (attackDices.get(i) > defenceDices.get(i)) {
                        nArmiesLostByDefender++;
                    } else {
                        nArmiesLostByAttacker++;
                    }
                }
            }
        }
        System.out.println("ARMIES LOST BY ATTACKER: " + nArmiesLostByAttacker);
        System.out.println("ARMIES LOST BY DEFENDER: " + nArmiesLostByDefender);
        return Arrays.asList(nArmiesLostByAttacker, nArmiesLostByDefender);
    }


    private void updateArmiesAfterBattle(String clientIp, String country, Integer deltaArmies) {
        var clt = this.cltPar.getClientList().stream().filter(c -> c.getIP().equals(clientIp)).collect(Collectors.toList()).get(0);
        this.cltPar.updateArmiesAfterBattle(clt, country, deltaArmies);
        gameSender.placeArmies(country, this.cltPar.getAllTerritories().get(
                        new Pair<>(clt, Territory.fromString(country))))
                .onComplete(h -> {
                    this.guiGame.updateMapImage();
                    System.out.println("Armies update after battle MSG - OK");
                }).onFailure(h -> {
                    System.out.println("Armies update after battle MSG - ERROR");
                });
    }

    private void computeAttackerResult(Territory myTerritory, Territory enemyTerritory) {
        List<Integer> result = commonComputeResult();
        var enemyArmies = getAllTerritories().entrySet().stream().filter(p -> p.getKey().getSecond().equals(enemyTerritory)).collect(Collectors.toList()).get(0).getValue();

        updateArmiesAfterBattle(this.getIP(), myTerritory.name(), -result.get(0));
        if ((enemyArmies - result.get(1)) <= 0) {
            //conquer
            this.gameSender.changeArmiesInTerritory(getIpFromTerritory(myTerritory), enemyTerritory, Optional.empty(), 0, Optional.ofNullable(getIpFromTerritory(enemyTerritory))).onSuccess(h -> {
                this.updateEnemyTerritoryWithConqueror(this.getIP(), enemyTerritory, 0, getIpFromTerritory(enemyTerritory));
                if (!cardIsDrawed) {
                    this.getStateCard().onSuccess(h1 -> {
                        cardIsDrawed = true;
                        this.guiGame.updateHandCards(this.cltPar.getBonusCards());
                    });
                }
                this.guiGame.movingPhaseAfterConquer(myTerritory, enemyTerritory);
            });
        } else {
            this.guiGame.updateMapImage();
        }
        this.guiGame.playingPhase();
    }

    private void computeDefenderResult(Territory myTerritory, Territory enemyTerritory) {
        List<Integer> result = commonComputeResult();
        var myArmies = getAllTerritories().entrySet().stream().filter(p -> p.getKey().getSecond().equals(myTerritory)).collect(Collectors.toList()).get(0).getValue();
        if ((myArmies - result.get(1)) > 0) {
            //not conquered
            System.out.println("DEFENDER NOT CONQUERED - ARMIES LOST: " + result.get(1));
            updateArmiesAfterBattle(this.getIP(), myTerritory.name(), -result.get(1));
        } else {
            //conquered, set nArmies to ZERO
            System.out.println("DEFENDER CONQUERED - ARMIES LOST:" + result.get(1));
            updateArmiesAfterBattle(this.getIP(), myTerritory.name(), -myArmies);
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

    public void checkForWin() {
        var myGoal = this.cltPar.getGoalCard();
        var myTerritory = this.getAllTerritories().keySet().stream().filter(p -> p.getFirst().getNickname().equals(this.getNickname())).collect(Collectors.toList());
        switch (myGoal) {
            case DESTROY_PLAYER_1:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(0).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_1, Optional.ofNullable(getClientList().get(0).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_1);
                    });
                }
                break;
            case DESTROY_PLAYER_2:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(1).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_2, Optional.ofNullable(getClientList().get(1).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_2);
                    });
                }
                break;
            case DESTROY_PLAYER_3:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(2).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_3, Optional.ofNullable(getClientList().get(2).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_3);
                    });
                }
                break;
            case DESTROY_PLAYER_4:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(3).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_4, Optional.ofNullable(getClientList().get(3).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_4);
                    });
                }
                break;
            case DESTROY_PLAYER_5:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(4).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_5, Optional.ofNullable(getClientList().get(4).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_5);
                    });
                }
                break;
            case DESTROY_PLAYER_6:
                if (this.getAllTerritories().keySet().stream().noneMatch(p -> p.getFirst().getNickname().equals(getClientList().get(5).getNickname()))) {
                    this.gameSender.clientWin(this.getIP(), Goal.DESTROY_PLAYER_6, Optional.ofNullable(getClientList().get(5).getNickname())).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.DESTROY_PLAYER_6);
                    });
                }
                break;

            case CONQUER_24_TERRITORIES:
                if (myTerritory.stream().filter(p -> p.getFirst().getNickname().equals(this.getNickname())).count() >= 24) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_24_TERRITORIES, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_24_TERRITORIES);
                    });
                }
                break;
            case CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH:
                if (this.getAllTerritories().entrySet().stream().filter(e -> e.getKey().getFirst().getNickname().equals(this.getNickname())).filter(e -> e.getValue() >= 2).count() >= 18) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_18_TERRITORIES_WITH_2_ARMIES_EACH);
                    });
                }
                break;
            case CONQUER_NORTH_AMERICA_AND_AFRICA:
                if (myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() == 9 &&
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.AFRICA)).count() == 6) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_NORTH_AMERICA_AND_AFRICA, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_NORTH_AMERICA_AND_AFRICA);
                    });
                }
                break;
            case CONQUER_ASIA_AND_SOUTH_AMERICA:
                if (myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.ASIA)).count() == 12 &&
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() == 4) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_ASIA_AND_SOUTH_AMERICA, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_ASIA_AND_SOUTH_AMERICA);
                    });
                }
                break;
            case CONQUER_ASIA_AND_AFRICA:
                if (myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.ASIA)).count() == 12 &&
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.AFRICA)).count() == 6) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_ASIA_AND_AFRICA, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_ASIA_AND_AFRICA);
                    });
                }
                break;
            case CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT:
                if (myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.EUROPE)).count() == 7 &&
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() == 4 && (
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.AFRICA)).count() == 6 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() == 9 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.ASIA)).count() == 12 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.OCEANIA)).count() == 4
                )) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_EUROPE_AND_SOUTH_AMERICA_AND_A_THIRD_CONTINENT);
                    });
                }
                break;
            case CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT:
                if (myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.EUROPE)).count() == 7 &&
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.OCEANIA)).count() == 4 && (
                        myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.AFRICA)).count() == 6 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.NORTH_AMERICA)).count() == 9 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.ASIA)).count() == 12 ||
                                myTerritory.stream().filter(p -> p.getSecond().getContinent().equals(Continent.SOUTH_AMERICA)).count() == 4
                )) {
                    this.gameSender.clientWin(this.getIP(), Goal.CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT, Optional.empty()).onSuccess(h -> {
                        this.guiGame.someoneWin(this.getIP(), Goal.CONQUER_EUROPE_AND_OCEANIA_AND_A_THIRD_CONTINENT);
                    });
                }
                break;
        }

    }
}
