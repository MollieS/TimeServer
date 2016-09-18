import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.time.LocalTime;

public class TimeServer implements SocketServer {

    @Override
    public void serve(Socket socket) {
        try {
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println("Time now: " + LocalTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
