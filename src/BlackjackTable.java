/************************************************************************
  * Program: BlackjackTable
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java BlackjackTable 
  * Dependencies: List.java, Card.java, System.out.java
  * 
  * Dealer Strategy for blackjack.
  * 
  * DEPRECATED - USE BLACKJACKTABLE2
  ***********************************************************************/
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

//enum Type { DEALER, BASIC, HILO, KO, ZEN; }


// DEPRECATED PLEASE USE BLACKJACKTABLE2
public class BlackjackTable
{
    // probably make a seperate enum file for all these
    final int S         = 0; // Stand
    final int H         = 1; // Hit
    final int D         = 2; // Double
    final int P         = 3; // Split
    final int BUST      = 4; // Bust
    final int BLACKJACK = 5; // Blackjack
    
    // array of blackjack players, dealer located at [0]
    private Player[] players; 
    private ArrayList<Card> decks;     // usually 1-8 decks
    private int numDecks;
    private boolean dbj;
    private ArrayList<Card> showlater;
    public boolean dbjdbug;
    public boolean interesting;
    
    // makes a blackjack table from number of decks and array of players, p
    // p is an array of players not including dealer
    public BlackjackTable(int numDecks, Type[] p, int[] money,
                          int[] bets)
    {
        this.numDecks = numDecks;
        decks = Card.newDecks(numDecks);
        Collections.shuffle(decks);
        /*for (Card card : decks)
         {
         System.out.println(card + " " + card.value());
         }*/
        showlater = new ArrayList<Card>();
        
        int N = p.length+1;
        players = new Player[N];
        
        for (int i = 1; i < N; i++)
        {
            if (p[i-1] == Type.BASIC)
                players[i] = new BlackjackPlayer(p[i-1], false, false, money[i-1], bets[0]);
            else
                players[i] = new CardCounter(p[i-1], false, false, 
                                             money[i-1], bets);
        }
        players[0] = new BlackjackPlayer(Type.DEALER, false, false, 0, bets[0]);
    }
    
    public void playHands(int numHands, boolean dbug)
    {
        int count = 0;
        int cut = decks.size()/4;
        while (count != numHands)
        {
            while (decks.size() > cut && count != numHands)
            {
                if (dbug)
                    System.out.println("Hand " + count);
                playHand(dbug);
                if (dbug)
                    System.out.println();
                count++;
            }
            decks = Card.newDecks(numDecks);
            if (dbug)
            {
                System.out.println("shuffle");
                System.out.println();
            }
            Collections.shuffle(decks);
            for (int i = 1; i < players.length; i++)
            {
                players[i].shuffle();
            }
        }
    }
    
    private void playHand(boolean dbug)
    {
        dbj = false;
        Card dc = null;
        int dealval = 0; // value of dealers hand
        for (int i = 0; i < players.length; i++)
        {
            Card one = decks.remove(0);
            Card two = decks.remove(0);
            
            
            if (i == 0)
            {
                if (dbug)
                    System.out.println("Dealer's cards are " + one
                                       + " and " + two);
                showlater.add(one);
                showlater.add(two);
            }
            else
            {
                if (dbug)
                    System.out.println("Player " + i + "'s cards are "
                                       + one + " and " + two);
                show(one, decks.size());
                show(two, decks.size());
            }
            
            ArrayList<Card> hand = new ArrayList<Card>();
            hand.add(one);
            hand.add(two);
            
            if (i == 0)
            {
                dc = one;
                players[0].setHand(false, hand, dc);
            }
            else
            {
                players[i].makeBet(false);
                players[i].setHand(false, hand, dc);
            }
            
            //if (dbug)
            //System.out.println("dc = " + dc);
            
            // returns true if blackjack or split occurred, 
            // results of each already handled
            if (dbug && dbj && (one.value() + two.value() == 21))
                interesting = true;
            if (!dbj || (one.value() + two.value() == 21))
            {
                if (playerActs(i, dealval, dc, dbug))
                    continue;
                
                int value = players[i].handValue();
                if (i == 0)
                {
                    if (value > 21)
                    {
                        if (dbug)
                            System.out.println("Dealer busts");
                        dealval = 0;
                    }
                    else
                    {
                        dealval = value;
                        if (dbug)
                            System.out.println("Dealer's value = " + dealval);
                    }
                }
                else
                {
                    result(i, dealval, value, dbug);
                }
            }
            // probably never gets here
            else if (dbug)
            {
                System.out.println("should prob never get here");
                System.out.println("Player " + i + " loses b/c dbj");
                System.out.println("money now = " + players[i].getMoney());
            }
        }
        showlater(decks.size());
    }
    
    private boolean playerActs(int i, int dealval, Card dc, boolean dbug)
    {
        int action;
        int count = 0;
        while ((action = players[i].nextAction()) != S 
                   && action != BUST)
        {
            //if (dbug)
            //System.out.println("Player " + i + "'s action: " + action);
            //System.out.println("is this infinite?");
            if (action == BLACKJACK)
            {
                if (i == 0)
                {
                    for (int j = 1; j < players.length; j++)
                    {
                        int bet = players[j].getBet();
                        players[j].loseBet(bet);
                        if (dbug)
                        {
                            System.out.println("Player " + j + " loses b/c dbj");
                            System.out.println("money now = " + players[j].getMoney());
                        }
                    }
                        
                    // should have something for not playing the
                    // rest of the hand?
                    if (dbug)
                        System.out.println("Dealer Blackjack");
                    dbj = true;
                    dbjdbug = true;
                    return true;
                }
                if (dbj)
                {
                    int bet = players[i].getBet();
                    players[i].winBet(bet);
                    if (dbug)
                    {
                        System.out.println("Player " + i + "pushes w/dbj");
                        System.out.println("money now = " + players[i].getMoney());
                    }
                }
                else
                {
                    players[i].winBlackjack();
                }
                if (dbug)
                {
                    System.out.println("money now = " + players[i].getMoney());
                    System.out.println("Player " + i + " blackjack");
                }
                return true;
            }
            else if (action == D && count == 0)
            {
                players[i].doubleDown();
                Card next = decks.remove(0);
                show(next, decks.size());
                if (dbug)
                    System.out.println("Player " + i + 
                                   " doubles a " + next);
                players[i].hit(next);
                return false;
            }
            else if (action == H || action == D)
            {
                Card next = decks.remove(0);
                if (i == 0)
                    showlater.add(next);
                else
                    show(next, decks.size());
                if (dbug)
                    System.out.println("Player " + i + " hits a " + next);
                players[i].hit(next);
            }
            else if (action == P)
            {
                if (dbug)
                    System.out.println("Player " + i + " splits");
                playSplit(i, dealval, dc, dbug);
                return true;
            }
            count++;
        }
        //System.out.println("Player " + i + "'s action: " + action);
        
        return false;
    }
    
    private void result(int i, int dealval, int value, boolean dbug)
    {
        if (value > 21)
        {
            int bet = players[i].getBet();
            players[i].loseBet(bet);
            if (dbug)
            {
                System.out.println("Player " + i + " busts");
                System.out.println("money now = " + players[i].getMoney());
            }
        }
        else
        {
            if (value > dealval)
            {
                players[i].winBet(10);
                if (dbug)
                {
                    System.out.println("Player " + i + 
                                   " wins bet with value: " + value);
                    System.out.println("money now = " + players[i].getMoney());
                }
            }
            else if (value < dealval)
            {
                players[i].loseBet(10);
                if (dbug)
                {
                    System.out.println("Player " + i
                                       + " loses bet with value: "
                                       + value);
                    System.out.println("money now = " + players[i].getMoney());
                }
            }
            else
            {
                if (dbug)
                {
                    System.out.println("Player " + 
                                   i + " pushes bet with value: " 
                                       + value);
                    System.out.println("money now = " + players[i].getMoney());
                }
            }
        }
    }
    
    private void playSplit(int i, int dealval, Card dc, boolean dbug)
    {
        List<Card> currenthand = players[i].getHand();
        Card second = currenthand.remove(1);
        Card first  = currenthand.remove(0);
        
        ArrayList<Card> newhand1 = new ArrayList<Card>();
        newhand1.add(first);
        Card nc = decks.remove(0);
        newhand1.add(nc);
        show(nc, decks.size());
        if (dbug)
            System.out.println("Player " + i + "'s new card for hand 1: " + nc);
        players[i].setHand(true, newhand1, dc);
        
        if (!playerActs(i, dealval, dc, dbug))
            result(i, dealval, players[i].handValue(), dbug);
        
        ArrayList<Card> newhand2 = new ArrayList<Card>();
        newhand2.add(second);
        nc = decks.remove(0);
        newhand2.add(nc);
        if (dbug)
            System.out.println("Player " + i + "'s second hand: " + second
                               + " and " + nc);
        show(nc, decks.size());
        // really should have a splitBet thing that makes the same bet
        players[i].makeBet(false);
        players[i].setHand(true, newhand2, dc);
        
        if (!playerActs(i, dealval, dc, dbug))
        {
            //System.out.println("gets here");
            result(i, dealval, players[i].handValue(), dbug);
        }
    }
    
    // method to show card to card counters
    private void show(Card c, int cardsleft)
    {
        for (int i = 1; i < players.length; i++)
        {
            players[i].show(c, cardsleft);
        }
    }
    
    private void showlater(int cardsleft)
    {
        while (!showlater.isEmpty())
        {
            Card c = showlater.remove(0);
            for (int i = 1; i < players.length; i++)
            {
                players[i].show(c, cardsleft);
            }
        }
    }
    
    public void printScore(int N)
    {
        for (int i = 1; i < players.length; i++)
        {
            System.out.println("Player " + i + 
                           " money level: " + players[i].getMoney());
        }
        /*int won = players[1].getWon();
         int lost = players[1].getLost();
         System.out.println(won + " hands won");
         System.out.println(lost + " hands lost");
         double pushed = N - (won + lost);
         System.out.println(pushed + " hands pushed");
         System.out.println((won + lost + pushed) + " hands played");
         double wp = (double) won/N;
         System.out.println("Win percentage = " + wp);*/
        
    }
    
    public void testHand(ArrayList<Card> dealerHand, 
                         ArrayList<Card> hand, 
                         ArrayList<Card> deck)
    {
        decks = deck;
        Card dc = null;
        int dealval = 0;
        dc = dealerHand.get(0);
        players[0].setHand(false, dealerHand, dc);
        System.out.println("Dealer's cards are " + dealerHand.get(0)
                           + " and " + dealerHand.get(1));
        System.out.println("dc = " + dc);
        if (!playerActs(0, dealval, dc, true))
        {
            int value = players[0].handValue();
            if (value > 21)
            {
                System.out.println("Dealer busts");
                dealval = 0;
            }
            else
            {
                dealval = value;
                System.out.println("Dealer's value = " + dealval);
            }
        }
        
        players[1].makeBet(false);
        players[1].setHand(false, hand, dc);
        System.out.println("Player 1's cards are " + hand.get(0) 
                           + " and " + hand.get(1));
        System.out.println("dc = " + dc);
        if (!playerActs(1, dealval, dc, true))
        {
            int value = players[1].handValue();
            result(1, dealval, value, true);
        }
    }
    
    // need code that will show cards to card-counting players
    
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
        
        /*Type[] p = {Type.BASIC};
         int[] money = { 0 };
         int[] bets = { 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
         BlackjackTable bjt = new BlackjackTable(4, p, money, bets);
         ArrayList<Card> dh = new ArrayList<Card>();
         ArrayList<Card> ph = new ArrayList<Card>();
         ArrayList<Card> deck = new ArrayList<Card>(); 
         dh.add(c4);
         dh.add(c2);
         deck.add(cQ);
         deck.add(c3);
         ph.add(c1);
         ph.add(c1);
         deck.add(c7);
         deck.add(c1);
         deck.add(c10);
         deck.add(cJ);
         deck.add(cK);
         deck.add(c9);
         deck.add(c2);
         bjt.testHand(dh, ph, deck);
         bjt.printScore(1);*/
        
        Type[] p = { Type.BASIC, Type.HILO };
        int[] money = { 500000, 500000 };
        int[] bets = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
        //int[] bets = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        //int[] bets = { 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, };
        BlackjackTable bjt = new BlackjackTable(6, p, money, bets);
        bjt.playHands(1000000, false);
        bjt.printScore(1000000);
        //System.out.println(bjt.dbjdbug);
        //System.out.println(bjt.interesting);
    }
}