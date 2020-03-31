
import java.io.*;
import java.net.Socket;

public class FileClient
{
    private final static String ADDRESS1 = "localhost";
    private final static String ADDRESS2 = "192.168.1.3";
    private final static int PORT = 61174;

    private Socket connection;

    private OutputStream outputStream;
    private InputStream inputStream;

    public FileClient(String fileName)
    {
        try
        {
            connection = new Socket(ADDRESS2, PORT);

            outputStream = connection.getOutputStream();
            inputStream = connection.getInputStream();

            send(fileName);
            receive(fileName + "_out." + fileName.split("\\.")[1]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void send(String fileName)
    {
        try
        {
            outputStream.write((fileName).getBytes());
            outputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void receive(String fileName)
    {
        byte [] buffer = new byte[1024];
        int bytesRead;

        File file = new File(fileName);

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            /* Reads and write the byte stream from the client onto the server disk */
            do {
                bytesRead = inputStream.read(buffer);

                if (bytesRead <= 0)
                    break;

                if(new String(buffer).startsWith("Error") )
                {
                    System.out.println(new String(buffer).trim());
                    break;
                }

                bufferedOutputStream.write(buffer, 0, bytesRead);
                bufferedOutputStream.flush();
            }
            while (true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String [] args)
    {
        new FileClient(args[0]);
    }
}
