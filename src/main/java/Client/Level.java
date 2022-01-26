package Client;

import Client.Handlers.KeyHandler;
import Client.Players.Bullet;
import Client.Players.LocalPlayer;
import Client.Players.Player;
import GameObject.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Level {
    private final BufferedImage backgroundImage;
    private final KeyHandler keyHandler;
    private Player opponentPlayer;
    private LocalPlayer localPlayer;

    private final int FONT_SIZE = 50;
    private final Font font = new Font("FreeSans", Font.BOLD, FONT_SIZE);
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

    public Level(KeyHandler kH) {
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
                    socketClient.startConnection("192.168.0.105", 6666);
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
                checkGameState();
                checkLocalPlayerBulletsHits();

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
                        switch (winner) {
                            case Game.DRAW -> state = DRAW;
                            case Game.PLAYER_1 -> state = WIN;
                            case Game.PLAYER_2 -> state = LOSE;
                        }
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
                        switch (winner) {
                            case Game.DRAW -> state = DRAW;
                            case Game.PLAYER_1 -> state = LOSE;
                            case Game.PLAYER_2 -> state = WIN;
                        }
                    }
                }
            }
            case WIN -> {
//                System.out.println("win");
            }
            case LOSE -> {
//                System.out.println("lose");
            }
            case DRAW -> {
//                System.out.println("draw");
            }
        }
    }

    private void checkGameState() {
        if (localPlayer.collision(opponentPlayer)) {
            localPlayer.setHp(0);
            opponentPlayer.setHp(0);
        }

        if (localPlayer.getHp() <= 0 && opponentPlayer.getHp() <= 0) {
            gameObjFromServer.setWinner(Game.DRAW);
        }
        else if (localPlayer.getHp() <= 0) {
            if (playerId == 1) gameObjFromServer.setWinner(Game.PLAYER_2);
            else gameObjFromServer.setWinner(Game.PLAYER_1);
        }
        else if (opponentPlayer.getHp() <= 0){
            if (playerId == 1) gameObjFromServer.setWinner(Game.PLAYER_1);
            else gameObjFromServer.setWinner(Game.PLAYER_2);
        }
    }

    private void checkLocalPlayerBulletsHits() {
        for (int i = 0; i < localPlayer.bullets.size(); ++i) {
            Bullet localPlayerBullet = localPlayer.bullets.get(i);
            if (localPlayerBullet.hit(opponentPlayer)) {
                // TODO draw hit
                localPlayer.bullets.remove(i);
                i--;
                opponentPlayer.decreaseHp(localPlayerBullet.getPower());
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);
        switch (state) {
            case CONNECTING -> {
                g2.setColor(Color.black);
                g2.setFont(font);
                g2.drawString("Connecting to server", 250, 300);
            }
            case WAITING -> {
                if (localPlayer != null) {
                    localPlayer.draw(g2);
                    localPlayer.drawHpBar(g2);
                }
                g2.setColor(Color.black);
                g2.setFont(font);
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
                }
                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 40));
                g2.drawString("GAME: " + gameObjFromServer.getID(), 700, 30);
                g2.drawString("ID: " + playerId, 900, 30);
            }
            case WIN -> {
                localPlayer.draw(g2);
                localPlayer.drawBullets(g2);
                g2.setColor(Color.green);
                g2.setFont(font);
                g2.drawString("WIN", 390, 300);
            }
            case LOSE -> {
                opponentPlayer.draw(g2);
                opponentPlayer.drawBullets(g2);
                g2.setColor(new Color(230, 0, 0));
                g2.setFont(font);
                g2.drawString("LOSE", 360, 300);
            }
            case DRAW -> {
                g2.setColor(Color.darkGray);
                g2.setFont(font);
                g2.drawString("DRAW", 360, 300);
            }
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
