package app.manager.contextManager;

import app.common.Pair;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextManagerParameters {
    private final String ip;
    private final List<JSONClient> clientList;
    private String nickname;
    private int idLobby;
    private String ipManager;
    private int maxPlayer;
    private final Map<Pair<JSONClient, Territory>, Integer> allTerritories;

    private Goal goalCard;
    private int currentArmiesPlaced;
    private int toPlaceArmies;

    public ContextManagerParameters() throws IOException {
        this.ip = Inet4Address.getLocalHost().getHostAddress();
        this.nickname = "";
        this.idLobby = -1;
        this.ipManager = "";
        this.maxPlayer = -1;
        this.clientList = new ArrayList<>();
        this.allTerritories = new HashMap<>();
        this.currentArmiesPlaced = 0;
        this.toPlaceArmies = 0;
    }

    public String getIp() {
        return ip;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getIdLobby() {
        return idLobby;
    }

    public void setIdLobby(int idLobby) {
        this.idLobby = idLobby;
    }

    public String getIpManager() {
        return ipManager;
    }

    public void setIpManager(String ipManager) {
        this.ipManager = ipManager;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public void addClient(JSONClient client) {
        this.clientList.remove(client);
        this.clientList.add(client);
    }

    public void deleteClient(JSONClient client) {
        this.clientList.remove(client);
    }

    public List<JSONClient> getClientList() {
        return this.clientList;
    }

    public void setClientList(List<JSONClient> clientList) {
        this.clientList.clear();
        this.clientList.addAll(clientList);
    }

    public void resetLobby() {
        this.idLobby = -1;
        this.ipManager = "";
        this.maxPlayer = -1;
        this.clientList.clear();
    }

    public void addTerritory(Territory t) {
        this.allTerritories.put(new Pair<>(new JSONClient(getIp(), getNickname()), t), 0);
    }

    public List<Territory> getMyTerritories() {
        return this.allTerritories.keySet()
                .stream()
                .filter(p -> p.getFirst().getIP().equals(getIp()))
                .map(Pair::getSecond)
                .collect(Collectors.toList());
    }

    public int getNumberPlayerReceivedTerritories() {
        return this.allTerritories.keySet().stream().map(Pair::getFirst).collect(Collectors.toSet()).size();
    }

    public Goal getGoalCard() {
        return this.goalCard;
    }

    public void setGoalCard(Goal goalCard) {
        this.goalCard = goalCard;
    }

    public void setEnemyTerritory(JSONClient client, Territory territory) {
        this.allTerritories.put(new Pair<>(client, territory), 0);
    }

    public void updateEnemyTerritories(JSONClient client, Territory territorySender, Territory territoryReceiver, Integer nArmies) {
        var newArmiesVal = this.allTerritories.get(new Pair<>(client, territoryReceiver)) + nArmies;
        this.allTerritories.put(new Pair<>(client, territoryReceiver), newArmiesVal);
        newArmiesVal = this.allTerritories.get(new Pair<>(client, territorySender)) - nArmies;
        this.allTerritories.put(new Pair<>(client, territorySender), newArmiesVal);
    }

    public void updateEnemyTerritoryWithConqueror(JSONClient winner, JSONClient loser, Territory territory, Integer nArmies) {
        //REMOVE THE TERRITORY FROM THE PREVIOUS OWNER
        this.allTerritories.remove(new Pair<>(loser, territory));
        //ADD THE TERRITORY TO THE NEW OWNER
        this.allTerritories.put(new Pair<>(winner, territory), nArmies);

    }

    public void updateMyTerritory(Territory territorySender, Territory territoryReceiver, Integer nArmies) {
        var jsonClient = this.clientList.stream().filter(c -> c.getIP().equals(getIp())).collect(Collectors.toList()).get(0);
        var newArmiesVal = this.allTerritories.get(new Pair<>(jsonClient, territorySender)) - nArmies;
        this.allTerritories.put(new Pair<>(jsonClient, territorySender), newArmiesVal);
        newArmiesVal = this.allTerritories.get(new Pair<>(jsonClient, territoryReceiver)) + nArmies;
        this.allTerritories.put(new Pair<>(jsonClient, territoryReceiver), newArmiesVal);
    }

    public boolean addArmy(JSONClient clt, String country, Integer deltaArmies) {
        if (this.currentArmiesPlaced < this.toPlaceArmies) {
            var clientPair = this.allTerritories.keySet().stream().filter(p -> p.getSecond().equals(Territory.fromString(country))).collect(Collectors.toList()).get(0);
            var armies = this.getAllTerritories().get(clientPair);
            System.out.println("Armies in " + country + " : " + armies);
            if (armies == null ||
                    armies + deltaArmies < 0) return false;
            this.allTerritories.put(clientPair, armies + deltaArmies);
            this.currentArmiesPlaced += deltaArmies;
        }
        return this.currentArmiesPlaced == this.toPlaceArmies;
    }

    public Map<Pair<JSONClient, Territory>, Integer> getAllTerritories() {
        return this.allTerritories;
    }

    public void setPlaceableArmiesAtStart() {
        this.currentArmiesPlaced = 0;
        this.toPlaceArmies = this.getMyTerritories().size() * 2;
        System.out.println("Armies to deploy: " + this.toPlaceArmies);
    }

    public void setPlaceableArmies(int armies) {
        this.currentArmiesPlaced = 0;
        this.toPlaceArmies = armies;
    }

    public Pair<Integer, Integer> getPlacingState() {
        return new Pair<>(this.currentArmiesPlaced, this.toPlaceArmies);
    }

    public void updateEnemyTerritoryAfterBroadcast(JSONClient clt, Territory country, Integer armies) {
        this.allTerritories.put(new Pair<>(clt, country), armies);
    }
}
