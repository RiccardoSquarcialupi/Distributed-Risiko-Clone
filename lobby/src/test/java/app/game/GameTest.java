package app.game;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.lobbySelector.LobbySelectorClientImpl;
import app.manager.Window;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static app.Utils.waitForCompletion;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

public class GameTest {

    static final int PLAYER = 2;

    @BeforeAll
    static void joinGame() throws IOException, InterruptedException {
        Launcher.debugInit(Window.BASE, System.getenv("NICKNAME"));

        // If i'm the one who have to create the lobby.
        if(System.getenv().containsKey("GAME")){
            // Create the new lobby.
            System.out.println("Creating lobby...");
            var cnl = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).createNewLobby("qq", PLAYER).onSuccess(v -> Launcher.lobbyCreatedSuccessfully());
            waitForCompletion(cnl);
            System.out.println("Lobby created");
        } else {
            // Try to join the lobby.
            System.out.println("Waiting for lobby to be created...");
            Future<HttpResponse<Buffer>> tjl;
            do {
                Thread.sleep(500);
                tjl = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).getLobbies();
                waitForCompletion(tjl);
            }while(tjl.result().bodyAsJsonArray().isEmpty());
            System.out.println("Lobby created, joining...");
            Thread.sleep(1000);

            // Join the lobby.
            var jtl = ((LobbySelectorClientImpl)Launcher.getCurrentClient())
                    .joinLobby(tjl.result().bodyAsJsonArray().getString(0).substring(47,59)).onSuccess(v -> Launcher.lobbyJoinedSuccessfully());
            waitForCompletion(jtl);
            System.out.println("Lobby joined");
        }

        // Wait for people to join.
        System.out.println("Wait for people to join");
        try{
            while(((LobbyClientImpl)Launcher.getCurrentClient()).getClientList().size() !=
                    ((LobbyClientImpl)Launcher.getCurrentClient()).getLobbyMaxPlayers()){
                Thread.sleep(500);
            }
        }catch (Exception ignored){}

        Thread.sleep(1500);
        Thread.sleep(1500);
        Thread.sleep(1500);

        // Start the game.
        if(System.getenv().containsKey("GAME")){
            // Make game start.
            var mgs = ((ManagerClientImpl)Launcher.getCurrentClient()).startGame();
            waitForCompletion(mgs);
            System.out.println("Game started");
        } else {
            // Wait for game to start.
            try{
                Thread.sleep(4000);
            } catch (Exception ignored){}
        }

        // Wait for territories.
        Thread.sleep(5000);
    }

    @Test
    void testJoinedCorrectly(){
        assertEquals(GameClientImpl.class, Launcher.getCurrentClient().getClass());
    }

    @Test
    void testDiceThrow() throws InterruptedException {
        if(!System.getenv().containsKey("GAME")){
            Thread.sleep(2000);
        }

        // Send dice throw.
        var sdt = ((GameClientImpl)Launcher.getCurrentClient()).throwDices(2);
        waitForCompletion(sdt);
        assertEquals(2, sdt.result().size());

        if(System.getenv().containsKey("GAME")){
            Thread.sleep(7000);
        }
    }

}
