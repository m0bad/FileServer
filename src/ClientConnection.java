import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection implements Runnable {

    private Socket socketClient;
    private BufferedReader in = null;

    public ClientConnection(Socket socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    socketClient.getInputStream()));
            String clientChoice;
            while ((clientChoice = in.readLine()) != null) {
                switch (clientChoice) {
                    case "1":
                        recieveFile();
                        break;
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }
                        break;
                    default:
                        System.out.println("Wrong Choice!!");
                        break;
                }
                in.close();
                break;
            }
        } catch (IOException e) {
            System.out.println("ClientConnection ERROR: " + e);
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void recieveFile() {
        try {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(socketClient.getInputStream());
            String fileName = clientData.readUTF();
            OutputStream outputStream = new FileOutputStream((Helpers.getCurrentDateTime() + "_" + fileName));
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            outputStream.close();
            clientData.close();
        } catch (IOException e) {
            System.out.println("recieveFile ERROR: " + e);
        }
    }

    public void sendFile(String fileName) {
        try {
            File file = new File(fileName); // filename --> path
            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            dataInputStream.readFully(bytesArray, 0, bytesArray.length);

            // file send over socket
            OutputStream outputStream = socketClient.getOutputStream();

            // Sending file name and file size to the server
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(bytesArray.length);
            dataOutputStream.write(bytesArray, 0, bytesArray.length);

            dataOutputStream.flush();
            System.out.println("File " + fileName + " sent to client");
        } catch (Exception e) {
            System.out.println("SEND FILE ERROR: " + e);
        }
    }


}
