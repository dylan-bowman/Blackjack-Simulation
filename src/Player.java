/************************************************************************
  * Interface: Player
  * Name: Dylan Bowman
  * 
  * Interface for a Player object. Supplies common methods to all Players.
  ***********************************************************************/

import java.util.List;

public interface Player
{
    //                   2  3  4  5  6  7  8  9  10   A
    final int[] hilo = { 1, 1, 1, 1, 1, 0, 0, 0, -1, -1 };
    final int[] ko   = { 1, 1, 1, 1, 1, 1, 0, 0, -1, -1 };
    final int[] zen  = { 1, 1, 2, 2, 2, 1, 0, 0, -2, -1 };
    
    // gets the player's money
    double getMoney();
    
    // clear the player's score
    void clearScore();
    
    // set the Player's hand
    void setHand(boolean split, List<Card> hand, Card dc);
    
    // get the Player's hand
    List<Card> getHand();
    
    // process the Player's next action
    int nextAction();
    
    // take another card into the Player's hand
    void hit(Card c);
    
    // only used with card counter player, recognizes the cards shown and adds them to the count
    void show(Card c, int cardsleft);
    
    // only used with card counter player, recognizes when the deck is shuffled and refreshes the count
    void shuffle();
    
    // return the hand value
    int handValue();
    
    // make a bet
    void makeBet(boolean split);
    
    // get the bet made by the Player
    int getBet();
    
    // win the Player's bet
    void winBet(int bet);
    
    // lose the Player's bet
    void loseBet(int bet);
    
    // win a blackjack, usually pays 1.5x bet + get back bet
    void winBlackjack();
    
    // double down the bet
    void doubleDown();
    
    // get # won
    int getWon();
    
    // get # lost
    int getLost();
    
    double[] getPos();
    
    double[] getNeg();
    
    double getZero();
    
}