package app.manager.client;

import app.base.BaseClientImpl;
import app.base.GUIBase;
import app.game.GameClientImpl;
import app.lobby.GUILobby;
import app.lobby.GUIManagerGui;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.login.GUILogin;
import app.login.LoginClient;
import app.manager.ClientWindow;

import java.io.IOException;
import java.net.Inet4Address;

public class ClientManagerImpl implements ClientManager{
    private Client currentClient;
    private ClientParameters clientParameters;

    public ClientManagerImpl(ClientWindow firstClient) throws IOException {
        this.clientParameters = new ClientParameters();

        setClientFromEnum(firstClient);
    }

    @Override
    public void change(ClientWindow newClient) {
        setClientFromEnum(newClient);
    }

    private void setClientFromEnum(ClientWindow window) {
        if (this.currentClient instanceof LobbyClientImpl) {
            ((LobbyClientImpl) this.currentClient).stop();
        }
        switch (window){
            case LOGIN:
                this.currentClient = new LoginClient(this.clientParameters);
                break;
            case BASE:
                this.currentClient = new BaseClientImpl(this.clientParameters);
                break;
            case LOBBY:
                this.currentClient = new LobbyClientImpl(this.clientParameters);
                break;
            case MANAGER:
                this.currentClient = new ManagerClientImpl(this.clientParameters);
                break;
            case GAME:
                this.currentClient = new GameClientImpl(this.clientParameters);
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
