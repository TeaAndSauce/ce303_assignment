package client.graphics;

import javax.swing.*;
import java.awt.*;

public class Interface
{
    private JFrame frame;
    private GamePanel game;
    private SidePanel sidepanel;

    public Interface(int rows, int cols, int width, int height)
    {
        // Scale size
        int gamepanelwidth = (int) (width*0.2)*4;
        int sidepanelwidth = width-gamepanelwidth;

        // Initialisation
        frame = new JFrame("Multiplayer Game");
        game = new GamePanel(rows, cols, gamepanelwidth, height);
        sidepanel = new SidePanel(Colors.BLANK);

        // Assign location
        frame.add(game, BorderLayout.CENTER);
        frame.add(sidepanel, BorderLayout.EAST);

        // Side panel sizes
        sidepanel.setPreferredSize(new Dimension(sidepanelwidth, height));

        // Pack and display
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public void place(int row, int col, int player)
    {
        game.place(row, col, player);
    }
}