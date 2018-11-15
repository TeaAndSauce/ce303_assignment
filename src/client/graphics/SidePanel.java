package client.graphics;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel
{

    Color color;
    Color buttons;
    JPanel north, south;

    GameButton replace, free, doub;
    SidePanel(Color col, Color buttonCol)
    {
        this.color = col;
        this.buttons = buttonCol;
        setLayout(new BorderLayout());

        // Jpanels
        north = new JPanel();
        south = new JPanel();
        north.setBackground(this.color);
        south.setBackground(this.color);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);

        // Buttons
        replace = new GameButton("Replace", buttons, this);
        free = new GameButton("Freedom", buttons, this);
        doub = new GameButton("Double", buttons, this);
        south.add(replace);
        south.add(free);
        south.add(doub);
    }

    void setColor(Color col)
    {
        this.color = col;
        this.repaint();
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
