/************************************************************************
  * Program: BlackjackDistribution
  * Name: Dylan Bowman
  * Execution: java BlackjackDistribution numberRuns numberDecks
  * 
  * used to run an entire blackjack simulation and print results neatly.
  * MUST BE SUPPLIED TWO INT ARGUMENTS TO BE SUCCESSFUL.
  ***********************************************************************/
//import BlackjackTable2;

public class BlackjackDistribution
{
    public static void main(String[] args)
    {
   	  if (args.length == 0) {
   		  System.out.println("Must supply numberRuns and numberDecks as args.");
   		  return;
   	  }
   	 
   	  int numberRuns  = Integer.parseInt(args[0]); // number of runs playing 1,000,000 hands
		  int numberDecks = Integer.parseInt(args[1]); // number of decks in each simulation
        
        double[] pos = new double[12];
        double[] neg = new double[12];
        double zero  = 0;
        
        double basic   = 0;
        double counter = 0;
        
        Type[] players    = { Type.BASIC, Type.HILO };
        int[] money = { 500000, 500000 };
        //int[] bets  = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
        int[] bets = {1, 1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13};
        
        // run numberRuns # of simulations with 1,000,000 hands each
        for (int i = 0; i < numberRuns; i++)
        {
            BlackjackTable2 bjt = new BlackjackTable2(numberDecks, players, money, bets);
            bjt.playHands(1000000, false);
            basic   += (bjt.players[0].getMoney()/1000000);
            counter += (bjt.players[1].getMoney()/1000000);
            double[] p = bjt.players[1].getPos();
            double[] n = bjt.players[1].getNeg();
            double z = bjt.players[1].getZero();
            for (int j = 0; j < 12; j++)
            {
                pos[j] += p[j];
            }
            for (int j = 0; j < 12; j++)
            {
                neg[j] += n[j];
            }
            zero += z;
        }
        
        // calculate distributions and results
        basic   = basic/numberRuns;
        counter = counter/numberRuns;
        for (int j = 0; j < 12; j++)
        {
            pos[j] = pos[j]/numberRuns;
        }
        for (int j = 0; j < 12; j++)
        {
            neg[j] = neg[j]/numberRuns;
        }
        zero = zero/numberRuns;
        
        // print results
        System.out.println("Average basic %: " + basic);
        System.out.println("Average counting %: " + counter);
        System.out.println("Average Distribution of card counting player:");
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
}