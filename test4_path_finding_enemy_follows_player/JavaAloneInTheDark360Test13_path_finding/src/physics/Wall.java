package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import math.Vec2;


/**
 *
 * @author Leo
 */
public class Wall extends Line2D.Double {
    
    private Vertex a;
    private Vertex b;
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
        return nextTriangle == null;
    }

    public Triangle getNextTriangle() {
        return nextTriangle;
    }

    public void setNextTriangle(Triangle nextTriangle) {
        this.nextTriangle = nextTriangle;
    }
    
    public void draw(Graphics2D g) {
        if (isBlocking()) {
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
    
    public void getCenter(Vec2 c) {
        c.x = 0.5 * (a.x + b.x);
        c.y = 0.5 * (a.y + b.y);
    }
    
    public boolean collides(PlayerMonster player) {
        double d = ptSegDist(player.x, player.y);
        return (d <= player.radius);
    }
    
}
