package physics;

/**
 * Body class
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 * @param <T> user data 1 type
 * @param <E> user data 2 type
 */
public class Body<T, E> {
    
    private final String id;

    public double x;
    public double y;
    public double height;
    
    public double angle;
    public double speed = 3;
    public double vx;
    public double vy;
    public double radius;
    
    private Triangle currentTriangle;
    
    private double sensorMaxAngle;

    private T userData;
    private E userData2;
    
    public Body(String id, double x, double y, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public Triangle getCurrentTriangle() {
        return currentTriangle;
    }

    void setCurrentTriangle(Triangle bodyCurrentTriangle) {
        this.currentTriangle = bodyCurrentTriangle;
    }

    public double getSensorMaxAngle() {
        return sensorMaxAngle;
    }

    public void setSensorMaxAngle(double sensorMaxAngle) {
        this.sensorMaxAngle = sensorMaxAngle;
    }

    public T getUserData() {
        return userData;
    }

    public void setUserData(T userData) {
        this.userData = userData;
    }

    public E getUserData2() {
        return userData2;
    }

    public void setUserData2(E userData2) {
        this.userData2 = userData2;
    }
    
    public boolean collides(Body other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double l = dx * dx + dy * dy;
        double r = other.radius + radius;
        return (l <= r * r);
    }
    
    public boolean collidesWithSensor(Body other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double l = dx * dx + dy * dy;
        double r = other.radius + radius;
        if (l > r * r) {
            return false;
        }
        // check angle
        double length = 1.0 / Math.sqrt(l);
        dx *= length;
        dy *= length;
        double cross = Math.cos(other.angle) * dx + Math.sin(other.angle) * dy;
        return Math.acos(cross) <= sensorMaxAngle;
    }

    public Body getCollidingSensor() {
        if (currentTriangle != null) {
            return currentTriangle.getCollidingSensor(this);
        }
        return null;
    }

    public boolean separate(World world, Body b2) {
        if (!collides(b2)) {
            return false;
        }

        double dx = b2.x - x;
        double dy = b2.y - y;
        double dist = Math.hypot(dx, dy);
        double lengthInv = 1.0 / dist;
        
        dx *= lengthInv;
        dy *= lengthInv;
        
        double penetration = (b2.radius + radius - dist) + 0.1;
        
        x -= penetration * 0.5 * dx;
        y -= penetration * 0.5 * dy;
        b2.x += penetration * 0.5 * dx;
        b2.y += penetration * 0.5 * dy;

        speed=0;
        b2.speed=0;
        world.collideAndSlide(this);
        world.collideAndSlide(b2);
        
        return true;
    }

}
