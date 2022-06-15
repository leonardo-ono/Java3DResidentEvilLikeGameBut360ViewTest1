package physics;


import java.awt.geom.Point2D;


/**
 *
 * @author Leo
 */
public class Vertex extends Point2D.Double {

    private int id;
    private double height;
    
    public Vertex(int id, double x, double y, double height) {
        super(x, y);
        this.id = id;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Vertex{id= " + id + ", x=" + x + ", y=" + y + ", height=" + height + '}';
    }
    
}
