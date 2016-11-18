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
        MockTimeServer mockTimeServer = new MockTimeServer();
        socketService.serve(9000, mockTimeServer);
        Socket socket = new Socket("localhost", 9000);
        BufferedReader bufferedReader = mockTimeServer.getBufferedReader(socket);
        String time = bufferedReader.readLine();
        socket.close();
        assertEquals("14:00", time);
    }

    @Test
    public void canHandleMultipleSimultaneousConnections() throws IOException {
        MockTimeServer mockTimeServer = new MockTimeServer();
        socketService.serve(9000, mockTimeServer);
        Socket socket1 = new Socket("localhost", 9000);
        BufferedReader bufferedReader1 = mockTimeServer.getBufferedReader(socket1);

        Socket socket2 = new Socket("localhost", 9000);
        BufferedReader bufferedReader2 = mockTimeServer.getBufferedReader(socket2);

        String time1 = bufferedReader1.readLine();
        String time2 = bufferedReader2.readLine();

        assertEquals("14:00", time1);
        assertEquals("14:00", time2);
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
                PrintStream printStream = getPrintStream(socket);
                printStream.println(LocalTime.of(14, 00));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public PrintStream getPrintStream(Socket socket) throws IOException {
            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            return printStream;

        }

        public BufferedReader getBufferedReader(Socket socket) throws IOException {
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader;
        }
    }

}
