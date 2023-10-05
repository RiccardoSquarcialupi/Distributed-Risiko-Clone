package app.login;

import app.Launcher;
import app.manager.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GUILogin extends JPanel implements GUI {

    @Override
    public String getTitle() {
        return "RiSiKo!!! Login Page";
    }

    private final JLabel nameLabel;
    private final JTextField nameField;

    public GUILogin() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x2E3842));

        JLabel titleLabel = new JLabel("Risiko Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0xF7DC6F));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        nameLabel = new JLabel("Enter your username:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(0xECF0F1));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(nameLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(120, 20));
        nameField.setPreferredSize(new Dimension(120, 20));
        centerPanel.add(nameField);

        centerPanel.add(Box.createVerticalStrut(10));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        loginButton.addActionListener(onLogin());
        centerPanel.add(loginButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    private ActionListener onLogin() {
        return e -> {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(Launcher.serverIP, Launcher.serverPort), 5000);
                    ((LoginClient) Launcher.getCurrentClient()).login(username);
                } catch (IOException ignore) {
                    JOptionPane.showMessageDialog(null,
                            "SERVER IS NOT RESPONDING!!!\nMAYBE YOU SHOULD CHECK YOUR INTERNET CONNECTION...",
                            "WARNING",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                nameLabel.setText("Please enter a valid username!");
            }
        };
    }
}
