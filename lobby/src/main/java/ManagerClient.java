public class ManagerClient extends LobbyClient {
    private int maxPlayer;

    public ManagerClient(int id, String ip, String nickname, int idLobby, int maxPlayer) {
        super(id, ip, nickname, idLobby, id);
        this.maxPlayer = maxPlayer;
    }

    private void addPlayer(BaseClient client){
        super.clientList.add(client);
    }

    private void removePlayer(BaseClient client){
        super.clientList.remove(client);
    }

    private void startGame(){}
}
