package src.Main;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final MouseInteractive target;
    private Point lastMousePos;

    public MouseHandler(MouseInteractive target){
        this.target = target;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        target.zoom(e.getWheelRotation());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastMousePos != null) {
            int dx = e.getX() - lastMousePos.x;
            int dy = -(e.getY() - lastMousePos.y);

            target.rotate(dx * 0.01, dy * 0.01);
            lastMousePos = e.getPoint();
            
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

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
}
