# Q learning Summary

We can break down reinforcement learning into five simple steps:

1. The agent is at state zero in an environment.
2. It will take an action based on a specific strategy.
3. It will receive a reward or punishment based on that action.
4. By learning from previous moves and optimizing the strategy. 
5. The process will repeat until an optimal strategy is found. 

### What is Q-Learning?

Q-learning is a model-free, value-based, off-policy algorithm that will find the best series of actions based on the agent's current state. The “Q” stands for quality. Quality represents how valuable the action is in maximizing future rewards.  

**model-free** algorithms learn the consequences of their actions through the experience without transition and reward function. 

The **value-based method** trains the value function to learn which state is more valuable and take action. 

In the **off-policy**, the algorithm evaluates and updates a policy that differs from the policy used to take an action. 

### Key Terminologies in Q-learning
Before we jump into how Q-learning works, we need to learn a few useful terminologies to understand Q-learning's fundamentals. 

* States(s): the current position of the agent in the environment. 
* Action(a): a step taken by the agent in a particular state. 
* Rewards: for every action, the agent receives a reward and penalty. 
* Episodes: the end of the stage, where agents can’t take new action. It happens when the agent has achieved the goal or failed. 
* Q(St+1, a): expected optimal Q-value of doing the action in a particular state. 
* Q(St, At): it is the current estimation of Q(St+1, a).
* Q-Table: the agent maintains the Q-table of sets of states and actions.
* Temporal Differences(TD): used to estimate the expected value of Q(St+1, a) by using the current state and action and previous state and action. 

