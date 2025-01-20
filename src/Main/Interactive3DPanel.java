package src.Main;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public abstract class Interactive3DPanel extends JPanel {
    protected int HEIGHT = 800;
    protected int WIDTH = 800;
        
    protected double posX = 0;
    protected double posY = 0;
    protected double posZ = 0;
    protected double angleX = 0;
    protected double angleY = 0;
    protected Point lastMousePos;
    protected double scale = 1.0;
    protected static final double ZOOM_FACTOR = 0.1;

    protected Interactive3DPanel() {
        setupEventListeners();
    }
    
    private void setupEventListeners(){
        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                WIDTH = getWidth();
                HEIGHT = getHeight();
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
            
        });
        setupMouseListeners();
    }

    public void setupMouseListeners(){
        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
            
        });

        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
            
        });

        addMouseWheelListener(this::handleMouseWheeled);
    }

    private void handleMouseDragged(MouseEvent e){
        if (lastMousePos != null) {
            int dx = e.getX() - lastMousePos.x;
            int dy = -(e.getY() - lastMousePos.y);

            angleY += dx * 0.01;
            angleX += dy * 0.01;

            lastMousePos = e.getPoint();
            repaint();
        }
    }
    private void handleMouseWheeled(MouseWheelEvent e){
        if(e.getWheelRotation() < 0){
            scale *= (1 + ZOOM_FACTOR);
        }
        else{
            scale *= (1 - ZOOM_FACTOR);
        }

        scale = Math.max(0.1, Math.min(scale, 5.0));
        repaint();
    }
    protected void setPosition(double x, double y, double z) {
        
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        repaint();
    }

    public abstract void renderFaces(BufferedImage buffer);
    public abstract void drawTexturedFace(BufferedImage buffer, int[] faceVertices);
}
