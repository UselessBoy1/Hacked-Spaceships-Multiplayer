package Client.Players;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public class Bullet implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    public int x, y;
    public int width = 5, height = 30;
    public boolean goDown;
    protected int speed = 3;
    protected int power = 30;
    public double hitDrawScale = 0.2;

    public Bullet(int x, int y, boolean goDown) {
        this.x = x;
        this.y = y - 10;
        this.goDown = goDown;
    }

    public int getPower() {
        return power;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fillRect(x, y, width, height);
    }

    public void move() {
        if (goDown) y += speed;
        else y -= speed;
    }

    public boolean outOfGame() {
        if (goDown) return y >= 750;
        else return y <= 0;
    }

    public boolean hit(Player target) {
        if (this.x > (target.x + target.POINT_X1) && (this.x + 5) < (target.x + target.POINT_X2))
            if (this.y > target.y && (this.y + 5) < (target.y + target.height))
                return true;
        if (this.x > target.x && (this.x + 5) < (target.x + target.width))
            return this.y > (target.y + target.POINT_Y1) && (this.y + 5) < (target.y + target.POINT_Y2);
        return false;
    }
}
