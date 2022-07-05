package app.base;

import app.manager.client.Client;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

import java.util.List;

public interface BaseClient extends Client {
    /**
     * Returns the nickname of the user.
     * @return user nickname.
     */
    String getNickname();

    /**
     * Join the lobby identified by his manager IP.
     * @param managerClientIp the manager IP.
     */
    void joinLobby(String managerClientIp);

    /**
     * Returns a Future with a buffer HTTP response to get lobbies.
     * @param maxPlayers filter on number of players.
     * @return the future with lobbies.
     */
    Future<HttpResponse<Buffer>> getFilteredLobbies(int maxPlayers);

    /**
     * Create a new lobby.
     * @param name name of the lobby.
     * @param maxPlayers maximum players of the lobby.
     */
    void createNewLobby(String name, int maxPlayers);
}
