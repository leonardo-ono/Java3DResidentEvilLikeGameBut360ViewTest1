package math;

import java.io.Serializable;

/**
 * Vec3 class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Vec3 implements Serializable {

    public double x;
    public double y;
    public double z;

    public Vec3() {
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void add(Vec3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public void sub(Vec3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public void scale(double s) {
        scale(s, s, s);
    }

    public void scale(double sx, double sy, double sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }
    
    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public void normalize() {
        double length = getLength();
        double lengthInv = length == 0.0 ? 0.0 : 1.0 / getLength();
        scale(lengthInv);
    }
    
    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public void cross(Vec3 v) {
        double cx = y * v.z - z * v.y;
        double cy = z * v.x - x * v.z;
        double cz = x * v.y - y * v.x;       
        x = cx;
        y = cy;
        z = cz;
    }
    
    public void rotateY(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double nx = x * c - z * s;
        double nz = x * s + z * c;
        x = nx;
        z = nz;
    }

    public void rotateX(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double nz = z * c - y * s;
        double ny = z * s + y * c;
        z = nz;
        y = ny;
    }

    public void setLerp(Vec3 a, Vec3 b, double p) {
        x = a.x + p * (b.x - a.x);
        y = a.y + p * (b.y - a.y);
        z = a.z + p * (b.z - a.z);
    }
    
    @Override
    public String toString() {
        return "Vec3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public double getCos(Vec3 o) {
        return dot(o) / (getLength() * o.getLength());
    }
    
}
