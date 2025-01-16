package src.Testing;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Testing extends JFrame{
    private static final int HEIGHT = 800;
    private static final int WIDTH = 800;
    private double[][] vertices = { //[][] represents matrix, where first [] will be row, second [] will be column 
        {-50, -50, -50},
        {50, -50, -50},
        {50, 50, -50},
        {-50, 50, -50},
        

        {-50, -50, 50},
        {50, -50, 50},
        {50, 50, 50},
        {-50, 50, 50},
        
    };

    private int[][] edges = {
        {0, 1}, {1, 2}, {2, 3}, {3, 0}, //mặt trước hình khối
        {4, 5}, {5, 6}, {6, 7}, {7, 4}, //mặt sau hình khối
        {0, 4}, {1, 5}, {2, 6}, {3, 7}, //những cạnh còn lại chưa dc edge
    };

    private Color[] faceColors = {
        Color.RED, Color.GREEN, Color.BLUE,
        Color.YELLOW, Color.CYAN, Color.MAGENTA
    };

    private BufferedImage[] textures = new BufferedImage[6];
    private double angleX = 0;
    private double angleY = 0;
    private Point lastMousePos;

    public Testing(){
        setTitle("3D Engine");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                lastMousePos = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(lastMousePos != null){
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;

                    angleY += dx * 0.01;
                    angleX += dy * 0.01;

                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
            
        });
        try {
            textures[0] = ImageIO.read(new File("img/Box.jpg"));
            textures[1] = ImageIO.read(new File("img/Box.jpg"));
            textures[2] = ImageIO.read(new File("img/Box.jpg"));
            textures[3] = ImageIO.read(new File("img/Box.jpg"));
            textures[4] = ImageIO.read(new File("img/Box.jpg"));
            textures[5] = ImageIO.read(new File("img/Box.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] rotatePoint(double[] point){
        double[] rotated = new double[3];

        /*Use cos because cos = kề/huyền, khi mình rotate theo Ox hay Oy thì cái cạnh đó sẽ di chuyển tới new coor, 
        và cạnh mới sẽ đóng như cạnh huyền, cạnh cũ là cạnh kề*/
        /*And point is {x, y, z} point[0] = x, point[1] = y, point[2] = z 
         * clockwise rotation matrix for Y axis, counterclockwise rotation matrix for X axis
        */

        //Rotate around Y axis
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double tempX = point[0] * cosY + point[2] * sinY;
        double tempZ = -point[0] * sinY + point[2] * cosY; 

        //Rotate around X axis
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        rotated[0] = tempX;
        rotated[1] = point[1] * cosX - tempZ * sinX;
        rotated[2] = point[1] * sinX + tempZ * cosX;

        return rotated;
    }

    private Point project(double[] point3D){
        double z = 400;
        double scale = 200;
        int x = (int)(point3D[0] * scale / (z - point3D[2]) + WIDTH / 2);
        int y = (int)(point3D[1] * scale / (z - point3D[2]) + HEIGHT / 2);

        return new Point(x, y);
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();

        //Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        

        //Draw edges (cạnh block)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        for(int[] edge: edges){
            double[] p1 = rotatePoint(vertices[edge[0]]);
            double[] p2 = rotatePoint(vertices[edge[1]]);

            Point pp1 = project(p1);
            Point pp2 = project(p2);

            g2d.drawLine(pp1.x, pp1.y, pp2.x, pp2.y);
        }

        // Draw textures for each side of the cube
        drawTexturedFace(g2d, 0, new int[]{0, 1, 2, 3}); // Front face
        drawTexturedFace(g2d, 1, new int[]{4, 5, 6, 7}); // Back face
        drawTexturedFace(g2d, 2, new int[]{0, 4, 7, 3}); // Left face
        drawTexturedFace(g2d, 3, new int[]{1, 5, 6, 2}); // Right face
        drawTexturedFace(g2d, 4, new int[]{3, 2, 6, 7}); // Top face
        drawTexturedFace(g2d, 5, new int[]{0, 1, 5, 4}); // Bottom face

        g2d.setColor(Color.RED);
        drawAxis(g2d, new double[]{0, 0, 0}, new double[]{50, 0, 0}); //X
        g2d.setColor(Color.GREEN);
        drawAxis(g2d, new double[]{0, 0, 0}, new double[]{0, 50, 0}); //Y
        g2d.setColor(Color.BLUE);
        drawAxis(g2d, new double[]{0, 0, 0}, new double[]{0, 0, 50}); //Z

        g.drawImage(buffer, 0,0, this);
    }

    public void drawTexturedFace(Graphics2D g2d, int textureIndex, int[] faceVertices){
        Point[] points = new Point[4];
        for(int i = 0; i < 4; i++){
            double[] rotated = rotatePoint(vertices[faceVertices[i]]);
            points[i] = project(rotated);
        }

        g2d.drawImage(textures[textureIndex], points[0].x, points[0].y, points[1].x, points[1].y, points[2].x, points[2].y, points[3].x, points[3].y, null);
    }
    public void drawAxis(Graphics2D g2d, double[] start, double[] end){
        Point p1 = project(rotatePoint(start));
        Point p2 = project(rotatePoint(end));
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Testing().setVisible(true);
        });
    }
}
