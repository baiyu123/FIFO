package game;

import util.Constants;
import util.Pair;

public class GamePanelCoords {
	
	private static double upperBound;
	private static double lowerBound;
	private static double leftBound;
	private static double rightBound;
	private static double goalUpperBound;
	private static double goalLowerBound;
	private static double leftGoalLeftBound;
	private static double rightGoalRightBound;
	private static double leftGoalRightBound;
	private static double rightGoalLeftBound;
	private static double halfwayX;
	private static double halfwayY;
	private static double centreRadiusX;
	private static double centreRadiusY;
	private static double penaltyUpperBound;
	private static double penaltyLowerBound;
	private static double leftPenaltyRightBound;
	private static double rightPenaltyLeftBound;
	private static double height;
	private static double width;
	private static double passUnit;
	private static double shootUnit;
	private static double rightAIGoalPointX;
	private static double leftAIGoalPointX;
	
	//some stats that is relative depending on the game board.
	private static double passLimit;
	private static double shootMaxLimit;
	private static double shootMinLimit;
	private static double passIncrement;
	private static double shootIncrement;
	

	public GamePanelCoords(double startX, double startY) {
		upperBound=startY;
		leftBound=startX;
		rightBound=Constants.screenSize.width;
		lowerBound=Constants.screenSize.height-70;
		height=lowerBound-startY;
		width=rightBound-startX;
		initializeComponents();
		
	}

	private void initializeComponents() {
		goalUpperBound=upperBound+height*0.35;
		goalLowerBound=upperBound+height*0.65;
		leftGoalRightBound=leftBound+width*0.05;
		leftGoalLeftBound = leftBound;
		rightGoalRightBound=rightBound;
		rightGoalLeftBound=leftBound+width*0.95;
		halfwayX=leftBound+width*0.5;
		halfwayY=upperBound+height*0.5;
		centreRadiusX=width*0.17;
		centreRadiusY=height*0.265;
		penaltyUpperBound=upperBound+height*0.2;
		penaltyLowerBound=upperBound+height*0.8;
		leftPenaltyRightBound=leftBound+width*0.16;
		rightPenaltyLeftBound=leftBound+width*0.84;
		
		
		passLimit=width/4*3;
		shootMaxLimit=width/2;
		shootMinLimit=width/12;
		passUnit=width/30;
		shootUnit=width/25;
		passIncrement=passUnit*5;
		shootIncrement=shootUnit*8;
		
		rightAIGoalPointX=rightBound+10;
		leftAIGoalPointX=leftBound-10;
		
	}
	
	
	//get functions

	public double getUpperBound(){
		return upperBound;
	}
	
	public double getLowerBound(){
		return lowerBound;
	}
	
	public double getLeftBound(){
		return leftBound;
	}
	
	public double getRightBound(){
		return rightBound;
	}
	
	public double getGoalUpperBound(){
		return goalUpperBound;
	}
	
	public double getGoalLowerBound(){
		return goalLowerBound;
	}
	
	public double getLeftGoalRightBound(){
		return leftGoalRightBound;
	}
	
	public double getRightGoalLeftBound(){
		return rightGoalLeftBound;
	}
	
	public double getHalfwayX(){
		return halfwayX;
	}
	
	public double getHalfwayY(){
		return halfwayY;
	}
	
	public double getCentreRadiusX(){
		return centreRadiusX;
	}
	
	public double getCentreRadiusY(){
		return centreRadiusY;
	}
	
	public double getPenaltyUpperBound(){
		return penaltyUpperBound;
	}
	
	public double getPenaltyLowerBound(){
		return penaltyLowerBound;
	}
	
	public double getLeftPenaltyRightBound(){
		return leftPenaltyRightBound;
	}
	
	public double getRightPenaltyLeftBound(){
		return rightPenaltyLeftBound;
	}
	
	public double getWidth(){
		return width;
	}
	
	public double getHeight(){
		return height;
	}
	
	public double getRightGoalRightBound(){
		return rightGoalRightBound;
	}
	public double getLeftGoalLeftBound(){
		return leftGoalLeftBound;
	}
	
	public double getPassLimit(){
		return passLimit;
	}
	
	public double getShootMinLimit(){
		return shootMinLimit;
	}
	
	public double getShootMaxLimit(){
		return shootMaxLimit;
	}
	
	public double getPassUnit(){
		return passUnit;
	}
	
	public double getShootUnit(){
		return shootUnit;
	}
	
	public double getPassIncrement(){
		return passIncrement;
	}
	
	public double getShootIncrement(){
		return shootIncrement;
	}
	
	public double getleftAIGoalPointY(){
		return leftAIGoalPointX;
	}
	public double getrightAIGoalPointY(){
		return rightAIGoalPointX;
	}
	
	public boolean isInField(int x, int y){
		if(x>leftBound&&x<rightBound&&y>upperBound&&y<lowerBound){
			return true;
		}
		else{  
			return false;
		}
	}  
	
	public boolean isInField(double x, double y){
		if(x>leftBound&&x<rightBound&&y>upperBound&&y<lowerBound){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isInField(Pair p)
	{
		int x = p.getX();
		int y = p.getY();
		if(x>leftBound&&x<rightBound&&y>upperBound&&y<lowerBound){
			return true;
		}
		else{
			return false;
		}
	}
	
}
