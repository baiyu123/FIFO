package game;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.ImageIcon;

import library.ImageLibrary;
import util.Constants;
import util.Pair;

public class Team implements Serializable{
	private static final long serialVersionUID = 1;
	private Vector<Player> starting;
	private Vector<Player> subs;
	private String name;
	private ImageIcon icon;
	
	private int mentality = Constants.normalMentality;
	
	public Team(String name)
	{
		this.name = name;
		readImage();
		starting = new Vector<Player>();
		subs = new Vector<Player>();
	}
	
	//GET methods
	public Vector<Player> getStarting()
	{
		return this.starting;
	}
	
	public Vector<Player> getSubs()
	{
		return this.subs;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public ImageIcon getIcon()
	{
		return this.icon;
	}
	
	public int getMentality()
	{
		return this.mentality;
	}
	
	
	//SET methods
	public void setStarting(Vector<Player> team)
	{
		//make sure all players exist in subs first
		for(Player p : team)
		{
			if (!subs.contains(p))
			{
				return;
			}
		}
		
		//now send all subs to starting
		for(Player p : team)
		{
			subs.remove(p);
			starting.add(p);
		}
	}
	
	//OTHER methods
	public void addPlayer(Player p)
	{
		//default add to subs
		subs.add(p);
		
	}
	
	private Boolean readImage()
	{
		//Team name : Manchester United
		//image file name : Manchester_United.png
		if (this.name.equals(""))
			return false;
		
		//change name to url
		String safeURLstring = this.name.replace(" ", "_");
		String url = "./resource/teams/" + safeURLstring + ".png";
		
		this.icon = new ImageIcon(ImageLibrary.getImage(url));
		
		if (this.icon != null)
			return true;
		else
			return false;
	}
	
	public Boolean hasBall()
	{
		for(Player p : this.starting)
		{
			if (p.hasBall())
				return true;
		}
		
		return false;
	}
	
	public Boolean isOnTeam(Player p)
	{
		if (starting.contains(p) || subs.contains(p))
			return true;
		else
			return false;
	}
	
	public void substitute(Player in, Player out)
	{
		//make sure these players exist first
		if (subs.contains(in) && starting.contains(out))
		{
			//set to same base position
			in.setBaseLocation(new Pair(out.getBaseX(), out.getBaseY()));
			//switch
			starting.add(in);
			subs.remove(in);
			subs.add(out);
			starting.remove(out);
		}
	}
	
	//for when goalkeeper gets ball
	public void resetToBasePosition()
	{
		for(Player p : this.starting)
		{
			p.reset();
		}
	}
	
	public void changeMentality(int num)
	{
		Boolean valid = false;
		for (int mentality : Constants.mentalities)
		{
			if (num == this.mentality)
				continue;
			if (num == mentality)
			{
				valid = true;
				break;
			}
		}
		
		if (!valid)
		{
			System.out.println("Position not valid : changeMentality() in Team.java");
			return;
		}
		//assumes we call this every time the player
		//hits the arrow button to change mentality
		//so this code will bug if goes from 0 to 2
		if (num > this.mentality)
		{
			//change base position of players
			
		}
		
		if (num < this.mentality)
		{
			//change base position of players
			
		}
		
		//update mentality
		this.mentality = num;
	}
	
	public Player getMainPlayer(){
		for(Player p: starting){
			if(p.isMainPlayer()){
				return p;
			}
		}
		return null;
	}
	public void SetMainPlayer(Player player){
		for(Player p:starting){
			if(p.isMainPlayer()){
				p = player;
			}
		}
	}
	public void setIconToNull(){
		icon = null;
	}
	
}
