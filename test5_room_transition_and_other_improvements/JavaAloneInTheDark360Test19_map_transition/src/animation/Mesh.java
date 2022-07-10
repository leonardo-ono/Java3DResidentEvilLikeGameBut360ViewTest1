package animation;

import java.util.*;

/**
 * Animation Mesh (Wavefront format)
 * 
 * @author Leo
 */
public class Mesh {
    
    private int[][] faces;
    private double[][] textureCoordinates;

    public Mesh(String res) {
        loadModel(res);
    }
    
    private void loadModel(String res) {
        try ( Scanner sc = new Scanner(getClass().getResourceAsStream(res)) ) {
            List<int[]> facesTmp = new ArrayList<>();
            List<double[]> stsTmp = new ArrayList<>();
            sc.useDelimiter("[ /\n]");
            while (sc.hasNext()) {
                String token = sc.next();
                if (token.equals("vt")) {
                    stsTmp.add(
                        new double[] { sc.nextDouble(), sc.nextDouble() } );
                }
                else if (token.equals("f")) {
                    int   v1 = sc.nextInt() - 1, st1 = sc.nextInt() - 1
                        , v2 = sc.nextInt() - 1, st2 = sc.nextInt() - 1
                        , v3 = sc.nextInt() - 1, st3 = sc.nextInt() - 1;
                    facesTmp.add( new int[] { v1, v2, v3, st1, st2, st3 } );
                }
            }
            faces = facesTmp.toArray(new int[0][0]);
            textureCoordinates = stsTmp.toArray(new double[0][0]);
        }
    }

    public int[][] getFaces() {
        return faces;
    }

    public double[][] getTextureCoordinates() {
        return textureCoordinates;
    }
        
}
