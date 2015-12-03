package game;


import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import client.ClientPanels;
import client.FieldPanel;
import util.Constants;
import util.Pair;

public class GameSimulation extends Thread{
	private Ball ball;
	private Team team1;
	private Team team2;
	private PlayerAI ai;
	private int team1score = 0;
	private int team2score = 0;
	private int time = 0;
	
	//use to set the team the user actually controls
	private Team mainTeam;
	private Player mainPlayer;
	private Player pastPlayer;
	
	private GamePanelCoords gpc;
	
	private boolean foulAllowed;
	private double friction; //friction depends on the type of the field.
	private Color fieldColor = Color.WHITE; //default white for now
	
	private int passStrength=0;
	private int passOrientation=-1;
	
	private int shootStrength=0;
	private int shootOrientationX=0;
	private int shootOrientationY=0;
	
	private boolean isTackle=false;
	private int tackleFailed;
	
	private FieldPanel field;
	
	//private long pass_time_before;
	//private long pass_time_after;
	//private long shoot_time_before;
	//private long shoot_time_after;
	
	public GameSimulation(){
		ball = new Ball();
		team1 = new Team("Manchester United");
		team2 = new Team("Chelsea");
		ai = null;
		
		
	}
	
	public void constructAI(){
		ai = new PlayerAI(team1, team2, ball, gpc);
	}
	
	public void setFieldPanel(FieldPanel field){
		this.field = field;
	}
	
	//GET functions
	public Team getTeam1(){
		return team1;
	}
	public Team getTeam2(){
		return team2;
	}
	public Team getMainTeam(){
		return mainTeam;
	}
	
	public Team getOtherTeam(){
		if(mainTeam == team1) return team2;
		else return team1;
	}
	
	public int getTeam1score()
	{
		return team1score;
	}
	
	public int getTeam2score()
	{
		return team2score;
	}
	
	public Team getTeam(int num)
	{
		if (num == 1)
			return team1;
		else if (num == 2)
			return team2;
		else
			return null;
	}
	
	public Ball getBall(){
		return ball;
	}
	
	public Color getFieldColor()
	{
		return this.fieldColor;
	}
	
	public int getTime()
	{
		return this.time;
	}
	//SET functions
	
	public void setBall(Ball ball){
		this.ball = ball;
	}
	
	public void setteam1(Team t1){
		team1=t1;
		if(ClientPanels.onlineGame){
			if(ClientPanels.isServer){
				mainTeam=team1;
				mainPlayer=mainTeam.getMainPlayer();
			}
		}
		else{
			mainTeam=team1;
			mainPlayer=mainTeam.getMainPlayer();
		}
	}
	public boolean isClient(){
		if(ClientPanels.onlineGame&&!ClientPanels.isServer){
			return true;
		}
		else{
			return false;
		}
	}
	public void setteam2(Team chosenAITeam) {
		team2=chosenAITeam;	
		if(ClientPanels.onlineGame && !ClientPanels.isServer){
			mainTeam = team2;
			mainPlayer = mainTeam.getMainPlayer();
		}
		//mainTeam=team2;
		//mainPlayer=mainTeam.getMainPlayer();
	}
	public void setMainTeam(Team team){
		mainTeam=team;
	}
	public void setTeam1Score(int score){
		team1score = score;
		//field.setTeam1Score(score);
	}
	public void setTeam2Score(int score){
		team2score = score;
		//field.setTeam2Score(score);
	}
	
	//OTHER functions
	/*public void setPassBeforeTime(long time){
		this.pass_time_before = time;
	}
	
	public void setPassAfterTime(long time){
		this.pass_time_after = time;
	}
	
	public void setShootBeforeTime(long time){
		this.shoot_time_before = time;
	}
	
	public void setShootAfterTime(long time){
		this.shoot_time_after = time;
	}*/
	
	
	public void changeFormation(int teamNum, int [] formation)
	{
		Team team = this.getTeam(teamNum);
	}
	
	public void prepareForGoalKick(){
		//do something with ball?
		//or does it not matter because keeper has ball?
		team1.resetToBasePosition();
		team2.resetToBasePosition();
	}
	
	public void prepareForKickOff(Team t)
	{
		ball.reset();
		//reset every variable
		passStrength=0;
		passOrientation=-1;
		shootStrength=0;
		shootOrientationX=0;
		shootOrientationY=0;
		
		isTackle=false;
		tackleFailed=0;
		
		if(t==team1){
			resetForKickOff(team1,true);
			resetForKickOff(team2,false);
		}
		else{
			resetForKickOff(team1,false);
			resetForKickOff(team2,true);
		}
	}
	
	public void resetForKickOff(Team t, boolean startsWithBall)
	{
		//first reset everything including ball
		t.resetToBasePosition();
		Vector<Player> players = t.getStarting();
		//will use these later
		Player circleP1 = null;
		Player circleP2 = null;
		//we neeed to have two players on the ball at the start
		if (startsWithBall)
		{
			if (t.equals(team1))
			{
				int maxX = 0;
				int maxX2 = 0;
				//find two most forward players
				for (Player p : players)
				{
					//will be base position
					int pX = p.getX();
					if (pX > maxX)
					{
						//shift over
						maxX2 = maxX;
						circleP2 = circleP1;
						//set new value
						maxX = pX;
						circleP1 = p;
					} else if (pX > maxX2)
					{
						//just set
						maxX2 = pX;
						circleP2 = p;
					}
				}
			} else //team2
			{
				int minX = 10000;
				int minX2 = 10000;
				//find two most forward players
				for (Player p : players)
				{
					//will be base position
					int pX = p.getX();
					if (pX < minX)
					{
						//shift over
						minX2 = minX;
						circleP2 = circleP1;
						//set new value
						minX = pX;
						circleP1 = p;
					} else if (pX < minX2)
					{
						//just set
						minX2 = pX;
						circleP2 = p;
					}
				}
			}
			
			//set their positions to center circle
			//to make it realistic have the player whose original position
			//is to the left be on the left
			int deltaY = 20;
			if (circleP1.getY() > circleP2.getY())
				deltaY *= 1;
			else
				deltaY *= (-1);
			
			int whichSide = 5;
			if (t.equals(team1))
			{
				whichSide *= (-1);
				//also invert deltaY
				
			}	
			circleP1.setLocation((int)gpc.getHalfwayX()+whichSide,(int)gpc.getHalfwayY()+deltaY);
			circleP2.setLocation((int)gpc.getHalfwayX()+whichSide,(int)gpc.getHalfwayY()-deltaY);
			
		}
		
		//set players relative to base position
		//different calculation for teams
		for (Player toSet : players)
		{
			if (toSet.equals(circleP1) || toSet.equals(circleP2))
				continue;
			if (toSet.getPosition().equals(Constants.goalkeeper))
				continue;
			//try setting
			//y should remain constant
			int toSetY = toSet.getY();
			int toSetX = 0;
			if (t.equals(team1))
			{
				toSetX = toSet.getX() / 2;
			} else if (t.equals(team2))
			{
				toSetX = ( (int)gpc.getWidth()+toSet.getX() ) / 2;
			}
			//if in center circle move it out
			//divide into four conditions for each quadrant
			double r = gpc.getCentreRadiusX();
			//top left
			if (toSetY >= (gpc.getHalfwayY() - r) && toSetY <= gpc.getHalfwayY()
					&& toSetX >= (gpc.getHalfwayX()-r) && toSetX <= gpc.getHalfwayX())
			{
				toSetY = ((int)gpc.getHalfwayY() - (int)r) - 5;
			}
			//top right
			if (toSetY >= (gpc.getHalfwayY() - r) && toSetY <= gpc.getHalfwayY()
					&& toSetX >= gpc.getHalfwayX() && toSetX <= (gpc.getHalfwayX()+r))
			{
				toSetY = ((int)gpc.getHalfwayY() - (int)r) - 5;
			}
			//bottom left
			if (toSetY >= gpc.getHalfwayY() && toSetY <= (gpc.getHalfwayY() + r)
					&& toSetX >= (gpc.getHalfwayX()-r) && toSetX <= gpc.getHalfwayX())
			{
				toSetY = ((int)gpc.getHalfwayY() + (int)r) + 5;
			}
			//bottom right
			if (toSetY >= gpc.getHalfwayY() && toSetY <= (gpc.getHalfwayY()+r)
					&& toSetX >= gpc.getHalfwayX() && toSetX <= (gpc.getHalfwayX()+r))
			{
				toSetY = ((int)gpc.getHalfwayY() + (int)r) + 5;
			}
			
			toSet.setLocation(toSetX,toSetY);
		}
	}
	
	public void run(){
		
	}
	

	public void setUp(String weather, String field, String time2, String foul) {
		//set up friction
		if(weather.equals(Constants.sunny)){
			friction=Constants.sunnyFriction;
		}
		else if(weather.equals(Constants.rainy)){
			friction=Constants.rainnyFriction;
		}
		else if(weather.equals(Constants.snowy)){
			friction=Constants.snowyFriction;
		}
		
		//set up field
		
		if(field.equals(Constants.normalGrass)){
			fieldColor = new Color(0,123,12);
		}
		else if(field.equals(Constants.muddyGrass)){
			fieldColor = new Color(128,128,0);
		}
		else if(field.equals(Constants.street)){
			fieldColor = new Color(169,169,169);
		} 
		
		
		//set up time 
		Scanner sc3 = new Scanner(time2);
		time=sc3.nextInt();
		sc3.close();
		
		//set up foul
		foul=foul.toLowerCase();
		if(foul.equals("yes")) {
			foulAllowed=true;
		}
		else{
			foulAllowed=false;
		}
	}
	
	
	public void setAIFormation(){//this is just for testing purposes.
		int numofForward=0;
		int numofMid=0;
		int numofDe=0;
		int numofGo=0;
		
		//initialize starting line up.
		Vector<Player> starts = new Vector<Player>();
		for(Player p : team2.getSubs()){
			if(starts.size()==5) break;
			if(p.getPosition().equals(Constants.forward)&&numofForward<1){
				starts.add(p);
				numofForward++;
			}
			else if(p.getPosition().equals(Constants.midfield)&&numofMid<1){
				starts.add(p);
				numofMid++;
			}
			else if(p.getPosition().equals(Constants.defender)&&numofDe<2){
				starts.add(p);
				numofDe++;
			}
			else if(p.getPosition().equals(Constants.goalkeeper)&&numofGo<1){
				starts.add(p);
				numofGo++;
			}
		}
		team2.setStarting(starts);
		
		//set position
		int fwCt=0;
		int mfCt=0;
		int dfCt=0;
		int totalFwCt=1;
		int totalMfCt=1;
		int totalDfCt=2;
		for(Player p: team2.getStarting()){
			if(p.getPosition().equals(Constants.forward)){
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 1 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)fwCt*2+1.0) / ((double)totalFwCt*2.0) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				if(fwCt==0){
					p.setIsMainPlayer(true);//set mainPlayer
					//p.setHasBall(true);
				}
				fwCt++;
			}else if (p.getPosition().equals(Constants.midfield))
			{
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 3 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)mfCt*2+1) / ((double)totalMfCt*2) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				mfCt++;
			}else if (p.getPosition().equals(Constants.defender))
			{
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 5 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)dfCt*2+1) / ((double)totalDfCt*2) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				dfCt++;
			}else if (p.getPosition().equals(Constants.goalkeeper))
			{
				double baseX = gpc.getRightBound()-30;
				double baseY = gpc.getUpperBound() + gpc.getHeight()/2;
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
			} else
			{
				System.out.println("Position not set for formation choosing");
				return;
			}
		}
		
		
		
		
	}

	public void passInGamePanelCoords(GamePanelCoords mGamePanelCoords) {
		gpc=mGamePanelCoords;
		
		//initialize the ball.
		int newX=(int) mGamePanelCoords.getHalfwayX()-8;
		int newY=(int)mGamePanelCoords.getHalfwayY()-8;
		ball.setCenterLocation(newX, newY);
	}

	
	//THIS FUNCTION IS ONLY FOR TESTING!!! WILL DELETE IT
	public void setTeamFormation() {
		int numofForward=0;
		int numofMid=0;
		int numofDe=0;
		int numofGo=0;
		
		//initialize starting line up.
		Vector<Player> starts = new Vector<Player>();
		for(Player p : team1.getSubs()){
			if(starts.size()==5) break;
			if(p.getPosition().equals(Constants.forward)&&numofForward<1){
				starts.add(p);
				numofForward++;
			}
			else if(p.getPosition().equals(Constants.midfield)&&numofMid<1){
				starts.add(p);
				numofMid++;
			}
			else if(p.getPosition().equals(Constants.defender)&&numofDe<2){
				starts.add(p);
				numofDe++;
			}
			else if(p.getPosition().equals(Constants.goalkeeper)&&numofGo<1){
				starts.add(p);
				numofGo++;
			}
		}
		team1.setStarting(starts);
		
		//set position
		int fwCt=0;
		int mfCt=0;
		int dfCt=0;
		int totalFwCt=1;
		int totalMfCt=1;
		int totalDfCt=2;
		for(Player p: team1.getStarting()){
			if (p.getPosition().equals(Constants.forward))
			{
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 5 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)fwCt*2+1) / ((double)totalFwCt*2) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				if(fwCt==0){
					p.setIsMainPlayer(true);//set mainPlayer
					p.setHasBall(true);
				}
				fwCt++;
			} else if (p.getPosition().equals(Constants.midfield))
			{
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 3 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)mfCt*2+1) / ((double)totalMfCt*2) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				mfCt++;
			}else if (p.getPosition().equals(Constants.defender))
			{
				double baseX = gpc.getLeftBound() + (gpc.getWidth() * 1 / 6);
				double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)dfCt*2+1) / ((double)totalDfCt*2) );
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
				dfCt++;
			}else if (p.getPosition().equals(Constants.goalkeeper))
			{
				double baseX = gpc.getLeftBound();
				double baseY = gpc.getUpperBound() + gpc.getHeight()/2;
				p.setBaseLocation(new Pair((int)baseX, (int)baseY));
				p.setLocation(new Pair((int)baseX, (int)baseY));
			} else
			{
				System.out.println("Position not set for formation choosing");
				return;
			}
		}
		team1.setStarting(starts);
		
	}

	
	//set up conditions for passing such as target and speed.
	public void setUpPass() {
		pastPlayer=null;
		Player p=mainTeam.getMainPlayer();
		passOrientation=-1;
		
		//if the player does not have ball,return
		if(!p.hasBall()){
			return;
		}
			
		if(passStrength<gpc.getPassLimit()){//add a constrain
			passStrength +=50*(double)mainPlayer.getPassing()/100;//passStrength +=gpc.getPassIncrement();
			System.out.println("passStrength "+passStrength);
		}
		
	}

	//execute passing.
	public void Pass(int orientation) {
		//long time_elapsed = pass_time_after - pass_time_before;
		//passStrength = (int) (80*time_elapsed/1000);
		if(passStrength > gpc.getPassLimit()) passStrength = (int) gpc.getPassLimit();
		//Player p=team1.getMainPlayer();
		Player p=mainTeam.getMainPlayer();
		p.setHasBall(false);
		ball.setPassDestination(orientation,passStrength);
		passOrientation=orientation;
		updateMainPlayer();
	}
	
	private void updateMainPlayer() {
		
		//get main player's coords
		mainPlayer=mainTeam.getMainPlayer();
		int mainX=mainPlayer.getX();
		int mainY=mainPlayer.getY();
		Player updated=null;
		int min=Integer.MAX_VALUE;
		
		
		//find player within the shortest distance and right orientation
		//for(Player p: team1.getStarting()){
		for(Player p: mainTeam.getStarting()){
			//if not main player and not goal keeper
			if(p!=mainPlayer&&(!p.getPosition().equals(Constants.goalkeeper))){
				int x=p.getX();
				int y=p.getY();
				int comOrientation=-1;
				if(x-mainX>0){
					if(y-mainY>0){
						comOrientation=3;
					}
					else if (y==mainY){
						comOrientation=2;
					}
					else {
						comOrientation=1;
					}
				}
				else if (x-mainX==0){
					if(y-mainY>0){
						comOrientation=4;
					}
					else{
						comOrientation=0;
					}
				}
				else if (x-mainX<0){
					if(y-mainY>0){
						comOrientation=5;
					}
					else if (y==mainY){
						comOrientation=6;
					}
					else {
						comOrientation=7;
					}
				}
				int futureBallX=ball.getDestination().getX();
				int futureBallY=ball.getDestination().getY();
				
				//get distance from the player and the ball.
				double dis= Math.sqrt((x-futureBallX)*(x-futureBallX)+(y-futureBallY)*(y-futureBallY));
				
				//if player is at the right orientation.
				if(((Math.abs(comOrientation-passOrientation)<3)||(Math.abs(comOrientation-passOrientation)>5))){
					if(dis<min){
						min=(int)dis;
						updated=p;
					}
				}
			}
		}
		if(updated!=null){
			//set update player as the main player.
			
			
			
			//Pair passDestination = new Pair(ball.getDestination().getX(), ball.getDestination().getY());
			
			
			//Pair playerDestination = ai.getDestination(updated);
			//updated.setDestination(passDestination);

			
			
			
			
			
			updated.setIsMainPlayer(true);
			mainPlayer.setIsMainPlayer(false);
			pastPlayer=mainPlayer;
			mainPlayer=mainTeam.getMainPlayer();
		}
		else{
			pastPlayer=mainPlayer;
		}
	}

	public boolean hasReceived(){
		if(mainPlayer != null){
			if(mainPlayer.hasBall()) 
				return true;
		}
		return false;
	}
	
	
	public void receive(int speed){
		
		if(mainPlayer == null) return;
		
		if(ball.getX() > gpc.getLeftBound() && ball.getX() < gpc.getRightBound() && ball.getY() > gpc.getUpperBound() && ball.getY() < gpc.getLowerBound()){
			int x_offset = Math.abs(ball.getX() - mainPlayer.getX());
			int y_offset = Math.abs(ball.getY() - mainPlayer.getY());
			double ratio = (double) x_offset/ (double) y_offset;

			
			if(mainPlayer.getX() < ball.getX()){

				Player hasBall=null;
				if(team1.isOnTeam(mainPlayer)){
					hasBall=team2.getMainPlayer();
				}
				else{
					hasBall=team1.getMainPlayer();
				}
				if(!PlayerCollide(hasBall, mainPlayer)){
					mainPlayer.setLocation((int) (mainPlayer.getX() + speed*ratio), mainPlayer.getY());
				}
			
			}
			/*else if(mainPlayer.getX() >= ball.getX() - 3 && mainPlayer.getX() < ball.getX()){
				mainPlayer.setLocation(ball.getX(), mainPlayer.getY());
			}
			else if(mainPlayer.getX() > ball.getX() && mainPlayer.getX() <= ball.getX() + 3){
				mainPlayer.setLocation(ball.getX(), mainPlayer.getY());
			}*/
			else if(mainPlayer.getX() > ball.getX()){
				Player hasBall=null;
				if(team1.isOnTeam(mainPlayer)){
					hasBall=team2.getMainPlayer();
				}
				else{
					hasBall=team1.getMainPlayer();
				}
				if(!PlayerCollide(hasBall, mainPlayer)){
					mainPlayer.setLocation((int) (mainPlayer.getX() - speed*ratio), mainPlayer.getY());
				}
				
				
				

			}
			else{
				mainPlayer.setHasBall(true);
			}
			
			if(mainPlayer.getY() < ball.getY()){
				Player hasBall=null;
				if(team1.isOnTeam(mainPlayer)){
					hasBall=team2.getMainPlayer();
				}
				else{
					hasBall=team1.getMainPlayer();
				}
				if(!PlayerCollide(hasBall, mainPlayer)){
					mainPlayer.setLocation(mainPlayer.getX(), (int) (mainPlayer.getY() + speed/ratio));
				}			
			}
			/*else if(mainPlayer.getY() >= ball.getY() - 3 && mainPlayer.getY() < ball.getY()){
				mainPlayer.setLocation(mainPlayer.getX(), ball.getY());
			}
			else if(mainPlayer.getY() > ball.getY() && mainPlayer.getY() <= ball.getY() + 3){
				mainPlayer.setLocation(mainPlayer.getX(), ball.getY());
			}*/
			else if(mainPlayer.getY() > ball.getY()){
				Player hasBall=null;
				if(team1.isOnTeam(mainPlayer)){
					hasBall=team2.getMainPlayer();
				}
				else{
					hasBall=team1.getMainPlayer();
				}
				if(!PlayerCollide(hasBall, mainPlayer)){
					mainPlayer.setLocation(mainPlayer.getX(), (int) (mainPlayer.getY() - speed/ratio));
				}	
			}
			else{
				mainPlayer.setHasBall(true);
			}
			
			try {
				Thread.sleep(66);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
	
	public void opponentDefensiveMove(){
		
		System.out.println("Other team has " + getMainTeam().getStarting().size() + " players");
		for(Player p : getMainTeam().getStarting()){
			System.out.println(p.getPosition() + ": " + p.getX() + " " + p.getY());
		}
		
		
		for(Player p: getOtherTeam().getStarting()){
			if(p.isMainPlayer())
				continue;
			
			if(p.getPosition().equals(Constants.goalkeeper)){
				
				
				System.out.println("gk is at" + p.getX() + " " + p.getY());
				
				if(ai.getDestination(p) != null)
					if(p.getY() != ai.getDestination(p).getY()){
						
						p.setLocation(ai.getDestination(p));
						
						//if(p.getY() < ai.getDestination(p).getY()) p.setLocation(p.getX(), p.getY() + 3);
						//else if(p.getY() > ai.getDestination(p).getY()) p.setLocation(p.getX(), p.getY() - 3);

					}
			}
			else if(p.getPosition().equals(Constants.midfield)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);
					
					/*int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 1){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 1){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 1){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 1){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
			
			else if(p.getPosition().equals(Constants.forward)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);
					
					
					/*int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 1){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 1){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 1){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 1){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
			
			else if(p.getPosition().equals(Constants.defender)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);
					
					
					/*int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 1){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 1){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 1){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 1){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
		}
		
		//teamOffensiveMove();
	}
	
	
	public void teamOffensiveMove(){
		
		System.out.println("Main team has " + getMainTeam().getStarting().size() + " players");
		for(Player p : getMainTeam().getStarting()){
			System.out.println(p.getPosition() + ": " + p.getX() + " " + p.getY());
		}
		 
		
		for(Player p: getMainTeam().getStarting()){
			
			if(p.isMainPlayer())
				continue;
			
			if(p.getPosition().equals(Constants.goalkeeper)){
				
				System.out.println("gk is at" + p.getX() + " " + p.getY());
				
				if(ai.getDestination(p) != null)
					if(p.getY() != ai.getDestination(p).getY()){
						Pair destination = ai.getDestination(p);						
						p.setLocation(destination);
						
						//if(p.getY() < destination.getY()) p.setLocation(p.getBaseX(), p.getY() + (destination.getY() - p.getY())/2);
						//else p.setLocation(p.getBaseX(), p.getY() - (p.getY() - destination.getY())/2);
						
						//if(p.getY() < ai.getDestination(p).getY()) p.setLocation(p.getX(), p.getY() + 10);
						//else p.setLocation(p.getX(), p.getY() - 10);

					}
			}
			else if(p.getPosition().equals(Constants.midfield)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					System.out.println("midfield destination is " + destination.getX() + " " + destination.getY());
					//p.setLocation(destination);
					
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);
					
					/*int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 0.5){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 0.5){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 0.5){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 0.5){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
			
			
			else if(p.getPosition().equals(Constants.forward)){
				//System.out.println("is a forward");
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					System.out.println("forward destination is " + destination.getX() + " " + destination.getY());

					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);					
					//p.setLocation(destination);
					
					/*int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 0.5){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 0.5){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 0.5){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 0.5){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
			
			else if(p.getPosition().equals(Constants.defender)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					
					System.out.println("defender destination is " + destination.getX() + " " + destination.getY());
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + 1), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - 1), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + 1);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - 1);
					/*p.setLocation(destination);
					
					
					int x_offset = Math.abs(p.getX() - destination.getX());
					int y_offset = Math.abs(p.getY() - destination.getY());
					double ratio = (double) x_offset/ (double) y_offset;
					
					if(p.getX() < destination.getX() - 1){
						p.setLocation((int) (p.getX() + 0.01*ratio), p.getY());
					}
					else if(p.getX() > destination.getX() + 1){
						p.setLocation((int) (p.getX() - 0.01*ratio), p.getY());
					}
					
					if(p.getY() < destination.getY() - 1){
						p.setLocation(p.getX(), (int) (p.getY() + 0.01/ratio));
					}
					else if(p.getY() > destination.getY() + 1){
						p.setLocation(p.getX(), (int) (p.getY() - 0.01/ratio));
					}*/
				
					//else do nothing
					
				}
			}
		}
		
	}
	
	public void team1Move(){
		int move = 2;
		
		for(Player p: team1.getStarting()){		
			if(p.isMainPlayer())
				continue;
			
			if(p.getPosition().equals(Constants.goalkeeper)){
				if(ai.getDestination(p) != null)
					if(p.getY() != ai.getDestination(p).getY()){
						Pair destination = ai.getDestination(p);						
						p.setLocation(destination);
					}
			}
			else if(p.getPosition().equals(Constants.midfield)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);
				}
			}
			
			
			else if(p.getPosition().equals(Constants.forward)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);						
				}
			}
			
			else if(p.getPosition().equals(Constants.defender)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);
				}
			}
		}
	}
	
	public void team2Move(){
		int move = 2;
		
		for(Player p: team2.getStarting()){		
			if(p.isMainPlayer())
				continue;
			
			if(p.getPosition().equals(Constants.goalkeeper)){
				if(ai.getDestination(p) != null)
					if(p.getY() != ai.getDestination(p).getY()){
						Pair destination = ai.getDestination(p);						
						p.setLocation(destination);
					}
			}
			else if(p.getPosition().equals(Constants.midfield)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);
				}
			}
			
			
			else if(p.getPosition().equals(Constants.forward)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);						
				}
			}
			
			else if(p.getPosition().equals(Constants.defender)){
				Pair destination = ai.getDestination(p);
				if(destination != null){
					if(p.getX() < destination.getX() - 0.5) p.setLocation((int) (p.getX() + move), p.getY());
					else if(p.getX() > destination.getX() + 0.5) p.setLocation((int) (p.getX() - move), p.getY());
					if(p.getY() < destination.getY() - 0.5) p.setLocation(p.getX(), p.getY() + move);
					else if(p.getY() > destination.getY() + 0.5) p.setLocation(p.getX(), p.getY() - move);
				}
			}
		}
	}
	
	
	
	public int getPassStrength(){
		return passStrength;
	}
	
	public int getPassOrientation(){
		return passOrientation;
	}

	public void setPassStrength(int passStrength2) {
		passStrength=passStrength2;
		if(passStrength==0){
			pastPlayer=null;
		}
	}
	
	public Player getPastPlayer(){
		return pastPlayer; 
	}

	public void setPassOrientation(int orientation) {
		passOrientation=orientation;
	}

	//Shoot functionality
	public void setUpShoot() {
		pastPlayer=null;
		Player p=mainTeam.getMainPlayer();
		//if the player does not have ball,return
		if(!p.hasBall()){
			return;
		}
		
		if(shootStrength<gpc.getShootMaxLimit()){
			shootStrength +=80*(double)mainPlayer.getShooting()/100;//shootStrength +=gpc.getShootIncrement();
			System.out.println("shooting strength: "+shootStrength);
		}
	}

	public void Shoot() {
		//long time_elapsed = shoot_time_after - shoot_time_before;
		//shootStrength = (int) (100*time_elapsed/1000);
		if(shootStrength > gpc.getShootMaxLimit()) shootStrength = (int) gpc.getShootMaxLimit();
		else if(shootStrength < gpc.getShootMinLimit()) shootStrength = (int) gpc.getShootMinLimit();
		
		//Player p=team1.getMainPlayer();
		Player p=mainTeam.getMainPlayer();
		if(team1.isOnTeam(p)){
			shootOrientationX=Constants.shoottoRightX;
		}
		else{
			shootOrientationX=Constants.shoottoLeftX;
		}
		p.setHasBall(false);
		shootOrientationY=Constants.shootY;
		double goalUpperY=gpc.getGoalUpperBound();
		double goalLowerY=gpc.getGoalLowerBound();
		double goalX=gpc.getRightGoalRightBound();
		ball.setShootDestination(p,shootStrength,goalUpperY,goalLowerY,goalX,shootOrientationX);
		
	}
	
	public int getShootStrength(){
		return shootStrength;
	}
	
	public int getShootOrientationX(){
		return shootOrientationX;
	}
	public int getShootOrientationY(){
		return shootOrientationY;
	}
	
	public void setShootStrength(int shootStrength2) {
		shootStrength=shootStrength2;
	}
	
	public void setShootOrientationX(int orientation){
		shootOrientationX=orientation;
	}
	
	public void setShootOrientationY(int orientation){
		shootOrientationY=orientation;
	}

	public void SwitchPlayer() {
		//if(team1.hasBall()){
		//	return;
		//}
		if(mainTeam.hasBall()){
			return;
		}
		//Player p=team1.getMainPlayer();
		Player p=mainTeam.getMainPlayer();
		Player updated=null;
		int min=Integer.MAX_VALUE;
		//for(Player tempp:team1.getStarting()){
		for(Player tempp:mainTeam.getStarting()){
			if(tempp!=p){
				int bx=ball.getX();
				int by=ball.getY();
				int px=tempp.getX();
				int py=tempp.getY();
				
				int dis=(int) Math.sqrt((bx-px)*(bx-px)+(by-py)*(by-py));
				if(dis<min){
					min=dis;
					updated=tempp;
				}
			}
		}
		if(updated!=null){
			pastPlayer=null;
			p.setIsMainPlayer(false);
			updated.setIsMainPlayer(true);
			mainPlayer=updated;
		}
	}
	
	public boolean isGoal(){
		int bx=ball.getX();
		int by=ball.getY();
		
		int goalUpperBound=(int) gpc.getGoalUpperBound();
		int goalLowerBound=(int) gpc.getGoalLowerBound();
		int leftBound=(int) gpc.getLeftBound();
		int rightBound=(int) gpc.getRightBound();
		
		if((bx+20>=rightBound)||(bx<=leftBound+20)){
			if(by<=goalLowerBound&&by>=goalUpperBound){
				if(bx+20>=rightBound){
					team1score++;
				}
				else if(bx<=leftBound){
					team2score++;
				}
			
				System.out.println("goal");
				System.out.println(bx);
				//ball.reset();
				return true;
			}
		}
		return false;
	}
	
	public boolean isGoal(int x, int y){
		int bx=x;
		int by=y;
		
		int goalUpperBound=(int) gpc.getGoalUpperBound();
		int goalLowerBound=(int) gpc.getGoalLowerBound();
		int leftBound=(int) gpc.getLeftBound();
		int rightBound=(int) gpc.getRightBound();
		
		if((bx+20>=rightBound)||(bx<=leftBound)){
			if(by<=goalLowerBound&&by>=goalUpperBound){
				return true;
			}
		}
		return false;
	}

	public void Tackle() {
		//get player who has the ball.
		mainPlayer=mainTeam.getMainPlayer();
		Player hasBall=null;
		if(team1.isOnTeam(mainPlayer)){
			hasBall=team2.getMainPlayer();
		}
		else{
			hasBall=team1.getMainPlayer();
		}
		
		//some base cases to ignore.
		if(!hasBall.hasBall()){
			return;
		}
		if(mainPlayer.hasBall()){
			return;
		}
		//the two players dont collide,return.
		if(!PlayerCollide(hasBall,mainPlayer)){
			return;
		}
		else{
			//get all the coords.
			int ballX=ball.getX();
			int ballY=ball.getY();
			
			int hasBallX=hasBall.getX();
			int hasBallY=hasBall.getY();
			
			int mainX=mainPlayer.getX();
			int mainY=mainPlayer.getY();
			
			double chance=0.3;
			
			//if the ball is in between the two players, set the chance even.
			if((ballX>=hasBallX)&&(ballX<=mainX)){
				if(ballY>=hasBallY&&ballY<=mainY){
					chance=0.5;
				}
				if((ballY<=hasBallY)&&(ballY>=mainY)){
					chance=0.5;
				}
			}
			else if(ballX<=hasBallX&&ballX>=mainX){
				if(ballY>=hasBallY&&ballY<=mainY){
					chance=0.5;
				}
				if(ballY<=hasBallY&&ballY>=mainY){
					chance=0.5;
				}
			}
			Random ran=new Random();
			int pob=Math.abs(ran.nextInt()%10);
			if(chance==0.3){
				if(pob>2){
					System.out.println("failed");
					tackleFailed=30;//the mainplayer can not move.
				}
				else{
					//mainPlayer has the ball;
					hasBall.setHasBall(false);
					mainPlayer.setHasBall(true);
					//set a boolean so the player dont shoot immediately he gets the ball.
					isTackle=true;
					//and the hasBall player should not move.
					//baiyu do something let the other user's gameClient called set tackleFailed to 30.Like what I did above.
				}
			}
			if(chance==0.5){
				if(pob>4){
					//the main player cant move
					System.out.println("failed");
					tackleFailed=30;	
				}
				else{
					//mainPlayer has the ball
					hasBall.setHasBall(false);
					mainPlayer.setHasBall(true);
					isTackle=true;
					//and the hasBall player should not move.
					//baiyu do something let the other user's gameClient called set tackleFailed to 30.Like what I did above.
				}
			}
		}
	}

	private boolean PlayerCollide(Player hasBall, Player Player) {
		int hasBallX=hasBall.getX();
		int hasBallY=hasBall.getY();
		
		int mainPlayerX=Player.getX();
		int mainPlayerY=Player.getY();
		
		int hasBallBoundX=hasBallX+35;
		int hasBallBoundY=hasBallY+35;
		int mainPlayerBoundX=mainPlayerX+35;
		int mainPlayerBoundY=mainPlayerY+35;
		
		if(( mainPlayerBoundX>hasBallX&&mainPlayerX<hasBallBoundX)&&(mainPlayerY<hasBallBoundY&&mainPlayerBoundY>hasBallY)){
			System.out.println("players collide");
			return true;
		}
		return false;
	}
	
	public boolean getIsTackle(){
		return isTackle;
	}
	
	public void setIsTackle(boolean in){
		isTackle=in;
	}
	
	public int getTackleFailed(){
		return tackleFailed;
	}
	
	public void setTackelFailed(int t){
		tackleFailed=t;
	}

	public boolean collide(int mainPlayerX, int mainPlayerY) {
		// check to see if the main player collide with any other player.
		mainPlayer=mainTeam.getMainPlayer();
		int mainPlayerBoundX=mainPlayerX+30;
		int mainPlayerBoundY=mainPlayerY+30;
		
		Vector<Player> temp=new Vector<Player>();
		for(Player p: team1.getStarting()){
			temp.add(p);
		}
		for(Player p:team2.getStarting()){
			temp.add(p);
		}
		for(Player p:temp){
			if(p!=mainPlayer){
				int pX=p.getX();
				int pY=p.getY();
				int pBoundX=pX+30;
				int pBoundY=pY+30;
				if(( mainPlayerBoundX>pX&&mainPlayerX<pBoundX)&&(mainPlayerY<pBoundY&&mainPlayerBoundY>pY)){
					System.out.println("two players collide");
					return true;
				}
			}
		}
		return false;
	}
	
	public int getTeam1Score(){
		return team1score;
	}
	public int getTeam2Score(){
		return team2score;
	}
	public Team winTeam(){
		if(team1score>team2score){
			return team1;
		}
		else if(team1score<team2score){
			return team2;
		}
		else{
			return null;
		}
	}

	public double getFriction() {
		// TODO Auto-generated method stub
		return friction;
	}
	
	public boolean isAI(Player p){
		if(p==team1.getMainPlayer()||p==team2.getMainPlayer()){
			return false;
		}
		else{
			return true;
		}
	}
	
	public Queue<Pair> findShortestPath(Player p){
		Queue<Pair> result=new LinkedList<Pair>();
		Pair destination=new Pair();
		//base case
		if(!isAI(p)){
			return null;
		}
		
		//get player he is marking.
		Player opp=p.getMarking();
		if(opp==null){
			System.out.println("null marking: "+p.getName());
			
			return null;
		}
		System.out.print("marking: "+opp.getName());
		if(isAI(opp)){
			destination=opp.getDestination();
		}
		else{
			return null;
		}
		
		int px=p.getX();
		int py=p.getY();
		int desX=destination.getX();
		int desY=destination.getY();
		
		//get the line.
		//y=slope*x+d
		double slope=(desY-py)/(desX-px);
		
		//get increment
		int xIncrement=(int)(0.01*0.01/(slope*slope+1));
		int yIncrement=(int) ((0.01*0.01*slope*slope)/(slope*slope+1));
		
		int x1=0;
		int y1=0;
		
		//set possible location.
		if(desX>=px){
			x1=px+xIncrement;
		}
		else{
			x1=px-xIncrement;
		}
		
		if(desY>=py){
			y1=py+yIncrement;
		}
		else{
			y1=py-yIncrement;
		}
		Pair p1=new Pair(x1,y1);
		
		//other possible location if the best location collides.
		int x2=px;
		int y2=py+yIncrement;
		Pair p2=new Pair(x2,y2);
		
		int x3=px+xIncrement;
		int y3=py;
		Pair p3=new Pair(x3,y3);
		
		int x4=px-xIncrement;
		int y4=py;
		Pair p4=new Pair(x4,y4);
		
		int x5=px;
		int y5=py-yIncrement;
		Pair p5=new Pair(x5,y5);
		
		Pair p6=new Pair(px,py);
		
		result.add(p1);
		result.add(p2);
		result.add(p3);
		result.add(p4);
		result.add(p5);
		result.add(p6);
		
		return result;
		
	}

	public boolean AIPlayerCollide(Player p, Pair pair) {
		// TODO Auto-generated method stub
		Vector<Player> vector=new Vector<Player>();
		for(Player p1:team1.getStarting()){
			vector.add(p1);
		}
		for(Player p2:team2.getStarting()){
			vector.add(p2);
		}
		
		int x=pair.getX();
		int y=pair.getY();
		int xBound=x+30;
		int yBound=y+30;
		
		for(Player p3:vector){
			if(p3!=p){
				int px=p3.getX();
				int py=p3.getY();
				int pxBound=px+30;
				int pyBound=py+30;
				if((pxBound>x&&xBound>px)&&(y<pyBound&&yBound>py)){
					return true;
				}
			}
		}
		
		return false;
	}
}
