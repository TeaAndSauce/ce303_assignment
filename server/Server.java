package server;

import shared.Cards;
import shared.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class Server
{
    //==============================================================================
    // PROPERTIES
    //==============================================================================

    public static int PLAYERLIMIT = 5;

    private int players, maxPlayers, port, spectators;
    private ServerSocket server;
    private ClientHandler[] clients;
    private Game game;
    private boolean online;
    private boolean started;
    private int turn;
    private boolean gameover;

    //==============================================================================
    // MAIN PROGRAM
    //==============================================================================

    public static void main(String[] args)
    {
        // READ COMMAND LINE ARGUMENTS AND START SERVER
        // -------------------------------------------------------------------------

        if (args.length != 3)
        {
            System.out.println("Incorrect arguments!");
            System.out.println("Usage: java Server.jar [nPlayers] [nBots] [port]");
            System.exit(1);
        }
        int players = Integer.valueOf(args[0]);
        int bots = Integer.valueOf(args[1]);
        int port = Integer.valueOf(args[2]);

        if (players+bots > Server.PLAYERLIMIT)
        {
            System.out.println("A maximum of 5 players is allowed.");
        }
        else
            Server.PLAYERLIMIT = (players+bots);
        Server server = new Server(players, bots, port);
        server.start();
        server.acceptClients();
    }

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    Server(int players, int bots, int port) throws InvalidPlayerAmountsException
    {
        this.maxPlayers = 5;

        // PLAYER AMOUNT CHECKER
        // -------------------------------------------------------------------------

        if (players + bots > maxPlayers)
            throw new InvalidPlayerAmountsException("Cannot create server with " + (players + bots) + " players!");
        else
            System.out.println("Server created for " + players + " players and " + bots + " bots.");

        // INITIALISE SERVER PROPERTIES
        // -------------------------------------------------------------------------

        this.port = port;
        this.players = 0;
        this.spectators = 0;
        this.game = new Game(this);
        this.clients = new ClientHandler[5];
        this.online = false;
        this.started = false;
        this.turn = 0;
        this.gameover = false;
    }

    //==============================================================================
    // STATIC METHODS
    //==============================================================================

    public static String formatGameState(int[][] state)
    {
        String result = "";
        for (int row = 0; row < state.length; row++)
        {
            for (int col = 0; col < state[0].length; col++)
            {
                result = result + state[row][col];
            }
            if (row < state.length-1)
                result += ".";
        }
        return result.trim();
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    public void start()
    {
        // INITIALISE SERVER SOCKET
        // -------------------------------------------------------------------------

        try {
            server = new ServerSocket(port);
            online = true;
        } catch (IOException e) {
            System.out.println("Unable to create server on port " + port);
        }
    }

    public void acceptClients()
    {
        while (online & !gameover)
        {
            acceptAndPushClient();
        }
        System.out.println("GAME OVER!");
    }

    public void sendPlayerTurn()
    {
        // DECIDE WHOS TURN IT IS
        // -------------------------------------------------------------------------

        for (ClientHandler client : clients)
        {
            if (client != null)
            {
                // If its this players turn and they are not blocked
                if (client.getPlayerNumber()-1 == turn % players & !client.isBlocked())
                {
                    client.sendMessage(Message.TURN, "true");
                }
                // If the player is blocked, skip them and disconnect them
                else if (client.isBlocked())
                {
                    client.sendMessage(Message.TURN, "false");
                    client.sendMessage(Message.SCORES, "" + game.getScore(client.getPlayerNumber()));
                    disconnectPlayer(client);
                    pushCounterToNextFreeClient();
                }
                // Otherwise, it is simply just not their turn
                else
                {
                    client.sendMessage(Message.TURN, "false");
                }
            }
        }
    }

    public void pushCounterToNextFreeClient()
    {
        boolean set = false;
        for (int i = 0; i < clients.length-1; i++)
        {
            ClientHandler client = clients[turn % players];
            if (client.isBlocked())
                turn++;
            else
            {
                set = true;
            }
        }
        if (set)
        {
            System.out.println("Next person to move is player " + (turn % players + 1));
        }
        else
        {
            System.out.println("No players can make a move, game is ending");
            setGameOver();
        }
    }

    private void acceptAndPushClient()
    {
        // ACCEPT THE CLIENT AND CREATE NEW CLIENT HANDLER & SET PLAYER NUMBER
        // -------------------------------------------------------------------------

        try {
            ClientHandler client = new ClientHandler(server.accept(), this);
            System.out.println("Accepted a client! Pushing..");
            Thread t = new Thread(client);
            addPlayer(client);
            t.start();
        } catch (IOException e) {
            System.out.println("Server: Unable to create client handler");
        }

        // START THE GAME WHEN ALL CLIENTS HAVE CONNECTED
        // -------------------------------------------------------------------------

        if (players == PLAYERLIMIT & !started)
        {
            int[][] state = game.newGame(players);
            started = true;
            broadcast(Message.STATE, formatGameState(state));
            sendPlayerTurn();
        }
    }

    public void disconnectPlayer(ClientHandler client)
    {
        // SAFELY DISCONNECT THE CLIENT AND REMOVE THE PLAYER FROM THE PLAYER LIST
        // -------------------------------------------------------------------------

        if (client.isConnected())
        {
            client.disconnect();
            System.out.println("Player " + client.getPlayerNumber() + " has disconnected.");
        }
    }

    private int addPlayer(ClientHandler client)
    {
        // FIND A FREE SLOT IN THE CLIENTS LIST, ADD IF FREE SPOT
        // -------------------------------------------------------------------------

        boolean couldPlace = false;
        for (int i = 0; i < clients.length; i++)
        {
            if (clients[i] == null)
            {
                clients[i] = client;
                client.setPlayerNumber(i+1);
                players++;
                couldPlace = true;
                break;
            }
        }
        if (!couldPlace)
        {
            // IF WE COULD NOT PLACE THE PLAYER, THEN THEY MUST BE A SPECTATOR
            // --------------------------------------------------------------------

            System.out.println("This player could not be placed into the game.");
            System.out.println("They will now become a spectator.");
            spectators++;
        }

        return client.getPlayerNumber()-1;
    }

    public boolean removePlayer(ClientHandler client)
    {
        // REMOVE THE PLAYER FROM THE PLAYER LIST
        // -------------------------------------------------------------------------

        for (int i = 0; i < clients.length; i++)
        {
            if (clients[i].equals(client))
            {
                clients[i] = null;
                return true;
            }
        }
        return false;
    }

    public void broadcast(int code, String message)
    {
        // BROADCAST A MESSAGE TO ALL CLIENTS CONNECTED
        // -------------------------------------------------------------------------

        for (ClientHandler c : clients)
        {
            if (c != null)
            {
                try {
                    PrintWriter writer = new PrintWriter(c.getSocket().getOutputStream(), true);
                    writer.println(code + " " + message);
                } catch (IOException e) {
                    System.out.println("Server: Cannot broadcast to player " + c.getPlayerNumber() + ".");
                }
            }
        }
    }

    public void placeMove(int row, int col, int player, int card)
    {
        // PLACE THE MOVE IF IT IS VALID
        // -------------------------------------------------------------------------

        boolean couldPlace = game.place(row, col, player, card, false);
        // if we could place the move
        if (couldPlace)
        {
            broadcast(Message.MOVE, "" + row + " " + col + " " + player);
            // If the player activated a double move card, allow an extra move
            if (!(card == Cards.DOUBLE))
                turn++;
            // If we blocked any players with this move, we should wipe them out
            wipeOutBlockedPlayers();
        }
        // If we couldnt place it, then they should be told it
        // is still their turn and nothing else should be done
        sendPlayerTurn();
    }

    public int getTurn()
    {
        // GETS THE CURRENT TURN COUNTER
        // -------------------------------------------------------------------------

        return turn;
    }

    public void wipeOutBlockedPlayers()
    {
        // WIPE OUT ALL PLAYERS THAT ARE MARKED AS BLOCKED
        // -------------------------------------------------------------------------

        for (ClientHandler client : clients)
        {
            if (client != null)
            {
                if (game.isPlayerBlocked(client.getPlayerNumber()))
                {
                    client.setPlayerBlocked();
                    disconnectPlayer(client);
                }
            }
        }
    }

    public int getPlayers()
    {
        // GETS THE PLAYER COUNT
        // -------------------------------------------------------------------------

        return players;
    }

    public String getCards(int player)
    {
        // GETS THE GIVEN PLAYERS SET OF CARDS AS A STRING
        // -------------------------------------------------------------------------

        return game.getCards(player);
    }

    public boolean hasCard(int player, int card)
    {
        // CHECKS IF A PLAYER HAS A SPECIFIC CARD AVAILABLE
        // -------------------------------------------------------------------------

        if (card == 0)
            return true;
        return game.hasCard(player, card);
    }

    public void setGameOver()
    {
        // SETS THE GAME TO BE OVER
        // -------------------------------------------------------------------------

        for (ClientHandler client : clients) {
            if (client != null) {
                if (client.getPlayerNumber() == game.getWinner())
                    client.sendMessage(Message.GAME_OVER, "1");
                else
                    client.sendMessage(Message.GAME_OVER, "2");
            }
        }
        gameover = true;
    }

    public boolean isGameOver()
    {
        // CHECKS IF THE GAME IS OVER
        // -------------------------------------------------------------------------

        return gameover;
    }

    //==============================================================================
    // EXCEPTIONS
    //==============================================================================

    /**
     * This exception is thrown if you try to set the player count for this
     * game to be > 5.
     */
    private class InvalidPlayerAmountsException extends RuntimeException
    { InvalidPlayerAmountsException(String message)
        { super(message);
        }
    }
}
