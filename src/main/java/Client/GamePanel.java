package Client;

import Client.Game.Game;
import Client.Handlers.KeyHandler;
import Client.Handlers.MouseHandler;

import javax.swing.*;
import java.awt.*;

// this class uses build-in JPanel
// it handles:
//   - game loop -> draw and update Game class (game logic) on screen
//   - keyboards and mouse events (handlers are written in different class)
public class GamePanel extends JPanel implements Runnable {
    // widow size
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 750;
    public static final int FPS = 60;

    private Thread gameThread;
    private final KeyHandler keyHandler = new KeyHandler();
    private final MouseHandler mouseHandler = new MouseHandler();

    // game logic is in Game class
    private final Game game = new Game(keyHandler, mouseHandler);

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
        game.update();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        game.draw(graphics2D);

        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}
