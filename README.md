# LoveLetter-Monte-Carlo
The implementation of the monte-carlo algorithm used to make intelligent decisions in the game loveletter

This was a univeristy project in which we were required to create an agen capable of playing the game loveletter

the rules for the game can be found here: https://www.ultraboardgames.com/love-letter/game-rules.php

To create an intelligent agent, I have chosen to implement the monte carlo algorithm. The professor created a simulator
and agents that make moves at random. The MCagent which implements the monte carlo algorithm was found to perform better than
the randomagents, yet not so well when put up against a "reflex agent" that utilizes a simple strategy and does not consider
future or past game states.

to run the code download into a local directory. navigate to the root directory and call javac loveletter/LoveLetter.java . 
Then after compilation is successful call java loveletter/LoveLetter to run the simulation. To make the monte-carlo agent 
competent, a high value of iterations were necessary to explore enough gamestates. Therefore running the 100 simulations 
necessary to observe a significant improvement in the MCagent over the random agents. You can reduce the number of games 
played by going into loveletter/LoveLetter.java and on line 50 change the following code:

    for(int g = 0; g < 100; g++)
    
 Into the number of games desired 
 
 Furthermore, more agents can be found in the agents folder. To test different agents simply go into the LoveLetter.js file and
 on line 90 replace the following code:
 
    Agent[] agents = {new agents.RandomAgent(),new agents.RandomAgent(),new agents.RandomAgent() , new agents.MCAgent()};
    
 You can simply replace agents._some_agent_ into any agent you find in the agents folder. For example, try the following code
 to test the MCAgent against three reflex agents:
 
    Agent[] agents = {new agents.ReflexmAgent(),new agents.ReflexAgent(),new agents.ReflexAgent() , new agents.MCAgent()};
