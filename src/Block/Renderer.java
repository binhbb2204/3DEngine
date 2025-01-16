package src.Block;

import java.awt.*;
import java.awt.image.BufferedImage;

import src.Vector.Mat4x4;
import src.Vector.TexturedFace;
import src.Vector.Vec4;
import src.Vector.Vec3;  
import src.Vector.UV;   
import src.Vector.Vertex;

public class Renderer {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double NEAR_PLANE = 0.1;
    private static final double FAR_PLANE = 1000.0;
    private static final double FOV = Math.toRadians(60.0);
    
    private double[][] projectionMatrix;
    private BufferedImage frameBuffer;
    private double[][] depthBuffer;
    
    public Renderer() {
        frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        depthBuffer = new double[HEIGHT][WIDTH];
        initProjectionMatrix();
    }
    
    private void initProjectionMatrix() {
        double aspect = (double) WIDTH / HEIGHT;
        double f = 1.0 / Math.tan(FOV / 2);
        double q = FAR_PLANE / (FAR_PLANE - NEAR_PLANE);
        
        projectionMatrix = new double[][] {
            {f/aspect, 0, 0, 0},
            {0, f, 0, 0},
            {0, 0, q, -NEAR_PLANE*q},
            {0, 0, 1, 0}
        };
    }
    
    public void clear() {
        Graphics2D g2d = frameBuffer.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.dispose();
        
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                depthBuffer[y][x] = Double.POSITIVE_INFINITY;
            }
        }
    }
    
    public void renderFace(TexturedFace face, Mat4x4 modelView) {
        // Project vertices
        Point[] screenPoints = new Point[4];
        double[] zValues = new double[4];
        
        for (int i = 0; i < 4; i++) {
            Vec4 worldPos = modelView.multiply(new Vec4(face.vertices[i].position));
            Vec4 clipPos = projectPoint(worldPos);
            
            if (clipPos.w <= 0) return; // Behind camera
            
            double invW = 1.0 / clipPos.w;
            screenPoints[i] = new Point(
                (int)((clipPos.x * invW + 1) * WIDTH / 2),
                (int)((1 - clipPos.y * invW) * HEIGHT / 2)
            );
            zValues[i] = clipPos.z * invW;
        }
        
        // Split quad into two triangles
        renderTriangle(
            screenPoints[0], screenPoints[1], screenPoints[2],
            face.vertices[0].texCoord, face.vertices[1].texCoord, face.vertices[2].texCoord,
            zValues[0], zValues[1], zValues[2],
            face.texture
        );
        
        renderTriangle(
            screenPoints[0], screenPoints[2], screenPoints[3],
            face.vertices[0].texCoord, face.vertices[2].texCoord, face.vertices[3].texCoord,
            zValues[0], zValues[2], zValues[3],
            face.texture
        );
    }
    
    private void renderTriangle(Point p1, Point p2, Point p3, UV uv1, UV uv2, UV uv3,
                              double z1, double z2, double z3, BufferedImage texture) {
        // Calculate bounding box
        int minX = Math.max(0, Math.min(Math.min(p1.x, p2.x), p3.x));
        int maxX = Math.min(WIDTH - 1, Math.max(Math.max(p1.x, p2.x), p3.x));
        int minY = Math.max(0, Math.min(Math.min(p1.y, p2.y), p3.y));
        int maxY = Math.min(HEIGHT - 1, Math.max(Math.max(p1.y, p2.y), p3.y));
        
        // Rasterize
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Calculate barycentric coordinates
                double[] bary = computeBarycentric(x, y, p1, p2, p3);
                if (bary == null) continue;
                
                // Interpolate Z
                double z = 1.0 / (bary[0] / z1 + bary[1] / z2 + bary[2] / z3);
                
                // Depth test
                if (z >= depthBuffer[y][x]) continue;
                depthBuffer[y][x] = z;
                
                // Perspective-correct texture coordinates
                double u = z * (bary[0] * uv1.u / z1 + bary[1] * uv2.u / z2 + bary[2] * uv3.u / z3);
                double v = z * (bary[0] * uv1.v / z1 + bary[1] * uv2.v / z2 + bary[2] * uv3.v / z3);
                
                // Sample texture
                int tx = (int) (u * (texture.getWidth() - 1));
                int ty = (int) (v * (texture.getHeight() - 1));
                
                if (tx >= 0 && tx < texture.getWidth() && ty >= 0 && ty < texture.getHeight()) {
                    frameBuffer.setRGB(x, y, texture.getRGB(tx, ty));
                }
            }
        }
    }
    
    private double[] computeBarycentric(int x, int y, Point p1, Point p2, Point p3) {
        Vec3 v0 = new Vec3(p2.x - p1.x, p3.x - p1.x, p1.x - x);
        Vec3 v1 = new Vec3(p2.y - p1.y, p3.y - p1.y, p1.y - y);
        
        Vec3 cross = v0.cross(v1);
        if (Math.abs(cross.z) < 0.0001) return null;
        
        double invZ = 1.0 / cross.z;
        double v = cross.x * invZ;
        double w = cross.y * invZ;
        double u = 1.0 - v - w;
        
        if (u < -0.0001 || v < -0.0001 || w < -0.0001) return null;
        return new double[]{u, v, w};
    }
    
    private Vec4 projectPoint(Vec4 point) {
        Vec4 result = new Vec4(0, 0, 0, 0);
        for (int i = 0; i < 4; i++) {
            result.x += projectionMatrix[0][i] * point.get(i);
            result.y += projectionMatrix[1][i] * point.get(i);
            result.z += projectionMatrix[2][i] * point.get(i);
            result.w += projectionMatrix[3][i] * point.get(i);
        }
        return result;
    }
    
    public BufferedImage getFrame() {
        return frameBuffer;
    }
}