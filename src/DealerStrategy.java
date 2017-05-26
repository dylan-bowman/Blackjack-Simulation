/************************************************************************
  * Program: DealerStrategy
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java DealerStrategy 
  * Dependencies: List.java, Card.java, System.out.java
  * 
  * Dealer Strategy for blackjack.
  ***********************************************************************/
import java.util.ArrayList;
import java.util.List;

public class DealerStrategy implements Strategy
{
    private List<Card> cards; // cards in dealer's hand
    private boolean hos17;    // does the dealer stand on soft 17?
    
    public DealerStrategy(boolean hitson17)
    {
        this.hos17 = hitson17;
    }
    
    public void setHand(boolean split, List<Card> hand, Card dealerCard)
    {
        if (hand.size() != 2)
            throw new 
            IllegalArgumentException("inital hand must be only 2 cards");
        if (hand.get(0).value() != dealerCard.value()
                && hand.get(1).value() != dealerCard.value())
            throw new IllegalArgumentException("dealer card not right");
        
        this.cards = hand;
    }
    
    public List<Card> getHand() { return cards; }
    
    public void hit(Card c) { cards.add(c); }
    
    public int nextPlay()
    {
        int value = 0;
        int acecount = 0;
        
        for (Card c : cards)
        {
            value += c.value();
            if (c.rank() == Rank.ACE)
                acecount++;
        }
        
        if (value == 21 && cards.size() == 2)
            return BLACKJACK;
        if (value > 21 && acecount == 0)
            return BUST;
        if (value > 21 && acecount >= 1)
        {
            while (acecount-- != 0)
            {
                value -= 10;
                if (value <= 21)
                    return strategy(value, acecount);
            }
            
            return BUST;
        }
        
        if (value < 17)
            return H;
        return S;
    }
    
    private int strategy(int value, int acecount)
    {
        // possibly throw an error catcher here to check value/acecount?
        
        // hits on soft 17
        if (hos17)
        {
            if (acecount == 0)
            {
                if (value < 17)
                    return H;
                return S;
            }
            
            if (value < 18)
                return H;
            return S;
        }
        // stays on soft 17
        if (value < 17)
            return H;
        return S;
    }
    
    public static void main(String[] args)
    {
        Card c1 = new Card(Rank.ACE, Suit.CLUBS);
        Card c2 = new Card(Rank.TWO, Suit.CLUBS);
        Card c3 = new Card(Rank.THREE, Suit.CLUBS);
        Card c4 = new Card(Rank.FOUR, Suit.CLUBS);
        Card c5 = new Card(Rank.FIVE, Suit.CLUBS);
        Card c6 = new Card(Rank.SIX, Suit.CLUBS);
        Card c7 = new Card(Rank.SEVEN, Suit.CLUBS);
        Card c8 = new Card(Rank.EIGHT, Suit.CLUBS);
        Card c9 = new Card(Rank.NINE, Suit.CLUBS);
        Card c10 = new Card(Rank.TEN, Suit.CLUBS);
        Card c11 = new Card(Rank.JACK, Suit.CLUBS);
        Card c12 = new Card(Rank.QUEEN, Suit.CLUBS);
        Card c13 = new Card(Rank.KING, Suit.CLUBS);
        ArrayList<Card> hand = new ArrayList<Card>();
        hand.add(c1);
        hand.add(c2);
        //hand.add(c3);
        //hand.add(c4);
        DealerStrategy ds = new DealerStrategy(false);
        ds.setHand(false, hand, c2);
        System.out.println(ds.nextPlay());
        ds.hit(c3);
        System.out.println(ds.nextPlay());
        ds.hit(c6);
        System.out.println(ds.nextPlay());
        ds.hit(c8);
        System.out.println(ds.nextPlay());
        ds.hit(c4);
        System.out.println(ds.nextPlay());
    }
    
}