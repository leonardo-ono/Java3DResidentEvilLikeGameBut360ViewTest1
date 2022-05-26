package md2parsertest;

/**
 * http://tfc.duke.free.fr/old/models/md2.htm
 * 
 * @author leonardo
 */
public class Header {

    private int ident;              // magic number. must be equal to "IDP2" 844121161
    private int version;            // md2 version. must be equal to 8
    private int skinwidth;          // width of the texture
    private int skinheight;         // height of the texture
    private int framesize;          // size of one frame in bytes
    private int num_skins;          // number of textures
    private int num_xyz;            // number of vertices
    private int num_st;             // number of texture coordinates
    private int num_tris;           // number of triangles
    private int num_glcmds;         // number of opengl commands
    private int num_frames;         // total number of frames
    private int ofs_skins;          // offset to skin names (64 bytes each)
    private int ofs_st;             // offset to s-t texture coordinates
    private int ofs_tris;           // offset to triangles
    private int ofs_frames;         // offset to frame data
    private int ofs_glcmds;         // offset to opengl commands
    private int ofs_end;            // offset to end of file
    private DataStream dataStream;
    
    public Header(DataStream dataStream) {
        this.dataStream = dataStream;
        dataStream.setPosition(0);
        ident = dataStream.getNextInt();
        version = dataStream.getNextInt();
        skinwidth = dataStream.getNextInt();
        skinheight = dataStream.getNextInt();
        framesize = dataStream.getNextInt();
        num_skins = dataStream.getNextInt();
        num_xyz = dataStream.getNextInt();
        num_st = dataStream.getNextInt();
        num_tris = dataStream.getNextInt();
        num_glcmds = dataStream.getNextInt();
        num_frames = dataStream.getNextInt();
        ofs_skins = dataStream.getNextInt();
        ofs_st = dataStream.getNextInt();
        ofs_tris = dataStream.getNextInt();
        ofs_frames = dataStream.getNextInt();
        ofs_glcmds = dataStream.getNextInt();
        ofs_end = dataStream.getNextInt();
    }

    public int getIdent() {
        return ident;
    }

    public int getVersion() {
        return version;
    }

    public int getSkinwidth() {
        return skinwidth;
    }

    public int getSkinheight() {
        return skinheight;
    }

    public int getFramesize() {
        return framesize;
    }

    public int getNum_skins() {
        return num_skins;
    }

    public int getNum_xyz() {
        return num_xyz;
    }

    public int getNum_st() {
        return num_st;
    }

    public int getNum_tris() {
        return num_tris;
    }

    public int getNum_glcmds() {
        return num_glcmds;
    }

    public int getNum_frames() {
        return num_frames;
    }

    public int getOfs_skins() {
        return ofs_skins;
    }

    public int getOfs_st() {
        return ofs_st;
    }

    public int getOfs_tris() {
        return ofs_tris;
    }

    public int getOfs_frames() {
        return ofs_frames;
    }

    public int getOfs_glcmds() {
        return ofs_glcmds;
    }

    public int getOfs_end() {
        return ofs_end;
    }

    public DataStream getDataStream() {
        return dataStream;
    }

    @Override
    public String toString() {
        return "Header{" + "ident=" + ident + ", version=" + version + ", skinwidth=" + skinwidth + ", skinheight=" + skinheight + ", framesize=" + framesize + ", num_skins=" + num_skins + ", num_xyz=" + num_xyz + ", num_st=" + num_st + ", num_tris=" + num_tris + ", num_glcmds=" + num_glcmds + ", num_frames=" + num_frames + ", ofs_skins=" + ofs_skins + ", ofs_st=" + ofs_st + ", ofs_tris=" + ofs_tris + ", ofs_frames=" + ofs_frames + ", ofs_glcmds=" + ofs_glcmds + ", ofs_end=" + ofs_end + '}';
    }
    
}
