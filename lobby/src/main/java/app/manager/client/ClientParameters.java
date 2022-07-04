package app.manager.client;

import app.base.JSONClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class ClientParameters {
    private String ip;
    private String nickname;
    private int idLobby;
    private String ipManager;
    private int maxPlayer;
    private List<JSONClient> clientList;

    public ClientParameters() throws IOException {
        this.ip = Inet4Address.getLocalHost().getHostAddress();
        this.nickname = "";
        this.idLobby = -1;
        this.ipManager = "";
        this.maxPlayer = -1;
        this.clientList = new ArrayList<>();
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
        this.clientList.add(client);
    }

    public void deleteClient(JSONClient client) {
        this.clientList.remove(client);
    }

    public List<JSONClient> getClientList() {
        return this.clientList;
    }
}
