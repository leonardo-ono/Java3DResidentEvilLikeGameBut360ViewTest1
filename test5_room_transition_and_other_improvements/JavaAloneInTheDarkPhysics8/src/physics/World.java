package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * World class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class World {

    private final Level level;
    private final List<Body> rigidBodies = new ArrayList<>();
            
    public World(String levelMeshRes, double scale) {
        level = new Level(levelMeshRes, scale);
    }

    public Level getLevel() {
        return level;
    }
    
    public void addRigidBody(Body body) {
        rigidBodies.add(body);
    }
    
    // separate rigid bodies
    public void update() {
        for (Body b1 : rigidBodies) {
            for (Body b2 : rigidBodies) {
                if (b1 == b2) {
                    continue;
                }
                b1.separate(this, b2);
            }
        }
    }
    
    public void draw(Graphics2D g, Body body) {
        g.setColor(Color.BLACK);
        
        level.getWalls().forEach(wall -> {
            wall.draw(g);
            if (wall.getNextTriangle() != null) {
                wall.getNextTriangle().draw(g);
            }
        });
        
        if (body.getCurrentTriangle() != null) {
            body.getCurrentTriangle().drawRecursive(g, body, 400, 300);
        }
        
        rigidBodies.forEach(b -> drawBody(g, b));
    }

    private final List<Wall> collidingWalls = new ArrayList<>();
    
    public boolean collideAndSlide(Body body) {
        body.vx = body.speed * Math.cos(body.angle);
        body.vy = body.speed * Math.sin(body.angle);
        
        body.x += body.vx;
        body.y += body.vy;
        
        if (body.getCurrentTriangle() == null) {
            // the first must be correct
            for (Triangle triangle : level.getTriangles()) {
                if (triangle.contains(body.x, body.y)) {
                    body.setCurrentTriangle(triangle);
                    break;
                }
            }
        }

        if (body.getCurrentTriangle() == null) {
            return false;
        }
        
        collidingWalls.clear();
        body.getCurrentTriangle().retrieveCollidingWalls(body, collidingWalls);
        
        boolean result = false;
        boolean collides = true;
        while (collides) {
            collides = false;
            for (Wall wall : collidingWalls) {
                double d = wall.ptSegDist(body.x, body.y);
                if (d <= body.radius) {

                    double dx = wall.getX2() - wall.getX1();
                    double dy = wall.getY2() - wall.getY1();
                    double length = Math.sqrt(dx * dx + dy * dy);
                    dx /= length;
                    dy /= length;

                    double nx = -dy;
                    double ny = dx;

                    double speed = nx * body.vx + ny * body.vy;

                    double nvx = body.vx - speed * nx;
                    double nvy = body.vy - speed * ny;

                    body.vx -= nvx;
                    body.vy -= nvy;

                    double ax = nx * (body.radius - d + 0.1);
                    double ay = ny * (body.radius - d + 0.1);
                    body.x += ax;
                    body.y += ay;

                    collides = true;
                    result = result || collides;
                    //break;
                }
            }
        }
        
        // check if player changed current triangle
        body.setCurrentTriangle(
                body.getCurrentTriangle().getCurrentTriangle(body));
        
        if (body.getCurrentTriangle() != null) {
            body.height = body.getCurrentTriangle().getHeight(body.x, body.y);
        }
        
        return result;
    }

    
    public static void drawBody(Graphics2D g, Body body) {
        g.setColor(Color.BLACK);
        g.drawOval((int) (body.x - body.radius), (int) (body.y - body.radius)
                            , (int) (2 * body.radius), (int) (2 * body.radius));
        
        double dx = body.radius * Math.cos(body.angle);
        double dy = body.radius * Math.sin(body.angle);        
        g.setColor(Color.BLACK);
        g.drawLine((int) body.x, (int) body.y
                , (int) (body.x + dx), (int) (body.y + dy));
        
        g.drawString(body.getId() + " h:" + ((int) body.height)
                , (int) (body.x + body.radius), (int) body.y);
        
    }

    public void addSensor(String triangleId
                    , double x, double y, double radius, double maxAngle) {
        
        for(Triangle triangle : level.getTriangles()) {
            if (triangle.getId().equals(triangleId)) {
                Body sensor = new Body("sensor_" + triangleId, x, y, radius);
                sensor.setSensorMaxAngle(maxAngle);
                triangle.setSensor(sensor);
                return;
            }
        }
    }
    
    public static class RaycastResult {
        public double length;
        public Body closestBody;
        public Wall closestWall;
        public Point2D source = new Point2D.Double();
        public Point2D hit = new Point2D.Double();
        
        public void clear() {
            length = 0;
            closestBody = null;
            closestWall = null;
            source.setLocation(0, 0);
            hit.setLocation(0, 0);
        }
    }
    
    private final Line2D rayTmp = new Line2D.Double();
    private final List<Triangle> collidingTriangles = new ArrayList<>();
    
    // this implementation didn't work well
    // depending the body's position, the ray can't intersect
    // the edges of current triangle properly.
    //
    // castRay2() method ia the workaround slower version for now
    public void castRay(
            Body source, double angle, double length, RaycastResult result) {
        
        result.clear();
        
        if (source.getCurrentTriangle() == null) {
            return;
        }
        
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);

        int visitedId = Triangle.currentVisitedId++;
        Triangle currentTriangle = source.getCurrentTriangle();

        double shortestDist = Double.MAX_VALUE;
        for (Wall wall : currentTriangle.getWalls()) {
            double dist = wall.ptLineDist(source.x, source.y);
            if (dist < shortestDist) {
                shortestDist = dist;
            }
        }
        double tx = 0;
        double ty = 0;
        if (shortestDist < source.radius) {
            tx = source.radius;
            ty = source.radius;
        }
        //System.out.println("" + shortestDist);

        result.source.setLocation(source.x + tx * dx, source.y + ty * dy);
        result.hit.setLocation(source.x + length * dx, source.y + length * dy);

        rayTmp.setLine(result.source.getX(), result.source.getY()
                            , result.hit.getX(), result.hit.getY());

        // current triangle is not the start
        // try to find the start triangle
        if (!currentTriangle.contains(result.source)) {
            collidingTriangles.clear();
            currentTriangle.retrieveCollidingTriangles(
                                            source, collidingTriangles);

            for (Triangle triangle : collidingTriangles) {
                if (triangle.contains(result.source)) {
                    currentTriangle = triangle;
                    break;
                }
            }
        }

        boolean ni = false;
        while (!ni) {
            
        while (currentTriangle != null) {
            if (currentTriangle.visitedId == visitedId) {
                break;
            }
            currentTriangle.visitedId = visitedId;
            for (Wall wall : currentTriangle.getWalls()) {
                if (!wall.isBlocking() 
                        && wall.getNextTriangle().visitedId == visitedId) {

                    continue;
                }
                else if (wall.intersectsLine(rayTmp)) {
                    ni = true;
                    if (wall.isBlocking()) {
                        Wall.getIntersectionPoint(wall, rayTmp, result.hit);
                        result.closestWall = wall;
                        currentTriangle = null;
                    }
                    else {
                        currentTriangle = wall.getNextTriangle();
                        currentTriangle.visitedId = 0;
                    }
                    break;
                }
            }
        }
        
        if (!ni) {
            System.out.println("ni!" + System.nanoTime());
            currentTriangle = source.getCurrentTriangle();
            source.speed = 0.01;
            source.angle += 0.00001;
            tx += Math.random();
            ty += Math.random();
            collideAndSlide(source);
            result.source.setLocation(source.x + tx * dx, source.y + ty * dy);
            result.hit.setLocation(
                    source.x + length * dx, source.y + length * dy);

            rayTmp.setLine(result.source.getX(), result.source.getY()
                                , result.hit.getX(), result.hit.getY());

            // current triangle is not the start
            // try to find the start triangle
            if (!currentTriangle.contains(result.source)) {
                collidingTriangles.clear();
                currentTriangle.retrieveCollidingTriangles(
                                            source, collidingTriangles);
                
                for (Triangle triangle : collidingTriangles) {
                    if (triangle.contains(result.source)) {
                        currentTriangle = triangle;
                        break;
                    }
                }
            }
        
        }
        }
    }
    
    private final Body rayBodyTmp = new Body("ray_tmp", 0, 0, 10);
    
    public void castRay2(
            Body source, double angle, double length, RaycastResult result) {

        //System.out.println("--- start raycast ---");

        result.clear();
        
        if (source.getCurrentTriangle() == null) {
            return;
        }
        
        rayBodyTmp.angle = source.angle;
        rayBodyTmp.x = source.x;
        rayBodyTmp.y = source.y;
        rayBodyTmp.setCurrentTriangle(source.getCurrentTriangle());
        
        rayBodyTmp.speed = 8;
        rayBodyTmp.vx = rayBodyTmp.speed * Math.cos(rayBodyTmp.angle);
        rayBodyTmp.vy = rayBodyTmp.speed * Math.sin(rayBodyTmp.angle);
        
        int iterations = 0;
        for (int i = 0; i < (int) (length / 8); i++) {
            iterations++;
            if (raycastCollideAndStop(rayBodyTmp, source, result)) {
                break;
            }
        }
        
        //System.out.println("iterations: " + iterations);
        
        result.source.setLocation(source.x, source.y);
        result.hit.setLocation(rayBodyTmp.x, rayBodyTmp.y);

        //System.out.println("--- end raycast ---");
    }

    private boolean raycastCollideAndStop(
                Body body, Body ignore, RaycastResult result) {
        
        body.x += body.vx;
        body.y += body.vy;
        
        // collided with other body
        for (Body b : rigidBodies) {
            if (b == body) continue;
            if (b == ignore) continue;
            if (b.collides(body)) {
                result.closestBody = b;
                return true;
            }
        }

        if (body.getCurrentTriangle() == null) {
            return false;
        }
        
        collidingWalls.clear();
        body.getCurrentTriangle().retrieveCollidingWalls(body, collidingWalls);
        
        // check if player changed current triangle
        body.setCurrentTriangle(
                body.getCurrentTriangle().getCurrentTriangle(body));
        
        if (body.getCurrentTriangle() != null) {
            body.height = body.getCurrentTriangle().getHeight(body.x, body.y);
        }
        
        for (Wall wall : collidingWalls) {
            if (!wall.isBlocking() || wall.canRaycastPassThrough()) {
                continue;
            }
            double d = wall.ptSegDist(body.x, body.y);
            if (d <= body.radius) {
                result.closestWall = wall;
                return true;
            }
        }
    
        return false;
    }
    
    public static void main(String[] args) {
        
        Line2D la = new Line2D.Double(0, 0, 5, 5);
        Line2D lb = new Line2D.Double(0, 5, 5, 0);
        Point2D p = new Point2D.Double();
        
        Wall.getIntersectionPoint(la, lb, p);
        System.out.println("" + (1 ^ 1));
    }
    
}
