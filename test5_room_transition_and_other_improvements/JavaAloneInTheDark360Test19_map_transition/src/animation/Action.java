package animation;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Animation Action (Point Cache 2 Reader)
 * 
 * References:
 * https://gist.github.com/iyadahmed/b0ba3c375e67fc5ed0f2de560fda27d4
 * https://blenderartists.org/t/point-cache-2-exporter/398999/16
 * https://forums.cgsociety.org/t/writing-a-file-for-point-cache/1287799/4
 * 
 * header
 * char : header (12) – “POINTCACHE2” followed by a null char
 * int : version – currently 1 (unsigned)
 * int : number of points – points per sample (unsigned)
 * float : start frame
 * float : samplerate
 * int : number of samples (unsigned)
 * 
 * positions
 * float : x
 * float : y
 * float : z
 * …
 * … 
 *
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Action {
    
    private String id;
    //private byte[] header = new byte[12];
    private int version;
    private int pointsCount;
    //private float startFrame;
    private float sampleRate;
    private int samplesCount;
    
    // positionts[sample][point]
    private double[][] positions;
    
    private int fps;
    private int startFrame;
    private int endFrame;
    private boolean loop;

    public Action(String id, String resPath, String res, int fps, int startFrame, int endFrame, boolean loop) {
        this(id, resPath, res);
        this.fps = fps;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.loop = loop;
    }

    public Action(String id, String resPath, String res) {
        this.id = id;
        try (
            DataInputStream dis 
                    = new DataInputStream(getClass().getResourceAsStream(resPath + res));
        ) 
        {
            dis.read(new byte[12]);
            version = fixInt(dis.readInt());
            pointsCount = fixInt(dis.readInt());
            float startFrame = fixFloat(dis.readInt());
            sampleRate = fixFloat(dis.readInt());
            samplesCount = fixInt(dis.readInt());
            
            positions = new double[samplesCount][pointsCount * 3];
            for (int s = 0; s < samplesCount; s++) {
                for (int p = 0; p < pointsCount * 3; p++) {
                    positions[s][p] = fixFloat(dis.readInt());
                }
            }
            //System.out.println("finished!" + (samplesCount * pointsCount * 3 * 4));
        } catch (IOException ex) {
            Logger.getLogger(Action.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int fixInt(int v) {
        return (int) (((v & 0xffl) << 24) + ((v & 0xff00l) << 8) 
                + ((v & 0xff0000l) >> 8) + ((v & 0xff000000l) >> 24));
    }
    
    private float fixFloat(int v) {
        return Float.intBitsToFloat(fixInt(v));
    }

//    public byte[] getHeader() {
//        return header;
//    }

    public int getVersion() {
        return version;
    }

    public int getPointsCount() {
        return pointsCount;
    }

//    public float getStartFrame() {
//        return startFrame;
//    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public double[][] getPositions() {
        return positions;
    }

    public String getId() {
        return id;
    }

    public int getFps() {
        return fps;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public boolean isLoop() {
        return loop;
    }

    @Override
    public String toString() {
        return "Action{" + "id=" + id + '}';
    }
    
}
