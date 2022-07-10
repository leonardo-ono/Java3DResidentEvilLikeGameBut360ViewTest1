package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

/**
 * Triangle class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Triangle extends Polygon {
    
    private final String id;
    private final Wall[] walls = new Wall[3];
    private final double increaseWallsRetrieveRadius;
    
    static int currentVisitedId = 1;
    int visitedId = 0;
    
    private double totalArea;
    
    private int pathDistance;
    
    private Body sensor;
    private double sensorMaxAngle;
    
    private boolean onlyRaycast;
    
    public Triangle(String id, Wall wallA, Wall wallB, Wall wallC
                , double increaseWallsRetrieveRadius, boolean onlyRaycast) {
        
        this.id = id;
        wallA.setOwner(this);
        wallB.setOwner(this);
        wallC.setOwner(this);
        
        walls[0] = wallA;
        walls[1] = wallB;
        walls[2] = wallC;
        
        reset();
        addPoint((int) wallA.x1, (int) wallA.y1);
        addPoint((int) wallB.x1, (int) wallB.y1);
        addPoint((int) wallC.x1, (int) wallC.y1);

        // for walls forming sharp angles, increase this value to 
        // return more walls around and calculate the collision more correctly.
        this.increaseWallsRetrieveRadius = increaseWallsRetrieveRadius;
        
        calculateTotalArea();
        
        this.onlyRaycast = onlyRaycast;
    }

    private void calculateTotalArea() {
        double vx1 = walls[1].x1 - walls[0].x1;
        double vy1 = walls[1].y1 - walls[0].y1;
        double vx2 = walls[2].x1 - walls[0].x1;
        double vy2 = walls[2].y1 - walls[0].y1;
        totalArea = (vx1 * vy2 - vy1 * vx2) * 0.5;
    }
    
    private double subarea1;
    private double subarea2;
    private double subarea3;
    
    private void calculateSubareas(double x, double y) {
        double vx1 = walls[1].x1 - x;
        double vy1 = walls[1].y1 - y;
        double vx2 = walls[2].x1 - x;
        double vy2 = walls[2].y1 - y;
        subarea1 = (vx1 * vy2 - vy1 * vx2) * 0.5;

        vx1 = walls[2].x1 - x;
        vy1 = walls[2].y1 - y;
        vx2 = walls[0].x1 - x;
        vy2 = walls[0].y1 - y;
        subarea2 = (vx1 * vy2 - vy1 * vx2) * 0.5;

        vx1 = walls[0].x1 - x;
        vy1 = walls[0].y1 - y;
        vx2 = walls[1].x1 - x;
        vy2 = walls[1].y1 - y;
        subarea3 = (vx1 * vy2 - vy1 * vx2) * 0.5;
        
        //System.out.println("" + (subarea1 + subarea2 + subarea3));
    }

    public String getId() {
        return id;
    }
    
    public Wall[] getWalls() {
        return walls;
    }
    
    public double getHeight(double x, double y) {
        calculateSubareas(x, y);
        double invTotalArea = 1.0 / totalArea;
        double p1 = subarea1 * invTotalArea;
        double p2 = subarea2 * invTotalArea;
        double p3 = subarea3 * invTotalArea;
        double height = p1 * walls[0].getA().getHeight() 
                            + p2 * walls[1].getA().getHeight() 
                            + p3 * walls[2].getA().getHeight();
        
        return height;
    }

    public boolean isOnlyRaycast() {
        return onlyRaycast;
    }
    
    public void draw(Graphics2D g) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
        
        double x = 0.33 * (walls[0].x1 + walls[1].x1 + walls[2].x1);
        double y = 0.33 * (walls[0].y1 + walls[1].y1 + walls[2].y1);
        g.setColor(Color.GRAY);
        g.drawString(id + " path:" + pathDistance + " rc:" + onlyRaycast, (int) x, (int) y);
        
        if (sensor != null) {
            World.drawBody(g, sensor);
        }
    }

//    public void getBaricenter(Vec2 v) {
//        v.x = 0.33 * (walls[0].x1 + walls[1].x1 + walls[2].x1);
//        v.y = 0.33 * (walls[0].y1 + walls[1].y1 + walls[2].y1);
//    }
    
    public void retrieveCollidingTriangles(
            Body body, List<Triangle> collidingTriangles) {
        
        retrieveCollidingTrianglesInternal(
                currentVisitedId++, body, collidingTriangles);
    }

    private void retrieveCollidingTrianglesInternal(
            int visitedId, Body body, List<Triangle> collidingTriangles) {
        
        if (this.visitedId == visitedId) return;
        this.visitedId = visitedId;
        for (Wall wall : walls) {
            // +increaseWallsRetrieveRadius helps 
            // to get the additional walls correctly
            if (wall.ptSegDist(body.x, body.y) 
                    <= body.radius + increaseWallsRetrieveRadius) {
                
                if (!wall.isBlocking()) {
                    Triangle nextTriangle = wall.getNextTriangle();
                    collidingTriangles.add(nextTriangle);
                    nextTriangle.retrieveCollidingTrianglesInternal(
                                    visitedId, body, collidingTriangles);
                }
            }
        }
    }
    
    public void retrieveCollidingWalls(Body body, List<Wall> collidingWalls) {
        retrieveCollidingWallsInternal(
                currentVisitedId++, body, collidingWalls);
    }
    
    private void retrieveCollidingWallsInternal(
            int visitedId, Body body, List<Wall> collidingWalls) {
        
        if (this.visitedId == visitedId) return;
        this.visitedId = visitedId;
        for (Wall wall : walls) {
            // + 1.0 helps to get the additional walls correctly
            if (wall.ptSegDist(body.x, body.y) 
                    <= body.radius + increaseWallsRetrieveRadius) {
                
                if (!wall.isBlocking()) {
                    Triangle nextTriangle = wall.getNextTriangle();
                    nextTriangle.retrieveCollidingWallsInternal(
                                    visitedId, body, collidingWalls);
                }
                else {
                    collidingWalls.add(wall);
                }
            }
        }
    }

    public Body getCollidingSensor(Body body) {
        return getCollidingSensorInternal(currentVisitedId++, body);
    }

    private Body getCollidingSensorInternal(int visitedId, Body body) {
        
        if (this.visitedId == visitedId) return null;
        this.visitedId = visitedId;
        if (sensor != null && sensor.collidesWithSensor(body)) {
            return sensor;
        }
        for (Wall wall : walls) {
            // +increaseWallsRetrieveRadius helps to 
            // get the additional walls correctly
            if (wall.ptSegDist(body.x, body.y) 
                    <= body.radius + increaseWallsRetrieveRadius 
                        && !wall.isBlocking()) {
                
                    Triangle nextTriangle = wall.getNextTriangle();
                    Body nextSensor = nextTriangle
                            .getCollidingSensorInternal(visitedId, body);
                    
                    if (nextSensor != null) {
                        return nextSensor;
                    }
            }
        }
        return null;
    }

    
    private static int currentTriangleVisitedCount;
        
    public Triangle getCurrentTriangle(Body body) {
        currentTriangleVisitedCount = 0;
        Triangle t = getCurrentTriangleInternal(null, this, body);
        return t;
    }
    
    public Triangle getCurrentTriangleInternal(
            Triangle previous, Triangle current, Body body) {
        
        currentTriangleVisitedCount++;
        if (current.contains(body.x, body.y)) {
            return current;
        }
        else {
            for (Wall wall : this.getWalls()) {
                if ((!wall.isBlocking() || wall.canRaycastPassThrough()) && wall.ptSegDist(
                        body.x, body.y) <= body.radius) {
                    
                    Triangle next = wall.getNextTriangle();
                    if (next != previous) {
                        Triangle r = next.getCurrentTriangleInternal(
                                                        current, next, body);
                        
                        if (r != null) {
                            return r;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public Wall getWallByHash(String hash) {
        for (Wall wall : walls) {
            if (wall.getHash1().equals(hash) || wall.getHash2().equals(hash)) {
                return wall;
            }
        }
        return null;
    }
    
    private static final Rectangle rectangleTmp = new Rectangle();

    public void drawRecursive(
            Graphics2D g, Body body, int width, int height) {
        
        rectangleTmp.setBounds((int) (body.x - width / 2)
                    , (int) (body.y - height / 2), width, height);
        
        drawRecursiveInternal(currentVisitedId++, g, body);
    }
    
    
    private void drawRecursiveInternal(
            int visitedId, Graphics2D g, Body player) {
        
        if (this.visitedId == visitedId) return;
        this.visitedId = visitedId;
        
        if (!rectangleTmp.contains(walls[0].x1, walls[0].y1) 
                && !rectangleTmp.contains(walls[0].x2, walls[0].y2)
                && !rectangleTmp.contains(walls[1].x1, walls[1].y1) 
                && !rectangleTmp.contains(walls[1].x2, walls[1].y2)
                && !rectangleTmp.contains(walls[2].x1, walls[2].y1) 
                && !rectangleTmp.contains(walls[2].x2, walls[2].y2)) {
            
            return;
        }
        
        draw(g);
        //for (Wall wall : walls) {
        //    wall.draw(g);
        //}
        
        for (Wall wall : walls) {
            if (!wall.isBlocking()) {
                Triangle nextTriangle = wall.getNextTriangle();
                nextTriangle.drawRecursiveInternal(visitedId, g, player);
            }
        }
    }

    public int getPathDistance() {
        return pathDistance;
    }

    public void setPathDistance(int pathDistance) {
        this.pathDistance = pathDistance;
    }

    public void setSensor(Body sensor) {
        this.sensor = sensor;
    }

    public Body getSensor() {
        return sensor;
    }

    public double getSensorMaxAngle() {
        return sensorMaxAngle;
    }

    @Override
    public String toString() {
        return "Triangle{" + "id=" + id + '}';
    }
    
}
