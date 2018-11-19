package client.graphics;

import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton
{
    private Color color;

    GameButton(String label, Color color)
    {
        setText(label);
        this.color = color;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
