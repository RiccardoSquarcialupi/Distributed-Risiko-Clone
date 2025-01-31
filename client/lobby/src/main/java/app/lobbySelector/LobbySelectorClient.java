package app.lobbySelector;

import app.common.Client;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public interface LobbySelectorClient extends Client {

    /**
     * Join the lobby identified by his manager IP.
     *
     * @param managerClientIp the manager IP.
     * @return
     */
    Future<String> joinLobby(String managerClientIp);

    /**
     * Returns a Future with a buffer HTTP response to get lobbies.
     *
     * @return the future with lobbies.
     */
    Future<HttpResponse<Buffer>> getLobbies();

    /**
     * Create a new lobby.
     *
     * @param name       name of the lobby.
     * @param maxPlayers maximum players of the lobby.
     * @return
     */
    Future<Void> createNewLobby(String name, int maxPlayers);

    @Override
    String getNickname();
}
