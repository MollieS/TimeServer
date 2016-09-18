import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class SocketServiceTest {

    private SocketService socketService = new SocketService();
    private SocketServer connectionCounter;
    private int connections = 0;

    @Before
    public void setUp() {
        connectionCounter = socket -> connections++;
    }

    @After
    public void tearDown() throws IOException {
        socketService.close();
    }

    @Test
    public void canMakeAConnection() throws IOException {
        socketService.serve(9000, connectionCounter);
        connect(9000);
        assertEquals(1, connections);
    }

    @Test
    public void canMakeManyConnections() throws IOException {
        socketService.serve(9000, connectionCounter);
        for (int i = 0; i < 10; i++) {
            connect(9000);
        }
        assertEquals(10, connections);
    }

    @Test
    public void sendsTheTime() throws IOException {
        socketService.serve(9000, new MockTimeServer());
        Socket socket = new Socket("localhost", 9000);
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String time = bufferedReader.readLine();
        socket.close();
        assertEquals("14:00", time);
    }

    private void connect(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            Thread.sleep(1000);
            socket.close();
        } catch (UnknownHostException e) {
            fail("Unknown host");
        } catch (IOException e) {
            fail("Could not connect");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class MockTimeServer implements SocketServer {
        @Override
        public void serve(Socket socket) {
            try {
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                printStream.println(LocalTime.of(14, 00));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
