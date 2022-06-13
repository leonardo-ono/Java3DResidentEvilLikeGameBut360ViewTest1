
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
    private Triangle currentTriangle;

    public World() {
        level = new Level("test4.obj", 50.0);
    }
    
    public void update() {
    }

    public void draw(Graphics2D g, Player player) {
        g.setColor(Color.BLACK);
        //level.getWalls().forEach(wall -> {
        //    wall.draw(g);
        //});
        if (currentTriangle != null) {
            currentTriangle.drawRecursive(g, player, 4096, 4096);
        }
    }

    private List<Wall> collidingWalls = new ArrayList<>();
    
    public boolean collideAndSlide(Player player) {
        if (currentTriangle == null) {
            // the first must be correct
            for (Triangle triangle : level.getTriangles()) {
                if (triangle.contains(player.x, player.y)) {
                    currentTriangle = triangle;
                    break;
                }
            }
        }

        if (currentTriangle == null) {
            return false;
        }
        
        collidingWalls.clear();
        
        currentTriangle.retrieveCollidingWalls(player, collidingWalls);
        
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
        currentTriangle = currentTriangle.getCurrentTriangle(player);
        
        if (currentTriangle != null) {
            player.height = currentTriangle.getHeight(player.x, player.y);
        }
        
        return result;
    }

}
