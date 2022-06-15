package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;
import math.Vec2;


/**
 *
 * @author Leo
 */
public class Triangle extends Polygon {
    
    private String id;
    private Wall[] walls = new Wall[3];
    
    //
    private double increaseWallsRetrieveRadius;
    
    
    private static int currentVisitedId = 1;
    private int visitedId = 0;
    
    private double totalArea;
    
//    public Triangle(String id, Vertex va, Vertex vb, Vertex vc, double increaseWallsRetrieveRadius) {
//        this(id, new Wall(va, vb), new Wall(vb, vc), new Wall(vc, va), increaseWallsRetrieveRadius);
//    }

    private int pathDistance;
    
    public Triangle(String id, Wall wallA, Wall wallB, Wall wallC, double increaseWallsRetrieveRadius) {
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
        calculateSubareas(-2980, 2950);
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
        double height = p1 * walls[0].getA().getHeight() + p2 * walls[1].getA().getHeight() + p3 * walls[2].getA().getHeight();
        return height;
    }
    
    public void draw(Graphics2D g) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
        
        double x = 0.33 * (walls[0].x1 + walls[1].x1 + walls[2].x1);
        double y = 0.33 * (walls[0].y1 + walls[1].y1 + walls[2].y1);
        g.setColor(Color.GRAY);
        g.drawString(id + " path:" + pathDistance, (int) x, (int) y);
    }

    public void getBaricenter(Vec2 v) {
        v.x = 0.33 * (walls[0].x1 + walls[1].x1 + walls[2].x1);
        v.y = 0.33 * (walls[0].y1 + walls[1].y1 + walls[2].y1);
    }

    public void retrieveCollidingWalls(PlayerMonster player, List<Wall> collidingWalls) {
        retrieveCollidingWallsInternal(currentVisitedId++, player, collidingWalls);
    }
    
    private void retrieveCollidingWallsInternal(int visitedId, PlayerMonster player, List<Wall> collidingWalls) {
        if (this.visitedId == visitedId) return;
        this.visitedId = visitedId;
        for (Wall wall : walls) {
            // + 1.0 helps to get the additional walls correctly
            if (wall.ptSegDist(player.x, player.y) <= player.radius + increaseWallsRetrieveRadius) {
                if (!wall.isBlocking()) {
                    Triangle nextTriangle = wall.getNextTriangle();
                    nextTriangle.retrieveCollidingWallsInternal(visitedId, player, collidingWalls);
                }
                else {
                    collidingWalls.add(wall);
                }
            }
        }
    }

    private static int currentTriangleVisitedCount;
        
    public Triangle getCurrentTriangle(PlayerMonster player) {
        currentTriangleVisitedCount = 0;
        Triangle t = getCurrentTriangleInternal(null, this, player);
        System.out.println("currentTriangleVisitedCount=" + currentTriangleVisitedCount);
        return t;
    }
    
    public Triangle getCurrentTriangleInternal(Triangle previous, Triangle current, PlayerMonster player) {
        currentTriangleVisitedCount++;
        if (current.contains(player.x, player.y)) {
            return current;
        }
        else {
            for (Wall wall : this.getWalls()) {
                if (!wall.isBlocking() && wall.ptSegDist(player.x, player.y) <= player.radius) {
                    Triangle next = wall.getNextTriangle();
                    if (next != previous) {
                        Triangle r = next.getCurrentTriangleInternal(current, next, player);
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

    public void drawRecursive(Graphics2D g, PlayerMonster player, int width, int height) {
        rectangleTmp.setBounds((int) (player.x - width / 2), (int) (player.y - height / 2), width, height);
        drawRecursiveInternal(currentVisitedId++, g, player);
    }
    
    
    private void drawRecursiveInternal(int visitedId, Graphics2D g, PlayerMonster player) {
        if (this.visitedId == visitedId) return;
        this.visitedId = visitedId;
        
        if (!rectangleTmp.contains(walls[0].x1, walls[0].y1) && !rectangleTmp.contains(walls[0].x2, walls[0].y2)
                && !rectangleTmp.contains(walls[1].x1, walls[1].y1) && !rectangleTmp.contains(walls[1].x2, walls[1].y2)
                && !rectangleTmp.contains(walls[2].x1, walls[2].y1) && !rectangleTmp.contains(walls[2].x2, walls[2].y2)) {
            
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
    
    @Override
    public String toString() {
        return "Triangle{" + "id=" + id + '}';
    }
    
}
