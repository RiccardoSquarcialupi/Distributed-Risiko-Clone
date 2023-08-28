package app.manager.contextManager;

import app.common.Client;
import app.manager.Window;

public interface ContextManager {
    void change(Window newClient);
    Client getCurrentClient();
}
