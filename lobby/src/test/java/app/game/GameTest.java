package app.game;

import app.Launcher;
import app.lobby.LobbyClientImpl;
import app.lobby.ManagerClientImpl;
import app.lobbySelector.LobbySelectorClientImpl;
import app.manager.Window;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
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
            var cnl = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).createNewLobby("qq", PLAYER);
            waitForCompletion(cnl);
            System.out.println("Lobby created");
        } else {
            // Try to join the lobby.
            System.out.println("Waiting for lobby to be created...");
            Future<HttpResponse<Buffer>> tjl;
            do {
                Thread.sleep(500);
                tjl = ((LobbySelectorClientImpl)Launcher.getCurrentClient()).getFilteredLobbies(PLAYER);
                waitForCompletion(tjl);
            }while(tjl.result().bodyAsJsonArray().isEmpty());
            System.out.println("Lobby created, joining...");

            // Join the lobby.
            var jtl = ((LobbySelectorClientImpl)Launcher.getCurrentClient())
                    .joinLobby(tjl.result().bodyAsJsonArray().getString(0).substring(47,57));
            // \"manager_client_ip\": \"172.20.0.3\",
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

}
