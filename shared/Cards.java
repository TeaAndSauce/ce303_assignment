package shared;

public class Cards
{
    /*
    Properties:
        * NONE    [STATIC]
        * FREEDOM [STATIC]
        * REPLACE [STATIC]
        * DOUBLE  [STATIC]
        * int[3]

    Methods:
        * hasCard
        * useCard
        * toString
        * setCards(String)
        * reset
     */

    //==============================================================================
    // PROPERTIES
    //==============================================================================

    public static final int NONE = 0;
    public static final int FREEDOM = 1;
    public static final int REPLACE = 2;
    public static final int DOUBLE = 3;
    private int[] cards;

    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================

    public Cards()
    {
        cards = new int[3];
        cards[0] = 1;
        cards[1] = 1;
        cards[2] = 1;
    }

    //==============================================================================
    // ABSTRACT METHODS
    //==============================================================================

    public static String getCardName(int card)
    {
        switch (card)
        {
            case FREEDOM:
                return "freedom";

            case REPLACE:
                return "replace";

            case DOUBLE:
                return "double";

            case NONE:
                return "none";
        }
        return "unknown";
    }

    //==============================================================================
    // METHODS
    //==============================================================================

    public void setCards(String cardString)
    {
        // EXAMPLE STRING: 'c101'
        // This would mean that the player has a freedom card and a double card
        // but no replace card

        cards = new int[3];
        if (cardString.startsWith("c") & cardString.length() == 4)
        {
            String[] tokens = cardString.split("");
            cards[0] = Integer.valueOf(tokens[FREEDOM]);
            cards[1] = Integer.valueOf(tokens[REPLACE]);
            cards[2] = Integer.valueOf(tokens[DOUBLE]);
        }
    }

    public boolean hasCard(int card)
    {
        if (card >= 1 & card <= 3)
            return cards[card-1] == 1;
        return false;
    }

    public void useCard(int card)
    {
        if (card >= 1 & card <= 3)
            if (cards[card-1] == 1)
                cards[card-1] = 0;
    }

    public void reset()
    {
        cards[0] = 1;
        cards[1] = 1;
        cards[2] = 1;
    }

    //==============================================================================
    // OVERRIDES
    //==============================================================================

    @Override
    public String toString()
    { return "c" + cards[0] + cards[1] + cards[2];
    }
}
