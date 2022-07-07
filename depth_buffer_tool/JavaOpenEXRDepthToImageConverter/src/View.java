

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 */
public class View extends JPanel implements MouseMotionListener {
    
    private BufferedImage image;
    
    public View() {
        try {
            image = ImageIO.read(new File("d:/depth.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }       
        addMouseMotionListener(this);
    }

    public void start() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(image, 0, 0, null);
        
    }
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            JFrame frame = new JFrame();
            frame.setTitle("");
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }    

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int color = image.getRGB(e.getX(), e.getY());
        System.out.println("depth: " + Float.intBitsToFloat(color));
    }
    
}
