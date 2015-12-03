package game;

import java.awt.Image;
import java.io.Serializable;
import java.util.Random;

import library.ImageLibrary;
import util.Constants;
import util.Pair;

public class Ball implements Serializable {
	private static final long serialVersionUID = 1;
	private Pair location;
	private Pair centerCircleLocation;
	
	private Pair destination;
	
	
	//increment for shooting
	private Pair shootingIncrement;
	private double slope=0;
	
	public Ball()
	{
		//calculate center circle
		centerCircleLocation = new Pair();
		location = new Pair();
		destination= new Pair();
		shootingIncrement= new Pair();
	}
	//override default according to panel size
	public void setCenterLocation(int x, int y){
		this.centerCircleLocation.set(x, y);
		reset();
	}

	
	public void setCenterLocation(Pair p){
		this.centerCircleLocation.set(p);
		reset();
	}
	
	//GET methods
	public int getX()
	{
		return this.location.getX();
	}
	
	public int getY()
	{
		return this.location.getY();
	}
	
	public Pair getDestination()
	{
		Pair copy = new Pair(this.destination);
		return copy;
	}
	
	//SET methods
	public void setLocation(int x, int y)
	{
		this.location.set(x,y);
	}
	
	public void setLocation(Pair p)
	{
		this.location.set(p);
	}
	
	//other methods
	public void reset()
	{
		this.location.set(this.centerCircleLocation);
	}
	public void setPassDestination(int orientation, int passStrength) {
		// TODO Auto-generated method stub
		int x=this.getX();
		int y=this.getY();
		
		if(orientation==0){
			y=y-passStrength/10*15;
		}
		else if(orientation==1){
			x=x+passStrength/10*10;
			y=y-passStrength/10*10;
		}
		else if(orientation==2){
			x=x+passStrength/10*15;
		}
		else if(orientation==3){
			x=x+passStrength/10*10;
			y=y+passStrength/10*10;
		}
		else if(orientation==4){
			y=y+passStrength/10*15;
		}
		else if(orientation==5){
			x=x-passStrength/10*10;
			y=y+passStrength/10*10;
		}
		else if(orientation==6){
			x=x-passStrength/10*10;
		}
		else if(orientation==7){
			x=x-passStrength/10*10;
			y=y-passStrength/10*10;
		}
		destination.set(x,y);
		
	}
	public void setShootDestination(Player p, int shootStrength, double goalUpperY, double goalLowerY, double goalX, int shootOrientationX) {
		double playerX=p.getX();
		double playerY=p.getY();
		
		//get ball's coords
		int x=this.getX();
		int y=this.getY();
		
		//determine if the player can shoot in target.
		boolean inGoal=false;
		Random ran=new Random();
		int poss=ran.nextInt()%11;
		int shooting=p.getShooting();
		System.out.println("shooting "+shooting);
		if(poss<shooting/10){
			inGoal=true;
		}
		
		double midGoalY=0.5*(goalUpperY+goalLowerY);
		double bestGoalY=Math.abs(goalUpperY-midGoalY);
		//get goal's coords based on the player's stats.
		double goalY=0;
		
		if(inGoal){
			//if inGoal set the goal to a perfect location if the player's shooting is high.
			//System.out.println("inGoal");
			double var=bestGoalY*((double)shooting/100)*((double)(ran.nextInt()%10)/(double)10);
			System.out.println("var: "+var);
			goalY=midGoalY+var;
		}
		else{
			//else let the goal go anywhere at random...
			System.out.println("notinGoal");
			double var=(goalLowerY-goalUpperY)*((double)shooting/100)*((double)(ran.nextInt()%10)/(double)10);
			System.out.println("var: "+var);
			goalY=midGoalY+var;
		}
		System.out.println("goalY: "+goalY);
		
		//get the function of the line between the ball and the goal y=slope*x+d
		double slope=0;
		double d=0;
		if(goalX-x!=0){
			slope=(goalY-y)/(goalX-x);
		}
		d=y-slope*x;
		
		//get the movement of the ball
		int travel=shootStrength/10*30;//in the future we want to change 15 to player's stats;
		
		//if shoot to right
		if(shootOrientationX==Constants.shoottoRightX){
			x+=Math.sqrt(travel*travel/(slope*slope+1));
		}
		//if shoot to left
		else{
			x-=Math.sqrt(travel*travel/(slope*slope+1));
		}
		
		//if shot up
		if(slope>=0){
			y+=Math.sqrt((travel*travel*slope*slope)/(slope*slope+1));
		}
		//if shot down
		else{
			y-=Math.sqrt((travel*travel*slope*slope)/(slope*slope+1));
		}
		
		System.out.println("ball: "+x+" "+y);
		destination.set(x, y);
		int xIncrement=(int) Math.sqrt(30*30/(slope*slope+1));
		int yIncrement=(int) Math.sqrt((30*30*slope*slope)/(slope*slope+1));
		shootingIncrement.set(xIncrement, yIncrement);
		System.out.println("increment: "+xIncrement+" "+yIncrement);
		this.slope=slope;
		System.out.println("slope: "+slope);
	}
	
	public double getSlope(){
		return slope;
	}
	
	public void setSlope(double inSlope){
		slope=inSlope;
	}
	
	public Pair getIncrement(){
		return shootingIncrement;
	}
}
