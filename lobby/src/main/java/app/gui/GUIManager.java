package app.gui;

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
    void change(GUIWindow newGUI);

    /**
     * Get current GUI java interface.
     */
    GUI getCurrentGUI();
}
