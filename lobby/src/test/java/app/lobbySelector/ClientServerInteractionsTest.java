package app.lobbySelector;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.manager.Window;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static app.Utils.waitForCompletion;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerInteractionsTest {

    @Test
    void testInteractions() throws IOException {
        Launcher.debugInit(Window.BASE);

        // Client request lobbies.
        var crl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).getFilteredLobbies(-1);
        waitForCompletion(crl);
        assertEquals(0, crl.result().bodyAsJsonArray().size());

        // Client create a lobby.
        var ccl = ((LobbySelectorClientImpl) Launcher.getCurrentClient()).createNewLobby("NewLobby", 4);
        waitForCompletion(ccl);
        assertEquals(ManagerClientImpl.class, Launcher.getCurrentClient().getClass());

        // Server knows the new lobby.
        var skl = WebClient.create(Launcher.getVertx()).get(
                5000, Launcher.serverIP, "/server/lobbies/"
        ).send();
        waitForCompletion(skl);
        assertEquals(1, skl.result().bodyAsJsonArray().size());

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
        assertEquals(0, skc.result().bodyAsJsonArray().size());
    }
}
