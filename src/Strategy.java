/************************************************************************
  * Interface: Strategy
  * Name: Dylan Bowman
  * 
  * Interface for a Strategy object. Supplies common methods to all Strategies.
  ***********************************************************************/

import java.util.List;

public interface Strategy
{
    // probably make a seperate enum file for all these
    final int S         = 0; // Stand
    final int H         = 1; // Hit
    final int D         = 2; // Double
    final int P         = 3; // Split
    final int BUST      = 4; // Bust
    final int BLACKJACK = 5; // Blackjack
    
    final int NORMAL    = 0;  // index for normal strategy
    final int ONEACE    = 17; // index for one ace strategy
    final int PAIRS     = 26; // index for pairs strategy
    
    // Basic strategy with the following rules:
    // Dealer stands on soft 17, doubling after split is allowed,
    // resplitting aces is allowed once, 
    final int[][] basicstrategy = {
       // Dealer's Card Showing Value     vvv Your Hand Value
       //2  3  4  5  6  7  8  9 10  A
        {H, H, H, H, H, H, H, H, H, H}, // 5 - strategy for normal hand
        {H, H, H, H, H, H, H, H, H, H}, // 6
        {H, H, H, H, H, H, H, H, H, H}, // 7
        {H, H, H, H, H, H, H, H, H, H}, // 8
        {H, D, D, D, D, H, H, H, H, H}, // 9
        {D, D, D, D, D, D, D, D, H, H}, // 10
        {D, D, D, D, D, D, D, D, D, H}, // 11
        {H, H, S, S, S, H, H, H, H, H}, // 12
        {S, S, S, S, S, H, H, H, H, H}, // 13
        {S, S, S, S, S, H, H, H, H, H}, // 14
        {S, S, S, S, S, H, H, H, H, H}, // 15
        {S, S, S, S, S, H, H, H, H, H}, // 16
        {S, S, S, S, S, S, S, S, S, S}, // 17
        {S, S, S, S, S, S, S, S, S, S}, // 18
        {S, S, S, S, S, S, S, S, S, S}, // 19
        {S, S, S, S, S, S, S, S, S, S}, // 20
        {S, S, S, S, S, S, S, S, S, S}, // 21
        {H, H, H, D, D, H, H, H, H, H}, // A/2 - strategy for one ace in hand
        {H, H, H, D, D, H, H, H, H, H}, // A/3
        {H, H, D, D, D, H, H, H, H, H}, // A/4
        {H, H, D, D, D, H, H, H, H, H}, // A/5
        {H, D, D, D, D, H, H, H, H, H}, // A/6
        {S, D, D, D, D, S, S, H, H, H}, // A/7
        {S, S, S, S, S, S, S, S, S, S}, // A/8
        {S, S, S, S, S, S, S, S, S, S}, // A/9
        {S, S, S, S, S, S, S, S, S, S}, // A/10
        {P, P, P, P, P, P, H, H, H, H}, // 2/2 - strategy for pairs
        {P, P, P, P, P, P, H, H, H, H}, // 3/3
        {H, H, H, P, P, H, H, H, H, H}, // 4/4
        {D, D, D, D, D, D, D, D, H, H}, // 5/5
        {P, P, P, P, P, H, H, H, H, H}, // 6/6
        {P, P, P, P, P, P, H, H, H, H}, // 7/7
        {P, P, P, P, P, P, P, P, P, P}, // 8/8
        {P, P, P, P, P, S, P, P, S, S}, // 9/9
        {S, S, S, S, S, S, S, S, S, S}, // 10/10
        {P, P, P, P, P, P, P, P, P, P}  // A/A
    };
    
    // get the current hand
    List<Card> getHand();
    
    // set the current hand
    void setHand(boolean split, List<Card> hand, Card dealerCard);
    
    // process the next play
    int nextPlay();
    
    // hit me!
    void hit(Card c);
}