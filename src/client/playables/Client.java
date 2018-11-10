package client.playables;

import client.types.Playable;

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

    public static void main(String[] args) {
        Client c = new Client();
        if (Client.LOCAL)
            c.connect("localhost", 1337);

        // Basic one-way chat system
        while (true)
        {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter a message to send: ");
            String message = input.nextLine();
            if (message.equals("quit"))
            {
                c.sendMessage("quit");
                break;
            }
            else
                c.sendMessage(message);
        }
    }

    @Override
    public void placeMove(int row, int col) {

    }

    @Override
    public int[][] getState() {
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
}
