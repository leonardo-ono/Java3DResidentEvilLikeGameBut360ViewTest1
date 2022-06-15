package renderer3d;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import math.Mat4;
import math.Vec2;
import math.Vec4;

/**
 * Renderer class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Renderer {

    private final Camera camera;
    private final DepthBuffer depthBuffer;
            
    private final Mat4 viewport = new Mat4();
    private final Mat4 projection = new Mat4();
    private final Mat4 model = new Mat4();
    private final Mat4 mvp = new Mat4();
    private final Vec4 pa = new Vec4();
    private final Vec4 pb = new Vec4();
    private final Vec4 pc = new Vec4();
    
    private Triangle currentTriangle;
    //private final Vec3 normal = new Vec3();
    //private final List<Vertex> vertices = new ArrayList<>();
    private Material material;
    
    public final double[] currentDepthBuffer;
    
    private final TriangleRasterizerComposite triangleComposite 
        = new TriangleRasterizerComposite();
    
    
    private int halfWidth;
    private int halfHeight;
    
    public Renderer(Camera camera, int width, int height, double[] currentDepthBuffer) {
        this.camera = camera;
        double aspectRatio = width / (double) height;
        aspectRatio = width / (double) height;
        projection.setPerspective(Math.toRadians(camera.getFov()), aspectRatio, camera.getzNear(), camera.getzFar());
        viewport.setViewportTransformation2(0, 0, width, height);
        depthBuffer = new DepthBuffer(width, height);
        this.currentDepthBuffer = currentDepthBuffer;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
    }

    private final Polygon polygonTmp = new Polygon();
    private final Vec4[] screenPoints = { new Vec4(), new Vec4(), new Vec4() };
    private final Vec4 screenPoint = new Vec4();

    Vec4 copyA = new Vec4();
    Vec4 copyB = new Vec4();
    Vec4 copyC = new Vec4();
    Vec4[] copyVs = { copyA, copyB, copyC };

    public DepthBuffer getDepthBuffer() {
        return depthBuffer;
    }
    
    public void draw(Graphics2D g, Triangle face, Material material) {
        this.material = material;
        
        this.currentTriangle = face;
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
        
        //viewport.multiply(pa);
        //viewport.multiply(pb);
        //viewport.multiply(pc);
        pa.x = pa.x * halfWidth + halfWidth;
        pa.y = pa.y * -halfHeight + halfHeight;
        pb.x = pb.x * halfWidth + halfWidth;
        pb.y = pb.y * -halfHeight + halfHeight;
        pc.x = pc.x * halfWidth + halfWidth;
        pc.y = pc.y * -halfHeight + halfHeight;
        
        //g.setColor(Color.WHITE);
        //g.drawLine((int) pa.x, (int) pa.y, (int) pb.x, (int) pb.y);
        //g.drawLine((int) pb.x, (int) pb.y, (int) pc.x, (int) pc.y);
        //g.drawLine((int) pc.x, (int) pc.y, (int) pa.x, (int) pa.y);
        
        copyVs[0].x = pa.x;
        copyVs[0].y = pa.y;
        copyVs[0].z = -pa.z;
        copyVs[0].w = pa.w;
        
        copyVs[1].x = pb.x;
        copyVs[1].y = pb.y;
        copyVs[1].z = -pb.z;
        copyVs[1].w = pb.w;
        
        copyVs[2].x = pc.x;
        copyVs[2].y = pc.y;
        copyVs[2].z = -pc.z;
        copyVs[2].w = pc.w;
        
        polygonTmp.reset();
        for (int index = 0; index < 3; index++) {
            int x = (int) (copyVs[index].x);
            int y = (int) (copyVs[index].y);
            polygonTmp.addPoint(x, y);
            screenPoints[index].set(x, 0, y, 1);
        }

        calculateTotalWeight(screenPoints[0], screenPoints[1], screenPoints[2]);
        
        // back-face culling
        //if (wtotal > 0) {
        //    return;
        //}
        
        triangleComposite.set(depthBuffer);
        
        Composite originalComposite = g.getComposite();
        
        g.setComposite(triangleComposite);
        g.fill(polygonTmp);
        
        g.setComposite(originalComposite);
    }
    
    private class TriangleRasterizerComposite implements Composite {
        
        private final TriangleRasterizerCompositeContext context 
            = new TriangleRasterizerCompositeContext();

        public void set(DepthBuffer depthBuffer) {
            context.setDepthBuffer(depthBuffer);
        }
        
        @Override
        public CompositeContext createContext(ColorModel srcColorModel
                , ColorModel dstColorModel, RenderingHints hints) {
            
            return context;
        }
        
    }
    
    private class TriangleRasterizerCompositeContext 
            implements CompositeContext {

        private DepthBuffer depthBuffer;
        private final int[] pxDst = new int[4];

        public void setDepthBuffer(DepthBuffer depthBuffer) {
            this.depthBuffer = depthBuffer;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            for (int mx = 0; mx < dstOut.getWidth(); mx++) {
                for (int my = 0; my < dstOut.getHeight(); my++) {
                    int x = mx - dstOut.getSampleModelTranslateX();
                    int y = my - dstOut.getSampleModelTranslateY();

                    screenPoint.set(x, 0, y, 1);
                    calculateWeights(screenPoint
                        , screenPoints[0], screenPoints[1], screenPoints[2]);

                    double z = w0 * copyA.z + w1 * copyB.z + w2 * copyC.z;
                    double w = w0 * copyA.w + w1 * copyB.w + w2 * copyC.w;
                    
                    Vec2 st0 = currentTriangle.uvA;
                    Vec2 st1 = currentTriangle.uvB;
                    Vec2 st2 = currentTriangle.uvC;
                    
                    double s = w0 * st0.x + w1 * st1.x + w2 * st2.x;
                    double t = w0 * st0.y + w1 * st1.y + w2 * st2.y;

                    if (material == null || material.getTexture() == null) {
                        pxDst[0] = 255;
                        pxDst[1] = 255;
                        pxDst[2] = 255;
                        pxDst[3] = 255;
                    }
                    else {
                        material.getTexture(s, t, pxDst);
                        pxDst[3] = 255;
                    }

                    if (pxDst[3] < 255) {
                    }
                    else {
                        if (!depthBuffer.update(x, y, z)) {
                            continue;
                        }
                        //System.out.println(""+(Math.toDegrees(FixDepth.cos[y][x])));
                        if (w < (currentDepthBuffer[y * (2 * halfWidth) + x] * FixDepth.cos[y][x])) {
                            dstOut.setPixel(mx, my, pxDst);
                        }
                        
                    }
                }
            }
        }
        
    }

    private double wtotal;
    private double w0;
    private double w1;
    private double w2;
    private final Vec4 v0 = new Vec4();
    private final Vec4 v1 = new Vec4();
    private final Vec4 v2 = new Vec4();
    
    public void calculateTotalWeight(Vec4 p0, Vec4 p1, Vec4 p2) {
        v0.set(p1);
        v0.sub(p0);
        v1.set(p2);
        v1.sub(p0);
        wtotal = v0.cross2D(v1);
        wtotal = 1 / wtotal;
    }
    
    public void calculateWeights(Vec4 p, Vec4 p0, Vec4 p1, Vec4 p2) {
        v0.set(p0, p);
        v1.set(p1, p);
        v2.set(p2, p);
        w0 = v1.cross2D(v2) * wtotal;
        w1 = v2.cross2D(v0) * wtotal;
        w2 = v0.cross2D(v1) * wtotal;
    }
    
}
