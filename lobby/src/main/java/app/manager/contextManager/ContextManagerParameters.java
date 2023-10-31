package app.manager.contextManager;

import app.common.Pair;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.*;
import java.util.stream.Collectors;

public class ContextManagerParameters {
    private final String ip;
    private String nickname;
    private int idLobby;
    private String ipManager;
    private int maxPlayer;
    private final List<JSONClient> clientList;

    private Map<Pair<JSONClient,Territory>,Integer> allTerritories;

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
        this.allTerritories.put(new Pair<>(new JSONClient(getIp(),getNickname()),t),0);
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

    public void setGoalCard(Goal goalCard) {
        this.goalCard = goalCard;
    }

    public Goal getGoalCard() {
        return this.goalCard;
    }

    public void setEnemyTerritory(JSONClient client, Territory territories) {
        this.allTerritories.put(new Pair<>(client,territories),0);
    }

    public void updateEnemyTerritories(JSONClient client, Territory territorySender, Territory territoryReceiver, Integer nArmies) {
        var newArmiesVal = this.allTerritories.get(new Pair<>(client,territoryReceiver)) + nArmies;
        this.allTerritories.put(new Pair<>(client,territoryReceiver),newArmiesVal);
        newArmiesVal = this.allTerritories.get(new Pair<>(client,territorySender)) - nArmies;
        this.allTerritories.put(new Pair<>(client,territorySender),newArmiesVal);
    }

    public void updateEnemyTerritoryWithConqueror(JSONClient client, Territory territory, Integer nArmies, String conquerorIp){
        //REMOVE THE TERRITORY FROM THE PREVIOUS OWNER
        this.allTerritories.remove(new Pair<>(client,territory));
        //ADD THE TERRITORY TO THE NEW OWNER
        this.allTerritories.put(new Pair<>((this.getClientList().stream().filter(c -> c.getIP().equals(conquerorIp)).collect(Collectors.toList()).get(0)),territory),nArmies);

    }

    public void updateMyTerritory(Territory territorySender, Territory territoryReceiver, Integer nArmies) {
        var newArmiesVal = this.allTerritories.get(new Pair<>(new JSONClient(getIp(),getNickname()),territorySender)) - nArmies;
        this.allTerritories.put(new Pair<>(new JSONClient(getIp(),getNickname()),territorySender),newArmiesVal);
        newArmiesVal = this.allTerritories.get(new Pair<>(new JSONClient(getIp(),getNickname()),territoryReceiver)) + nArmies;
        this.allTerritories.put(new Pair<>(new JSONClient(getIp(),getNickname()),territoryReceiver),newArmiesVal);
    }

    public boolean addArmy(JSONClient clt, String country) {
        if(this.currentArmiesPlaced < this.toPlaceArmies){
            var cl = new Pair<>(clt, Territory.fromString(country));
            if(this.allTerritories.get(cl) == null) return false;
            this.allTerritories.put(cl, this.allTerritories.get(cl) + 1);
            this.currentArmiesPlaced++;
        }
        return this.currentArmiesPlaced == this.toPlaceArmies;
    }

    public Map<Pair<JSONClient,Territory>,Integer> getAllTerritories() {
        return this.allTerritories;
    }

    public void resetPlaceableArmies() {
        this.currentArmiesPlaced = 0;
        this.toPlaceArmies = this.getMyTerritories().size() * 2;
        System.out.println("Armies to deploy: " + this.toPlaceArmies);
    }

    public Pair<Integer, Integer> getPlacingState(){
        return new Pair<>(this.currentArmiesPlaced, this.toPlaceArmies);
    }
}
