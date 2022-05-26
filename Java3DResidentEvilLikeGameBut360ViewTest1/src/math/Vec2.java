package math;

import java.io.Serializable;

/**
 * Vec2 class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Vec2 implements Serializable {

    public double x;
    public double y;

    public Vec2() {
    }

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void sub(Vec2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void sub(double x, double y) {
        this.x -= x;
        this.y -= y;
    }
    
    public void multiply(double s) {
        this.x *= s;
        this.y *= s;
    }

    public void setLerp(Vec2 a, Vec2 b, double p) {
        x = a.x + p * (b.x - a.x);
        y = a.y + p * (b.y - a.y);
    }
    
    @Override
    public String toString() {
        return "Vec2{" + "x=" + x + ", y=" + y + '}';
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }
    
}
