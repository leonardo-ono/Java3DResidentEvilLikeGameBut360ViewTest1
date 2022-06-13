package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import view.Keyboard;

/**
 *
 * @author Leo
 */
public class Player {

    private final World world;
    
    public double x;
    public double y;
    public double height;
    
    public double angle;
    public double speed = 0.001;
    public double vx;
    public double vy;
    public double radius;

    private Color color = Color.BLUE;
            
    public Player(World world) {
        this.world = world;
        x = 0; //253;
        y = 0; //135;
        radius = 25.0;
    }
    
    public void update() {
        vx = 0;
        vy = 0;
        
        if (Keyboard.isDown(KeyEvent.VK_LEFT)) {
            //vx = -speed;
            angle -= 0.05;
        }
        if (Keyboard.isDown(KeyEvent.VK_RIGHT)) {
            //vx = speed;
            angle += 0.05;
        }
        
        speed = 0;
        if (Keyboard.isDown(KeyEvent.VK_UP)) {
            speed = 1.5;
        }
        if (Keyboard.isDown(KeyEvent.VK_DOWN)) {
            speed = -1.5;
        }
        
        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
        
        x += vx;
        y += vy;

        if (world.collideAndSlide(this)) {
            color = Color.MAGENTA;
        }
        else {
            color = Color.GREEN;
        }
        
    }
    
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
        
        double dx = radius * Math.cos(angle);
        double dy = radius * Math.sin(angle);        
        g.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
        
        g.drawString("h:" + ((int) height) , (int) (x + radius), (int) y);
        
    }
    
}
