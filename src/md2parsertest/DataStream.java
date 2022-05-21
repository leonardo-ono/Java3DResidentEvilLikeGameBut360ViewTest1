package md2parsertest;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author leonardo
 */
public class DataStream {

    private byte[] data;
    private int position;
    
    public DataStream(String resource) throws Exception {
        //File resourceFile = new File(getClass().getResource(resource).toURI());
        InputStream is = getClass().getResourceAsStream(resource);
        data = is.readAllBytes();
        //is.read(data);
        is.close();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    private int getNextData() {
        return data[position++] & 0xFF;
    }
    
    // 1 byte
    public int getNextByte() {
        return getNextData();
    }

    // 2 bytes
    public int getNextShort() {
        int a = getNextData();
        int b = getNextData();
        int r = a + (b << 8);
        return r;
    }
    
    // 4 bytes int
    public int getNextInt() {
        int a = getNextData();
        int b = getNextData();
        int c = getNextData();
        int d = getNextData();
        int r = a + (b << 8) + (c << 16) + (d << 24);
        return r;
    }
    
    // 4 bytes float
    public float getNextFloat() {
        return Float.intBitsToFloat( getNextInt() );
    }
    
    public String getString(int length) {
        String r = "";
        while (length > 0) {
            if (data[position] > 0) {
                r += (char) data[position];
            }
            position++;
            length--;
        }
        return r;
    }
    
    public void dispose() {
        data = null;
    }
    
}
