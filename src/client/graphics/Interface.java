package client.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Interface
{
    private JFrame frame;
    private GamePanel game;
    private SidePanel sidepanel;
    private int player;
    private Map<Integer, Color> colors;

    public Interface(int player, int rows, int cols, int width, int height)
    {
        // Player number
        this.player = player;

        // Init color map
        colors = new HashMap<>();
        colors.put(0, Colors.BLANK);
        colors.put(1, Colors.RED);
        colors.put(2, Colors.BLUE);
        colors.put(3, Colors.GREEN);
        colors.put(4, Colors.YELLOW);
        colors.put(5, Colors.PURPLE);

        // Scale size
        int gamepanelwidth = (int) (width*0.2)*4;
        int sidepanelwidth = width-gamepanelwidth;

        // Initialisation
        frame = new JFrame("Multiplayer Game");
        game = new GamePanel(rows, cols, gamepanelwidth, height);
        sidepanel = new SidePanel(Colors.BLANK, colors.get(player));

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

    public Color getColor()
    {
        return colors.get(player);
    }
}