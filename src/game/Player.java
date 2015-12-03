package game;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

import library.ImageLibrary;
import util.Constants;
import util.Pair;

public class Player implements Serializable{
	private static final long serialVersionUID = 1;
	//Team
	protected Team team;
	
	//Name
	protected String name;
	//Position
	protected String position;
	
	//Coordinates
	protected Pair location;
	//Base position??
	protected Pair baseLocation;
	//has ball
	private Boolean hasBall = false;
	//redundant now because the player w/ ball always is main player
	private Boolean isMainPlayer = false;
	
	private Boolean isReceivingBall = false;
	
	private Pair destination;

	private int defending;
	private int passing;
	private int dribbling;
	private int shooting;
	private int speed;
	private int strength;
	
	//AI utilities
	//for space finding
	private Boolean isTrianglePoint = false;
	//for defending
	private Player marking = null;
	
	{
		location = new Pair();
		baseLocation = new Pair();
		destination = new Pair();
		
	}

	public Player(String name, Team team, String position, int defending, int passing, int dribbling, int shooting, int speed, int strength)
	{
		this.name = name;
		this.team  = team;
		//includes error checking
		setPosition(position);
		this.defending = defending;
		this.passing = passing;
		this.dribbling = dribbling;
		this.shooting = shooting;
		this.speed = speed;
		this.strength = strength;
	}
	
	
	//GET methods
	public int getX()
	{
		return location.getX();
	}
	
	public int getY()
	{
		return location.getY();
	}
	
	public Team getTeam()
	{
		return this.team;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getBaseX()
	{
		return baseLocation.getX();
	}
	
	public int getBaseY()
	{
		return baseLocation.getY();
	}
	
	public int getDefending()
	{
		return this.defending;
	}
	
	public int getPassing()
	{
		return this.passing;
	}
	
	public int getDribbling()
	{
		return this.dribbling;
	}
	
	public int getShooting()
	{
		return this.shooting;
	}
	
	public int getSpeed()
	{
		return this.speed;
	}
	
	public int getStrength()
	{
		return this.strength;
	}
	
	public Boolean isTrianglePoint()
	{
		return this.isTrianglePoint;
	}
	
	public Player getMarking()
	{
		return this.marking;
	}
	
	public Boolean hasBall()
	{
		return this.hasBall;
	}
	
	public Boolean isMainPlayer()
	{
		return this.isMainPlayer;
	}
	
	public Boolean isReceivingBall()
	{
		return this.isReceivingBall;
	}
	
	public String getPosition()
	{
		return this.position;
	}
	
	public Pair getDestination()
	{
		//we don't want to return the same instance
		//because we might change it
		return new Pair(destination.getX(), destination.getY());
	}
	
	//SET methods
	public void setLocation(int x, int y)
	{
		location.set(x, y);
	}
	
	public void setLocation(Pair p)
	{
		location.setX(p.getX());
		location.setY(p.getY());
	}
	
	public void setHasBall(Boolean hb)
	{
		this.hasBall = hb;
	}
	
	public void setBaseLocation(int baseX, int baseY)
	{
		baseLocation.set(baseX, baseY);
	}
	
	public void setBaseLocation(Pair p)
	{
		baseLocation.setX(p.getX());
		baseLocation.setY(p.getY());
	}
	
	private void setPosition(String position)
	{
		//error checking
		Boolean match = false;
		for (String pos : Constants.positions)
		{
			if (pos.equals(position))
			{
				match = true;
			}
		}
		if (match)
			this.position = position;
	}
	
	public void setIsMainPlayer(Boolean ip)
	{
		this.isMainPlayer = ip;
	}
	
	public void setDestination(Pair p)
	{
		this.destination.set(p);
	}
	
	public void setTrianglePoint(Boolean bool)
	{
		this.isTrianglePoint = bool;
	}
	
	public void setMarking(Player p)
	{
		this.marking = p;
	}
	
	//OTHER methods
	public void reset()
	{
		if (this.hasBall)
		{
			this.hasBall = false;
		}
		//not really base position
		//for example base location for striker would be in the opponent's half
		//but we have to be in our half to start the game
		setLocation(baseLocation);	
	}
	
}
