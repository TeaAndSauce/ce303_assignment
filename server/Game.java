package server;

import javafx.util.Pair;
import shared.Cards;
import shared.Message;

import java.util.*;

public class Game
{
    /*
    Properties:
        * running
        * state
        * height
        * width
        * Map<Integer, String> pieces
        * Map<Integer, List<Pair<Integer, Integer>>> plays
        * Map<Integer, Integer> scores

    Methods:
        * getMoves(int player, int card)
        * place(int row, int col, int player)
        * getAllFreePositions
        * newGame
        * cloneState
        * getState
        * isRunning
     */

    //==============================================================================
    // PROPERTIES
    //==============================================================================

    private int width, height, max;
    private int[][] state;
    private boolean running;
    private Server server;
    private Map<Integer, String> pieces;
    private Map<Integer, List<Pair<Integer, Integer>>> plays;
    private Map<Integer, Cards> cards;
    private Map<Integer, Integer> scores;

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    Game(Server server)
    {
        // STORE SERVER INSTANCE
        this.server = server;

        // GAME DIMENSIONS
        width = 10; height = 6;

        // GAME STATE
        state = new int[height][width];

        // MAXIMUM PLAYERS
        max = 5;

        // PLAYER INFORMATION
        //// 'pieces' = player labels
        //// 'plays'  = player moves
        pieces = new HashMap<>();
        pieces.put(1, "R");
        pieces.put(2, "G");
        pieces.put(3, "B");
        pieces.put(4, "Y");
        pieces.put(5, "P");
        plays = new HashMap<>();
        plays.put(1, new ArrayList<>());
        plays.put(2, new ArrayList<>());
        plays.put(3, new ArrayList<>());
        plays.put(4, new ArrayList<>());
        plays.put(5, new ArrayList<>());
        scores = new HashMap<>();
        scores.put(1, 0);
        scores.put(2, 0);
        scores.put(3, 0);
        scores.put(4, 0);
        scores.put(5, 0);
        cards = new HashMap<>();
        cards.put(1, new Cards());
        cards.put(2, new Cards());
        cards.put(3, new Cards());
        cards.put(4, new Cards());
        cards.put(5, new Cards());
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    public boolean isPlayerBlocked(int player)
    {
        List<Pair<Integer, Integer>> possible = getPossibleMoves(player, 0);
        if (possible.isEmpty() & !cards.get(player).hasCard(Cards.FREEDOM) & !cards.get(player).hasCard(Cards.REPLACE))
        {
            return true;
        }
        return false;
    }

    public boolean place(int row, int col, int player, int card, boolean init)
    {
        // PLACE A MOVE ONTO THE BOARD, THE FLAG 'INIT' IS FOR INITIAL MOVES
        // -------------------------------------------------------------------------
        // Without this flag, it would use up each players freedom card

        Pair<Integer, Integer> move = new Pair<>(row, col);
        List<Pair<Integer, Integer>> possible = getPossibleMoves(player, card);
        if (possible.contains(move))
        {
            // Remove players move if it was replaced
            if (card == Cards.REPLACE)
            {
                int oldOwner = state[row][col];
                if (oldOwner != 0)
                {
                    plays.get(oldOwner).remove(new Pair(row, col));
                }
            }
            // place the move
            state[row][col] = player;
            plays.get(player).add(move);
            scores.put(player, scores.get(player)+1);
            if (!init)
            {
                if (card != 0)
                {
                    // use the card if this was not an initial move
                    cards.get(player).useCard(card);
                }
            }
            // check if the game is over after move was placed
            if (isGameOver())
            {
                server.setGameOver();
            }
            // return if this move was made
            return true;
        }
        // the move was invalid
        else
        {
            System.out.println("Player " + player + " tried to make an invalid move.");
        }
        return false;
    }

    List<Pair<Integer, Integer>> getPossibleMoves(int player, int card)
    {
        // GETS THE POSSIBLE MOVES FOR THE GIVEN CARD
        // -------------------------------------------------------------------------

        List<Pair<Integer, Integer>> possible = new ArrayList<>();
        switch(card)
        {
            // If we are using the freedom card, we should be able to
            // go anywhere that is free
            case Cards.FREEDOM:
                possible = getAllFreePositions();
                break;

            // If we are using the replace card, we should only look
            // at our neighbours and see if another player is next to one
            // of our moves
            case Cards.REPLACE:
                possible = getReplaceablePositions(player);
                break;

            // This is simply a normal move
            case Cards.DOUBLE:
                possible = getNormalMoves(player);
                break;

            // If no card was used, it is a normal move
            case Cards.NONE:
                possible = getNormalMoves(player);
                //System.out.println("Is possible moves empty? " + possible.isEmpty());
                break;
        }

        return possible;
    }

    List<Pair<Integer, Integer>> getAllFreePositions()
    {
        // GETS ALL FREE POSITIONS ON THE BOARD
        // -------------------------------------------------------------------------

        List<Pair<Integer, Integer>> free = new ArrayList<>();
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++) {
                if (state[row][col] == 0)
                    free.add(new Pair(row, col));
            }
        }
        return free;
    }

    List<Pair<Integer, Integer>> getNormalMoves(int player)
    {
        // LOOKS FOR FREE POSITIONS WITHIN OUR LIST OF NEIGHBOURS
        // -------------------------------------------------------------------------

        List<Pair<Integer, Integer>> possible = new ArrayList<>();
        List<Pair<Integer, Integer>> nodes = plays.get(player);
        for (Pair<Integer, Integer> move : nodes )
        {
            int r = move.getKey();
            int c = move.getValue();
            for (int nr = Math.max(0, r - 1); nr <= Math.min(r + 1, state.length - 1); ++nr){
                for (int nc = Math.max(0, c - 1); nc <= Math.min(c + 1, state[0].length - 1); ++nc) {
                    if (!(nr==r && nc==c))  {  // don't process board[r][c] itself
                        // board[nr][nc] is one of board[r][c]'s neighbors
                        if (state[nr][nc] == 0)
                        {
                            Pair<Integer, Integer> m = new Pair<>(nr, nc);
                            if (!possible.contains(m))
                            {
                                //System.out.println("Added move (" + nr + ", " + nc + ") to possible moves.");
                                possible.add(new Pair(nr, nc));
                            }
                        }
                    }
                }
            }
        }
        return possible;
    }

    List<Pair<Integer, Integer>> getReplaceablePositions(int player)
    {
        // LOOKS FOR OTHER PLAYERS MOVES WITHIN OUR LIST OF NEIGHBOURS
        // -------------------------------------------------------------------------

        List<Pair<Integer, Integer>> replaceable = new ArrayList<>();
        List<Pair<Integer, Integer>> nodes = plays.get(player);
        for (Pair<Integer, Integer> move : nodes )
        {
            int r = move.getKey();
            int c = move.getValue();
            for (int nr = Math.max(0, r - 1); nr <= Math.min(r + 1, state.length - 1); ++nr){
                for (int nc = Math.max(0, c - 1); nc <= Math.min(c + 1, state[0].length - 1); ++nc) {
                    if (!(nr==r && nc==c))  {  // don't process board[r][c] itself
                        // board[nr][nc] is one of board[r][c]'s neighbors
                        if (state[nr][nc] != 0 & state[nr][nc] != player)
                        {
                            Pair<Integer, Integer> m = new Pair<>(nr, nc);
                            if (!replaceable.contains(m))
                                replaceable.add(new Pair(nr, nc));
                        }
                    }
                }
            }
        }
        return replaceable;
    }

    public int[][] getState()
    {
        // GETS A CLONE OF THE GAME STATE
        // -------------------------------------------------------------------------

        return cloneState(state);
    }

    private int[][] cloneState(int[][] state)
    {
        // CREATES AND RETURNS A COPY OF THE GAME STATE
        // -------------------------------------------------------------------------

        int r = state.length;
        int c = state[0].length;
        int[][] result = new int[r][c];

        for ( int row = 0; row < height; row++ ) {
            for (int col = 0; col < width; col++) {
                result[row][col] = state[row][col];
            }
        }
        return result;
    }

    public int getWinner()
    {
        int playerBest = 0;
        int scoreBest = 0;
        for (Map.Entry<Integer, Integer> score : scores.entrySet())
        {
            if (score.getValue() > scoreBest)
            {
                scoreBest = score.getValue();
                playerBest = score.getKey();
            }
        }
        return playerBest;
    }

    public int[][] newGame(int playersConnected)
    {

        // WIPE THE GAME BOARD AND PLACE PLAYERS RANDOMLY
        // -------------------------------------------------------------------------

        //System.out.println("Clearing board..");
        clear();

        //System.out.println("Placing players randomly..");
        Random r = new Random();
        for (int i=0; i < playersConnected; i++ )
        {
            List<Pair<Integer, Integer>> moves = getAllFreePositions();
            Pair<Integer, Integer> choice = moves.get(r.nextInt(moves.size()));
            //System.out.println("Random choice: " + choice.getKey() + ", " + choice.getValue());
            place(choice.getKey(), choice.getValue(), i+1, 1, true);
            List<Pair<Integer, Integer>> playerPlays = plays.get(i+1);
            playerPlays.add(choice);
        }

        System.out.println("Game is now running!");
        running = true;
        return getState();
    }

    private void clear()
    {
        // CLEAR PLAYER INFORMATION MAPS
        // -------------------------------------------------------------------------

        plays.keySet().forEach(p -> plays.get(p).clear());
        scores.keySet().forEach(s -> scores.put(s, 0));
        cards.keySet().forEach(c -> cards.put(c, new Cards()));

        // WIPE THE GAME STATE
        // -------------------------------------------------------------------------

        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
                state[row][col] = 0;
    }

    public boolean hasCard(int player, int card)
    {
        // CHECKS IF THE GIVEN PLAYER HAS THE GIVEN CARD
        // -------------------------------------------------------------------------

        return cards.get(player).hasCard(card);
    }

    public String getCards(int player)
    {
        // GETS THE STRING FORM OF THE PLAYERS SET OF CARDS
        // -------------------------------------------------------------------------

        return cards.get(player).toString();
    }

    public boolean isGameOver()
    {
        // CHECKS IF THE GAME IS OVER
        // -------------------------------------------------------------------------

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                if (state[row][col] == 0)
                    return false;
            }
        }
        return true;
    }

    public int getScore(int player)
    {
        // GETS THE SCORE FOR A PLAYER
        // -------------------------------------------------------------------------

        return scores.get(player);
    }
}
