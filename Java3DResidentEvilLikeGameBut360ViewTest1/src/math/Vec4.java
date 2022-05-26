package math;

import java.io.Serializable;

/**
 * Vec4 class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Vec4 implements Serializable {

    public double x;
    public double y;
    public double z;
    public double w = 1.0;
    
    public Vec4() {
    }

    public Vec4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(Vec4 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(Vec4 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public void add(Vec4 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public void sub(Vec4 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public void scale(double s) {
        x *= s;
        y *= s;
        z *= s;
    }

    public void translate(double dx, double dy, double dz) {
        x += dx;
        y += dy;
        z += dz;
    }
    
    public void rotateZ(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double nx = x * c - y * s;
        double ny = x * s + y * c;
        x = nx;
        y = ny;
    }
    
    public void rotateY(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double nx = x * c - z * s;
        double nz = x * s + z * c;
        x = nx;
        z = nz;
    }
    
    public void doPerspectiveDivision() {
        x = x / w;
        y = y / w;
        z = z / w;
        w = 1.0;
    }

    public double getSize() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public void normalize() {
        double sizeInv = 1 / getSize();
        scale(sizeInv);
    }
    
    public double dot(Vec4 v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public double getRelativeCosBetween(Vec4 v) {
        return dot(v) / (getSize() * v.getSize());
    }

    public double getRelativeAngleBetween(Vec4 v) {
        return Math.acos(getRelativeCosBetween(v));
    }

    public void setLerp(Vec4 a, Vec4 b, double p) {
        x = a.x + p * (b.x - a.x);
        y = a.y + p * (b.y - a.y);
        z = a.z + p * (b.z - a.z);
        w = a.w + p * (b.w - a.w);
    }
    
    public static void cross(Vec4 a, Vec4 b, Vec4 r) {
        r.x = a.y * b.z - a.z * b.y;
        r.y = a.z * b.x - a.x * b.z;
        r.z = a.x * b.y - a.y - b.x;      
    }
    
    public static void sub(Vec4 a, Vec4 b, Vec4 r) {
        r.x = a.x - b.x;
        r.y = a.y - b.y;
        r.z = a.z - b.z;
    }

    public static void lerp(Vec4 a, Vec4 b, Vec4 r, double p) {
        p = p < 0 ? 0 : p > 1 ? 1 : p;
        r.x = a.x + (b.x - a.x) * p;
        r.y = a.y + (b.y - a.y) * p;
        r.z = a.z + (b.z - a.z) * p;
    }
    
    public void cross(Vec4 v) {
        double cx = y * v.z - z * v.y;
        double cy = z * v.x - x * v.z;
        double cz = x * v.y - y * v.x;       
        x = cx;
        y = cy;
        z = cz;
    }
    
    @Override
    public String toString() {
        return "Vec4{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

}
