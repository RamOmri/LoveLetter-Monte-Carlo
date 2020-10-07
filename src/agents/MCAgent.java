package agents;
import loveletter.*;
import java.util.*;


import loveletter.hypotheticalState;

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
  public String toString(){return "MCAgent";}


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
    int hypotheticalStates = 500;
    hypotheticalState[] states = new hypotheticalState[hypotheticalStates];
     ArrayList<move> moves = new ArrayList<move>();
     hypotheticalState ms = createState(c);
    moves = makeMoveList(c, current.getCard(myIndex), ms);
    if(moves.size() > 0){
                  for(int i = 0; i < moves.size(); i++){
                        moves.get(i).identifier = i;
                      }
        
          if(current.unseenCards().length > 0){
              for(int i = 0; i < hypotheticalStates; i++){
                states[i] = createState(c);
                                                                                                  //for(int j = 0; j < states[i].hand.length; j++)System.out.println("this is state: " + i + " and has hand " + states[i].hand[j] );
                                                                                                  //for(int k = 0; k < states[i].deck.length; k++)System.out.println("deck: " + states[i].deck[k]);
              }
              
                move m = null;
                for(int s = 0; s < states.length; s++){
                          m = MCTS(c, states[s]);
                          moves.get(m.identifier).value++; 
                }
                        for(int i = 0; i < moves.size(); i++){
                          if(m.value < moves.get(i).value){ 
                            m = moves.get(i);
                          }
                        }
                        System.out.println(":::::::::::::::: " + m.identifier + " " + m.value);
                        act = m.act;
          }
          else{
            move m = moves.get(0);
            for(int i = 0; i < moves.size(); i++){
              if(m.act.card.value() < moves.get(i).act.card.value()){
                m = moves.get(i);
              }
            }
            act = m.act;
          }
        }
        else{
          try{
          act = Action.playGuard(myIndex, 3, Card.PRINCESS);
          }catch(IllegalActionException e){
              //  ps.println("Something has gone wrong.");
                e.printStackTrace();
              }

        }
    return act;
  }

  public move MCTS(Card c, hypotheticalState s){
        int iterations = 1000;
        ArrayList<move> moves = new ArrayList<move>();
        moves = makeMoveList(c, current.getCard(myIndex), s);
           for(int i = 0; i < moves.size(); i++){
                  moves.get(i).identifier = i;
                }
        node currentNode = new node();
        currentNode.current = s;
        currentNode.visits = 1;
        currentNode = expand(currentNode, moves);
        node head = currentNode;
        for(int i = 0; i < iterations; i++){
            while(!currentNode.children.isEmpty()){
              if(currentNode.terminal){
                currentNode = currentNode.parent;
                currentNode.children.poll();
              }
              currentNode = currentNode.children.peek();
            }
            if(currentNode.visits > 0 && !currentNode.terminal){
              //this is just a band aid solution and may cause problems later on. Please note that top[0] for some reason keeps on
              //ending up being greater than the length of the deck in terminal nodes and needs to be fixed.
              if(currentNode.current.top[0] >= currentNode.current.deck.length){//System.out.println("111111111111111111111");
                  currentNode.terminal = true;
              }else{//System.out.println("2222222222222222222222222");
                        moves.clear();  
                        moves = makeMoveList(currentNode.current.hand[myIndex], currentNode.current.deck[currentNode.current.top[0]], currentNode.current);
                        currentNode = expand(currentNode, moves);
                                                                                                                                                        /* int ch = 0;
                                                                                                                                                          for(node child : currentNode.children){ ch++;
                                                                                                                                                            for(Card o : child.current.hand){
                                                                                                                                                              System.out.println("Card in child: " + ch + " " + o);
                                                                                                                                                            }
                                                                                                                                                          }*/
                        currentNode.visits++;
                        currentNode = currentNode.children.peek();
                                                    }
              }

              if(currentNode == null){
                currentNode = head;
                continue; //Why the fuck does currentNode end up as null on line 135??
              }
              if(!currentNode.terminal){
                  double simValue = simulation(currentNode);
                  while(currentNode.parent != null){
                    currentNode.parent.children.remove(currentNode);
                    currentNode.value += simValue;
                    currentNode.visits++;
                    currentNode.UCB();
                    currentNode.parent.children.add(currentNode);
                    currentNode = currentNode.parent;
                  } 
              }
              else{
                  while(currentNode.parent != null){
                    currentNode.parent.children.remove(currentNode);
                    currentNode.visits++;
                    currentNode.UCB();
                    currentNode.parent.children.add(currentNode);
                    currentNode = currentNode.parent;
                  }
              }
              
        }
        move m = head.children.peek().m;
      //  move m = nm.m;
      //  System.out.println("1: " + nm.UCB);

       // if(!currentNode.children.isEmpty())
        //System.out.println("2: " + currentNode.children.poll().UCB);
      //  moves.get(moves.indexOf(m)).value++;
        
      //  System.out.println(m.act);
      //for(node t : currentNode.children)
        // System.out.println(t.m.act);
         return m;
  }

  public double simulation(node n){
      
       hypotheticalState gameState = n.current.cloneState();
                  int playerIndex = myIndex;
                  try{
                  hypotheticalState[] playerStates = new hypotheticalState[current.numPlayers()];
                  for(int i = 0; i < current.numPlayers(); i++){
                      playerStates[playerIndex] = gameState.playerState(playerIndex);
                      gameState.agents[playerIndex].myIndex = playerIndex;
                    gameState.agents[playerIndex].current = playerStates[playerIndex];
                      playerIndex++;
                      if(playerIndex == current.numPlayers())playerIndex = 0;
                  }
                  while(!gameState.roundOver()){
                    Card topCard = gameState.drawCard();
                    Action act = gameState.agents[gameState.nextPlayer()].playCard(topCard);
                    gameState.update(act, topCard);
                  }

                }catch(IllegalActionException e){
              //  ps.println("Something has gone wrong.");
                e.printStackTrace();
                return 0.0;
              }
              if(gameState.hand[myIndex] != null)
              return (double)gameState.hand[myIndex].value()*gameState.hand[myIndex].value()*100; 
              else return 1;
  }


  public node expand(node n, ArrayList<move> moves){

      for(move m : moves){
                  node child = new node();
                  child.current = n.current.cloneState();
                  int playerIndex = myIndex;
                  try{
                  hypotheticalState[] playerStates = new hypotheticalState[current.numPlayers()];
                  
                  for(int i = 0; i < current.numPlayers(); i++){
                    playerStates[playerIndex] = child.current.playerState(playerIndex);
                    child.current.agents[playerIndex].myIndex = playerIndex;
                    child.current.agents[playerIndex].current = playerStates[playerIndex];
                    playerIndex++;
                    if(playerIndex == current.numPlayers())playerIndex = 0;
                  }

                   int i = 0;
                    child.current.nextPlayer[0] = myIndex;
                    while(!child.current.roundOver() && i < current.numPlayers()){  
                      i++;
                        Card topCard = child.current.drawCard();  
                        Action act = child.current.agents[child.current.nextPlayer()].playCard(topCard);
                    
                        child.current.update(act, topCard);
                    }
                  }catch(IllegalActionException e){
              //  ps.println("Something has gone wrong.");
                e.printStackTrace();
                return null;
              } 
              child.m = m;
              child.parent = n;
              if(child.current.hand[myIndex] == null)child.terminal = true;
              if(child.current.gameOver())child.terminal = true;
              if(child.current.top[0] == child.current.hand.length) child.terminal = true;

              if(child.terminal == true){
                if(child.current.hand[myIndex] != null)
                child.value += child.current.hand[myIndex].value()*child.current.hand[myIndex].value()*100;
                else child.value = 1;
                child.UCB();
              }
              n.children.add(child);
      }
      
      return n;
  }


  public boolean mustPlayCountess(Card c, Card b){///////////////////////////////////
    if(c.value() == 7 || b.value() == 7){
        if((c.value() > 4 && c.value() != 7) || (b.value() >4 && b.value() !=7)){
            return true;
        }
    }
    return false;
  }///////////////////////////////////////////

  
  public ArrayList<move> makeMoveList(Card c, Card b, hypotheticalState s){//////////////////////////////
      ArrayList<move> list = new ArrayList<>();

      try{
          if(mustPlayCountess(c, b)){ 
            move m = new move(Card.COUNTESS, Action.playCountess(myIndex));
            list.add(m);
            return list;
          }
          if(b.value() == 1 || c.value() == 1){
            int k = 0; 
            for(int i = 0; i < s.numPlayers(); i++){
              if(s.eliminated(i) || i == myIndex || s.handmaid(i)) continue;
              k++;
              for(Card g : s.unseenCards()){
                   if(g.value() == 1)continue;
                    move m = new move(Card.GUARD, Action.playGuard(myIndex, i, Card.values()[g.value() - 1]));
                    list.add(m);
              }
            }
            if(k ==0){
              move m = new move(Card.GUARD, Action.playGuard(myIndex, 0, Card.values()[6]));
              list.add(m);
            }
          }
          if(b.value() == 2 || c.value() == 2){ 
            move m = null;
            for(int i = 0; i < s.numPlayers(); i++){
              if(s.eliminated(i) || i == myIndex || s.handmaid(i))continue;
              m = new move(Card.PRIEST, Action.playPriest(myIndex, i));
              list.add(m);
            }
          }
          if(b.value() == 3 || c.value() == 3){ 
            for(int i = 0; i < s.numPlayers(); i++){
              if(i == myIndex || s.eliminated(i)) continue;
              move m = new move(Card.BARON, Action.playBaron(myIndex, i));
              list.add(m);
            }
          }
          if(b.value() == 4 || c.value() == 4){ 
            move m = new move(Card.HANDMAID, Action.playHandmaid(myIndex));
            list.add(m);
          }
          if(b.value() == 5 || c.value() == 5){ 
            move m;
            if(c.value() != 8 && b.value() != 8){
              m = new move(Card.PRINCE, Action.playPrince(myIndex, myIndex));
                list.add(m);
            }
              for(int i = 0; i < s.numPlayers(); i++){
                if(!s.eliminated(i) && !s.handmaid(i)){
                    m = new move(Card.PRINCE, Action.playPrince(myIndex, i));
                    list.add(m);
                }
              }
          }
        if(b.value() == 6 || c.value() == 6){ 
          for(int i = 0; i < s.numPlayers(); i++){
            if(!s.eliminated(i) && i != myIndex){
              move m = new move(Card.KING, Action.playKing(myIndex, i));
              list.add(m);
            }
          }
        }
        if(b.value() == 7 || c.value() == 7){
          move m = new move(Card.COUNTESS, Action.playCountess(myIndex));
          list.add(m);
        }
    }
      catch(IllegalActionException e){
            System.out.println("Made illegal move!!!!!!!!!!!!");
      } 

      return list;
  }/////////////////////////////
  


  public hypotheticalState createState(Card c){
    Random r = new Random();
    simAgent[] agents = new simAgent[current.numPlayers()];
    for(int a = 0; a < current.numPlayers(); a++) agents[a] = new simAgent();

    ArrayList<Card> deck = new ArrayList<Card>(Arrays.asList(current.unseenCards()));
    Card[] playerCards = new Card[current.numPlayers()];
    Collections.shuffle(deck);


    deck.remove(c);
    deck.remove(current.getCard(myIndex));
    int de = 0;
    for(int i = 0; i < current.numPlayers(); i++){   //System.out.println(de + " " + deck.size() + " " + current.unseenCards().length);
      if(!current.eliminated(i) && i != myIndex){
        playerCards[i] = deck.get(0);
        deck.remove(0);
        //de++;
      }
      else playerCards[i] = null;
    }

    playerCards[myIndex] = current.getCard(myIndex);
    deck.add(0, c);

    Card[] d = new Card[deck.size()];
    for(int j = 0; j < d.length; j++){
      d[j] = deck.get(j);
    }
   
  hypotheticalState s = new hypotheticalState(r, agents, playerCards, d);
  s.nextPlayer[0] = myIndex;
  for(int i = 0; i < current.numPlayers(); i++){
    s.handmaid[i] = current.handmaid(i);
  }
    return s;
  }




            public class move{
              public int value;
                public Card c;
                public Action act;
                public int identifier;

                public move(Card a, Action test1) 
                {
                  this.value = 0;
                  this.c = a;
                  this.act = test1;
                
                }
            }


            public class node implements Comparable<node>
            {
                hypotheticalState current;
                move m;
                node parent;
                double UCB;
                int visits;
                double value;
                PriorityQueue<node> children;
                boolean terminal;
                public node(){
                    visits = 0;
                    children = new PriorityQueue<node>();
                    this.current = null;
                    terminal = false;
                }
                public void UCB(){
                      UCB = value/visits + 5000*Math.sqrt((Math.log(parent.visits + 1)/visits));
                }

                public int compareTo(node other){
                    double thisUCB = -100000 * this.UCB;
                    double otherUCB = 100000 * other.UCB;
                      if(this.visits == 0) return (int)thisUCB;
                      if(other.visits == 0) return (int)otherUCB;
                      if(otherUCB > thisUCB) return (int)otherUCB;
                      if(other.UCB == this.UCB) return (int)0;
                      return (int)thisUCB;
             
                    }
            }
}


