/************************************************************************
  * Program: BasicStrategy
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java BasicStrategy 
  * Dependencies: List.java, Card.java, System.out.java
  * 
  * Basic Strategy only for blackjack. No card counting.
  * This strategy should lead to close to 50% winning level.
  ***********************************************************************/
import java.util.List;
import java.util.ArrayList;

public class BasicStrategy implements Strategy
{
    private List<Card> cards; // cards in hand
    private int dc;           // dealer card value as an index into strategy array
    private boolean oneace;   // hand has **at least** one ace
    private boolean pair;     // hand is only two cards and contains a pair
    private boolean splitaces;
    private int split;
    
    private boolean hos17; // dealer hits on 17?
    private boolean das;   // double after split allowed?
    
    public BasicStrategy(boolean hitsOn17, boolean das)
    {
        this.hos17 = hitsOn17;
        this.das = das;
        this.oneace  = false;
        this.pair    = false;
    }
    
    // set the initial hand of the Player
    public void setHand(boolean split, List<Card> hand, Card dealerCard)
    {
        if (hand.size() != 2)
            throw new 
            IllegalArgumentException("inital hand must be only 2 cards");
        
        this.cards = hand;
        this.dc = cnv2(dealerCard.value());
        //System.out.println("Check: cards: " + cards.get(0) + " and " + cards.get(1));
        
        pair = false;
        oneace = false;
        if (!split)
        {
            this.split = 0;
            splitaces = false;
        }
        
        // check if pair
        if (hand.get(0).value() == hand.get(1).value())
            pair = true;
        if (hand.get(0).rank() == Rank.ACE 
                     || hand.get(1).rank() == Rank.ACE)
            oneace = true;
        
        //System.out.println("Check: pair: " + pair + " ... oneace: " + oneace);
    }
    
    // get hand
    public List<Card> getHand() { return cards; }
    
    // converts the value of the card to an index to be used in accessing
    // strategy array
    private int cnv2(int value) { return value - 2; }
    private int cnv5(int value) 
    { 
        int temp = value - 5;
        if (temp < 0)
            temp = 0;
        return temp; 
    }
    
    // choose next play
    public int nextPlay()
    {
        int nextplay;
        
        // different strategies depending on current state
        if (pair)
            nextplay = playPair();
        else if (oneace)
            nextplay = playAce();
        else
            nextplay = playNormal();
        
        // can't double down after split if das not enabled
        if (nextplay == D && split > 0 && !das)
            nextplay = H;
        
        return nextplay;
    }
    
    // strategy for playing with a pair (only two cards)
    // takes splits into account
    private int playPair()
    {
        int nextplay;
        
        // if split aces, stand
        if (splitaces)
            return S;
        // if pair of aces
        else if (oneace)
        {
            pair = false;
            
            // consult strategy double array for next play
            nextplay = basicstrategy[PAIRS + cnv2(cards.get(0).value())][dc];
            // if a split
            if (nextplay == P)
            {
                split++;
                splitaces = true;
            }
            else
            {
                System.out.println("should never get here"); // should always be splitting on pair of aces
            }
        }
        // max number of splits is 2 in most blackjack
        else if (split < 3)
        {
            pair = false;
            
            // consult strategy double array for next play
            nextplay = basicstrategy[PAIRS + cnv2(cards.get(0).value())][dc];
            if (nextplay == P)
                split++;
        }
        // can't split anymore, already split twice
        else
        {
            nextplay = playNormal();
        }
        
        return nextplay;
    }
    
    // strategy for playing with a pair of aces
    private int playAce()
    {
   	 // if splitaces stand
        if (splitaces)
            return S;
        
        // check status of current hand
        int hardvalue = 0;
        int acecount = 0;
        for (Card c : cards)
        {
            hardvalue += c.value();
            if (c.rank() == Rank.ACE)
                acecount++;
        }
        
        // not blackjack if you've split or taken more cards already
        if (hardvalue == 21 && cards.size() == 2 && split == 0)
            return BLACKJACK;
        
        // return play if below 21
        if (hardvalue <= 21)
        {
            //System.out.println("play = " + basicstrategy[ONEACE + cnv2(hardvalue - 11)][dc]);
            return basicstrategy[ONEACE + cnv2(hardvalue - 11)][dc];
        }
        
        while (acecount-- != 0)
        {
            // change an ace counting as 11 to count as 1
            hardvalue -= 10;
            
            // return play if below 21
            if (hardvalue <= 21 && acecount >= 1)
                return basicstrategy[ONEACE + cnv2(hardvalue - 11)][dc];
        }
        
        // return play if below 21
        if (hardvalue <= 21)
        {
            //System.out.println("gets here");
            //System.out.println("play = " + basicstrategy[NORMAL + cnv5(hardvalue)][dc]);
            return basicstrategy[NORMAL + cnv5(hardvalue)][dc];
        }
        
        return BUST;
    }
    
    // play hand without aces or a pair
    private int playNormal()
    {
        int value = 0;
        
        // evaluate hand value
        for (Card c : cards)
            value += c.value();
        
        // bust if over 21
        if (value > 21)
            return BUST;

        // return next play
        return basicstrategy[NORMAL + cnv5(value)][dc];
    }
    
    // hit me!
    public void hit(Card c)
    {
        cards.add(c);
        
        if (c.rank() == Rank.ACE && !oneace)
            oneace = true;
    }
    
    // testing
    public static void main(String[] args)
    {
        Card c2 = new Card(Rank.TWO, Suit.CLUBS);
        Card c3 = new Card(Rank.THREE, Suit.CLUBS);
        Card c4 = new Card(Rank.FOUR, Suit.CLUBS);
        Card c5 = new Card(Rank.FIVE, Suit.CLUBS);
        Card c6 = new Card(Rank.SIX, Suit.CLUBS);
        Card c7 = new Card(Rank.SEVEN, Suit.CLUBS);
        Card c8 = new Card(Rank.EIGHT, Suit.CLUBS);
        Card c9 = new Card(Rank.NINE, Suit.CLUBS);
        Card c10 = new Card(Rank.TEN, Suit.CLUBS);
        Card cJ = new Card(Rank.JACK, Suit.CLUBS);
        Card cQ = new Card(Rank.QUEEN, Suit.CLUBS);
        Card cK = new Card(Rank.KING, Suit.CLUBS);
        Card cA = new Card(Rank.ACE, Suit.CLUBS);
        
        // test if double after split is allowed in testing
        boolean das = false;
        int nextplay;
        BasicStrategy bs = new BasicStrategy(false, das);
        ArrayList<Card> hand = new ArrayList<Card>();
        hand.add(c6);
        hand.add(c6);
        bs.setHand(false, hand, c2);
        if (bs.nextPlay() != P)
            System.out.println("1");
        hand.remove(1);
        hand.add(c5);
        bs.setHand(true, hand, c2);
        if (!das)
        {
            if (bs.nextPlay() != H)
                System.out.println("2");
        }
        else
        {
            if (bs.nextPlay() != D)
                System.out.println("3");
        }
        // test if new hand resets split count
        bs.setHand(false, hand, c2);
        if (bs.nextPlay() != D)
            System.out.println("4");
        
        // test that player can only split up to 4 hands
        hand.clear();
        hand.add(c6);
        hand.add(c6);
        bs.setHand(false, hand, c2);
        if (bs.nextPlay() != P)
            System.out.println("5");
        bs.setHand(true, hand, c2);
        if (bs.nextPlay() != P)
            System.out.println("6");
        bs.setHand(true, hand, c2);
        if (bs.nextPlay() != P)
            System.out.println("7");
        bs.setHand(true, hand, c2);
        if (bs.nextPlay() != H)
            System.out.println("8");
        
        // test splitting aces only once and no resplitting aces
        hand.clear();
        hand.add(cA);
        hand.add(cA);
        bs.setHand(false, hand, c2);
        if (bs.nextPlay() != P)
            System.out.println("9");
        bs.setHand(true, hand, c2);
        nextplay = bs.nextPlay();
        if (nextplay != S)
            System.out.println("10 " + nextplay);
        hand.remove(1);
        hand.add(c5);
        bs.setHand(true, hand, c2);
        nextplay = bs.nextPlay();
        if (nextplay != S)
            System.out.println("11 " + nextplay);
        
        // test return of blackjack
        hand.clear();
        hand.add(cA);
        hand.add(cJ);
        bs.setHand(false, hand, c6);
        if (bs.nextPlay() != BLACKJACK)
            System.out.println("12");
        
        // test return of BUST ww/o Ace
        hand.clear();
        hand.add(c7);
        hand.add(c6);
        bs.setHand(false, hand, c8);
        if (bs.nextPlay() != H)
            System.out.println("13");
        bs.hit(cQ);
        if (bs.nextPlay() != BUST)
            System.out.println("14");
        // w/ace
        hand.clear();
        hand.add(cA);
        hand.add(c2);
        bs.setHand(false, hand, cK);
        if (bs.nextPlay() != H)
            System.out.println("15");
        bs.hit(c5);
        if (bs.nextPlay() != H)
            System.out.println("16");
        bs.hit(c8);
        if (bs.nextPlay() != H)
            System.out.println("17");
        bs.hit(c6);
        if (bs.nextPlay() != BUST)
            System.out.println("18");
        
        hand.clear();
        hand.add(cA);
        hand.add(c2);
        bs.setHand(false, hand, cK);
        if (bs.nextPlay() != H)
            System.out.println("19");
        bs.hit(c5);
        if (bs.nextPlay() != H)
            System.out.println("20");
        bs.hit(c7);
        if (bs.nextPlay() != H)
            System.out.println("21");
        bs.hit(cA);
        if (bs.nextPlay() != H)
            System.out.println("22");
        bs.hit(c5);
        if (bs.nextPlay() != S)
            System.out.println("23");
        
        
    }
    
}