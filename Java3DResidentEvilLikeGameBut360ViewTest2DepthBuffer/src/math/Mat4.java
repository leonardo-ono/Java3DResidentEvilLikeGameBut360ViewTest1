package math;

/**
 * Mat4 class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Mat4 {

    public double m00, m01, m02, m03;
    public double m10, m11, m12, m13;
    public double m20, m21, m22, m23;
    public double m30, m31, m32, m33;

    public Mat4() {
    }

    public void set(Mat4 m) {
        m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = m.m03;
        m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = m.m13;
        m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = m.m23;
        m30 = m.m30; m31 = m.m31; m32 = m.m32; m33 = m.m33;
    }

    public void setIdentity() {
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
        m30 = m31 = m32 = 0;
        m00 = m11 = m22 = m33 = 1;
    }

    public void setPerspectiveProjection(double fov, double screenWidth) {
        double d = (screenWidth * 0.5) / Math.tan(fov * 0.5);
        setIdentity();
        m32 = -1 / d;
        m33 = 0.0;
    }
    
    // https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluPerspective.xml
    // https://www.khronos.org/opengl/wiki/GluPerspective_code
    // note: near and far are always positive values
    //       fov is vertical field of view in radians
    public void setPerspective(
            double fov, double aspectRatio, double near, double far) {
        
        double ymax = near * Math.tan(fov * 0.5);
        double xmax = ymax * aspectRatio;
        setFrustum(-xmax, xmax, -ymax, ymax, near, far);
        //setSymmetricFrustrum(xmax, ymax, near, far);
    }

    // ref.: http://www.songho.ca/opengl/gl_projectionmatrix.html
    public void setSymmetricFrustrum(
            double right, double top, double near, double far) {
        
        setIdentity();
        m00 = near / right;
        m11 = near / top;
        m22 = -(far + near) / (far - near);
        m23 = (-2.0 * far * near) / (far - near);
        m32 = -1.0;
        m33 = 0.0;
    }
    
    // ref.: https://www.khronos.org/opengl/wiki/GluPerspective_code
    public void setFrustum(double left, double right
                , double bottom, double top, double near, double far) {
        
        double temp = 2.0 * near;
        double temp2 = right - left;
        double temp3 = top - bottom;
        double temp4 = far - near;
        setIdentity();
        m00 = temp / temp2;
        m11 = temp / temp3;
        m02 = (right + left) / temp2;
        m12 = (top + bottom) / temp3;
        m22 = (-far - near) / temp4;
        m32 = -1.0;
        m23 = (-temp * far) / temp4;
        m33 = 0.0;
    }
    
    // ref.: http://glasnost.itcarlow.ie/~powerk/GeneralGraphicsNotes/projection/viewport_transformation.html
    public void setViewportTransformation(
            double x, double y, double width, double height) {
        
        double left = x;
        double bottom = y + height;
        double right = x + width;
        double top = y;
        setIdentity();
        m00 = (right - left) / 2.0;
        m11 = (top - bottom) / 2.0;
        m22 = 0.5;
        m03 = (right + left) / 2.0; 
        m13 = (top + bottom) / 2.0; 
        m23 = 0.5; 
        m33 = 1.0;         
    }
    
    //https://www.gamedev.net/forums/topic/531748-the-viewport-matrix-in-opengl/4439106/
    public void setViewportTransformation2(double x, double y, double width, double height) {
        setIdentity();
        m00 = width * 0.5;
        m11 = -height * 0.5;
        m03 = width * 0.5 + x;
        m13 = height * 0.5 + y;
    }
    
    public void setScale(double sx, double sy, double sz) {
        setIdentity();
        m00 = sx;
        m11 = sy;
        m22 = sz;
        m33 = 1;
    }
    
    public void setTranslation(double x, double y, double z) {
        setIdentity();
        m03 = x;
        m13 = y;
        m23 = z;
    }
    
    public void setRotationX(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m11 =  c; m12 = s;
        m21 = -s; m22 = c;
    }

    public void setRotationY(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m00 = c; m02 = -s;
        m20 = s; m22 =  c;
    }

    public void setRotationZ(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m00 =  c; m01 = s;
        m10 = -s; m11 = c;
    }
    
    public void multiply(Mat4 m) {
        double nm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
        double nm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
        double nm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
        double nm03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;
        double nm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
        double nm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
        double nm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
        double nm13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;
        double nm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
        double nm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
        double nm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
        double nm23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;
        double nm30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
        double nm31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
        double nm32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
        double nm33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;
        m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
        m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
        m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
        m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;
    }

    public void multiply(Vec4 v) {
        double nx = m00 * v.x + m01 * v.y + m02 * v.z + m03 * v.w;
        double ny = m10 * v.x + m11 * v.y + m12 * v.z + m13 * v.w;
        double nz = m20 * v.x + m21 * v.y + m22 * v.z + m23 * v.w;
        double nw = m30 * v.x + m31 * v.y + m32 * v.z + m33 * v.w;
        v.x = nx;
        v.y = ny;
        v.z = nz;
        v.w = nw;
    }

    public void multiply(double[] v, int indexOffset) {
        double vx = v[0 + indexOffset];
        double vy = v[1 + indexOffset];
        double vz = v[2 + indexOffset];
        double vw = v[3 + indexOffset];
        double nx = m00 * vx + m01 * vy + m02 * vz + m03 * vw;
        double ny = m10 * vx + m11 * vy + m12 * vz + m13 * vw;
        double nz = m20 * vx + m21 * vy + m22 * vz + m23 * vw;
        double nw = m30 * vx + m31 * vy + m32 * vz + m33 * vw;
        v[0 + indexOffset] = nx;
        v[1 + indexOffset] = ny;
        v[2 + indexOffset] = nz;
        v[3 + indexOffset] = nw;
    }
    
}
