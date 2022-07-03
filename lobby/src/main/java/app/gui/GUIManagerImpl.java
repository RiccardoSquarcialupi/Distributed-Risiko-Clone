package app.gui;

import javax.swing.*;

public class GUIManagerImpl extends JFrame implements GUIManager {
    private GUI currentGUI;

    public GUIManagerImpl(GUI firstGUI) {
        this.currentGUI = firstGUI;
        setContentPane((JPanel)firstGUI);
        pack();
    }

    @Override
    public void open() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    @Override
    public void change(GUI newGUI) {
        this.currentGUI = newGUI;
        setContentPane((JPanel) newGUI);
        setTitle(newGUI.getTitle());
        pack();
    }

    @Override
    public GUI getCurrentGUI() {
        return this.currentGUI;
    }
}
