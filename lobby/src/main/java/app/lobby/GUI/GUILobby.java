package app.lobby.GUI;

import app.Launcher;
import app.lobby.ManagerClient;
import app.lobbySelector.LobbySelectorClient;
import app.manager.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    @Override
    public String getTitle() {
        return "RiSiKo!!! Lobby Pre Game";
    }

    private DefaultListModel<String> playersListModel;
    private JList<String> playersList;


    public GUILobby() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x2E3842));

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Please Wait for the game to start...");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(0x2E3842));
        titleLabel.setForeground(new Color(0xF7DC6F));
        add(titlePanel, BorderLayout.NORTH);

        //LOAD GIF IMAGE
        ImageIcon icon = new ImageIcon(getAbsoluteCurrentPathOfGif());
        JLabel loadingLabel = new JLabel(icon);
        add(loadingLabel, BorderLayout.CENTER);

        // Exit Button
        JButton exitButton = new JButton("Exit Lobby");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(onClick());
        add(exitButton, BorderLayout.SOUTH);
    }

    private String getAbsoluteCurrentPathOfGif(){
        Path currRelativePath = Paths.get("");
        return currRelativePath.toAbsolutePath().toString().concat("\\\\src\\\\main\\\\java\\\\assets\\\\image\\\\RisikoLoading.gif");
    }

    private ActionListener onClick() {
        return e -> {
            if ( ((ManagerClient) Launcher.getCurrentClient()).getClientList().isEmpty()){
                System.out.println("No one have joined, i can close the server");
                //TODO: close the server
            }else {
                System.out.println("Someone have joined, i can't close the server");
                //new manager is the first client to have joined
                ((ManagerClient) Launcher.getCurrentClient()).managerClientChange(String.valueOf(((ManagerClient) Launcher.getCurrentClient()).getClientList().get(0)));
            }
            System.exit(0);
        };
    }
}
