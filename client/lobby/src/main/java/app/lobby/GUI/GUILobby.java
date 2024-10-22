package app.lobby.GUI;

import app.Launcher;
import app.lobby.LobbyClient;
import app.lobby.LobbyClientImpl;
import app.lobbySelector.JSONClient;
import app.manager.gui.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

public class GUILobby extends JPanel implements GUI, GUILobbyActions {
    JLabel titleLabel;
    private final JScrollPane jspClient;
    private final JTable clientTable;
    private final JButton startButton;
    private final JLabel jlClient;
    public GUILobby() {

        setLayout(new BorderLayout());
        setBackground(new Color(0x2E3842));

        // ############ TOP #############

        JPanel titlePanel = new JPanel();
        titleLabel = new JLabel("<html>Waiting for other players<br></br>before starting the game...</html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(0x2E3842));
        titleLabel.setForeground(new Color(0xF7DC6F));
        add(titlePanel, BorderLayout.NORTH);

        // ############ MID ##############
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        midPanel.setBackground(new Color(0x2E3842));

        // LEFT MID PANEL
        JPanel leftMidPanel = new JPanel();
        leftMidPanel.setLayout(new BoxLayout(leftMidPanel, BoxLayout.Y_AXIS));
        leftMidPanel.setBackground(new Color(0x2E3842));
        midPanel.add(leftMidPanel);

        // CLIENT LABEL COUNT
        jlClient = new JLabel();
        jlClient.setForeground(new Color(0xF7DC6F));
        leftMidPanel.add(jlClient);

        // LOAD GIF IMAGE
        System.out.println(getAbsoluteCurrentPathOfGif());
        ImageIcon icon = new ImageIcon(getAbsoluteCurrentPathOfGif());
        JLabel loadingLabel = new JLabel(icon);
        midPanel.add(loadingLabel);

        // CLIENT LIST
        clientTable = new JTable(new DefaultTableModel(new Object[]{"IP", "NICKNAME"}, 4));
        clientTable.setFont(new Font("Arial", Font.PLAIN, 14));
        clientTable.setDefaultEditor(Object.class, null);
        clientTable.setCellSelectionEnabled(false);
        clientTable.setColumnSelectionAllowed(false);
        clientTable.setRowSelectionAllowed(this instanceof GUILobbyManager); // TRUE only for MANAGER
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jspClient = new JScrollPane(clientTable);
        jspClient.setMaximumSize(new Dimension(400, 200));
        jspClient.setViewportView(clientTable);
        leftMidPanel.add(jspClient);

        add(midPanel, BorderLayout.CENTER);

        // ############ BOT #############
        JPanel botPanel = new JPanel();
        botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.Y_AXIS));
        botPanel.setBackground(new Color(0x2E3842));

        // START GAME BUTTON (manager)
        startButton = new JButton("Start Game");
        if (this instanceof GUILobbyManager) {
            startButton.setFont(new Font("Arial", Font.BOLD, 16));
            startButton.addActionListener(onStartClick());
            startButton.setEnabled(false);
            botPanel.add(startButton);
        }

        // Exit Button
        JButton exitButton = new JButton("Exit Lobby");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(onExitClick());
        botPanel.add(exitButton);

        add(botPanel, BorderLayout.SOUTH);

        ((LobbyClientImpl) Launcher.getCurrentClient()).broadcastClientIp();

        updateClientList(((LobbyClient) Launcher.getCurrentClient()).getClientList());
    }

    @Override
    public String getTitle() {
        return "RiSiKo!!! Waiting Lobby";
    }

    private String getAbsoluteCurrentPathOfGif() {
        return Paths.get("src/main/java/assets/image/RisikoLoading.gif").toAbsolutePath().toString();
    }

    protected ActionListener onStartClick() {
        return null;
    }

    protected ActionListener onExitClick() {
        return e -> {
            ((LobbyClient) Launcher.getCurrentClient()).exitLobby()
                    .onSuccess(s -> Launcher.lobbyClosed());
        };
    }

    @Override
    public void disableStartButton() {
        SwingUtilities.invokeLater(() -> {
            this.startButton.setEnabled(false);
        });
    }

    @Override
    public void enableStartButton() {
        SwingUtilities.invokeLater(() -> {
            this.startButton.setEnabled(true);
        });
    }

    @Override
    public void updateClientList(java.util.List<JSONClient> clients) {
        SwingUtilities.invokeLater(() -> {
            this.jlClient.setText(
                    "Number of players: " + clients.size() + "/" +
                            ((LobbyClient) Launcher.getCurrentClient()).getLobbyMaxPlayers()
            );
            var model = (DefaultTableModel) (this.clientTable.getModel());
            model.setRowCount(0);
            for (var client : clients) {
                model.addRow(new Object[]{client.getIP(), client.getNickname()});
            }
            try {
                SwingUtilities.getWindowAncestor(this).setPreferredSize(new Dimension(470, 300));
                SwingUtilities.getWindowAncestor(this).pack();
            }catch (Exception ignored){/*Silently ignored for testing purpose*/}
        });

    }
}
