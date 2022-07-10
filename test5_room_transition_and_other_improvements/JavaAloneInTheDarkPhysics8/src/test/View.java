package test;

import java.awt.*;
import javax.swing.*;
import physics.Body;
import physics.World;

/**
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends JPanel {
    
    private PlayerEntity player;
    private World world;
    
    public View() {
        world = new World("/res/test8.obj", 50);
        world.addSensor("3681", -189, 44, 5, Math.toRadians(45));
        world.addSensor("11255", -234, -25, 5, Math.toRadians(180));
        player = new PlayerEntity(world);
        world.addRigidBody(player.getBody());
        world.addRigidBody(new Body("enemy_1", -150, -50, 20));
        world.addRigidBody(new Body("enemy_2", -50,  50, 20));
        world.addRigidBody(new Body("enemy_3",  50,  50, 20));
        world.addRigidBody(new Body("enemy_4",  50, -50, 20));
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
            JFrame frame = new JFrame("2D/3D Player vs Level Walls collision detection test");
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
        //g.scale(0.5, 0.5);
        g.translate(-player.getBody().x + 400, -player.getBody().y + 300);
        //g.translate(400, 300);
        
        player.draw(g);
        world.draw(g, player.getBody());
    }
    
}
