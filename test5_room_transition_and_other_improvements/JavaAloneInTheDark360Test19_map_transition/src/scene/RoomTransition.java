package scene;

import animation.Animation;
import core.Audio;
import core.Scene;
import core.SceneManager;
import core.View;
import static core.View.canvasHeight;
import static core.View.canvasWidth;
import java.awt.Graphics2D;
import math.Vec4;
import renderer3d.BackgroundRenderer;
import renderer3d.Camera;
import renderer3d.Renderer;

/**
 * RoomTransition class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */

public class RoomTransition extends Scene {
    
    private Camera camera;
    private Renderer renderer2;
    private BackgroundRenderer backgroundRenderer;
    
    private Animation animation;
    private Animation animation2;
    private Animation animation3;
    
    private boolean doorOpenSoundPlayed;
    private boolean doorCloseSoundPlayed;
    
    public RoomTransition(SceneManager manager) {
        super("room_transition", manager);
        createOnce();
    }    

    private void createOnce() {
        backgroundRenderer = new BackgroundRenderer(View.canvasWidth, View.canvasHeight);
        
        // 90 = horizontal fov -> convert to vertical fov
        double d = canvasWidth / Math.tan(Math.toRadians(30.0));
        double a = Math.atan(canvasHeight / d);
        
        camera = new Camera("camera", Math.toDegrees(a * 2), canvasWidth / (double) canvasHeight, 0.1, 100.0);
        renderer2 = new Renderer(camera, canvasWidth, canvasHeight, null);        
        
        animation = new Animation("/res/door/", "door.anim");
        animation2 = new Animation("/res/door/", "door_handle.anim");
        animation3 = new Animation("/res/door/", "door_camera.anim");
    }
    
    @Override
    public void onEnter() {
        animation.setAction("door");
        animation2.setAction("door");
        animation3.setAction("door");
        
        doorOpenSoundPlayed = false;
        doorCloseSoundPlayed = false;
        waitTime = 0;
    }

    @Override
    public void onExit() {
    }
    
    private long waitTime;
    
    @Override
    public void update() {
        animation.update(1.0 / 60.0);
        animation2.update(1.0 / 60.0);
        animation3.update(1.0 / 60.0);
        if (animation.getCurrentFrame() >= 118 && !doorCloseSoundPlayed) {
            doorCloseSoundPlayed = true;
            waitTime = System.currentTimeMillis() + 250;
        }
        else if (animation.getCurrentFrame() >= 118 && System.currentTimeMillis() >= waitTime) {
            Audio.playSoundOnce("door_close");
            getManager().switchTo("stage");
        }
        else if (animation.getCurrentFrame() >= 42 && !doorOpenSoundPlayed) {
            doorOpenSoundPlayed = true;
            Audio.playSoundOnce("door_open");
        }
        //System.out.println("door frame:" + (int) animation.getCurrentFrame());
    }
    
    private final Vec4 vec4Tmp1 = new Vec4();
    private final Vec4 vec4Tmp2 = new Vec4(0, 0, 1, 1.0);
    
    @Override
    public void draw(Graphics2D g) {
        //g.setBackground(Color.BLACK);
        backgroundRenderer.getOg2d().clearRect(0, 0, 800, 600);
        
        double[] vertices = animation3.getCurrentAction().getPositions()[(int) animation3.getCurrentFrame()];
        
        vec4Tmp1.set(vertices[0], vertices[1], -vertices[2] - 0.5, 1.0);
        camera.lookAt(vec4Tmp1, vec4Tmp2);

        renderer2.getDepthBuffer().clear();
        
        animation.draw(backgroundRenderer.getOg2d(), renderer2, 0, 0, -1, 0);
        animation2.draw(backgroundRenderer.getOg2d(), renderer2, 0, 0, -1, 0);
        
        g.drawImage(backgroundRenderer.getOffscreenImage()
                                , 0, 0, 800, 600, 400, 0, 0, 300, null);        
        
        //g.setColor(Color.WHITE);
        //g.drawString("DOOR ANIMATION", 50, 50);
    }
    
}
