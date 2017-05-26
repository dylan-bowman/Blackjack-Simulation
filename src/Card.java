/************************************************************************
  * Program: Card
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java Card numberofDecks
  * Dependencies: java.util.ArrayList, java.integer
  * 
  * Card class to represent a playing card, and also can return a deck
  * of unshuffled cards or set of multiple unshuffled decks of card.
  ***********************************************************************/
import java.util.List;
import java.util.ArrayList;

enum Rank { 
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), 
            NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);
        private int value;
        Rank(int value) { this.value = value;}
        public int value() { return this.value;}
    }

enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

public class Card
{
    
    
    private final Rank rank;
    private final Suit suit;
    
    public Card(Rank rank, Suit suit)
    {
        this.rank  = rank;
        this.suit  = suit;
        
    }
    
    public Rank rank()  { return rank; }
    public Suit suit()  { return suit; }
    public int  value() { return rank.value(); }
    
    public String toString() { return rank + " of " + suit; }
    
    // make a single deck
    public static ArrayList<Card> newDeck()
    {
        return makeDecks(1);
    }
    
    // make multiple decks
    public static ArrayList<Card> newDecks(int n)
    {
        return makeDecks(n);
    }
    
    // private method to make the actual decks
    private static final ArrayList<Card> makeDecks(int n)
    {
        ArrayList<Card> decks = new ArrayList<Card>(n*52);
        
        for (int i = 0; i < n; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) 
                    decks.add(new Card(rank, suit));
            }
        }
        
        return decks;
    }
    
    public static void main(String[] args)
    {
        int numDecks = Integer.parseInt(args[0]);
        List<Card> decks = Card.newDecks(numDecks);
        
        for (Card card : decks)
        {
            System.out.println(card + " " + card.rank.value());
        }
    }
}