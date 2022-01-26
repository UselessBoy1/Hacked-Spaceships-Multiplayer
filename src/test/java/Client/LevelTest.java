package Client;

import Client.Handlers.KeyHandler;
import GameObject.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    public void goodWhenClientConnectsToServer() {
        Level level = new Level(new KeyHandler());
        level.update();

        assertEquals("waiting", level.getState());
    }

    @Test
    public void goodWhenClientReceivesGameObjFromServer() throws IOException, ClassNotFoundException {
        SocketClient socketClient = new SocketClient();
        socketClient.startConnection("192.168.0.105", 6666);
        Game game = socketClient.sendAndReceiveGame(null);
        assertNotNull(game);
    }
}