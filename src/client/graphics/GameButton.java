package client.graphics;

import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton
{
    private Color color;
    private String label;
    private SidePanel panel;

    GameButton(String label, Color color, SidePanel panel)
    {
        this.color = color;
        this.label = label;
        this.panel = panel;

        setText(label);
        setPreferredSize(new Dimension(panel.getWidth(), 25));
        setMinimumSize(new Dimension(panel.getWidth(), 25));
        setBackground(color);
        repaint();

        this.setBorderPainted(false);
        this.setFocusPainted(false);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}
