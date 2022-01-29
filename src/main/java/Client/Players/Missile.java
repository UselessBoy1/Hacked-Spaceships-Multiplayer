package Client.Players;

import java.awt.*;

public class Missile extends Bullet {
    private final int TARGET_X;

    public Missile(int x, int y, boolean goDown, int playerX) {
        super(x, y, goDown);
        TARGET_X = playerX + 50;
        speed = 5;
        power = 300;
        hitDrawScale = 0.5;
        width = 10;
    }

    @Override
    public void move() {
        if (pos.x > TARGET_X + 5) pos.x -= speed;
        else if (pos.x < TARGET_X - 5) pos.x += speed;
        if (goDown) pos.y += speed;
        else pos.y -= speed;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (goDown) {
            g2.setColor(Color.orange);
            g2.fillRect(pos.x, pos.y, width, 3);
            g2.setColor(Color.red);
            g2.fillRect(pos.x + 2, pos.y + 1, width - 4, 2);
            g2.setColor(Color.black);
            g2.fillRect(pos.x, pos.y + 3, width, 6);
            g2.setColor(Color.gray);
            g2.fillRect(pos.x + 2, pos.y + 9, width - 4, 17);
            g2.setColor(Color.black);
            g2.fillRect(pos.x, pos.y + 26, width, 14);
        }
        else {
            g2.setColor(Color.black);
            g2.fillRect(pos.x, pos.y, width, 14);
            g2.setColor(Color.gray);
            g2.fillRect(pos.x + 2, pos.y + 14, width - 4, 17);
            g2.setColor(Color.black);
            g2.fillRect(pos.x, pos.y + 31, width, 6);
            g2.setColor(Color.orange);
            g2.fillRect(pos.x, pos.y + 37, width, 3);
            g2.setColor(Color.red);
            g2.fillRect(pos.x + 2, pos.y + 37, width - 4, 2);
        }
    }
}
