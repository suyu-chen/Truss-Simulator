import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 * EscapeKeyListener.java 
 * Quits the game when the Esc key is pressed
 * @author Suyu
 * @version 1.0
 */
class EscapeKeyListener implements KeyListener {

    // JFrame to dispose when Esc is pressed
    private JFrame parentFrame;

    /**
     * Creates a new EscapeKeyListener with an associateed JFrame.
     * @param parentFrame the JFrame associated with this EscapeKeyListener.
     */
    EscapeKeyListener(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Detects key presses. Closes the parent frame and exits the program when Esc
     * is pressed.
     * @param e the KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            parentFrame.dispose();
            System.exit(0);
        }
    }

    // Empty methods required for implementation 
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}
