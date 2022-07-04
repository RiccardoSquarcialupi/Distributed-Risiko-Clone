package app.manager.gui;

import app.manager.ClientWindow;

public interface GUIManager {
    /**
     * Open gui.
     */
    void open();

    /**
     * Close gui.
     */
    void close();

    /**
     * Change gui.
     */
    void change(ClientWindow newGUI);

    /**
     * Get current GUI java interface.
     */
    GUI getCurrentGUI();
}
