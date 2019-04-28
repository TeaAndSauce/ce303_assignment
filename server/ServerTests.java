package server;

import org.junit.Assert;
import org.junit.Test;

public class ServerTests
{

    @Test
    public void testFormatState()
    {
        Game game = new Game(new Server(1, 1, 9001));
        assert Server.formatGameState(game.getState()).equals("0000000000.0000000000.0000000000.0000000000.0000000000.0000000000");
    }

    @Test
    public void testGamePlaceMove()
    {
        Game g = new Game(new Server(2, 0, 9001));
        g.newGame(2);
        System.out.println("If this does not work, it is probably because" +
        "move (0, 0) is occupied due to the server initially placing it here.");
        assert g.place(0, 0, 1, 0, false);
    }

    @Test
    public void testFreePositionsIsNotEmpty()
    {
        Game g = new Game(new Server(2, 0, 9001));
        g.newGame(2);
        assert !g.getAllFreePositions().isEmpty();
    }

    @Test
    public void testPlayersHaveCards()
    {
        Game g = new Game(new Server(2, 0, 9001));
        g.newGame(2);
        String cards = g.getCards(1);
        assert (cards.equals("c111"));
    }

    @Test
    public void testIfGameNotOver()
    {
        Game g = new Game(new Server(2, 0, 9001));
        g.newGame(2);
        assert !g.isGameOver();
    }
}
