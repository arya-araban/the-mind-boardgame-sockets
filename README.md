# The Mind Boardgame

Implementation of The Mind boardgame in a client/server enviorment using sockets in Java. 

## Game Rules
[This video](https://www.youtube.com/watch?v=uXl8MC0GMYE) explains the rules of Mind in adequate detail.

## How to use the app

Run `Server.java` In order to have the server running. Then you can run multiple instances of `Client.java`.
These clients will be connected to the server which is currently running.

Each connected client will be one of the players of the game.
Once a client is connected to the server, they will be prompted to specify their nickname, which will be used to identify them throughout the game.

Once a client has entered their nickname, they will have to wait until the game host starts the game

## Game Host 
The first client which connects to the server will be known as the "host" of the game. 

The host will be given the option to specify the number of the players of the game. After they have specified their own nickname, they also have the command option to start the game at will by entering 'start'.


## AI Bots 

when the host starts the game, if the number of players specified by the host is greater than the amount of clients currently connected to the server,
the remaining players will be AI powered bots. 

Each of these bots is created with a unique game strategy which determines how early or late they will play their cards compared to other bots, given a similar game state.

This 'game state' is taken into account for each bot, by it looking at the smallest valued card it has at hand, as well as the total number of cards other players have in their hand. 
This state is resetted for every bot once a player(client or bot) plays a card. 
