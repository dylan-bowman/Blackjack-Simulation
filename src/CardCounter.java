/************************************************************************
  * Program: CardCounter
  * Name: Dylan Bowman
  * Execution: java CardCounter
  * 
  * A card counter blackjack player.  Uses hilo, ko or zen card counting
  * strategies to track the status of the deck to play blackjack with
  * a higher probability of winning.
  * 
  ***********************************************************************/
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


//enum Action { S, H, D, P, BUST, BLACKJACK; }

public class CardCounter implements Player
{
    private Strategy strategy;
    private int[] countstrat;
    private double money;
    private Type type;
    private int bet;
    private int[] bets;
    private int won;
    private int lost;
    
    private int count;
    private int tc;  // true count = count / decks left, rounded
    
    // these keep track of the money won or lost at the different counts throughout the simulation
    private double[] pos; // positive counts
    private double[] neg; // negative counts
    private double zero; // when count is 0
    
    private int ltc; // true count when bet was made.
    
    // should make a class called Rules which passes on Rules
    public CardCounter(Type t, boolean ho17, boolean das, int money, int[] bets)
    {
        this.money = money;
        this.bets = bets;
        this.type = t;
        
        pos = new double[12];
        neg = new double[12];
        zero = 0.0;
        
        if (t == Type.HILO)
            this.countstrat = hilo;
        else if (t == Type.KO)
            this.countstrat = ko;
        else if (t == Type.ZEN)
            this.countstrat = zen;
        this.strategy = new BasicStrategy(ho17, das);
        this.count = 0;
    }
    
    // converts the value of the card to an index to be used in accessing
    // strategy array
    private int cnv2(int value) { return value - 2; }
    
    // get the current money level
    public double getMoney() { return money; }
    
    // clear the player's score
    public void clearScore() {money = 0; }
    
    // set the hand
    public void setHand(boolean split, List<Card> hand, Card dc) 
    { strategy.setHand(split, hand, dc); }
    
    // get the current hand
    public List<Card> getHand() { return strategy.getHand(); }
    
    // get the next action
    public int nextAction() { return strategy.nextPlay(); }
    
    // hit me!
    public void hit(Card c) { strategy.hit(c); }
    
    // calculate the current hand value
    public int handValue()
    {
        List<Card> hand = strategy.getHand();
        int value = 0;
        int acecount = 0;
        for (Card c : hand)
        {
            value += c.value();
            if (c.rank() == Rank.ACE)
                acecount++;
        }
        if (value <= 21)
            return value;
        while (acecount-- != 0)
        {
            // change an ace counting as 11 to count as 1
            value -= 10;
            if (value <= 21)
                return value;
        }
        return value;
    }
    
    // show the card's value to the cardcounter and calculate the deck's current value
    public void show(Card c, int cardsleft)
    {
        count += countstrat[cnv2(c.value())];
        //System.out.println("Count = " + count);
        float decksleft = (float) cardsleft/52;
        //System.out.println("decksleft = " + decksleft);
        tc = Math.round(count/decksleft);
        //System.out.println("tc = " + tc);
    }
    
    // the decks were shuffled, reset the count
    public void shuffle() { count = 0; tc = 0; }
    
    // make a bet from the player's bets distribution (will bet higher at more
    // advantageous counts and vice versa)
    public void makeBet(boolean split) 
    { 
        if (!split)
            ltc = tc;
        if (tc > 11)
            bet = bets[11];
        else if (tc < 0)
            bet = bets[0];
        else
            bet = bets[tc];
        //System.out.println("tc = " + tc);
        //System.out.println("Bet = " + bet);
    }
    
    // get the current bet
    public int getBet() { return bet; }
    
    // win the current bet and register it on the distributions
    public void winBet(int b) { 
        money += b; 
        won++;
        if (ltc > 11)
            pos[11] += b;
        else if (ltc > 0)
            pos[ltc-1] += b;
        else if (ltc < -11)
            neg[11] += b;
        else if (ltc < 0)
            neg[Math.abs(ltc)-1] += b;
        else
            zero += b;
    }
    
    // lose the current bet and register it on the distributions
    public void loseBet(int b) { 
        money -= b; 
        lost++;
        if (ltc > 11)
            pos[11] -= b;
        else if (ltc > 0)
            pos[ltc-1] -= b;
        else if (ltc < -11)
            neg[11] -= b;
        else if (ltc < 0)
            neg[Math.abs(ltc)-1] -= b;
        else
            zero -= b;
    }
    
    // win a blackjack and register it on the distributions
    public void winBlackjack() { 
        money += (double) bet*1.5; 
        won++;
        if (ltc > 11)
            pos[11] += (double) bet*1.5;
        else if (ltc > 0)
            pos[ltc-1] += (double) bet*1.5;
        else if (ltc < -11)
            neg[11] += (double) bet*1.5;
        else if (ltc < 0)
            neg[Math.abs(ltc)-1] += (double) bet*1.5;
        else
            zero += (double) bet*1.5;
    }
    
    // double down
    public void doubleDown() { bet = bet*2; }
    
    // get the number of hands won
    public int getWon() {return won;};
    
    // get the number of hands lost
    public int getLost() {return lost;};
    
    // get the distributions
    public double[] getPos() {return pos;};
    public double[] getNeg() {return neg;};
    public double getZero() {return zero;};
    
    // testing
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
        Card cJ = new Card(Rank.JACK, Suit.CLUBS);
        Card cQ = new Card(Rank.QUEEN, Suit.CLUBS);
        Card cK = new Card(Rank.KING, Suit.CLUBS);
        
        int[] bets = { 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        CardCounter player = new CardCounter(Type.HILO, false, false, 500, bets);
        
        ArrayList<Card> deck = Card.newDecks(4);
        Collections.shuffle(deck);
        while (!deck.isEmpty())
        {
            Card c = deck.remove(0);
            System.out.println(c);
            player.show(c, deck.size());
            player.makeBet(false);
        }
        
    }
    }