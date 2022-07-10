package core;

import static core.TextRenderer.DEFAULT_FONT_COLOR;
import static core.TextRenderer.DEFAULT_FONT_SHADOW_COLOR;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Dialog class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Dialog {
    
    private static final BufferedImage OFFSCREEN;
    private static final Graphics2D OFFSCREEN_G2D;
    
    private static boolean visible;
    
    private static int col;
    private static int row;
    private static int width;
    private static int height;
    private static int startCol;
    private static int startRow;
    
    private static String text = "";
    private static double textIndex;
    private static int lastTextIndex;
    private static double textSpeed;
    
    private static Color color;
    private static Color colorShadow;
    
    private static long waitTime;
    
    static {
        OFFSCREEN = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        OFFSCREEN_G2D = OFFSCREEN.createGraphics();
    }
            
    public static void update() {
        if (!visible) {
            return;
        }
        if (System.currentTimeMillis() < waitTime) {
            return;
        }
        updateInternal(textIndex + textSpeed);
    }
    
    private static void updateInternal(double textIndexTmp) {
        textIndex = textIndexTmp;
        if ((int) textIndex > text.length() - 1) {
            textIndex = text.length() - 1;
        }
        if ((int) textIndex > lastTextIndex) {
            
            if (text.charAt((int) textIndex) != ' ') {
                Audio.playSoundOnce("beep");
            }
            
            for (int i = lastTextIndex + 1; i <= textIndex; i++) {
                char c = text.charAt(i);
                lastTextIndex = i;
                if (c == '|') {
                    col = startCol;
                    row++;
                    continue;
                }
                if (colorShadow == null) {
                    TextRenderer.draw(
                            OFFSCREEN_G2D, "" + c, col, row, color);
                }
                else {
                    TextRenderer.draw(OFFSCREEN_G2D
                            , "" + c, col, row, color, colorShadow);
                }
                col++;
            }
        }
        
    }
    
    public static void draw(Graphics2D g) {
        if (visible) {
            g.drawImage(OFFSCREEN, 0, 0, null);
        }
    }

    public static void show(int startCol, int startRow, int width, int height
                                , String text, long waitTime) {
        
        show(startCol, startRow, width, height, text
                , DEFAULT_FONT_COLOR, DEFAULT_FONT_SHADOW_COLOR, waitTime);
    }

    public static void show(int startCol, int startRow, int width, int height
                , String text, Color color, Color colorShadow, long wait) {
        
        waitTime = System.currentTimeMillis() + wait;
        
        OFFSCREEN_G2D.setBackground(new Color(0, 0, 0, 0));
        OFFSCREEN_G2D.clearRect(
                0, 0, OFFSCREEN.getWidth(), OFFSCREEN.getHeight());
        
        col = startCol;
        row = startRow;
        Dialog.width = width;
        Dialog.height = height;
        Dialog.startCol = startCol;
        Dialog.startRow = startRow;
        Dialog.text = text;
        textSpeed = 0.3;
        textIndex = 0.0;
        lastTextIndex = -1;
        Dialog.color = color;
        Dialog.colorShadow = colorShadow;
        visible = true;
    }

    public static int getLastTextIndex() {
        return lastTextIndex;
    }

    public static boolean isVisible() {
        return visible;
    }
    
    public static void hide() {
        visible = false;
    }
    
    public static void showAll() {
        textIndex = text.length() - 1;
    }

    public static boolean isFinished() {
        return text.isEmpty() || (int) textIndex == text.length() - 1;
    }

}