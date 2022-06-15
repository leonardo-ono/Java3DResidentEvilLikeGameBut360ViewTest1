package view;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Leo
 */
public class Resource {

    public static class CameraInfo {

        public CameraInfo(double x, double y, double z, String baseRes, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.textureMap = getImage("/res/" + baseRes + "_" + id + ".png");
            this.depthMap = convertToDepthMap(getImage("/res/" + baseRes + "_depth_" + id + ".png"));
        }
        
        public double x;
        public double y;
        public double z;
        public double[] depthMap;
        public BufferedImage textureMap;
    }
    
    public static Map<Integer, CameraInfo> cameras = new HashMap<>();

    public static Map<Integer, Integer> changeCameraAreas = new HashMap<>();

    static {
        // camera conversion:
        // blender x -> -z
        //         y -> -x
        //         z ->  y
        cameras.put(0, new CameraInfo(4.11909, 6.78287, -0.902543, "alysium", 0));
        cameras.put(1, new CameraInfo(-4.5709, 7.08299, -22.4977, "alysium", 1));
        cameras.put(2, new CameraInfo(-4.55542, 7.20907, 21.3513, "alysium", 2));
        cameras.put(3, new CameraInfo(-1.51603, 2.44702, -0.214541, "alysium", 3));
        
        // change camera areas hardcoded for test
        changeCameraAreas.put(9, 3);
        changeCameraAreas.put(10, 3);
        changeCameraAreas.put(11, 3);
        changeCameraAreas.put(14, 3);
        changeCameraAreas.put(15, 3);

        changeCameraAreas.put(81, 0);
        changeCameraAreas.put(91, 0);
        changeCameraAreas.put(89, 0);
        changeCameraAreas.put(4, 0);
        changeCameraAreas.put(94, 0);
        changeCameraAreas.put(97, 0);
        changeCameraAreas.put(98, 0);
        changeCameraAreas.put(100, 0);
        changeCameraAreas.put(101, 0);
        changeCameraAreas.put(6, 0);
        changeCameraAreas.put(103, 0);
        changeCameraAreas.put(102, 0);
        changeCameraAreas.put(80, 0);
        
        changeCameraAreas.put(71, 0);
        changeCameraAreas.put(69, 0);
        changeCameraAreas.put(70, 0);
        changeCameraAreas.put(67, 0);
        changeCameraAreas.put(68, 0);
        
        changeCameraAreas.put(39, 0);
        changeCameraAreas.put(34, 0);
        changeCameraAreas.put(32, 0);
        
        changeCameraAreas.put(75, 1);
        changeCameraAreas.put(76, 1);
        changeCameraAreas.put(73, 1);
        changeCameraAreas.put(74, 1);
        
        changeCameraAreas.put(35, 2);
        changeCameraAreas.put(36, 2);
        
    }
    
    public static BufferedImage getImage(String res) {
        BufferedImage image = null;
        try {
            BufferedImage imageTmp = ImageIO.read(Resource.class.getResourceAsStream(res));
            image = new BufferedImage(imageTmp.getWidth(), imageTmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().drawImage(imageTmp, 0, 0, null);
        } catch (IOException ex) {
            Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return image;
    }
    
    public static double[] convertToDepthMap(BufferedImage depthTmp) {
        BufferedImage depth = new BufferedImage(depthTmp.getWidth(), depthTmp.getHeight(), BufferedImage.TYPE_INT_ARGB);

        double[] backgroundDepthBuffer = new double[depth.getWidth() * depth.getHeight()];

        depth.getGraphics().drawImage(depthTmp, 0, 0, null);
        int[] z = ((DataBufferInt) depth.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < depth.getHeight(); y++) {
            for (int x = 0; x < depth.getWidth(); x++) {
                backgroundDepthBuffer[y * depth.getWidth() + x] = Float.intBitsToFloat(z[y * depth.getWidth() + x]);
            }
        }
        return backgroundDepthBuffer;
    }    
    
}
