package src.Shape.Block;

import src.Panel.ShapePanel.Shape3D;
import src.geometry.Geometry3D;
import src.util.Face;
import src.util.Point3D;
import src.util.TextureManager;
import src.util.TextureMapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Block3D implements Shape3D{
    private int HEIGHT = 800;
    private int WIDTH = 800;
    
    private double posX = 0;
    private double posY = 0;
    private double posZ = 0;

    
    private final TextureManager textureManager;
    private final double[][] vertices;
    private final int[][] faces;

    private double[][] initializeVertices() {
        return new double[][] {
            {-100, -100, -100},
            {100, -100, -100},
            {100, 100, -100},
            {-100, 100, -100},
            {-100, -100, 100},
            {100, -100, 100},
            {100, 100, 100},
            {-100, 100, 100},
        };
    }

    private int[][] initializeFaces() {
        return new int[][] {
            {0, 1, 2, 3}, // Back face
            {4, 5, 6, 7}, // Front face
            {0, 1, 5, 4}, // Bottom face
            {3, 2, 6, 7}, // Top face
            {0, 4, 7, 3}, // Left face
            {1, 5, 6, 2}, // Right face
        };
    }

    public Block3D(String texturePath, double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        textureManager = new TextureManager(texturePath);
        vertices = initializeVertices();
        faces = initializeFaces();
    }

    public Block3D(String texturePath){
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
        Point3D[] projectedPoints = new Point3D[4];
        double[][] uvCoords = calculateUVCoordinates(faceVertices, projectedPoints, angleX, angleY, scale);

        if(uvCoords == null) return;

        Rectangle bounds = calculateBoundingBox(projectedPoints);
        if(bounds != null){
            renderTexturedPolygon(buffer, projectedPoints, uvCoords, bounds);
        } 
    }

    private double[][] calculateUVCoordinates(int[] faceVertices, Point3D[] projectedPoints,
                                            double angleX, double angleY, double scale){
        double[][] uvCoords = new double[4][3];

        for(int i = 0; i < 4; i++){
            double[] rotated = Geometry3D.rotatePoint(vertices[faceVertices[i]], posX, posY, posZ, angleX, angleY);

            projectedPoints[i] = Geometry3D.project3D(rotated, WIDTH, HEIGHT, scale);

            double z = 400 - rotated[2];
            double w = 1.0/Math.max(z, 0.001);

            double u = (i == 1 || i == 2) ? 1.0 : 0.0;
            double v = (i == 2 || i == 3) ? 1.0 : 0.0;

            uvCoords[i][0] = u * w;
            uvCoords[i][1] = v * w;
            uvCoords[i][2] = w;
        }
        return uvCoords;
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

    private void renderTexturedPolygon(BufferedImage buffer, Point3D[] projectedPoints, double[][] uvCoords, Rectangle bounds){
        BufferedImage texture = textureManager.getTexture();

        for(int y = bounds.y; y <= bounds.y + bounds.height; y++){
            for(int x = bounds.x; x <= bounds.x + bounds.width; x++){
                if(Geometry3D.isPointInPolygon(x, y, projectedPoints)){
                    double[] coords = TextureMapper.calculateQuadTextureCoordinates(x, y, projectedPoints, 
                                                                                    uvCoords, WIDTH, HEIGHT);

                    if(coords != null){
                        applyTexture(buffer, x, y, coords[0],coords[1], texture);
                    }
                }
            }
        }
    }

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