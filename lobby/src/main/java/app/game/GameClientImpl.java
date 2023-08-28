package app.game;


import app.base.BaseClientImpl;
import app.manager.client.ClientParameters;


public class GameClientImpl extends BaseClientImpl implements GameClient{
    private ClientParameters cltPar;
    private GameActionSender gameActionSender = new GameActionSender();
    private GameActionReceiver gameActionReceiver = new GameActionReceiver();
    public GameClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
        gameActionReceiver.start();
    }

    public void loop(){
        while(gameActionReceiver.isRunning()){
            //TODO: STRUCTURE OF THE LOOP
            //TODO: DIVIDE RISIKO GAME LOGIC IN SUB-FUNCTION
            gameActionReceiver.stop();
        }

    }

}
