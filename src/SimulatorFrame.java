import javax.swing.JFrame;
import java.awt.Toolkit;


class SimulatorFrame extends JFrame {

  // Game Screen
  static SimPanel panel;

  /**
   * Creates a new full screen game frame.
   */
  SimulatorFrame(Bridge bridge) {
    super("Truss Simulator");

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Set the size and properties of the game frame
    this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    // this.setUndecorated(true); // Set to true to remove title bar
    this.setResizable(false);

    // Set up the game panel (where we put our graphics)
    panel = new SimPanel(this, bridge);
    this.add(panel);

    this.setFocusable(false); // we will focus on the JPanel
    this.setVisible(true);
    
  }
}
