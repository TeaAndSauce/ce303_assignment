package testing;

class FormatGamestate
{
    public static void main(String[] args) {
        FormatGamestate f = new FormatGamestate();
        int[][] test = new int[6][10];
        System.out.println(f.formatGameState(test));
    }

    public String formatGameState(int[][] state)
    {
        String result = "";
        for (int row = 0; row < state.length; row++)
        {
            for (int col = 0; col < state[0].length; col++)
            {
                result += state[row][col] + " ";
            }
            if (row < state.length-1)
                result += "| ";
        }
        return result.trim();
    }
}
