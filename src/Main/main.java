package src.Main;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import src.Block.Block3D;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String texturePath = "src/img/Box1.jpg";
            String iconPath = "src/img/Box.jpg";
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
            // Create the Block3D panel
            Block3D block3D = new Block3D(texturePath);

            // Add the panel to the frame
            frame.add(block3D);

            // Set frame visibility
            frame.setVisible(true);
        });
    }
}
