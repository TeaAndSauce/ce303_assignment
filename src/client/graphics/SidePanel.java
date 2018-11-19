package client.graphics;

import client.graphics.handlers.GameButtonHandler;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel
{

    private Color color;
    private Color buttons;
    private JPanel north, south;
    private GameButton replace, free, doub;

    SidePanel(Color col, Color buttonCol, int width, int height)
    {
        this.color = col;
        this.buttons = buttonCol;

        // Settings
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));

        // Buttons
        replace = new GameButton("Replace", buttonCol);
        free = new GameButton("Freedom", buttonCol);
        doub = new GameButton("Double", buttonCol);

        // Action Listeners
        replace.addActionListener(new GameButtonHandler(replace));
        free.addActionListener(new GameButtonHandler(free));
        doub.addActionListener(new GameButtonHandler(doub));

        // Add components
        add(replace);
        add(free);
        add(doub);
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