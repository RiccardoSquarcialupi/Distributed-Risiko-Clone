package app.manager.contextManager;

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
    private String nickname;
    private int idLobby;
    private String ipManager;
    private int maxPlayer;
    private final List<JSONClient> clientList;
    private List<Territory> myTerritories;

    private Map<Map<JSONClient,Territory>,Integer> enemyTerritories;

    private Goal goalCard;

    public ContextManagerParameters() throws IOException {
        this.ip = Inet4Address.getLocalHost().getHostAddress();
        this.nickname = "";
        this.idLobby = -1;
        this.ipManager = "";
        this.maxPlayer = -1;
        this.clientList = new ArrayList<>();
        this.myTerritories = new ArrayList<>();
        this.enemyTerritories = new HashMap<>();
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
        this.myTerritories.add(t);
    }

    public List<Territory> getMyTerritories() {
        return this.myTerritories;
    }

    public void setGoalCard(Goal goalCard) {
        this.goalCard = goalCard;
    }

    public Goal getGoalCard() {
        return this.goalCard;
    }

    public void setEnemyTerritory(JSONClient client, Territory territories, Integer nArmies) {
        this.enemyTerritories.put(Map.of(client,territories),nArmies);
    }

    public void updateEnemyTerritory(JSONClient client,Territory territories, Integer nArmies) {
        var newArmiesVal = this.enemyTerritories.get(Map.of(client,territories)) + nArmies;
        this.enemyTerritories.put(Map.of(client,territories),newArmiesVal);
    }

    public void updateEnemyTerritoryWithConqueror(JSONClient client,Territory territories, Integer nArmies,String conquerorIp){
        //REMOVE THE TERRITORY FROM THE PREVIOUS OWNER
        this.enemyTerritories.remove(Map.of(client,territories));
        //ADD THE TERRITORY TO THE NEW OWNER
        //CHECK IF IS MYSELF OTHERWISE ADD TO THE ENEMY LIST
        if(this.ip.equals(conquerorIp)){
            this.myTerritories.add(territories);
        }else{
            this.enemyTerritories.put(Map.of((this.getClientList().stream().filter(c -> c.getIP().equals(conquerorIp)).collect(Collectors.toList()).get(0)),territories),nArmies);
        }
    }
}
