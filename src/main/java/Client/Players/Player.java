package Client.Players;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player {
    public int x, y;
    public final int POINT_X1 = 49, POINT_X2 = 74;
    public final int POINT_Y1 = 38, POINT_Y2 = 63;
    public int width = 120, height = 120;
    public String name;

    protected final int SPEED = 3;

    protected HpBar hpBar;
    protected int hp = 200;

    protected final int NUM_OF_IMAGES = 11;
    protected BufferedImage[] images = new BufferedImage[NUM_OF_IMAGES];
    protected BufferedImage[] boomImages = new BufferedImage[NUM_OF_IMAGES];
    protected int animationCounter = 0;

    // opponent constructor
    public Player() {
        name = "OPPONENT";
        x = 400;
        y = 100;
        hpBar = new HpBar(20, 25, hp, name);
        loadAllImages("red_enemy/enemy1_");
    }

    public Point getPos() {
        return new Point(x, y);
    }

    public int getHp() {
        return hp;
    }

    public void draw(Graphics2D g2) {
        animationCounter++;
        if (animationCounter >= 58) {
            animationCounter = 0;
        }
        g2.drawImage(images[animationCounter / 6], x, y, null);
    }

    public void drawHpBar(Graphics2D g2) {
        hpBar.draw(g2);
    }

    protected void loadAllImages(String imgFolderAndFilePathName) {
        for (int i = 1; i <= NUM_OF_IMAGES; ++i) {
            String IMG_PATH = "/" + imgFolderAndFilePathName;
            images[i - 1] = loadImage(IMG_PATH + i + ".png");
            boomImages[i - 1] = loadImage("/boom_animation/boom_" + i + ".png");
        }
    }

    protected BufferedImage loadImage(String path) {
        BufferedImage bg = null;
        try {
            bg = ImageIO.read(
                    Objects.requireNonNull(getClass().getResourceAsStream(path))
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bg;
    }
}
