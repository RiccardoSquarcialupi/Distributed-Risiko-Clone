package app.manager.client;

import app.base.BaseClient;
import app.manager.ClientWindow;

public interface ClientManager {
    void change(ClientWindow newClient);
    Client getCurrentClient();
}
