package server;

import client.types.Playable;
import server.threads.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private int playerTurn = 1;
    private Game game;
    private List<PrintWriter> writers;

    // Only a single server should run
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public int getTurn()
    {
        return playerTurn;
    }

    Server()
    {
    }

    public void start()
    {
        // Init game
        game = new Game();

        // Init writers list
        writers = new ArrayList<>();

        // Attempt to create server socket
        try {
            serversocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Server unable to start up on port [" + PORT + "].");
            System.out.println("Server closing down..");
            return;
        }

        // Server is now online
        online = true;

        while (online)
        {
            // Allow client connections
            try {
                clientsocket = serversocket.accept();

                // client should send a message straight away to identify
                // If it is a client, bot or spectator

                ClientHandler handler = new ClientHandler(clientsocket, this, playersConnected+1);
                Thread test = new Thread(handler);
                test.start();
                System.out.println("Client " + (playersConnected+1) + " has joined the game.");
            } catch (IOException e) {
                System.out.println("Client socket not initialised.");
                continue;
            }

            // Initialise stream writer & reader
            try {
                out = new PrintWriter(clientsocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
                writers.add(out);
            } catch (IOException e) {
                System.out.println("Unable to create stream writer/reader for client " + (playersConnected+1));
                continue;
            }
            playersConnected++;
        }
    }

    // TODO: Test this
    public void sendMessage(int player, String message)
    {
        // Playing with fire here..
        if (writers.get(player-1).equals(null))
            System.out.println("CANNOT SEND MESSAGE TO NULL SOCKET");
        else
            writers.get(player-1).println(message);
    }

    // TODO: Test this
    public void broadcast(String message)
    {
        for (PrintWriter writer : writers)
            if (!writer.equals(null))
                writer.println(message);
    }

    public void place(int x, int y, int player)
    {
        game.place(player, x, y, false);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        for (PrintWriter writer : writers)
            writer.close();
        clientsocket.close();
        serversocket.close();
    }
}
