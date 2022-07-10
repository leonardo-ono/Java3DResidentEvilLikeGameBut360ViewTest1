package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Wall class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Wall extends Line2D.Double {
    
    private final Vertex a;
    private final Vertex b;
    private Triangle owner;
    private Triangle nextTriangle;
    
    public Wall(Vertex a, Vertex b) {
        super(a.x, a.y, b.x, b.y);
        this.a = a;
        this.b = b;
        
    }

    public Vertex getA() {
        return a;
    }

    public Vertex getB() {
        return b;
    }

    public Triangle getOwner() {
        return owner;
    }

    public void setOwner(Triangle owner) {
        this.owner = owner;
    }

    public boolean isBlocking() {
        return nextTriangle == null || nextTriangle.isOnlyRaycast();
    }
    
    public boolean canRaycastPassThrough() {
        return nextTriangle != null && nextTriangle.isOnlyRaycast();
    }

    public Triangle getNextTriangle() {
        return nextTriangle;
    }

    public void setNextTriangle(Triangle nextTriangle) {
        this.nextTriangle = nextTriangle;
    }
    
    public void draw(Graphics2D g) {
        if (nextTriangle != null && nextTriangle.isOnlyRaycast()) {
            g.setColor(Color.CYAN);
        }
        else if (isBlocking()) {
            g.setColor(Color.RED);
        }
        else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.draw(this);
    }

    public String getHash1() {
        return a.getId() + "_" + b.getId();
    }

    public String getHash2() {
        return b.getId() + "_" + a.getId();
    }
    
//    public void getCenter(Vec2 c) {
//        c.x = 0.5 * (a.x + b.x);
//        c.y = 0.5 * (a.y + b.y);
//    }
    
    public boolean collides(Body body) {
        double d = ptSegDist(body.x, body.y);
        return (d <= body.radius);
    }
    
    public static void getIntersectionPoint(
                Line2D la, Line2D lb, Point2D result) {
        
        double d1 = la.ptLineDist(lb.getX1(), lb.getY1());
        double d2 = la.ptLineDist(lb.getX2(), lb.getY2());
        double s = d2 / (d1 + d2);
        double vx = s * (lb.getX1() - lb.getX2());
        double vy = s * (lb.getY1() - lb.getY2());
        result.setLocation(lb.getX2() + vx, lb.getY2() + vy);
    }
    
}
