package src.Vector;

public class Vec4 {
    public double x, y, z, w;
    
    public Vec4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vec4(Vec3 v) {
        this(v.x, v.y, v.z, 1.0);
    }
    
    public double get(int index) {
        switch(index) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            case 3: return w;
            default: throw new IndexOutOfBoundsException();
        }
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getW() { return w; }
}
