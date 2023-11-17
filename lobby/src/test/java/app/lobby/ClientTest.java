package app.lobby;

import app.Launcher;
import app.lobby.GUI.GUILobbyManager;
import app.lobbySelector.JSONClient;
import app.lobbySelector.LobbySelectorClient;
import app.lobbySelector.LobbySelectorClientImpl;
import app.manager.Window;
import app.manager.contextManager.ContextManagerParameters;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ClientTest {
    static LobbyClient lobbyClient;
    static ContextManagerParameters cltPar;
    static LobbySelectorClientImpl lobbySelectorClient;

    @BeforeAll
    static void setupParameters() throws IOException {
        cltPar = new ContextManagerParameters();
        cltPar.setNickname("Ricky");
        cltPar.setIdLobby(0);
        cltPar.setIpManager("0.0.0.0");
        cltPar.setMaxPlayer(5);
    }

    @Test
    void testBaseClient() throws UnknownHostException {
        lobbySelectorClient = new LobbySelectorClientImpl(cltPar);
        assertEquals("Ricky", lobbySelectorClient.getNickname());
        assertEquals(Inet4Address.getLocalHost().getHostAddress(), lobbySelectorClient.getIP());
    }

    @Test
    void testLobbyClient() {
        lobbySelectorClient = new LobbyClientImpl(cltPar);
        assertEquals(LobbyClientImpl.class, lobbySelectorClient.getClass());
        ((LobbyClient) lobbySelectorClient).stop();
    }

    @Test
    void testManagerClient() {
        lobbySelectorClient = new ManagerClientImpl(cltPar);
        assertEquals(ManagerClientImpl.class, lobbySelectorClient.getClass());
        ((ManagerClient) lobbySelectorClient).stop();
    }

    void waitForCompletion(Future<?> fut) {
        while (!fut.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        } // Deliberately busy waiting.
        if (fut.failed()) {
            fail(fut.cause());
        }
    }

    @Test
    void testClientServerPartAPI() throws IOException {
        Launcher.debugInit(Window.MANAGER);
        lobbyClient = (LobbyClient) Launcher.getCurrentClient();
        ((ManagerClient)lobbyClient).start();
        Vertx vertx = Vertx.vertx();

        // Client join the lobby.

        var fut = WebClient.create(vertx)
                .post(5001, lobbyClient.getIP(), "/client/lobby/clients")
                .sendJsonObject(JSONClient.fromBase((LobbySelectorClient) lobbyClient).toJson());
        waitForCompletion(fut);
        assertEquals(lobbyClient.getIP(), ((ManagerClientImpl) lobbyClient).getClientList().get(0).getIP());

        // Client asks for clients.

        fut = WebClient.create(vertx)
                .get(5001, lobbyClient.getIP(), "/manager/lobby/clients")
                .send();
        waitForCompletion(fut);
        assertEquals(1, fut.result().bodyAsJsonArray().size());

        // Client leave lobby.

        fut = WebClient.create(vertx)
                .delete(5001, lobbyClient.getIP(), "/client/lobby/clients")
                .sendJsonObject(JSONClient.fromBase((LobbySelectorClient) lobbyClient).toJson());
        waitForCompletion(fut);
        assertEquals(0, ((ManagerClientImpl) lobbyClient).getClientList().size());

        // Stop client.
        ((ManagerClient) lobbyClient).stop();
    }

    @Test
    void testManagerChange() throws IOException {
        Launcher.debugInit(Window.MANAGER);
        lobbyClient = (LobbyClient) Launcher.getCurrentClient();
        ((ManagerClient) lobbyClient).start();
        Vertx vertx = Vertx.vertx();

        // Manager change.

        JsonObject newMan = new JsonObject();
        newMan.put("manager_ip", "255.1.255.1");
        var fut = WebClient.create(vertx)
                .put(5001, lobbyClient.getIP(), "/client/lobby/manager")
                .sendJsonObject(newMan);
        waitForCompletion(fut);
        assertEquals(newMan.getString("manager_ip"), ((ManagerClient) lobbyClient).getIpManager());

        ((ManagerClient) lobbyClient).stop();
    }
}
