import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;

/**
 * DisplayedText.java
 * Class for text displayed in the game, displays text in Verdana.
 * 
 * @author Suyu
 * @version 1.0
 */
public class DisplayedText {

    // Variables
    private String text;
    private Color textColor;
    private int textWidth;
    private int fontHeight;
    private Font font;
    private Color bgColor;
    private int bgBuffer;

    /**
     * Creates a new DisplayedText object with a specified string, font size, and
     * font color
     * 
     * @param text     the string of text to be displayed
     * @param fontSize the size of the font to the displayed
     * @param color    the color of the font to be displayed
     */
    public DisplayedText(String text, int fontSize, Color color) {
        this.text = text;
        this.textColor = color;
        setFont(fontSize);
        this.bgColor = null;
        this.bgBuffer = 0;
    }

    /**
     * Creates a new DisplayedText object with a specified string, font size, font
     * color and bg
     * 
     * @param text     the string of text to be displayed
     * @param fontSize the size of the font to the displayed
     * @param color    the color of the font to be displayed
     */
    public DisplayedText(String text, int fontSize, Color color, Color bgColor, int bgBuffer) {
        this.text = text;
        this.textColor = color;
        setFont(fontSize);
        this.bgColor = bgColor;
        this.bgBuffer = bgBuffer;
    }

    /**
     * Sets the font of this DisplayedText object to plain Verdana at the
     * specified font size Defaults to a general sans serif font if Verdana is
     * unavailable
     * 
     * @param fontSize the font size in pixels
     */
    private void setFont(int fontSize) {
        try {
            this.font = new Font("Verdana", Font.PLAIN, fontSize);
        } catch (Exception e) {
            this.font = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        }
    }

    /**
     * Changes the string that is displayed by this object
     * 
     * @param text the new string to be displayed
     */
    public void changeText(String text) {
        this.text = text;
    }

    /**
     * Changes the color of the text displayed by this object
     * 
     * @param color the new color to be used
     */
    public void changeColor(Color color) {
        this.textColor = color;
    }

    /**
     * Gets the width of the text displayed by this object in pixels. The draw
     * method of this class must be called at least once before this can be used.
     * 
     * @return the width of the text displayed by this object in pixels.
     */
    public int getWidth() {
        return textWidth;
    }

    /**
     * Gets the height of the text displayed by this object in pixels. The draw
     * method of this class must be called at least once before this can be used.
     * 
     * @return the height of the text displayed by this object in pixels.
     */
    public int getHeight() {
        return fontHeight;
    }

    /**
     * Draws the text of this object onto the screen at a specified position
     * 
     * @param g       the Graphics object for drawing this text
     * @param centerX the x coordinate of the center of this text
     * @param centerY the y coordinate of the center of this text
     */
    public void draw(Graphics g, int centerX, int centerY) {
        // set font & color
        g.setFont(this.font);
        

        // gets a FontMetrics object to determine the width and height of the text in px
        if (textWidth == 0) {
            FontMetrics fontMetrics = g.getFontMetrics();
            textWidth = fontMetrics.stringWidth(text);
            fontHeight = fontMetrics.getAscent();
        }

        g.setColor(bgColor);
        g.fillRect(centerX - textWidth / 2 - bgBuffer, centerY - fontHeight / 2 + fontHeight / 10 - bgBuffer,
                textWidth + 2 * bgBuffer, fontHeight + 2 * bgBuffer);

        g.setColor(textColor);
        g.drawString(text, centerX - textWidth / 2, centerY + fontHeight / 2 - fontHeight / 10);
    }

}
