
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
        radius = 15;
    }
    
    public void update() {
        vx = 0;
        vy = 0;
        
        if (Keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
            //vx = -speed;
            angle -= 0.1;
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) {
            //vx = speed;
            angle += 0.1;
        }
        
        speed = 0;
        if (Keyboard.isKeyDown(KeyEvent.VK_UP)) {
            speed = 3.0;
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_DOWN)) {
            speed = -3.0;
        }
        
        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
        
        x += vx;
        y += vy;

        if (world.collideAndSlide(this)) {
            color = Color.RED;
        }
        else {
            color = Color.BLUE;
        }
        
    }
    
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
        
        double dx = radius * Math.cos(angle);
        double dy = radius * Math.sin(angle);        
        g.setColor(Color.BLACK);
        g.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
        
        g.drawString("h:" + ((int) height) , (int) (x + radius), (int) y);
        
    }
    
}
