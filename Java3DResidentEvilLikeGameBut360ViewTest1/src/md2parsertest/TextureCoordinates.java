package md2parsertest;

/**
 *
 * @author leonardo
 */
public class TextureCoordinates {

    public double s;
    public double t;
    
    public TextureCoordinates(Header header, int index) {
        DataStream ds = header.getDataStream();
        ds.setPosition(header.getOfs_st() + 4 * index);
        s = ds.getNextShort() / (double) header.getSkinwidth();
        t = ds.getNextShort() / (double) header.getSkinheight();
    }

    public double getS() {
        return s;
    }

    public double getT() {
        return t;
    }
    
}
