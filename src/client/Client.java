package client;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    /**
     * Run on 10.102.128.25 (netprog2.csit.rmit.edu.au)
     */
    private Socket socket;

    public Client() {
        try {
            init();
        } catch (IOException e) {
            System.out.println("Cannot connect to server.");
        }
        action();
    }

    private void init() throws IOException {
        String ADDRESS = "10.102.128.22"; // server is running on 10.102.128.22
        int PORT = 61177;
        this.socket = new Socket(ADDRESS, PORT);
    }

    private void action() {
        String response;
        String req;
        boolean flag = true;
        Scanner scanner = new Scanner(System.in);
        while (flag) {
            try {
                System.out.println(
                        "[0] Download\n" +
                        "[1] Send\n" +
                        "[2] Exit\n" +
                        "Input your choice number:");
                req = scanner.nextLine();
                // send("MESSAGE THINGSYOUWANTTOSAY")
                switch (req) {
                    case "0":
                        System.out.println("Input the file name to download:");
                        String fileName = scanner.nextLine();
                        send("DOWNLOAD " + fileName);
                        response = read(); // read server response
                        if (response.toUpperCase().contains("NOTFOUND ")) {
                            System.out.println("Not such file on the server.");
                        } else {
                            receiveFile(fileName);
                        }
                        break;
                    case "1":
                        System.out.println("Write the message to send:");
                        send("MESSAGE " + scanner.nextLine());
                        response = read();
                        System.out.println(response);
                        break;
                    case "2":
                        System.out.println("Exit.");
                        send("EXIT ");
                        closeConnection();
                        flag = false;
                        break;
                    default:
                        System.out.println("Please input menu option number.");
                }
            } catch (IOException e) {
                System.out.println("Error: connection lost.");
            }
        }
    }

    private void receiveFile(String fileName) {
        try {
            // handle the data stream for server.
            System.out.println("Start downloading...");
            int bytesRead;
            InputStream in = socket.getInputStream();
            DataInputStream clientData = new DataInputStream(in);
            try {
                // here got the download file name.
                fileName = clientData.readUTF();
                OutputStream output = new FileOutputStream(
                        "download_by_" + socket.getLocalPort()+"_"+
                                fileName);
                long size = clientData.readLong();
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(
                        buffer,
                        0,
                        (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }
                output.close();
                //in.close();
            } catch (SocketException se) {
                System.err.println("Connection lost.");
                return;
            }
            System.out.println("File = (" + fileName + ") downloaded.");
        } catch (IOException ex) {
            System.err.println("File not found.");
        }
    }

    private void closeConnection() {
        try {
            this.socket.close();
            System.out.println("Connection ended by client side.");
        } catch (IOException e) {

        }
    }

    private String read() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return in.readLine();
    }

    private void send(String message) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        out.println(message);
        out.flush();
    }

    public static void main(String[] args) {
        new Client();
    }
}
