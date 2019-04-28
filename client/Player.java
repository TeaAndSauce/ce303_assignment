package client;

import shared.Cards;
import shared.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Player
{
    //==============================================================================
    // PROPERTIES
    //==============================================================================

    public static final int LOST = 2;
    public static final int WON = 1;

    protected Socket server;
    protected PrintWriter out;
    protected BufferedReader in;
    protected int activatedCard, port, playerNumber;
    protected boolean connected, myTurn, blocked;
    protected String ip;
    protected Cards cards;
    protected int[][] gamestate;
    protected int wonOrLost;

    //==============================================================================
    // DEFAULT METHODS
    //==============================================================================

    public int[][] parseGameState(String state)
    {
        // GET NUMBER OF ROWS AND COLUMNS
        // -------------------------------------------------------------------------
        // For some reason .split(".") is not working and so i have to improvise

        int nrows = (state.length() - new String(state).replace(".", "").length()) + 1;
        int ncols = state.substring(0, state.indexOf(".")).length();

        // REMOVE ALL DELIMITERS FROM THE STRING
        // -------------------------------------------------------------------------

        state = state.replace(".", "");
        int[][] result = new int[nrows][ncols];

        // INSERT NUMBERS INTO STATE ARRAY
        // -------------------------------------------------------------------------

        int crow = 0;
        for (int i = 0; i < state.length(); i++)
        {
            if (i % ncols == 0 & i != 0)
                crow++;
            result[crow][i % ncols] = Integer.valueOf(String.valueOf(state.charAt(i)));
        }
        return result;
    }

    public void connect()
    {
        // INITIALISE SERVER CONNECTION
        // -------------------------------------------------------------------------

        try {
            System.out.println("Connecting to server..");
            server = new Socket(ip, port);
        } catch (IOException e) {
            System.out.println("Could not connect to server.");
            disconnect();
            return;
        }

        // INITIALISE INPUT/OUTPUT STREAM READER/WRITER
        // -------------------------------------------------------------------------

        try {
            System.out.println("Initialising streams..");
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Could not initialise streams.");
            disconnect();
            return;
        }

        System.out.println("You have successfully connected to the server.");
        connected = true;

        // FETCH PLAYER NUMBER
        // -------------------------------------------------------------------------

        cards = new Cards();
        sendMessage(Message.PLAYERNUMBER, "request");
        sendMessage(Message.CARDS, "request");
        playerNumber = Integer.valueOf(getMessage());
        activatedCard = 0;
    }

    public void placeMove(int row, int col)
    {
        // SEND THE MOVE TO SERVER AND SET ACTIVATED CARD TO NONE
        // -------------------------------------------------------------------------

        sendMessage(Message.MOVE, "" + row + " " + col + " " + playerNumber + " " + activatedCard);
        activatedCard = 0;
    }

    public void useCard(int card)
    {
        // ACTIVATE A CARD IF ONE ISN'T ALREADY ACTIVATED
        // -------------------------------------------------------------------------

        if (activatedCard == 0)
        {
            if (card != 0)
                if (hasCard(card))
                    cards.useCard(card);
            this.activatedCard = card;
            System.out.println("You have activated the " + Cards.getCardName(card) + " card.");
        }
    }

    public void sendMessage(int code, String message)
    {
        // SEND MESSAGE TO SERVER WITH SPECIFIC CODE
        // -------------------------------------------------------------------------

        if (out != null)
            out.println(code + " " + message);
        else
            System.out.println("Output stream is null!");
    }

    public String getMessage()
    {
        // GET MOST RECENT MESSAGE FROM THE SERVER
        // -------------------------------------------------------------------------

        String message = null;
        if (connected)
        {
            try {
                message = in.readLine();
            } catch (IOException e) {
                System.out.println("Server has stopped responding.. Disconnecting.");
                disconnect();
                return null;
            }
        }
        return message;
    }

    public void disconnect()
    {
        // CLOSE CONNECTIONS TO THE SERVER
        // -------------------------------------------------------------------------

        try {
            in.close();
            out.close();
            server.close();
            connected = false;
        } catch (IOException e) {
            System.out.println("Could not disconnect player for unknown reason. (?)");
        }
    }

    public boolean hasCard(int card)
    {
        // CHECKS LOCAL CARD STORAGE TO SEE IF WE HAVE A CARD
        // -------------------------------------------------------------------------

        if(cards != null)
            return cards.hasCard(card);
        return false;
    }

    public void getCards()                  { sendMessage(Message.CARDS, "request"); }
    public Cards myCards()                  { return cards; }
    public void setCards(String cards)      { this.cards.setCards(cards); }
    public int getPlayerNumber()            { return playerNumber; }
    public boolean isMyTurn()               { return myTurn; }
    public void setActivatedCard(int card)  { this.activatedCard = card; }
    public int getActivatedCard()           { return activatedCard; }
    public void setBlocked(boolean b)       { this.blocked = b; }
    public boolean getBlocked()             { return blocked; }
    public void setWonOrLost(int wol)       { wonOrLost = wol; }
    public int getWonOrLost()               { return wonOrLost; }
}
