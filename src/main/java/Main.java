import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        SocketService socketService = new SocketService();
        try {
            socketService.serve(portNumber, new TimeServer());
        } catch (IOException e) {
            System.out.println("Cannot get IO connection");
            System.out.println(e.getMessage());
        }
    }
}
