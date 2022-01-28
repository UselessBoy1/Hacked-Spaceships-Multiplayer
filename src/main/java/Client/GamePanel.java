package Client;

import Client.Handlers.KeyHandler;
import Client.Handlers.MouseHandler;

import javax.swing.*;
import java.awt.*;

// this class uses build-in JPanel
// it handles:
//   - game loop -> draw and update Level class (game logic) on screen
//   - keyboards and mouse events (handlers are written in different class)
public class GamePanel extends JPanel implements Runnable {
    // widow size
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 750;
    final int FPS = 60;

    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();
    MouseHandler mouseHandler = new MouseHandler();

    // game logic is in Level class
    Level level = new Level(keyHandler, mouseHandler);

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1_000_000_000 / (double) FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                --delta;
            }
        }
    }

    private void update() {
        level.update();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        level.draw(graphics2D);

        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
