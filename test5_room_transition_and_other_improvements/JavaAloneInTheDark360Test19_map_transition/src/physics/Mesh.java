package physics;

import java.util.*;

/**
 * Mesh class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Mesh {
    
    private double[][] vertices;
    private int[][] faces;
    private double[][] textureCoordinates;

    public Mesh(String res) {
        loadModel(res);
    }
    
    private void loadModel(String res) {
        try ( Scanner sc = new Scanner(getClass().getResourceAsStream(res)) ) {
            List<double[]> verticesTmp = new ArrayList<>();
            List<int[]> facesTmp = new ArrayList<>();
            List<double[]> stsTmp = new ArrayList<>();
            int onlyRaycast = 0; // 0 = false / 1 = true
            sc.useDelimiter("[ /\n]");
            while (sc.hasNext()) {
                String token = sc.next();
                //System.out.println("token: " + token);
                if (token.equals("usemtl")) {
                    String raycast = sc.next();
                    onlyRaycast 
                            = raycast.toLowerCase().equals("raycast") ? 1 : 0;
                }
                else if (token.equals("v")) {
                    verticesTmp.add(new double[] { sc.nextDouble()
                                    , sc.nextDouble() , sc.nextDouble() } );
                }
                else if (token.equals("vt")) {
                    stsTmp.add(
                        new double[] { sc.nextDouble(), sc.nextDouble() } );
                }
                else if (token.equals("f")) {
                    int   v1 = sc.nextInt() - 1, st1 = sc.nextInt() - 1, n1 = sc.nextInt()
                        , v2 = sc.nextInt() - 1, st2 = sc.nextInt() - 1, n2 = sc.nextInt()
                        , v3 = sc.nextInt() - 1, st3 = sc.nextInt() - 1, n3 = sc.nextInt();
                    facesTmp.add( new int[] { v1, v2, v3, onlyRaycast } );
                }
            }
            vertices = verticesTmp.toArray(new double[0][0]);
            faces = facesTmp.toArray(new int[0][0]);
            textureCoordinates = stsTmp.toArray(new double[0][0]);
        }
    }

    public double[][] getVertices() {
        return vertices;
    }

    public int[][] getFaces() {
        return faces;
    }

    public double[][] getTextureCoordinates() {
        return textureCoordinates;
    }
        
}
