package md2parsertest;

/**
 *
 * @author leonardo
 */
public class Frame {

    public double[] scale;
    public double[] translate;
    public String name;
    public int[][] vertices; 
    
    public Frame(Header h, int index) {
        DataStream ds = h.getDataStream();
        
        ds.setPosition(h.getOfs_frames() + h.getFramesize() * index);
        
        scale = new double[3];
        scale[0] = ds.getNextFloat();
        scale[1] = ds.getNextFloat();
        scale[2] = ds.getNextFloat();

        translate = new double[3];
        translate[0] = ds.getNextFloat();
        translate[1] = ds.getNextFloat();
        translate[2] = ds.getNextFloat();
        
        name = ds.getString(16);

        // fetch all vertices
        vertices = new int[h.getNum_xyz()][4];
        
        for (int i=0; i<h.getNum_xyz(); i++) {
            vertices[i][0] = ds.getNextByte();
            vertices[i][1] = ds.getNextByte();
            vertices[i][2] = ds.getNextByte();
            vertices[i][3] = ds.getNextByte();
        }
    }

    public double[] getScale() {
        return scale;
    }

    public double[] getTranslate() {
        return translate;
    }

    public String getName() {
        return name;
    }
    
    public String getBaseName() {
        String baseName = "";
        for (int c=0; c<name.length(); c++) {
            if (Character.isLetter(name.charAt(c))) {
                baseName += name.charAt(c);
            }
            else {
                break;
            }
        }
        return baseName;
    }

    public int[][] getVertices() {
        return vertices;
    }
    
    @Override
    public String toString() {
        return "Frame{" + "scale=" + scale + ", translate=" + translate + ", name=" + name + ", vertices=" + vertices + '}';
    }
    
}
