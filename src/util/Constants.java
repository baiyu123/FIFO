package util;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Constants {
	
	public static final String forward = "FW";
	public static final String midfield = "MF";
	public static final String defender = "DF";
	public static final String goalkeeper = "GK";
	public static final String[] positions = {forward,midfield,defender,goalkeeper};
	
	public static final int defensiveMentality = 2;
	public static final int normalMentality = 1;
	public static final int offensiveMentality = 0;
	public static final int[] mentalities = {defensiveMentality, normalMentality, offensiveMentality};
	
	public static final int moveUpward=1;
	public static final int moveDownward=-1;
	public static final int moveLeft=2;
	public static final int moveRight=-2;
	public static final int noChoice=0;
	
	public static final double sunnyFriction=0;
	public static final double rainnyFriction=0.2;
	public static final double snowyFriction=0.5;
	
	public static final String sunny="Sunny";
	public static final String rainy="Rainy";
	public static final String snowy="Snowy";
	
	public static final String normalGrass="Normal Grass";
	public static final String muddyGrass="Muddy Grass";
	public static final String street="Street";
	
	public static final int shoottoRightX=1;
	public static final int shoottoLeftX=-1;
	public static final int shootY=1;
	public static final int shootReverseY=-1;
	
	public static final int expTie=300;
	public static final int expLose=100;
	public static final int expWin=500;
	
	public static final int baseCoin=200;
	
	
	///whenever we call this we are connecting from Java to the computer and back??
	//so better off to just call this once to reduce stress on CPU?
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
}
