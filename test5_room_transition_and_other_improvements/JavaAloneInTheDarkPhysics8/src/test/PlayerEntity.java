package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import physics.Body;
import physics.World;

/**
 * PlayerEntity class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class PlayerEntity {
    
    private final World world;
    private final Body body;
    private Color color;
    private Body lastSensor;
    private World.RaycastResult raycastResult = new World.RaycastResult();
    
    public PlayerEntity(World world) {
        this.world = world;
        body = new Body("player", 0, 0, 20);
        body.angle = Math.toRadians(45);
    }

    public World getWorld() {
        return world;
    }

    public Body getBody() {
        return body;
    }

    public void update() {
        if (Keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
            body.angle -= 0.05;
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) {
            body.angle += 0.05;
        }
        
        body.speed = 0;
        if (Keyboard.isKeyDown(KeyEvent.VK_UP)) {
            body.speed = 3.0;
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_DOWN)) {
            body.speed = -3.0;
        }

        if (world.collideAndSlide(body)) {
            color = Color.RED;
        }
        else {
            color = Color.BLUE;
        }
        
        Body sensor = body.getCollidingSensor();
        if (sensor != null) {
            if (lastSensor != sensor) {
                lastSensor = sensor;
                onSensorEnter(sensor);
            }
        }
        else {
            if (lastSensor != null) {
                onSensorExit(lastSensor);
                lastSensor = null;
            }
        }
    }
    
    private void onSensorEnter(Body sensor) {
        System.out.println("colliding sensor enter " + sensor.getId() + " " + System.nanoTime());
    }

    private void onSensorExit(Body sensor) {
        System.out.println("colliding sensor exit " + sensor.getId() + " " + System.nanoTime());
    }
    
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (body.x - body.radius), (int) (body.y - body.radius)
                            , (int) (2 * body.radius), (int) (2 * body.radius));
        
        double dx = body.radius * Math.cos(body.angle);
        double dy = body.radius * Math.sin(body.angle);        
        g.setColor(Color.BLACK);
        g.drawLine((int) body.x, (int) body.y
                , (int) (body.x + dx), (int) (body.y + dy));
        
        g.drawString("h:" + ((int) body.height + "p:" + ((int) body.x) + "," + ((int) body.y))
                , (int) (body.x + body.radius), (int) body.y);
        
        world.castRay2(body, body.angle, 400, raycastResult);
        
        g.setColor(Color.GREEN);
        g.drawLine((int) raycastResult.source.getX(), (int) raycastResult.source.getY(), (int) raycastResult.hit.getX(), (int) raycastResult.hit.getY());
        
        String result = "";
        result += raycastResult.closestBody != null ? "raycast body:" + raycastResult.closestBody.getId() : "";
        result += raycastResult.closestWall != null ? " wall:" + raycastResult.closestWall.getHash1() : "";
        g.drawString(result , (int) raycastResult.hit.getX(), (int) raycastResult.hit.getY());
    }
    
}
