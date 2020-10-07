package agents;
import loveletter.*;
import java.util.*;
import javax.smartcardio.Card;
import loveletter.hypotheticalState;;

/**
 * An interface for representing an agent in the game Love Letter
 * All agent's must have a 0 parameter constructor
 * */
public class MCAgent implements Agent{

 
  private Random rand;
  private State current;
  private int myIndex;

  //0 place default constructor
  public MCAgent(){
    rand  = new Random();
  }

  /**
   * Reports the agents name
   * */
  public String toString(){return "Rando";}


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
  public Action playCard(Card c){
    Action act = null;
    Card play;
    while(!current.legalAction(act, c)){
      if(rand.nextDouble()<0.5) play= c;
      else play = current.getCard(myIndex);
      int target = rand.nextInt(current.numPlayers());
      try{
        switch(play){
          case GUARD:
            act = Action.playGuard(myIndex, target, Card.values()[rand.nextInt(7)+1]);
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
      }catch(IllegalActionException e){/*do nothing*/}
    }
    return act;
  }















  /**
   * Perform an action after drawing a card from the deck
   * @param c the card drawn from the deck
   * @return the action the agent chooses to perform
   * @throws IllegalActionException when the Action produced is not legal.
   * 
  public Action playCard(Card c){
    Action act = null;
    Card play;
    int hypotheticalStates = 3;
    hypotheticalState[] states = new hypotheticalState[hypotheticalStates];

    for(int i = 0; i < hypotheticalStates; i++){
      states[i] = createState(c);
    }


   for(hypotheticalState s : states){
        
   }

    return act;
  }

 /* public hypotheticalState createState(Card c){
    Random r = new Random();
    Agent[] agents = new Agent[4];
    hypotheticalState s = new hypotheticalState(r, agents);

    ArrayList<Card> deck = new ArrayList<Card>();
    Card[] playerCards = new Card[current.numPlayers()];
    deck = Arrays.asList(current.unseenCards());
    Collections.shuffle(deck);
    playerCards[myIndex] = current.getCard(myIndex);
    deck.remove(i);

    for(int i = 0; i < current.numPlayers(); i++){
      if(!current.eliminated(i) && i != myIndex){
        playerCards[i] = deck(i);
        deck.remove(i);
      }
    }

    
    return s;
  }*/





}


