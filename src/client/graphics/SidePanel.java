package client.graphics;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel
{

    Color color;
    SidePanel(Color col)
    {
        this.color = col;
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
