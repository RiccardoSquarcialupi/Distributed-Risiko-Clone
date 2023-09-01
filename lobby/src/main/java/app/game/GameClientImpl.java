package app.game;


import app.game.comunication.GameReceiver;
import app.game.comunication.GameSender;
import app.manager.contextManager.ContextManagerParameters;


public class GameClientImpl implements GameClient {
    private ContextManagerParameters cltPar;
    private GameSender gameSender = new GameSender();
    private GameReceiver gameReceiver = new GameReceiver();

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
    public String getNickname() throws IllegalAccessException {
        return this.cltPar.getNickname();
    }
}
