package Client.Players;

import Client.GamePanel;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;

public class Bullet implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    public Point pos;
    public int width = 5, height = 30;
    public boolean goDown;
    protected int speed = 6;
    protected int power = 30;
    public double hitDrawScale = 0.2;

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
        for (int i = 0; i < bullets.size(); ++i) {
            bullets.get(i).pos.x = GamePanel.WIDTH - bullets.get(i).width - bullets.get(i).pos.x;
            bullets.get(i).pos.y = GamePanel.HEIGHT - bullets.get(i).height - bullets.get(i).pos.y;
            bullets.get(i).goDown = true;
        }
    }

    public static void drawBullets(Graphics2D g2, LinkedList<Bullet> bullets) {
        for (int i = 0; i < bullets.size(); ++i) {
            Bullet b = bullets.get(i);
            b.draw(g2);
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fillRect(pos.x, pos.y, width, height);
    }

    public void move() {
        if (goDown) pos.x += speed;
        else pos.y -= speed;
    }

    public boolean outOfGame() {
        if (goDown) return pos.y >= 750;
        else return pos.y <= 0;
    }

    public boolean hit(Player target) {
        if (this.pos.x > (target.pos.x + target.SPECIAL_POINT_X1) && (this.pos.x + 5) < (target.pos.x + target.SPECIAL_POINT_X2))
            if (this.pos.y > target.pos.y && (this.pos.y + 5) < (target.pos.y + target.HEIGHT))
                return true;
        if (this.pos.x > target.pos.x && (this.pos.x + 5) < (target.pos.x + target.WIDTH))
            return this.pos.y > (target.pos.y + target.SPECIAL_POINT_Y1) && (this.pos.y + 5) < (target.pos.y + target.SPECIAL_POINT_Y2);
        return false;
    }

    public int getPower() {
        return power;
    }
}
