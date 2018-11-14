package server.threads;

import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Socket client;
    private Server server;
    private int playerNumber;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket client, Server server, int playerCount)
    {
        this.client = client;
        this.server = server;
        this.playerNumber = playerCount;

        // Create stream writer / reader
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (client.isConnected())
        {
            String message = listen();

            // A message might be structured like
            // [command] [arg1] [arg2]
            // We will use the command to decide what to do
            String[] message_split = message.split(" ");
            String command = message_split[0];

            // If its player chat, dont ignore
            if (command.equals("chat"))
                System.out.println("Client [" + playerNumber + "]: " + command);

            // Ignore commands if its not this clients turn
            else if (server.getTurn() != playerNumber)
                continue;

            // Follow up on what the client is requesting
            else
                switch (command)
                {
                    case "move":
                        // If its not this clients turn to move
                        // Ignore it
                        if (!(server.getTurn() == playerNumber))
                            break;

                        // The move will be in the form of 'x y'
                        int x = Integer.valueOf(message_split[1]);
                        int y = Integer.valueOf(message_split[2]);
                        server.place(x, y, playerNumber);
                        break;
                }
        }

        // When player leaves the game
        System.out.println("Player " + playerNumber + " has left the game.");
    }

    private String listen()
    {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("Cannot listen to client [" + playerNumber + "].");

            // This is where the client has disconnected
            // We need to remove the client writers and close the connection
            // When they disconnect

            return null;
        }
    }
}
