package src.Vector;
import java.awt.image.BufferedImage;

public class TexturedFace {
    public Vertex[] vertices;
    public BufferedImage texture;
    
    public TexturedFace(Vertex[] vertices, BufferedImage texture) {
        if (vertices.length != 4) {
            throw new IllegalArgumentException("Face must have exactly 4 vertices");
        }
        this.vertices = vertices;
        this.texture = texture;
    }
}
