package agents;
import loveletter.*;
import java.util.*;

/**
 * An interface for representing an agent in the game Love Letter
 * All agent's must have a 0 parameter constructor
 * */
public class ReflexAgent implements Agent{

  private Random rand;
  private State current;
  private int myIndex;
  private int t;

  //0 place default constructor
  public ReflexAgent(){
    rand  = new Random();
   // t = 0;
  }

  /**
   * Reports the agents name
   * */
  public String toString(){return "ReflexAgent";}


  /**
   * Method called at the start of a round
   * @param start the starting state of the round
   **/
  public void newRound(State start){
    current = start;
    myIndex = current.getPlayerIndex();
  }

  /**
   * Method called when any agent performs an action. 
   * @param act the action an agent performs
   * @param results the state of play the agent is able to observe.
   * **/
  public void see(Action act, State results){
    current = results;
  }

  /**
   * Perform an action after drawing a card from the deck
   * @param c the card drawn from the deck
   * @return the action the agent chooses to perform
   * @throws IllegalActionException when the Action produced is not legal.
   * */
  public int guess(){
      int g = 5;
   ArrayList<Card> guesses = new ArrayList<Card>(Arrays.asList(current.unseenCards()));

        if(guesses.contains(Card.PRINCESS)) g = 7;
        if(guesses.contains(Card.PRIEST)) g = 1;
        if(guesses.contains(Card.KING)) g = 5;
        if(guesses.contains(Card.HANDMAID)) g = 3;
        if(guesses.contains(Card.COUNTESS)) g = 6;
        if(guesses.contains(Card.BARON)) g = 2;
        if(guesses.contains(Card.PRINCE)) g = 4;

    return g;

  }
   public int choosetarget(){
     int target = 0;
        int i = 0;
        int j = 0;

        while(current.eliminated(target) || target == myIndex || current.handmaid(target)){
             target++;
                    if(target == current.numPlayers()){
                        i++;
                        target = 0;
                    }
                    if(i == 2){
                        target = -1;
                        break;
                    }
                }
                
                return target;
   }

  public Action playCard(Card c){
    Action act = null;
    Card play = null;
   int target = 0;
   int g = 5;

      //  System.outSystem.out.println("my cards::::::::::::: " + current.getCard(myIndex) + c);
        if(c.value() <= current.getCard(myIndex).value()) play= c;
        else play = current.getCard(myIndex);
        
        if(c.value() == 7 || current.getCard(myIndex).value() == 7){
            if(c.value() >= 5 && c.value() != 7) play = current.getCard(myIndex);
            if(current.getCard(myIndex).value()>= 5 && current.getCard(myIndex).value() != 7) play = c;
        } 


        
            if(play.value() != 4)
              target = choosetarget();
              
              //System.out.println(target);
            if(target == -1 && play.value() != 5){
                if(0 == myIndex) target = 1;
                else target = 0;
            }
            else if(play.value() == 5 && target == -1){
                target = myIndex;
            }
        
            /* if(target == myIndex && play.value() != 4){
                 if(c.value() >= 4 && c.value() != 6) play = c;
                 else play = current.getCard(myIndex);
             }*/
             if(play.value() == 1) g = guess();
          //  System.out.println("target:" + target);
           // System.out.println("card: " + play.value());
         //   System.out.println("guess:::::::::" + g);
        try{
            switch(play){
            case GUARD:
                act = Action.playGuard(myIndex, target, Card.values()[g]);
                break;
            case PRIEST:
                act = Action.playPriest(myIndex, target);
                break;
            case BARON:  
                act = Action.playBaron(myIndex, target);
                break;
            case HANDMAID:
                act = Action.playHandmaid(myIndex);
                break;
            case PRINCE:  
                act = Action.playPrince(myIndex, target);
                break;
            case KING:
                act = Action.playKing(myIndex, target);
                break;
            case COUNTESS:
                act = Action.playCountess(myIndex);
                break;
            default:
                act = null;//never play princess
            }
        }catch(IllegalActionException e){
            System.out.println("Made illegal move!!!!!!!!!!!!");
            }  
        //System.out.println(act);
        return act;
        }
    
}


