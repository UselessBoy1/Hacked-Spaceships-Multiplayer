package Client.Players;

import java.awt.*;

public class MissileBar extends HpBar {
    final int VALUE_DIV = 3;
    final int MAX_VALUE = 450;

    public MissileBar(int x, int y) {
        super(x, y,300, "");
        value = 0;
    }

    public void update() {
        value++;
    }

    public boolean isReady() {
        return value == MAX_VALUE;
    }

    public void use() {
        value = 0;
    }

    @Override
    public void draw(Graphics2D g2) {
        // border
        g2.setColor(Color.black);
        g2.fillRect(X, Y, WIDTH, HEIGHT);

        // hp
        g2.setColor(Color.gray);
        g2.fillRect(X + BORDER_SIZE, Y + BORDER_SIZE, WIDTH - 2 * BORDER_SIZE, HEIGHT - 2 * BORDER_SIZE);
        g2.setColor(new Color(0, value / 2, 0));
        g2.fillRect(X + BORDER_SIZE, Y + BORDER_SIZE, value / VALUE_DIV, HEIGHT - 2 * BORDER_SIZE);

        drawSmallPicture(g2);
    }

    protected void drawSmallPicture(Graphics2D g2) {
        // small missile
        g2.setColor(Color.black);
        g2.fillRect(X + WIDTH + 5, Y - 6, 10, 10);
        g2.setColor(Color.gray);
        g2.fillRect(X + WIDTH + 7, Y + 4, 6, 15);
        g2.setColor(Color.black);
        g2.fillRect(X + WIDTH + 5, Y + 19, 10, 6);
        g2.setColor(Color.orange);
        g2.fillRect(X + WIDTH + 5, Y + 25, 10, 3);
        g2.setColor(Color.red);
        g2.fillRect(X + WIDTH + 7, Y + 25, 6, 2);
    }
}