package util;

import java.io.Serializable;

public class Pair implements Serializable {
	private static final long serialVersionUID = 1;
	int x;
	int y;
	//utility for AI searching
	double value;
	
	public Pair()
	{
		this.x = -1;
		this.y = -1;
	}
	
	public Pair(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Pair(double x, double y)
	{
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public Pair(Pair p)
	{
		this.x = p.getX();
		this.y = p.getY();
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public double getValue()
	{
		return this.value;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void set(Pair p)
	{
		this.x = p.x;
		this.y = p.y;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}
}
