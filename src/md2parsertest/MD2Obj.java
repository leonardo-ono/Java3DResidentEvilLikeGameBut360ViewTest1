package md2parsertest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author leonardo
 */
public class MD2Obj {

    private double scaleFactor;
    
    private DataStream dataStream;
    private Header header;
    private String[] skinNames;
    private TextureCoordinates[] textureCoordinates;
    private Triangle[] triangles;
    private Frame[] frames;

    // animation
    private int startFrame;
    private int endFrame;
    private int currentFrame;
    private String currentAnimationName;
    private double tweening;
    private Map<String, AnimationFramesInfo> animationFramesInfoMap = new HashMap<String, AnimationFramesInfo>();
    
    private class AnimationFramesInfo {
        public String name;
        public int startFrame;
        public int endFrame;
    }
    
    public MD2Obj(String resource, double scaleFactor) throws Exception {
        this.scaleFactor = scaleFactor;
        dataStream = new DataStream(resource);
        header = new Header(dataStream);
        fetchAllSkinNames();
        fetchAllTextureCoordinates();
        fetchAllTriangles();
        fetchAllFrames();
        startFrame = 0;
        endFrame = header.getNum_frames() - 1;
        dataStream.dispose();
    }

    private void fetchAllSkinNames() {
        skinNames = new String[header.getNum_skins()];
        dataStream.setPosition(header.getOfs_skins());
        for (int s = 0; s < skinNames.length; s++) {
            skinNames[s] = dataStream.getString(64);
        }
    }
    
    private void fetchAllTextureCoordinates() {
        textureCoordinates = new TextureCoordinates[header.getNum_st()];
        for (int t = 0; t < textureCoordinates.length; t++) {
            textureCoordinates[t] = new TextureCoordinates(header, t);
        }
    }
    
    private void fetchAllTriangles() {
        triangles = new Triangle[header.getNum_tris()];
        for (int t = 0; t < triangles.length; t++) {
            triangles[t] = new Triangle(header, t);
        }
    }
    
    private void fetchAllFrames() {
        AnimationFramesInfo animationFramesInfo = null;
        frames = new Frame[header.getNum_frames()];
        for (int f=0; f<frames.length; f++) {
            frames[f] = new Frame(header, f);
            //System.out.println(f + " frame=" + frames[f]);
            if (animationFramesInfo == null) {
                animationFramesInfo = new AnimationFramesInfo();
                animationFramesInfo.name = frames[f].getBaseName();
            }
            else if (!frames[f].getBaseName().equals(animationFramesInfo.name) || f == frames.length - 1) {
                animationFramesInfo.endFrame = f == frames.length - 1 ? f : f - 1;
                animationFramesInfoMap.put(animationFramesInfo.name, animationFramesInfo);
                animationFramesInfo = new AnimationFramesInfo();
                animationFramesInfo.name = frames[f].getBaseName();
                animationFramesInfo.startFrame = f;
            }
        }
    }
    
    public DataStream getDataStream() {
        return dataStream;
    }

    public Header getHeader() {
        return header;
    }

    public String[] getSkinNames() {
        return skinNames;
    }

    public Triangle[] getTriangles() {
        return triangles;
    }

    public Frame[] getFrames() {
        return frames;
    }

    public Set<String> getAnimationNames() {
        return animationFramesInfoMap.keySet();
    }

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public String getCurrentAnimationName() {
        return currentAnimationName;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void nextFrame() {
        currentFrame++;
        if (currentFrame > endFrame) {
            currentFrame = startFrame;
        }
    }
    
    public void nextFrame(double t) {
        tweening += t;
        if (tweening >= 1) {
            nextFrame();
            tweening = 0;
        }
    }
    
    public void setAnimation(String name) {
        AnimationFramesInfo animationFramesInfo = animationFramesInfoMap.get(name);
        if (animationFramesInfo == null) {
            throw new RuntimeException("Invalid animation name !");
        }
        startFrame = animationFramesInfo.startFrame;
        endFrame = animationFramesInfo.endFrame;
        currentFrame = startFrame;
        currentAnimationName = name;
    }
    
    private Frame getNextFrameObj() {
        int cf = currentFrame + 1;
        if (cf > endFrame) {
            cf = startFrame;
        }
        return frames[cf];
    } 
    
    private double[] vtmp = new double[8];
    private double[] vtmp2 = new double[8];
    
    // p = indice do vertice do triangulo. pode ser 0, 1 ou 2
    public double[] getTriangleVertex(int triangleIndex, int p) {
        Triangle triangle = triangles[triangleIndex];
        int vertexIndex = triangle.index_xyz[p];
        int stIndex = triangle.index_st[p];
        TextureCoordinates st = textureCoordinates[stIndex];
        setVertexInfo(vertexIndex, vtmp, frames[currentFrame], st);
        if (tweening > 0) {
            setVertexInfo(vertexIndex, vtmp2, getNextFrameObj(), st);
            for (int l=0; l<6; l++) {
                vtmp[l] = lerp (vtmp[l], vtmp2[l], tweening);
            }
        }
        return vtmp;
    }
    
    private void setVertexInfo(int vertexIndex, double[] v, Frame frame, TextureCoordinates st) {
        v[0] = (frame.scale[0] * frame.vertices[vertexIndex][0] + frame.translate[0]) * scaleFactor;
        v[1] = (frame.scale[1] * frame.vertices[vertexIndex][1] + frame.translate[1]) * scaleFactor;
        v[2] = (frame.scale[2] * frame.vertices[vertexIndex][2] + frame.translate[2]) * scaleFactor;
        
        int normalIndex = frame.vertices[vertexIndex][3];
        double[] normal = NormalTable.MD2_NORMAL_TABLE[normalIndex];
        v[3] = normal[0];
        v[4] = normal[1];
        v[5] = normal[2];
        
        v[6] = st.getS();
        v[7] = st.getT();
    }
    
    private double lerp(double start, double end, double p) {
        return start + (end - start) * p;
    }
    
}
