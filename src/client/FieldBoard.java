package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import game.Ball;
import game.GamePanelCoords;
import game.GameSimulation;
import game.Player;
import game.Team;
import library.ImageLibrary;
import util.Constants;
import util.Pair;



public class FieldBoard extends JPanel {
	
	private static final long serialVersionUID = 1;
		private final String iconURL = "./resource/ball.png";
		private Image ballImage;
		
		private int startx;
		private int starty;
		private int endx;
		private int endy;
		private int width;
		private int height;
		private Ball ball;
		
		private GameSimulation mGameSimulation;
		private GamePanelCoords mGamePanelCoords;
		
		//for testing purposes, added two parameters to the constructor, which are posx and posy
		//also created these two private variables: posx and posy
		
		private int posx;
		private int posy;
		
		private int orientation;
		
		public int getCurrentOrientation(){
			return orientation;
		}
		
		
		public FieldBoard( GameSimulation inGameSimulation, int startX2, int startY2, int orientation, GamePanelCoords mGamePanelCoords){
			mGameSimulation=inGameSimulation;
			this.mGamePanelCoords=mGamePanelCoords;
			startx=startX2;
			starty=startY2;
			width=Constants.screenSize.width-startx;
			height=Constants.screenSize.height-starty;
			endx=startx+width;
			endy=starty+height;
			
			ballImage = ImageLibrary.getImage(iconURL);
			ballImage = ballImage.getScaledInstance(15, 15, Image.SCALE_FAST);
			
			
			this.orientation = orientation;
			
			mGameSimulation=inGameSimulation;
			ball=mGameSimulation.getBall();
		}
		
		
		@Override
		public void paintComponent(Graphics g)
		{			
			super.paintComponent(g);
			
			int w = (int)mGamePanelCoords.getWidth();
			int h = (int)mGamePanelCoords.getHeight();
			g.setColor(mGameSimulation.getFieldColor());
			g.fillRect(0, 0, w, h);
			
			g.setColor(Color.WHITE);
			//border
			g.drawLine(0, 0, w, 0);
			g.drawLine(w, 0, w, h);
			g.drawLine(0, 0, 0, h);
			g.drawLine(0, h, w, h);
			//center
			g.drawLine(w/2, 0, w/2, h);
			int radius =(int) mGamePanelCoords.getCentreRadiusX();
			g.drawOval(w/2-radius, h/2-radius, radius*2, radius*2);
			
			//goal rectangles
			int goaly=(int)mGamePanelCoords.getGoalUpperBound();
			int goalx=(int)mGamePanelCoords.getRightGoalLeftBound();
			int goalheight=(int) mGamePanelCoords.getGoalLowerBound()-(int)mGamePanelCoords.getGoalUpperBound();
			g.drawRect(0,goaly,(int)mGamePanelCoords.getLeftGoalRightBound(),goalheight);
			g.drawRect(goalx,goaly,(int)mGamePanelCoords.getRightBound(),goalheight);		
			//g.drawImage(fieldBg,0,0,this);//draw the field.
			
			
			//int newX=(int) mGamePanelCoords.getHalfwayX()-8;
			//int newY=(int)mGamePanelCoords.getHalfwayY()-8;
			//ball.setCenterLocation(newX, newY); initialize in the gamesimulation.

			//g.drawImage(ballImage, ball.getX()-20, ball.getY()-20, this); // repaint the ball.
			
			
			//g.drawImage(playerImage, posx, posy, this);
			
			Team team1= mGameSimulation.getTeam1();
			Team team2=mGameSimulation.getTeam2();
			
			g.setColor(Color.RED);
			for(Player p: team1.getStarting()){
				//System.out.println(p.getName());
				//System.out.println(x+" "+y);
				if(p==team1.getMainPlayer()){
					g.setColor(Color.magenta);
				}
				else{
					g.setColor(Color.red);
					if(!team1.hasBall()){
						//if ai should defense
						Queue<Pair> result=mGameSimulation.findShortestPath(p);
						if(result!=null){
							//check if there is a location that is no collision.
							for(Pair pair: result){
								if(!mGameSimulation.AIPlayerCollide(p,pair)){
									//set ai's location.
									p.setLocation(pair);
									break;
								}
							}
						}
					}
				}
				int x=p.getX();
				int y=p.getY();
				if(!mGamePanelCoords.isInField(x, y)){
					p.reset();
				}
				g.fillOval(x, y, 30, 30);
				g.drawString(p.getPosition(), x+7, y-8);
			}
			
			
			/* orientation
			 *      0
			 * 	 7	   1
			 *6  		 2
			 * 	 5	   3
			 * 	    4	
			 */

			
			//see if any player has the ball or the ball is being passed or shooted and if it is in the goal.
			boolean isPassed=false;
			//boolean hasPassed = false;
			//boolean received = false;
			boolean isShoot=false;
			boolean hasBall=false;
			boolean isGoal=mGameSimulation.isGoal();
			int passStrength=mGameSimulation.getPassStrength();
			int shootStrength=mGameSimulation.getShootStrength();
			System.out.println("passStrength: "+passStrength);
			System.out.println("shootStrength: "+shootStrength);
			Player p=null;
			Ball b=mGameSimulation.getBall();
			
			//if any team has the ball
			if(team1.hasBall()){
				p=team1.getMainPlayer();
			}
			else if(team2.hasBall()){
				p=team2.getMainPlayer();
			}
			
			if(p==null){
				Player collide=collide(b);
				//if a player get the ball.
				if(collide!=null){
					//if collide is in team1
					if(mGameSimulation.getTeam1().isOnTeam(collide)){
						mGameSimulation.getTeam1().getMainPlayer().setIsMainPlayer(false);
					}
					//if collide is in team2
					else if(mGameSimulation.getTeam2().isOnTeam(collide)) {
						mGameSimulation.getTeam2().getMainPlayer().setIsMainPlayer(false);
					}
					//set collide as the mainPlayer.
					collide.setIsMainPlayer(true);
					//refresh pass and shoot
					mGameSimulation.setPassStrength(0);
					mGameSimulation.setShootStrength(0);
					p=collide;
					p.setHasBall(true);
				}
			}
			
			//set ball to be painted next to the player
			int x=0;
			int y=0;
			if(p!=null){
				x=p.getX();
				y=p.getY();
				hasBall=true;
			}
			else{
				//if the ball is being passed.
				x=b.getX();
				y=b.getY();
				if(mGameSimulation.getPassStrength()>0){
					isPassed=true;
					//System.out.println(x+" "+y);
					orientation=mGameSimulation.getPassOrientation();
					//System.out.println("or: "+orientation);
				}
				//if the ball is being shot.
				if(mGameSimulation.getShootStrength()>0){
					isShoot=true;
					System.out.println("shoot");
				}
			}
			
			//if the player has the ball.
			if(hasBall&&mGameSimulation.getMainTeam().hasBall()){
						if(orientation == 0){
							if(x+14+15<mGamePanelCoords.getRightBound()){
								x = x + 14;
							}
							if(y-12>mGamePanelCoords.getUpperBound()){
								y = y -12;
							}
						}
						else if(orientation == 1){
							if(x+32+15<mGamePanelCoords.getRightBound()){
								x = x + 32;
							}
							if(y-1>mGamePanelCoords.getUpperBound()){
								y = y - 1;
							}
						}
						else if(orientation == 2){
							if(x+37+15<mGamePanelCoords.getRightBound()){
								x = x + 37;
							}
							if(y+15+15<mGamePanelCoords.getLowerBound()){
								y = y + 15;
							}
						}
						else if(orientation == 3){
							if(x+30+15<mGamePanelCoords.getRightBound()){
								x = x + 30;
							}
							if(y+30+15<mGamePanelCoords.getLowerBound()){
								y = y + 30;
							}
						}
						else if(orientation == 4){
							if(x+14+15<mGamePanelCoords.getRightBound()){
								x = x + 14;
							}
							if(y+37+15<mGamePanelCoords.getLowerBound()){
								y = y + 37;
							}
						}
						else if(orientation == 5){
							if(x-2>mGamePanelCoords.getLeftBound()){
								x = x - 2;
							}
							if(y+32+15<mGamePanelCoords.getLowerBound()){
								y = y + 32;
							}
						}
						else if(orientation == 6){
							if(x-12>mGamePanelCoords.getLeftBound()){
								x = x - 12;
							}
							if(y+15+15<mGamePanelCoords.getLowerBound()){
								y = y + 15;
							}
						}
						else if(orientation == 7){
							if(x-2>mGamePanelCoords.getLeftBound()){
								x = x - 2;
							}
							if(y-3>mGamePanelCoords.getUpperBound()){
								y = y - 3;
							}
						}
			}
			//if the ball is being passed. in the future change 15 and 10 to player's stats.
			else if(isPassed&&passStrength>0&&!isGoal){
				double friction=mGameSimulation.getFriction();
				if(orientation == 0){
					if(y-15*(1-friction)>mGamePanelCoords.getUpperBound()){//if(y-mGamePanelCoords.getPassUnit()>mGamePanelCoords.getUpperBound()
						y = (int) (y -15*(1-friction)); //y -=mGamePanelCoords.getPassIncrement();
					}
					else{
						//reverse sign
						if(isPassed&&passStrength>0){
							orientation=4;
							mGameSimulation.setPassOrientation(orientation);
						}
					}
				}
				else if(orientation == 1){
					
					if(mGameSimulation.isGoal( (int) (x+10*(1-friction)+15), (int) (y-10*(1-friction)))){
						x=(int) mGamePanelCoords.getRightBound();
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x+10*(1-friction)+15<mGamePanelCoords.getRightBound()){
							x = (int) (x + 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=7;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
						if(y-10*(1-friction)>mGamePanelCoords.getUpperBound()){
							y = (int) (y - 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=3;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				else if(orientation == 2){
					if(mGameSimulation.isGoal( (int) (x+15*(1-friction)+15), (int) y)){
						x=(int) mGamePanelCoords.getRightBound();
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x+15*(1-friction)+15<mGamePanelCoords.getRightBound()){
							x = (int) (x + 15*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=6;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				else if(orientation == 3){
					if(mGameSimulation.isGoal( (int) (x+10*(1-friction)+15), (int) (y+10*(1-friction)+15))){
						x=(int) mGamePanelCoords.getRightBound();
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x+10*(1-friction)+15<mGamePanelCoords.getRightBound()){
							x = (int) (x + 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=5;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
						if(y+10*(1-friction)+15<mGamePanelCoords.getLowerBound()){
							y = (int) (y + 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=1;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				else if(orientation == 4){			
					if(y+15*(1-friction)+15<mGamePanelCoords.getLowerBound()){
						y = (int) (y + 15*(1-friction));
					}
					else{
						if(isPassed&&passStrength>0){
							orientation=0;
							mGameSimulation.setPassOrientation(orientation);
						}
					}
				}
				else if(orientation == 5){
					if(mGameSimulation.isGoal((int) (x-10*(1-friction)), (int) (y+10*(1-friction)+15))){
						x=(int) mGamePanelCoords.getLeftBound()-5;
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x-10*(1-friction)>mGamePanelCoords.getLeftBound()){
							x = (int) (x - 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=3;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
						if(y+10*(1-friction)+15<mGamePanelCoords.getLowerBound()){
							y = (int) (y + 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=7;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				else if(orientation == 6){
					if(mGameSimulation.isGoal((int) (x-15*(1-friction)), (int) y)){
						x=(int) mGamePanelCoords.getLeftBound()-5;
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x-15*(1-friction)>mGamePanelCoords.getLeftBound()){
							x = (int) (x - 15*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=2;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				else if(orientation == 7){
					if(mGameSimulation.isGoal((int) (x-10*(1-friction)), (int) (y-10*(1-friction)))){
						x=(int) mGamePanelCoords.getLeftBound()-5;
						mGameSimulation.setPassStrength(0);
					}
					else{
						if(x-10*(1-friction)>mGamePanelCoords.getLeftBound()){
							x = (int) (x - 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=1;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
						if(y-10*(1-friction)>mGamePanelCoords.getUpperBound()){
							y = (int) (y - 10*(1-friction));
						}
						else{
							if(isPassed&&passStrength>0){
								orientation=5;
								mGameSimulation.setPassOrientation(orientation);
							}
						}
					}
				}
				if(isPassed) passStrength-=10;
				if(passStrength<=0) passStrength=0;
				mGameSimulation.setPassStrength(passStrength);
				
				//if(!mGameSimulation.hasReceived()) mGameSimulation.receive(1);
			}

			//if the ball is shot.
			else if (isShoot&&(shootStrength>0)&&!isGoal){
				
				//int desX=ball.getDestination().getX();
				//int desY=ball.getDestination().getY();
				double slope=ball.getSlope();
				double friction=mGameSimulation.getFriction();
				int xIncrement=ball.getIncrement().getX();
				int yIncrement=ball.getIncrement().getY();
				//reverse the sign of increment if the direction is to left.
				xIncrement=(int) (xIncrement*mGameSimulation.getShootOrientationX()*(1-friction));
				yIncrement=(int) (yIncrement*mGameSimulation.getShootOrientationY()*(1-friction));
				if(slope!=0) {
					//set X.
					//if shoot to right
					if(mGameSimulation.getShootOrientationX()==Constants.shoottoRightX){
						//if can move to right move to right
						if((x+xIncrement+15)<mGamePanelCoords.getRightBound()){
							x+=xIncrement;
						}
						else{
							//else get the potential destination of the ball.
							int potentialX=x+xIncrement;
							int potentialY=0;
							if(slope>0){
									potentialY=y+yIncrement;
							}
							else{
								potentialY=y-yIncrement;
							}
							//if it will score.
							if(mGameSimulation.isGoal(potentialX,potentialY)){
								x=(int) mGamePanelCoords.getRightBound();
								mGameSimulation.setShootStrength(0);
							}
							//if it will bounce back
							else{
								x=(int) mGamePanelCoords.getRightBound()-10;
								mGameSimulation.setShootOrientationX(Constants.shoottoLeftX);
							}
						}
					}
					//if shoot to left
					else{
						if((x+xIncrement)>mGamePanelCoords.getLeftBound()){
							x+=xIncrement;
						}
						else{
							//else get the potential destination of the ball.
							int potentialX=x+xIncrement;
							int potentialY=0;
							if(slope>0){
									potentialY=y+yIncrement;
							}
							else{
								potentialY=y-yIncrement;
							}
							//if it will score.
							if(mGameSimulation.isGoal(potentialX,potentialY)){
								x=(int) mGamePanelCoords.getLeftBound()-5;
								System.out.println("leftGoal");
								mGameSimulation.setShootStrength(0);
							}
							//if it will bounce back
							else{
								x=(int) mGamePanelCoords.getLeftBound()-5;
								mGameSimulation.setShootOrientationX(Constants.shoottoRightX);
							}
						}
					}
					
					//Set Y.
					if(slope>0){
						if((y+yIncrement+15)<mGamePanelCoords.getLowerBound()){
							y+=yIncrement;
						}
						else{
							y=(int) mGamePanelCoords.getLowerBound()+10;
							ball.setSlope(-slope);
							//mGameSimulation.setShootOrientationY(Constants.shootReverseY);
						}
					}
					else{
						if((y-yIncrement-15)>mGamePanelCoords.getUpperBound()){
							y-=yIncrement;
						}
						else{
							y=(int) mGamePanelCoords.getUpperBound()-10;
							ball.setSlope(-slope);
							//mGameSimulation.setShootOrientationY(Constants.shootReverseY);
						}
					}
				}
				else{ 
					if(mGameSimulation.getShootOrientationX()==Constants.shoottoRightX){
						if(x+30+15<mGamePanelCoords.getRightBound()){
							x+=30;
						}
						else{
							int potentialX=x+xIncrement;
							int potentialY=0;
							if(slope>0){
									potentialY=y+yIncrement;
							}
							else{
								potentialY=y-yIncrement;
							}
							if(mGameSimulation.isGoal(potentialX,potentialY)){
								x=(int) mGamePanelCoords.getRightBound();
								mGameSimulation.setShootStrength(0);
							}
							else{
								x=(int) mGamePanelCoords.getRightBound()-5;
								mGameSimulation.setShootOrientationX(Constants.shoottoLeftX);
							}
						}
					}
					else{
						if(x-30<mGamePanelCoords.getLeftBound()){
							x-=30;
						}
						else{
							int potentialX=x-30;
							int potentialY=y;
							if(mGameSimulation.isGoal(potentialX,potentialY)){
								x=(int) mGamePanelCoords.getLeftBound()-5;
								mGameSimulation.setShootStrength(0);
							}
							else{
								x=(int) mGamePanelCoords.getLeftBound()-5;
								mGameSimulation.setShootOrientationX(Constants.shoottoRightX);
							}
						}
					}
				}
				
				System.out.println(x+" "+y);
				if(isShoot){
					shootStrength-=10;
					if(shootStrength<=0) {
						shootStrength=0;
						isShoot = false;
					}
				}
				mGameSimulation.setShootStrength(shootStrength);
			}
			
			else if(isGoal){
				mGameSimulation.setShootStrength(0);
				mGameSimulation.setPassStrength(0);
			}
			
			if(!mGameSimulation.hasReceived() && !mGameSimulation.getOtherTeam().hasBall() && !isShoot) {
					mGameSimulation.receive(3);
			}
			
			if(!ClientPanels.onlineGame) {
				//mGameSimulation.team1Move();
				//mGameSimulation.team2Move();
			}
			
			
			b.setLocation(x, y);
			g.drawImage(ballImage, x, y, this);
			
			g.setColor(Color.YELLOW);
			for(Player p2: team2.getStarting()){
				//preventing duplicate variables
				//System.out.println("AI team: "+p2.getName());
				if(p2==team2.getMainPlayer()){
					g.setColor(Color.ORANGE);
				}
				else{
					g.setColor(Color.yellow);
					if(!team2.hasBall()){
						Queue<Pair> result=mGameSimulation.findShortestPath(p2);
						if(result!=null){
							for(Pair pair: result){
								if(!mGameSimulation.AIPlayerCollide(p2,pair)){
									p2.setLocation(pair);
									break;
								}
							}
						}
					}
					
				}
				int x2=p2.getX();
				int y2=p2.getY();
				if(!mGamePanelCoords.isInField(x2, y2)){
					p2.reset();
				}
				g.fillOval(x2, y2, 30, 30);
				g.drawString(p2.getPosition(), x2+7, y2-8);
			}
		}
		
		private Player collide(Ball b) {
			// TODO Auto-generated method stub
			Player tempMainPlayer=mGameSimulation.getPastPlayer();
			boolean isPassed=false;
			boolean isShot=false;
			if(mGameSimulation.getPassStrength()>0){
				isPassed=true;
			}
			if(mGameSimulation.getShootStrength()>0){
				isShot=true;
			}
			
			//if it is not a shot, then we can perform collide.
			if(!isShot){
				//go through every player at the field.
				Vector<Player> temp=new Vector<Player>();
				for(Player p:mGameSimulation.getTeam1().getStarting()){
					temp.add(p);
				}
				for(Player p:mGameSimulation.getTeam2().getStarting()){
					temp.add(p);
				}
				for(Player p: temp){
					if(p!=tempMainPlayer){
						int x=p.getX();
						int y=p.getY();
						
						int bx=ball.getX();
						int by=ball.getY();
						
						//if the ball is within the radius of any player.
						int pBoundX=x+35;
						int pBoundY=y+35;
						int bBoundX=bx+20;
						int bBoundY=by+20;
						
						if((pBoundX>bx&&x<bBoundX)&&(y<bBoundY&&pBoundY>by)){
							System.out.println("enter");
							return p;
						}
					}
				}
			}
			else if(isShot){
				//calculate if goalkeeper have the ball.
				Vector<Player> temp=new Vector<Player>();
				for(Player p:mGameSimulation.getTeam1().getStarting()){
					temp.add(p);
				}
				for(Player p:mGameSimulation.getTeam2().getStarting()){
					temp.add(p);
				}
				for(Player p: temp){
					if(p.getPosition().equals(Constants.goalkeeper)){
						int x=p.getX();
						int y=p.getY();
						
						int bx=ball.getX();
						int by=ball.getY();
						
						//if the ball is within the radius of any player.
						int pBoundX=x+35;
						int pBoundY=y+35;
						int bBoundX=bx+20;
						int bBoundY=by+20;
						
						if((pBoundX>bx&&x<bBoundX)&&(y<bBoundY&&pBoundY>by)){
							System.out.println("enter");
							if(mGameSimulation.getTeam1().isOnTeam(p)){
								//set the ball's location.
								b.setLocation(p.getX()+30, p.getY()+20);
							}
							else{
								b.setLocation(p.getX()-5, p.getY()+20);
							}
							return p;
						}
					}
				}
				
			}
			return null;
		}


		public void refresh(){
			repaint();
			revalidate();
		}
}
