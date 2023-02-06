package app.game;

import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.login.LoginClient;
import app.manager.client.ClientParameters;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

public class GameClientImpl extends BaseClientImpl implements GameClient {
    private ClientParameters cltPar;
    public GameClientImpl(ClientParameters cltPar) {
        super(cltPar);
        this.cltPar = cltPar;
    }
}
