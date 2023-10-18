package app.game;


import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.lobbySelector.JSONClient;
import app.manager.contextManager.ContextManagerParameters;

import java.util.List;


public class GameClientImpl implements GameClient {
    private final ContextManagerParameters cltPar;
    private final GameSender gameSender = new GameSender();
    private final GameReceiver gameReceiver = new GameReceiver();

    public GameClientImpl(ContextManagerParameters cltPar) {
        this.cltPar = cltPar;
        gameReceiver.start();
    }

    public void loop() {
        while (gameReceiver.isRunning()) {
            //TODO: STRUCTURE OF THE LOOP
            //TODO: DIVIDE RISIKO GAME LOGIC IN SUB-FUNCTION
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
}
