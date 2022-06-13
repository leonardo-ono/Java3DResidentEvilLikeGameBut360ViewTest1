package physics;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Leo
 */
public class Level {
    

    private Wavefront wavefront;
    private double scale = 1.0;
    
    private List<Vertex> vertices = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();
    private List<Triangle> triangles = new ArrayList<>();

    public Level(String res, double scale) {
        wavefront = new Wavefront(res);
        this.scale = scale;
        
        create();
    }

    public Wavefront getWavefront() {
        return wavefront;
    }

    public double getScale() {
        return scale;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
    
    private void create() {
        createVertices();
        createWalls();
        createTriangles();
        linkTriangles();
    }    

    private void createVertices() {
        int vi = 0;
        for (double[] v : wavefront.getVertices()) {
            Vertex vertex = new Vertex(vi++, v[0] * scale, v[2] * scale, v[1] * scale);
            vertices.add(vertex);
            //System.out.println("creating vertex " + vertex);
        }
    }

    private void createWalls() {
    }

    private void createTriangles() {
        int ti = 0;
        for (int[] face : wavefront.getFaces()) {
            Vertex va = vertices.get(face[2]);
            Vertex vb = vertices.get(face[1]);
            Vertex vc = vertices.get(face[0]);
            
            Wall wallA = new Wall(va, vb);
            Wall wallB = new Wall(vb, vc);
            Wall wallC = new Wall(vc, va);
            
            walls.add(wallA);
            walls.add(wallB);
            walls.add(wallC);
            
            Triangle triangle = new Triangle("" + (ti++), wallA, wallB, wallC, 3.0);
            triangles.add(triangle);
        }
    }

    private void linkTriangles() {
        Map<String, List<Triangle>> wallsTriangles = new HashMap<>();
        
        for (Triangle triangle : triangles) {
            for (Wall wall : triangle.getWalls()) {
                
                List<Triangle> l1 = wallsTriangles.get(wall.getHash1());
                if (l1 == null) {
                    l1 = new ArrayList<>();
                    wallsTriangles.put(wall.getHash1(), l1);
                }
                if (!l1.contains(triangle)) {
                    l1.add(triangle);
                }

                List<Triangle> l2 = wallsTriangles.get(wall.getHash2());
                if (l2 == null) {
                    l2 = new ArrayList<>();
                    wallsTriangles.put(wall.getHash2(), l2);
                }
                if (!l2.contains(triangle)) {
                    l2.add(triangle);
                }
                
            }
        }
        
        for (String wallId : wallsTriangles.keySet()) {
            List<Triangle> ts = wallsTriangles.get(wallId);
            
            if (ts.size() == 2) {
                Triangle t1 = ts.get(0);
                Triangle t2 = ts.get(1);
                
                Wall w1 = t1.getWallByHash(wallId);
                Wall w2 = t2.getWallByHash(wallId);
                
                w1.setNextTriangle(t2);
                w2.setNextTriangle(t1);
                
            }

            //System.out.println("" + wallId + " " + ts);
            
            if (ts.size() > 2) {
                System.err.println("warning: found walls sharing more than 2 triangles !");
            }
        }
        
        
    }
    
    
    public static void main(String[] args) {
        Level level = new Level("test.obj", 100.0);
    }
    
}
