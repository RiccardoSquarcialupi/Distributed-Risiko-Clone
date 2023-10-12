package app.lobby.GUI;

import app.Launcher;
import app.lobby.ManagerClient;
import io.vertx.core.Promise;

import java.awt.event.ActionListener;

public class GUILobbyManager extends GUILobby{
    @Override
    public String getTitle() {
        return "RiSiKo!!! Lobby Manager";
    }

    public GUILobbyManager() {
        super();
    }

    @Override
    protected ActionListener onClick() {
        return e -> {
            var currClient = ((ManagerClient) Launcher.getCurrentClient());
            Promise<Void> prm = Promise.promise();
            if (currClient.getClientList().size() == 1){
                //System.out.println("No one have joined, i can close the server");
                currClient.closeLobby().onSuccess(s -> prm.complete());
            }else {
                System.out.println("Someone have joined, i can't close the server. Proceed to change manager.");
                //new manager is the second client in the list, the first is me.
                for (var client : currClient.getClientList()){
                    if(!client.getIP().equals(currClient.getIP())){
                        currClient.managerClientChange(client.getIP()).onSuccess(s -> {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception ex) {System.out.println(ex.getMessage());}
                            currClient.exitLobby().onSuccess(ss ->
                                        prm.complete()
                                );
                            }
                        );
                        break;
                    }
                }
            }
            prm.future().onSuccess(s -> Launcher.lobbyClosed());
        };
    }
}
