package Client.Game;

import Client.GamePanel;
import Client.Handlers.KeyHandler;
import Client.Handlers.MouseHandler;
import Client.Players.Bullet;
import Client.Players.LocalPlayer;
import Client.Players.Player;
import Client.SocketClient;
import GameDataObject.GameDataObject;
import Client.Button.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Game {
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
    private GameDataObject gameDataObjectFromServer;
    private int playerId;

    private final HitsAndBoomAnimationController hitsAndBoomAnimationController= new HitsAndBoomAnimationController();

    private Button resetButton;
    private final MouseHandler mouseHandler;

    private int connectionWithServerCounter = 0;

    public Game(KeyHandler kH, MouseHandler mH) {
        mouseHandler = mH;
        backgroundImage = loadBackgroundImage("/level_background/background_l1.png");
        keyHandler = kH;
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
                    gameDataObjectFromServer = socketClient.sendAndReceiveGame(null);
                    playerId = gameDataObjectFromServer.getPlayerId();
                    state = WAITING;
                    localPlayer = new LocalPlayer(keyHandler);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Fail to connect");
                    state = CONNECTING;
                }
            }
            case WAITING -> {
                try {
                    gameDataObjectFromServer = socketClient.sendAndReceiveGame(gameDataObjectFromServer);
                    if (gameDataObjectFromServer.isReady()) {
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
                Bullet.moveBullets(localPlayer.bullets);
                opponentPlayer.refreshHp();

                gameDataObjectFromServer.setWinner(checkGameWinner());

                checkBulletsHits(localPlayer, opponentPlayer, true);
                checkBulletsHits(opponentPlayer, localPlayer, false);

                if (playerId == 1) {
                    gameDataObjectFromServer.updatePlayer1(localPlayer, opponentPlayer.getHp());
                }
                else { // 2
                    gameDataObjectFromServer.updatePlayer2(localPlayer, opponentPlayer.getHp());
                }

                connectionWithServerCounter++;
                if (connectionWithServerCounter == 2) {
                    connectionWithServerCounter = 0;
                    try {
                        gameDataObjectFromServer = socketClient.sendAndReceiveGame(gameDataObjectFromServer);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Lost connection");
                        state = CONNECTING;
                    }
                    // TODO refactor!!!
                    if (playerId == 1) {
                        localPlayer.setHp(gameDataObjectFromServer.getPlayer1HP());
                        opponentPlayer.getPos().x = GamePanel.WIDTH - opponentPlayer.WIDTH - gameDataObjectFromServer.getPlayer2Position().x;
                        opponentPlayer.getPos().y = GamePanel.HEIGHT - opponentPlayer.HEIGHT - gameDataObjectFromServer.getPlayer2Position().y;
                        opponentPlayer.bullets = gameDataObjectFromServer.getPlayers2BulletsPositions();
                        Bullet.refreshBulletsPos(opponentPlayer.bullets);

                        String winner = gameDataObjectFromServer.getWinner();
                        if (!winner.equals(GameDataObject.NONE)) {
                            resetButton = new Button(340, 400, 320, 100, Color.BLUE, new Color(0, 0, 150), "PLAY AGAIN", 50);
                            switch (winner) {
                                case GameDataObject.DRAW -> state = DRAW;
                                case GameDataObject.PLAYER_1 -> state = WIN;
                                case GameDataObject.PLAYER_2 -> state = LOSE;
                            }
                            mouseHandler.clicked = false;
                            try {
                                gameDataObjectFromServer = socketClient.sendAndReceiveGame(gameDataObjectFromServer);
                            } catch (IOException | ClassNotFoundException e) {
                                System.out.println("Lost connection");
                                state = CONNECTING;
                            }
                            checkGameWinner();
                        }
                    } else { // 2
                        localPlayer.setHp(gameDataObjectFromServer.getPlayer2HP());
                        opponentPlayer.getPos().x = GamePanel.WIDTH - opponentPlayer.WIDTH - gameDataObjectFromServer.getPlayer1Position().x;
                        opponentPlayer.getPos().y = GamePanel.HEIGHT - opponentPlayer.HEIGHT - gameDataObjectFromServer.getPlayer1Position().y;
                        opponentPlayer.bullets = gameDataObjectFromServer.getPlayers1BulletsPositions();
                        Bullet.refreshBulletsPos(opponentPlayer.bullets);


                        String winner = gameDataObjectFromServer.getWinner();
                        if (!winner.equals(GameDataObject.NONE)) {
                            resetButton = new Button(340, 400, 320, 100, Color.BLUE, new Color(0, 0, 150), "PLAY AGAIN", 50);
                            switch (winner) {
                                case GameDataObject.DRAW -> state = DRAW;
                                case GameDataObject.PLAYER_1 -> state = LOSE;
                                case GameDataObject.PLAYER_2 -> state = WIN;
                            }
                            mouseHandler.clicked = false;
                            try {
                                gameDataObjectFromServer = socketClient.sendAndReceiveGame(gameDataObjectFromServer);
                            } catch (IOException | ClassNotFoundException e) {
                                System.out.println("Lost connection");
                                state = CONNECTING;
                            }
                            checkGameWinner();
                        }
                    }
                }
            }
            case WIN, LOSE, DRAW -> {
                Bullet.moveBullets(localPlayer.bullets);
                Bullet.moveBullets(opponentPlayer.bullets);
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
            hitsAndBoomAnimationController.startBoomAnimation(localPlayer.getPos());
            hitsAndBoomAnimationController.startBoomAnimation(opponentPlayer.getPos());
            return GameDataObject.DRAW;
        }
        else if (localPlayer.getHp() <= 0) {
            hitsAndBoomAnimationController.startBoomAnimation(localPlayer.getPos());
            if (playerId == 1)
                return GameDataObject.PLAYER_2;
            else
                return GameDataObject.PLAYER_1;
        }
        else if (opponentPlayer.getHp() <= 0){
            hitsAndBoomAnimationController.startBoomAnimation(opponentPlayer.getPos());
            if (playerId == 1)
                return GameDataObject.PLAYER_1;
            else
                return GameDataObject.PLAYER_2;
        }
        return GameDataObject.NONE;
    }

    // TODO refactor
    private void checkBulletsHits(Player source, Player target, boolean hpDec) {
        // checks if bullet hits the target -> start drawing animation and decreases hp if necessary

        for (int i = 0; i < source.bullets.size(); ++i) {
            Bullet bullet = source.bullets.get(i);
            if (bullet.hit(target)) {
                if (hpDec) {
                    hitsAndBoomAnimationController.startDrawingHit(bullet.getPos().x, bullet.getPos().y, bullet.getHitDrawScale());
                    target.decreaseHp(bullet.getPower());
                }
                else {
                    hitsAndBoomAnimationController.startDrawingHit(bullet.getPos().x, bullet.getPos().y + bullet.getHeight(), bullet.getHitDrawScale());
                }
                source.bullets.remove(i);
                i--;
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
                    Bullet.drawBullets(g2, localPlayer.bullets);

                    opponentPlayer.draw(g2);
                    Bullet.drawBullets(g2, opponentPlayer.bullets);

                    localPlayer.drawHpBar(g2);
                    opponentPlayer.drawHpBar(g2);

                    hitsAndBoomAnimationController.draw(g2);
                }
                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 40));
                g2.drawString("GAME: " + gameDataObjectFromServer.getID(), 700, 30);
                g2.drawString("ID: " + playerId, 900, 30);
            }
            case WIN -> {
                localPlayer.draw(g2);
                Bullet.drawBullets(g2, localPlayer.bullets);
                hitsAndBoomAnimationController.draw(g2);
                g2.setColor(Color.green);
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("WIN", 390, 300);
                resetButton.draw(g2);
            }
            case LOSE -> {
                opponentPlayer.draw(g2);
                Bullet.drawBullets(g2, opponentPlayer.bullets);
                hitsAndBoomAnimationController.draw(g2);
                g2.setColor(new Color(230, 0, 0));
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("LOSE", 360, 300);
                resetButton.draw(g2);
            }
            case DRAW -> {
                hitsAndBoomAnimationController.draw(g2);
                g2.setColor(Color.darkGray);
                g2.setFont(new Font("FreeSans", Font.BOLD, 100));
                g2.drawString("DRAW", 360, 300);
                resetButton.draw(g2);
            }
        }
    }

    // TODO BUG on second client

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
