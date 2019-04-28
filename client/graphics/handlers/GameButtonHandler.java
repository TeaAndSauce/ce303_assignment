package client.graphics.handlers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameButtonHandler implements ActionListener
{
    private String buttonClass;
    private JButton button;

    public GameButtonHandler(JButton button)
    {
        this.buttonClass = button.getText().toLowerCase().trim();
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // If the card is activated, we need to make sure
        // that the client sends this information to the
        // server in the form of [row, col, player, card]
        // where the card is a string that matches the
        // strings below in the switch case statement

        switch(buttonClass)
        {
            case "double":
                break;

            case "replace":
                break;

            case "freedom":
                break;
        }
    }
}
