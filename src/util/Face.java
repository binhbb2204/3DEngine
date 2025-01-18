package src.util;

public class Face implements Comparable<Face>{
    public int[] vertices;
    public double depth;

    public Face(int[] vertices, double depth){
        this.vertices = vertices;
        this.depth = depth;
    }

    @Override
    public int compareTo(Face other) {
        return Double.compare(this.depth, other.depth);
    }
    
}
