import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;
import static java.lang.Math.*;

class SimPanel extends JPanel {

  // frame stuff
  public static final int FPS = 30;
  public static final int FRAME_TIME = 1000/FPS;

  public static int screenW;
  public static int screenH;
  public static int gridSize;

  private Bridge bridge;

  SimPanel(SimulatorFrame frame, Bridge bridge){

    screenW = frame.getSize().width;
    screenH = frame.getSize().height;
    gridSize = screenW/30;

    this.bridge = bridge;

    // Listener - Esc key to quit
    EscapeKeyListener escapeKeyListener = new EscapeKeyListener(frame);
    this.addKeyListener(escapeKeyListener);

    // // Listener for editing lines
    // LineEditingMouseListener lineEditor = new LineEditingMouseListener(network);
    // this.addMouseListener(lineEditor);
    // this.addMouseMotionListener(lineEditor);

    // JPanel Stuff
    this.setFocusable(true);
    this.setBackground(Color.BLACK);
    this.requestFocusInWindow();

    // Start the game in a separate thread (allows simple frame rate control)
    // the alternate is to delete this and just call repaint() at the end of paintComponent()
    Thread t = new Thread(new Runnable() {public void run(){ animate(); }}); // start the game
    t.start();

  }

  public static int mapX(double x){
    return (int) round(screenW/(2.0*gridSize))*gridSize + (int) round(x*gridSize);
  }

  public static int mapY(double y){
    return (int) round(screenH/(2.0*gridSize))*gridSize - (int) round(y*gridSize);
  }

  /**
   * Updates the game state for each frame
   */
  public void animate(){

    while (true) {

      // update game content
   
      // delay
      try {
        Thread.sleep(FRAME_TIME);
      } catch (Exception exc) {
        System.out.println("Thread Error");
      }

      // repaint request
      this.repaint();
    }
  }

  /**
   * Runs every time the screen is refreshed. Draws all game content on the screen.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g); // required
    setDoubleBuffered(true);

   

    // draw grid
    g.setColor(Color.DARK_GRAY);
    for(int i=0; i<screenW; i+= gridSize){
        g.drawLine(i, 0, i, screenH);
    }
    for(int i=0; i<screenH; i+= gridSize){
        g.drawLine(0, i, screenW, i);
    }

    g.setColor(Color.WHITE);
    g.drawLine(mapX(0), 0,mapX(0),screenH);
    g.drawLine(0, mapY(0), screenW, mapY(0));

    //  bridge.drawHeatMap(g);

    // draw bridge
    bridge.draw(g);
    // bridge.drawDeckOnly(g);
  }
}
