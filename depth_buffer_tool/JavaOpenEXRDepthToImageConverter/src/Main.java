
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Leo
 */
public class Main {
    static int width = 800;
    static int height = 800;
    
    public static void main(String[] args) throws Exception {
        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        
        
        int[] data = ((DataBufferInt) (i.getRaster().getDataBuffer())).getData();
            
        DataInputStream dis = new DataInputStream(new FileInputStream("D:/work/java/JavaAloneInTheDark360Test10_multiple_cameras/src/res/alysium_depth_3.dat"));

        for (int index = 0; index < (width * height); index++) {
        
            //System.out.println(dis.readFloat());

            int b1 = dis.readByte() & 0xff;
            int b2 = dis.readByte() & 0xff;
            int b3 = dis.readByte() & 0xff;
            int b4 = dis.readByte() & 0xff;
            //System.out.println("" + b1);
            //System.out.println("" + b2);
            //System.out.println("" + b3);
            //System.out.println("" + b4);
            int f = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24) + 0xffffffff;
            //System.out.println("" + f);
            
            //if (index == 400 * 800 + 400) {
            //    System.out.println("" + Float.intBitsToFloat(f));
            //}
            //i.setRGB(index % 800, index / 800, 0xffffff & f);
            data[index] = f;
        }
        dis.close();
        
        ImageIO.write(i, "png", new File("D:/work/java/JavaAloneInTheDark360Test10_multiple_cameras/src/res/alysium_depth_3.png"));
    }
        
}
