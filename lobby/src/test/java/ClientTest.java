import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest{

    static BaseClient client1;

    @BeforeAll
    static void setupClient() throws UnknownHostException {
        client1 = new BaseClient(Inet4Address.getLocalHost().getHostAddress(), "Riky");
    }
    
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
        ((LobbyClient) client1).start();
    }
    @Test
    void testManagerClient(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        assertEquals(ManagerClient.class, client1.getClass());
        ((ManagerClient) client1).start();
    }
    @Test
    void testClientServerPartAPI(){
        client1 = new ManagerClient(client1.getIp(), client1.getNickname(), 0, 5);
        ((ManagerClient) client1).start();
        client1.joinLobby(client1,client1.getIp());
        System.out.println(((ManagerClient) client1).getIp());
        System.out.println(((LobbyClient) client1).clientList);
    }
}
