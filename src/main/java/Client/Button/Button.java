package Client.Button;

import java.awt.*;

public class Button {
    private final int X, Y;
    private final int WIDTH, HEIGHT;
    private final String TEXT;
    private final Font font;
    private final Color COLOR;
    private final Color FOCUS_COLOR;
    private Color currentColor;

    public Button(int x, int y, int width, int height, Color color, Color focusColor, String text, int fontSize) {
        X = x; Y = y;
        WIDTH = width; HEIGHT = height;
        COLOR = color;
        FOCUS_COLOR = focusColor;
        currentColor = color;
        TEXT = text;
        font = new Font("FreeSans", Font.BOLD, fontSize);
    }

    public void draw(Graphics2D g2) {
        // border
        int BORDER_SIZE = 5;
        g2.setColor(Color.black);
        g2.fillRect(X - BORDER_SIZE, Y - BORDER_SIZE, WIDTH + BORDER_SIZE * 2, HEIGHT + BORDER_SIZE * 2);

        // normal color
        g2.setColor(currentColor);
        g2.fillRect(X, Y, WIDTH, HEIGHT);

        // text
        g2.setColor(Color.black);
        g2.setFont(font);
        g2.drawString(TEXT, X + (BORDER_SIZE), Y + (HEIGHT / 2 + BORDER_SIZE) + 16);
    }

    // methods to change color if mouse cursor is on this button.
    public void changeColorToDefault() {
        currentColor = COLOR;
    }

    public void changeColorToFocus() {
        currentColor = FOCUS_COLOR;
    }

    // check if mouse cursor is on this button
    public boolean isMouse(Point pos) {
        if (pos.x > X && pos.x < X + WIDTH) {
            return pos.y > Y && pos.y < Y + HEIGHT;
        }
        return false;
    }
}