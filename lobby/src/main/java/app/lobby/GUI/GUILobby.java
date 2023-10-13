package app.lobby.GUI;

import app.Launcher;
import app.lobby.LobbyClient;
import app.lobby.LobbyClientImpl;
import app.lobbySelector.JSONClient;
import app.manager.gui.GUI;
import io.vertx.core.json.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    @Override
    public String getTitle() {
        return "RiSiKo!!! Waiting Lobby";
    }

    private DefaultListModel<String> playersListModel;
    private JList<String> playersList;
    JLabel titleLabel;

    public GUILobby() {

        setLayout(new BorderLayout());
        setBackground(new Color(0x2E3842));

        JPanel titlePanel = new JPanel();
        titleLabel = new JLabel("<html>Waiting for other players<br></br>before starting the game...</html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(0x2E3842));
        titleLabel.setForeground(new Color(0xF7DC6F));

        add(titlePanel, BorderLayout.NORTH);
        //LOAD GIF IMAGE
        System.out.println(getAbsoluteCurrentPathOfGif());
        ImageIcon icon = new ImageIcon(getAbsoluteCurrentPathOfGif());
        JLabel loadingLabel = new JLabel(icon);
        add(loadingLabel, BorderLayout.CENTER);

        // Exit Button
        JButton exitButton = new JButton("Exit Lobby");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(onClick());
        add(exitButton, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(2000);
                ((LobbyClientImpl) Launcher.getCurrentClient()).broadcastClientIp();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });



    }

    private String getAbsoluteCurrentPathOfGif() {
        return Paths.get("src/main/java/assets/image/RisikoLoading.gif").toAbsolutePath().toString();
    }

    protected ActionListener onClick() {
        return e -> {
            ((LobbyClient) Launcher.getCurrentClient()).exitLobby()
                    .onSuccess(s -> Launcher.lobbyClosed());
        };
    }
}
