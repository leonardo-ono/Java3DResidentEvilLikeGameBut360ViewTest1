package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import math.Vec2;
import view.Keyboard;

/**
 *
 * @author Leo
 */
public class PlayerMonster {

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

    private Triangle bodyCurrentTriangle;
    
    public PlayerMonster(World world) {
        this.world = world;
        x = 0; //253;
        y = 0; //135;
        radius = 35.0;
    }

    public Triangle getBodyCurrentTriangle() {
        return bodyCurrentTriangle;
    }

    public void setBodyCurrentTriangle(Triangle bodyCurrentTriangle) {
        this.bodyCurrentTriangle = bodyCurrentTriangle;
    }
    
    public void update() {
        vx = 0;
        vy = 0;
//        
//        if (Keyboard.isDown(KeyEvent.VK_LEFT)) {
//            //vx = -speed;
//            angle -= 0.05;
//        }
//        if (Keyboard.isDown(KeyEvent.VK_RIGHT)) {
//            //vx = speed;
//            angle += 0.05;
//        }
        
        speed = 0;
//        if (Keyboard.isDown(KeyEvent.VK_UP)) {
//            speed = 1.5;
//        }
//        if (Keyboard.isDown(KeyEvent.VK_DOWN)) {
//            speed = -1.5;
//        }
        
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

        if (Keyboard.isDown(KeyEvent.VK_F)) {
            follow = !follow;
        }
        
        if (follow) {
            followTarget();
            followVel.x = targetPoint.x - x;
            followVel.y = targetPoint.y - y;
            //followVel.normalize();
            double targetAngle = Math.atan2(followVel.y, followVel.x);
            if (targetAngle < 0) {
                targetAngle += 2 * Math.PI;
            }
            if (Math.abs((targetAngle - angle)) > Math.PI) {
                targetAngle -= 2 * Math.PI;
            }
            angle = angle + 0.03 * Math.signum(targetAngle - angle);
            x += Math.cos(angle);
            y += Math.sin(angle);
        }
        
    }
    
    private boolean follow;
    private final Vec2 targetPoint = new Vec2();
    private final Vec2 followVel = new Vec2();

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
    
    public void followTarget() {
        Triangle currentTriangle = getBodyCurrentTriangle();
        
        
        Wall targetWall = null;
        Triangle targetTriangle = null;
        int pathDistance = Integer.MAX_VALUE;
        if (currentTriangle != null) {
            boolean collidingWall = true;
            while (collidingWall) {
                if (currentTriangle.getPathDistance() == 0) {
                    follow = false;
                    return;
                }

                for (Wall wall : currentTriangle.getWalls()) {
                    if (!wall.isBlocking()) {
                        if (wall.getNextTriangle().getPathDistance() < pathDistance) {
                            targetTriangle = wall.getNextTriangle();
                            pathDistance = wall.getNextTriangle().getPathDistance();
                            targetWall = wall;
                        }
                    }
                }
                
                if (targetWall != null && targetWall.collides(this)) {
                    currentTriangle = targetWall.getNextTriangle();
                    targetWall = null;
                    targetTriangle = null;
                }
                else {
                    collidingWall = false;
                }
            }
        }
        if (targetWall != null) {
            //targetTriangle.getBaricenter(targetPoint);
            targetWall.getCenter(targetPoint);
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
