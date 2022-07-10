package scene;

import core.Audio;
import core.CutsceneTest;
import core.Dialog;
import core.Entity;
import core.Keyboard;
import core.Resource;
import core.Scene;
import core.SceneManager;
import core.View;
import static core.View.canvasHeight;
import static core.View.canvasWidth;
import entity.Player;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import math.Vec2;
import math.Vec4;
import physics.Triangle;
import physics.World;
import renderer3d.BackgroundRenderer;
import renderer3d.Camera;
import renderer3d.Renderer;

/**
 * Stage class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Stage extends Scene {

    private Camera camera;
    private Renderer renderer2;
    private BackgroundRenderer backgroundRenderer;

    private boolean showWireframe = true;
    
    public static World world;
    
    private Resource.CameraInfo cameraInfo;
    
    private boolean showDebugPhysics;
    
    private final List<Entity> entities = new ArrayList<>();
    private Player player;
    
    private TeleportInfo nextTeleportInfo = null;
    
    public Stage(SceneManager manager) {
        super("stage", manager);
        createOnce();
    }

    public void setNextTeleportInfo(TeleportInfo nextTeleportInfo) {
        this.nextTeleportInfo = nextTeleportInfo;
    }
    
    public static class TeleportInfo {
        public String triangleId;
        public double x;
        public double y;

        public TeleportInfo(String triangleId, double x, double y) {
            this.triangleId = triangleId;
            this.x = x;
            this.y = y;
        }
    }
            
    
    private void createOnce() {
        backgroundRenderer = new BackgroundRenderer(View.canvasWidth, View.canvasHeight);
        View.view.addMouseMotionListener(backgroundRenderer);
        // 90 = horizontal fov -> convert to vertical fov
        double d = canvasWidth / Math.tan(Math.toRadians(45.0));
        double a = Math.atan(canvasHeight / d);
        
        camera = new Camera("camera", Math.toDegrees(a * 2), canvasWidth / (double) canvasHeight, 0.1, 100.0);
        renderer2 = new Renderer(camera, canvasWidth, canvasHeight, backgroundRenderer.currentDepthBuffer);        
        

        world = new World("/res/test8.obj", 50.0);
        
        // --- test DOORS sensors ---
        world.addSensor("10", -224, -336, 16, Math.toRadians(120), "door", null);
        world.addSensor("17", 0, -668, 16, Math.toRadians(120), "door", null);
        world.addSensor("18", 237, -336, 16, Math.toRadians(120), "door", new TeleportInfo("120", 239, -370));
        world.addSensor("66", 532, -336, 16, Math.toRadians(120), "door", new TeleportInfo("126", 540, -376));
        world.addSensor("78", 1055, -669, 16, Math.toRadians(120), "door", new TeleportInfo("123", 1040, -743));
        world.addSensor("73", 1050, -60, 16, Math.toRadians(120), "door", new TeleportInfo("141", 1055, -15));
        world.addSensor("62", 245, -336, 16, Math.toRadians(120), "door", null);
        world.addSensor("44", -225, -336, 16, Math.toRadians(120), "door", new TeleportInfo("134", -240, -375));
        world.addSensor("40", -651, -336, 16, Math.toRadians(120), "door", new TeleportInfo("132", -659, -375));
        world.addSensor("36", -1116, -59, 16, Math.toRadians(120), "door", new TeleportInfo("128", -1121, -16));
        world.addSensor("37", -1213, -475, 16, Math.toRadians(120), "door", new TeleportInfo("140", -1266, -488));
        world.addSensor("21", 1093, -59, 16, Math.toRadians(120), "door", new TeleportInfo("144", 1095, -20));
        world.addSensor("24", 1089, -668, 16, Math.toRadians(120), "door", new TeleportInfo("146", 1095, -762));
        world.addSensor("26", -1125, -55, 16, Math.toRadians(120), "door", new TeleportInfo("128", -1122, -21));
        world.addSensor("28", -1092, -668, 16, Math.toRadians(120), "door", new TeleportInfo("129", -1099, -741));
        // room 6
        world.addSensor("120", 239, -350, 16, Math.toRadians(120), "door", new TeleportInfo("18", 237, -286));
        // room 7
        world.addSensor("123", 1040, -710, 16, Math.toRadians(120), "door", new TeleportInfo("78", 1040, -660));
        // room 8
        world.addSensor("141", 1055, -50, 16, Math.toRadians(120), "door", new TeleportInfo("73", 1055, -96));
        // room 9
        world.addSensor("126", 540, -344, 16, Math.toRadians(120), "door", new TeleportInfo("66", 532, -310));
        // room 10
        world.addSensor("134", -237, -344, 16, Math.toRadians(120), "door", new TeleportInfo("44", -226, -307));
        // room 11
        world.addSensor("132", -660, -344, 16, Math.toRadians(120), "door", new TeleportInfo("40", -650, -307));
        // room 12
        world.addSensor("128", -1121, -47, 16, Math.toRadians(120), "door", new TeleportInfo("36", -1115, -89));
        // room 13
        world.addSensor("140", -1235, -488, 16, Math.toRadians(120), "door", new TeleportInfo("37", -1200, -475));
        // room 14
        world.addSensor("144", 1095, -47, 16, Math.toRadians(120), "door", new TeleportInfo("21", 1092, -90));
        // room 15
        // room 16 ? bug
        // room 17 ? bug
        // room 18
        world.addSensor("129", -1099, -700, 16, Math.toRadians(120), "door", new TeleportInfo("28", -1090, -650));
        
        // --- test ITEM_ON_FLOOR sensors ---
        world.addSensor("93", -42, 312, 1, Math.toRadians(180), "item_on_floor", null);
        world.addSensor("38", -1035, -563, 1, Math.toRadians(180), "item_on_floor", null);
        
        // --- test ITEM_FRONT sensors ---
        world.addSensor("119", 340, -420, 5, Math.toRadians(30), "item_front", null);
        world.addSensor("128", -1281, 7, 5, Math.toRadians(30), "item_front", null);
        
        // --- test LOOK sensors ---
        world.addSensor("54", 90, -336, 16, Math.toRadians(120), "look", null);

        // create all entities
        entities.add(player = new entity.Player(this, world));
        entities.forEach(entity -> entity.start());        
        
        cameraInfo = Resource.cameras.get(0);
    }
    
    @Override
    public void onEnter() {
        Audio.playMusic("longstrings2high");
        Audio.setMusicVolume(3);
        
        if (nextTeleportInfo != null) {
            player.getBody().x = 0;
            player.getBody().y = 0;
            world.teleport(player.getBody(), nextTeleportInfo.triangleId, nextTeleportInfo.x, nextTeleportInfo.y);            
            nextTeleportInfo = null;
        }
    }

    @Override
    public void onExit() {
        Audio.stopMusic();
    }
    
    @Override
    public void update() {
        entities.forEach(entity -> entity.update());
        Dialog.update();
        world.update();
        
        if (Keyboard.isKeyDown(KeyEvent.VK_T)) {
            world.teleport(player.getBody(), "123", -1, -1);
        }

        if (Keyboard.isKeyDown(KeyEvent.VK_W)) {
            showWireframe = !showWireframe;
        }
        
        if (Keyboard.isKeyDown(KeyEvent.VK_1)) {
            String cameraId = JOptionPane.showInputDialog(View.view, "camera id");
            cameraInfo = Resource.cameras.get(Integer.valueOf(cameraId));
            if (cameraInfo == null) {
                cameraInfo = Resource.cameras.get(0);
            }
        }
        
        if (Keyboard.isKeyDown(KeyEvent.VK_2)) {
            cameraInfo = Resource.cameras.get(1);
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_3)) {
            cameraInfo = Resource.cameras.get(2);
        }
        if (Keyboard.isKeyDown(KeyEvent.VK_4)) {
            cameraInfo = Resource.cameras.get(3);
        }
        
        Triangle currentTriangle = player.getBody().getCurrentTriangle();
        if (currentTriangle != null) {
            Integer cameraId = Resource.changeCameraAreas.get(
                            Integer.parseInt(currentTriangle.getId()));
            
            if (cameraId != null) {
                cameraInfo = Resource.cameras.get(cameraId);
                player.setScale(cameraInfo.playerScale);
            }
            //System.out.println("cameraId:" + cameraId);
        }

        if (Keyboard.isKeyDown(KeyEvent.VK_P)) {
            showDebugPhysics = !showDebugPhysics;
        }        
    }

    private final renderer3d.Triangle faceTmp 
            = new renderer3d.Triangle(new Vec4(), new Vec4(), new Vec4()
                    , new Vec4(), new Vec2(), new Vec2(), new Vec2());



    private final Vec2 sourceZTmp = new Vec2();
    private final Vec2 targetZTmp = new Vec2();
    private final Vec2 dTmp = new Vec2();
    
    @Override
    public void draw(Graphics2D g) {
        double dx = 0;
        double dy = 0;
        double dz = 0;
        
        // y = -1.33109
        sourceZTmp.set(0.0, -1.94023);
        targetZTmp.set(dx, dz);
        dTmp.set(sourceZTmp.x, sourceZTmp.y);
        dTmp.sub(targetZTmp);
        
        // camera conversion:
        // blender x -> -z
        //         y -> -x
        double cameraX = 4.11909;
        double cameraY = 6.78287;
        double cameraZ = -0.902543;
        
        cameraX = cameraInfo.x;
        cameraY = cameraInfo.y;
        cameraZ = cameraInfo.z;
        
        dx = player.getBody().y / 50.0;
        dz = -player.getBody().x / 50.0;
        dy = player.getBody().height / 50.0 + 0.65;
        
        camera.getWorldTransform().setLookAt(
                new Vec4(cameraX, cameraY, cameraZ, 1.0)
                    , new Vec4(dx, dy, dz, 1.0), new Vec4(0.0, 1.0, 0.0, 0.0));

        backgroundRenderer.changeCamera(cameraInfo);
        backgroundRenderer.draw(g, camera.getWorldTransform());

        camera.lookAt(new Vec4(cameraX, cameraY, cameraZ, 1.0)
                                        , new Vec4(dx, dy, dz, 1.0));

        renderer2.getDepthBuffer().clear();

        // draw all entities        
        entities.forEach(
                entity -> entity.draw(backgroundRenderer.getOg2d(), renderer2));
        
        CutsceneTest.draw(backgroundRenderer.getOg2d());
        Dialog.draw(backgroundRenderer.getOg2d());
        g.drawImage(
                backgroundRenderer.getOffscreenImage(), 0, 0, 800, 600, null);
        
        if (showDebugPhysics) {
            g.translate(-player.getBody().x + 400, -player.getBody().y + 300);
            g.drawString("camera id: " + cameraInfo.id,  (int) (player.getBody().x - 100), (int) (player.getBody().y - 100));
            World.drawBody(g, player.getBody());
            world.draw(g, player.getBody());
            
        }        
    }

}
