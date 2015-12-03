Zhi Wang (zhiwang@usc.edu) 30389
Baiyu Huang (baiyuhua@usc.edu) 30389
Xinkai Chen (xinkaich@usc.edu) 30389
Ryosuke Mitsubayashi (mitsubay@usc.edu) 30303

************************
Notes Regarding Our Code Submitted on Nov 22

First, please code in your MySQL password in JDBC.java (line 139) to test our user database.
Then please execute database/Users.sql in MySQL workbench.
Thank you!

During anytime of the actual game (the actual game on the soccer field), if a bug appears, 
which include a player moving randomly, a player disappearing, the ball disappearing, and 
the main player that you are controlling not moving accordingly, please try pressing 'r' on
your keyboard to reset the positions of all players and the ball.


*****How our game works:
Once the game is started, the user needs to select whether he/she wants to be a host or a client. 
If the user wants to play a friendly game without signing in, he/she should select host.

Once the user reaches the main menu, he/she can sign in or sign up. 
Only signed-in users can play online game. Otherwise, only friendly game can be played.

During an online game, (currently) one user has to wait for the other before jumping to further screens.
For example, when a user is done choosing team, he moves to the next screen which is GameSettings;
The user has to wait till the other user gets into GameSettings before moving on to the next screen.

To select the starting five players, the user has to click on a position in the diagram, and then choose
a player from below. The player has to be able to play that position: a forward can only be put in a spot
for forwards but not any other spots, for example.

Once the game starts, the team on the left will kick off. Users can pause/unpause the game from the button
on the game information board at the top of the screen. There will be a score board and a timer. Both half
will be 20 minutes long. Once half time is reached, the game will be automatically paused. Once the user 
presses "Continue to Second Half" button, the second half will be started with the team on the right 
kicking off. The score board will be updated when a team scores. When full time is reached, after some 
delay, the results page will show up, which will display the experience points and coins the user gets
if this is an online game. Also, if the game is online, both users will have their experience points and 
coins updated in the user database. 

Users will then be directed back to the main menu.

******What doesn't work:
There are some functionalities that are not currently working appropriately.
1. Our AI system isn't working as expected and sometimes will cause AI-controlled players to move randomly
or even disappear. We need to debug our AI system. This is why we decided to comment out much of our code 
for AI, as it may cause the game experience to be really bad.

2. Due to the issue above, in online game, AI system doesn't work properly.

3. Users cannot change the team's formation during the game. We deleted this feature. We also decided that 
two teams won't switch sides at half time.

4. We also deleted the feature "special moves".

