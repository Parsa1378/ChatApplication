import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("[SERVER] Waiting For Client Connection");
        Socket client = listener.accept();
        System.out.println("[SERVER] Connected To The Client");

    }
}
