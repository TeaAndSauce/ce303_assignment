package client;

import org.junit.Test;

import java.util.Arrays;

public class Tests
{
    @Test
    public void testParseState()
    {
        Client c = new Client("localhost", 9001);
        String state = "1011100010.1011100010.1011100010.1011100010.1011100010.1011100010";
        int[][] compare = {
                {1,0,1,1,1,0,0,0,1,0},
                {1,0,1,1,1,0,0,0,1,0},
                {1,0,1,1,1,0,0,0,1,0},
                {1,0,1,1,1,0,0,0,1,0},
                {1,0,1,1,1,0,0,0,1,0},
                {1,0,1,1,1,0,0,0,1,0}
        };
        int[][] result = c.parseGameState(state);
        assert Arrays.deepEquals(compare, result);
    }
}
