package app.lobby;

import app.base.BaseClient;
import app.base.BaseClientImpl;
import app.manager.client.ClientParameters;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.cli.CLI;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest{

    static BaseClient client1;
    static ClientParameters cltPar;

    @BeforeAll
    static void setupParameters() throws IOException {
        cltPar = new ClientParameters();
        cltPar.setNickname("Ricky");
        cltPar.setIdLobby(0);
        cltPar.setIpManager("0.0.0.0");
        cltPar.setMaxPlayer(5);
    }
    @Test
    void testBaseClient() throws UnknownHostException {
        client1 = new BaseClientImpl(cltPar);
        assertEquals("Ricky",client1.getNickname());
        assertEquals(Inet4Address.getLocalHost().getHostAddress(),client1.getIP());
    }
    @Test
    void testLobbyClient(){
        client1 = new LobbyClientImpl(cltPar);
        assertEquals(LobbyClientImpl.class, client1.getClass());
        ((LobbyClient)client1).stop();
    }
    @Test
    void testManagerClient(){
        client1 = new ManagerClientImpl(cltPar);
        assertEquals(ManagerClientImpl.class, client1.getClass());
        ((ManagerClient)client1).stop();
    }

    void waitForCompletion(Future<?> fut){
        while(!fut.isComplete()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        } // Deliberately busy waiting.
        if(fut.failed()){
            fail();
        }
    }

    @Test
    void testClientServerPartAPI(){
        client1 = new ManagerClientImpl(cltPar);
        ((ManagerClient) client1).start();
        Vertx vertx = Vertx.vertx();

        // Client join the lobby.

        var fut = WebClient.create(vertx)
                .post(8080, client1.getIP(), "/client/lobby/clients")
                .sendJsonObject(client1.toJson());
        waitForCompletion(fut);
        assertEquals(client1.getIP(), ((ManagerClientImpl) client1).getClientList().get(0).getIP());

        // Client asks for clients.

        fut = WebClient.create(vertx)
                .get(8080, client1.getIP(), "/manager/lobby/clients")
                .send();
        waitForCompletion(fut);
        assertEquals(1, fut.result().bodyAsJsonArray().size());

        // Client leave lobby.

        fut = WebClient.create(vertx)
                .delete(8080, client1.getIP(), "/client/lobby/clients")
                .sendJsonObject(client1.toJson());
        waitForCompletion(fut);
        assertEquals(0, ((ManagerClientImpl) client1).getClientList().size());

        // Stop client.
        ((ManagerClient) client1).stop();
    }

    @Test
    void testManagerChange(){
        client1 = new ManagerClientImpl(cltPar);
        ((ManagerClient) client1).start();
        Vertx vertx = Vertx.vertx();

        // Manager change.

        JsonObject newMan = new JsonObject();
        newMan.put("manager_ip", "255.1.255.1");
        var fut = WebClient.create(vertx)
                .put(8080, client1.getIP(), "/client/lobby/manager")
                .sendJsonObject(newMan);
        waitForCompletion(fut);
        assertEquals(newMan.getString("manager_ip"), ((ManagerClient) client1).getIpManager());

        ((ManagerClient) client1).stop();
    }
}
