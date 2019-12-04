import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// TODO Consider to refactor send and recieve function
// TODO in order  to remove extra code
public class FileServer {
    public static ServerSocket serverSocket;
    public static Socket clientSocket = null;
    private static final int PORT = 4444;
    public static void main(String[] args) throws IOException{
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started at Port: " + PORT);
        }catch(Exception e){
            System.out.println("ERROR: " + e);
            System.exit(1);
        }

        while (true){
            try{
                // TODO consider some validations and security in future
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);

                Thread thread = new Thread(new ClientConnection(clientSocket));

                thread.start();
            }catch(Exception e){
                System.err.println("Error in connection attempt.");
            }
        }
    }
}
