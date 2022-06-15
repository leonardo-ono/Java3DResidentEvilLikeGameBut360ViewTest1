package renderer3d;

import math.Vec3;

/**
 *
 * @author Leo
 */
public class FixDepth {

    // [y][x] = 1 / cos(a)
    public static double[][] cos;
    
    public static void precalculate(int width, int height, double fovInRad) {
        cos = new double[height][width];
        
        double cameraPlaneDistance = (width / 2.0) / Math.tan(fovInRad / 2.0);
        
        Vec3 forward = new Vec3(0.0, 0.0, 1.0);
        Vec3 pixelDir = new Vec3();
        Vec3 center = new Vec3(width / 2, height / 2, 0.0);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //if (x == 193 && y == 141) {
                    //System.out.println("");
                //}
                pixelDir.set(x, y, cameraPlaneDistance);
                pixelDir.sub(center);
                
                double c = pixelDir.getCos(forward);
                
                //Math.toDegrees(Math.acos(c))
                
                if (c < 0 ) {
                    c = -c;
                }
                FixDepth.cos[y][x] = c;
            }
        }
        
    }
    
    
}
