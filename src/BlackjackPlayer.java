/************************************************************************
  * Program: BlackjackPlayer
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java BlackjackPlayer 
  * Dependencies: 
  * 
  * A typical blackjack player at the BlackjackTable.  Can be playing
  * as the dealer or just basic strategy.  Assuming a lot from basic
  * blackjack players.. I know.
  ***********************************************************************/
import java.util.List;

//enum Action { S, H, D, P, BUST, BLACKJACK; }

public class BlackjackPlayer implements Player
{
    private Strategy strategy;
    private double money;
    private Type type;
    private int bet;
    private int betamount;
    private int won;
    private int lost;
    private double[] pos;
    private double[] neg;
    private double zero;
    private int ltc; // true count when bet was made.
    
    // should make a class called Rules which passes on Rules
    public BlackjackPlayer(Type strat, boolean ho17, boolean das,
                           int money, int betamount)
    {
        this.money = money;
        this.type = strat;
        this.betamount = betamount;
        
        if (strat == Type.DEALER)
            strategy = new DealerStrategy(ho17);
        else if (strat == Type.BASIC)
            strategy = new BasicStrategy(ho17, das);
    }
    
    // get the current money level
    public double getMoney() { return money; }
    
    // used for tests
    public void clearScore() {money = 0; }
    
    // set the hand
    public void setHand(boolean split, List<Card> hand, Card dc) 
    { strategy.setHand(split, hand, dc); } // passed on to the strategy to deal with 
    
    // get the current hand
    public List<Card> getHand() { return strategy.getHand(); }
    
    // get the next action
    public int nextAction() { return strategy.nextPlay(); } // passed on to the strategy to deal with
    
    // hit me!
    public void hit(Card c) { strategy.hit(c); }
    
    // get the current hand value
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
    
    // not used for these players, they don't care what cards are shown, not counting
    public void show(Card c, int cardsleft) {};
    
    // not used for these players, don't care when deck is shuffled
    public void shuffle() {};
    
    // wager a bet
    public void makeBet(boolean split) 
    { 
        bet = betamount; 
        //StdOut.println("Bet = " + bet);
    }
    
    // get the bet currently wagered
    public int getBet() { return bet; }
    
    // win the bet currently wagered
    public void winBet(int b) { 
        money += b; 
        won++;
    }
    
    // lose the bet currently wagered
    public void loseBet(int b) { 
        money -= b; 
        lost++;
    }
    
    // win a blackjack with the current bet wagered (x1.5!)
    public void winBlackjack() { 
        money += (double) bet*1.5; 
        won++;
    }
    
    // double the bet currently wagered
    public void doubleDown() { bet = bet*2; }
    
    // get the number of hands won
    public int getWon() {return won;};
    
    // get the number of hands lost
    public int getLost() {return lost;};
    
    // these are used with this subclass.. probably defeats the purpose of an interface eh?... to research
    public double[] getPos() {return pos;};
    public double[] getNeg() {return neg;};
    public double getZero() {return zero;};
}