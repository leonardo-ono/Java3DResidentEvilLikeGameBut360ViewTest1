

import java.awt.*;
import javax.swing.*;

/**
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends JPanel {
    
    private Player player;
    private World world;
    
    public View() {
        world = new World();
        player = new Player(world);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        update();
        
        draw((Graphics2D) g);
        
        try {
            Thread.sleep(1000 / 60);
        } catch (InterruptedException ex) {
        }
        
        repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.addKeyListener(new Keyboard());
            JFrame frame = new JFrame("2D/3D Player vs Level Walls and Floors Collision Detection Test");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(view);
            frame.setVisible(true);
            view.requestFocus();
        });
    } 

    private void update() {
        world.update();
        player.update();
    }

    private void draw(Graphics2D g) {
        g.translate(-player.x + 400, -player.y + 300);
        //g.translate(400, 300);
        
        player.draw(g);
        world.draw(g, player);
    }
    
}
