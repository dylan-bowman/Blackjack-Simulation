/************************************************************************
  * Program: BlackjackTable2
  * Name: Dylan Bowman
  * NetID: dbowman
  * Execution: java BlackjackTable2 
  * Dependencies: List.java, Card.java, System.out.java
  * 
  * UPGRADED VERSION FROM BLACKJACKTABLE. DO NOT USE THAT VERSION.
  * 
  * The blackjack table which controls the simulation.
  ***********************************************************************/
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

enum Type { DEALER, BASIC, HILO, KO, ZEN; }

public class BlackjackTable2
{
    // probably make a seperate enum file for all these
    final int S         = 0; // Stand
    final int H         = 1; // Hit
    final int D         = 2; // Double
    final int P         = 3; // Split
    final int BUST      = 4; // Bust
    final int BLACKJACK = 5; // Blackjack
    
    // array of blackjack players, dealer located at [0]
    public Player[] players; 
    private ArrayList<Card> decks;     // usually 1-8 decks
    private int numDecks;
    private boolean dbj;
    private ArrayList<Card> showlater; // card that is shown after the hand.. i.e. dealer's down card
    private ArrayList[] handValues;
    private ArrayList[] handBets;
    
    private boolean dbug;
    
    // makes a blackjack table from number of decks and array of players, p
    // p is an array of players not including dealer
    public BlackjackTable2(int numDecks, Type[] p, int[] money,
                           int[] bets)
    {
        //rules
        boolean ho17 = false;
        boolean das = true;
        
        dbug = false;
        this.numDecks = numDecks;
        decks = Card.newDecks(numDecks);
        Collections.shuffle(decks);
        /*for (Card card : decks)
         {
         System.out.println(card + " " + card.value());
         }*/
        showlater = new ArrayList<Card>();
        
        // initialize arrays
        int N = p.length+1;
        players = new Player[N];
        handValues = new ArrayList[N];
        for (int i = 0; i < N; i++)
        {
            handValues[i] = new ArrayList<Integer>();
        }
        handBets = new ArrayList[N];
        for (int i = 0; i < N; i++)
        {
            handBets[i] = new ArrayList<Integer>();
        }
        
        // initialize players and dealer
        for (int i = 0; i < N-1; i++)
        {
            if (p[i] == Type.BASIC)
                players[i] = new BlackjackPlayer(p[i], ho17, das, money[i], bets[0]);
            else
                players[i] = new CardCounter(p[i], ho17, das,
                                             money[i], bets);
        }
        players[N-1] = new BlackjackPlayer(Type.DEALER, ho17, das, 0, bets[0]);
    }
    
    // start the simulation for the given number of hands and debug on or off
    public void playHands(int numHands, boolean db)
    {
        this.dbug = db;
        int count = 0;
        int cut = decks.size()/4; // level at which the deck will be shuffled
        
        // play the hand
        while (count != numHands)
        {
            while (decks.size() > cut && count != numHands)
            {
                if (dbug)
                    System.out.println("Hand " + count);
                playHand();
                if (dbug)
                    System.out.println();
                count++;
            }
            
            // shuffle if below the cut
            decks = Card.newDecks(numDecks);
            if (dbug)
            {
                System.out.println("shuffle");
                System.out.println();
            }
            Collections.shuffle(decks);
            for (int i = 0; i < players.length-1; i++)
            {
                players[i].shuffle();
            }
        }
    }
    
    // play one hand in the simulation
    private void playHand()
    {
   	 // if the cards in the deck are lower than 24, shuffle midhand
        if (decks.size() < 24)
        {
            decks = Card.newDecks(numDecks);
            if (dbug)
            {
                System.out.println("shuffle");
                System.out.println();
            }
            Collections.shuffle(decks);
            for (int i = 0; i < players.length-1; i++)
            {
                players[i].shuffle();
            }
        }
        
        int dealer = players.length-1; // index of dealer in players
        
        // make bets
        for (int i = 0; i < dealer; i++)
            players[i].makeBet(false);
        
        // deal cards
        Card dc = dealCards();
        
        // if dealer gets blackjack
        if (players[dealer].handValue() == 21)
        {
            if (dbug)
                System.out.println("Dealer Blackjack");
            
            for (int i = 0; i < players.length-1; i++)
            {
                // if player does not have blackjack, he loses
                if (players[i].handValue() != 21)
                {
                    int bet = players[i].getBet();
                    players[i].loseBet(bet);
                    if (dbug)
                    {
                        System.out.println("Player " + i + " loses b/c dbj");
                        System.out.println("money now = " + players[i].getMoney());
                    }
                }
                // else he pushes with dealer, nothing happens
                else
                {
                    if (dbug)
                    {
                        System.out.println("Player " + i + "pushes w/dbj");
                        System.out.println("money now = " + players[i].getMoney());
                    }
                }
            }
            
            return;
        }
        
        // each player's turn now
        for (int i = 0; i < players.length; i++)
        {
            //if (dbug)
            //System.out.println("dc = " + dc);
            
            if (playerActs(i, dc))
                continue;
            
            handValues[i].add(players[i].handValue());
            handBets[i].add(players[i].getBet());
        }
        results();
        showlater(decks.size());
    }
    
    private void results()
    {
        // find dealer's result
        int value = players[players.length-1].handValue();
        int dealval;
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
        
        // compute results for other players
        int bet;
        for (int i = 0; i < players.length-1; i++)
        {
            while (!handValues[i].isEmpty())
            {
                value = (Integer) handValues[i].remove(0);
                bet   = (Integer) handBets[i].remove(0);
                
                // if a bust
                if (value > 21)
                {
                    players[i].loseBet(bet);
                    if (dbug)
                    {
                        System.out.println("Player " + i + " busts");
                        System.out.println("money now = " + players[i].getMoney());
                    }
                }
                else
                {
               	 // if beat the dealer's value = win
                    if (value > dealval)
                    {
                        players[i].winBet(bet);
                        if (dbug)
                        {
                            System.out.println("Player " + i + 
                                           " wins bet with value: " + value);
                            System.out.println("money now = " + players[i].getMoney());
                        }
                    }
                    // if lost to dealer's value = lose
                    else if (value < dealval)
                    {
                        players[i].loseBet(bet);
                        if (dbug)
                        {
                            System.out.println("Player " + i
                                               + " loses bet with value: "
                                               + value);
                            System.out.println("money now = " + players[i].getMoney());
                        }
                    }
                    // else push
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
        }
    }
    
    // returns true when done, false if still going; recursive.
    private boolean playerActs(int i, Card dc)
    {
        int action;
        int count = 0;
        while ((action = players[i].nextAction()) != S && action != BUST)
        {
            //if (dbug)
            //System.out.println("Player " + i + "'s action: " + action);
            
            // if player gets blackjack (dealer didnt have it either)
            if (action == BLACKJACK)
            {
                // don't need to get bets, because blackjack can't happen after split
                players[i].winBlackjack();
                
                if (dbug)
                {
                    System.out.println("Player " + i + " blackjack");
                    System.out.println("money now = " + players[i].getMoney());
                }
                return true;
            }
            // if player wants to double down and its his first action
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
            // if player wants to hit (or double but not first action)
            else if (action == H || action == D)
            {
                Card next = decks.remove(0);
                show(next, decks.size());
                if (dbug)
                    System.out.println("Player " + i + " hits a " + next);
                players[i].hit(next);
            }
            // if player wants to split
            else if (action == P)
            {
                if (dbug)
                    System.out.println("Player " + i + " splits");
                playSplit(i, dc);
                return true;
            }
            count++;
        }
        // if (dbug)
        //System.out.println("Player " + i + "'s action: " + action);
        
        return false;
    }
    
    // process the split, players now has two hands.. should player have struct that can hold multiple hands? yes
    private void playSplit(int i,Card dc)
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
        
        // play out this separate hand first, since don't control it after this method call
        if (!playerActs(i,dc))
        {
            handBets[i].add(players[i].getBet());
            handValues[i].add(players[i].handValue());
        }
        
        players[i].makeBet(true);
        ArrayList<Card> newhand2 = new ArrayList<Card>();
        newhand2.add(second);
        nc = decks.remove(0);
        newhand2.add(nc);
        if (dbug)
            System.out.println("Player " + i + "'s second hand: " + second
                               + " and " + nc);
        show(nc, decks.size());
        // dont need to make a new bet b/c result structure takes care of it
        // split second bet will be same as first
        players[i].setHand(true, newhand2, dc);
        
        // play out original hand
        if (!playerActs(i, dc))
        {
            handBets[i].add(players[i].getBet());
            handValues[i].add(players[i].handValue());
        }
    }
    
    // deal the cards
    private Card dealCards()
    {
        int dealer = players.length-1;
        Card dc;
        
        // deal dealer's hand's first for simplicity & dc
        Card one = decks.remove(0);
        Card two = decks.remove(0);
        if (dbug)
            System.out.println("Dealer's cards are " + one
                               + " and " + two);
        show(one, decks.size());
        showlater.add(two);
        dc = one;
        ArrayList<Card> hand = new ArrayList<Card>();
        hand.add(one);
        hand.add(two);
        players[players.length-1].setHand(false, hand, dc);
        
        // deal rest of player's hands
        for (int i = 0; i < players.length-1; i++)
        {
            one = decks.remove(0);
            two = decks.remove(0);
            
            if (dbug)
                System.out.println("Player " + i + "'s cards are "
                                   + one + " and " + two);
            show(one, decks.size());
            show(two, decks.size());
            
            hand = new ArrayList<Card>();
            hand.add(one);
            hand.add(two);
            
            players[i].setHand(false, hand, dc);
        }
        
        return dc;
    }
    
    // method to show card to card counters
    private void show(Card c, int cardsleft)
    {
        for (int i = 0; i < players.length-1; i++)
        {
            players[i].show(c, cardsleft);
        }
    }
    
    // show these cards after the current hand is complete, more about when its called..
    private void showlater(int cardsleft)
    {
        while (!showlater.isEmpty())
        {
            Card c = showlater.remove(0);
            for (int i = 0; i < players.length-1; i++)
            {
                players[i].show(c, cardsleft);
            }
        }
    }
    
    // get the score of player i
    public double getScore(int i)
    {
        return players[i].getMoney();
    }
    
    // clear the score of player i
    public void clearScore(int i)
    {
        players[i].clearScore();
    }
    
    // print the score
    public void printScore(int N)
    {
        for (int i = 0; i < players.length-1; i++)
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
    
    // print the distribution of player i
    public void printDistribution(int i)
    {
        double[] pos = players[i].getPos();
        double[] neg = players[i].getNeg();
        double zero = players[i].getZero();
        
        System.out.println("Distribution of player " + i);
        System.out.println("-12 and greater: " + neg[11]);
        for (int j = 10; j >= 0; --j)
        {
            System.out.println("-" + (j+1) + ": " + neg[j]);
        }
        System.out.println("0: " + zero);
        for (int j = 0; j < 11; j++)
        {
            System.out.println("+" + (j+1) + ": " + pos[j]);
        }
        System.out.println("+12 and greater: " + pos[11]);
    }
    
    // play a test hand
    public void testHand(ArrayList<Card> dealerHand, 
                         ArrayList<Card> hand, 
                         ArrayList<Card> deck)
    {
        dbug = true;
        decks = deck;
        Card dc = dealerHand.get(0);
        int dealval = 0;
        players[0].makeBet(false);
        players[0].setHand(false, hand, dc);
        //System.out.println("Player 0's cards are " + hand.get(0) 
                           //+ " and " + hand.get(1));
        //System.out.println("dc = " + dc);
        if (!playerActs(0, dc))
        {
            handValues[0].add(players[0].handValue());
            handBets[0].add(players[0].getBet());
        }
        
        
        players[1].setHand(false, dealerHand, dc);
        //System.out.println("Dealer's cards are " + dealerHand.get(0)
                           //+ " and " + dealerHand.get(1));
        //System.out.println("dc = " + dc);
        if (!playerActs(1, dc))
        {
            handValues[1].add(players[1].handValue());
            handBets[1].add(players[1].getBet());
        }
        results();
    }
    
    // main simulation here
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
        
        /*Type[] p = {Type.BASIC};
        int[] money = { 0 };
        int[] bets = { 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        BlackjackTable2 bjt = new BlackjackTable2(6, p, money, bets);
        ArrayList<Card> dh = new ArrayList<Card>();
        ArrayList<Card> ph = new ArrayList<Card>();
        ArrayList<Card> deck = new ArrayList<Card>(); 
        
        // test for can't split more than 3 times
        dh.add(c2);
        dh.add(c3);
        ph.add(c6);
        ph.add(c6);
        // split
        // fh = 10, 6
        deck.add(c10);
        // stay
        // split again
        deck.add(c6);
        // fh = A, 6
        deck.add(cA);
        // hit a 9, S
        deck.add(c9);
        // sh = 6, 6
        // split again
        deck.add(c6);
        // fh = 6, 6
        // don't split again
        deck.add(c6);
        // hit a 8, stay
        deck.add(c8);
        // sh = 6, J
        deck.add(cJ);
        // Stay
        deck.add(cJ);
        // dealer hits J
        deck.add(cK);
        // dealer hits K, busts
        bjt.testHand(dh, ph, deck);
        if (bjt.getScore(0) != 4.0)
            System.out.println("0");
        
        bjt.clearScore(0);
        dh.clear();
        ph.clear();
        deck.clear();
        ph.add(c6);
        ph.add(c5);
        dh.add(c4);
        dh.add(c6);
        deck.add(c10);
        deck.add(c9);
        bjt.testHand(dh, ph, deck);
        System.out.println(bjt.getScore(0));
        if (bjt.getScore(0) != 2.0)
            System.out.println("1");
        
        bjt.clearScore(0);
        dh.clear();
        ph.clear();
        deck.clear();
        ph.add(c6);
        ph.add(c6);
        dh.add(c5);
        dh.add(c6);
        // fh = 6, 5
        deck.add(c5);
        // double down Q
        deck.add(cQ);
        // sh = 6, 10
        deck.add(c10);
        // stay
        deck.add(c4);
        deck.add(c9);
        // dealer busts
        bjt.testHand(dh, ph, deck);
        System.out.println(bjt.getScore(0));
        if (bjt.getScore(0) != 3.0)
            System.out.println("2");*/
        
        
        Type[] p = { Type.BASIC, Type.HILO };
         int[] money = { 500000, 500000 };
         int[] bets = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
         //int[] bets = { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, };
         //int[] bets = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
         BlackjackTable2 bjt = new BlackjackTable2(6, p, money, bets);
         bjt.playHands(1000000, false);
         bjt.printScore(1000000);
         bjt.printDistribution(1);
         //System.out.println(bjt.dbjdbug);
         //System.out.println(bjt.interesting);
    }
}