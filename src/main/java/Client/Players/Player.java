package Client.Players;

import Client.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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

    public ArrayList<Bullet> bullets = new ArrayList<>();
//    public ArrayList<Point> bulletsPos = new ArrayList<>();
    protected boolean lostBullet = false;

    // opponent constructor
    public Player() {
        name = "OPPONENT";
        x = -100;
        y = -100;
        hpBar = new HpBar(20, 25, hp, name);
        loadAllImages("red_enemy/enemy1_");
    }

    public boolean collision(Player enemy) {
        if ((enemy.x + enemy.width) > this.x && enemy.x < (this.x + this.width))
            if ((enemy.y + enemy.height - enemy.POINT_Y2) > this.y && (enemy.y + enemy.POINT_Y1) < (this.y + this.height))
                return true;
        if ((enemy.x + enemy.POINT_X2) > (this.x + this.POINT_X1) && (enemy.x + enemy.POINT_X1) < (this.x + this.POINT_X2))
            return (enemy.y + enemy.height) > this.y && enemy.y < (this.y + this.height);
        return false;
    }

    public void moveBullets() {
        for (int i = 0; i < bullets.size(); ++i) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            if (bullet.outOfGame()) {
                lostBullet = true;
                bullets.remove(i);
                --i;
            }
        }
    }

    public void setHp(int val) {
        hp = val;
    }

    public void refreshHp() {
        hpBar.setHp(hp);
    }

    public void refreshBulletsPos() {
        for (int i = 0; i < bullets.size(); ++i) {
            bullets.get(i).x = GamePanel.WIDTH - bullets.get(i).width - bullets.get(i).x;
            bullets.get(i).y = GamePanel.HEIGHT - bullets.get(i).height - bullets.get(i).y;
            bullets.get(i).goDown = true;
        }
    }

    public void drawBullets(Graphics2D g2) {
        for (int i = 0; i < bullets.size(); ++i) {
            Bullet b = bullets.get(i);
            b.draw(g2);
        }
    }
    public Point getPos() {
        return new Point(x, y);
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
