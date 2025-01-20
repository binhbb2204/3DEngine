package src.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class TextureManager {
    private BufferedImage texture;

    public TextureManager(String TexturePath){
        loadTexture(TexturePath);
    }

    public void loadTexture(String TexturePath){
        try {
            texture = ImageIO.read(new File(TexturePath));
            resizePowerOfTwo();
        } catch (Exception e) {
            System.err.println("Error loading texture: " + e.getMessage());
            createCheckerboardTexture(256, 256);
        }
    }

    private void resizePowerOfTwo(){
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
        int tileSize = 32; // Size of each checker square
        Graphics2D g2d = img.createGraphics();
        
        for (int y = 0; y < height; y += tileSize) {
            for (int x = 0; x < width; x += tileSize) {
                boolean isWhite = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                g2d.setColor(isWhite ? Color.WHITE : Color.BLACK);
                g2d.fillRect(x, y, tileSize, tileSize);
            }
        }
        
        g2d.dispose();
        return img;
    }

    public BufferedImage getTexture() {
        return texture;
    }
}
