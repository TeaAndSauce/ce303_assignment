package client.graphics;

import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton
{
    private Color color;
    private String label;
    private int w, h;

    GameButton(String label, Color color, int width, int height)
    {
        this.color = color;
        this.label = label;
        this.h = height;
        this.w = width;

        setPreferredSize(new Dimension(w, h));
        setMinimumSize(new Dimension(w, h));
        setMaximumSize(new Dimension(w, h));
        setText(label);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(color);
        g.fillRect(0, 0, this.w, 35);
        super.paintComponent(g);
    }
}
