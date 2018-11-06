package client.types;

public abstract class Playable
{
    public static int MAX_PLAYERS = 3;
    public abstract void placeMove(int row, int col);
    public abstract int[][] getState();
    public abstract void useFreedomCard();
    public abstract void useReplacementCard();
    public abstract void useDoubleMoveCard();
}