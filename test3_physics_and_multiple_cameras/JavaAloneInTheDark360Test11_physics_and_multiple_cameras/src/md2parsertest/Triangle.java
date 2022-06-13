package md2parsertest;

/**
 *
 * @author leonardo
 */
public class Triangle {
    
    public int[] index_xyz;
    public int[] index_st;

    public Triangle(Header h, int i) {
        DataStream ds = h.getDataStream();
        ds.setPosition(h.getOfs_tris() + 12 * i);
        
        index_xyz = new int[3];
        index_xyz[0] = ds.getNextShort();
        index_xyz[1] = ds.getNextShort();
        index_xyz[2] = ds.getNextShort();
        
        index_st = new int[3];
        index_st[0] = ds.getNextShort();
        index_st[1] = ds.getNextShort();
        index_st[2] = ds.getNextShort();
    }

    @Override
    public String toString() {
        return "Triangle{" + "index_xyz=" + index_xyz + ", index_st=" + index_st + '}';
    }
    
}
