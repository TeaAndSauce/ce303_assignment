package client.graphics;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel
{

    private Color color;
    private Color buttons;
    private JPanel north, south;
    private int width, height;

    GameButton replace, free, doub;
    SidePanel(Color col, Color buttonCol, int width, int height)
    {
        this.color = col;
        this.buttons = buttonCol;
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        // Jpanels
        north = new JPanel();
        south = new JPanel();
        north.setBackground(this.color);
        south.setBackground(this.color);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setPreferredSize(new Dimension(this.width, this.height/2));
        north.setPreferredSize(new Dimension(this.width, this.height/2));

        // Buttons
        replace = new GameButton("Replace", buttonCol, south.getWidth(), 25);
        free = new GameButton("Freedom", buttonCol, south.getWidth(), 25);
        doub = new GameButton("Double", buttonCol, south.getWidth(), 25);

        // Add components
        south.add(replace);
        south.add(free);
        south.add(doub);
        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(Colors.BACKGROUND_SIDE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(color);
        g.fillRect(2, 2, this.getWidth()-4, this.getHeight()-4);
    }
}
