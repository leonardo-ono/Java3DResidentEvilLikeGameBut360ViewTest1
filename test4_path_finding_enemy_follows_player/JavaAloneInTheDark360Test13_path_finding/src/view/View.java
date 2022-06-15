package view;

import audio.Audio;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import math.Vec2;
import math.Vec4;
import md2parsertest.MD2Obj;
import physics.Player;
import physics.PlayerMonster;
import physics.Triangle;
import physics.World;
import renderer3d.Camera;
import renderer3d.FixDepth;
import renderer3d.Material;
import renderer3d.Renderer;
import view.Resource.CameraInfo;


/**
 */
public class View extends JPanel {
    
    private Camera camera;
    private Renderer renderer2;
    
    private BackgroundRenderer backgroundRenderer;

    private MD2Obj md2Obj;
    private Material material;

    private MD2Obj md2ObjEnemy;
    private Material materialEnemy;
    
    private Keyboard keyboard = new Keyboard();

    private boolean showWireframe = true;
    
    private int canvasWidth = 400;
    private int canvasHeight = 300;
    
    private Player player;
    private PlayerMonster playerMonster;
    private World world;
    
    private CameraInfo cameraInfo;
    
    private boolean showDebugPhysics;
    
    public View() {
        Audio.start();
        Audio.playMusic("psycho");
        
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {
        }
        
        Audio.setMusicVolume(4);
        Audio.playSound("zombie_groan");    
        
        FixDepth.precalculate(canvasWidth, canvasHeight, Math.toRadians(90));
        
        backgroundRenderer = new BackgroundRenderer(canvasWidth, canvasHeight);
        addMouseMotionListener(backgroundRenderer);
        
        try {
            md2Obj = new MD2Obj("/res/doomguy.md2", 0.0355);
            md2ObjEnemy = new MD2Obj("/res/ghoul.md2", 0.0135);
            for (String animationName : md2ObjEnemy.getAnimationNames()) {
                System.out.println(animationName);
            }
            md2ObjEnemy.setAnimation("frame");
            md2Obj.setAnimation("stand");
            
            material = new Material("/res/doomguy.png");
            materialEnemy = new Material("/res/ghoul.png");
            //md2Obj.nextFrame();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }        
        
        world = new World();
        player = new Player(world);
        playerMonster = new PlayerMonster(world);
        
        cameraInfo = Resource.cameras.get(0);
    }

    public void start() {
        
        // 90 = horizontal fov -> convert to vertical fov
        double d = canvasWidth / Math.tan(Math.toRadians(45.0));
        double a = Math.atan(canvasHeight / d);
        
        camera = new Camera("camera", Math.toDegrees(a * 2), canvasWidth / (double) canvasHeight, 0.1, 100.0);
        renderer2 = new Renderer(camera, canvasWidth, canvasHeight, backgroundRenderer.currentDepthBuffer);        
        
        addKeyListener(keyboard);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        update();
        
        draw((Graphics2D) g);
        
//        try {
//            Thread.sleep(1000 / 120);
//        } catch (InterruptedException ex) { }
        
        repaint();
    }
    
    private double direction = 0;
    private double speed = 0.035;
    private double dx = 0;
    private double dz = 0;    
    private double dy = -160.0;    
    private boolean walking = false;
    private double frameRate = 0.1;
    
    public void update() {
        
        world.getLevel().updateTarget(player.getBodyCurrentTriangle());
        playerMonster.setFollow(true);
            
        if (keyboard.isDown(KeyEvent.VK_Q)) {
            CutsceneTest.fadeIn();
            Dialog.show(1, 12, 0, 0, "WHAT THE HELL!!! THIS MONSTER WITHOUT |FACE IS FOLLOWING ME!!");
        }
        if (keyboard.isDown(KeyEvent.VK_W)) {
            CutsceneTest.fadeOut();
        }
        CutsceneTest.update();
        if (CutsceneTest.getCurrentAlpha() > 254) {
            Dialog.update();
        }
        else if (CutsceneTest.getTargetAlpha() == 0 && CutsceneTest.getCurrentAlpha() < 128) {
            Dialog.hide();
        }
        
        if (keyboard.isDown(KeyEvent.VK_W)) {
            showWireframe = !showWireframe;
        }
        
        if (keyboard.isDown(37)) {
            direction-=0.04;
            
            if (!md2Obj.getCurrentAnimationName().equals("run")) {
                md2Obj.setAnimation("run");
                frameRate = 0.12;
            }
        }
        else if (keyboard.isDown(39)) {
            direction+=0.04;

            if (!md2Obj.getCurrentAnimationName().equals("run")) {
                md2Obj.setAnimation("run");
                frameRate = 0.12;
            }
        }
        else if (!walking) {
            if (!md2Obj.getCurrentAnimationName().equals("stand")) {
                md2Obj.setAnimation("stand");
                frameRate = 0.1;
            }
        }
        
        if (walking) {
            dx += speed * Math.cos(direction);
            dz += speed * Math.sin(direction);
        }
        
        if (keyboard.isDown(38) && !walking) {
            md2Obj.setAnimation("run");
            frameRate = 0.12;
            walking = true;
        }
        else if (keyboard.isUp(38) && walking) {
            if (!md2Obj.getCurrentAnimationName().equals("stand")) {
                md2Obj.setAnimation("stand");
                frameRate = 0.1;
            }
            walking = false;
        }
        md2Obj.nextFrame(frameRate);
        md2ObjEnemy.nextFrame(frameRate * 2.5);
        //md2Obj.nextFrame();

        if (!CutsceneTest.isActive()) {
            world.update();
            player.update();
            playerMonster.update();
        }
        
        if (keyboard.isDown(KeyEvent.VK_1)) {
            cameraInfo = Resource.cameras.get(0);
        }
        if (keyboard.isDown(KeyEvent.VK_2)) {
            cameraInfo = Resource.cameras.get(1);
        }
        if (keyboard.isDown(KeyEvent.VK_3)) {
            cameraInfo = Resource.cameras.get(2);
        }
        if (keyboard.isDown(KeyEvent.VK_4)) {
            cameraInfo = Resource.cameras.get(3);
        }
        
        Triangle currentTriangle = player.getBodyCurrentTriangle();
        if (currentTriangle != null) {
            Integer cameraId = Resource.changeCameraAreas.get(Integer.parseInt(currentTriangle.getId()));
            if (cameraId != null) {
                cameraInfo = Resource.cameras.get(cameraId);
            }
        }

        if (keyboard.isDown(KeyEvent.VK_P)) {
            showDebugPhysics = !showDebugPhysics;
        }
    }
    
    private final renderer3d.Triangle faceTmp = new renderer3d.Triangle(new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec2(), new Vec2(), new Vec2());

    private void draw(Graphics2D g) {
        // y = -1.33109
        Vec2 sourceZ = new Vec2(0.0, -1.94023);
        Vec2 targetZ = new Vec2(dx, dz);
        Vec2 d = new Vec2(sourceZ.x, sourceZ.y);
        d.sub(targetZ);
        
        //camera.rx = 0.2 + 0.01 * Math.sin(System.nanoTime() * 0.0000000025) + 0.025 * Math.cos(System.nanoTime() * 0.000000000731); //backgroundRenderer.currentRotationX;
        //camera.ry = -d.getAngle(); //-backgroundRenderer.currentRotationY;
        //camera.update();
        

        //backgroundRenderer.update();
        //backgroundRenderer.currentRotationX = camera.rx;
        //backgroundRenderer.currentRotationY = -camera.ry;
        
        // camera conversion:
        // blender x -> -z
        //         y -> -x
        double cameraX = 4.11909;
        double cameraY = 6.78287;
        double cameraZ = -0.902543;
        
        cameraX = cameraInfo.x;
        cameraY = cameraInfo.y;
        cameraZ = cameraInfo.z;
        
        dx = player.y / 50.0;
        dz = -player.x / 50.0;
        dy = player.height / 50.0 + 0.65;
        
        // ok, why?
        camera.getWorldTransform().setLookAt(new Vec4(cameraX, cameraY, cameraZ, 1.0), new Vec4(dx, dy, dz, 1.0), new Vec4(0.0, 1.0, 0.0, 0.0));
        //camera.getWorldTransform().invert();

        backgroundRenderer.changeCamera(cameraInfo);
        backgroundRenderer.draw(g, camera.getWorldTransform());

        camera.lookAt(new Vec4(cameraX, cameraY, cameraZ, 1.0), new Vec4(dx, dy, dz, 1.0));
        //camera.lookAt(new Vec4(0.0, 0.0, 0.0, 1.0), new Vec4(0, 0, -100.0, 1.0));

        renderer2.getDepthBuffer().clear();

        // draw player
        
        g.setColor(Color.BLACK);
        for (int t = 0; t < md2Obj.getTriangles().length; t++) {
            double[] tva = md2Obj.getTriangleVertex(t, 0);
            faceTmp.a.set(tva[0], tva[2], tva[1], 1.0);
            faceTmp.a.rotateY(player.angle - Math.toRadians(90));
            faceTmp.a.x += dx;
            faceTmp.a.y += dy + 0.4;
            faceTmp.a.z += dz;
            faceTmp.uvA.set(tva[6], 1.0 - tva[7]);
                    
            double[] tvb = md2Obj.getTriangleVertex(t, 1);
            faceTmp.b.set(tvb[0], tvb[2], tvb[1], 1.0);
            faceTmp.b.rotateY(player.angle - Math.toRadians(90));
            faceTmp.b.x += dx;
            faceTmp.b.y += dy + 0.4;
            faceTmp.b.z += dz;
            faceTmp.uvB.set(tva[6], 1.0 - tva[7]);
            
            double[] tvc = md2Obj.getTriangleVertex(t, 2);
            faceTmp.c.set(tvc[0], tvc[2], tvc[1], 1.0);
            faceTmp.c.rotateY(player.angle - Math.toRadians(90));
            faceTmp.c.x += dx;
            faceTmp.c.y += dy + 0.4;
            faceTmp.c.z += dz;
            faceTmp.uvC.set(tva[6], 1.0 - tva[7]);
        
            renderer2.draw(backgroundRenderer.getOg2d(), faceTmp, material);
        }
        
        // draw enemy
        dx = playerMonster.y / 50.0;
        dz = -playerMonster.x / 50.0;
        dy = playerMonster.height / 50.0; // + 0.85;
        
        g.setColor(Color.BLACK);
        for (int t = 0; t < md2ObjEnemy.getTriangles().length; t++) {
            double[] tva = md2ObjEnemy.getTriangleVertex(t, 0);
            faceTmp.a.set(tva[0], tva[2], tva[1], 1.0);
            faceTmp.a.rotateY(playerMonster.angle - Math.toRadians(90));
            faceTmp.a.x += dx;
            faceTmp.a.y += dy;
            faceTmp.a.z += dz;
            faceTmp.uvA.set(tva[6], 1.0 - tva[7]);
                    
            double[] tvb = md2ObjEnemy.getTriangleVertex(t, 1);
            faceTmp.b.set(tvb[0], tvb[2], tvb[1], 1.0);
            faceTmp.b.rotateY(playerMonster.angle - Math.toRadians(90));
            faceTmp.b.x += dx;
            faceTmp.b.y += dy;
            faceTmp.b.z += dz;
            faceTmp.uvB.set(tva[6], 1.0 - tva[7]);
            
            double[] tvc = md2ObjEnemy.getTriangleVertex(t, 2);
            faceTmp.c.set(tvc[0], tvc[2], tvc[1], 1.0);
            faceTmp.c.rotateY(playerMonster.angle - Math.toRadians(90));
            faceTmp.c.x += dx;
            faceTmp.c.y += dy;
            faceTmp.c.z += dz;
            faceTmp.uvC.set(tva[6], 1.0 - tva[7]);

            renderer2.draw(backgroundRenderer.getOg2d(), faceTmp, materialEnemy);
            //renderer.draw(g, faceTmp, true);
        }
        
        CutsceneTest.draw(backgroundRenderer.getOg2d());
        Dialog.draw(backgroundRenderer.getOg2d());
        g.drawImage(backgroundRenderer.getOffscreenImage(), 0, 0, getWidth(), getHeight(), null);
        
        if (showDebugPhysics) {
            g.translate(-player.x + 400, -player.y + 300);
            player.draw(g);
            world.draw(g, playerMonster);     
        }
        
    }
        
    public static void main(String[] args) {
        System.out.println("test");
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            JFrame frame = new JFrame();
            frame.setTitle("'Resident Evil 1'-like game but 360 view test #4 - enemy follows player");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }    
    
}
