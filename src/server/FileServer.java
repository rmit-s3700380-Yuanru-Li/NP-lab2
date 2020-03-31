
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer
{
    private final static int PORT = 61174;

    private Socket connection;

    private InputStream inputStream;
    private OutputStream outputStream;

    private String request;

    public FileServer()
    {
        ServerSocket server = null;

        try
        {
            server = new ServerSocket(PORT);
            System.out.println("Starts the server");
            System.out.println(server.getInetAddress());
            System.out.println(server.getInetAddress().getLocalHost().getHostAddress());
            while (true)
            {
                System.out.println("Waiting for new connections...");
                connection = server.accept();
                System.out.println("Accepted");

                outputStream = connection.getOutputStream();
                outputStream.flush();
                inputStream = connection.getInputStream();


                request = readFromClient(inputStream);
                System.out.println("Request: " + request);
                byte [] buffer = readFromDisk(request);
                writeToClient(buffer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                server.close();
            }
            catch (IOException e) { }
        }
    }

    private String readFromClient(InputStream inputStream)
    {
        int character = 0;
        StringBuffer stringBuffer = new StringBuffer();
        byte [] buffer = new byte[1024];

        try
        {
            inputStream.read(buffer);

            for(int i=0; i < 1024; i++) {
                if(buffer[i] == 0)
                    break;

                stringBuffer.append((char)buffer[i]);
            }
            System.out.println("finished reading");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    private byte [] readFromDisk(String request)
    {
        File file = new File(request);
        BufferedInputStream bufferedInputStream = null;

        byte [] buffer = new byte[(int)file.length()];

        try
        {
            FileInputStream fileInputStream = new FileInputStream(request);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            bufferedInputStream.read(buffer,0 , buffer.length);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error, file not found : " + request);
            buffer = "Error, Server couldn't find your file".getBytes();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (bufferedInputStream != null) bufferedInputStream.close();
            }
            catch (IOException e)
            {

            }
        }

        return buffer;
    }

    private void writeToClient(byte [] buffer)
    {
        try
        {
            outputStream.write(buffer); // Writes the complete data to the server
            outputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if(outputStream != null) outputStream.close();
                if(inputStream != null) inputStream.close();
                if(connection != null) connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args)
    {
        new FileServer();
    }
}
