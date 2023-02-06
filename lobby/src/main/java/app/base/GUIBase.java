package app.base;

import app.Launcher;
import app.manager.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUIBase extends JPanel implements GUI, GUIBaseActions{
    @Override
    public String getTitle() {
        return "Lobby selector";
    }

    JPanel leftPanel;
    JPanel rightPanel;

    JButton jbtRefresh;
    JButton jbtJoin;
    JScrollPane jspLobbies;

    JLabel jblName;
    JTextField jtfName;
    JLabel jblMaxPlayers;
    JTextField jtfMaxPlayers;
    JButton jbtCreate;

    public GUIBase() {
        setLayout(new FlowLayout());

        this.leftPanel = new JPanel();
        this.leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

        this.jbtRefresh = new JButton("Refresh");
        this.jbtRefresh.addActionListener(this.onRefresh());
        this.leftPanel.add(this.jbtRefresh);

        this.jbtJoin = new JButton("Join");
        this.jbtJoin.addActionListener(this.onJoin());
        this.leftPanel.add(this.jbtJoin);

        this.jspLobbies = new JScrollPane();
        this.leftPanel.add(this.jspLobbies);

        add(this.leftPanel);

        this.rightPanel = new JPanel();
        this.rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

        this.jblName = new JLabel("Lobby name:");
        this.rightPanel.add(this.jblName);

        this.jtfName = new JTextField();
        this.rightPanel.add(this.jtfName);

        this.jblMaxPlayers = new JLabel("Lobby max players:");
        this.rightPanel.add(this.jblMaxPlayers);

        this.jtfMaxPlayers = new JTextField();
        this.jtfMaxPlayers.setText("4");
        this.rightPanel.add(this.jtfMaxPlayers);

        this.jbtCreate = new JButton("Create");
        this.jbtCreate.addActionListener(this.onCreate());
        this.rightPanel.add(this.jbtCreate);

        add(this.rightPanel);

        this.refreshTable();
    }

    private void refreshTable() {
        ((BaseClient)Launcher.getCurrentClient()).getFilteredLobbies(Integer.parseInt(this.jtfMaxPlayers.getText()))
                .onSuccess((httpResponse) -> SwingUtilities.invokeLater(() -> {
                    Object[] tableHeader = {"Name", "ID", "Manager", "Slots"};
                    Object[][] obj = new Object[][]{
                            {"name", "id", "manager", "4/5"},
                            {"eman", "di", "reganam", "5/4"}};
                    System.out.println(httpResponse.body().toJson());
                    JTable jtb = new JTable(obj, tableHeader);
                    jtb.setDefaultEditor(Object.class, null);
                    jtb.setCellSelectionEnabled(false);
                    jtb.setColumnSelectionAllowed(false);
                    jtb.setRowSelectionAllowed(true);
                    jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    this.jspLobbies.setViewportView(jtb);
                    ((JFrame) SwingUtilities.getWindowAncestor(this)).pack();
                }))
                .onFailure(System.out::println);
    }

    private ActionListener onRefresh() {
        return (e) -> this.refreshTable();
    }

    private ActionListener onJoin() {
        return (e) -> {

        };
    }

    private ActionListener onCreate() {
        return (e) -> {
            ((BaseClient)Launcher.getCurrentClient()).createNewLobby(
                    this.jtfName.getText(), Integer.parseInt(this.jtfMaxPlayers.getText()));
        };
    }
}
