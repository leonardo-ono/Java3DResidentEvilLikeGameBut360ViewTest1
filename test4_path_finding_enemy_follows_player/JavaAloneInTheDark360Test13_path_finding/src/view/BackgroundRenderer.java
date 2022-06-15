package view;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import math.Mat4;
import math.Vec4;
import renderer3d.Renderer;
import view.Resource.CameraInfo;

/**
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class BackgroundRenderer implements MouseMotionListener {

    private BufferedImage sphereImage;
    
    private final BufferedImage offscreenImage;
    private final Graphics2D og2d;
    
    private int[] sphereImageBuffer;
    private final int[] offscreenImageBuffer;
    //private final int[] offscreenImageBuffer2;
    private static final double FOV = Math.toRadians(90);
    private final double cameraPlaneDistance;
    private double rayVecs[][][];
    private static final double ACCURACY_FACTOR = 512 * 4; 
    private static final int REQUIRED_SIZE = (int) (2 * ACCURACY_FACTOR);
    private double[] asinTable;
    private double[] atan2Table;
    private static final double INV_PI = 1 / Math.PI;
    private static final double INV_2PI = 1 / (2 * Math.PI);
    private double targetRotationX, targetRotationY;
    public double currentRotationX, currentRotationY;
    private int mouseX, mouseY;
    private BufferStrategy bs;

    private double[] backgroundDepthBuffer;
    public double[] currentDepthBuffer;
    
    private int width;
    private int height;
    
    public BackgroundRenderer(int width, int height) {
        
        this.width = width;
        this.height = height;
                
        try {
            BufferedImage sphereTmpImage = ImageIO.read(getClass().getResourceAsStream("/res/alysium_0.png"));
            sphereImage = new BufferedImage(sphereTmpImage.getWidth(), sphereTmpImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            sphereImage.getGraphics().drawImage(sphereTmpImage, 0, 0, null);
            sphereImageBuffer = ((DataBufferInt) sphereImage.getRaster().getDataBuffer()).getData();
        } catch (IOException ex) {
            Logger.getLogger(BackgroundRenderer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        og2d = (Graphics2D) offscreenImage.getGraphics();
        
        offscreenImageBuffer = ((DataBufferInt) offscreenImage.getRaster().getDataBuffer()).getData();
        //offscreenImageBuffer2 = new int[width * height];
        cameraPlaneDistance = (offscreenImage.getWidth() / 2.0) / Math.tan(FOV / 2);
        createRayVecs();
        precalculateAsinAtan2();
        loadBackgroundDepthBuffer(width, height);
    }


    private BufferedImage depth;
    private void loadBackgroundDepthBuffer(int width, int height) {
        try {
            
            BufferedImage depthTmp = ImageIO.read(getClass().getResourceAsStream("/res/alysium_depth_0.png"));
            depth = new BufferedImage(depthTmp.getWidth(), depthTmp.getHeight(), BufferedImage.TYPE_INT_ARGB);

            backgroundDepthBuffer = new double[depth.getWidth() * depth.getHeight()];
            currentDepthBuffer = new double[width * height];

            depth.getGraphics().drawImage(depthTmp, 0, 0, null);
            int[] z = ((DataBufferInt) depth.getRaster().getDataBuffer()).getData();
            for (int y = 0; y < depth.getHeight(); y++) {
                for (int x = 0; x < depth.getWidth(); x++) {
                    backgroundDepthBuffer[y * depth.getWidth() + x] = Float.intBitsToFloat(z[y * depth.getWidth() + x]);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    private void createRayVecs() {
        rayVecs = new double[offscreenImage.getWidth()][offscreenImage.getHeight()][3]; // x, y, z
        for (int y = 0; y < offscreenImage.getHeight(); y++) {
            for (int x = 0; x < offscreenImage.getWidth(); x++) {
                double vecX = x - offscreenImage.getWidth() / 2;
                double vecY = y - offscreenImage.getHeight() / 2;
                double vecZ = cameraPlaneDistance;
                double invVecLength = 1 / Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
                rayVecs[x][y][0] = vecX * invVecLength;
                rayVecs[x][y][1] = vecY * invVecLength;
                rayVecs[x][y][2] = vecZ * invVecLength;
            }
        }
    }
    
    private void precalculateAsinAtan2() {
        asinTable = new double[REQUIRED_SIZE];
        atan2Table = new double[REQUIRED_SIZE * REQUIRED_SIZE];
        for (int i = 0; i < 2 * ACCURACY_FACTOR; i++) {
            asinTable[i] = Math.asin((i - ACCURACY_FACTOR) * 1 / ACCURACY_FACTOR);
            for (int j = 0; j < 2 * ACCURACY_FACTOR; j++) {
                double y = (i - ACCURACY_FACTOR) / ACCURACY_FACTOR;
                double x = (j - ACCURACY_FACTOR) / ACCURACY_FACTOR;
                atan2Table[i + j * REQUIRED_SIZE] = Math.atan2(y, x);
            }
        }
    }

    public BufferedImage getOffscreenImage() {
        return offscreenImage;
    }

    public Graphics2D getOg2d() {
        return og2d;
    }
    
    public void update() {
        targetRotationX = (mouseY - (offscreenImage.getHeight() / 2)) * 0.025;
        targetRotationY = (mouseX - (offscreenImage.getWidth() / 2)) * 0.025;
        currentRotationX += (targetRotationX - currentRotationX) * 0.1;
        currentRotationY += (targetRotationY - currentRotationY) * 0.1;
        
        //currentRotationX = 0.0001;
        //currentRotationY = Math.toRadians(90.0);
    }
    
    Vec4 vTmp = new Vec4();
    
    public void draw(Graphics2D g, Mat4 camTransform) {
        
//        double sinRotationX = Math.sin(currentRotationX);
//        double cosRotationX = Math.cos(currentRotationX);
//        double sinRotationY = Math.sin(currentRotationY);
//        double cosRotationY = Math.cos(currentRotationY);
//        double tmpVecX, tmpVecY, tmpVecZ;
        for (int y = 0; y < offscreenImage.getHeight(); y++) {
            for (int x = 0; x < offscreenImage.getWidth(); x++) {
                double vecX = rayVecs[x][y][0];
                double vecY = rayVecs[x][y][1];
                double vecZ = rayVecs[x][y][2];
                vTmp.set(vecX, vecY, vecZ, 1.0);
//                // rotate x
//                tmpVecZ = vecZ * cosRotationX - vecY * sinRotationX;
//                tmpVecY = vecZ * sinRotationX + vecY * cosRotationX;
//                vecZ = tmpVecZ;
//                vecY = tmpVecY;
//                // rotate y
//                tmpVecZ = vecZ * cosRotationY - vecX * sinRotationY;
//                tmpVecX = vecZ * sinRotationY + vecX * cosRotationY;
//                vecZ = tmpVecZ;
//                vecX = tmpVecX;
                camTransform.multiply(vTmp);
                vecX = vTmp.x;
                vecY = vTmp.y;
                vecZ = vTmp.z;
                int iX = (int) ((vecX + 1) * ACCURACY_FACTOR); 
                int iY = (int) ((vecY + 1) * ACCURACY_FACTOR);
                int iZ = (int) ((vecZ + 1) * ACCURACY_FACTOR);

                if (iY > asinTable.length - 1) {
                    iY = asinTable.length - 1;
                }
                
                // https://en.wikipedia.org/wiki/UV_mapping
                double u = 0.5 + (atan2Table[iZ + iX * REQUIRED_SIZE] * INV_2PI);
                double v = 0.5 - (asinTable[iY] * INV_PI);
                
                int tx = (int) (sphereImage.getWidth() * u);
                int ty = (int) (sphereImage.getHeight() * (1 - v));
                int srcIndex = ty * sphereImage.getWidth() + tx;
                int color = sphereImageBuffer[srcIndex]; 
                offscreenImageBuffer[y * offscreenImage.getWidth() + x] = color;
                
                int dx = (int) (depth.getWidth() * u);
                int dy = (int) (depth.getHeight() * (1 - v));
                srcIndex = dy * depth.getWidth() + dx;
                double depthValue = backgroundDepthBuffer[srcIndex];
                currentDepthBuffer[y * offscreenImage.getWidth() + x] = depthValue;
            }
        }
        //g.drawImage(offscreenImage, 0, 0, 800, 600, null);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    public void changeCamera(CameraInfo cameraInfo) {
        sphereImage = cameraInfo.textureMap;
        sphereImageBuffer = ((DataBufferInt) sphereImage.getRaster().getDataBuffer()).getData();
        backgroundDepthBuffer = cameraInfo.depthMap;
    }
    
}
