package app.lobbySelector;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.manager.Window;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static app.Utils.waitForCompletion;
import static org.junit.jupiter.api.Assertions.*;

public class ClientServerInteractionsTest {

    @Test
    void testInteractions() throws IOException, InterruptedException {
        Launcher.debugInit(Window.BASE);

        // Client request lobbies.
        var crl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).getFilteredLobbies(-1);
        waitForCompletion(crl);

        // Client create a lobby.
        var ccl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).createNewLobby("NewLobby", 4);
        waitForCompletion(ccl);
        assertEquals(ManagerClientImpl.class, Launcher.getCurrentClient().getClass());

        // Server knows the new lobby.
        var skl = WebClient.create(Launcher.getVertx()).get(
                5000, Launcher.serverIP, "/server/lobbies/"
        ).send();
        waitForCompletion(skl);
        assertFalse(skl.result().bodyAsJsonArray().isEmpty());
        int lobbyCount = skl.result().bodyAsJsonArray().size();

        //Thread.sleep(5000);

        // Client exit the lobby.
        var cel = ((LobbyClientImpl)Launcher.getCurrentClient()).exitLobby();
        waitForCompletion(cel);
        var slc = ((ManagerClientImpl)Launcher.getCurrentClient()).closeLobby();
        waitForCompletion(slc);
        Launcher.lobbyClosed();
        assertEquals(LobbySelectorClientImpl.class, Launcher.getCurrentClient().getClass());

        // Server knows lobby close.
        var skc = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).getFilteredLobbies(-1);
        waitForCompletion(skc);
        assertTrue(skc.result().bodyAsJsonArray().size() < lobbyCount);
    }
}
