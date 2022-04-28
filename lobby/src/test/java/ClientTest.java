import io.vertx.core.Vertx;
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
    }
    @Test
    void testManagerClient(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        assertEquals(ManagerClient.class, client1.getClass());
    }
    @Test
    void testClientServerPartAPI(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        ((ManagerClient) client1).start();
        var fut = WebClient.create(Vertx.vertx())
                .post(8080, client1.getIp(), "/client/lobby/clients")
                .sendJsonObject(client1.toJson())
                .onSuccess(ris -> {
                    //System.out.println(((ManagerClient) client1).clientList);
                    assertEquals(client1.getIp(), ((ManagerClient) client1).clientList.get(0).getIp());
                }).onFailure(ris -> {
                    fail();
                });
        while(!fut.isComplete()){} // Deliberately busy waiting.
        if(fut.failed()){
            fail();
        }
        ((ManagerClient) client1).stop();
    }
}
