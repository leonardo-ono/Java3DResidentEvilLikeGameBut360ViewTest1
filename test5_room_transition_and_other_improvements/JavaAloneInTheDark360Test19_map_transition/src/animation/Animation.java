package animation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import math.Vec2;
import math.Vec4;
import renderer3d.Material;
import renderer3d.Renderer;

/**
 *
 * @author Leo
 */
public class Animation {
    
    private String id;
    private Mesh mesh;
    private double scale;
    private BufferedImage texture;
    private final Map<String, Action> actions = new HashMap<String, Action>();
    
    private Action currentAction;
    private double currentFrame;
    
    public Animation(String resPath, String res) {
        load(resPath, res);
    }
    
    private void load(String resPath, String res) {
        try ( Scanner sc = new Scanner(
                getClass().getResourceAsStream(resPath + res)) ) {
            
            //sc.useDelimiter("[ \n]");
            while (sc.hasNext()) {
                String token = sc.next();
                
                // ignore comments
                if (token.startsWith("#")) {
                    sc.nextLine();
                    continue;
                }
                
                switch (token.toLowerCase()) {
                    case "id" -> id = sc.next();
                    //mesh walking_hand_gun.obj
                    case "mesh" -> mesh = new Mesh(resPath + sc.next());
                    //scale 1.10
                    case "scale" -> scale = sc.nextDouble();
                    //texture walking_hand_gun.png
                    case "texture" -> {
                        try {
                            texture = ImageIO.read(getClass().getResourceAsStream(resPath + sc.next()));
                            material = new Material(id + "_material", texture);
                        } catch (IOException ex) {
                            Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
                            System.exit(-1);
                        }
                    }
                    //#action action_id pc2_file fps start_frame end_frame loop
                    case "action" -> {
                        String actionId = sc.next();
                        // action idle idle.pc2 24 0 1 true
                        Action action = new Action(actionId
                            , resPath, sc.next(), sc.nextInt(), sc.nextInt()
                                , sc.nextInt(), sc.nextBoolean());
                        actions.put(actionId, action);
                        System.out.println("loading action: " + action);
                    }
                    //#event action_id frame event_id event_param_0 ... event_param_n
                    //event walking 3 sound step
                    case "event" -> {
                        // TODO
                    }
                }
            }
        }        
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(double currentFrame) {
        this.currentFrame = currentFrame;
    }

    public String getId() {
        return id;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public Action getAction(String actionId) {
        return actions.get(actionId);
    }

    public void setAction(String actionId) {
        currentAction = actions.get(actionId);
        currentFrame = 0.0;
        if (currentAction != null) {
            currentFrame = currentAction.getStartFrame();
        }
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void update(double delta) {
        if (currentAction == null) {
            return;
        }
        
        currentFrame += delta * currentAction.getFps();
        if (currentFrame >= currentAction.getEndFrame() + 1) {
            
            if (currentAction.isLoop()) {
                currentFrame = currentAction.getStartFrame() + (currentAction.getEndFrame() + 1 - currentFrame);
            }
            else {
                currentFrame = currentAction.getEndFrame();
            }
        }
        
    }

    private Material material;
    private final renderer3d.Triangle faceTmp = new renderer3d.Triangle(new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec2(), new Vec2(), new Vec2());
    private final double[][] va = new double[3][5];
    
    public void draw(Graphics2D g, Renderer renderer, double dx, double dy, double dz, double angle) {
        if (currentAction == null) {
            return;
        }
        
        double[] vertices = currentAction.getPositions()[(int) currentFrame];
        
        int nextFrame = (((int) currentFrame + 1)) % currentAction.getPositions().length;
        if (nextFrame == 0 && !currentAction.isLoop()) {
            nextFrame = (int) currentFrame;
        }
        else if (nextFrame == 0) {
            nextFrame = currentAction.getStartFrame();
        }
        
        double[] vertices2 = currentAction.getPositions()[nextFrame];
        
        double p = currentFrame - (int) currentFrame;
        
        for (int f = 0; f < mesh.getFaces().length; f++) {
            int[] face = mesh.getFaces()[f];
            for (int fv = 0; fv < 3; fv++) {
                int vi = face[fv];
                int sti = face[fv + 3];
                
                //double px = vertices[vi * 3 + 0] * scale;
                //double py = vertices[vi * 3 + 1] * scale;
                //double pz = vertices[vi * 3 + 2] * scale;

                double px = vertices[vi * 3 + 0] * scale + p * (vertices2[vi * 3 + 0] * scale - vertices[vi * 3 + 0] * scale);
                double py = vertices[vi * 3 + 1] * scale + p * (vertices2[vi * 3 + 1] * scale - vertices[vi * 3 + 1] * scale);
                double pz = vertices[vi * 3 + 2] * scale + p * (vertices2[vi * 3 + 2] * scale - vertices[vi * 3 + 2] * scale);

                double s = mesh.getTextureCoordinates()[sti][0];
                double t = mesh.getTextureCoordinates()[sti][1];
                
                va[fv][0] = px; //300 * (px / -pz);
                va[fv][1] = py; //300 * (py / -pz);
                va[fv][2] = pz;
                va[fv][3] = s;
                va[fv][4] = t;
            }
            
            faceTmp.a.set(va[0][0], va[0][1], va[0][2], 1.0);
            faceTmp.a.rotateY(angle);
            faceTmp.a.x += dx;
            faceTmp.a.y += dy;
            faceTmp.a.z += dz;
            faceTmp.uvA.set(va[0][3], va[0][4]);

            faceTmp.b.set(va[1][0], va[1][1], va[1][2], 1.0);
            faceTmp.b.rotateY(angle);
            faceTmp.b.x += dx;
            faceTmp.b.y += dy;
            faceTmp.b.z += dz;
            faceTmp.uvB.set(va[1][3], va[1][4]);

            faceTmp.c.set(va[2][0], va[2][1], va[2][2], 1.0);
            faceTmp.c.rotateY(angle);
            faceTmp.c.x += dx;
            faceTmp.c.y += dy;
            faceTmp.c.z += dz;
            faceTmp.uvC.set(va[2][3], va[2][4]);
            
            renderer.draw(g, faceTmp, material);
        }        
    }
    
    public static void main(String[] args) {
        String test = "action";
        System.out.println("" + test.matches("id|mesh|texture|action|event"));
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        GraphicsConfiguration cg =  gd.getDefaultConfiguration();
//        BufferedImage ci = cg.createCompatibleImage(100, 200);
//        ColorModel cm = ci.getColorModel();
        Animation anim = new Animation("/res/player/", "player.anim");
        
        System.out.println("" + anim);

    }
}
