package app.lobby.GUI;

import app.manager.gui.GUI;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    @Override
    public String getTitle() {
        return "RiSiKo!!! Lobby Pre Game";
    }

    private JLabel loadingLabel;
    private DefaultListModel<String> playersListModel;
    private JList<String> playersList;
    private JButton exitButton;


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


        // Loading Image
        ImageIcon imgIcon = new ImageIcon("/assets/image/RisikoLoading.gif");
        loadingLabel = new JLabel(imgIcon);
        add(loadingLabel, BorderLayout.CENTER);


        // Players List
        /*playersListModel = new DefaultListModel<>();
        playersList = new JList<>(playersListModel);
        JScrollPane playersScrollPane = new JScrollPane(playersList);
        add(playersScrollPane, BorderLayout.NORTH);*/

        // Exit Button
        exitButton = new JButton("Exit Lobby");
        add(exitButton, BorderLayout.SOUTH);
    }
}
