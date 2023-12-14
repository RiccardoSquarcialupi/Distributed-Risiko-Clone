package app.lobbySelector.GUI;

import app.Launcher;
import app.lobbySelector.LobbySelectorClient;
import app.manager.gui.GUI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class GUILobbySelector extends JPanel implements GUI, GUILobbySelectorActions {
    private final CardLayout cardLayout;
    private final JPanel cards;
    private final Object[] tableHeader = {"ID", "Server IP", "Players Inside", "Max Players"};
    Object[][] obj = new Object[][]{};
    private boolean isJoinPage = true;
    private JLabel titleLabel;
    private JScrollPane jspLobbies;
    private JTable jtb;
    private JTextField jtfName;
    private JTextField jtfMaxPlayers;
    public GUILobbySelector() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x2E3842));

        JPanel titlePanel = new JPanel();
        titleLabel = new JLabel("Lobby Selector");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(0x2E3842));
        titleLabel.setForeground(new Color(0xF7DC6F));
        add(titlePanel, BorderLayout.NORTH);

        cards = new JPanel();
        cardLayout = new CardLayout();
        cards.setLayout(cardLayout);

        cards.add(createJoinLobbyPanel(), "join");
        cards.add(createCreateLobbyPanel(), "create");

        add(cards, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x2E3842));

        jtb = new JTable(obj, tableHeader);
        jtb.setDefaultEditor(Object.class, null);
        jtb.setCellSelectionEnabled(false);
        jtb.setColumnSelectionAllowed(false);
        jtb.setRowSelectionAllowed(true);
        jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jspLobbies.setViewportView(jtb);

        JButton switchButton = new JButton("Switch");
        switchButton.setFont(new Font("Arial", Font.BOLD, 16));
        switchButton.addActionListener(onSwitch());
        buttonPanel.add(switchButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initially show the join lobby page
        cardLayout.show(cards, "join");
        refreshTable();
    }

    @Override
    public String getTitle() {
        return "RiSiKo!!! Lobby selector";
    }

    private ActionListener onSwitch() {
        return e -> {
            // Toggle between "join" and "create" panels
            if (isJoinPage) {
                cardLayout.show(cards, "create");
                titleLabel.setText("Lobby Creator");
                repaint();
                revalidate();
            } else {
                cardLayout.show(cards, "join");
                titleLabel.setText("Lobby Selector");
                repaint();
                revalidate();
            }
            isJoinPage = !isJoinPage;
        };
    }

    private JPanel createJoinLobbyPanel() {
        JPanel joinPanel = new JPanel(new BorderLayout());
        joinPanel.setBackground(new Color(0x2E3842));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JButton jbtRefresh = new JButton("Refresh");
        jbtRefresh.setFont(new Font("Arial", Font.BOLD, 16));
        jbtRefresh.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbtRefresh.addActionListener(onRefresh());
        leftPanel.add(jbtRefresh);

        JButton jbtJoin = new JButton("Join");
        jbtJoin.setFont(new Font("Arial", Font.BOLD, 16));
        jbtJoin.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbtJoin.addActionListener(onJoin());
        leftPanel.add(jbtJoin);

        JTable lobbyTable = new JTable();
        lobbyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        lobbyTable.setRowSelectionAllowed(true);
        lobbyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jspLobbies = new JScrollPane(lobbyTable);
        jspLobbies.setMaximumSize(new Dimension(400, 200));
        leftPanel.add(jspLobbies);

        joinPanel.add(leftPanel, BorderLayout.WEST);

        return joinPanel;
    }

    private JPanel createCreateLobbyPanel() {
        JPanel createPanel = new JPanel();
        createPanel.setBackground(new Color(0x2E3842));
        createPanel.setLayout(new BoxLayout(createPanel, BoxLayout.Y_AXIS));

        jtfName = new JTextField();
        jtfName.setFont(new Font("Arial", Font.PLAIN, 14));
        setupFormField(createPanel, "Lobby name:", jtfName);

        jtfMaxPlayers = new JTextField("4");
        jtfMaxPlayers.setFont(new Font("Arial", Font.PLAIN, 14));
        setupFormField(createPanel, "Lobby max players:", jtfMaxPlayers);

        JButton jbtCreate = new JButton("Create");
        jbtCreate.setFont(new Font("Arial", Font.BOLD, 16));
        jbtCreate.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbtCreate.addActionListener(onCreate());
        createPanel.add(jbtCreate);

        return createPanel;
    }

    private void setupFormField(JPanel panel, String label, JTextField textField) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.setBackground(new Color(0x2E3842));
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        jLabel.setForeground(new Color(0xECF0F1));
        fieldPanel.add(jLabel);

        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(200, 25));
        fieldPanel.add(textField);

        panel.add(fieldPanel);
    }

    private void refreshTable() {
        ((LobbySelectorClient) Launcher.getCurrentClient()).getLobbies()
                .onSuccess((httpResponse) -> SwingUtilities.invokeLater(() -> {
                    obj = new Object[][]{};
                    ObjectMapper mapper = new ObjectMapper();
                    List<JsonObject> temp;
                    try {
                        temp = mapper.readValue(httpResponse.bodyAsString(), new TypeReference<>() {
                        });
                        obj = temp.stream().map((JsonObject j) ->
                                        new Object[]{
                                                j.getInteger("lobby_id"),
                                                j.getString("manager_client_ip"),
                                                j.getString("players_inside"),
                                                j.getInteger("max_players")})
                                .collect(Collectors.toList())
                                .toArray(obj);
                    } catch (JsonProcessingException e) {
                        System.out.println("Something went wrong when parsing the JSON response from the server");
                        throw new RuntimeException(e);
                    }

                    jtb = new JTable(obj, tableHeader);
                    jtb.setDefaultEditor(Object.class, null);
                    jtb.setCellSelectionEnabled(false);
                    jtb.setColumnSelectionAllowed(false);
                    jtb.setRowSelectionAllowed(true);
                    jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    this.jspLobbies.setViewportView(jtb);
                    SwingUtilities.getWindowAncestor(this).setPreferredSize(new Dimension(470, 300));
                    SwingUtilities.getWindowAncestor(this).pack();
                }))
                .onFailure(System.out::println);
    }


    private ActionListener onRefresh() {
        return (e) -> this.refreshTable();
    }

    private ActionListener onJoin() {
        return (e) -> {
            int selectedRow = jtb.getSelectedRow();
            if (selectedRow != -1) {
                String managerIp = Arrays.stream(obj).map((o) -> (String) o[1]).collect(Collectors.toList()).get(selectedRow);
                ((LobbySelectorClient) Launcher.getCurrentClient()).joinLobby(managerIp).onSuccess(v -> Launcher.lobbyJoinedSuccessfully());
            }
        };
    }

    private ActionListener onCreate() {
        return (e) -> {
            if(!jtfMaxPlayers.getText().isEmpty() && !jtfName.getText().isEmpty()){
                try{
                    System.out.println("Value of max player Right now: "+ jtfMaxPlayers.getText());
                    var maxPlayers = Integer.parseInt(jtfMaxPlayers.getText());
                    if (maxPlayers > 2 && maxPlayers < 7) {
                        ((LobbySelectorClient) Launcher.getCurrentClient()).createNewLobby(
                                jtfName.getText(), maxPlayers).onSuccess(v -> Launcher.lobbyCreatedSuccessfully());
                    }
                }catch(NumberFormatException n){
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Max players must be between 3 and 6 and Name not empty", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Max players must be between 3 and 6 and Name not empty", "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        };
    }
}
