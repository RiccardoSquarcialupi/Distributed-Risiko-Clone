package app.lobbySelector;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.manager.Window;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ClientServerInteractionsTest {

    @Test
    void testInteractions() throws IOException {
        Launcher.debugInit(Window.BASE);

        // Client request lobbies.
        var crl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).getFilteredLobbies(-1);
        waitForCompletion(crl);
        assertEquals(crl.result().bodyAsJsonArray().size(), 0);

        // Client create a lobby.
        var ccl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).createNewLobby("NewLobby", 4);
        waitForCompletion(ccl);
        assertEquals(Launcher.getCurrentClient().getClass(), LobbyClientImpl.class);

        // Server knows the new lobby.
        var skl = WebClient.create(Launcher.getVertx()).get(
                5000, Launcher.serverIP, "/server/lobbies/"
        ).send();
        waitForCompletion(skl);
        assertEquals(skl.result().bodyAsJsonArray().size(), 1);

        // Client exit the lobby.
        var cel = ((LobbyClientImpl)Launcher.getCurrentClient()).exitLobby();
        waitForCompletion(cel);
        assertEquals(Launcher.getCurrentClient().getClass(), LobbySelectorClientImpl.class);

        // Server knows lobby close.
        var skc = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).getFilteredLobbies(-1);
        waitForCompletion(skc);
        assertEquals(skc.result().bodyAsJsonArray().size(), 0);
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
}
