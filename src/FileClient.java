import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient {

    private static Socket socket;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;
    private static  int PORT = 4444;
    public static void main(String [] args) throws IOException{
        try {
            socket = new Socket("localhost", PORT);
            stdin = new BufferedReader(new InputStreamReader(System.in));
        }catch(Exception e){
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        os = new PrintStream(socket.getOutputStream());

        try{
            switch(Integer.parseInt(selectAction())){
                case 1:
                    os.println("1");
                    sendFile();
                    break;
                case 2:
                    os.println("2");
                    System.err.print("Enter file name: ");
                    fileName = stdin.readLine();
                    os.println(fileName);
                    recieveFile(fileName);
                    break;
                default:
                    System.out.println("Something Went Wrong!!");
                    break;
            }
        }catch(Exception e){
            System.err.println("not valid input");
        }

    }

    public static String selectAction() throws IOException{
        System.out.println("1. Send file.");
        System.out.println("2. Recieve file.");
        System.out.print("\nMake selection: ");

        return stdin.readLine();
    }

    public static void sendFile(){
        try{
            System.err.println("Enter file name: ");
            fileName = stdin.readLine();

            File file = new File(fileName);
            byte [] byteArray = new byte[(int) file.length()];

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            dataInputStream.readFully(byteArray, 0, byteArray.length);

            OutputStream outputStream = socket.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(byteArray.length);
            dataOutputStream.write(byteArray, 0, byteArray.length);

            dataOutputStream.flush();
            System.out.println("File "+fileName+" sent to Server.");
        }catch(Exception e){
            System.out.println("File Does Not Exist!");
        }
    }

    public static void recieveFile(String fileName){
        try {
            int bytesRead;
            InputStream in = socket.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            fileName = clientData.readUTF();

            OutputStream outputStream = new FileOutputStream("recieved_" + fileName);

            long size = clientData.readLong();
            byte [] buffer = new byte[1024];

            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1){
                outputStream.write(buffer, 0, bytesRead);
                System.out.println("buffer: " + buffer);
                System.out.println("bytesRead: " + bytesRead);
                size -= bytesRead;
            }

            outputStream.close();
            in.close();
        }catch(IOException e){
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, e);

        }
    }
}
