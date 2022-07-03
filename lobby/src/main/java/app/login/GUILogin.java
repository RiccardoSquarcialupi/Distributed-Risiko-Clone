package app.login;

import app.Launcher;
import app.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUILogin extends JPanel implements GUI {

    JLabel jlbDescr;
    JTextField jtfName;
    JButton jbtEnter;

    public GUILogin() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.jlbDescr = new JLabel("Enter your name");
        this.jlbDescr.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(this.jlbDescr);

        this.jtfName = new JTextField();
        this.jtfName.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(this.jtfName);

        this.jbtEnter = new JButton("Enter");
        this.jbtEnter.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.jbtEnter.addActionListener(this.onEnter());
        add(this.jbtEnter);
    }

    private ActionListener onEnter(){
        return (e) -> {
            if(jtfName.getText().length() > 0){
                Launcher.userLoginned(jtfName.getText());
            } else {
                jlbDescr.setText(jlbDescr.getText() + "!");
            }
        };
    }

    @Override
    public String getTitle() {
        return "Login";
    }
}
