package src.Shape.Cylinder;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import src.Panel.ShapePanel.Shape3D;
import src.geometry.Geometry3D;
import src.util.Face;
import src.util.Point3D;
import src.util.TextureManager;
import src.util.TextureMapper;

public class Cylinder implements Shape3D{
    private int WIDTH = 800;
    private int HEIGHT = 800;
    private static final double TEXTURE_SCALE = 1.0;
    private static final double BIAS = 0.00001;
    private double posX = 0;
    private double posY = 0;
    private double posZ = 0;

    private final TextureManager textureManager;
    private final double[][] vertices;
    private final int[][] faces;
    //SEGMENT đại loại là nó là bề ngoài hình trụ nó smoother, the bigger the number, the smoothier the cylinder face is
    public static final int SEGMENTS = 80;
    public static final int RADIUS = 100;
    public static final int HEIGHT_HALF = 200;

    private double[][] initializeVertices() {
        int numVertices = SEGMENTS * 2 + 2;
        double[][] vertices = new double[numVertices][3];

        vertices[0] = new double[]{0, -HEIGHT_HALF, 0}; //Bottom circle
        vertices[1] = new double[]{0, +HEIGHT_HALF, 0}; //Top circle

        for(int i = 0; i < SEGMENTS;  i++){
            double angle = 2 * Math.PI * i / SEGMENTS;  //calculate angle each points(vertice), toán lượng giác :V
            //in order to find x and y, we apply right triangle principle, sin(angle) = z/RADIUS, cos(angle) = x/RADIUS
            /*
             *         /|
             *        / |
             * ->R   /  |
             *      /   | <- z
             *     /    |
             *     -----<- x
             */     
            double x = RADIUS * Math.cos(angle);
            double z = RADIUS * Math.sin(angle); 

            vertices[i + 2] = new double[]{x, -HEIGHT_HALF, z}; // Bottom circle vertices
            vertices[i + 2 + SEGMENTS] = new double[]{x, HEIGHT_HALF, z}; // Top circle vertices

        }

        return vertices;
    }

    private int[][] initializeFaces(){
        List<int[]> facesList = new ArrayList<>();

        //Add bottom face triangles
        for(int i = 0; i < SEGMENTS; i++){
            int nextI = (i + 1) % SEGMENTS;
            facesList.add(new int[]{0, i + 2, nextI + 2});
        }

        //Add top face triangles
        for (int i = 0; i < SEGMENTS; i++) {
            int nextI = (i + 1) % SEGMENTS;
            facesList.add(new int[]{1, SEGMENTS + nextI + 2, SEGMENTS + i + 2});
        }

        //Add side faces (quads mode of 2 triangles => rectangle)
        for(int i = 0; i < SEGMENTS; i++){
            int nextI = (i + 1) % SEGMENTS;
            int bottomLeft = i + 2;
            int bottomRight = nextI + 2;
            int topLeft = SEGMENTS + bottomLeft;
            int topRight = SEGMENTS + bottomRight;

            facesList.add(new int[]{bottomLeft, bottomRight, topRight, topLeft});
        }

        return facesList.toArray(new int[0][]);
    }

    public Cylinder(String texturePath, double x, double y, double z){
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        textureManager = new TextureManager(texturePath);
        vertices = initializeVertices();
        faces = initializeFaces();
    }

    public Cylinder(String texturePath){
        this(texturePath, 0, 0, 0);    
    }


    private double calculateFaceDepth(int[] face, double angleX, double angleY) {
        double depth = 0;
        for (int vertex : face) {
            depth += Geometry3D.rotatePoint(vertices[vertex], posX, posY, posZ, angleX, angleY)[2];
        }
        return depth / face.length;
    }

    @Override
	public double[][] getVertices() {
		return vertices;
	}

    @Override
    public List<Face> getFaces(double angleX, double angleY) {
        List<Face> faceList = new ArrayList<>();
        for(int i = 0; i < faces.length; i++){
            int[] face = faces[i];
            double depth = calculateFaceDepth(face, angleX, angleY);
            faceList.add(new Face(this, face, depth));
        }

        return faceList;
    }

    @Override
    public void renderFace(BufferedImage buffer, Face face, double angleX, double angleY, double scale) {
        drawTexturedFace(buffer, face.getVertices(), angleX, angleY, scale);
    }

    // private void renderFaces(BufferedImage buffer, double angleX, double angleY, double scale){
    //     List<Face> faceList = new ArrayList<>();
    //     for (int i = 0; i < faces.length; i++) {
    //         int[] face = faces[i];
    //         double depth = calculateFaceDepth(face, angleX, angleY);
    //         faceList.add(new Face(face, depth));
    //     }

    //     // Sort faces by depth (farthest to nearest)
    //     Collections.sort(faceList);

    //     // Draw faces with texture mapping
    //     for (Face face : faceList) {
    //         drawTexturedFace(buffer, face.getVertices(), angleX, angleY, scale);
    //     }

        
    // }
    

    public void drawTexturedFace(BufferedImage buffer, int[] faceVertices, double angleX, double angleY, double scale){
        Point3D[] projectedPoints = new Point3D[faceVertices.length];
        double[][] uvCoords = new double[faceVertices.length][3];

        //if(uvCoords == null) return;

        // 
        // 
        for(int i = 0; i < faceVertices.length; i++){
            double[] rotated = Geometry3D.rotatePoint(vertices[faceVertices[i]], posX, posY, posZ, angleX, angleY);
            projectedPoints[i] = Geometry3D.project3D(rotated, WIDTH, HEIGHT, scale);

            double z = 400 - rotated[2];
            double w = 1.0/Math.max(z, BIAS);

            if (faceVertices.length == 4) { // Side faces
                double[] vertex = vertices[faceVertices[i]];
                double angle = Math.atan2(vertex[2], vertex[0]);  //Calculate angle from x,z coordinates
                if(angle < 0){
                    angle += 2 * Math.PI;
                }
                double u = (angle / (2 * Math.PI)) * TEXTURE_SCALE;
                u = Math.min(1.0, Math.max(0.0, u)); //Ensure U is in [0,1] range
                double heightRatio = (vertex[1] + HEIGHT_HALF) / (2.0 * HEIGHT_HALF);
                double v = Math.min(1.0, Math.max(0.0, heightRatio));
                uvCoords[i] = new double[]{u * w, v * w, w};
            } else { // Top/bottom faces
                // Calculate radial UV coordinates for circular faces
                double angle = Math.atan2(rotated[2], rotated[0]);
                double u = (angle / (2 * Math.PI)) + 0.5;
                double v = Math.sqrt(rotated[0] * rotated[0] + rotated[2] * rotated[2]) / RADIUS;
                uvCoords[i] = new double[]{u * w, v * w, w};
            }
        }

        Rectangle bounds = calculateBoundingBox(projectedPoints);
        if(bounds != null) {
            if (faceVertices.length == 4) {
                renderTexturedQuad(buffer, projectedPoints, uvCoords, bounds);
            } else {
                renderTexturedTriangle(buffer, projectedPoints, uvCoords, bounds);
            }
        }
    }

    private void renderTexturedQuad(BufferedImage buffer, Point3D[] projectedPoints, double[][] uvCoords, Rectangle bounds) {
        BufferedImage texture = textureManager.getTexture();
    
        for(int y = bounds.y; y <= bounds.y + bounds.height; y++) {
            for(int x = bounds.x; x <= bounds.x + bounds.width; x++) {
                if(Geometry3D.isPointInPolygon(x, y, projectedPoints)) {
                    double[] coords = TextureMapper.calculateQuadTextureCoordinates(x, y, projectedPoints, 
                                                                                 uvCoords, WIDTH, HEIGHT);
                    if(coords != null) {
                        applyTexture(buffer, x, y, coords[0], coords[1], texture);
                    }
                }
            }
        }
    }

    private void renderTexturedTriangle(BufferedImage buffer, Point3D[] projectedPoints, double[][] uvCoords, Rectangle bounds) {
        BufferedImage texture = textureManager.getTexture();
    
        for(int y = bounds.y; y <= bounds.y + bounds.height; y++) {
            for(int x = bounds.x; x <= bounds.x + bounds.width; x++) {
                if(Geometry3D.isPointInPolygon(x, y, projectedPoints)) {
                    double[] coords = TextureMapper.calculateTriangleTextureCoordinates(x, y, projectedPoints, 
                                                                                     uvCoords, WIDTH, HEIGHT);
                    if(coords != null) {
                        applyTexture(buffer, x, y, coords[0], coords[1], texture);
                    }
                }
            }
        }
    }

    private Rectangle calculateBoundingBox(Point3D[] projectedPoints){
        int minX = WIDTH, minY = HEIGHT, maxX = 0, maxY = 0;
        for (Point3D p : projectedPoints) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        // Clip to screen bounds
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(WIDTH - 1, maxX);
        maxY = Math.min(HEIGHT - 1, maxY);

        if (minX >= WIDTH || minY >= HEIGHT || 
            maxX < 0 || maxY < 0 || maxX < minX || maxY < minY) {
            return null;
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    // private void renderTexturedPolygon(BufferedImage buffer, Point3D[] projectedPoints, double[][] uvCoords, Rectangle bounds){
    //     BufferedImage texture = textureManager.getTexture();

    //     for(int y = bounds.y; y <= bounds.y + bounds.height; y++){
    //         for(int x = bounds.x; x <= bounds.x + bounds.width; x++){
    //             if(Geometry3D.isPointInPolygon(x, y, projectedPoints)){
    //                 double[] coords = TextureMapper.calculateQuadTextureCoordinates(x, y, projectedPoints, 
    //                                                                                 uvCoords, WIDTH, HEIGHT);

    //                 if(coords != null){
    //                     applyTexture(buffer, x, y, coords[0],coords[1], texture);
    //                 }
    //             }
    //         }
    //     }
    // }

    public void applyTexture(BufferedImage buffer, int x, int y, double u, double v, BufferedImage texture){
        u = Math.max(0.0, Math.min(1.0, u));
        v = Math.max(0.0, Math.min(1.0, v));

        int texX = (int) (u * (texture.getWidth() - 1));
        int texY = (int) (v * (texture.getHeight() - 1));

        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight() &&
            texX >= 0 && texX < texture.getWidth() && texY >= 0 && texY < texture.getHeight()) {
            try {
                buffer.setRGB(x, y, texture.getRGB(texX, texY));
            } catch (ArrayIndexOutOfBoundsException e) {
            // Silently skip problematic pixels
            }
        }
    }



	

    
}
