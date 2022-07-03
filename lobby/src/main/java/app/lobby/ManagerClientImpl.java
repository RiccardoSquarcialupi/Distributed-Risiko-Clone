package app.lobby;

import app.base.BaseClient;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerClientImpl extends LobbyClientImpl implements ManagerClient {
    private final int maxPlayer;
    private final List<Integer> cards = new ArrayList<>(Stream.iterate(1, n -> n + 1).limit(42).collect(Collectors.toList()));

    public ManagerClientImpl(String ip, String nickname, int idLobby, int maxPlayer) {
        super(ip, nickname, idLobby, ip);
        this.maxPlayer = maxPlayer;
    }

    @Override
    public List<BaseClient> getClientList() {
        return this.clientList;
    }

    @Override
    public void managerClientChange(String newManagerIP) {
        this.clientPart.managerClientChange(JsonObject.mapFrom(newManagerIP), this.clientList);
    }

    @Override
    public void startGame() {
        Collections.shuffle(cards);
        for (int i = 0; i < this.maxPlayer; i++) {
            this.clientPart.gameHasStarted(JsonObject.mapFrom((cards.subList(0, cards.size() / (this.maxPlayer - i)))),this.clientList.get(i).getIp());
            Stream.iterate(0, n -> cards.remove(0)).limit(cards.size() / (this.maxPlayer - i));
        }
    }
}
