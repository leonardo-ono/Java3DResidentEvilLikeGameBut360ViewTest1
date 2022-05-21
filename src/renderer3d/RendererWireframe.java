package renderer3d;

import math.Vec4;
import math.Mat4;
import java.awt.Graphics2D;
import java.awt.Polygon;
import wavefront.Triangle;

/**
 * RendererWireframe class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class RendererWireframe {

    private final Camera camera;
    private final Mat4 viewport = new Mat4();
    private final Mat4 projection = new Mat4();
    private final Mat4 model = new Mat4();
    private final Mat4 mvp = new Mat4();
    private final Vec4 pa = new Vec4();
    private final Vec4 pb = new Vec4();
    private final Vec4 pc = new Vec4();
    
    public RendererWireframe(Camera camera, int width, int height) {
        this.camera = camera;
        double aspectRatio = width / (double) height;
        aspectRatio = width / (double) height;
        projection.setPerspective(Math.toRadians(camera.getFov()), aspectRatio, camera.getzNear(), camera.getzFar());
        viewport.setViewportTransformation2(0, 0, width, height);
    }
    
    private final Polygon polygon = new Polygon();
    public void draw(Graphics2D g, Triangle face, boolean fill) {
        model.setIdentity();
        //model.multiply(geometry.getWorldTransform());
                
        mvp.set(projection);
        mvp.multiply(camera.getWorldTransform());
        mvp.multiply(model);
        
        pa.set(face.a);
        pb.set(face.b);
        pc.set(face.c);
        
        mvp.multiply(pa);
        mvp.multiply(pb);
        mvp.multiply(pc);
        
        if (pa.z < 0.1 || pb.z < 0.1 || pc.z < 0.1) {
            return;
        }
        
        // TODO: frustrum clipping
        
        pa.doPerspectiveDivision();
        pb.doPerspectiveDivision();
        pc.doPerspectiveDivision();
        
        viewport.multiply(pa);
        viewport.multiply(pb);
        viewport.multiply(pc);
        
        //pa.x = pa.x * 400 + 400;
        //pa.y = pa.y * -300 + 300;
        //pb.x = pb.x * 400 + 400;
        //pb.y = pb.y * -300 + 300;
        //pc.x = pc.x * 400 + 400;
        //pc.y = pc.y * -300 + 300;
        
        //g.setColor(Color.BLACK);
        //g.drawLine((int) pa.x, (int) pa.y, (int) pb.x, (int) pb.y);
        //g.drawLine((int) pb.x, (int) pb.y, (int) pc.x, (int) pc.y);
        //g.drawLine((int) pc.x, (int) pc.y, (int) pa.x, (int) pa.y);
        
        polygon.reset();
        polygon.addPoint((int) pa.x, (int) pa.y);
        polygon.addPoint((int) pb.x, (int) pb.y);
        polygon.addPoint((int) pc.x, (int) pc.y);
        if (fill) {
            g.fill(polygon);
        }
        else {
            g.draw(polygon);
        }
    }
    
}
