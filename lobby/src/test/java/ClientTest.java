import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest{

    static BaseClient client1;
    
    @Test
    void testBaseClient() throws UnknownHostException {
        client1 = new BaseClient(Inet4Address.getLocalHost().getHostAddress(), "Riky");
        assertEquals("Riky",client1.getNickname());
        assertEquals(Inet4Address.getLocalHost().getHostAddress(),client1.getIp());
    }
    @Test
    void testLobbyClient(){
        client1 = new LobbyClient(client1.getIp(), client1.getNickname(), 0, "0.0.0.0");
        assertEquals(LobbyClient.class, client1.getClass());
        ((LobbyClient)client1).stop();
    }
    @Test
    void testManagerClient(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        assertEquals(ManagerClient.class, client1.getClass());
        ((ManagerClient)client1).stop();
    }

    void waitForCompletition(Future<?> fut){
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
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        ((ManagerClient) client1).start();
        Vertx vertx = Vertx.vertx();

        // Client join the lobby.

        var fut = WebClient.create(vertx)
                .post(8080, client1.getIp(), "/client/lobby/clients")
                .sendJsonObject(client1.toJson());
        waitForCompletition(fut);
        assertEquals(client1.getIp(), ((ManagerClient) client1).clientList.get(0).getIp());

        // Client asks for clients.

        fut = WebClient.create(vertx)
                .get(8080, client1.getIp(), "/manager/lobby/clients")
                .send();
        waitForCompletition(fut);
        assertEquals(1, fut.result().bodyAsJsonArray().size());

        // Client leave lobby.

        fut = WebClient.create(vertx)
                .delete(8080, client1.getIp(), "/client/lobby/clients")
                .sendJsonObject(client1.toJson());
        waitForCompletition(fut);
        assertEquals(0, ((ManagerClient) client1).clientList.size());

        // Stop client.
        ((ManagerClient) client1).stop();
    }

    @Test
    void testManagerChange(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        ((ManagerClient) client1).start();
        Vertx vertx = Vertx.vertx();

        // Manager change.

        JsonObject newMan = new JsonObject();
        newMan.put("manager_ip", "255.1.255.1");
        var fut = WebClient.create(vertx)
                .put(8080, client1.getIp(), "/client/lobby/manager")
                .sendJsonObject(newMan);
        waitForCompletition(fut);
        assertEquals(newMan.getString("manager_ip"), ((ManagerClient) client1).getIpManagerClient());

        ((ManagerClient) client1).stop();
    }
}
