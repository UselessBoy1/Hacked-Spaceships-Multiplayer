package Client;

import Client.Handlers.KeyHandler;
import Client.Handlers.MouseHandler;
import Client.Players.Bullet;
import Client.Players.LocalPlayer;
import Client.Players.Player;
import GameObject.Game;
import Client.Button.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class Level {
    private final BufferedImage backgroundImage;
    private final KeyHandler keyHandler;
    private Player opponentPlayer;
    private LocalPlayer localPlayer;

    // states
    private final String CONNECTING = "connecting";
    private final String WAITING = "waiting";
    private final String GAME = "game";
    private final String WIN = "win";
    private final String LOSE = "lose";
    private final String DRAW = "draw";

    private String state = CONNECTING;

    private final SocketClient socketClient = new SocketClient();
    private Game gameObjFromServer;
    private int playerId;

    private final LinkedList<Player> boomPlayers = new LinkedList<>();
    private final LinkedList<Hit> bulletHits = new LinkedList<>();
    private final int NUM_OF_IMAGES = 11;
    private final BufferedImage[] boomImages = new BufferedImage[NUM_OF_IMAGES];

    private Button resetButton;
    private final MouseHandler mouseHandler;

    public Level(KeyHandler kH, MouseHandler mH) {
        mouseHandler = mH;
        backgroundImage = loadBackgroundImage("/level_background/background_l1.png");
        keyHandler = kH;
        loadBoomImages();
    }

    // for test
    public String getState() {
        return state;
    }

    public void update() {
        switch (state) {
            case CONNECTING -> {
                try {
                    socketClient.startConnection();
                    gameObjFromServer = socketClient.sendAndReceiveGame(null);
                    playerId = gameObjFromServer.getPlayerId();
                    state = WAITING;
                    localPlayer = new LocalPlayer(keyHandler);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Fail to connect");
                    state = CONNECTING;
                }
            }
            case WAITING -> {
                try {
                    gameObjFromServer = socketClient.sendAndReceiveGame(gameObjFromServer);
                    if (gameObjFromServer.isReady()) {
                        state = GAME;
                        opponentPlayer = new Player();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Lost connection");
                    state = CONNECTING;
                }
            }
            case GAME -> {
                localPlayer.move();
                localPlayer.moveBullets();
                opponentPlayer.refreshHp();

                gameObjFromServer.setWinner(checkGameWinner());

                checkBulletsHits(localPlayer, opponentPlayer);

                if (playerId == 1) {
                    gameObjFromServer.updatePlayer1(localPlayer, opponentPlayer.getHp());
                }
                else { // 2
                    gameObjFromServer.updatePlayer2(localPlayer, opponentPlayer.getHp());
                }

                try {
                    gameObjFromServer = socketClient.sendAndReceiveGame(gameObjFromServer);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Lost connection");
                    state = CONNECTING;
                }
                if (playerId == 1) {
                    localPlayer.setHp(gameObjFromServer.getPlayer1HP());
                    opponentPlayer.x = GamePanel.WIDTH - opponentPlayer.width - gameObjFromServer.getPlayer2Position().x;
                    opponentPlayer.y = GamePanel.HEIGHT - opponentPlayer.height - gameObjFromServer.getPlayer2Position().y;
                    opponentPlayer.bullets = gameObjFromServer.getPlayers2BulletsPositions();
                    opponentPlayer.refreshBulletsPos();

                    String winner = gameObjFromServer.getWinner();
                    if ( ! winner.equals(Game.NONE)) {
                        resetButton = new Button(340, 400, 320, 100, Color.BLUE, new Color(0, 0, 150), "PLAY AGAIN", 50);
                        switch (winner) {
                            case Game.DRAW -> state = DRAW;
                            case Game.PLAYER_1 -> state = WIN;
                            case Game.PLAYER_2 -> state = LOSE;
                        }
                        mouseHandler.clicked = false;
                    }
                }
                else { // 2
                    localPlayer.setHp(gameObjFromServer.getPlayer2HP());
                    opponentPlayer.x = GamePanel.WIDTH - opponentPlayer.width - gameObjFromServer.getPlayer1Position().x;
                    opponentPlayer.y = GamePanel.HEIGHT - opponentPlayer.height - gameObjFromServer.getPlayer1Position().y;
                    opponentPlayer.bullets = gameObjFromServer.getPlayers1BulletsPositions();
                    opponentPlayer.refreshBulletsPos();

                    String winner = gameObjFromServer.getWinner();
                    if ( ! winner.equals(Game.NONE)) {
                        resetButton = new Button(340, 400, 320, 100, Color.BLUE, new Color(0, 0, 150), "PLAY AGAIN", 50);
                        switch (winner) {
                            case Game.DRAW -> state = DRAW;
                            case Game.PLAYER_1 -> state = LOSE;
                            case Game.PLAYER_2 -> state = WIN;
                        }
                        mouseHandler.clicked = false;
                    }
                }
            }
            case WIN, LOSE, DRAW -> {
                localPlayer.moveBullets();
                opponentPlayer.moveBullets();
                if (resetButton.isMouse(mouseHandler.mousePos)) {
                    resetButton.changeColorToFocus();
                    if (mouseHandler.clicked) {
                        mouseHandler.clicked = false;
                        state = CONNECTING;
                        try {
                            socketClient.stopConnection();
                        } catch (IOException ignored) {}
                    }
                 }
                else {
                    resetButton.changeColorToDefault();
                }
                mouseHandler.clicked = false;
//                System.out.println("win");
            }
        }
    }

    private String checkGameWinner() {
        if (localPlayer.collision(opponentPlayer)) {
            localPlayer.setHp(0);
            opponentPlayer.setHp(0);
        }

        if (localPlayer.getHp() <= 0 && opponentPlayer.getHp() <= 0) {
            startBoomAnimation(localPlayer);
            startBoomAnimation(opponentPlayer);
            return Game.DRAW;
        }
        else if (localPlayer.getHp() <= 0) {
            startBoomAnimation(localPlayer);
            if (playerId == 1)
                return Game.PLAYER_2;
            else
                return Game.PLAYER_1;
        }
        else if (opponentPlayer.getHp() <= 0){
            startBoomAnimation(opponentPlayer);
            if (playerId == 1)
                return Game.PLAYER_1;
            else
                return Game.PLAYER_2;
        }
        return Game.NONE;
    }

    private void checkBulletsHits(Player source, Player target) {
        // checks if bullet hits the target -> start drawing animation and decreases hp if necessary

        for (int i = 0; i < source.bullets.size(); ++i) {
            Bullet bullet = source.bullets.get(i);
            if (bullet.hit(target)) {
                startDrawingHit(bullet.x, bullet.y, bullet.hitDrawScale);
                source.bullets.remove(i);
                i--;
                target.decreaseHp(bullet.getPower());
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);
        switch (state) {
            case CONNECTING -> {
                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 50));
                g2.drawString("Connecting to server", 250, 300);
            }
            case WAITING -> {
                if (localPlayer != null) {
                    localPlayer.draw(g2);
                    localPlayer.drawHpBar(g2);
                }
                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 50));
                g2.drawString("Waiting for opponent", 250, 300);
            }
            case GAME -> {
                if (localPlayer != null && opponentPlayer != null) {
                    localPlayer.draw(g2);
                    localPlayer.drawBullets(g2);

                    opponentPlayer.draw(g2);
                    opponentPlayer.drawBullets(g2);

                    localPlayer.drawHpBar(g2);
                    opponentPlayer.drawHpBar(g2);

                    drawHits(g2);
                    drawAllBoomAnimations(g2);
                }
                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 40));
                g2.drawString("GAME: " + gameObjFromServer.getID(), 700, 30);
                g2.drawString("ID: " + playerId, 900, 30);
            }
            case WIN -> {
                localPlayer.draw(g2);
                localPlayer.drawBullets(g2);
                drawHits(g2);
                drawAllBoomAnimations(g2);
                g2.setColor(Color.green);
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("WIN", 390, 300);
                resetButton.draw(g2);
            }
            case LOSE -> {
                opponentPlayer.draw(g2);
                opponentPlayer.drawBullets(g2);
                drawHits(g2);
                drawAllBoomAnimations(g2);
                g2.setColor(new Color(230, 0, 0));
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("LOSE", 360, 300);
                resetButton.draw(g2);
            }
            case DRAW -> {
                drawHits(g2);
                drawAllBoomAnimations(g2);
                g2.setColor(Color.darkGray);
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("DRAW", 360, 300);
                resetButton.draw(g2);
            }
        }
    }

    private void startBoomAnimation(Player p) {
        p.isBoom = true;
        boomPlayers.add(p);
    }

    private void drawAllBoomAnimations(Graphics2D g2) {
        for (int i = 0; i < boomPlayers.size(); ++i) {
            Player p = boomPlayers.get(i);
            p.drawBoomAnimation(g2);
            if (!p.isBoom) {
                boomPlayers.remove(i);
                --i;
            }
        }
    }

    private class Hit {
        private final BufferedImage[] hitImages = Arrays.copyOf(boomImages, NUM_OF_IMAGES);
        private final int x, y;
        private int boomCounter = 0;
        public boolean isBoom = true;

        public Hit(int x, int y, double scale) {
            this.x = x - 5;
            this.y = y;
            scaleBoomImages(scale);
        }

        public void drawBoomAnimation(Graphics2D g2) {
            boomCounter++;
            if (boomCounter >= 40) {
                isBoom = false;
            }
            g2.drawImage(hitImages[boomCounter / 4], x, y, null);
        }

        protected void scaleBoomImages(double scale) {
            for (int i = 0; i < hitImages.length; ++i) {
                int w = hitImages[i].getWidth();
                int h = hitImages[i].getHeight();
                int integerScale = (int) Math.ceil(scale);
                BufferedImage after = new BufferedImage(w * integerScale, h * integerScale, BufferedImage.TYPE_INT_ARGB);
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                after = scaleOp.filter(hitImages[i], after);
                hitImages[i] = after;
            }
        }
    }
    private void startDrawingHit(int x, int y, double scale) {
        bulletHits.add(new Hit(x, y, scale));
    }
    private void drawHits(Graphics2D g2) {
        for (int i = 0; i < bulletHits.size(); ++i) {
            Hit h = bulletHits.get(i);
            h.drawBoomAnimation(g2);
            if (!h.isBoom) {
                bulletHits.remove(i);
                --i;
            }
        }
    }

    private void loadBoomImages() {
        for (int i = 1; i <= NUM_OF_IMAGES; ++i) {
            boomImages[i - 1] = loadBackgroundImage("/boom_animation/boom_" + i + ".png");
        }
    }
    private BufferedImage loadBackgroundImage(String path) {
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
