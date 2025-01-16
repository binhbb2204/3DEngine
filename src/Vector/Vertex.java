package src.Vector;

public class Vertex {
    public Vec3 position;
    public UV texCoord;
    
    public Vertex(Vec3 position, UV texCoord) {
        this.position = position;
        this.texCoord = texCoord;
    }
}
