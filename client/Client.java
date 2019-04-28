package client;

import client.graphics.UI;
import shared.Message;

public class Client extends Player
{
    //==============================================================================
    // PROPERTIES
    //==============================================================================

    UI ui;

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    public Client(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        this.activatedCard = 0;
        this.wonOrLost = 0;
    }

    //==============================================================================
    // MAIN PROGRAM
    //==============================================================================

    public static void main(String[] args) {

        // READ COMMAND LINE ARGUMENTS AND CREATE A CLIENT INSTANCE
        // -------------------------------------------------------------------------

        if (args.length != 2)
        {
            System.out.println("Incorrect usage!");
            System.out.println("Usage: java Client.jar [address] [port]");
            System.exit(1);
        }

        // CREATE CLIENT INSTANCE
        // -------------------------------------------------------------------------

        String address = args[0];
        int port = Integer.valueOf(args[1]);
        Client client = new Client(address, port);

        // CONNECT CLIENT TO SERVER
        // -------------------------------------------------------------------------

        client.connect();
        System.out.println("You are player number: " + client.playerNumber);

        // DECIDE WHAT TO DO BASED ON THE MESSAGE CODE RECEIVED FROM THE SERVER
        // -------------------------------------------------------------------------

        System.out.println();
        while(client.getWonOrLost() == 0)
        {
            String message = client.getMessage();
            if (message != null)
            {
                String[] tokens = message.split(" ", 2);
                int code = Integer.valueOf(tokens[0]);

                switch (code)
                {
                    case Message.STATE:
                        // System.out.println("We have a gamestate message!");
                        // System.out.println(tokens[1]);
                        client.gamestate = client.parseGameState(tokens[1]);
                        client.startUI();
                        break;

                    case Message.TURN:
                        client.myTurn = Boolean.valueOf(tokens[1]);
                        // System.out.println("It is " + (client.myTurn ? "your turn!" : "not your turn."));
                        client.ui.updateSidePanel();
                        break;

                    case Message.MOVE:
                        // Update UI with received move
                        System.out.println(tokens[1]);
                        String[] vals = tokens[1].split(" ");
                        int row = Integer.valueOf(vals[0]);
                        int col = Integer.valueOf(vals[1]);
                        int player = Integer.valueOf(vals[2]);
                        client.addMoveToUI(row, col, player);
                        break;

                    case Message.CHAT:
                        System.out.println(tokens[1]);
                        break;

                    case Message.BLOCKED:
                        System.out.println(tokens[1]);
                        client.setBlocked(true);
                        client.setWonOrLost(2);
                        client.showLossScreen();
                        client.ui.updateSidePanel();
                        break;

                    case Message.CARDS:
                        client.setCards(tokens[1]);
                        System.out.println("Cards received and set.");
                        break;

                    case Message.GAME_OVER:
                        System.out.println(tokens[1]);
                        client.setWonOrLost(Integer.valueOf(tokens[1]));
                        client.ui.updateSidePanel();
                        // get the winner
                        break;

                    case Message.SCORES:
                        System.out.println(tokens[1]);
                        break;

                    default:
                        System.out.println("Unrecognised message from server with code: " + code);
                        break;
                }
            }
            else
                client.disconnect();
        }
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    public void startUI()
    {
        // START THE UI FOR THIS CLIENT
        // -------------------------------------------------------------------------

        ui = new UI(this, this.gamestate);
    }

    public void addMoveToUI(int row, int col, int player)
    {
        // ADD A MOVE TO THE UI
        // -------------------------------------------------------------------------

        ui.placeMove(row, col, player);
    }

    public void showLossScreen()
    {
        // FADE THE GAME SCREEN TO BLACK
        // -------------------------------------------------------------------------

        ui.showLossScreen();
    }
}
