package server;

import client.types.Playable;
import server.threads.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server
{
    private int PORT = 1337;
    private ServerSocket serversocket;
    private Socket clientsocket;
    private BufferedReader in;
    private PrintWriter out;
    public boolean online = false;
    private int playersConnected = 0;
    private int spectatorsConnected = 0;
    private int playerTurn = 1;
    private Game game;
    private Map<Integer, PrintWriter> writers;
    private boolean acceptingClients = false;

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
        writers = new HashMap<>();

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

        // Initialise waiting timer for 1 minute
        acceptingClients = true;
        QueueTimer task = new QueueTimer(this);
        Timer timer = new Timer();
        timer.schedule(task, 60000);

        // While we are accepting clients
        while (acceptingClients)
        {
            // Accept connections
            acceptConnections();
        }

        // Once all players have connected, we wish to start a new game
        game.newGame(playersConnected);

        // Perform updating and communication
        // while the game is in progress
        while(game.isRunning())
        {

        }
    }

    // TODO: Test this
    public void sendMessage(int player, String message)
    {
        // Playing with fire here..
        if (writers.get(player).equals(null))
            System.out.println("CANNOT SEND MESSAGE TO NULL SOCKET");
        else
            writers.get(player).println(message);
    }

    // TODO: Test this
    public void broadcast(String message)
    {
        for (PrintWriter writer : writers.values())
            if (!writer.equals(null))
                writer.println(message);
    }

    public void stopAcceptingClients()
    {
        acceptingClients = false;
    }

    public void acceptConnections()
    {
        // Allow client connections
        try {
            clientsocket = serversocket.accept();

            ClientHandler handler = new ClientHandler(clientsocket, this, playersConnected+1);
            Thread test = new Thread(handler);
            test.start();
            System.out.println("Client " + (playersConnected+1) + " has joined the game.");
        } catch (IOException e) {
            System.out.println("Client socket not initialised.");
            return;
        }

        // Initialise stream writer & reader
        try {
            out = new PrintWriter(clientsocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
            // Store this stream writer to a list
            writers.put(playersConnected+1, out);
        } catch (IOException e) {
            System.out.println("Unable to create stream writer/reader for client " + (playersConnected+1));
            return;
        }

        // Send client their player number
        System.out.println("Sending client " + (playersConnected+1) + " their player number.");
        if (!out.equals(null))
            out.println("" + (playersConnected+1));
        playersConnected++;
    }

    public void place(int x, int y, int player)
    {
        game.place(player, x, y, false);
    }

    public String formatGameState(int[][] state)
    {
        String result = "";
        for (int row = 0; row < state.length; row++)
        {
            for (int col = 0; col < state[0].length; col++)
            {
                result += state[row][col] + " ";
            }
            if (row < state.length-1)
                result += "| ";
        }
        return result.trim();
    }

    public void disconnectClient(int player, Socket socket)
    {
        // Close their write stream
        // And set their stream to null
        writers.get(player).close();
        writers.put(player, null);


        // Remove players moves from the game
        game.removePlayer(player);
        // Which should set all their tiles to have the value of 0 (neutral)

        // Send the game to all existing clients
        broadcast(formatGameState(game.getState()));

        // We do not need to update the player's number, as all client connections
        // will be denied if the game has already started
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        for (PrintWriter writer : writers.values())
            writer.close();
        clientsocket.close();
        serversocket.close();
    }
}

// Closing streams and setting writers to null
//  make this a safe operation