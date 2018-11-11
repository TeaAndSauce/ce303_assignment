package client.graphics;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel
{
    private int gap = 2;
    private int gameRows, gameCols;
    private int[][] gamestate;
    private int squaresWidth, squaresHeight;
    private Map<Integer, Color> playerColors;
    private Map<Integer, String> playerLabels;
    private List<Pair<Integer, Integer>> possible;

    void update()
    {
        this.repaint();
    }

    void place(int row, int col, int player)
    {
        gamestate[row][col] = player;
        update();
    }

    GamePanel(int rows, int cols, int width, int height)
    {
        // Show possible moves for this client
        possible = new ArrayList<>();

        // Initialise player maps
        playerLabels = new HashMap<>();
        playerColors = new HashMap<>();

        // Initialise size of panel
        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));

        // Player colors
        playerColors.put(0, Colors.BLANK);
        playerColors.put(1, Colors.RED);
        playerColors.put(2, Colors.BLUE);
        playerColors.put(3, Colors.GREEN);

        // Player labels
        playerLabels.put(0, "");
        playerLabels.put(1, "R");
        playerLabels.put(2, "G");
        playerLabels.put(3, "B");

        // Dimensions
        gameRows = rows;
        gameCols = cols;

        // Calculate square sizes
        gamestate = new int[rows][cols];
        squaresHeight = (this.getHeight()) / rows;
        squaresWidth = (this.getWidth()) / cols;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        // Background
        g.setColor(Colors.BACKGROUND);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw game
        for (int row = 0; row < gameRows; row++)
        {
            for (int col = 0; col < gameCols; col++)
            {
                g.setColor(playerColors.get(gamestate[row][col]));
                g.fillRoundRect(col*squaresWidth+gap, row*squaresHeight+gap, squaresWidth-gap, squaresHeight-gap, 4, 4);
            }
        }
    }
}
