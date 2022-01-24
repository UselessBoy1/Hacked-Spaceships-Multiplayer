package Client;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SocketClientTest {

    @RepeatedTest(value = 4)
    public void clientReceivesMessageFromServer() throws IOException {
        SocketClient testClient = new SocketClient();
        testClient.startConnection("127.0.0.1", 6666);

        String msg = testClient.receiveMessage();
        testClient.sendMessage("Hello server!");

//        testClient.stopConnection();

        assertEquals("[SERVER] Hello client " + msg.charAt(msg.length() - 1), msg);

        String msg2 = testClient.receiveMessage();
    }

}