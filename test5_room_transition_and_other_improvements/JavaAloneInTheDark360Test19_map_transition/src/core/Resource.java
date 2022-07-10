package core;

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

    private static final Map<String, BufferedImage> images = new HashMap<>();
    
    public static BufferedImage getImage(String resPath, String res) {
        BufferedImage image = images.get(res);
        if (image == null) {
            try {
                BufferedImage imageTmp = ImageIO.read(Resource.class.getResourceAsStream(resPath + res + ".png"));
                image = new BufferedImage(imageTmp.getWidth(), imageTmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
                image.getGraphics().drawImage(imageTmp, 0, 0, null);
                images.put(res, image);
            } catch (IOException ex) {
                Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
        }
        return image;
    }
    
    
    
    public static class CameraInfo {

        public CameraInfo(double x, double y, double z, String baseRes, int id, double playerScale) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            String resPath = "/res/";
            this.textureMap = getImage(resPath, baseRes + "_" + id);
            this.depthMap = convertToDepthMap(getImage(resPath, baseRes + "_depth_" + id));
            this.playerScale = playerScale;
        }
        public final int id;
        public double x;
        public double y;
        public double z;
        public double[] depthMap;
        public BufferedImage textureMap;
        public double playerScale;
    }
    
    public static Map<Integer, CameraInfo> cameras = new HashMap<>();

    public static Map<Integer, Integer> changeCameraAreas = new HashMap<>();

    static {
        // camera conversion:
        // blender x -> -z
        //         y -> -x
        //         z ->  y

// blender python script        
//        import bpy
//        loc = bpy.data.objects["Camera"].location
//        #print(-location.y + " " + location.z + " " + -location.x)
//        print("(%0.6f, %0.6f, %0.6f)" % (-loc.y, loc.z, -loc.x))

        cameras.put(0, new CameraInfo(4.11909, 6.78287, -0.902543, "alysium", 0, 1.10));
        cameras.put(1, new CameraInfo(-4.5709, 7.08299, -22.4977, "alysium", 1, 1.10));
        cameras.put(2, new CameraInfo(-4.55542, 7.20907, 21.3513, "alysium", 2, 1.10));
        cameras.put(3, new CameraInfo(-1.51603, 2.44702, -0.214541, "alysium", 3, 1.10));
        cameras.put(4, new CameraInfo(-6.263285, 3.548546, 18.373096, "alysium", 4, 1.10));
        cameras.put(5, new CameraInfo(-6.502196, 3.471132, -19.129536, "alysium", 5, 1.10));
        
        // rooms
        cameras.put(6, new CameraInfo(-10.3719, 3.37537, -2.97283, "room", 6, 0.90));
        cameras.put(7, new CameraInfo(-23.049999, 7.055812, -20.850000, "room", 7, 0.90));
        cameras.put(8, new CameraInfo(3.977634, 5.997511, -23.645403, "room", 8, 0.90));
        cameras.put(9, new CameraInfo(-12.004307, 6.736326, -15.416447, "room", 9, 1.00));
        cameras.put(10, new CameraInfo(-10.327862, 6.404604, 6.320866, "room", 10, 0.90));
        cameras.put(11, new CameraInfo(-10.386443, 5.341978, 12.569709, "room", 11, 0.90));
        cameras.put(12, new CameraInfo(4.739331, 5.709574, 26.551863, "room", 12, 0.90));
        cameras.put(13, new CameraInfo(-12.286411, 7.009785, 27.322279, "room", 13, 0.90));
        cameras.put(14, new CameraInfo(1.607421, 2.154932, -19.955412, "room", 14, 1.00));
        cameras.put(15, new CameraInfo(-21.401402, 2.695475, -21.540831, "room", 15, 0.90));
        cameras.put(16, new CameraInfo(-12.010837, 2.703370, -11.757189, "room", 16, 0.90));
        cameras.put(17, new CameraInfo(3.522977, 3.191117, 27.481913, "room", 17, 1.00));
        cameras.put(18, new CameraInfo(-23.095160, 5.296899, 22.181736, "room", 18, 0.90));
        
        // change camera areas hardcoded for test
        changeCameraAreas.put(9, 3);
        changeCameraAreas.put(10, 3);
        changeCameraAreas.put(11, 3);
        changeCameraAreas.put(14, 3);
        changeCameraAreas.put(15, 3);
        changeCameraAreas.put(18, 3);
        changeCameraAreas.put(9, 3);

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
        changeCameraAreas.put(44, 0);
        
        changeCameraAreas.put(39, 0);
        changeCameraAreas.put(40, 0);
        changeCameraAreas.put(42, 0);
        
        changeCameraAreas.put(71, 0);
        changeCameraAreas.put(69, 0);
        changeCameraAreas.put(70, 0);
        changeCameraAreas.put(66, 0);
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
        changeCameraAreas.put(37, 2);
        
        changeCameraAreas.put(12, 4);
        changeCameraAreas.put(26, 4);
        changeCameraAreas.put(28, 4);
        
        changeCameraAreas.put(19, 5);
        changeCameraAreas.put(21, 5);
        changeCameraAreas.put(24, 5);
        
        // room 6
        changeCameraAreas.put(120, 6);
        changeCameraAreas.put(18, 3);
        // room 7
        changeCameraAreas.put(123, 7);
        changeCameraAreas.put(78, 1);
        // room 8
        changeCameraAreas.put(141, 8);
        // room 9
        changeCameraAreas.put(126, 9);
        // room 10
        changeCameraAreas.put(134, 10);
        // room 11
        changeCameraAreas.put(132, 11);
        // room 12
        changeCameraAreas.put(128, 12);
        // room 13
        changeCameraAreas.put(140, 13);
        // room 14
        changeCameraAreas.put(144, 14);
        // room 15
        changeCameraAreas.put(146, 15);
        // room 16 ?
        // room 17
        changeCameraAreas.put(128, 17);
        // room 18
        changeCameraAreas.put(129, 18);
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
