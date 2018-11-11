package server;

import client.types.Playable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private int PORT = 1337;
    private Playable[] players;
    private ServerSocket serversocket;
    private Socket clientsocket;
    private BufferedReader in;
    private PrintWriter out;
    public boolean online = false;
    private int playersConnected = 0;
    private int spectatorsConnected = 0;
    private int botsConnected = 0;

    // Only a single server should run
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        // Constantly listen for messages until client
        // sends a quit message
        while (server.online)
        {
            String received = server.listen();
            if (received.equals("quit"))
                break;
            System.out.println(received);
        }
    }

    Server()
    {
    }

    public void start()
    {
        // Attempt to create server socket
        try {
            serversocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Server unable to start up on port [" + PORT + "].");
            System.out.println("Server closing down..");
            return;
        }

        // Allow client connections
        try {
            clientsocket = serversocket.accept();
        } catch (IOException e) {
            System.out.println("Client socket not initialised.");
            System.out.println("Server closing down..");
            return;
        }

        // Initialise stream writer & reader
        try {
            out = new PrintWriter(clientsocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to create stream writer/reader");
            System.out.println("Server closing down..");
            return;
        }

        // Server is now online
        online = true;
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientsocket.close();
        serversocket.close();
    }

    public String listen()
    {
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.out.println("Client has stopped responding.");
        }
        return message;
    }
}
