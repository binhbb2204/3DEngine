package src.Vector;

public class Mat4x4 {
    public double[][] m;
    
    public Mat4x4() {
        m = new double[4][4];
        // Initialize as identity matrix
        for (int i = 0; i < 4; i++) {
            m[i][i] = 1.0;
        }
    }
    
    public Vec4 multiply(Vec4 v) {
        return new Vec4(
            m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3] * v.w,
            m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3] * v.w,
            m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3] * v.w,
            m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3] * v.w
        );
    }
    
    public Mat4x4 multiply(Mat4x4 other) {
        Mat4x4 result = new Mat4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += this.m[i][k] * other.m[k][j];
                }
            }
        }
        return result;
    }
    
    public static Mat4x4 createRotationX(double angle) {
        Mat4x4 m = new Mat4x4();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        m.m[1][1] = cos;
        m.m[1][2] = -sin;
        m.m[2][1] = sin;
        m.m[2][2] = cos;
        return m;
    }
    
    public static Mat4x4 createRotationY(double angle) {
        Mat4x4 m = new Mat4x4();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        m.m[0][0] = cos;
        m.m[0][2] = sin;
        m.m[2][0] = -sin;
        m.m[2][2] = cos;
        return m;
    }
    
    public static Mat4x4 createTranslation(double x, double y, double z) {
        Mat4x4 m = new Mat4x4();
        m.m[0][3] = x;
        m.m[1][3] = y;
        m.m[2][3] = z;
        return m;
    }
}
