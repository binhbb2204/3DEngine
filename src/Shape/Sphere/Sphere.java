package src.Shape.Sphere;

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

public class Sphere implements Shape3D {
    private static final int SEGMENTS = 40;
    private static final int RINGS = 40;
    private static final double RADIUS = 100;
    private static final double BIAS = 0.00001;
    private static final double TEXTURE_SCALE = 1.0;

    private final TextureManager textureManager;
    private final double[][] vertices;
    private final int[][] faces;
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private double posX = 0;
    private double posY = 0;
    private double posZ = 0;

    public Sphere(String texturePath, double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        textureManager = new TextureManager(texturePath);
        vertices = initializeVertices();
        faces = initializeFaces();
    }

    public Sphere(String texturePath) {
        this(texturePath, 0, 0, 0);
    }

    private double[][] initializeVertices() {
        int numVertices = (SEGMENTS + 1) * (RINGS + 1);
        double[][] vertices = new double[numVertices][3];

        for (int i = 0; i <= SEGMENTS; i++) {
            double theta = i * Math.PI / SEGMENTS;
            for (int j = 0; j <= RINGS; j++) {
                double phi = j * 2 * Math.PI / RINGS;
                double x = RADIUS * Math.sin(theta) * Math.cos(phi);
                double y = RADIUS * Math.cos(theta);
                double z = RADIUS * Math.sin(theta) * Math.sin(phi);
                vertices[i * (RINGS + 1) + j] = new double[]{x, y, z};
            }
        }

        return vertices;
    }

    private int[][] initializeFaces() {
        List<int[]> facesList = new ArrayList<>();

        for (int i = 0; i < SEGMENTS; i++) {
            for (int j = 0; j < RINGS; j++) {
                int first = i * (RINGS + 1) + j;
                int second = first + RINGS + 1;
                facesList.add(new int[]{first, second, second + 1, first + 1});
            }
        }

        return facesList.toArray(new int[0][]);
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
        for (int[] face : faces) {
            double depth = calculateFaceDepth(face, angleX, angleY);
            faceList.add(new Face(this, face, depth));
        }
        return faceList;
    }

    @Override
    public void renderFace(BufferedImage buffer, Face face, double angleX, double angleY, double scale) {
        drawTexturedFace(buffer, face.getVertices(), angleX, angleY, scale);
    }

    private void drawTexturedFace(BufferedImage buffer, int[] faceVertices, double angleX, double angleY, double scale) {
        Point3D[] projectedPoints = new Point3D[faceVertices.length];
        double[][] uvCoords = new double[faceVertices.length][3];

        for (int i = 0; i < faceVertices.length; i++) {
            double[] rotated = Geometry3D.rotatePoint(vertices[faceVertices[i]], posX, posY, posZ, angleX, angleY);
            projectedPoints[i] = Geometry3D.project3D(rotated, WIDTH, HEIGHT, scale);

            double z = 400 - rotated[2];
            double w = 1.0 / Math.max(z, BIAS);

            double u = (i == 1 || i == 2) ? 1.0 : 0.0;
            double v = (i == 2 || i == 3) ? 1.0 : 0.0;

            uvCoords[i][0] = u * w;
            uvCoords[i][1] = v * w;
            uvCoords[i][2] = w;
        }

        Rectangle bounds = calculateBoundingBox(projectedPoints);
        if (bounds != null) {
            renderTexturedPolygon(buffer, projectedPoints, uvCoords, bounds);
        }
    }

    private Rectangle calculateBoundingBox(Point3D[] projectedPoints) {
        int minX = WIDTH, minY = HEIGHT, maxX = 0, maxY = 0;
        for (Point3D p : projectedPoints) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(WIDTH - 1, maxX);
        maxY = Math.min(HEIGHT - 1, maxY);

        if (minX >= WIDTH || minY >= HEIGHT || maxX < 0 || maxY < 0 || maxX < minX || maxY < minY) {
            return null;
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private void renderTexturedPolygon(BufferedImage buffer, Point3D[] projectedPoints, double[][] uvCoords, Rectangle bounds) {
        BufferedImage texture = textureManager.getTexture();

        for (int y = bounds.y; y <= bounds.y + bounds.height; y++) {
            for (int x = bounds.x; x <= bounds.x + bounds.width; x++) {
                if (Geometry3D.isPointInPolygon(x, y, projectedPoints)) {
                    double[] coords = TextureMapper.calculateQuadTextureCoordinates(x, y, projectedPoints, uvCoords, WIDTH, HEIGHT);
                    if (coords != null) {
                        applyTexture(buffer, x, y, coords[0], coords[1], texture);
                    }
                }
            }
        }
    }

    private void applyTexture(BufferedImage buffer, int x, int y, double u, double v, BufferedImage texture) {
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