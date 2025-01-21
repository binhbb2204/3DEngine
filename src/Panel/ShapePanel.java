package src.Panel;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.Main.MouseHandler;
import src.Main.MouseInteractive;
import src.util.Face;

public class ShapePanel extends JPanel implements MouseInteractive{
    private int WIDTH = 800;
    private int HEIGHT = 800;
    private double angleX = 0;
    private double angleY = 0;
    private double scale = 1.0;
    private static final double ZOOM_FACTOR = 0.1;

    private List<Shape3D> shapes;

    public interface Shape3D {
        List<Face> getFaces(double angleX, double angleY);
        void renderFace(BufferedImage buffer, Face face, double angleX, double angleY, double scale);
        double[][] getVertices();
    }

    public ShapePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(400, 400));
        shapes = new ArrayList<>();

        MouseHandler mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
    }

    public void addShape(Shape3D shape){
        shapes.add(shape);
    }
    @Override
    public void rotate(double dAngleY, double dAngleX) {
        this.angleX += dAngleX;
        this.angleY += dAngleY;   
        repaint();
    }

    @Override
    public void zoom(int wheelRotation) {
        if (wheelRotation < 0) {
            scale *= (1 + ZOOM_FACTOR);
        } else {
            scale *= (1 - ZOOM_FACTOR);
        }
        scale = Math.max(0.1, Math.min(scale, 5.0));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        List<Face> sortedFaces = getAllFaces(angleX, angleY);
        for (Face face : sortedFaces) {
            face.getShape().renderFace(buffer, face, angleX, angleY, scale);
        }

        g.drawImage(buffer, 0, 0, this);
    }

    private List<Face> getAllFaces(double angleX, double angleY){
        List<Face> allFaces = new ArrayList<>();

        for(Shape3D shape: shapes){
            List<Face> shapFaces = shape.getFaces(angleX, angleY);
            allFaces.addAll(shapFaces);
        }
        Collections.sort(allFaces);
        return allFaces;
    }
    
}
