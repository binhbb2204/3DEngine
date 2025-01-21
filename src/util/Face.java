package src.util;

import src.Panel.ShapePanel.Shape3D;

public class Face implements Comparable<Face>{
    public int[] vertices;
    public double depth;
    private Shape3D shape;

    public Face(Shape3D shape, int[] vertices, double depth){
        this.vertices = vertices;
        this.depth = depth;
        this.shape = shape;
    }

    public Shape3D getShape(){
        return shape;
    }

    public int[] getVertices() {
        return vertices;
    }

    @Override
    public int compareTo(Face other) {
        return Double.compare(this.depth, other.depth);
    }
    
}
