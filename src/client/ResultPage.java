package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import game.GameSimulation;
import game.Team;
import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class ResultPage extends JPanel {
	private JLabel scoreLabel;
	private JLabel expLabel;
	private JLabel coinsLabel;
	private JButton continueButton;
	private int scoreTeam1;
	private int scoreTeam2;
	private int expPoint;
	private int coins;
	private GameSimulation mGameSimulation;
	
	private final String bgURL = "./resource/honda.jpg";
	private Image bg;
	
	public ResultPage(ActionListener al)
	{
		initializeComponents();
		createGUI();
		addEvents(al);
	}
	
	private void initializeComponents()
	{
		
		scoreLabel = new JLabel("");
		expLabel = new JLabel("");
		coinsLabel = new JLabel("");
		continueButton = new JButton("Continue");
	}
	
	public void setGameSimulation(GameSimulation input){
		mGameSimulation=input;
		scoreTeam1=mGameSimulation.getTeam1score();
		scoreTeam2=mGameSimulation.getTeam2score();
		calculate();
		scoreLabel.setText(""+scoreTeam1+" : "+scoreTeam2);
		expLabel.setText(""+expPoint+" EXP");
		coinsLabel.setText(""+coins+" COINS");
	}
	private void calculate() {
		// TODO Auto-generated method stub
		Team mainTeam=mGameSimulation.getMainTeam();
		Team winTeam=mGameSimulation.winTeam();
		
		//if tied.
		if(winTeam==null){
			expPoint=Constants.expTie;
			coins=Constants.baseCoin;
		}
		else if(winTeam==mainTeam){
			//win
			expPoint=Constants.expWin;
			coins=Math.abs(scoreTeam1-scoreTeam2)*Constants.baseCoin;
		}
		else{
			//if lose
			expPoint=Constants.expLose;
			coins=0;
		}
	}

	private void createGUI()
	{
		//set font
		ProjectFonts.setMainFont(scoreLabel, 100);
		ProjectFonts.setMainFont(expLabel, 40);
		ProjectFonts.setMainFont(coinsLabel, 40);
		ProjectFonts.setMainFont(continueButton, 40);
		
		scoreLabel.setAlignmentX(CENTER_ALIGNMENT);
		expLabel.setAlignmentX(CENTER_ALIGNMENT);
		coinsLabel.setAlignmentX(CENTER_ALIGNMENT);
		continueButton.setAlignmentX(CENTER_ALIGNMENT);
		
		//top border
		scoreLabel.setBorder(new EmptyBorder(100,100,100,100));
		
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		this.setForeground(Color.WHITE);
		this.setPreferredSize(Constants.screenSize);
		this.add(scoreLabel);
		this.add(expLabel);
		this.add(coinsLabel);
		//newline
		this.add(new JLabel(" "));
		continueButton.setOpaque(false);
		continueButton.setContentAreaFilled(false);
		continueButton.setBorderPainted(false);
		continueButton.setForeground(Color.WHITE);
		this.add(continueButton);
	}
	
	private void addEvents(ActionListener al)
	{
		continueButton.addActionListener(al);
		/*continueButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				storeData();
			}
			
		});*/
	}
	
	public void paintComponent(Graphics g)
	{
		bg = ImageLibrary.getImage(bgURL);
		bg = bg.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(bg, 0, 0, this);
	}
	//true if store success
	public boolean storeData(){
		if(ClientPanels.onlineGame&&MainMenu.currentUser != null){
			if(ClientPanels.isServer){
				JDBC jdbc = new JDBC();
				jdbc.updateExpAndCoin(MainMenu.currentUser, expPoint+MainMenu.userExp, coins+MainMenu.userCoin);
			}
			else{
				ClientPortGUI.clientSocket.updateClientExpAndCoin(MainMenu.currentUser, expPoint+MainMenu.userExp, coins+MainMenu.userCoin);
			}
			return true;
		}
		return false;
	}
}
