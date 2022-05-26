package view;

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
import renderer3d.Camera;
import renderer3d.Material;
import renderer3d.Renderer;


/**
 */
public class View extends JPanel {
    
    private Camera camera;
    private Renderer renderer2;
    
    private BackgroundRenderer backgroundRenderer;

    private MD2Obj md2Obj;
    private Material material;
    
    private Keyboard keyboard = new Keyboard();

    private boolean showWireframe = true;
    
    private int canvasWidth = 400;
    private int canvasHeight = 300;
    
    public View() {
        backgroundRenderer = new BackgroundRenderer(canvasWidth, canvasHeight);
        addMouseMotionListener(backgroundRenderer);
        
        try {
            md2Obj = new MD2Obj("/res/doomguy.md2", 0.0225);
            for (String animationName : md2Obj.getAnimationNames()) {
                System.out.println(animationName);
            }
            md2Obj.setAnimation("stand");
            
            material = new Material("/res/doomguy.png");
            //md2Obj.nextFrame();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }        
    }

    public void start() {

        // 90 = horizontal fov -> convert to vertical fov
        double d = canvasWidth / Math.tan(Math.toRadians(45.0));
        double a = Math.atan(canvasHeight / d);
        
        camera = new Camera("camera", Math.toDegrees(a * 2), canvasWidth / (double) canvasHeight, 0.01, 100.0);
        renderer2 = new Renderer(camera, canvasWidth, canvasHeight, backgroundRenderer.currentDepthBuffer);        
        
        addKeyListener(keyboard);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        update();
        
        draw((Graphics2D) g);
        
        try {
            Thread.sleep(1000 / 80);
        } catch (InterruptedException ex) { }
        
        repaint();
    }
    
    private double direction = 0;
    private double speed = 0.035;
    private double dx = 0;
    private double dz = 0;    
    private double dy = 1.025;    
    private boolean walking = false;
    private double frameRate = 0.1;
    
    public void update() {
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
        //md2Obj.nextFrame();
    }
    
    private final renderer3d.Triangle faceTmp = new renderer3d.Triangle(new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec2(), new Vec2(), new Vec2());

    private void draw(Graphics2D g) {
        // y = -1.33109
        Vec2 sourceZ = new Vec2(0.0, -1.94023);
        Vec2 targetZ = new Vec2(dx, dz);
        Vec2 d = new Vec2(sourceZ.x, sourceZ.y);
        d.sub(targetZ);
        
        camera.rx = 0.2 + 0.01 * Math.sin(System.nanoTime() * 0.0000000025) + 0.025 * Math.cos(System.nanoTime() * 0.000000000731); //backgroundRenderer.currentRotationX;
        camera.ry = -d.getAngle(); //-backgroundRenderer.currentRotationY;
        camera.update();

        //backgroundRenderer.update();
        backgroundRenderer.currentRotationX = camera.rx;
        backgroundRenderer.currentRotationY = -camera.ry;
        
        backgroundRenderer.draw(g);

        renderer2.getDepthBuffer().clear();

        // draw player
        g.setColor(Color.BLACK);
        for (int t = 0; t < md2Obj.getTriangles().length; t++) {
            double[] tva = md2Obj.getTriangleVertex(t, 0);
            faceTmp.a.set(tva[0], tva[2], tva[1], 1.0);
            faceTmp.a.rotateY(direction);
            faceTmp.a.x += dx;
            faceTmp.a.y += dy;
            faceTmp.a.z += dz;
            faceTmp.uvA.set(tva[6], 1.0 - tva[7]);
                    
            double[] tvb = md2Obj.getTriangleVertex(t, 1);
            faceTmp.b.set(tvb[0], tvb[2], tvb[1], 1.0);
            faceTmp.b.rotateY(direction);
            faceTmp.b.x += dx;
            faceTmp.b.y += dy;
            faceTmp.b.z += dz;
            faceTmp.uvB.set(tva[6], 1.0 - tva[7]);
            
            double[] tvc = md2Obj.getTriangleVertex(t, 2);
            faceTmp.c.set(tvc[0], tvc[2], tvc[1], 1.0);
            faceTmp.c.rotateY(direction);
            faceTmp.c.x += dx;
            faceTmp.c.y += dy;
            faceTmp.c.z += dz;
            faceTmp.uvC.set(tva[6], 1.0 - tva[7]);

            renderer2.draw(backgroundRenderer.getOg2d(), faceTmp, material);
            //renderer.draw(g, faceTmp, true);
        }
        
        g.drawImage(backgroundRenderer.getOffscreenImage(), 0, 0, getWidth(), getHeight(), null);
    }
        
    public static void main(String[] args) {
        System.out.println("test");
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            JFrame frame = new JFrame();
            frame.setTitle("'Resident Evil 1'-like game but 360 view test #2 - Depth Buffer");
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
