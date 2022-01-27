package Client;

import Client.Handlers.KeyHandler;
import Client.Handlers.MouseHandler;
import Client.Players.LocalPlayer;
import GameObject.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    void goodWhenClientConnectsToServer() {
        Level level = new Level(new KeyHandler(), new MouseHandler());
        level.update();

        assertEquals("waiting", level.getState());
    }

    @Test
    void goodWhenClientReceivesGameObjFromServer() throws IOException, ClassNotFoundException {
        SocketClient socketClient = new SocketClient();
        socketClient.startConnection("192.168.0.105", 6666);
        Game game = socketClient.sendAndReceiveGame(null);
        assertNotNull(game);
    }
}