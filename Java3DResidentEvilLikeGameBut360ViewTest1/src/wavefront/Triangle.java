package wavefront;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import math.Vec2;
import math.Vec3;
import math.Vec4;

/**
 *
 * @author Leo
 */
public class Triangle implements Comparable<Triangle>, Serializable {

    public Vec4 a;
    public Vec4 b;
    public Vec4 c;
    public Vec4 normal = new Vec4();
    
    public Vec2 uvA;
    public Vec2 uvB;
    public Vec2 uvC;
    
    public String materialId;
    
    private final Vec3 p1Tmp = new Vec3();
    
    private static Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    private static Color[] colors = new Color[256];
    
    static {
        for (int c = 0; c < 256; c++) {
            colors[c] = new Color(c, c, c, 255);
        }
    }
    
    public Triangle(Vec4 a, Vec4 b, Vec4 c, Vec4 n, Vec2 uvA, Vec2 uvB, Vec2 uvC) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.normal = n;
        this.uvA = uvA;
        this.uvB = uvB;
        this.uvC = uvC;
        if (uvA == null || uvB == null || uvC == null || normal == null || a == null || b == null || c == null) {
            throw new RuntimeException("Triangle will null arguments in constructor !");
        }
    }
    
//    public Triangle(Vec3 a, Vec3 b, Vec3 c) {
//        this.a = a;
//        this.b = b;
//        this.c = c;
//
//        p1Tmp.set(a);
//        p1Tmp.sub(b);
//        normal.set(c);
//        normal.sub(b);
//        normal.cross(p1Tmp);
//    }
    
    private static Polygon polygon = new Polygon();
    
    public void draw(Graphics2D g) {
        polygon.reset();
        polygon.addPoint((int) (a.x + 0), (int) (a.z + 0));
        polygon.addPoint((int) (b.x + 0), (int) (b.z + 0));
        polygon.addPoint((int) (c.x + 0), (int) (c.z + 0));
        g.draw(polygon);
    }
    
    @Override
    public String toString() {
        return "Triangle{" + "a=" + a + ", b=" + b + ", c=" + c + ", normal=" + normal + '}';
    }

    @Override
    public int compareTo(Triangle o) {
        return (int) Math.signum((o.a.z + o.b.z + o.c.z) / 3 - (a.z + b.z + c.z) / 3);
    }


}
