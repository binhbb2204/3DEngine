package src.Testing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block3D extends JPanel {
    private int HEIGHT = 800;
    private int WIDTH = 800;

    private double posX = 0;
    private double posY = 0;
    private double posZ = 0;
    private double[][] vertices = {
        {-100, -100, -100},
        {100, -100, -100},
        {100, 100, -100},
        {-100, 100, -100},
        {-100, -100, 100},
        {100, -100, 100},
        {100, 100, 100},
        {-100, 100, 100},
    };

    // Texture coordinates for each vertex of each face


    private int[][] faces = {
        {0, 1, 2, 3}, // Back face
        {4, 5, 6, 7}, // Front face
        {0, 1, 5, 4}, // Bottom face
        {3, 2, 6, 7}, // Top face
        {0, 4, 7, 3}, // Left face
        {1, 5, 6, 2}, // Right face
    };

    private double angleX = 0;
    private double angleY = 0;
    private Point lastMousePos;
    private double scale = 1.0;
    private static final double ZOOM_FACTOR = 0.1;
    private BufferedImage texture;
    private static final double CAMERA_DISTANCE = 1000;

    public Block3D(String texturePath, double x, double y, double z){
        this(texturePath);
        setPosition(x, y, z);
        
    }

    public Block3D(String texturePath) {
        // setTitle("3D Engine");
        // setSize(WIDTH, HEIGHT);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setLocationRelativeTo(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Load the texture image
        loadTexture(texturePath);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                WIDTH = getWidth();
                HEIGHT = getHeight();
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                
            }

            @Override
            public void componentShown(ComponentEvent e) {
                
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                
            }

        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePos != null) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;

                    angleY += dx * 0.01;
                    angleX += dy * 0.01;

                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0){
                    scale *= (1 + ZOOM_FACTOR);
                }
                else{
                    scale *= (1 - ZOOM_FACTOR);
                }

                scale = Math.max(0.1, Math.min(scale, 5.0));
                repaint();
            }
            
        });
    }

    private void setPosition(double x, double y, double z){
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        repaint();
    }

    private void loadTexture(String texturePath) {
        try {
            // Load the image file
            texture = ImageIO.read(new File(texturePath));
            
            // resize texture to power of 2 dimensions for better performance
            int targetWidth = nextPowerOfTwo(texture.getWidth());
            int targetHeight = nextPowerOfTwo(texture.getHeight());
            if (texture.getWidth() != targetWidth || texture.getHeight() != targetHeight) {
                BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(texture, 0, 0, targetWidth, targetHeight, null);
                g.dispose();
                texture = resized;
            }
        } catch (IOException e) {
            System.err.println("Error loading texture: " + e.getMessage());
            // Fall back to checkerboard texture if loading fails
            texture = createCheckerboardTexture(256, 256);
        }
    }

    private int nextPowerOfTwo(int n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }

    private BufferedImage createCheckerboardTexture(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int tileSize = 32;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isWhite = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                img.setRGB(x, y, isWhite ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return img;
    }

    private double[] rotatePoint(double[] point) {

        double[] translate = new double[3];

        translate[0] = point[0] + posX;
        translate[1] = point[1] + posY;
        translate[2] = point[2] + posZ;
        double[] rotated = new double[3];

        // Rotate around Y axis
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double tempX = translate[0] * cosY + translate[2] * sinY;
        double tempZ = -translate[0] * sinY + translate[2] * cosY;

        // Rotate around X axis
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        rotated[0] = tempX;
        rotated[1] = translate[1] * cosX - tempZ * sinX;
        rotated[2] = translate[1] * sinX + tempZ * cosX;

        return rotated;
    }

    private Point3D project3D(double[] point3D) {
        double z = CAMERA_DISTANCE - 400;
        if (z < 1) z = 1;
        double baseScale = Math.min(WIDTH, HEIGHT) * 0.625;
        double projZ = z - point3D[2];
        int x = (int) (point3D[0] * baseScale * scale / projZ + WIDTH / 2);
        int y = (int) (point3D[1] * baseScale * scale / projZ + HEIGHT / 2);
        return new Point3D(x, y, projZ);
    }

    private double calculateFaceDepth(int[] face) {
        double depth = 0;
        for (int vertex : face) {
            depth += rotatePoint(vertices[vertex])[2];
        }
        return depth / face.length;
    }

    // @Override
    // public void paint(Graphics g) {
    //     BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    //     Graphics2D g2d = buffer.createGraphics();

    //     // Draw background
    //     g2d.setColor(Color.WHITE);
    //     g2d.fillRect(0, 0, WIDTH, HEIGHT);

    //     List<Face> faceList = new ArrayList<>();
    //     for (int i = 0; i < faces.length; i++) {
    //         int[] face = faces[i];
    //         double depth = calculateFaceDepth(face);
    //         faceList.add(new Face(face, depth));
    //     }

    //     // Sort faces by depth (farthest to nearest)
    //     Collections.sort(faceList);

    //     // Draw faces with texture mapping
    //     for (Face face : faceList) {
    //         drawTexturedFace(buffer, face.vertices);
    //     }

    //     g.drawImage(buffer, 0, 0, this);
    // }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();

        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        List<Face> faceList = new ArrayList<>();
        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];
            double depth = calculateFaceDepth(face);
            faceList.add(new Face(face, depth));
        }

        // Sort faces by depth (farthest to nearest)
        Collections.sort(faceList);

        // Draw faces with texture mapping
        for (Face face : faceList) {
            drawTexturedFace(buffer, face.vertices);
        }

        g.drawImage(buffer, 0, 0, this);
    }


    private void drawTexturedFace(BufferedImage buffer, int[] faceVertices) {
        Point3D[] projectedPoints = new Point3D[4];

        double[][] uvCoords = new double[4][3];

        for (int i = 0; i < 4; i++) {
            double[] rotated = rotatePoint(vertices[faceVertices[i]]);
            projectedPoints[i] = project3D(rotated);

            double z = 400 - rotated[2];
            double w = 1.0 / z;

            // Store u/z, v/z, and 1/z
            // Map vertex index to u,v coordinates (0,0) to (1,1)
            double u = (i == 1 || i == 2) ? 1.0 : 0.0;
            double v = (i == 2 || i == 3) ? 1.0 : 0.0;

            uvCoords[i][0] = u * w; // u/z
            uvCoords[i][1] = v * w; // v/z
            uvCoords[i][2] = w;     // 1/z

        }

        
        // Find bounding box
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

        // Scan each pixel in the bounding box
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInPolygon(x, y, projectedPoints)) {
                    // Calculate bilinear coordinates for quad
                    double[] coords = calculateQuadTextureCoordinates(x, y, projectedPoints, uvCoords);
                    if (coords != null) {
                        double u = coords[0];
                        double v = coords[1];

                        // Sample texture
                        int texX = (int) (u * (texture.getWidth() - 1));
                        int texY = (int) (v * (texture.getHeight() - 1));
                        texX = Math.min(Math.max(texX, 0), texture.getWidth() - 1);
                        texY = Math.min(Math.max(texY, 0), texture.getHeight() - 1);
                        
                        buffer.setRGB(x, y, texture.getRGB(texX, texY));
                    }
                }
            }
        }
    }

    private boolean isPointInPolygon(int x, int y, Point3D[] points) {
        int i, j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > y) != (points[j].y > y) &&
                (x < (points[j].x - points[i].x) * (y - points[i].y) / 
                    (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }

    private double[] calculateQuadTextureCoordinates(int x, int y, Point3D[] points, double[][] uvCoords) {
        // Convert screen coordinates to normalized device coordinates (-1 to 1)
        double normalizedX = (2.0 * x / WIDTH) - 1.0;
        double normalizedY = (2.0 * y / HEIGHT) - 1.0;

        // Inverse bilinear interpolation
        double[] bilinearCoor = solveInverseBilinear(
            normalizedX, normalizedY,
            points[0].x / (double)WIDTH * 2 - 1, points[0].y / (double)HEIGHT * 2 - 1,
            points[1].x / (double)WIDTH * 2 - 1, points[1].y / (double)HEIGHT * 2 - 1,
            points[2].x / (double)WIDTH * 2 - 1, points[2].y / (double)HEIGHT * 2 - 1,
            points[3].x / (double)WIDTH * 2 - 1, points[3].y / (double)HEIGHT * 2 - 1
        );

        if(bilinearCoor == null){
            return null;
        }

        double u = bilinearCoor[0];
        double v = bilinearCoor[1];

        double uz = bilinearInterpolation(uvCoords, 0, u, v);
        double uv = bilinearInterpolation(uvCoords, 1, u, v);
        double inverse_z = bilinearInterpolation(uvCoords, 2, u, v);

        if(Math.abs(inverse_z) < 1e-6){
            return null;
        }
        double perspU = uz / inverse_z;
        double perspV = uv / inverse_z;

        return new double[]{
            Math.max(0, Math.min(1, perspU)),
            Math.max(0, Math.min(1, perspV))
        };
    }

    private double bilinearInterpolation(double[][] values, int index, double u, double v){
        return (1-u) * (1-v) * values[0][index] + 
            u*(1-v) * values[1][index] + 
            u * v * values[2][index] + 
            (1-u) * v * values[3][index];
    }

    private double[] solveInverseBilinear(double x, double y, 
                                         double x0, double y0, double x1, double y1,
                                         double x2, double y2, double x3, double y3) {
        // Newton-Raphson method to solve the inverse bilinear interpolation
        double u = 0.5, v = 0.5;  // Initial guess
        double epsilon = 0.0001;
        int maxIterations = 20;

        for (int i = 0; i < maxIterations; i++) {
            // Compute the bilinear interpolation at current (u,v)
            double bx = (1-u)*(1-v)*x0 + u*(1-v)*x1 + u*v*x2 + (1-u)*v*x3;
            double by = (1-u)*(1-v)*y0 + u*(1-v)*y1 + u*v*y2 + (1-u)*v*y3;

            // Compute the error
            double ex = x - bx;
            double ey = y - by;

            if (Math.abs(ex) < epsilon && Math.abs(ey) < epsilon) {
                // Check if result is within valid range
                if (u >= -epsilon && u <= 1+epsilon && v >= -epsilon && v <= 1+epsilon) {
                    return new double[]{
                        Math.max(0, Math.min(1, u)),
                        Math.max(0, Math.min(1, v))
                    };
                }
                break;
            }

            // Compute Jacobian
            double dxdu = (1-v)*(-x0+x1) + v*(-x3+x2);
            double dxdv = (1-u)*(-x0+x3) + u*(-x1+x2);
            double dydu = (1-v)*(-y0+y1) + v*(-y3+y2);
            double dydv = (1-u)*(-y0+y3) + u*(-y1+y2);

            // Compute determinant
            double det = dxdu*dydv - dxdv*dydu;
            if (Math.abs(det) < epsilon) break;

            // Update u and v
            u += (dydv*ex - dxdv*ey) / det;
            v += (-dydu*ex + dxdu*ey) / det;
        }

        return null;
    }

    private static class Point3D {
        int x, y;
        double z;

        Point3D(int x, int y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class Face implements Comparable<Face> {
        int[] vertices;
        double depth;

        public Face(int[] vertices, double depth) {
            this.vertices = vertices;
            this.depth = depth;
        }

        @Override
        public int compareTo(Face other) {
            return Double.compare(this.depth, other.depth);
        }
    }
}