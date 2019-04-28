package client.graphics;

import client.Client;
import client.Player;
import shared.Cards;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class UI
{
    //==============================================================================
    // PROPERTIES
    //==============================================================================

    Client client;
    GamePanel game;
    SidePanel side;
    JFrame frame;
    Map<Integer, Color> playerColours;
    int[][] gamestate;

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    public UI(Client client, int[][] gamestate)
    {
        this.client = client;
        this.gamestate = gamestate;

        // INITIALISE COLOUR MAP FOR PLAYERS AND SET VALUES
        // -------------------------------------------------------------------------

        playerColours = new HashMap<>();
        playerColours.put(0, Colors.BLANK);
        playerColours.put(1, Colors.RED);
        playerColours.put(2, Colors.BLUE);
        playerColours.put(3, Colors.GREEN);
        playerColours.put(4, Colors.YELLOW);
        playerColours.put(5, Colors.PURPLE);

        // CREATE COMPONENTS
        // -------------------------------------------------------------------------

        createComponents(1000, 500);
    }

    //==============================================================================
    // MAIN METHOD TO TEST FUNCTIONALITY
    //==============================================================================

    public static void main(String[] args) {

        int[][] gamestate = {
            {2,0,1,0,1,0,2,0,2,0},
            {0,1,1,1,1,1,2,0,2,0},
            {2,0,1,1,1,0,2,0,2,0},
            {2,0,0,1,0,0,2,2,2,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
        };

        Client c = new Client("localhost", 9001);
        UI ui = new UI(c, gamestate);
        ui.createComponents(1000, 500);
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    private void createComponents(int width, int height)
    {
        // INITIALISE FRAME AND SET SIZES
        // -------------------------------------------------------------------------

        frame = new JFrame("CE303 - Multiplayer Game");
        frame.setPreferredSize(new Dimension(width, height));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // CREATE GAME PANEL
        // -------------------------------------------------------------------------

        game = new GamePanel((int)(width * 0.8), height, 6, 10);

        // CREATE SIDE PANEL
        // -------------------------------------------------------------------------

        side = new SidePanel((int)(width * 0.2), height);
        side.update();

        // ADD KEY LISTENER
        // -------------------------------------------------------------------------

        frame.addKeyListener(new GameKeys(game));

        // ADD COMPONENTS AND DISPLAY WINDOW
        // -------------------------------------------------------------------------

        frame.add(game, BorderLayout.CENTER);
        frame.add(side, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }

    public void placeMove(int row, int col, int player)
    {
        // PLACES A MOVE ONTO THE UI
        // -------------------------------------------------------------------------

        gamestate[row][col] = player;
        frame.repaint();
        game.repaint();
        side.repaint();
    }

    public void updateSidePanel()
    {
        // UPDATES THE SIDE PANELS TEXT AREA
        // -------------------------------------------------------------------------

        side.update();
    }

    public void showLossScreen()
    {
        // FADES GAME SCREEN TO BLACK
        // -------------------------------------------------------------------------

        game.showLossScreen();
    }

    //==============================================================================
    // SIDE PANEL CLASS
    //==============================================================================

    class SidePanel extends JPanel
    {
        int width, height;
        JTextArea logger;

        SidePanel(int width, int height)
        {
            this.width = width;
            this.height = height;
            this.logger = new JTextArea();

            // SET PANEL SIZES
            // ---------------------------------------------------------------------

            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));

            // SET LOGGER SIZES & BACKGROUND COLOUR
            // ---------------------------------------------------------------------

            this.logger.setBackground(Colors.BACKGROUND);
            this.logger.setPreferredSize(new Dimension(width-30, height));
            this.logger.setMaximumSize(new Dimension(width-30, height));
            this.logger.setMinimumSize(new Dimension(width-30, height));
            this.logger.setFocusable(false);
            this.logger.setForeground(playerColours.get(client.getPlayerNumber()));
            add(logger);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.setColor(Colors.BACKGROUND_SIDE);
            g.fillRect(0, 0, width, height);
            g.setColor(Colors.BACKGROUND);
            g.fillRect(2, 2, width-4, height-4);
        }

        public void writeControls()
        {
            // WRITES CONTROLS ONTO SIDE PANEL
            // -------------------------------------------------------------------------

            String[] controls = {
                    "----------------------------------",
                    "- CONTROLS",
                    "----------------------------------",
                    "Arrow UP: Move up",
                    "Arrow Down: Move down",
                    "Arrow Left: Move left",
                    "Arrow Right: Move right",
                    "",
                    "----------------------------------",
                    "- CARDS",
                    "----------------------------------",
                    "F: Freedom Card",
                    "R: Replace Card",
                    "D: Double Move Card",
                    ""
            };
            for (String line : controls)
            {
                logger.append(line + "\n");
            }
        }

        public void writeKnownBugs()
        {
            // WRITES BUGS ONTO SIDE PANEL
            // -------------------------------------------------------------------------

            String[] bugs = {
                    "----------------------------------",
                    "- BUGS",
                    "----------------------------------",
                    "1. Game freezes after",
                    "a player has been wiped out."
            };
            for (String line : bugs)
            {
                logger.append(line + "\n");
            }
        }

        public void writeYourTurn()
        {
            // WRITE IF ITS OUR TURN OR NOT ONTO SIDE PANEL
            // -------------------------------------------------------------------------

            if (client.isMyTurn() & !client.getBlocked())
            {
                logger.append("\n----------------------------------\n");
                logger.append("IT IS YOUR TURN!\n");
                logger.append("----------------------------------");
            }
            else if (!client.isMyTurn() & !client.getBlocked())
            {
                logger.append("\n----------------------------------\n");
                logger.append("WAIT FOR YOUR TURN.\n");
                logger.append("----------------------------------");
            }
            writeGameOver();
        }

        public void writeGameOver()
        {
            // WRITE IF THE PLAYER WON OR LOST
            // -------------------------------------------------------------------------

            if (client.getWonOrLost() == Player.LOST)
            {
                logger.append("\n----------------------------------\n");
                logger.append("UNFORTUNATELY..\nYou were wiped out!\n");
                logger.append("----------------------------------");
            }
            else if (client.getWonOrLost() == Player.WON)
            {
                logger.append("\n----------------------------------\n");
                logger.append("CONGRATULATIONS..\nYou won!\n");
                logger.append("----------------------------------");
            }
        }

        public void update()
        {
            // UPDATE THE SIDE PANELS TEXT AREA
            // -------------------------------------------------------------------------

            logger.setText("");
            writeControls();
            writeKnownBugs();
            writeYourTurn();
        }
    }

    //==============================================================================
    // GAME PANEL CLASS
    //==============================================================================

    class GamePanel extends JPanel
    {
        int width, height, nrows, ncols, sqw, sqh, gap, hcol, hrow;
        int animalpha = 0;

        GamePanel(int width, int height, int rows, int cols)
        {
            // SET NUMBER OF ROWS AND COLUMNS TO DISPLAY AND SET SQUARE SIZES
            // ---------------------------------------------------------------------

            this.width = width;
            this.height = height-26;
            this.nrows = rows;
            this.ncols = cols;
            this.sqw = this.width / cols;
            this.sqh = this.height / rows;
            this.gap = 2;

            // SET PANEL SIZES
            // ---------------------------------------------------------------------

            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
        }

        public void moveUp()
        {
            // MOVE TARGETER UP
            // -------------------------------------------------------------------------

            this.hrow = hrow < 1 ? nrows-1 : hrow-1;
            repaint();
        }

        public void moveDown()
        {
            // MOVE TARGETER DOWN
            // -------------------------------------------------------------------------

            this.hrow = hrow > nrows-2 ? 0 : hrow+1;
            repaint();
        }

        public void moveLeft()
        {
            // MOVE TARGETER LEFT
            // -------------------------------------------------------------------------

            this.hcol = hcol < 1 ? ncols-1 : hcol-1;
            repaint();
        }

        public void moveRight()
        {
            // MOVE TARGETER RIGHT
            // -------------------------------------------------------------------------

            this.hcol = hcol > ncols-2 ? 0 : hcol+1;
            repaint();
        }

        public void showLossScreen()
        {
            // FADE GAME SCREEN TO BLACK
            // -------------------------------------------------------------------------

            while (animalpha < 255)
            {
                animalpha += 5;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            // SET BACKGROUND COLOUR
            // ---------------------------------------------------------------------

            g.setColor(Colors.BACKGROUND);
            g.fillRect(0, 0, this.width, this.height);

            // DRAW ALL SQUARES WITH COLOURS CORRESPONDING TO TILE OWNER
            // -------------------------------------------------------------------------

            for (int row = 0; row < nrows; row++)
            {
                for ( int col = 0;col < ncols; col++)
                {
                    g.setColor(playerColours.get(gamestate[row][col]));
                    g.fillRoundRect(col*sqw+gap, row*sqh+gap, sqw-gap, sqh-gap, 4, 4);

                    if (row == hrow & col == hcol & client.isMyTurn())
                    {
                        // Draw hover
                        Color mycol = playerColours.get(client.getPlayerNumber());
                        g.setColor(new Color(mycol.getRed(), mycol.getGreen(), mycol.getBlue(), 100));
                        g.fillRoundRect(col*sqw+gap, row*sqh+gap, sqw-gap, sqh-gap, 4, 4);
                    }
                    if (client.getWonOrLost() == Player.LOST | client.getBlocked())
                    {
                        g.setColor(new Color(0, 0, 0, animalpha));
                        g.fillRect(0, 0, width, height);
                    }
                }
            }
        }
    }

    //==============================================================================
    // KEY LISTENER CLASS
    //==============================================================================

    class GameKeys implements KeyListener
    {
        GamePanel game;

        GameKeys(GamePanel game)
        {
            this.game = game;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        // PERFORM ACTIONS WHEN A KEY IS PRESSED SUCH AS USE A CARD OR MOVE TARGETER
        // -------------------------------------------------------------------------

        @Override
        public void keyPressed(KeyEvent e) {
            if (client.isMyTurn())
            {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.moveUp();
                        break;

                    case KeyEvent.VK_DOWN:
                        game.moveDown();
                        break;

                    case KeyEvent.VK_LEFT:
                        game.moveLeft();
                        break;

                    case KeyEvent.VK_RIGHT:
                        game.moveRight();
                        break;

                    case KeyEvent.VK_F:
                        System.out.println("Used freedom card..");
                        if (client.hasCard(Cards.FREEDOM))
                            client.useCard(Cards.FREEDOM);
                        else
                        {
                            System.out.println("You do not have this card.");
                            client.setActivatedCard(0);
                        }
                        break;

                    case KeyEvent.VK_D:
                        System.out.println("Used double move card..");
                        if (client.hasCard(Cards.DOUBLE))
                            client.useCard(Cards.DOUBLE);
                        else
                        {
                            System.out.println("You do not have this card.");
                            client.setActivatedCard(0);
                        }
                        break;

                    case KeyEvent.VK_R:
                        System.out.println("Used replacement card..");
                        if (client.hasCard(Cards.REPLACE))
                            client.useCard(Cards.REPLACE);
                        else
                        {
                            System.out.println("You do not have this card.");
                            client.setActivatedCard(0);
                        }
                        System.out.println(client.myCards());
                        break;

                    case KeyEvent.VK_ENTER:
                        // System.out.println("Placed a move at row=" + game.hrow + ", col=" + game.hcol);
                        client.placeMove(game.hrow, game.hcol);
                        break;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}
