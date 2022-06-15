package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Text renderer class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class TextRenderer {

    public static final Color DEFAULT_FONT_COLOR = Color.WHITE;
    public static final Color DEFAULT_FONT_SHADOW_COLOR = Color.BLACK;

    private static Font FONT;
    private static int textWidth = 10;
    private static int textHeight = 18;
    static {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT
                , TextRenderer.class.getResourceAsStream("/res/ferrum.otf"));

            FONT = font.deriveFont(Font.BOLD, 14.0f);
        } catch (Exception ex) {
            System.exit(-1);
        }
    }
    
    public static void draw(Graphics2D g, String text, int col, int row) {
        draw(g, text, col, row, DEFAULT_FONT_COLOR, DEFAULT_FONT_SHADOW_COLOR);
    }
    
    public static void draw(
            Graphics2D g, String text, int col, int row, Color color) {
        
        g.setFont(FONT);
        g.setColor(color);
        g.drawString(text, col * textWidth, (row + 1) * textHeight);
    }

    public static void draw(Graphics2D g
            , String text, int col, int row, Color color, Color colorShadow) {
        
        g.setFont(FONT);
        g.setColor(colorShadow);
        g.drawString(text, col * textWidth + 0, (row + 1) * textHeight + 0 + 8);
        g.drawString(text, col * textWidth + 1, (row + 1) * textHeight + 0 + 8);
        g.drawString(text, col * textWidth + 0, (row + 1) * textHeight + 1 + 8);
        g.drawString(text, col * textWidth + 1, (row + 1) * textHeight + 1 + 8);
        g.setColor(color);
        g.drawString(text, col * textWidth, (row + 1) * textHeight + 8);
        g.drawString(text, col * textWidth, (row + 1) * textHeight + 8);
    }
    
}