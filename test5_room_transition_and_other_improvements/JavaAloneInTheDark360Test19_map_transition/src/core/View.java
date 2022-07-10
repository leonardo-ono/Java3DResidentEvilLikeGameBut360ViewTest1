package core;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import renderer3d.FixDepth;

/**
 * View class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends Canvas {
    
    public static View view;
    private BufferStrategy bs;
    private boolean running;
    public static final int canvasWidth = 400;
    public static final int canvasHeight = 300;
    private SceneManager sceneManager;
    
    public View() {
    }

    public void start() {
        createBufferStrategy(3);
        bs = getBufferStrategy();
        view = this;
        FixDepth.precalculate(canvasWidth, canvasHeight, Math.toRadians(90));
        sceneManager = new SceneManager();
        Audio.start();
        addKeyListener(new Keyboard());
        new Thread(new MainLoop()).start();
    }

    private class MainLoop implements Runnable{
        @Override
        public void run() {
            running = true;
            long timePerFrame = 1000000000 / 60;
            long unprocessedTime = 0;
            long previousTime = System.nanoTime();
            while (running) {
                long currentTime = System.nanoTime();
                long delta = currentTime - previousTime;
                previousTime = currentTime;
                unprocessedTime += delta;
                while (unprocessedTime >= timePerFrame) {
                    unprocessedTime -= timePerFrame;
                    update();
                }
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                draw(g);
                bs.show();
                g.dispose();
                
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException ex) {
//                }
            }
        }
    }

    private long lastTime = System.nanoTime();
    private long fps;
    private long accumulated;
    
    public void update() {
//        long currentTime = System.nanoTime();
//        long delta = currentTime - lastTime;
//        lastTime = currentTime;
        //System.out.println("delta:" + delta);
        
        sceneManager.update();
        
//        fps++;
//        accumulated += delta;
//        if (accumulated >= 1000000000) {
//            System.out.println("fps:" + fps);
//            accumulated -= 1000000000;
//            fps = 0;
//        }
    }
    
    private void draw(Graphics2D g) {
        sceneManager.draw(g);
    }
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View viewLocal = new View();
            JFrame frame = new JFrame();
            frame.setTitle("'Resident Evil 1'-like game but 360 view test #6 - room transition");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(viewLocal);
            frame.setResizable(false);
            frame.setVisible(true);
            viewLocal.requestFocus();
            viewLocal.start();
        });
    }    
    
}
