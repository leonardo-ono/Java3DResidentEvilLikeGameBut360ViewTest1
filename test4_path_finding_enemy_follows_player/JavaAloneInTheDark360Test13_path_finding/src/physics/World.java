package physics;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Leo
 */
public class World {

    private Level level;

    public World() {
        level = new Level("/res/test7.obj", 50.0);
    }

    public Level getLevel() {
        return level;
    }

    public List<Wall> getCollidingWalls() {
        return collidingWalls;
    }
    
    public void update() {
    }

    public void draw(Graphics2D g, PlayerMonster player) {
        g.setColor(Color.BLACK);
        //level.getWalls().forEach(wall -> {
        //    wall.draw(g);
        //});
        if (player.getBodyCurrentTriangle() != null) {
            player.getBodyCurrentTriangle().drawRecursive(g, player, 800, 600);
        }
    }

    private List<Wall> collidingWalls = new ArrayList<>();
    
    public boolean collideAndSlide(PlayerMonster player) {
        if (player.getBodyCurrentTriangle() == null) {
            // the first must be correct
            for (Triangle triangle : level.getTriangles()) {
                if (triangle.contains(player.x, player.y)) {
                    player.setBodyCurrentTriangle(triangle);
                    break;
                }
            }
        }

        if (player.getBodyCurrentTriangle() == null) {
            return false;
        }
        
        collidingWalls.clear();
        
        player.getBodyCurrentTriangle().retrieveCollidingWalls(player, collidingWalls);
        
        boolean result = false;
        boolean collides = true;
        while (collides) {
            collides = false;
            for (Wall wall : collidingWalls) {
                double d = wall.ptSegDist(player.x, player.y);
                if (d <= player.radius) {

                    double dx = wall.getX2() - wall.getX1();
                    double dy = wall.getY2() - wall.getY1();
                    double length = Math.sqrt(dx * dx + dy * dy);
                    dx /= length;
                    dy /= length;

                    double nx = -dy;
                    double ny = dx;

                    double speed = nx * player.vx + ny * player.vy;

                    double nvx = player.vx - speed * nx;
                    double nvy = player.vy - speed * ny;

                    player.vx -= nvx;
                    player.vy -= nvy;

                    double ax = nx * (player.radius - d + 0.1);
                    double ay = ny * (player.radius - d + 0.1);
                    player.x += ax;
                    player.y += ay;

                    collides = true;
                    result = result || collides;
                    //break;
                }
            }
        }
        
        // check if player changed current triangle
        player.setBodyCurrentTriangle(player.getBodyCurrentTriangle().getCurrentTriangle(player));
        
        if (player.getBodyCurrentTriangle() != null) {
            player.height = player.getBodyCurrentTriangle().getHeight(player.x, player.y);
        }
        
        return result;
    }

}
