package server;

import server.exceptions.InvalidMoveException;
import javafx.util.Pair;
import java.util.*;
import java.util.List;

public class Game
{
    /*
     * Methods required:
     *      - New game
     *      - Clear board
     *      - Clone state
     *      - Place piece
     *      - Get all possible moves for a player
     *      - Get all free spots on board
     *
     *      The turns will be decided by the server.
     */
    private Map<Integer, String> pieces;
    private Map<Integer, List<Pair<Integer, Integer>>> plays;
    private int[][] state;
    public static int HEIGHT = 6;
    public static int WIDTH = 10;

    /**
     * Initialises the game and sets its default
     * state.
     */
    Game()
    {
        // Each player will be represented as a number
        // These numbers are stored in a map that
        // correspond to that player's label.
        pieces = new HashMap<>();
        pieces.put(1, "R");
        pieces.put(2, "G");
        pieces.put(3, "B");

        // List containing all positions currently
        // occupied each specific player.
        plays = new HashMap<>();

        // New HEIGHTxWIDTH board, default will be
        // 6x10. This is to make the board scalable
        // if a bigger board is desired.
        state = new int[HEIGHT][WIDTH];
    }

    /**
     * Places a piece on the board at the
     * given position. Will not place the
     * piece if the spot is already occupied.
     *
     * If 'freecard' is true
     *
     * @param player
     *      The number representing the player
     * @param row
     *      The row number
     * @param col
     *      The column number
     */
    public void place(int player, int row, int col, boolean freecard) throws InvalidMoveException
    {
        List<Pair<Integer, Integer>> possible = getPossibleMovesForPlayer(player, freecard);

        // Is the desired move valid?
        if ( !possible.contains(new Pair(row, col)) )
            throw new InvalidMoveException(row, col);
        // Make the move if valid
        else
            state[row][col] = player;
    }

    /**
     * Gets all the possible moves for a specific
     * player. This method looks at all of the
     * neighbours of each piece that is placed
     * on the board by that specific player and
     * adds them to the 'possibleMoves' list if
     * that neighbour is free.
     *
     * If the freecard flag is true, it will
     * instead return all the free positions on
     * the board using the 'getAllFreePositions'
     * method.
     *
     * @param player
     *      The player to check for nearest neighbours
     * @param freecard
     *      The freecard flag that indicates the activation
     *      of the 'freedom' influence card where the player
     *      can place a piece on any spot on the board.
     * @return
     *      If 'freecard' is true, it will return all free
     *      spots on the board.
     *      If 'freecard' is false, it will return all
     *      available moves adjacent to each of the players
     *      current pieces on the board.
     */
    public List<Pair<Integer, Integer>> getPossibleMovesForPlayer(int player, boolean freecard)
    {
        // The possible moves to return
        List<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();

        // If the freecard is disabled, get all available neighbours for
        // each piece placed on the board that belongs to specified player
        if (!freecard)
        {
            // All current plays on the board for specific player
            List<Pair<Integer, Integer>> nodes = plays.get(player);

            for (Pair<Integer, Integer> move : nodes )
            {
                int thisPosX = move.getKey();   // row
                int thisPosY = move.getValue(); // col

                // Get bounds
                int startPosX = (thisPosX - 1 < 0) ? thisPosX : thisPosX-1;
                int startPosY = (thisPosY - 1 < 0) ? thisPosY : thisPosY-1;
                int endPosX =   (thisPosX + 1 > WIDTH) ? thisPosX : thisPosX+1;
                int endPosY =   (thisPosY + 1 > HEIGHT) ? thisPosY : thisPosY+1;

                // Look at all neighbours within bounds
                for (int rowNum=startPosX; rowNum<=endPosX; rowNum++)
                {
                    for (int colNum=startPosY; colNum<=endPosY; colNum++)
                    {
                        // state[rowNum][colNum] = Neighbours
                        // If there is no current piece in this neighbour
                        if (state[rowNum][colNum] == 0)
                            possibleMoves.add(new Pair(rowNum, colNum));
                    }
                }
            }
        }
        // If the freecard is activated, just get list of all free moves
        else
            possibleMoves = getAllFreePositions();
        return possibleMoves;
    }

    /**
     * Gets all the positions of which are
     * not occupied by a player piece.
     *
     * @return
     *      The free positions on the board
     */
    public List<Pair<Integer, Integer>> getAllFreePositions()
    {
        List<Pair<Integer, Integer>> free = new ArrayList<>();
        for (int row = 0; row < HEIGHT; row++)
        {
            for (int col = 0; col < WIDTH; col++) {
                if (state[row][col] == 0)
                    free.add(new Pair(row, col));
            }
        }
        return free;
    }

    /**
     * Creates a new game with the pieces placed
     * in random parts of the board. Players cannot
     * be on the same spot.
     */
    public void newGame()
    {
        clear();
        Random r = new Random();
        for (int i=1; i <= 3; i++ )
        {
            // Get all free positions
            List<Pair<Integer, Integer>> moves = getAllFreePositions();
            // Select random position
            Pair<Integer, Integer> choice = moves.get(r.nextInt(moves.size()));
            // Place random choice, freecard is true as there are no
            // pieces currently on the board that you can check the neighbours of
            // and therefore means the player must be able to pick any of
            // the free spots
            place(i, choice.getKey(), choice.getValue(), true);
            // Add to players current pieces on the board
            plays.get(i).add(new Pair(choice.getKey(), choice.getValue()));
        }
    }

    /**
     * Clears the game board by setting all
     * values to zero.
     */
    private void clear()
    {
        for (int row = 0; row < state.length; row++ )
            for (int col = 0; col < state[0].length; col++ )
                state[row][col] = 0;
    }

    /**
     * Clones the state of a game and returns
     * the state. This is to avoid returning
     * the reference to the game state.
     *
     * @param state
     *      The state of the game
     * @return
     *      A cloned state of the game
     */
    private int[][] cloneState(int[][] state)
    {
        int r = state.length;
        int c = state[0].length;
        int[][] result = new int[r][c];

        for ( int row = 0; row < HEIGHT; row++ ) {
            for (int col = 0; col < WIDTH; col++) {
                result[row][col] = state[row][col];
            }
        }
        return result;
    }

    /**
     * Gets a copy of the board state.
     *
     * @return
     *      The game state copy.
     */
    public int[][] getState()
    {
        return cloneState(state);
    }
}