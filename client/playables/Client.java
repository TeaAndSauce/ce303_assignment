package client.playables;

import client.graphics.Interface;
import client.types.Playable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Playable
{
    public static boolean LOCAL = true;

    private Socket clientsocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private Interface ui;
    private Color color;
    private int player;

    Client()
    {
    }

    public static void main(String[] args) {
        Client c = new Client();

        if (Client.LOCAL)
            c.connect("localhost", 1337);

        // Basic one-way chat system
        while (true)
        {
            // Player needs to request initial game board
            // Player makes a move when its their turn
            //      - Server broadcasts whos turn it is
            // Buttons in the UI lock when its not their turn
            //      - UI shows all possible moves when it is their turn

            Scanner input = new Scanner(System.in);
            System.out.println("Enter a message to send: ");
            String message = input.nextLine();
            if (message.equals("quit"))
            {
                c.sendMessage("quit");
                break;
            }
            else
                c.sendMessage("chat " + message);
        }
    }

    @Override
    public void placeMove(int row, int col) {
        sendMessage("" + row + " " + col);
    }

    @Override
    public int[][] getState() {
        sendMessage("state");
        String response = listen();

        return new int[0][];
    }

    @Override
    public void useFreedomCard() {

    }

    @Override
    public void useReplacementCard() {

    }

    @Override
    public void useDoubleMoveCard() {

    }

    // ------------------------------------------------------------------------
    // CLIENT / SERVER COMMUNICATION
    // ------------------------------------------------------------------------

    @Override
    public void connect(String ip, int port) {
        // Initialise connection
        try {
            clientsocket = new Socket(ip, port);
        } catch (IOException e) {
            System.out.println("Unable to connect to [" + ip + "] at port [" + port + "].");
        }

        // Get Input / Output streams
        try {
            out = new PrintWriter(clientsocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get our player number
        try {
            String playerNumber = in.readLine();
            player = Integer.valueOf(playerNumber.trim());
            System.out.println("You are player " + player + ".");
        } catch (IOException e) {
            System.out.println("Could not set our player number: " + player);
        }

        // Create UI
        ui = new Interface(player, 6, 10, 1000, 500);
        // Placeholders
        ui.place(0, 1, 1);
        ui.place(4, 8, 2);
        ui.place(4, 7, 2);
        ui.place(5, 3, 3);
        ui.place(2, 5, 4);
        ui.place(3, 5, 5);

        // Client is now connected
        connected = true;
    }

    @Override
    public void disconnect() {
        if (connected)
        {
            // Do stuff
            connected = false;
        }
    }

    @Override
    public void sendMessage(String msg) {
        if (connected)
        {
            out.println(msg);
        }
    }

    @Override
    public String listen() {
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.out.println("Could not receive message from server.");
        }
        return message;
    }

    @Override
    public int[][] parseArray(String message) {
        String toParse = message.substring(1, message.length()-1);
        // TODO: Look at Questions.txt
        return new int[0][];
    }
}
