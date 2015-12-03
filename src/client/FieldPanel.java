package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import game.Ball;
import game.GamePanelCoords;
import game.GameSimulation;
import game.Player;
import library.ImageLibrary;
import server.Clock;
import server.CounterThread;
import server.ServerPortGUI;
import util.Constants;

public class FieldPanel extends JPanel{

	private static final long serialVersionUID = 1;

	private Player demo;
	
	private final String fieldBgURL = "./resource/field_background.png";
	private Image fieldBg;
	private FieldBoard actualField;
	private Ball ball;
	private JPanel gameInfo;
	private JLabel scoreLabel;
	private JLabel scoreTeam1;
	private JLabel scoreTeam2;
	private JLabel halfInfo;
	private int scoreIntTeam1;
	private int scoreIntTeam2;
	private JLabel timeLabel;
	private JLabel time;
	//for demo purposes only
	private JButton endButton;
	private JButton pauseButton;
	private JButton continueButton;

	
	private int posx;
	private int posy;
	private int score1;
	private int score2;
	
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean moveUp = false;
	private boolean moveDown = false;
	
	private int orientation = 2;
	
	private GameSimulation mGameSimulation;
	private GamePanelCoords mGamePanelCoords;
	private CounterThread counterThread;
	private Clock clock;
	
	private boolean isPaused = false;
	
	private long time_before;
	
	
	public FieldPanel(ActionListener al, GameSimulation gameSimulation, ChooseFormation cf)
	{	
		mGameSimulation= gameSimulation;
		initializeComponents(cf);
		createGUI();
		addEvents(al);
		setKeyBindings();
	}
	
	private void setKeyBindings(){
	    ActionMap actionMap = getActionMap();
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = getInputMap(condition);

	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "left_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "left_released");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "right_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "right_released"); 
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "up_released");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "down_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "down_released");
	    //Offense: J: pass K: shoot Defense: J: switch player K: tackle 
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J,0,false), "j_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J,0,true), "j_released");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K,0,false), "k_pressed");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K,0,true), "k_released");
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,0,false), "r_pressed");
	    

	    actionMap.put("left_pressed", new KeyAction("left_pressed"));
	    actionMap.put("left_released", new KeyAction("left_released"));
	    actionMap.put("right_pressed", new KeyAction("right_pressed"));
	    actionMap.put("right_released", new KeyAction("right_released"));
	    actionMap.put("up_pressed", new KeyAction("up_pressed"));
	    actionMap.put("up_released", new KeyAction("up_released"));
	    actionMap.put("down_pressed", new KeyAction("down_pressed"));
	    actionMap.put("down_released", new KeyAction("down_released"));
	    actionMap.put("j_pressed", new KeyAction("j_pressed"));
	    actionMap.put("j_released", new KeyAction("j_released"));
	    actionMap.put("k_pressed", new KeyAction("k_pressed"));
	    actionMap.put("k_released", new KeyAction("k_released"));
	    actionMap.put("r_pressed", new KeyAction("r_pressed"));
	    
	}
	
	   private class KeyAction extends AbstractAction {
		   
		private static final long serialVersionUID = 1;

			public KeyAction(String actionCommand) {
		         putValue(ACTION_COMMAND_KEY, actionCommand);
		      }

		      @Override
		      public void actionPerformed(ActionEvent actionEvt) {
		    	  if(actionEvt.getActionCommand() == "left_pressed"){
		    		  moveLeft = true;
		    	  }
		    	  else if(actionEvt.getActionCommand() == "left_released"){
		    		  moveLeft = false;
		    	  }
		    	  
		    	  if(actionEvt.getActionCommand() == "right_pressed"){
		    		  moveRight = true;
		    	  }
		    	  else if(actionEvt.getActionCommand() == "right_released"){
		    		  moveRight = false;
		    	  }
		    	  
		    	  if(actionEvt.getActionCommand() == "up_pressed"){
		    		  moveUp = true;
		    	  }
		    	  else if(actionEvt.getActionCommand() == "up_released"){
		    		  moveUp = false;
		    	  }
		    	  if(actionEvt.getActionCommand() == "down_pressed"){
		    		  moveDown = true;
		    	  }
		    	  
		    	  else if(actionEvt.getActionCommand() == "down_released"){
		    		  moveDown = false;
		    	  }
		    	  
		    	  if(actionEvt.getActionCommand() == "r_pressed"){
		    		  mGameSimulation.prepareForKickOff(mGameSimulation.getTeam1());
					ball.reset();
		    	  }
		    	  
		    	  if(actionEvt.getActionCommand()=="j_pressed"){
		    		  if(mGameSimulation.getMainTeam().hasBall()){		    			  
			    		  Player p=mGameSimulation.getMainTeam().getMainPlayer();
		    			  if(p.hasBall()) {
		    				  //mGameSimulation.setPassBeforeTime(System.currentTimeMillis());
		    				  mGameSimulation.setUpPass();
		    			  }
		    		  }
		    		  else{
		    			  mGameSimulation.SwitchPlayer();
		    		  }
		    	  }
		    	  else if(actionEvt.getActionCommand()=="j_released"){
		    		  
		    		  Player p=mGameSimulation.getMainTeam().getMainPlayer();
		    		  if(p.hasBall()) {
		    			  //mGameSimulation.setPassAfterTime(System.currentTimeMillis());
		    			  mGameSimulation.Pass(orientation);  
		    		  }
		    	  }
		    	
		    	  if(actionEvt.getActionCommand()=="k_pressed"){
		    		  Player p=mGameSimulation.getMainTeam().getMainPlayer();
		    		  if(p.hasBall()){
		    			  //mGameSimulation.setShootBeforeTime(System.currentTimeMillis());
		    			  mGameSimulation.setUpShoot();
		    		  }
		    		  else{
		    			  if(!mGameSimulation.getIsTackle()){
		    				  mGameSimulation.Tackle();
		    			  }
		    			  else{
		    				  mGameSimulation.setIsTackle(false);
		    			  }
		    		  }
		    	  }
		    	  else if(actionEvt.getActionCommand()=="k_released"){
		    		  Player p=mGameSimulation.getMainTeam().getMainPlayer();
		    		  if(p.hasBall()) {
		    			  //mGameSimulation.setShootAfterTime(System.currentTimeMillis());
		    			  mGameSimulation.Shoot();
		    		  }
		    	  }
		      }
	   }
	   
	public void refresh(){
  	  	deleteOldBoard();
  	  	addNewBoard();
		this.revalidate();
		this.repaint();

	}
	   
	public void deleteOldBoard(){
		this.remove(actualField);
	}
	   
	public void setHalfTime(){
		continueButton.setEnabled(true);
		pauseButton.setEnabled(false);
		halfInfo.setText(" PAUSED");
		counterThread.setPause(true);
		clock.setPause(true);
	}
	   
	   
	public void addNewBoard(){
		Player mainPlayer=mGameSimulation.getMainTeam().getMainPlayer();
		//System.out.println("mainPlayer: "+mainPlayer.getName());
		posx=mainPlayer.getX();
		posy=mainPlayer.getY();
		if(moveLeft){
			if(posx-5>mGamePanelCoords.getLeftBound()){
				posx -= 5+(double)mainPlayer.getSpeed()/100;//evetually we want to set the increment to be the speed of the player.
				//posx -= mainPlayer.getSpeed()
			}
		}
		
		if(moveRight){
  		  if(posx+45<mGamePanelCoords.getRightBound()){
			  posx += 5+(double)mainPlayer.getSpeed()/100;
		  }
		}
		
		if(moveUp){
  		  if(posy-5>mGamePanelCoords.getUpperBound()){
    		  posy -= 5+(double)mainPlayer.getSpeed()/100;
		  }
		}
		
		if(moveDown){
  		  if(posy+45<mGamePanelCoords.getLowerBound()){
			  posy += 5+(double)mainPlayer.getSpeed()/100;
		  }
		}
		
		if(moveUp&&!moveDown&&!moveRight&&!moveLeft){
			orientation = 0;
		}
		else if(moveUp&&!moveDown&&moveRight&&!moveLeft){
			orientation = 1;
		}
		else if(!moveUp&&!moveDown&&moveRight&&!moveLeft){
			orientation = 2;
		}
		else if(!moveUp&&moveDown&&moveRight&&!moveLeft){
			orientation = 3;
		}
		else if(!moveUp&&moveDown&&!moveRight&&!moveLeft){
			orientation = 4;
		}
		else if((!moveUp&&moveDown&&!moveRight&&moveLeft)){
			orientation = 5;
		}
		else if(!moveUp&&!moveDown&&!moveRight&&moveLeft){
			orientation = 6;
		}
		else if(moveUp&&!moveDown&&!moveRight&&moveLeft){
			orientation = 7;
		}
		else{
			orientation = actualField.getCurrentOrientation();
		}
				
		//if a player loses the ball he should freeze.
		if(!(mGameSimulation.getTackleFailed()>0)&&(!mGameSimulation.collide(posx,posy))){
			mGameSimulation.getMainTeam().getMainPlayer().setLocation(posx,posy);
		}
		else{
			if(mGameSimulation.getTackleFailed()>0) mGameSimulation.setTackelFailed(mGameSimulation.getTackleFailed()-3);
		}
		ball = mGameSimulation.getBall();
		int ballX=ball.getX();
		if(mGameSimulation.isGoal()){
			if(!ClientPanels.onlineGame||(ClientPanels.onlineGame&&ClientPanels.isServer)){
				System.out.println("bx: "+ballX);
				System.out.println(mGamePanelCoords.getRightBound()/2);
				if(ballX<=mGamePanelCoords.getLeftBound()+20){
					scoreIntTeam2++;
					mGameSimulation.setTeam2Score(scoreIntTeam2);
					scoreTeam2.setText(" "+scoreIntTeam2);
					mGameSimulation.prepareForKickOff(mGameSimulation.getTeam1());
					CounterThread.serverHoldingBall = true;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				else{
					scoreIntTeam1++;
					mGameSimulation.setTeam1Score(scoreIntTeam1);
					scoreTeam1.setText(" "+scoreIntTeam1);
					mGameSimulation.prepareForKickOff(mGameSimulation.getTeam2());
					CounterThread.serverHoldingBall = false;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			if(ClientPanels.onlineGame&&!ClientPanels.isServer){
				if(ballX<=mGamePanelCoords.getLeftBound()+10){
					mGameSimulation.resetForKickOff(mGameSimulation.getTeam2(),false);
					CounterThread.serverHoldingBall = true;
					ball.reset();
				}
				else{
					mGameSimulation.resetForKickOff(mGameSimulation.getTeam2(),true);
					CounterThread.serverHoldingBall = false;
					ball.reset();
				}
			}
		}
		actualField = new FieldBoard(mGameSimulation, 0 , gameInfo.getHeight(), orientation, mGamePanelCoords);
		actualField.refresh();
		this.add(actualField, BorderLayout.CENTER);
	}
	  
	public void reset(){
		mGameSimulation.prepareForKickOff(mGameSimulation.getTeam2());
		ball.reset();
		actualField = new FieldBoard(mGameSimulation, 0 , gameInfo.getHeight(), orientation, mGamePanelCoords);
		actualField.refresh();
		this.add(actualField, BorderLayout.CENTER);
	}
	   
	private void initializeComponents(ChooseFormation cf)
	{
		
		ball = mGameSimulation.getBall();
		//ball.reset();
		endButton = new JButton("EndGame");
		
		gameInfo = new JPanel();
		scoreLabel = new JLabel("Score ");
		scoreIntTeam1=0;
		scoreIntTeam2=0;
		scoreTeam1 = new JLabel(" "+scoreIntTeam1);
		scoreTeam2 = new JLabel(" "+scoreIntTeam2);
		
		scoreIntTeam1=0;
		scoreIntTeam2=0;
		
		int startX=0;
		int startY=gameInfo.getHeight();
		
		timeLabel = new JLabel("Time ");
		time = new JLabel("0:00");
		
		halfInfo = new JLabel(" FIRST HALF");
		
		pauseButton = new JButton("Pause Game");
		continueButton = new JButton("Continue to Second Half");
		continueButton.setEnabled(false);
		
		
		
		// initialize the FieldBoard
		mGamePanelCoords=new GamePanelCoords(startX,startY);
		actualField = new FieldBoard(mGameSimulation,startX,startY, orientation, mGamePanelCoords);
		//pass in to chooseformation to determine base position
		cf.passInGamePanelCoords(mGamePanelCoords);
		mGameSimulation.passInGamePanelCoords(mGamePanelCoords);
		
	}
	
	private void createGUI()
	{
		this.setPreferredSize(Constants.screenSize);
		this.setLayout(new BorderLayout());
		
		gameInfo.add(scoreLabel);
		gameInfo.add(scoreTeam1);
		gameInfo.add(new JLabel("  "));
		gameInfo.add(scoreTeam2);
		gameInfo.add(new JLabel("   "));
		gameInfo.add(timeLabel);
		gameInfo.add(time);
		gameInfo.add(halfInfo);
		gameInfo.add(pauseButton);
		gameInfo.add(continueButton);
		gameInfo.add(endButton);
		this.add(gameInfo,BorderLayout.NORTH);
		
		fieldBg = ImageLibrary.getImage(fieldBgURL);
		//- 100 arbitrary number
		//will adjust later
		fieldBg = fieldBg.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height-100, Image.SCALE_SMOOTH);
		
		actualField.setPreferredSize(new Dimension(Constants.screenSize.width, Constants.screenSize.height-100));
		actualField.setVisible(true);
		this.add(actualField, BorderLayout.CENTER);
	}
	
	private void addEvents(ActionListener al)
	{
		endButton.addActionListener(al);
		pauseButton.addMouseListener(
				new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isPaused) {
					clock.setPause(true);
					counterThread.setPause(true);
					isPaused = true;
					pauseButton.setText("Unpause");
				}
				else {
					clock.setPause(false);
					counterThread.setPause(false);
					isPaused = false;
					pauseButton.setText("Pause");
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				//clock.setPause(true);
				/*clock.setPause(true);
				//show popup and freeze screen
				String title = "Pause";
				String message = "Please select an option";
				Object[] options = {new JButton("Quit Game"), new JButton("Formation"),new JButton("Controls")};
				JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);	
				*/
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//clock.setPause(false);
				//clock.run();
				//clock.setPause(false);
				//System.out.println("Is clock paused? " + clock.getIsPaused());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}	
		});
		
		continueButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				reset();
				continueButton.setEnabled(false);
				clock.startSecondHalf();
				clock.setPause(false);
				counterThread.setPause(false);
				halfInfo.setText(" SECOND HALF");
				pauseButton.setEnabled(true);
			}
			
		});
		
		
		
	}
	
	public void updateClock(String time_){
		time.setText(time_);
	}
	
	public void addClock(Clock clock){
		this.clock = clock;
	}
	
	
	//what to do when the game is over
	private void gameOver()
	{
		//go to next screen
	}
	
	
	public int getTeam1Score(){
		return scoreIntTeam1;
	}
	public int getTeam2Score(){
		return scoreIntTeam2;
	}
	public void setTeam1Score(int score){
		scoreIntTeam1 = score;
		mGameSimulation.setTeam1Score(score);
		scoreTeam1.setText(" "+scoreIntTeam1);
	}
	public void setTeam2Score(int score){
		scoreIntTeam2 = score;
		mGameSimulation.setTeam2Score(score);
		scoreTeam2.setText(" "+scoreIntTeam2);
	}
	public void addCounterThread(CounterThread counterThread){
		this.counterThread = counterThread;
	}
	public GameSimulation getGameSimulation(){
		return mGameSimulation;
	}
}