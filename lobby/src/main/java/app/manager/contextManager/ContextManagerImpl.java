package app.manager.contextManager;

import app.lobbySelector.LobbySelectorClientImpl;
import app.common.Client;
import app.game.GameClientImpl;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.login.LoginClient;
import app.manager.Window;

import java.io.IOException;

public class ContextManagerImpl implements ContextManager {
    private Client currentClient;
    private ContextManagerParameters contextManagerParameters;

    public ContextManagerImpl(Window firstClient) throws IOException {
        this.contextManagerParameters = new ContextManagerParameters();

        setClientFromEnum(firstClient);
    }

    @Override
    public void change(Window newClient) {
        setClientFromEnum(newClient);
    }

    private void setClientFromEnum(Window window) {
        if (this.currentClient instanceof LobbyClientImpl) {
            ((LobbyClientImpl) this.currentClient).stop();
        }
        switch (window){
            case LOGIN:
                this.currentClient = new LoginClient(this.contextManagerParameters);
                break;
            case BASE:
                this.currentClient = new LobbySelectorClientImpl(this.contextManagerParameters);
                break;
            case LOBBY:
            case MANAGER:
                this.currentClient = new LobbyClientImpl(this.contextManagerParameters);
                break;
            case GAME:
                this.currentClient = new GameClientImpl(this.contextManagerParameters);
            default:
                throw new RuntimeException("This can't happen");
        }
        if (this.currentClient instanceof LobbyClientImpl) {
            ((LobbyClientImpl) this.currentClient).start();
        }
    }

    @Override
    public Client getCurrentClient() {
        return this.currentClient;
    }
}
