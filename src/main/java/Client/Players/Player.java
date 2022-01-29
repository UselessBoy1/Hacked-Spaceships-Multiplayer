package Client.Players;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class Player {
    // player position
    // special points are used to better collision and bullets hits detection
    public final int SPECIAL_POINT_X1 = 49, SPECIAL_POINT_X2 = 74;
    public final int SPECIAL_POINT_Y1 = 38, SPECIAL_POINT_Y2 = 63;
    public final int WIDTH = 120, HEIGHT = 120;
    String name;

    Point pos;

    final int SPEED = 6;

    HpBar hpBar;
    int hp = 600;


    boolean shieldActive = false;
    Shield shield;

    final int NUM_OF_IMAGES = 11;
    BufferedImage[] images = new BufferedImage[NUM_OF_IMAGES];
    BufferedImage[] boomImages = new BufferedImage[NUM_OF_IMAGES];
    int animationCounter = 0;

    public LinkedList<Bullet> bullets = new LinkedList<>();

    // opponent constructor
    public Player() {
        name = "OPPONENT";
        pos = new Point(-100, -100);
        shield = new Shield(this);
        hpBar = new HpBar(5, 25, hp, name);
        loadAllImages("red_enemy/enemy1_");
    }

    public boolean collision(Player enemy) {
        if ((enemy.pos.x + enemy.WIDTH) > this.pos.x && enemy.pos.x < (this.pos.x + this.WIDTH))
            if ((enemy.pos.y + enemy.HEIGHT - enemy.SPECIAL_POINT_Y2) > this.pos.y && (enemy.pos.y + enemy.SPECIAL_POINT_Y1) < (this.pos.y + this.HEIGHT))
                return true;
        if ((enemy.pos.x + enemy.SPECIAL_POINT_X2) > (this.pos.x + this.SPECIAL_POINT_X1) && (enemy.pos.x + enemy.SPECIAL_POINT_X1) < (this.pos.x + this.SPECIAL_POINT_X2))
            return (enemy.pos.y + enemy.HEIGHT) > this.pos.y && enemy.pos.y < (this.pos.y + this.HEIGHT);
        return false;
    }

    public void setHp(int val) {
        hp = val;
    }

    public void refreshHp() {
        hpBar.setValue(hp);
    }

    public Point getPos() {
        return pos;
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public void setShieldActive(boolean shieldActive) {
        this.shieldActive = shieldActive;
    }

    public int getHp() {
        return hp;
    }

    public void decreaseHp(int val) {
        hp -= val;
    }

    public void draw(Graphics2D g2) {
        animationCounter++;
        if (animationCounter >= 58) {
            animationCounter = 0;
        }
        g2.drawImage(images[animationCounter / 6], pos.x, pos.y, null);
        if (isShieldActive()) {
            shield.draw(g2);
        }
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
