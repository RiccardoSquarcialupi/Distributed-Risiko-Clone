package app.manager.gui;

import app.manager.Window;

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
    void change(Window newGUI);

    /**
     * Get current GUI java interface.
     */
    GUI getCurrentGUI();
}
