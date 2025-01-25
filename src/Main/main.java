package src.Main;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import src.Panel.ShapePanel;
import src.Shape.Block.Block3D;
import src.Shape.Cylinder.Cylinder;
import src.Shape.Sphere.Sphere;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String texturePath = "src/img/Box1.jpg";
            String iconPath = "src/img/Box.jpg";
            String cylinderPath = "src/img/Wood.jpg";
            // Create a JFrame
            JFrame frame = new JFrame("3D Engine");
            try {
                Image icon = ImageIO.read(new File(iconPath));
                frame.setIconImage(icon);
            } 
            catch (IOException e) {
                System.err.println("Error loading icon: " + e.getMessage());
            }
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            // JPanel panel = new JPanel(new GridLayout(1, 2));
            // Create the Block3D panel

            ShapePanel shapePanel = new ShapePanel();
            Block3D block3D = new Block3D(texturePath,0 ,0 ,0);
            Block3D block3D_1 = new Block3D(texturePath,0 ,200 ,0);
            Block3D block3D_2 = new Block3D(texturePath,0 ,-200 ,0);
            //Block3D block3D_3 = new Block3D(texturePath,0 ,-400 ,0);
            // Block3D block3D_4 = new Block3D(texturePath,0 ,400 ,0);
            // Block3D block3D_5 = new Block3D(texturePath,200 ,0 ,0);
            // Block3D block3D_6 = new Block3D(texturePath,400 ,0 ,0);
            // Block3D block3D_7 = new Block3D(texturePath,400 ,-200 ,0);
            // Block3D block3D_8 = new Block3D(texturePath,400 ,-400 ,0);
            // Block3D block3D_9 = new Block3D(texturePath,-200 ,0 ,0);
            // Block3D block3D_10 = new Block3D(texturePath,-400 ,0 ,0);
            // Block3D block3D_11 = new Block3D(texturePath,-400 ,200 ,0);
            // Block3D block3D_12 = new Block3D(texturePath,-400 ,400 ,0);
            // Block3D block3D_13 = new Block3D(texturePath,-200 ,-400 ,0);
            // Block3D block3D_14 = new Block3D(texturePath,-400 ,-400 ,0);
            // Block3D block3D_15 = new Block3D(texturePath,200 ,400 ,0);
            // Block3D block3D_16 = new Block3D(texturePath,400 ,400 ,0);
            Cylinder cylinder = new Cylinder(cylinderPath,200, 0, 0);
            Cylinder cylinder1 = new Cylinder(cylinderPath,-200, 0, 0);
            // Cylinder cylinder = new Cylinder(cylinderPath, 100, 100, 5);
            Sphere sphere = new Sphere(texturePath, 300, 300, 300);
            shapePanel.addShape(block3D);
            shapePanel.addShape(block3D_1);
            shapePanel.addShape(block3D_2);
            //shapePanel.addShape(block3D_3);
            // shapePanel.addShape(block3D_4);
            // shapePanel.addShape(block3D_5);
            // shapePanel.addShape(block3D_6);
            // shapePanel.addShape(block3D_7);
            // shapePanel.addShape(block3D_8);
            // shapePanel.addShape(block3D_9);
            // shapePanel.addShape(block3D_10);
            // shapePanel.addShape(block3D_11);
            // shapePanel.addShape(block3D_12);
            // shapePanel.addShape(block3D_13);
            // shapePanel.addShape(block3D_14);
            // shapePanel.addShape(block3D_15);
            // shapePanel.addShape(block3D_16);
            shapePanel.addShape(cylinder);
            shapePanel.addShape(cylinder1);
            shapePanel.addShape(sphere);
            // panel.add(block3D);
            //panel.add(cylinder);
            frame.add(shapePanel);
            

            // Set frame visibility
            frame.setVisible(true);
        });
    }
}
