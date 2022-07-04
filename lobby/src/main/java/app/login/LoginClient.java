package app.login;

import app.Launcher;
import app.manager.client.Client;
import app.manager.client.ClientManager;
import app.manager.client.ClientManagerImpl;
import app.manager.client.ClientParameters;

public class LoginClient implements Client {
    protected ClientParameters cltPar;
    public LoginClient(ClientParameters cltPar){
        this.cltPar = cltPar;
    }
    @Override
    public String getIP() {
        return this.cltPar.getIp();
    }

    @Override
    public void login(String nickname) {
        this.cltPar.setNickname(nickname);
        Launcher.userLoginned();
    }
}
