package test;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 *
 * @author Leo
 */
public class Keyboard implements KeyListener {
    
    private static boolean[] keydown = new boolean[256];
    
    public static boolean isKeyDown(int keyCode) {
        return keydown[keyCode];
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keydown[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keydown[e.getKeyCode()] = false;
    }
    
}
