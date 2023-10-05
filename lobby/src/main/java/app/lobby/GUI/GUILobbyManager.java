package app.lobby.GUI;

import app.Launcher;
import app.lobby.ManagerClient;

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
            if (((ManagerClient) Launcher.getCurrentClient()).getClientList().isEmpty()){
                //System.out.println("No one have joined, i can close the server");
                ((ManagerClient) Launcher.getCurrentClient()).closeLobby();
            }else {
                System.out.println("Someone have joined, i can't close the server. Proceed to change manager.");
                //new manager is the second client in the list, the first is me.
                ((ManagerClient) Launcher.getCurrentClient()).managerClientChange(String.valueOf(((ManagerClient) Launcher.getCurrentClient()).getClientList().get(1).getIP()));
            }
            Launcher.lobbyClosed();
        };
    }
}
