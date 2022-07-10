package entity;

import animation.Animation;
import core.Audio;
import core.Dialog;
import core.Entity;
import core.Keyboard;
import core.Resource;
import core.Scene;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import physics.Body;
import physics.Triangle;
import physics.World;
import renderer3d.Renderer;
import scene.Stage;
import scene.Stage.TeleportInfo;

/**
 * Player class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Player extends Entity<Scene> {

    private World world;
    private Body body;
    private Animation animation;

    public Player(Scene scene, World world) {
        super(scene);
        this.world = world;
        Integer[] a = {0, 84, 86, 85, 83, 82, 113, 111, 112, 110, 114, 79};
        woodenFloorTriangles.addAll(Arrays.asList(a));
    }

    public Body getBody() {
        return body;
    }
    
    @Override
    public void start() {
        body = new Body("player", 0, 0, 25);
        
        animation = new Animation("/res/player/", "player.anim");
        animation.setAction("idle");
    }
    
    private double direction = 0;
    private double speed = 0.035;
    private double dx = 0;
    private double dz = 0;    
    private double dy = -160.0;    
    private boolean walking = false;
    private double aim;
    private long lastFireTime;
    
    private int ammo = 6;
    
    private final Set<Integer> woodenFloorTriangles = new HashSet<>();


    private long lastIteractionTime = 0;
    
    private boolean checkIteraction() {
        if (System.currentTimeMillis() < lastIteractionTime + 1000) {
            return false;
        }
        if (!Keyboard.isKeyDown(KeyEvent.VK_X) 
                || Keyboard.isKeyDown(KeyEvent.VK_Z)) {
            
            return false;
        }
        lastIteractionTime = System.currentTimeMillis();
        switch (iteration) {
            case "DOOR" -> {
                TeleportInfo teleportInfo = (TeleportInfo) iterationSensor.getUserData2();
                if (teleportInfo == null) {
                    Audio.playSoundOnce("door_locked");
                    Dialog.show(1, 13, 0, 0, "This door is locked.", 1000);
                }
                else {
                    ((Stage) getScene()).setNextTeleportInfo(teleportInfo);
                    getScene().getManager().switchTo("room_transition");
                }
                return true;
            }
            case "ITEM_ON_FLOOR" -> {
                Dialog.show(1, 13, 0, 0, "You found an item.", 1000);
                animation.setAction("picking_floor");
                return true;
            }
            case "ITEM_FRONT" -> {
                Dialog.show(1, 13, 0, 0, "You found an item.", 1000);
                animation.setAction("picking_front");
                return true;
            }
            case "LOOK" -> {
                Dialog.show(1, 13, 0, 0, "There is a password written: 1234", 0);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void update() {
        animation.update(1.0 / 60.0);
        if (Keyboard.isKeyDown(KeyEvent.VK_1)) {
            animation.setAction("idle");
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_2)) {
            animation.setAction("walking");
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_3)) {
            animation.setAction("running");
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_4)) {
            animation.setAction("idle_aim_transition");
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_5)) {
            animation.setAction("shoot");
        }

        if (Keyboard.isKeyDown(KeyEvent.VK_R)) {
            Audio.playSoundOnce("pistol_reload");
            ammo = 6;
        }

        if (animation.getCurrentAction().getId().equals("picking_floor")) {
            if (animation.getCurrentFrame() < 55) {
                return;
            }
        }
        if (animation.getCurrentAction().getId().equals("picking_front")) {
            if (animation.getCurrentFrame() < 55) {
                return;
            }
        }

        if (animation.getCurrentAction().getId().equals("shoot")) {
            if (animation.getCurrentFrame() < 15) {
                return;
            }
        }
        if (animation.getCurrentAction().getId().equals("dry_fire")) {
            if (animation.getCurrentFrame() < 3) {
                return;
            }
        }
        
        if (!Dialog.isFinished()) {
            return;
        }
        
        if (checkIteraction()) {
            return;
        }
        
        boolean weaponActivated = Keyboard.isKeyDown(KeyEvent.VK_Z)
                && !Keyboard.isKeyDown(KeyEvent.VK_UP)
                && !Keyboard.isKeyDown(KeyEvent.VK_DOWN)
                && !Keyboard.isKeyDown(KeyEvent.VK_LEFT)
                    && !Keyboard.isKeyDown(KeyEvent.VK_RIGHT);
        
        if (weaponActivated) {
            aim += 1.0;
            if (aim >= 10) {
                aim = 9;
            }
        }
        else {
            aim -= 1.0;
            if (aim < 0) {
                aim = 0;
            }
        }
        
        // System.out.println("aim=" + aim);
        
        if (aim >= 9.0 && Keyboard.isKeyDown(KeyEvent.VK_X) && (System.currentTimeMillis() - lastFireTime > 750)) {
            
            if (ammo > 0) {
                ammo--;
                Audio.playSoundOnce("pistol_shot");
                animation.setAction("shoot");
                lastFireTime = System.currentTimeMillis();
            }
            else {
                Audio.playSoundOnce("pistol_dry_fire");
                animation.setAction("dry_fire");
                lastFireTime = System.currentTimeMillis();
            }
            
            return;
        }
        else if (aim > 0) {
            animation.setAction("idle_aim_transition");
            animation.setCurrentFrame(aim);
            return;
        }
        
        // picking floor
//        if (Keyboard.isKeyDown(KeyEvent.VK_X)) {
//            animation.setAction("picking_floor");
//            return;
//        }
//        if (Keyboard.isKeyDown(KeyEvent.VK_C)) {
//            animation.setAction("picking_front");
//            return;
//        }

        if (Keyboard.isKeyDown(37)) {
            direction-=0.075;
            
            if (!animation.getCurrentAction().getId().equals("running")) {
                animation.setAction("running");
            }
        }
        else if (Keyboard.isKeyDown(39)) {
            direction+=0.075;

            if (!animation.getCurrentAction().getId().equals("running")) {
                animation.setAction("running");
            }
        }
        else if (!walking) {
            if (!animation.getCurrentAction().getId().equals("idle")) {
                animation.setAction("idle");
            }
        }

        if (walking) {
            dx += speed * Math.cos(direction);
            dz += speed * Math.sin(direction);
        }
        
        if (Keyboard.isKeyDown(38) && !walking) {
            animation.setAction("running");
            walking = true;
        }
        else if (Keyboard.isKeyUp(38) && walking) {
            if (!animation.getCurrentAction().getId().equals("idle")) {
                animation.setAction("idle");
            }
            walking = false;
        }
        
        boolean walkActivated = Keyboard.isKeyDown(KeyEvent.VK_SHIFT);
        
        if (animation.getCurrentAction().getId().equals("running") && walkActivated) {
            if (!animation.getCurrentAction().getId().equals("walking")) {
                animation.setAction("walking");
                //Audio.playSound("walking");
            }
        }

        Triangle currentTriangle = body.getCurrentTriangle();
        
        body.angle = direction;
        body.speed = 0;
        boolean moving = Keyboard.isKeyDown(KeyEvent.VK_UP) || Keyboard.isKeyDown(KeyEvent.VK_DOWN);
        
        // test
        boolean isFloorWoodenTest = currentTriangle == null ? false : woodenFloorTriangles.contains(Integer.valueOf(currentTriangle.getId()));
        
        if (animation.getCurrentAction().getId().equals("walking") ) {
            if (moving) body.speed = 1.25;
            if (footPreviousFrame != (int) animation.getCurrentFrame()) {
                footPreviousFrame = (int) animation.getCurrentFrame();
                if (footPreviousFrame == 5 || footPreviousFrame == 11) {
                    
                    if (isFloorWoodenTest) {
                        Audio.playSoundOnce("footstep_wood" + (footSound++ % 2));
                    }
                    else {
                        Audio.playSoundOnce("footstep" + (footSound++ % 6));
                    }
                }
            }
        }
        else if (animation.getCurrentAction().getId().equals("running") ) {
            if (moving) body.speed = 2.25;
            if (footPreviousFrame != (int) animation.getCurrentFrame()) {
                footPreviousFrame = (int) animation.getCurrentFrame();
                if (footPreviousFrame == 3 || footPreviousFrame == 7) {

                    if (isFloorWoodenTest) {
                        Audio.playSoundOnce("footstep_wood" + (footSound++ % 2));
                    }
                    else {
                        Audio.playSoundOnce("footstep" + (footSound++ % 6));
                    }

                }
            }
        }


        if (moving) {
            // test
            Dialog.hide();
        }

        updateBody();
        
        //View.world.getLevel().updateTarget(body.getBodyCurrentTriangle());        
        //System.out.println("body max speed=" + body.getSpeed());
        
    }
    
    private Color color = Color.BLACK;
    private Body lastSensor;
    
    private void updateBody() {
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
    
    private String iteration = "";
    private Body iterationSensor = null;
    
    private void onSensorEnter(Body sensor) {
        System.out.println("colliding sensor enter " + sensor.getId() + " " + System.nanoTime());
        iteration = "" + sensor.getUserData().toString().toUpperCase();
        iterationSensor = sensor;
    }

    private void onSensorExit(Body sensor) {
        System.out.println("colliding sensor exit " + sensor.getId() + " " + System.nanoTime());
        iteration = "";
        iterationSensor = null;
    }
    
    private int footPreviousFrame;
    private int footSound;
    
    @Override
    public void draw(Graphics2D g, Renderer renderer) {
        //for (int ty = -1; ty < 1; ty++) {
        //    for (int tx = -1; tx < 1; tx++) {
                animation.draw(g, renderer, body.y / 50.0, body.height / 50.0, -body.x / 50.0, body.angle + Math.PI);
        //    }
        //}
        
        //g.setColor(Color.WHITE);
        //g.drawString(iteration, 200, 150);
        
        if (iteration != null && !iteration.isEmpty()) {
            BufferedImage icon = Resource.getImage("/res/icon/", iteration.toLowerCase());
            
            // door locked
            if (iteration.toLowerCase().equals("door") && iterationSensor.getUserData2() == null) {
                icon = Resource.getImage("/res/icon/", iteration.toLowerCase() + "_locked");
            }
            
            int dy = (int) (4 * Math.sin(System.nanoTime() * 0.0000000025));
            g.drawImage(icon, 200 - icon.getWidth() / 2, 150 - icon.getHeight() / 2 - 64 + dy, null);
        }
    }

    // TODO
    public void setScale(double s) {
        animation.setScale(s);
    }
    
}
