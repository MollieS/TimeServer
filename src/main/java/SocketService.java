import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService {

    private ServerSocket serverSocket = null;
    private Thread socketThread = null;
    private boolean running = false;
    private int connections = 0;
    private SocketServer itsServer;

    public void serve(int port, SocketServer server) throws IOException {
        itsServer = server;
        serverSocket = new ServerSocket(port);
        socketThread = createServerThread();
        socketThread.start();
    }

    private Thread createServerThread() {
        return new Thread(() -> {
            running = true;
            while (running) {
                acceptAndServeConnection();
            }
        });
    }

    private void acceptAndServeConnection() {
        try {
            Socket socket = serverSocket.accept();
            itsServer.serve(socket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        running = false;
        serverSocket.close();
    }

    public int connections() {
        return connections;
    }
}
