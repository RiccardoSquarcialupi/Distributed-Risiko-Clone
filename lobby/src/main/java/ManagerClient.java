import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerClient extends LobbyClient {
    private int maxPlayer;
    private ArrayList<Integer> cards = new ArrayList<Integer>(Stream.iterate(1, n -> n + 1).limit(42).collect(Collectors.toList()));

    public ManagerClient(int id, String ip, String nickname, int idLobby, int maxPlayer) {
        super(id, ip, nickname, idLobby, id);
        this.maxPlayer = maxPlayer;
    }

    private void addPlayer(BaseClient client) {
        super.clientList.add(client);
    }

    private void removePlayer(BaseClient client) {
        super.clientList.remove(client);
    }

    private void startGame() {
    }

    public List<BaseClient> getClientList() {
        return this.clientList;
    }

    public void managerClientChange() {
        this.clientWebClientPart.managerClientChange(JsonObject.mapFrom(this.getId()), this.clientList);
    }

    public void gameHasStarted(){
        Collections.shuffle(cards);
        for(int i = 0; i < this.maxPlayer; i++){
            this.clientWebClientPart.gameHasStarted(JsonObject.mapFrom((cards.subList(0, cards.size() / (this.maxPlayer - i)))));
            Stream.iterate(0, n -> cards.remove(0)).limit(cards.size() / (this.maxPlayer - i));

        }
    }
}
