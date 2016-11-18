import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService {

    private ServerSocket serverSocket = null;
    private Thread socketThread = null;
    private boolean running = false;
    private SocketServer server;

    public void serve(int port, SocketServer server) throws IOException {
        this.server = server;
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
            new Thread(new ServiceRunnable(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        running = false;
        serverSocket.close();
    }

    public class ServiceRunnable implements Runnable {

        private Socket socket;

        public ServiceRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                server.serve(socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
