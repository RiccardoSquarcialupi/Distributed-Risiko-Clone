package app.login;

import app.Launcher;
import app.common.Client;
import app.manager.contextManager.ContextManagerParameters;

public class LoginClient implements Client {
    protected ContextManagerParameters cltPar;
    public LoginClient(ContextManagerParameters cltPar){
        this.cltPar = cltPar;
    }
    @Override
    public String getIP() {
        return this.cltPar.getIp();
    }

    @Override
    public String getNickname() throws IllegalAccessException {
        throw new IllegalAccessException("LoginClient doesn't have a nickname");
    }

    public void login(String nickname) {
        this.cltPar.setNickname(nickname);
        Launcher.userLogged();
    }
}
