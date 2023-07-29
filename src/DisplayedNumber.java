import java.awt.Graphics;
import java.awt.Color;

/**
 * DisplayedNumber.java
 * A class for displaying numbers on the screen.
 * @author Suyu
 * @version 1.0
 */
public class DisplayedNumber extends DisplayedText{

    // variables
    private int x, y;
    
    /**
     * Creates a new DisplayedNumber with specified number, font size, color, and
     * position
     * 
     * @param number    the number to be displayed
     * @param fontSize  the font size of the number
     * @param textColor the color of the number
     * @param centerX   the x coordinate of the center of the number on the screen
     * @param centerY   the y coordinate of the center of the number on the screen
     */
    public DisplayedNumber(double number, int fontSize, Color textColor, int centerX, int centerY){
        super(Double.toString(number), fontSize, textColor);
        this.x = centerX;
        this.y = centerY;
    }

    public DisplayedNumber(double number, int fontSize, Color textColor, int centerX, int centerY, Color bgColor, int bgBuffer){
        super(Double.toString(number), fontSize, textColor, bgColor, bgBuffer);
        this.x = centerX;
        this.y = centerY;
    }

    /**
     * Changes the number that is displayed by this DisplayedNumber object
     * @param number the new number to be displayed
     */
    public void changeNumber(double number){
        super.changeText(Double.toString(number));
    }

    /**
     * Changes the position of the number displayed by this DisplayedNumber object
     * @param x the new x coordinate of the center of the number on the screen
     * @param y the new y coordinate of the center of the number on the screen
     */
    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this DisplayedNumber on the screen
     * @param g Graphics object to draw with
     */
    public void draw(Graphics g){
        super.draw(g, x, y);
    }
}
