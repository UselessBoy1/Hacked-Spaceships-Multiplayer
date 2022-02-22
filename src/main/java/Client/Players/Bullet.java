package Client.Players;

import Client.GamePanel;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;

public class Bullet implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    final Point pos;
    int width = 5, height = 30;
    boolean goDown;
    int speed = 6;
    int power = 30;
    double hitDrawScale = 0.2;

    public Bullet(int x, int y, boolean goDown) {
        this.pos = new Point(x, y - 10);
        this.goDown = goDown;
    }

    public static void moveBullets(LinkedList<Bullet> bullets) {
        for (int i = 0; i < bullets.size(); ++i) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            if (bullet.outOfGame()) {
                bullets.remove(i);
                --i;
            }
        }
    }

    public static void refreshBulletsPos(LinkedList<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            bullet.pos.x = GamePanel.WIDTH - bullet.width - bullet.pos.x;
            bullet.pos.y = GamePanel.HEIGHT - bullet.height - bullet.pos.y;
            bullet.goDown = true;
        }
    }

    public static void drawBullets(Graphics2D g2, LinkedList<Bullet> bullets) {
        for (Bullet b : bullets) {
            b.draw(g2);
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fillRect(pos.x, pos.y, width, height);
    }

    public void move() {
        if (goDown) pos.y += speed;
        else pos.y -= speed;
    }

    public boolean outOfGame() {
        if (goDown) return pos.y >= 750;
        else return pos.y <= 0;
    }

    public boolean hit(Player target) {
        return true;
        /*if (this.pos.x > (target.pos.x + target.SPECIAL_POINT_X1) && (this.pos.x + this.width) < (target.pos.x + target.SPECIAL_POINT_X2)) {
            if (this.pos.y > target.pos.y && (this.pos.y) < (target.pos.y + target.HEIGHT))
                return true;

            // for draw opponent hits
            if (this.goDown && (this.pos.y + this.height + 20) > target.pos.y && (this.pos.y) < (target.pos.y + target.HEIGHT))
                return true;
        }
        if (this.pos.x > target.pos.x && (this.pos.x + this.width) < (target.pos.x + target.WIDTH)) {
            if (this.pos.y > (target.pos.y + target.SPECIAL_POINT_Y1) && (this.pos.y) < (target.pos.y + target.SPECIAL_POINT_Y2))
                return true;

            // for draw opponent hits
            if (this.goDown && ((this.pos.y + this.height - 5) > (target.pos.y + target.SPECIAL_POINT_Y1) && (this.pos.y) < (target.pos.y + target.SPECIAL_POINT_Y2)))
                return true;
        }
        return false;*/
    }

    public int getPower() {
        return power;
    }

    public Point getPos() {
        return pos;
    }

    public int getXPosWhereHit() {
        return pos.x + width / 2;
    }

    public int getHeight() {
        return height;
    }

    public double getHitDrawScale() {
        return hitDrawScale;
    }
}
