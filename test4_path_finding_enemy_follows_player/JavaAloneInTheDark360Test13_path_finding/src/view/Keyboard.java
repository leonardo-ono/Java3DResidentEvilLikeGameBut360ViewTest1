package view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Keyboard class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Keyboard implements KeyListener {
    
    private static boolean[] keysDown = new boolean[256];
    
    public Keyboard() {
    }

    public static void set(Integer keycode, Boolean down) {
        keysDown[keycode] = down;
    }
    
    public static boolean isDown(Integer keycode) {
        return keysDown[keycode];
    }

    public static boolean isUp(Integer keycode) {
        return !keysDown[keycode];
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        set(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        set(e.getKeyCode(), false);
    }
    
}
