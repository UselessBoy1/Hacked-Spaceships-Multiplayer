package Client.Players;

import java.awt.*;

// class which draws player's hp bar
public class HpBar {
    final int X, Y;
    final int WIDTH, HEIGHT;

    final int FONT_Y;
    final int FONT_SIZE = 20;

    final int BORDER_SIZE = 5;

    int value;
    final int HP_DIV = 2;

    final Font font = new Font("FreeSans", Font.BOLD, FONT_SIZE);
    final String TEXT;

    public HpBar(int x, int y, int hp, String text) {
        X = x;
        Y = y;
        FONT_Y = y - FONT_SIZE / 3;
        value = hp;
        WIDTH = hp / HP_DIV + 2 * BORDER_SIZE;
        HEIGHT = 20;
        TEXT = text;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void draw(Graphics2D g2) {
        // text
        g2.setColor(Color.black);
        g2.setFont(font);
        g2.drawString(TEXT, X, FONT_Y);

        // border
        g2.fillRect(X, Y, WIDTH, HEIGHT);

        // hp
        g2.setColor(Color.red);
        g2.fillRect(X + BORDER_SIZE, Y + BORDER_SIZE, WIDTH - 2 * BORDER_SIZE, HEIGHT - 2 * BORDER_SIZE);
        g2.setColor(Color.green);
        g2.fillRect(X + BORDER_SIZE, Y + BORDER_SIZE, value / HP_DIV, HEIGHT - 2 * BORDER_SIZE);
    }
}