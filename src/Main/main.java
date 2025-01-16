package src.Main;

import javax.swing.SwingUtilities;

import src.Block.Block3D;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String texturePack = "src/img/Box1.jpg";
            new Block3D(texturePack).setVisible(true);
        });
    }
}
