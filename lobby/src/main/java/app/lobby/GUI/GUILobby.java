package app.lobby.GUI;

import app.manager.gui.GUI;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    @Override
    public String getTitle() {
        return "RiSiKo!!! Lobby Pre Game";
    }

    private DefaultListModel<String> playersListModel;
    private JList<String> playersList;
    private JLabel countLabel;
    private JButton removeButton;

    public GUILobby() {
        setLayout(new BorderLayout());

        playersListModel = new DefaultListModel<>();
        playersList = new JList<>(playersListModel);
        JScrollPane listScrollPane = new JScrollPane(playersList);

        countLabel = new JLabel("Participants: 1");
        removeButton = new JButton("Rimuovi Persona");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(removeButton);

        add(listScrollPane, BorderLayout.CENTER);
        add(countLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
