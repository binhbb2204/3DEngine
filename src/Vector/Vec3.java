package src.Vector;

public class Vec3 {
    public double x, y, z;
    
    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }
    
    public Vec3 cross(Vec3 other) {
        return new Vec3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    public Vec3 normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new Vec3(x / length, y / length, z / length);
    }
}
