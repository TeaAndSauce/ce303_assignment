package server;

import shared.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    //==============================================================================
    // PROPERTIES
    //==============================================================================

    private int playerNumber;
    private Server server;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private boolean blocked;

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    public ClientHandler(Socket client, Server server)
    {
        this.client = client;
        this.server = server;
        this.connected = true;
        this.blocked = false;

        // CREATE INPUT/OUTPUT STREAM READERS/PRINTERS
        // -------------------------------------------------------------------------

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Cannot create client stream operators.");
        }
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    private String getMessage()
    {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("ClientHandler: Cannot read client input stream!");
            server.disconnectPlayer(this);
            return null;
        }
    }

    public void sendMessage(int code, String message)
    {
        // SENDS A MESSAGE TO THE CLIENT WITH THE GIVEN CODE
        // -------------------------------------------------------------------------

        if (connected)
            out.println(code + " " + message);
    }

    public void disconnect()
    {
        // CLOSE STREAMS AND SET CONNECTED TO FALSE
        // -------------------------------------------------------------------------

        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.out.println("Unable to disconnect player. (?)");
        }
        connected = false;
    }

    public Socket getSocket()
    {
        // RETURNS THE SOCKET OF THIS CLIENT
        // -------------------------------------------------------------------------

        return client;
    }

    public int getPlayerNumber()
    {
        // RETURNS THIS CLIENTS PLAYER NUMBER
        // -------------------------------------------------------------------------

        return playerNumber;
    }

    public void setPlayerNumber(int n)
    {
        // SETS THIS CLIENTS PLAYER NUMBER TO BE N
        // -------------------------------------------------------------------------

        this.playerNumber = n;
    }

    public void setPlayerBlocked()
    {
        // MARK THIS CLIENT AS BLOCKED
        // -------------------------------------------------------------------------

        blocked = true;
        sendMessage(Message.BLOCKED, "You have been blocked.");
        server.disconnectPlayer(this);
    }

    public boolean isBlocked()
    {
        // CHECKS IF THIS PLAYER IS MARKED AS BLOCKED
        // -------------------------------------------------------------------------

        return blocked;
    }

    public boolean isConnected()
    {
        return connected;
    }

    //==============================================================================
    // OVERRIDES
    //==============================================================================

    @Override
    public void run() {
        while (connected & !server.isGameOver())
        {
            // READ MESSAGES FROM THE CLIENT
            // ---------------------------------------------------------------------

            String message = getMessage();
            String[] tokens = message.split(" ", 2);
            int code = Integer.valueOf(tokens[0]);

            // DECIDE WHAT TO DO WITH THE MESSAGE BASED ON MESSAGE CODE
            // ---------------------------------------------------------------------

            switch (code)
            {
                case Message.CHAT:
                    System.out.println("Player " + playerNumber + ": " + tokens[1]);
                    break;

                case Message.MOVE:
                    System.out.println("Player " + playerNumber + " requested to move to " + tokens[1]);
                    if (playerNumber-1 != (server.getTurn() % server.getPlayers()))
                        break;
                    String[] vals = tokens[1].split(" ");
                    int row = Integer.valueOf(vals[0]);
                    int col = Integer.valueOf(vals[1]);
                    int player = Integer.valueOf(vals[2]);
                    int card = Integer.valueOf(vals[3]);

                    // If the player has the card used
                    if (server.hasCard(playerNumber, card))
                        server.placeMove(row, col, player, card);
                    break;

                case Message.CARDS:
                    System.out.println("Player " + playerNumber + " requested their cards.");
                    sendMessage(Message.CARDS, server.getCards(playerNumber));
                    break;

                case Message.STATE:
                    System.out.println("Player " + playerNumber + " requested the game state.");
                    break;

                case Message.PLAYERNUMBER:
                    out.println(playerNumber);
                    System.out.println("Player " + playerNumber + " requested their player number.");
                    break;

                default:
                    System.out.println("Message not recognised.");
                    break;
            }
        }

        // IF WE GET HERE, THE GAME HAS ENDED FOR THIS CLIENT
        // -------------------------------------------------------------------------

        sendMessage(Message.GAME_OVER, "Game over!");
        server.disconnectPlayer(this);
    }
}
