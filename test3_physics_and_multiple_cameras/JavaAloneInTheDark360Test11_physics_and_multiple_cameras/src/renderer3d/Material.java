package renderer3d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Material class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Material {
    
    private final String id;
    private BufferedImage texture;
    private Raster textureRaster;
    
    public Material(String res) {
        this.id = res;
        try {
            BufferedImage textureTmp = ImageIO.read(getClass().getResourceAsStream(res));
            texture = new BufferedImage(textureTmp.getWidth(), textureTmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
            texture.getGraphics().drawImage(textureTmp, 0, 0, null);
            textureRaster = texture.getRaster();
        } catch (IOException ex) {
            Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public String getId() {
        return id;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public void getTexture(double s, double t, int[] pxDst) {
        getPixelRepeat(s, t, pxDst);
    }
    
    private void getPixelRepeat(double s, double t, int[] pxDst) {
        s = s % 1;
        t = t % 1;
        if (s < 0) {
            int si = (int) s;
            s -= si - 1;
        }
        if (t < 0) {
            int ti = (int) t;
            t -= ti - 1;
        }
        int tx = (int) (s * (texture.getWidth() - 1));
        int ty = (int) ((1 - t) * (texture.getHeight() - 1));
        textureRaster.getPixel(tx, ty, pxDst);
    }
    
}
