package client.playables;

import client.types.Playable;

public class Bot extends Playable
{
    /*
     * This bot may contain some level of AI
     * where it looks ahead. Although this will
     * only be added if there is extra time to
     * kill during this assignment.
     */

    public static void main(String[] args) {
        // Bot stuff
    }

    @Override
    public void placeMove(int row, int col) {

    }

    @Override
    public int[][] getState() {
        return new int[0][];
    }

    @Override
    public void useFreedomCard() {

    }

    @Override
    public void useReplacementCard() {

    }

    @Override
    public void useDoubleMoveCard() {

    }
}
