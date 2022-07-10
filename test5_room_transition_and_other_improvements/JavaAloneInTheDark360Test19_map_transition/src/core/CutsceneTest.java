package core;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Leo
 */
public class CutsceneTest {

    private static int targetAlpha = 0;
    private static int currentAlpha = 0;
    
    private static Color[] alphas = new Color[256];
    
    static {
        for (int i = 0; i < 256; i++) {
            alphas[i] = new Color(0, 0, 0, i);
        }
    }

    public static int getCurrentAlpha() {
        return currentAlpha;
    }

    public static int getTargetAlpha() {
        return targetAlpha;
    }
    
    public static void update() {
        int dif = targetAlpha - currentAlpha;
        if (dif == 0) return;
        currentAlpha += (int) (5 * Math.signum(dif));
        if (currentAlpha < 0) currentAlpha = 0;
        if (currentAlpha > 255) currentAlpha = 255;
    }
    
    public static void draw(Graphics2D g) {
        if (currentAlpha == 0) return;
        g.setColor(alphas[currentAlpha]);
        g.fillRect(0, 0, 400, 35);
        g.fillRect(0, 265, 400, 35);
    }
    
    public static void fadeIn() {
        targetAlpha = 255;
    }

    public static void fadeOut() {
        targetAlpha = 0;
    }
    
    public static boolean isActive() {
        return currentAlpha > 0;
    }
    
}
