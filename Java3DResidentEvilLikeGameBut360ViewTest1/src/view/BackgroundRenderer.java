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

/**
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class BackgroundRenderer implements MouseMotionListener {

    private BufferedImage sphereImage;
    private final BufferedImage offscreenImage;
    private int[] sphereImageBuffer;
    private final int[] offscreenImageBuffer;
    private final int[] offscreenImageBuffer2;
    private static final double FOV = Math.toRadians(90);
    private final double cameraPlaneDistance;
    private double rayVecs[][][];
    private static final double ACCURACY_FACTOR = 512 * 3; 
    private static final int REQUIRED_SIZE = (int) (2 * ACCURACY_FACTOR);
    private double[] asinTable;
    private double[] atan2Table;
    private static final double INV_PI = 1 / Math.PI;
    private static final double INV_2PI = 1 / (2 * Math.PI);
    private double targetRotationX, targetRotationY;
    public double currentRotationX, currentRotationY;
    private int mouseX, mouseY;
    private BufferStrategy bs;

    
    public BackgroundRenderer() {
        try {
            //BufferedImage sphereTmpImage = ImageIO.read(getClass().getResourceAsStream("/res/factory.jpg"));
            BufferedImage sphereTmpImage = ImageIO.read(getClass().getResourceAsStream("/res/deposito_360.png"));
            //BufferedImage sphereTmpImage = ImageIO.read(getClass().getResourceAsStream("/res/zz.png"));
            sphereImage = new BufferedImage(sphereTmpImage.getWidth(), sphereTmpImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            sphereImage.getGraphics().drawImage(sphereTmpImage, 0, 0, null);
            sphereImageBuffer = ((DataBufferInt) sphereImage.getRaster().getDataBuffer()).getData();
        } catch (IOException ex) {
            Logger.getLogger(BackgroundRenderer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        offscreenImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        offscreenImageBuffer = ((DataBufferInt) offscreenImage.getRaster().getDataBuffer()).getData();
        offscreenImageBuffer2 = new int[400 * 300];
        cameraPlaneDistance = (offscreenImage.getWidth() / 2.0) / Math.tan(FOV / 2);
        createRayVecs();
        precalculateAsinAtan2();
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
    
    public void update() {
        targetRotationX = (mouseY - (offscreenImage.getHeight() / 2)) * 0.025;
        targetRotationY = (mouseX - (offscreenImage.getWidth() / 2)) * 0.025;
        currentRotationX += (targetRotationX - currentRotationX) * 0.1;
        currentRotationY += (targetRotationY - currentRotationY) * 0.1;
        
        //currentRotationX = 0.0001;
        //currentRotationY = Math.toRadians(90.0);
    }
    
    public void draw(Graphics2D g) {
        
        double sinRotationX = Math.sin(currentRotationX);
        double cosRotationX = Math.cos(currentRotationX);
        double sinRotationY = Math.sin(currentRotationY);
        double cosRotationY = Math.cos(currentRotationY);
        double tmpVecX, tmpVecY, tmpVecZ;
        for (int y = 0; y < offscreenImage.getHeight(); y++) {
            for (int x = 0; x < offscreenImage.getWidth(); x++) {
                double vecX = rayVecs[x][y][0];
                double vecY = rayVecs[x][y][1];
                double vecZ = rayVecs[x][y][2];
                // rotate x
                tmpVecZ = vecZ * cosRotationX - vecY * sinRotationX;
                tmpVecY = vecZ * sinRotationX + vecY * cosRotationX;
                vecZ = tmpVecZ;
                vecY = tmpVecY;
                // rotate y
                tmpVecZ = vecZ * cosRotationY - vecX * sinRotationY;
                tmpVecX = vecZ * sinRotationY + vecX * cosRotationY;
                vecZ = tmpVecZ;
                vecX = tmpVecX;
                int iX = (int) ((vecX + 1) * ACCURACY_FACTOR); 
                int iY = (int) ((vecY + 1) * ACCURACY_FACTOR);
                int iZ = (int) ((vecZ + 1) * ACCURACY_FACTOR);
                // https://en.wikipedia.org/wiki/UV_mapping
                double u = 0.5 + (atan2Table[iZ + iX * REQUIRED_SIZE] * INV_2PI);
                double v = 0.5 - (asinTable[iY] * INV_PI);
                int tx = (int) (sphereImage.getWidth() * u);
                int ty = (int) (sphereImage.getHeight() * (1 - v));
                int color = sphereImageBuffer[ty * sphereImage.getWidth() + tx]; 
                offscreenImageBuffer[y * offscreenImage.getWidth() + x] = color;
                offscreenImageBuffer2[y * offscreenImage.getWidth() + x] = color;
            }
        }
        g.drawImage(offscreenImage, 0, 0, 800, 600, null);
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
    
}
