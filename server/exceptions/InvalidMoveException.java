package server.exceptions;

/**
 * Called if a player attempts to place a piece in
 * an occupied spot.
 */
public class InvalidMoveException extends RuntimeException
{
    public InvalidMoveException(int row, int col)
    { super("INVALID MOVE: row=" + row + ", col=" + col);
    }
}
