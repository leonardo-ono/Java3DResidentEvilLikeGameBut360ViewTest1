package renderer3d;

import math.Mat4;

/**
 * Camera class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Camera {
    
    private final Mat4 worldTransform = new Mat4();
    private final double fov; // in degrees
    private final double aspectRatio;
    private final double zNear;
    private final double zFar;
    
    public Mat4 transRx = new Mat4();
    public Mat4 transRy = new Mat4();
    public Mat4 transTr = new Mat4();
    
    public double rx = 0;
    public double ry = 0;
    
    public Camera(String id, double fov, double aspectRatio, double zNear, double zFar) {
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public void update() {
        transTr.setTranslation(0.0, -1.33109, 1.94023);
        transRx.setRotationX(-rx);
        transRy.setRotationY(ry + Math.toRadians(90.0));
        
        worldTransform.setIdentity();
        worldTransform.multiply(transRx);
        worldTransform.multiply(transRy);
        worldTransform.multiply(transTr);
    }
    
    public Mat4 getWorldTransform() {
        return worldTransform;
    }

    public double getFov() {
        return fov;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public double getzNear() {
        return zNear;
    }

    public double getzFar() {
        return zFar;
    }

}
