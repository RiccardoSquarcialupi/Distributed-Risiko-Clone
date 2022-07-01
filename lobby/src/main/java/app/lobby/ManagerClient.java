package app.lobby;

import app.base.BaseClient;
import app.lobby.LobbyClient;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerClient extends LobbyClient {
    private final int maxPlayer;
    private final List<Integer> cards = new ArrayList<>(Stream.iterate(1, n -> n + 1).limit(42).collect(Collectors.toList()));

    public ManagerClient(String ip, String nickname, int idLobby, int maxPlayer) {
        super(ip, nickname, idLobby, ip);
        this.maxPlayer = maxPlayer;
    }

    private void addPlayer(BaseClient client) {
        super.clientList.add(client);
    }

    private void removePlayer(BaseClient client) {
        super.clientList.remove(client);
    }

    private void startGame() {}

    public List<BaseClient> getClientList() {
        return this.clientList;
    }

    public void managerClientChange() {
        this.clientWebClientPart.managerClientChange(JsonObject.mapFrom(this.getIp()), this.clientList);
    }

    public void gameHasStarted() {
        Collections.shuffle(cards);
        for (int i = 0; i < this.maxPlayer; i++) {
            this.clientWebClientPart.gameHasStarted(JsonObject.mapFrom((cards.subList(0, cards.size() / (this.maxPlayer - i)))),this.clientList.get(i).getIp());
            Stream.iterate(0, n -> cards.remove(0)).limit(cards.size() / (this.maxPlayer - i));
        }
    }
}
