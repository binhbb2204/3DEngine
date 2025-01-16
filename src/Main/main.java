package src.Main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import src.Block.Block3D;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String texturePath = "src/img/Box1.jpg";

            // Create a JFrame
            JFrame frame = new JFrame("3D Block Viewer");
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create the Block3D panel
            Block3D block3D = new Block3D(texturePath);

            // Add the panel to the frame
            frame.add(block3D);

            // Set frame visibility
            frame.setVisible(true);
        });
    }
}
