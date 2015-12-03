package game;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import util.Constants;
import util.Pair;

public class PlayerAI {
	private Team team1;
	private Team team2;
	private Ball ball;
	private GamePanelCoords fieldCoords;
	
	
	private final double preferredDistanceOfPass = 200;
	private final double maximumDistanceOfPass = 300;
	private final double minimumDistanceFromPlayer = 10;
	private final double maximumDistanceFromBasePosition = 400;
	private final double consideredSpaceDistance = 50;
	//I did not implement a debug for this for if it is out-of-bounds
	private final int preferredDistanceOfBackLine = 10;
	
	//regarding when to call
	//on offense there are expensive functions so do not call as much
	//(do not need to call as much either way)
	//for defense, it is more responsive so need to call more often
	//(pretty sure there aren't any expensive operations)
	
	//also, some functions (most of the defending, the non-triangle offense showing)
	//require more than one player to move
	//so maybe add a queue of all players and remove as we update a player's destination
	//to assure we don't try moving the same player duplicate times, creating a loop??
	
	//Ryo
	
	public PlayerAI(Team team1, Team team2, Ball ball, GamePanelCoords gpc){
		this.team1 = team1;
		this.team2 = team2;
		this.ball = ball;
		this.fieldCoords = gpc;
	}
	
	//GET methods
	public Pair getDestination(Player p)
	{
		//Pair newLocation = calculateDestination(p);
		if(p.getPosition().equals(Constants.goalkeeper)){
			return calculateDestination(p);
		}
		else{
			//System.out.println(keepFormationOffense(p).getX());
			if(p.getTeam().hasBall())
				return keepFormationOffense(p);
			else
				return keepFormationDefense(p);
		}
	}
	
	//OTHER methods
	
	private Team getTeam(Player p)
	{
		if (team1.isOnTeam(p))
			return team1;
		
		if (team2.isOnTeam(p))
			return team2;
		
		return null;
	}
	
	private Team getOtherTeam(Team t)
	{
		if (team1.equals(t))
			return team2;
		
		if (team2.equals(t))
			return team1;
		
		return null;
	}
	
	private Team getTeamWithBall()
	{
		if (team1.hasBall())
			return team1;
		
		if (team2.hasBall())
			return team2;
		
		//neither team has ball
		return null;
	}
	
	private Player getPlayerWithBall()
	{
		Vector<Player> team1vec = team1.getStarting();
		for(Player p1 : team1vec)
		{
			if (p1.hasBall())
				return p1;
		}
		
		Vector<Player> team2vec = team2.getStarting();
		for (Player p2 : team2vec)
		{
			if (p2.hasBall())
				return p2;
		}
		
		//no player has ball
		return null;
	}
	
	private Vector<Player> getTeammates(Player p)
	{
		if (p.getTeam().equals(team1))
			return team1.getStarting();
		else
			return team2.getStarting();
	}
	
	private Vector<Player> getOpponents(Player p)
	{
		if (p.getTeam().equals(team1))
			return team2.getStarting();
		else
			return team1.getStarting();
	}
	
	private double getDistance(Object a, Object b)
	{
		double distance = 0;
		
		int aX = 0;
		int aY = 0;
		int bX = 0;
		int bY = 0;
		
		//can only be ball or player
		if (a instanceof Player)
		{
			aX = ((Player)a).getX();
			aY = ((Player)a).getX();
		} else if (a instanceof Ball)
		{
			aX = ((Ball)a).getX();
			aY = ((Ball)a).getY();
		} else if (a instanceof Pair)
		{
			aX = ((Pair)a).getX();
			aY = ((Pair)a).getY();
		} else
		{
			System.out.println("Invalid parameters for getDistance() in PlayerAI");
			return -1;
		}
		
		if (b instanceof Player)
		{
			bX = ((Player)b).getX();
			bY = ((Player)b).getX();
		} else if (b instanceof Ball)
		{
			bX = ((Ball)b).getX();
			bY = ((Ball)b).getY();
		} else if (b instanceof Pair)
		{
			bX = ((Pair)b).getX();
			bY = ((Pair)b).getY();
		} else
		{
			System.out.println("Invalid parameters for getDistance() in PlayerAI");
			return -1;
		}
		
		distance = (aX - bX)^2 + (aY - bY)^2;	
		if(distance > 0)
			distance = Math.sqrt(distance);
		else
			distance = 0;
		
		return distance;
	}
	
	private Vector<Player> closestToFarthest(Object obj, Vector<Player> team)
	{
		Vector<Player> newVector = new Vector<Player>();
		//insertion sort fastest for small data??
		
		for (Player p : team)
		{
			//not sure if this works,
			//maybe instead add boolean and call isGoalKeeper()??
			if (p.getPosition().equals(Constants.goalkeeper))
			{
				continue;
			}
			if (newVector.isEmpty())
			{
				newVector.add(p);
			} else
			{
				Boolean inserted = false;
				for(int i=0; i<newVector.size(); i++)
				{
					Player comp = newVector.get(i);
					if (getDistance(obj,comp) > getDistance(obj,p))
					{
						newVector.insertElementAt(p, i);
						inserted = true;
						break;
					}
				}
				
				if (!inserted)
				{
					//add at end
					newVector.add(p);
				}
			}
		}
		
		return newVector;
	}
	
	//better variable names? lol
	private Pair getShortestPath(Object from, Object to)
	{
		Pair intersection = new Pair();
		int fromX = -1;
		int fromY = -1;
		int toX = -1;
		int toY = -1;
		int toDestinationX = -1;
		int toDestinationY = -1;
		//what we are trying to find
		int fromDestinationX = 0;
		int fromDestinationY = 0;
		
		
		if (from instanceof Player)
		{
			fromX = ((Player)from).getX();
			fromY = ((Player)from).getY();
		}
		else if (from instanceof Ball)
		{
			fromX = ((Ball)from).getX();
			fromY = ((Ball)from).getY();
		} else
		{
			return null;
		}
		
		if (to instanceof Player)
		{
			toX = ((Player)to).getX();
			toY = ((Player)to).getY();
			Pair destination = ((Player)to).getDestination();
			toDestinationX = destination.getX();
			toDestinationY = destination.getY();
		}
		else if (to instanceof Ball)
		{
			toX = ((Ball)to).getX();
			toY = ((Ball)to).getY();
			Pair destination = ((Ball)to).getDestination();
			toDestinationX = destination.getX();
			toDestinationY = destination.getY();
		}
		
		//!!!
		//don't know how speed works so leaving this until someone does speed
		//for now just return destination
		fromDestinationX = toDestinationX;
		fromDestinationY = toDestinationY;
		
		intersection.set(fromDestinationX, fromDestinationY);
		return intersection;
	}
	
	
	private Pair calculateDestination(Player p)
	{
		
		//we don't need AI for user-controlled player
		//if (p.isMainPlayer())
		//	return null;
		
		Pair destination = new Pair();
		
		
		Team t = getTeam(p);
		//Team t = team2;
		//shouldn't happen but if the player is not on either team
		if (t == null)
		{
			System.out.println("team is null");
			return null;
		}
		
		//getting necessary variables to decide which function to call
		String position = p.getPosition();
		Boolean teamHasBall = t.hasBall();

		//chooses AI based on ball possession and position
		if (position.equals(Constants.goalkeeper))
		{
			//does not matter whether on offense or defense
			destination = goalKeeperDestination(p);
		} 
		/*else if (p.isReceivingBall()) 
		{
			//if player is receiving ball, he should not do anything but receive the ball
			destination = receiveBall(p);
		}*/
		else if (position.equals(Constants.forward))
		{
			
			if (teamHasBall)
				destination = forwardDestinationOffense(p);
			else
				destination = forwardDestinationDefense(p);
			
			
		}
		else if (position.equals(Constants.midfield))
		{
			if (teamHasBall)
			{
				destination = midfieldDestinationOffense(p);
			} else
			{
				destination = midfieldDestinationDefense(p);
			}
		}
		
		else if (position.equals(Constants.defender))
		{
			if (teamHasBall)
			{
				destination = defenderDestinationOffense(p);
			} else
			{
				destination = defenderDestinationDefense(p);
			}
		}
		if(destination != null) return destination;
		else return new Pair(p.getX(),p.getY());
	}
	
	//actual AI for GK
	//does not matter if on offense or defense
	private Pair goalKeeperDestination(Player p)
	{
		if (!p.getPosition().equals(Constants.goalkeeper)){
			return null;
		}
		
		int destinationX = -1;
		int destinationY = -1;
		
		int playerX = p.getX();
		
		int ballX = ball.getX();
		int ballY = ball.getY();
		
		int goalCenterX = 0;
		int goalCenterY = p.getBaseY();
		
		//first see if the ball is heading towards goal
		Boolean isShot = false;
		Pair ballDestination = ball.getDestination();
		int ballDestinationX = ballDestination.getX();
		int ballDestinationY = ballDestination.getY();
		
		//for now assuming that the ball's destination stops at the goal line
		if (ballDestinationX == playerX)
		{
			isShot = true;
		}
		
		//GK only moves left and right (along the y axis)
		destinationX = playerX;
		
		if (isShot)
		{
			//chase ball's destination point
			//again, assuming destination is goal line
			destinationY = ballDestinationY;
		} else
		{
			//just get in between ball and center of goal
			int slope = (goalCenterY - ballY) / (goalCenterX - ballX);
			int intercept = ballY - (slope * ballX);
			destinationY = slope * destinationX + intercept;
		}
		
		if(destinationY < p.getBaseY() - 85){
			destinationY = p.getBaseY() - 85;
		}
		if(destinationY > p.getBaseY() + 85){
			destinationY = p.getBaseY() + 85;
		}
		
		return new Pair(destinationX, destinationY);
	}
	
	//selects which AI to use based on mentality
	//DF
	private Pair defenderDestinationOffense(Player p)
	{
		//no matter the mentality, will attack defensively
		return defensiveOffense(p);
	}
	private Pair defenderDestinationDefense(Player p)
	{
		//special case for defenders
		//because they will move in sync with each other
		return defensiveDefense(p);
	}
	//MF
	private Pair midfieldDestinationOffense(Player p)
	{
		//Team playerTeam = getTeam(p);
		
		Team playerTeam = team1;
		int mentality = playerTeam.getMentality();
		
		if (mentality == Constants.offensiveMentality)
		{
			return offensiveOffense(p);
		} else if (mentality == Constants.normalMentality)
		{
			return normalOffense(p);
		} else
		{
			return defensiveOffense(p);
		}
	}
	private Pair midfieldDestinationDefense(Player p)
	{
		Team playerTeam = getTeam(p);
		int mentality = playerTeam.getMentality();
		
		if (mentality == Constants.offensiveMentality)
		{
			return offensiveDefense(p);
		} else
		{
			return normalDefense(p);
		}
	}
	//FW
	private Pair forwardDestinationOffense(Player p)
	{
		//Team playerTeam = getTeam(p);
		Team playerTeam = team1;
		int mentality = playerTeam.getMentality();
		
		if (mentality == Constants.offensiveMentality || mentality == Constants.normalMentality)
		{
			return offensiveOffense(p);
		} else
		{
			return normalOffense(p);
		}
	}
	private Pair forwardDestinationDefense(Player p)
	{
		Team playerTeam = getTeam(p);
		int mentality = playerTeam.getMentality();
		
		//if offensive or normal, should press the ball high
		//if defensive, should behave like defenders
		
		if (mentality == Constants.offensiveMentality)
		{
			return offensiveDefense(p);
		} else
		{
			return normalDefense(p);
		} 
	}
	
	private Pair defensiveMove(Player p){
		if(p.getPosition().equals(Constants.defender))
			return defensiveDefense(p);
		else if(p.getPosition().equals(Constants.midfield))
			return normalDefense(p);
		else
			return offensiveDefense(p);
	}
	
	//actual AI
	//normal defense
	//offensive defense
	//defensive offense
	//normal offense
	//offensive offense
	//defensive defense
	private Pair normalDefense(Player p)
	{
		Player mark = p.getMarking();
		//Player mark = null;
		//just update
		if (mark != null)
		{
			System.out.println("midfielder is marking: " + mark.getName() + " " + mark.getPosition() + "\n\n");
			return this.mark(p, mark, Constants.normalMentality);
		}
		
		//we just need to find an appropriate mark
		//no consideration of covering for midfielders and forwards
		Player findMark = findMark(p);
		System.out.println("midfielder is marking: " + findMark.getName() + " " + findMark.getPosition() + "\n\n");
		p.setMarking(findMark);
		return mark(p,findMark,Constants.normalMentality);
	}
	
	private Pair offensiveDefense(Player p)
	{
		//an offensive player should either chase the ball or
		//cut a pass course, not mark
		
		//see if this player is already marking
		Player mark = p.getMarking();
		if (mark == null)
		{
			mark = findMark(p);
		}

		//if in final third (where defenders are)
		//do not chase ball but cut pass course
		if (p.getX() > this.fieldCoords.getLeftBound() + this.fieldCoords.getWidth()*1/3)
		{
			//this method sets mark too			
			return this.cutPassCourse(p, mark);
		} else
		{
			Vector<Player> closestPlayers = closestToFarthest(ball,p.getTeam().getStarting());
			for (Player compP : closestPlayers)
			{
				//if you are the closest person, chase ball
				if (compP.equals(p))
				{
					//so the offensive midfielder may chase the same person a defender is marking
					//however, when the defending AI is called it will fix that
					
					//this method sets mark as well
					return this.chaseBall(p);
					
				} else if (compP.getPosition().equals(Constants.forward))
				{
					//only one forward should chase the ball,
					//but it is fine to have a forward and midfield to chase
					if (p.getPosition().equals(Constants.forward))
					{
						return this.cutPassCourse(p,mark);
					}
					continue;
				} else
				{
					return this.cutPassCourse(p,mark);
				}
			}
		}
		
		//should not call
		System.out.println("Logic error in offensiveDefense : this should not have been called");
		return null;
	}
	
	private Pair defensiveDefense(Player p)
	{
		//special case for defenders
		//will move in sync with other defenders
		
		//we will not move in a specific player but detect defenders in general
		//(still need to pass in player to find which team to search)
		return maintainBackLine(p);
	}
	
	private Pair defensiveOffense(Player p)
	{
		//the player should drop back to show for the ball
		//so that he does not leave the opponent's FW free
		return show(p,Constants.defensiveMentality);
	}
	
	private Pair normalOffense(Player p)
	{
		//the player should find an open space anywhere on pitch
		//and move there
		return show(p,Constants.normalMentality);
	}
	
	private Pair offensiveOffense(Player p)
	{
		//the player should try to find space in front as much as possible
		return show(p,Constants.offensiveMentality);
	}
	
	//Actions for AI
	private Pair receiveBall(Player p)
	{
		return this.getShortestPath(p, ball);
	}
	
	private Pair cutPassCourse(Player p, Player opponent)
	{
		//math
		int opponentX=opponent.getX();
		int opponentY=opponent.getY();
		
		int px=p.getX();
		int py=p.getY();
		
		int midX = (opponentX + px) / 2;
		int midY = (opponentY + py) / 2;
		
		p.setMarking(opponent);
		return new Pair(midX, midY);
	}
	
	private Pair chaseBall(Player p)
	{
		//just return ball position?
		Player playerWithBall = this.getPlayerWithBall();
		p.setMarking(playerWithBall);
		return new Pair(ball.getX(),ball.getY());
	}
	
	
	private Pair mark(Player p, Player markPlayer, int mentality)
	{
		//get in between player and goal
		int markX = markPlayer.getX();
		int markY = markPlayer.getY();
		double goalCenterX = 0;
		if (p.getTeam().equals(team1))
			goalCenterX = this.fieldCoords.getRightBound();
		else
			goalCenterX = this.fieldCoords.getLeftBound();
		double goalCenterY = this.fieldCoords.getHalfwayY();
		
		double slope = (goalCenterY - markY) / (goalCenterX - markX);
		double intercept = markY - (slope * markX);
		
		//distance between the two players
		//need to adjust
		//also, this should be relative to screen size
		
		int deltaY = 0;
		if (mentality == Constants.offensiveMentality)
			deltaY = 5;
		else if (mentality == Constants.normalMentality)
			deltaY = 10;
		else if (mentality == Constants.defensiveMentality)
			deltaY = 15;
		double deltaX = (deltaY - intercept) / slope;
		if (goalCenterY - markY == 0)
			deltaX = deltaY;
		//just problems with infinity?
		if (goalCenterX - markX == 0)
			deltaX = deltaY;
		//which direction is between goal and marking player?
		if (p.getTeam().equals(team2))
				deltaX *= (-1);
		
		if (markY > goalCenterY)
			deltaY *= (-1);
			
		return new Pair(markX + deltaX, markY + deltaY);
	}
	//helper for mark
	private Player findMark(Player p)
	{
		
		boolean canMark = true;
		
		//start from the base location?
		Vector<Player> closestToPlayer = this.closestToFarthest(p, getOtherTeam(p.getTeam()).getStarting());
		//Vector<Player> closestToBaseLocation = this.closestToFarthest(
		//		new Pair(p.getBaseX(),p.getBaseY()), this.getOpponents(p));
		
		
		for(int i=0; i<closestToPlayer.size(); i++){
			for(int j=0; j<p.getTeam().getStarting().size(); j++){
				if(p.getTeam().getStarting().elementAt(j).getMarking() == closestToPlayer.elementAt(i)){
					canMark = false;
					break;
				}					
			}
			if(canMark == true){
				return closestToPlayer.elementAt(i);
			}
			canMark = true;
		}
		return null;
	}
	
	private Pair maintainBackLine(Player p)
	{
		//will consider all defenders' locations
		Vector<Player> realTeam = p.getTeam().getStarting();
		Vector<Player> team = new Vector<Player>(realTeam);
		Player removeGK = null;
		Vector<Player> defenders = new Vector<Player>();
		for(Player teammate : team)
		{
			if (teammate.getPosition().equals(Constants.defender))
			{
				defenders.add(teammate);
			} else if (teammate.getPosition().equals(Constants.goalkeeper))
			{
				removeGK = teammate;
			}
		}
		//do not need GK
		team.remove(removeGK);
		
		//order opponents by how far they are from your goal
		// so dangerous -> least dangerous
		
		Pair goalLoc = new Pair(fieldCoords.getRightBound(), fieldCoords.getHalfwayY());
		Vector<Player> copy = this.closestToFarthest(goalLoc,getOtherTeam(p.getTeam()).getStarting());
		Vector<Player> opponents = new Vector<Player>(copy);
		//no need to remove gk because it's always the last element
		
		//first let's check if the most dangerous players (equal to the number of defenders) have a mark
		Vector<Player> opponentsToMark = new Vector<Player>();
		for (int i=0; i<defenders.size(); i++)
		{
			opponentsToMark.add(opponents.get(i));
		}
		
		Vector<Player> defenderMarkingPlayer = new Vector<Player>();
		Vector<Player> otherMarkingPlayer = new Vector<Player>();
		for (Player playerToCheck : opponentsToMark)
		{
			for(Player teammate : team)
			{
				if (playerToCheck.equals(teammate.getMarking()))
				{
					if (teammate.getPosition().equals(Constants.defender))
						defenderMarkingPlayer.add(teammate);
					else
						otherMarkingPlayer.add(teammate);
				}
			}
		}
		
		if (otherMarkingPlayer.size() == 0)
		{
			//the defenders are all marking the closest player to the goal
			if (defenderMarkingPlayer.size() == defenders.size())
			{
				//update their positioning
				//we do this after all if condition
				//organizeBackLine(defenders, opponentsToMark);
			} else //there is a defender not marking the most dangerous player
			{
				//see if the idle defender(s) are marking someone else
				//if so, shift accordingly
				Vector<Player> otherPositions = new Vector<Player>();
				for(Player pl : team)
				{
					if (!pl.getPosition().equals(Constants.defender))
						otherPositions.add(pl);
				}
				//store marks here for now
				Vector<Player> needToMark = new Vector<Player>();
				for (Player df : defenders)
				{
					Player mark = df.getMarking();
					//this defender is doing his job
					if (opponentsToMark.contains(mark))
						continue;
					
					//not marking most dangerous player
					
					if (mark == null)
						continue; //no need to worry, we can organize later
					else 
					{
						//we need to pass this player on to the nearest other player
						needToMark.add(mark);
					}
				}
				
				//let's mark the passed on players
				//have not even simulated this...
				//probably will end up in chaos
				while (!needToMark.isEmpty())
				{
					if (otherPositions.isEmpty())
						break;
					Player toMark = needToMark.get(0);
					Vector<Player> sorted = this.closestToFarthest(toMark, otherPositions);
					//the closest will mark this player
					Player closest = sorted.get(0);
					//mentality should be different for forwards and midfielders?
					if (closest.getPosition().equals(Constants.midfield))
						closest.setDestination(this.mark(closest, toMark, Constants.normalMentality));
					else //forward
						closest.setDestination(this.mark(closest, toMark, Constants.offensiveMentality));
					
					if (closest.getMarking() != null)
					{
						//we also need to care of this player's mark
						needToMark.add(closest.getMarking());
					} 
					closest.setMarking(toMark);
					//we don't need to search for this player again
					otherPositions.remove(closest);
				}
				
				
			}
		} else //end if other marking player = 0
		{
			//there must be a defender marking someone else or idle
			//so pass on that player to another non-defending player
			// (for now just switch)
			//and shift accordingly
			Vector<Player> needToMark = new Vector<Player>();
			for (Player df : defenders)
			{
				Player marking = df.getMarking();
				if (opponentsToMark.contains(marking))
					continue; //this defender is doing his job
				
				if (marking != null)
					needToMark.add(marking);
			}
			
			//set othermarkingplayer to another needed mark or null
			for (Player other : otherMarkingPlayer)
			{
				if (needToMark.isEmpty())
				{
					//let another call of AI figure out this player's destination
					other.setMarking(null);
					continue;
				}
				Vector<Player> sorted = this.closestToFarthest(other, needToMark);
				Player closestNeedToMark = sorted.get(0);
				if (other.getPosition().equals(Constants.midfield))
					other.setDestination(this.mark(other, closestNeedToMark, Constants.normalMentality));
				else //forward
					other.setDestination(this.mark(other, closestNeedToMark, Constants.offensiveMentality));
				other.setMarking(closestNeedToMark);
			}
			
			//now there should not be conflict?
		}
		organizeBackLine(defenders, opponentsToMark);
		
		
		return p.getDestination();
	}
	
	private void organizeBackLine(Vector<Player> defenders, Vector<Player> opponents)
	{
		//these two should be the same
		if (defenders.size() != opponents.size())
		{
			System.out.println("Error in organizeBackLine : defender and opponent #s different");
			return;
		}
		
		//first sort defenders and offense left to right
		Vector<Player> sortedDefenders = new Vector<Player>();
		for(Player df : defenders)
		{
			boolean inserted = false;
			int y = df.getY();
			for (int i=0; i<sortedDefenders.size(); i++)
			{
				if (sortedDefenders.get(i).getY() > y)
				{
					sortedDefenders.insertElementAt(df, i);
					break;
				}
			}
			
			if (!inserted)
				sortedDefenders.add(df);
		}
		
		Vector<Player> sortedOpponents = new Vector<Player>();
		for(Player op : opponents)
		{
			//first element
			if (sortedDefenders.isEmpty())
			{
				sortedOpponents.add(op);
				continue;
			}
			
			int y = op.getY();
			boolean inserted = false;
			for (int i=0; i<sortedOpponents.size(); i++)
			{
				if (sortedOpponents.get(i).getY() > y)
				{
					sortedOpponents.insertElementAt(op, i);
					inserted = true;
					break;
				}
			}
			if (!inserted)
				sortedOpponents.add(op);
		}
		
		
		Player playerWithBall = this.getPlayerWithBall();
		int index = -1;
		
		if (sortedOpponents.contains(playerWithBall))
			index = sortedOpponents.indexOf(playerWithBall);
		
		//the player with the ball is in opponent vector
		if (index != -1)
		{
			//we should make sure the corresponding defender steps up and mark him
			Player correspondingDefender = sortedDefenders.get(index);
			
			//but what if the defender is already beat (behind the opponent)?
			//one other defender needs to switch marks with him
			
			//check if beat
			boolean beat = false;
			if (playerWithBall.getTeam().equals(team1))
			{
				if (playerWithBall.getX() > correspondingDefender.getX())
					beat = true;
			} else
			{
				if (playerWithBall.getX() > correspondingDefender.getX())
					beat = true;
			}
			
			if (beat)
			{
				//find closest person besides the defender that got beat
				Player closestDefender = null;
				double closest = 10000;
				for (Player df : defenders)
				{
					if (df.equals(correspondingDefender))
						continue;
					double dist = this.getDistance(playerWithBall, df);
					if (dist < closest)
					{
						closest = dist;
						closestDefender = df;
					}
				}
				
				//we move the new marking defender not just where it could mark
				//but on the line with other defenders
				//because the theory is to drop as much as possible until the beat defender
				//has time to recover
				
				//in other words, we don't call mark()
				
				//get right in front of the player with ball
				int y = playerWithBall.getY();
				//stay on the line
				int x = playerWithBall.getX() - this.preferredDistanceOfBackLine;
				closestDefender.setDestination(new Pair(x,y));
				closestDefender.setMarking(playerWithBall);
				
				//now set up for next part where we will decide other players' movements
				int closestIndex = sortedDefenders.indexOf(closestDefender);
				sortedDefenders.set(index, closestDefender);
				sortedDefenders.set(closestIndex, correspondingDefender);
				
				correspondingDefender = closestDefender;
			}
			else //not beat
			{
				//the closest defender should just mark him
				Pair mark = this.mark(correspondingDefender, playerWithBall, Constants.defensiveMentality);
				correspondingDefender.setDestination(mark);
				correspondingDefender.setMarking(playerWithBall);
			}
			
			//now find others
			
			int lineX = playerWithBall.getX();
			if (playerWithBall.getTeam().equals(team1)) lineX -= this.preferredDistanceOfBackLine;
			else lineX += this.preferredDistanceOfBackLine;
				
			//if the opponent is past the line, just mark
			//if not, back up to a certain line and get in midpoint between mark and next opponent
			
			for (int i=0; i<sortedDefenders.size(); i++)
			{
				//somehow gives different numbers. if so, just return
				if (sortedDefenders.size() != sortedOpponents.size())
					return;
				Player defender = sortedDefenders.get(i);
				Player opponent = sortedOpponents.get(i);
				
				//already covered this
				if (defender.equals(correspondingDefender))
					continue;
				
				boolean pastLine = false;
				if (playerWithBall.getTeam().equals(team1))
				{
					if (opponent.getX() > lineX)
					{
						pastLine = true;
					}
				} else
				{
					if (opponent.getX() < lineX)
					{
						pastLine = true;
					}
				}
				
				if (pastLine)
				{
					//past the line so just mark
					defender.setDestination(this.mark(defender, opponent, Constants.defensiveMentality));
					defender.setMarking(opponent);
					//remove from vector to set up for next part
					sortedDefenders.remove(i);
					sortedOpponents.remove(i);
					//note we haven't removed the player with the ball
					//this is because that player is still in front of the line
				}
			}
			
			//finding midpoints in between opponents in front of the line
			int[] midpoints = new int[sortedOpponents.size()-1];
			for (int i=0; i<sortedOpponents.size()-1; i++)
			{
				//dont need to get abs value because they are already ordered
				int midpoint = (sortedOpponents.get(i+1).getX() + sortedOpponents.get(i).getX())/2;
				midpoints[i] = midpoint;
			}
			//now set location of other defenders, left to right
			int j=0;
			for(int i=0; i<sortedDefenders.size(); i++)
			{
				Player remainingDefender = sortedDefenders.get(i);
				if (remainingDefender.equals(correspondingDefender))
					continue;
				remainingDefender.setDestination(new Pair(lineX,midpoints[j]));
				remainingDefender.setMarking(sortedOpponents.elementAt(i));
				j++;
			}
		} else //end if statement: player with ball is in opponent vector
		{
			//the defenders should position themselves in between center Y of field and opponent
			//making sure they drop behind the opponent
			System.out.println("Calculating defender location");
			//find ball location
			//if on left side, the defenders on the left mark a bit tighter
			//than defenders on the right
			int left = 1;
			int right = 2;
			int ballLocation = 0;
			//how much mark should be tighter
			int deltaX = 10;
			
			if (this.ball.getY() > this.fieldCoords.getHalfwayY())
				ballLocation = left;
			else
				ballLocation = right;
			
			for (int i=0; i<sortedOpponents.size(); i++)
			{
				//first find midpoint between opponent and center of field
				//and then see if it is on left or right
				Player opponent = sortedOpponents.elementAt(i);
				Player defender = sortedDefenders.elementAt(i);
				int midpointY = (opponent.getY() + (int)this.fieldCoords.getHalfwayY()) / 2;
				int x = opponent.getX() + this.preferredDistanceOfBackLine;
				if (midpointY > this.fieldCoords.getHalfwayY())
				{
					if (ballLocation == left)
						x += deltaX;
				} else
				{
					if (ballLocation == right)
						x += deltaX;
				}
				System.out.println(x + " " + midpointY);
				defender.setDestination(new Pair(x,midpointY));
				defender.setMarking(opponent);
			}
			
			
		}
	}
	
	private Boolean pathObstructed(Object from, Object to)
	{
		//should have at least one object as a player
		int fromX = 0;
		int fromY = 0;
		int toX = 0;
		int toY = 0;
		
		//should only be a player for now
		//but in case we pass in the destination
		//pair of the player
		if (from instanceof Player)
		{
			fromX = ((Player)from).getX();
			fromY = ((Player)from).getY();
			
		}
		else if (from instanceof Ball)
		{
			fromX = ((Ball)from).getX();
			fromY = ((Ball)from).getY();
		}
		else if (from instanceof Pair)
		{
			fromX = ((Pair)from).getX();
			fromY = ((Pair)from).getY();
		}
		
		//the to object can be anything
		if (to instanceof Player)
		{
			toX = ((Player)to).getX();
			toY = ((Player)to).getY();
		}
		else if (to instanceof Ball)
		{
			toX = ((Ball)to).getX();
			toY = ((Ball)to).getY();
		}
		else if (to instanceof Pair)
		{
			toX = ((Pair)to).getX();
			toY = ((Pair)to).getY();
		}
		
		
		
		return true;
	}
	
	//finding open space
	//long implementation...
	
	private Pair show(Player p, int mentality)
	{
		//base space finding on the concept that
		//players will want to form a triangle shape
		//so that the player on the ball will have
		//two options to pass to
		//first find triangle points
		//player on ball is automatically a triangle point
		int trianglePoints = 0;
		//Vector<Player> teammates = this.getTeammates(p);
		
		if(team1 == null) System.out.println("team1 is null");

		
		Vector<Player> teammates = team1.getStarting();
		//Vector<Player> teammates = team1.getStarting();
		
		Vector<Player> closestToFarthest = this.closestToFarthest(ball, teammates);
		
		//just set triangle point here
		if(this.getPlayerWithBall()!=null)
			this.getPlayerWithBall().setTrianglePoint(true);
		
		Player pointPlayer = null;
		Player point2player = null;
		Player playerOnBall = this.getPlayerWithBall();
		
		if(playerOnBall != null){
			playerOnBall.setTrianglePoint(true);
		}
		
		for (Player teamPlayer : closestToFarthest)
		{
			//do not count gk
			if (teamPlayer.getPosition().equals(Constants.goalkeeper))
				continue;
			if (teamPlayer.isTrianglePoint())
			{
				trianglePoints++;
				//not player on ball
				if (!teamPlayer.equals(playerOnBall))
				{
					//this would be the closest player
					//or the 2nd closest if the player
					//we are calculating for is the
					//closest player, but it doesn't matter
					if (pointPlayer == null)
						pointPlayer = teamPlayer;
					else
						point2player = teamPlayer;
				}
			}
			
		}
		
		//need to figure out how far the player is from the ball
		//compared to other field players
		double playerDistanceFromBall = this.getDistance(ball, p);
		
		System.out.println("after first getDistance()");
		System.out.println("closestToFarthest has size of: " + closestToFarthest.size());
		System.out.println("player distance: " + playerDistanceFromBall);
		int rank = 0;
		for(Player teamPlayer : closestToFarthest)
		{
			System.out.println("distance to compare with: " + this.getDistance(ball, teamPlayer));
			//do not count gk
			if (teamPlayer.getPosition().equals(Constants.goalkeeper))
				continue;
			if (teamPlayer.equals(playerOnBall))
				continue;
			if (this.getDistance(ball,teamPlayer) > playerDistanceFromBall)
			{			
				break;
			}
			rank++;
		}
		
		System.out.println("trianglePoints: " + trianglePoints);
		
		//not just conditioning whether rank == 3 (farthest field player)
		//because the farthest player may already be heading toward
		//a triangle point now
		if (trianglePoints == 1 || trianglePoints == 2)
		{
			System.out.println("rank is "+ rank);
			if (rank == 3){
				for(Player player : p.getTeam().getStarting()){
					if(!player.isMainPlayer()){
						player.setTrianglePoint(false);
					}
				}
				
				trianglePoints = 0;
				return OSfindNonTrianglePoint(p, mentality);
			}
			else{
				for(Player player : p.getTeam().getStarting()){
					if(!player.isMainPlayer()){
						player.setTrianglePoint(false);
					}
				}
				
				trianglePoints = 0;
				return OSfindTrianglePoint(p, pointPlayer, mentality);
				}
		}
		else if (trianglePoints == 3)
		{
			for(Player player : p.getTeam().getStarting()){
				if(!player.isMainPlayer()){
					player.setTrianglePoint(false);
				}
			}
			
			trianglePoints = 0;
			//haven't implemented yet
			return OSfindNonTrianglePoint(p, mentality);
		} else
		{
			for(Player player : p.getTeam().getStarting()){
				if(!player.isMainPlayer()){
					player.setTrianglePoint(false);
				}
			}
			
			trianglePoints = 0;
			System.out.println("Error in finding spacing: invalid # of triangle points");
			return null;
		}
		
		
		
		
	}
	
	//all helper functions for showing will have OS (openSpace) in front of function name
	private Pair OSfindTrianglePoint(Player p, Player firstPointPlayer, int mentality)
	{
		//firstPointPlayer is the player that is already heading towards a point
		//that is not the main player
		Pair startingPoint = OSgetStartingTrianglePoint(p,firstPointPlayer);
		
		System.out.println("starting point is: " + startingPoint.getX() + " " + startingPoint.getY());
		
		OSHeuristicTriangle heur = new OSHeuristicTriangle(p, mentality);
		Set<Pair> done = new HashSet<Pair>();
		
		//set this point to triangle point before we forget
		//shouldn't affect rest of function???
		p.setTrianglePoint(true);
		
		//A* search
		PriorityQueue<Pair> queue = new PriorityQueue<Pair>(new OSHeurComparator());
		heur.calculateHeuristic(startingPoint);
		queue.add(startingPoint);
		
		while (!queue.isEmpty())
		{
			Pair thisPair = queue.peek();
			double value = thisPair.getValue();
			
			if (value >= 1)
			{
				//choose this
				return thisPair;
			}
			
			queue.remove();
			
			Pair rightPoint = null;
			Pair leftPoint = null;
			Pair behindPoint = null;
			Pair aheadPoint = null;
			
			//the pass course is blocked
			//this is a special case
			if (value == -1)
			{
				//do not need to search the location behind or ahead
				//because it will also be blocked indefinitely
				rightPoint = this.OSrightPoint(thisPair);
				if (this.OSpointValid(done, rightPoint, p))
				{
					heur.calculateHeuristic(rightPoint);
					queue.add(rightPoint);
				}
				
				leftPoint = this.OSleftPoint(thisPair);
				if (this.OSpointValid(done, leftPoint, p))
				{
					heur.calculateHeuristic(leftPoint);
					queue.add(leftPoint);
				}
				
				//store these not in the queue but done
				//so we don't search it again
				aheadPoint = this.OSaheadPoint(thisPair);
				//make sure it's valid, just in case we end up choosing this pair
				if (this.OSpointValid(done, aheadPoint, p))
					done.add(aheadPoint);
				behindPoint = this.OSbehindPoint(thisPair);
				if (this.OSpointValid(done, behindPoint, p))
					done.add(behindPoint);
				
				//move element from queue to done
				done.add(thisPair);
				
				continue;
			}
			
			rightPoint = this.OSrightPoint(thisPair);
			if (this.OSpointValid(done, rightPoint, p))
			{
				heur.calculateHeuristic(rightPoint);
				queue.add(rightPoint);
			}
			leftPoint = this.OSleftPoint(thisPair);
			if (this.OSpointValid(done, leftPoint, p))
			{
				heur.calculateHeuristic(leftPoint);
				queue.add(leftPoint);
			}
			aheadPoint = this.OSaheadPoint(thisPair);
			if (this.OSpointValid(done, aheadPoint, p))
			{
				heur.calculateHeuristic(aheadPoint);
				queue.add(aheadPoint);
			}
			behindPoint = this.OSbehindPoint(thisPair);
			if (this.OSpointValid(done, behindPoint, p))
			{
				heur.calculateHeuristic(behindPoint);
				queue.add(behindPoint);
			}
			
			//done with this pair
			done.add(thisPair);
			
			//move onto next pair in queue
		}
		
		//no space found
		//order and return highest heuristic
		for(Pair donePair : done)
		{
			//a bit expensive but
			//practically should not get to this code
			//because a soccer field will always have space
			queue.add(donePair);
		}
		return queue.peek();
	}
	
	//heuristic to finding open space for triangle
	private class OSHeuristicTriangle {
		private Player player;
		private int mentality;
		private Pair goalLocation;
		
		//various weights
		//should equal 1
		private final double goalDistanceWeight = 0.8;
		private final double basePositionWeight = 0.2;
		
		public OSHeuristicTriangle(Player p, int mentality)
		{	
			this.player = p;
			this.mentality = mentality;
			if(player.getTeam().equals(team1)) goalLocation = new Pair(fieldCoords.getWidth(), fieldCoords.getHeight()/2);
			else goalLocation = new Pair(0, fieldCoords.getHeight()/2);
		}
		
		//sets the final heuristic in the pair!!
		public void calculateHeuristic(Pair pair)
		{
			double heur = 0;
			
			//if not a pass course, we will never choose this point
			if (!PlayerAI.this.pathObstructed(ball, pair))
			{
				pair.setValue(-1);
				return;
			}
			
			//if over 1.0, choose this point
			if (OSisOpenSpace(this.player.getTeam(),pair))
			{
				heur += 1.0;
			}
			
			//negatives
			
			//there is a player in between
			if (!PlayerAI.this.pathObstructed(player, pair))
			{
				heur -= 0.5;
			}
			
			//distance from goal
			double baseDistance = PlayerAI.this.getDistance(ball, goalLocation);
			double thisDistance = PlayerAI.this.getDistance(pair, goalLocation);
			
			//Diff just for reference
			// Goal   A     B     BaseDistance   C     D
			// thisD   1/4   3/4      1          5/4    7/4
			double diff = thisDistance / baseDistance;
			//Weight
			// 0 <= x < 1
			double weight = 0;
			if (mentality == Constants.offensiveMentality)
			{
				//closer to goal, the better
				weight = 1 - diff;
				if (weight < 0)
				{
					weight = 0;
				}
			} else if (mentality == Constants.normalMentality)
			{
				//no weight based on position
				weight = 0;
			} else if (mentality == Constants.defensiveMentality)
			{
				if (diff == 1)
				{
					//this should be preferred most?
					weight = 0.9;
				}
				else if (diff > 0)
				{
					//never go closer to goal
					weight = 0;
				} else
				{
					weight = 1 / diff;
				}
			}
			
			// 0 <= x  < 0.?
			double weightedWeight = goalDistanceWeight * weight;
			heur += weightedWeight;
			
			//base position
			double dist = PlayerAI.this.getDistance(new Pair(player.getBaseX(),player.getBaseY()), pair);
			if (dist == 0)
			{
				//max
				//will be equal to a distance of 1 in else condition
				//we do this to prevent weight from becoming 1
				weight = 1 - 1/maximumDistanceFromBasePosition;
			} else
			{
				weight = 1 - dist / maximumDistanceFromBasePosition;
			}
			weightedWeight = weight * basePositionWeight;
			
			heur += weightedWeight;
			pair.setValue(heur);
			return;
		}
		
		
		
	}
	private class OSHeurComparator implements Comparator<Pair>
	{
		@Override
		public int compare(Pair a, Pair b)
		{
			if (a.getValue() >= b.getValue())
				return 1;
			else
				return -1;
		}
	}
	
	private boolean OSisOpenSpace(Team t, Pair p)
	{
		//random player from team
		Vector<Player> opponents = this.getOpponents(t.getMainPlayer());
		for (Player opponent : opponents)
		{
			//opponent is too close to that point
			if (this.getDistance(opponent, p) < this.consideredSpaceDistance)
			{
				return false;
			}
		}
		
		return true;
	}
	
	//left and right points should curve
	//so there is no distance difference between point and ball
	private Pair OSleftPoint(Pair p)
	{
		int x = p.getX();
		int y = p.getY();
		
		int ballX=ball.getX();
		int ballY=ball.getY();
		
		//radius of the circle
		double radius=Math.sqrt((x-ballX)*(x-ballX)+(y-ballY)*(y-ballY));
		
		//slope of the line.
		double slope=0;
		if (ballX - x == 0) slope = 99;
		else slope = (ballY-y)/(ballX-x);
		//y=slope*x+d;
		double d=y-slope*x;
		
		//mid point between ball the p
		int midX=(ballX+x)/2;
		int midY=(ballY+y)/2;
		//mid point between p and the midpoint.
		int midMidX=(midX+x)/2;
		int midMidY=(midY+y)/2;
		
		double verticalSlope=-1/slope;
		double verticalD=midMidY-verticalSlope*midMidX;
		
		double a=1+verticalSlope*verticalSlope;
		double b=-2*ballX+2*verticalSlope*verticalD-2*verticalSlope*ballY;
		double c=ballX*ballX+verticalD*verticalD-2*verticalD*ballY+ballY*ballY-radius*radius;
		

		double potentialX2= (-b-Math.sqrt(b*b-4*a*c))/(2*a);
		double potentialY2=verticalSlope*potentialX2+verticalD;
		
		return new Pair(potentialX2,potentialY2);
	}
	
	private Pair OSrightPoint(Pair p)
	{
		int x = p.getX();
		int y = p.getY();
		
		int ballX=ball.getX();
		int ballY=ball.getY();
		
		//radius of the circle
		double radius=Math.sqrt((x-ballX)*(x-ballX)+(y-ballY)*(y-ballY));
		
		//slope of the line.
		double slope=0;
		//prevent infinity
		if (ballX - x == 0) slope = 99;
		else slope = (ballY-y)/(ballX-x);
		
		//y=slope*x+d;
		double d=y-slope*x;
		
		//mid point between ball the p
		int midX=(ballX+x)/2;
		int midY=(ballY+y)/2;
		//mid point between p and the midpoint.
		int midMidX=(midX+x)/2;
		int midMidY=(midY+y)/2;
		
		double verticalSlope=-1/slope;
		double verticalD=midMidY-verticalSlope*midMidX;
		
		double a=1+verticalSlope*verticalSlope;
		double b=-2*ballX+2*verticalSlope*verticalD-2*verticalSlope*ballY;
		double c=ballX*ballX+verticalD*verticalD-2*verticalD*ballY+ballY*ballY-radius*radius;
		
		double potentialX1= (-b+Math.sqrt(b*b-4*a*c))/(2*a);
		double potentialY1=verticalSlope*potentialX1+verticalD;
		
		
		
		return new Pair(potentialX1,potentialY1);
	}
	
	private Pair OSbehindPoint(Pair p)
	{
		//relative to ball position
		int x = p.getX();
		int y = p.getY();
		int ballX=ball.getX();
		int ballY=ball.getY();
		
		//mid point between ball the p
		int midX=(ballX+x)/2;
		int midY=(ballY+y)/2;
		
		//p is the midpoint between the midpoint above and behindPoint.
		int headX=2*x-midX;
		int headY=2*y-midY;
		
		return new Pair(headX,headY);
	}
	
	private Pair OSaheadPoint(Pair p)
	{
		//relative to ball position
		int x = p.getX();
		int y = p.getY();
		
		int ballX=ball.getX();
		int ballY=ball.getY();
		
		int slope=0;
		//infinity
		if (x-ballX == 0)
			slope = 99;
		else
			slope = (y-ballY)/(x-ballX);
		int d=y-slope*x;
		int midX=(ballX+x)/2;
		int midY=(ballY+y)/2;
		
		return new Pair(ballX,ballY);
	}
	
	private boolean OSpointValid(Set<Pair> done, Pair pair, Player player)
	{
		//Valid = not exceeding max distance of pass + 
		// point exists on the field
		// (technically it's fine, even recommended for wingers to step outside
		// the field to receive a pass) +
		// point not too close to another player +
		// not covered already
		
		if (!done.contains(pair))
			return false;
		
		if (!fieldCoords.isInField(pair))
			return false;
		
		double distFromBall = this.getDistance(pair, ball);
		if (distFromBall > this.maximumDistanceOfPass)
			return false;
		
		Vector<Player> teammates = this.getTeammates(player);
		for (Player teammate : teammates)
		{
			if (this.getDistance(pair, teammate) < this.minimumDistanceFromPlayer)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private Pair OSgetStartingTrianglePoint(Player firstPlayer, Player p)
	{
		//the first point we start our search should depend
		//for 1st point
		if (firstPlayer == null)
		{
			//start shortest path between player and ball
			//at the preferred distance of passes
			/*
			double distX = p.getX() - ball.getX();
			double distY = p.getY() - ball.getY();
			double squared = Math.pow(distX, 2) + Math.pow(distY, 2);
			double dist = Math.sqrt(squared);
			//it's OK for distX or distY to be negative??
			double xLoc = preferredDistanceOfPass * distX / dist;
			double yLoc = preferredDistanceOfPass * distY / dist;
			
			double x = xLoc + ball.getX();
			double y = yLoc + ball.getY();*/
			//start at midpoint until debug calculation
			double x = (p.getX() + ball.getX()) / 2;
			double y = (p.getY() + ball.getY()) / 2;
			
			if (!this.fieldCoords.isInField(x,y))
			{
				//adjust point if not on field
				double leftBound = this.fieldCoords.getLeftBound();
				double rightBound = this.fieldCoords.getRightBound();
				double upperBound = this.fieldCoords.getUpperBound();
				double lowerBound = this.fieldCoords.getLowerBound();
				
				if (x <= leftBound)
					x = leftBound + 1;
				if (x >= rightBound)
					x = rightBound - 1;
				if (y <= upperBound)
					y = upperBound + 1;
				if (y >= lowerBound)
					y = lowerBound - 1;
					
			}
			
			return new Pair(x,y);
		}
		
		//for 2nd point
		else
		{
			//*COPY PASTED except variable names and the last mentality part
			//from the non-triangle equivalent method
			//so if changing this also change other method
			
			//find point where preferred distance of pass of the other two points intersect
			//there will be two points to consider here,
			//points where the distance to the two other points = preferred pass distance
			//but choose the closest to player for normal mentality
			//and closest to goal for defensive mentality
			
			//we are assuming that the distance between the two points are less than 2 * preferred pass distance
			//so if it's more, start at the midpoint
			
			
			
			double distPointToPoint = this.getDistance(firstPlayer, this.ball);
			
			System.out.println("distPointToPoint doesn't have Invalid parameters");
			
			//first find midpoint
			//absolute val because points cannot be negative by design
			double midpointX = (firstPlayer.getX()+this.ball.getX())/2;
			double midpointY = (firstPlayer.getY()+this.ball.getY())/2;
			double x1 = firstPlayer.getX();
			double x2 = this.ball.getX();
			double y1 = firstPlayer.getY();
			double y2 = this.ball.getY();
			//Matrix matrixCompute = new Matrix();
			double[][] Xmatrix = new double[3][3];
			double theta = Math.atan((y2-y1)/(x2-x1));
			Xmatrix[0][0] = Math.cos(theta+Math.PI/2);
			Xmatrix[0][1] = -Math.sin(theta+Math.PI/2);
			Xmatrix[0][2] = midpointX;
			Xmatrix[1][0] = Math.sin(theta+Math.PI/2);
			Xmatrix[1][1] = -Math.cos(theta+Math.PI/2);
			Xmatrix[1][2] = midpointY;
			Xmatrix[2][0] = 0;
			Xmatrix[2][1] = 0;
			Xmatrix[2][2] = 1;
			double[][] localMat = new double[3][1];
			localMat[0][0] = this.preferredDistanceOfPass;
			localMat[1][0] = 0;
			localMat[2][0] = 1;
			double[][] resultMatrix = new double[3][1];
			resultMatrix = Matrix.multiply(Xmatrix, localMat);
			Pair pair1 = new Pair(resultMatrix[0][0],resultMatrix[1][0]);
			return pair1;
			/*
			System.out.println("midpoint: " + midpointX + " " + midpointY);
			
			//check here and return the midpoint if we cannot create a triangle
			if (distPointToPoint > 2 * this.preferredDistanceOfPass)
			{
				return new Pair(midpointX, midpointY);
			}
			
			//now find slope of line connecting midpoint and point
			double slope;
			if(firstPlayer.getX() == this.ball.getX()){ 
				if(firstPlayer.getY() > this.ball.getY())			
					slope = Integer.MAX_VALUE;
				else if(firstPlayer.getY() < this.ball.getY())	
					slope = Integer.MIN_VALUE;
				else
					slope = 0;
			}
			else
				slope = (firstPlayer.getY()-this.ball.getY()) / (firstPlayer.getX()-this.ball.getX());
			if(slope == 0) {
				if(firstPlayer.getX() - this.ball.getX() > 0){
					slope = 0.01;
				}
				else{
					slope = -0.01;
				}
			}
			
			double perpendicularSlope = (-1) / slope;
			double intercept = midpointY - (perpendicularSlope * midpointX);
			
			System.out.println("slope is "+ slope);
			System.out.println("perpendicularSlope is "+ perpendicularSlope);
			System.out.println("intercept is "+ intercept);
			
			
			
			//finding distance from midpoint to the point
			double distFromMidpoint = Math.sqrt(Math.pow(this.preferredDistanceOfPass,2) - Math.pow(distPointToPoint/2, 2) );
			//use pythagorean theorum + line formula
			//a,b,c for quadratic formula
			double b = -1 * 2 * (midpointX + (intercept + midpointY) * perpendicularSlope);
			double a = Math.pow(perpendicularSlope, 2) + 1;
			double c = Math.pow(distFromMidpoint, 2) - Math.pow(midpointX, 2) - Math.pow(midpointY+intercept, 2);
			
			System.out.println(a + " " + b + " " + c);
			
			
			
			double x1 = ( (-1) * b + Math.sqrt(Math.pow(b, 2) + 4 * a * c) ) / 2 * a;
			double x2 = ( (-1) * b - Math.sqrt(Math.pow(b, 2) + 4 * a * c) ) / 2 * a;
			double y1 = perpendicularSlope * x1 + intercept;
			double y2 = perpendicularSlope * x2 + intercept;
			
			System.out.println("pairs:");
			System.out.println(x1 + " " + y1);
			System.out.println(x2 + " " + y2);

			
			
			
			
			Pair pair1 = new Pair(x1,y1);
			Pair pair2 = new Pair(x2,y2);
			
			//find closer point to player
			double dist1 = this.getDistance(p, pair1);
			double dist2 = this.getDistance(p, pair2);
			
			if (dist1 > dist2)
				return pair2;
			else
				return pair1;
			*/
		}
	}
	
	private Pair OSfindNonTrianglePoint(Player p, int mentality)
	{
		//also use a* search
		//but in this we are calling a* in another method for simplicity
		//because we might change the player to search for defensive mentality for defending
		if (mentality == Constants.defensiveMentality)
		{
			//the defensive player wants to drop behind the two farthest from goal players
			//but if there is a player closer to that position
			//we move that player and send this player to that player's location
			Vector<Player> teammates = this.getTeammates(p);
			Vector<Player> closestToPlayer = this.closestToFarthest(p, teammates);
			Pair goalPair = null;
			if(p.getTeam().equals(team1))
				goalPair = new Pair(fieldCoords.getWidth(), fieldCoords.getHeight()/2);
			else
				goalPair = new Pair(10, fieldCoords.getHeight()/2);
			Vector<Player> closestToGoal = this.closestToFarthest(goalPair, teammates);
			Vector<Player> farthestFromGoal = new Vector<Player>(closestToGoal.size());
			//just revert order
			for (int i=closestToGoal.size()-1; i>=0; i--)
				farthestFromGoal.add(closestToGoal.get(i));
			
			//now find two closest field players for each
			Vector<Player> closestToPlayer2 = new Vector<Player>();
			Vector<Player> farthestFromGoal2 = new Vector<Player>();
			//we can't just cut the vector because the closest player might be a gk
			for (Player teammate : closestToPlayer)
			{
				//skip keeper
				if (!teammate.position.equals(Constants.goalkeeper))
				{
					//fill up until there are 2 players
					if (closestToPlayer2.size() != 2)
						closestToPlayer2.add(teammate);
				}
			}
			
			for (Player teammate : farthestFromGoal)
			{
				//skip keeper
				if (!teammate.position.equals(Constants.goalkeeper))
				{
					//fill up until there are 2 players
					if (farthestFromGoal2.size() != 2)
						farthestFromGoal2.add(teammate);
				}
			}
			
			Player mutualPlayer1 = null;
			Player mutualPlayer2 = null;
			//now compare
			if (closestToPlayer2.contains(farthestFromGoal2.get(0)))
			{
				mutualPlayer1 = farthestFromGoal2.get(0);
			}
			
			if (closestToPlayer2.contains(farthestFromGoal2.get(1)))
			{
				mutualPlayer2 = farthestFromGoal2.get(1);
			}
			
			if (mutualPlayer1 != null && mutualPlayer2 != null)
			{
				//just move defender
				Pair startingPoint = this.OSgetStartingNonTrianglePoint(p, mutualPlayer1, mutualPlayer2, mentality);
				return this.OSnonTriangleAStar(p, startingPoint, mentality, mutualPlayer1, mutualPlayer2);
			} else 
			{
				//we are moving a total of two players
				
				//there is one mutual player
				Player mutualPlayer = null;
				//there will never be a case w/ 5 where both are null
				if (mutualPlayer1 == null)
					mutualPlayer = mutualPlayer2;
				if (mutualPlayer2 == null)
					mutualPlayer = mutualPlayer1;
				
				Player playerToMove = null;
				//see if player is on the ball
				if (mutualPlayer.hasBall())
				{
					//we cannot move this player
					//so move other player farthest from goal
					farthestFromGoal2.remove(mutualPlayer);
					//only element left
					playerToMove = farthestFromGoal2.get(0);
					
					
				} else
				{
					playerToMove = mutualPlayer;
				}
				
				//destination will be the triangle point
				Pair savedLocation = playerToMove.getDestination();
				//now move the player already on the triangle point
				//this is not very good practice because
				//we were initially trying to move player A but end up moving player A and B
				//but Ryo and Casey decided it's fine in this circumstance
				Pair startingPoint = OSgetStartingNonTrianglePoint(playerToMove,playerToMove,mutualPlayer,mentality);
				Pair moveTo = OSnonTriangleAStar(playerToMove, startingPoint, mentality, playerToMove, mutualPlayer);
				
				//set triangle points
				p.setTrianglePoint(true);
				playerToMove.setTrianglePoint(false);
				
				//move other player
				playerToMove.setDestination(moveTo);
				
				//we are making this player move to where the other player was
				return savedLocation;
			}
			
			
	
			
		} else if (mentality == Constants.normalMentality)
		{
			//closest two players
			Vector<Player> orderedTeam = this.closestToFarthest(p, this.getTeammates(p));
			Vector<Player> closestToPlayer = new Vector<Player>(orderedTeam);
			//remove gk
			Player gk = null;
			for (Player teammate : closestToPlayer)
			{
				if (teammate.getPosition().equals(Constants.goalkeeper))
				{
					//don't remove here because it might cause bug?
					gk = teammate;
				}
			}
			closestToPlayer.remove(gk);
			
			Pair startingPoint = this.OSgetStartingNonTrianglePoint(p, closestToPlayer.get(0), closestToPlayer.get(1), mentality);
			
			return OSnonTriangleAStar(p,startingPoint,mentality, closestToPlayer.get(0), closestToPlayer.get(1));
			
		} else //offensive mentality
		{
			//get closest player to goal
			Pair goalLoc = new Pair();
			if(p.getTeam().equals(team1))
				goalLoc = new Pair(fieldCoords.getWidth(), fieldCoords.getHeight()/2);
			else
				goalLoc = new Pair(0, fieldCoords.getHeight()/2);
			Vector<Player> closestToGoal = this.closestToFarthest(goalLoc, this.getTeammates(p));
			Player closest = null;
			if (closestToGoal.get(0).getPosition().equals(Constants.goalkeeper))
			{
				//there is only one keeper so the next player should not be a keeper
				closest = closestToGoal.get(1);
			} else
			{
				closest = closestToGoal.get(0);
			}
			
			Pair startingPoint = this.OSgetStartingNonTrianglePoint(p, closest, null, mentality);
			
			return OSnonTriangleAStar(p,startingPoint,mentality, closest, null);
		}
	}
	
	//unlike the triangle point, we should have this
	//because we are calling this in two different cases:
	//for the player
	//for the player moved because of the player
	
	//WE SHOULD NOT USE OSrightpoint and OSleftpoint
	//because these are dependent on the ball
	//we do not need to use this for non-triangle
	//but is's fine for now...
	private Pair OSnonTriangleAStar(Player p, Pair startPair, int mentality, Player keyPlayer1, Player keyPlayer2)
	{
		Set<Pair> done = new HashSet<Pair>();
		PriorityQueue<Pair> queue = new PriorityQueue(new OSHeurComparator());
		OSHeuristicNonTriangle heur = new OSHeuristicNonTriangle(p,mentality, keyPlayer1, keyPlayer2);
		queue.add(startPair);
		
		while (!queue.isEmpty())
		{
			Pair thisPair = queue.peek();
			double value = thisPair.getValue();
			
			if (value >= 1)
			{
				//choose this
				return thisPair;
			}
			
			queue.remove();
			
			Pair rightPoint = null;
			Pair leftPoint = null;
			Pair behindPoint = null;
			Pair aheadPoint = null;
			
			//the pass course is blocked
			//this is a special case
			if (value == -1)
			{
				//do not need to search the location behind or ahead
				//because it will also be blocked indefinitely
				rightPoint = this.OSrightPoint(thisPair);
				if (this.OSpointValidNonTriangle(done, rightPoint, p))
				{
					heur.calculateHeuristic(rightPoint);
					queue.add(rightPoint);
				}
				
				leftPoint = this.OSleftPoint(thisPair);
				if (this.OSpointValidNonTriangle(done, leftPoint, p))
				{
					heur.calculateHeuristic(leftPoint);
					queue.add(leftPoint);
				}
				
				//store these not in the queue but done
				//so we don't search it again
				aheadPoint = this.OSaheadPoint(thisPair);
				//make sure it's valid, just in case we end up choosing this pair
				if (this.OSpointValidNonTriangle(done, aheadPoint, p))
					done.add(aheadPoint);
				behindPoint = this.OSbehindPoint(thisPair);
				if (this.OSpointValidNonTriangle(done, behindPoint, p))
					done.add(behindPoint);
				
				//move element from queue to done
				done.add(thisPair);
				
				continue;
			}
			
			rightPoint = this.OSrightPoint(thisPair);
			if (this.OSpointValidNonTriangle(done, rightPoint, p))
			{
				heur.calculateHeuristic(rightPoint);
				queue.add(rightPoint);
			}
			leftPoint = this.OSleftPoint(thisPair);
			if (this.OSpointValidNonTriangle(done, leftPoint, p))
			{
				heur.calculateHeuristic(leftPoint);
				queue.add(leftPoint);
			}
			aheadPoint = this.OSaheadPoint(thisPair);
			if (this.OSpointValidNonTriangle(done, aheadPoint, p))
			{
				heur.calculateHeuristic(aheadPoint);
				queue.add(aheadPoint);
			}
			behindPoint = this.OSbehindPoint(thisPair);
			if (this.OSpointValidNonTriangle(done, behindPoint, p))
			{
				heur.calculateHeuristic(behindPoint);
				queue.add(behindPoint);
			}
			
			//done with this pair
			done.add(thisPair);
			
			//move onto next pair in queue
		}
		
		//if not found
		//order and return highest heuristic
		for(Pair donePair : done)
		{
			//a bit expensive but
			//practically should not get to this code
			//because a soccer field will always have space
			queue.add(donePair);
		}
		return queue.peek();
	}
	
	private class OSHeuristicNonTriangle
	{
		Player player;
		int mentality;
		//we are basing heuristic on these two players
		//(or one if offensive)
		Player keyPlayer1;
		Player keyPlayer2;
		private Pair goalLocation;

		//various weights
		//should equal 1
		private final double goalDistanceWeight = 0.8;
		private final double basePositionWeight = 0.2;
		
		
		public OSHeuristicNonTriangle(Player player, int mentality, Player keyPlayer1, Player keyPlayer2)
		{
			this.player = player;
			this.mentality = mentality;
			this.keyPlayer1 = keyPlayer1;
			this.keyPlayer2 = keyPlayer2;
			if(player.getTeam().equals(team1)) goalLocation = new Pair(fieldCoords.getWidth(), fieldCoords.getHeight()/2);
			else goalLocation = new Pair(0, fieldCoords.getHeight()/2);
		}
				
		//sets the final heuristic in the pair!!
		public void calculateHeuristic(Pair pair)
		{
			double heur = 0;
			
			if (OSisOpenSpace(this.player.getTeam(),pair))
			{
				heur += 1.0;
			}
			
			//negatives
			//try to find a pass course to one of the key players
			if (this.mentality == Constants.defensiveMentality || this.mentality == Constants.normalMentality)
			{
				if (PlayerAI.this.pathObstructed(pair,keyPlayer1))
				{
					heur -= 0.25;
				}
				
				if (PlayerAI.this.pathObstructed(pair,keyPlayer1))
				{
					heur -= 0.25;
				}
				//note we don't return -1 to indicate that both pass courses are blocked here
				//because since there are two players we are looking at, a blocked pass course
				//for one player is irrelevant to the availability of a pass course the other
			} else //offensive mentality
			{
				//we are comparing one player so it's fine to do return -1
				if (PlayerAI.this.pathObstructed(pair,keyPlayer1))
				{
					pair.setValue(-1);
					return;
				}
			}
			
			//I think the weights also apply in the same way to the non-triangle points too?
			//this part is pretty much copy pasted
			//distance from goal
			double baseDistance = PlayerAI.this.getDistance(ball, goalLocation);
			double thisDistance = PlayerAI.this.getDistance(pair, goalLocation);
			
			//Diff just for reference
			// Goal   A     B     BaseDistance   C     D
			// thisD   1/4   3/4      1          5/4    7/4
			double diff = thisDistance / baseDistance;
			//Weight
			// 0 <= x < 1
			double weight = 0;
			if (mentality == Constants.offensiveMentality)
			{
				//closer to goal, the better
				weight = 1 - diff;
				if (weight < 0)
				{
					weight = 0;
				}
			} else if (mentality == Constants.normalMentality)
			{
				//no weight based on position
				weight = 0;
			} else if (mentality == Constants.defensiveMentality)
			{
				if (diff == 1)
				{
					//this should be preferred most?
					weight = 0.9;
				}
				else if (diff > 0)
				{
					//never go closer to goal
					weight = 0;
				} else
				{
					weight = 1 / diff;
				}
			}
			
			// 0 <= x  < 0.?
			double weightedWeight = goalDistanceWeight * weight;
			heur += weightedWeight;
			
			//base position
			double dist = PlayerAI.this.getDistance(new Pair(player.getBaseX(),player.getBaseY()), pair);
			if (dist == 0)
			{
				//max
				//will be equal to a distance of 1 in else condition
				//we do this to prevent weight from becoming 1
				weight = 1 - 1/maximumDistanceFromBasePosition;
			} else
			{
				weight = 1 - dist / maximumDistanceFromBasePosition;
			}
			weightedWeight = weight * basePositionWeight;
			
			heur += weightedWeight;
			pair.setValue(heur);
			return;
		}
	}
	
	//slightly different than triangle point
	private Pair OSgetStartingNonTrianglePoint(Player p, Player firstPoint, Player secondPoint, int mentality)
	{
		//only one point
		//find shortest path like 
		//triangle point getting 1st point
		//but between player and most progressed player
		//for offensive (mentality = Constants.offensiveMentality)
		if (secondPoint == null)
		{
			//start shortest path between player and ball
			//at the preferred distance of passes
			double distX = p.getX() - firstPoint.getX();
			double distY = p.getY() - firstPoint.getY();
			double squared = Math.pow(distX, 2) + Math.pow(distY, 2);
			double dist = Math.sqrt(squared);
			//it's OK for distX or distY to be negative??
			double xLoc = preferredDistanceOfPass * distX / dist;
			double yLoc = preferredDistanceOfPass * distY / dist;
			
			double x = xLoc + firstPoint.getX();
			double y = yLoc + firstPoint.getY();
			
			if (!this.fieldCoords.isInField(x,y))
			{
				//adjust point if not on field
				double leftBound = this.fieldCoords.getLeftBound();
				double rightBound = this.fieldCoords.getRightBound();
				double upperBound = this.fieldCoords.getUpperBound();
				double lowerBound = this.fieldCoords.getLowerBound();
				
				if (x <= leftBound)
					x = leftBound + 1;
				if (x >= rightBound)
					x = rightBound - 1;
				if (y <= upperBound)
					y = upperBound + 1;
				if (y >= lowerBound)
					y = lowerBound - 1;
					
			}
			
			return new Pair(x,y);
		} else
		{
			//there will be two points to consider here,
			//points where the distance to the two other points = preferred pass distance
			//but choose the closest to player for normal mentality
			//and closest to goal for defensive mentality
			
			//we are assuming that the distance between the two points are less than 2 * preferred pass distance
			//so if it's more, start at the midpoint
			double distPointToPoint = this.getDistance(firstPoint, secondPoint);
			
			//first find midpoint
			//absolute val because points cannot be negative by design
			double midpointX = (firstPoint.getX()+secondPoint.getX())/2;
			double midpointY = (firstPoint.getY()+secondPoint.getY())/2;
			
			double x1 = firstPoint.getX();
			double x2 = secondPoint.getX();
			double y1 = firstPoint.getY();
			double y2 = secondPoint.getY();
			//Matrix matrixCompute = new Matrix();
			double[][] Xmatrix = new double[3][3];
			double theta = Math.atan((y2-y1)/(x2-x1));
			Xmatrix[0][0] = Math.cos(theta+Math.PI/2);
			Xmatrix[0][1] = -Math.sin(theta+Math.PI/2);
			Xmatrix[0][2] = midpointX;
			Xmatrix[1][0] = Math.sin(theta+Math.PI/2);
			Xmatrix[1][1] = -Math.cos(theta+Math.PI/2);
			Xmatrix[1][2] = midpointY;
			Xmatrix[2][0] = 0;
			Xmatrix[2][1] = 0;
			Xmatrix[2][2] = 1;
			double[][] localMat1 = new double[3][1];
			localMat1[0][0] = this.preferredDistanceOfPass;
			localMat1[1][0] = 0;
			localMat1[2][0] = 1;
			double[][] localMat2 = new double[3][1];
			localMat2[0][0] = -this.preferredDistanceOfPass;
			localMat2[1][0] = 0;
			localMat2[2][0] = 1;
			
			double[][] resultMatrix1 = new double[3][1];
			double[][] resultMatrix2 = new double[3][1];
			resultMatrix1 = Matrix.multiply(Xmatrix, localMat1);
			resultMatrix2 = Matrix.multiply(Xmatrix, localMat2);
			Pair pair1 = new Pair(resultMatrix1[0][0],resultMatrix1[1][0]);
			Pair pair2 = new Pair(resultMatrix2[0][0],resultMatrix2[1][0]);
			
			if (mentality == Constants.normalMentality)
			{
				//find closer point
				double dist1 = this.getDistance(p, pair1);
				double dist2 = this.getDistance(p, pair2);
				
				if (dist1 > dist2)
					return pair2;
				else
					return pair1;
			} else //defensive mentality
			{
				//we find distance, not from player but goal
				//this is opponent's goal??
				Pair goalPair = null;
				double dist1 = getDistance(goalPair, pair1);
				double dist2 = getDistance(goalPair,pair2);
				
				if (dist1 > dist2)
					return pair1;
				else
					return pair2;
			}
			
			/*
			//check here and return the midpoint if we cannot create a triangle
			if (distPointToPoint > 2 * this.preferredDistanceOfPass)
			{
				return new Pair(midpointX, midpointY);
			}
			
			//now find slope of line connecting midpoint and point
			double slope;
			if(firstPoint.getX() == secondPoint.getX()){ 
				if(firstPoint.getY() > secondPoint.getY())			
					slope = Integer.MAX_VALUE;
				else if(firstPoint.getY() < secondPoint.getY())	
					slope = Integer.MIN_VALUE;
				else
					slope = 0;
			}
			else
				slope = (firstPoint.getY()-secondPoint.getY()) / (firstPoint.getX()-secondPoint.getX());
			
			if(slope == 0) {
				if(firstPoint.getX() - secondPoint.getX() > 0){
					slope = 0.01;
				}
				else{
					slope = -0.01;
				}
			}
			
			double perpendicularSlope = (-1) / slope;
			double intercept = midpointY - (perpendicularSlope * midpointX);
			
			
			System.out.println("perpendicularSlope is "+ perpendicularSlope);
			System.out.println("intercept is "+ intercept);
			
			
			//finding distance from midpoint to the point
			double distFromMidpoint = Math.sqrt(Math.pow(this.preferredDistanceOfPass,2) - Math.pow(distPointToPoint/2, 2) );
			//use pythagorean theorum + line formula
			//a,b,c for quadratic formula
			double b = -1 * 2 * (midpointX + (intercept + midpointY) * perpendicularSlope);
			double a = Math.pow(perpendicularSlope, 2) + 1;
			double c = Math.pow(distFromMidpoint, 2) - Math.pow(midpointX, 2) - Math.pow(midpointY+intercept, 2);
			
			double x1 = ( (-1) * b + Math.sqrt(Math.pow(b, 2) + 4 * a * c) ) / 2 * a;
			double x2 = ( (-1) * b - Math.sqrt(Math.pow(b, 2) + 4 * a * c) ) / 2 * a;
			double y1 = perpendicularSlope * x1 + intercept;
			double y2 = perpendicularSlope * x2 + intercept;
			Pair pair1 = new Pair(x1,y1);
			Pair pair2 = new Pair(x2,y2);
			
			if (mentality == Constants.normalMentality)
			{
				//find closer point
				double dist1 = this.getDistance(p, pair1);
				double dist2 = this.getDistance(p, pair2);
				
				if (dist1 > dist2)
					return pair2;
				else
					return pair1;
			} else //defensive mentality
			{
				//we find distance, not from player but goal
				//this is opponent's goal??
				Pair goalPair = null;
				double dist1 = getDistance(goalPair, pair1);
				double dist2 = getDistance(goalPair,pair2);
				
				if (dist1 > dist2)
					return pair1;
				else
					return pair2;
			}*/
		}
	}
	
	private boolean OSpointValidNonTriangle(Set<Pair> done, Pair pair, Player player)
	{
		//Valid = //not exceeding max distance of pass(don't need) + 
		// point exists on the field
		// (technically it's fine, even recommended for wingers to step outside
		// the field to receive a pass) +
		// point not too close to another player +
		// not covered already
		
		if (!done.contains(pair))
			return false;
		
		if (!fieldCoords.isInField(pair))
			return false;
		
		//double distFromBall = this.getDistance(pair, ball);
		//if (distFromBall > this.maximumDistanceOfPass)
			//return false;
		
		Vector<Player> teammates = this.getTeammates(player);
		for (Player teammate : teammates)
		{
			if (this.getDistance(pair, teammate) < this.minimumDistanceFromPlayer)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private Pair keepFormationOffense(Player p){		
		Team t = p.getTeam();
		if(t.equals(team1)){
			Player mainPlayer = t.getMainPlayer();
			if(p.equals(mainPlayer)) return new Pair(p.getX(), p.getY());
			if(p.getPosition().equals(Constants.goalkeeper)) {
				return new Pair(p.getX(), p.getY());
			}
			else{
				int des_x = p.getX();
				int des_y = p.getY();
				if(show(p, Constants.normalMentality) != null){
					des_x = show(p, Constants.normalMentality).getX();
					des_y = show(p, Constants.normalMentality).getY();
				}
				des_y = (des_y + p.getBaseY())/2;
				
				if(mainPlayer.getPosition().equals(Constants.forward)){
					
					if(mainPlayer.getX() < (fieldCoords.getHalfwayX()/2)) {
						des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;	
						return new Pair(des_x, des_y);
					}
					else{				
						if(p.getPosition().equals(Constants.forward)){ 
							des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;	
							return new Pair(des_x, des_y);
						}
						else if(p.getPosition().equals(Constants.midfield)){ 
							des_x = (des_x + (mainPlayer.getX() - 150) + p.getBaseX()) /3;
							return new Pair(des_x, des_y);
						}
						else {
							des_x = (des_x + (mainPlayer.getX() - 250) + p.getBaseX())/3;						
							return new Pair(des_x, des_y);				
						}
					}

				}

				if(p.getPosition().equals(Constants.defender)){
					int x;
					if(mainPlayer.getPosition().equals(Constants.defender)) x = mainPlayer.getX();
					else if(mainPlayer.getPosition().equals(Constants.midfield)) x = mainPlayer.getX() - 250;
					else x = mainPlayer.getX() - 480;
					
					x = (des_x + x + p.getBaseX())/3;	
					if(x <= 10) x = 10;

					return new Pair(x, des_y);
								
				}
				else if(p.getPosition().equals(Constants.midfield)){
					if(mainPlayer.getPosition().equals(Constants.defender)){
						int x = mainPlayer.getX() + 250;
						x = (des_x + x + p.getBaseX())/3;	
						
						if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);

						return new Pair(x, des_y);
					}
					else if(mainPlayer.getPosition().equals(Constants.midfield)){
						des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;
						return new Pair(des_x, des_y);
					}
					else{
						int x = mainPlayer.getX() - 230;
						x = (des_x + x + p.getBaseX())/3;	
						if(x <= 10) x = 10;

						return new Pair(x, des_y);
					}
				}
				else{
					int x;
					if(mainPlayer.getPosition().equals(Constants.defender)) x = mainPlayer.getX() + 480;
					else if(mainPlayer.getPosition().equals(Constants.midfield)) x = mainPlayer.getX() + 230;
					else x = mainPlayer.getX();
					x = (des_x + x + p.getBaseX())/3;
					if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);

					return new Pair(x, des_y);
				}
			}
		}
		else{ //team2 is attacking
			Player mainPlayer = t.getMainPlayer();
			if(p.equals(mainPlayer)) return new Pair(p.getX(), p.getY());
			if(p.getPosition().equals(Constants.goalkeeper)) {
				return new Pair(p.getX(), p.getY());
			}
			else{
				int des_x = p.getX();
				int des_y = p.getY();
				if(show(p, Constants.normalMentality) != null){
					des_x = show(p, Constants.normalMentality).getX();
					des_y = show(p, Constants.normalMentality).getY();
				}		
				des_y = (des_y + p.getBaseY())/2;
				
				if(mainPlayer.getPosition().equals(Constants.forward)){
					
					if(mainPlayer.getX() < (fieldCoords.getHalfwayX()/2)) {
						des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;	
						return new Pair(des_x, des_y);
					}
					else{				
						if(p.getPosition().equals(Constants.forward)){ 
							des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;	
							return new Pair(des_x, des_y);
						}
						else if(p.getPosition().equals(Constants.midfield)){ 
							des_x = (des_x + (mainPlayer.getX() + 150) + p.getBaseX()) /3;
							return new Pair(des_x, des_y);
						}
						else {
							des_x = (des_x + (mainPlayer.getX() + 250) + p.getBaseX())/3;						
							return new Pair(des_x, des_y);				
						}
					}

				}

				if(p.getPosition().equals(Constants.defender)){
					int x;
					if(mainPlayer.getPosition().equals(Constants.defender)) x = mainPlayer.getX();
					else if(mainPlayer.getPosition().equals(Constants.midfield)) x = mainPlayer.getX() - 250;
					else x = mainPlayer.getX() + 480;
					
					x = (des_x + x + p.getBaseX())/3;	
					if(x <= 10) x = 10;

					return new Pair(x, des_y);
								
				}
				else if(p.getPosition().equals(Constants.midfield)){
					if(mainPlayer.getPosition().equals(Constants.defender)){
						int x = mainPlayer.getX() - 250;
						x = (des_x + x + p.getBaseX())/3;	
						
						if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);

						return new Pair(x, des_y);
					}
					else if(mainPlayer.getPosition().equals(Constants.midfield)){
						des_x = (des_x + mainPlayer.getX() + p.getBaseX())/3;
						return new Pair(des_x, des_y);
					}
					else{
						int x = mainPlayer.getX() + 230;
						x = (des_x + x + p.getBaseX())/3;	
						if(x <= 10) x = 10;

						return new Pair(x, des_y);
					}
				}
				else{
					int x;
					if(mainPlayer.getPosition().equals(Constants.defender)) x = mainPlayer.getX() + 480;
					else if(mainPlayer.getPosition().equals(Constants.midfield)) x = mainPlayer.getX() + 230;
					else x = mainPlayer.getX();
					x = (des_x + x + p.getBaseX())/3;
					if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);

					return new Pair(x, des_y);
				}
			}
		}
		
		
		
		
	}
	
	private Pair keepFormationDefense(Player p){
		
		Team t = p.getTeam();
		Vector<Player> players = t.getStarting();
		
		
		//if team2 is defending
		if(t.equals(team2)){
			int max = -1;
			for(Player pl : players){
				if(!pl.getPosition().equals(Constants.goalkeeper) && pl.getX() > max){
					max = pl.getX();
				}
			}
			if(max < ball.getX()){
				return new Pair(ball.getX() + 30, (4*ball.getY() + p.getBaseY())/5);				
			}

			Player mainPlayer = t.getMainPlayer();
			if(p.equals(mainPlayer)) return new Pair(p.getX(), p.getY());
			else{
				if(p.getPosition().equals(Constants.goalkeeper)) {
					return new Pair(p.getX(), p.getY());
				}
				else{
					int des_x = assignMarking(p).getX();
					int des_y = assignMarking(p).getY();
					des_y = (des_y + 2*p.getBaseY())/3;
					if(des_y <= 10) des_y = 10;
					else if(des_y >= fieldCoords.getHeight() - 10) des_y = (int) (fieldCoords.getHeight() - 10);
					
					if(mainPlayer.getPosition().equals(Constants.defender)){
						if(mainPlayer.getX() > (3*fieldCoords.getHalfwayX()/4)){
							return new Pair(mainPlayer.getX(), p.getBaseY());
						}
						else{
							if(p.getPosition().equals(Constants.defender)){					
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else if(p.getPosition().equals(Constants.midfield)){					
								des_x = (des_x + 2*(mainPlayer.getX() - 180) + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else {
								des_x = (des_x + 2*(mainPlayer.getX() - 360) + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							
							}
						}
					}
					else{
						if(p.getPosition().equals(Constants.defender)){
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								int x;
								x = mainPlayer.getX() + 150;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){ //mainPlayer.getPosition().equals(Constants.forward)
								int x;
								x = mainPlayer.getX() + 280;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() - 150;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}
						else if(p.getPosition().equals(Constants.midfield)){
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x>=fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								if(des_x<=10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){
								int x;
								x = mainPlayer.getX() + 130;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								if(x<=10) x = 10;
								return new Pair(x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() - 300;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}
						else{
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								int x;
								x = mainPlayer.getX() - 130;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								if(x<=10) x = 10;
								return new Pair(x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x>=fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								if(des_x<=10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() - 430;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}	
					}
				}
			}
		}
		else{ //if team1 is defending
			int min = 5000;
			for(Player pl : players){
				if(!pl.getPosition().equals(Constants.goalkeeper) && pl.getX() < min){
					min = pl.getX();
				}
			}
			if(min > ball.getX()){
				return new Pair(ball.getX() - 30, (4*ball.getY() + p.getBaseY())/5);
			}
		
			Player mainPlayer = t.getMainPlayer();
			if(p.equals(mainPlayer)) return new Pair(p.getX(), p.getY());
			else{
				if(p.getPosition().equals(Constants.goalkeeper)) {
					return new Pair(p.getX(), p.getY());
				}
				else{
					int des_x = assignMarking(p).getX();
					int des_y = assignMarking(p).getY();
					des_y = (des_y + 2*p.getBaseY())/3;
					if(des_y <= 10) des_y = 10;
					else if(des_y >= fieldCoords.getHeight() - 10) des_y = (int) (fieldCoords.getHeight() - 10);
					
					if(mainPlayer.getPosition().equals(Constants.defender)){
						if(mainPlayer.getX() < (fieldCoords.getHalfwayX()/4)){
							return new Pair(mainPlayer.getX(), p.getBaseY());
						}
						else{
							if(p.getPosition().equals(Constants.defender)){					
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else if(p.getPosition().equals(Constants.midfield)){					
								des_x = (des_x + 2*(mainPlayer.getX() + 180) + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else {
								des_x = (des_x + 2*(mainPlayer.getX() + 360) + p.getBaseX())/4;
								if(des_x >= fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								else if(des_x <= 10) des_x = 10;
								return new Pair(des_x, des_y);
							
							}
						}
					}
					else{
						if(p.getPosition().equals(Constants.defender)){
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								int x;
								x = mainPlayer.getX() - 150;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){ //mainPlayer.getPosition().equals(Constants.forward)
								int x;
								x = mainPlayer.getX() - 280;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() + 150;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}
						else if(p.getPosition().equals(Constants.midfield)){
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x>=fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								if(des_x<=10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){
								int x;
								x = mainPlayer.getX() - 130;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								if(x<=10) x = 10;
								return new Pair(x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() + 300;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}
						else{
							if(mainPlayer.getPosition().equals(Constants.midfield)){
								int x;
								x = mainPlayer.getX() + 130;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								if(x<=10) x = 10;
								return new Pair(x, des_y);
							}
							else if(mainPlayer.getPosition().equals(Constants.forward)){
								des_x = (des_x + 2*mainPlayer.getX() + p.getBaseX())/4;
								if(des_x>=fieldCoords.getWidth() - 10) des_x = (int) (fieldCoords.getWidth() - 10);
								if(des_x<=10) des_x = 10;
								return new Pair(des_x, des_y);
							}
							else{ //gk
								int x;
								x = mainPlayer.getX() + 430;
								x = (2*x + des_x + p.getBaseX())/4;
								if(x>=fieldCoords.getWidth() - 10) x = (int) (fieldCoords.getWidth() - 10);
								else if(x <= 10) x = 10;
								return new Pair(x, des_y);
							}
						}	
					}
				}
			}
		}
	}
	
	private Pair assignMarking(Player p){
		
		Team t = p.getTeam();
		Team otherTeam;
		if(t == team1) otherTeam = team2;
		else otherTeam = team1;
		
		Vector<Player> attackingPlayers = otherTeam.getStarting();
		Vector<Player> defendingPlayers = t.getStarting();
		
		if(t.equals(team2)) { //team2 is defending
			if(p.getPosition().equals(Constants.goalkeeper)) return new Pair(p.getX(), p.getY());
			else if(p.getPosition().equals(Constants.defender)){
				
				int max_x = -1;
				Player mostDangerous = null;
				for(Player pl : attackingPlayers){
					if(pl.getX() > max_x){
						mostDangerous = pl;
						max_x = pl.getX();
					}
				}
				
				p.setMarking(mostDangerous);
				
				return new Pair(mostDangerous.getX() + 60, mostDangerous.getY() - 60);

			}
			else{
	
				Vector<Player> priority = closestToFarthest(p, attackingPlayers);
				Player toMark = null;
				boolean isMarked = false;
				for(Player att : priority){
					for(Player def : defendingPlayers){
						if(def.getMarking() != null){
							if(def.getMarking().equals(att)){
								isMarked = true;
								break;
							}
						}
					}
					if(isMarked == false){
						toMark = att;
						break;
					}
					isMarked = false; 
				}
				
				if(toMark == null) toMark = priority.elementAt(0);
				if(getDistance(p, toMark) > 500) toMark = priority.elementAt(0);
				
				p.setMarking(toMark);
				
				Pair destination = new Pair(toMark.getX() + 30, toMark.getY() - 60);
				
				if(destination.getX() <= 10) destination.setX(10);
				else if(destination.getX() >= fieldCoords.getWidth() - 10) destination.setX((int) (fieldCoords.getWidth() - 10));
				if(destination.getY() <= 10) destination.setY(10);
				else if(destination.getY() >= fieldCoords.getHeight() - 10) destination.setY((int) (fieldCoords.getHeight() - 10));
				
				return destination;
			}
		}
		else{ //team 1 is defending
			if(p.getPosition().equals(Constants.goalkeeper)) return new Pair(p.getX(), p.getY());
			else if(p.getPosition().equals(Constants.defender)){
				
				int min_x = 5000;
				Player mostDangerous = null;
				for(Player pl : attackingPlayers){
					if(pl.getX() < min_x){
						mostDangerous = pl;
						min_x = pl.getX();
					}
				}
				
				p.setMarking(mostDangerous);
				
				return new Pair(mostDangerous.getX() - 60, mostDangerous.getY() + 60);

			}
			else{
	
				Vector<Player> priority = closestToFarthest(p, attackingPlayers);
				Player toMark = null;
				boolean isMarked = false;
				for(Player att : priority){
					for(Player def : defendingPlayers){
						if(def.getMarking() != null){
							if(def.getMarking().equals(att)){
								isMarked = true;
								break;
							}
						}
					}
					if(isMarked == false){
						toMark = att;
						break;
					}
					isMarked = false; 
				}
				
				if(toMark == null) toMark = priority.elementAt(0);
				if(getDistance(p, toMark) > 500) toMark = priority.elementAt(0);
				
				p.setMarking(toMark);
				
				Pair destination = new Pair(toMark.getX() - 30, toMark.getY() + 60);
				
				if(destination.getX() <= 10) destination.setX(10);
				else if(destination.getX() >= fieldCoords.getWidth() - 10) destination.setX((int) (fieldCoords.getWidth() - 10));
				if(destination.getY() <= 10) destination.setY(10);
				else if(destination.getY() >= fieldCoords.getHeight() - 10) destination.setY((int) (fieldCoords.getHeight() - 10));
				
				return destination;
			}
		}
	}

}
