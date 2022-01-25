package Client.Players;

import Client.KeyHandler;

public class LocalPlayer extends Player{
    private final KeyHandler keyHandler;

    public LocalPlayer(KeyHandler kH) {
        keyHandler = kH;
        name = "PLAYER";
        x = 400;
        y = 600;
        hpBar = new HpBar(20, 720, hp, name);
        loadAllImages("player/player_");
    }

    public void move() {
        if (keyHandler.rightPressed && !keyHandler.leftPressed) {
            x += SPEED;
//            currentImages = imagesTurnRight;
        }
        else if (keyHandler.leftPressed && !keyHandler.rightPressed) {
            x -= SPEED;
//            currentImages = imagesTurnLeft;
        }
//        else currentImages = images;

        if (keyHandler.upPressed) y -= SPEED;
        if (keyHandler.downPressed) y += SPEED;

//        shot();
        hpBar.setHp(hp);
        detectBorderCollision();
    }

    private void detectBorderCollision() {
        if (x <= 0) x = 0;
        if (x >= 1000 - width) x = 1000 - width;
        if (y <= 0) y = 0;
        if (y >= 750 - height) y = 750 - height;
    }
}
