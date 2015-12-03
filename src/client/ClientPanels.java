package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import game.GameSimulation;
import server.AIThread;
import server.Clock;
import server.CounterThread;
import server.ServerPortGUI;
public class ClientPanels extends JPanel{
	
	private static final long serialVersionUID = 1;
	private StartScreen startScreen;
	public MainMenu mainMenu;
	public ChooseTeams chooseTeams;
	private ChooseGameSettings chooseGameSettings;
	private ChooseFormation chooseFormation;
	private FieldPanel field;
	private ResultPage resultPage;
	private HostOrJoin hostJoinMenu;
	private ClientPortGUI clientPort;
	private ServerPortGUI serverPort;
	private LeaderBoardPage leaderBoardPanel;
	private GameSimulation gameSimulation;
	public static boolean isServer;// if this is a server
	public static boolean onlineGame;//if this is a online game
	
	private CounterThread counterThread;
	private Clock clock;
	private AIThread aiThread;
	
	{
		
		isServer = false;
		onlineGame = false;
		startScreen = new StartScreen(
			new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent me){
					if (me.getButton() == MouseEvent.BUTTON1)
					{
						ClientPanels.this.removeAll();
						ClientPanels.this.repaint();
						ClientPanels.this.add(hostJoinMenu);
						ClientPanels.this.revalidate();
					}
				}
			}		
		);
		add(startScreen);
		
		gameSimulation= new GameSimulation();
		
		addEvents();
	}
	
	private ClientPanels getClientPanels(){
		return this;
	}
	
	private void addEvents()
	{
		
		mainMenu = new MainMenu(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					ClientPanels.this.removeAll();
					ClientPanels.this.repaint();
					//if(mainMenu.getOnline()){
					//	ClientPanels.this.add(hostJoinMenu);
					//}
					//else{
						//ClientPanels.this.add(field);
						ClientPanels.this.add(chooseTeams);
					//}
					ClientPanels.this.revalidate();
				}
			}
		);
		
		mainMenu.addLeaderBoardEvent(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							ClientPanels.this.removeAll();
							ClientPanels.this.repaint();
							leaderBoardPanel.updateLeaderBoard();
							ClientPanels.this.add(leaderBoardPanel);
							ClientPanels.this.revalidate();
						}
					}
				);
		
		leaderBoardPanel = new LeaderBoardPage(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent ae)
						{
							ClientPanels.this.removeAll();
							ClientPanels.this.repaint();
							ClientPanels.this.add(mainMenu);
							ClientPanels.this.revalidate();
						}
					}
				);
		
		serverPort = new ServerPortGUI(ClientPanels.this);
	
		clientPort = new ClientPortGUI(ClientPanels.this);
		
		
		ActionListener hostAction = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ClientPanels.onlineGame = true;
				ClientPanels.this.removeAll();
				ClientPanels.this.repaint();
				ClientPanels.this.add(serverPort);
				ClientPanels.this.revalidate();
			}
			
		};
		ActionListener clientAction = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ClientPanels.onlineGame = true;
				ClientPanels.this.removeAll();
				ClientPanels.this.repaint();
				ClientPanels.this.add(clientPort);
				ClientPanels.this.revalidate();
			}
		};
		
		hostJoinMenu = new HostOrJoin(clientAction,hostAction);
		
		
		chooseFormation = new ChooseFormation(
			this,	
			gameSimulation);
		
		chooseTeams = new ChooseTeams(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					ClientPanels.this.removeAll();
					ClientPanels.this.repaint();
					ClientPanels.this.add(chooseGameSettings);
					ClientPanels.this.revalidate();
					
					chooseFormation.updateTeam();
				}
			}	
		, gameSimulation);
		
		chooseGameSettings = new ChooseGameSettings(
			new ActionListener(){
			@Override
				public void actionPerformed(ActionEvent e) {
					ClientPanels.this.removeAll();
					ClientPanels.this.repaint();
					ClientPanels.this.add(chooseFormation);
					ClientPanels.this.revalidate();
				}
			}
		, gameSimulation);
		
		
		
		
		field = new FieldPanel(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ClientPanels.this.removeAll();
					ClientPanels.this.repaint();
					resultPage.setGameSimulation(gameSimulation);
					if(resultPage.storeData()){
						mainMenu.updateExpAndCoin();
					}
					ClientPanels.this.add(resultPage);
					ClientPanels.this.revalidate();
					
					gameSimulation.setFieldPanel(field);
				}
			}
		, gameSimulation, chooseFormation);
		
		resultPage = new ResultPage(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ClientPanels.this.removeAll();
					ClientPanels.this.repaint();
					ClientPanels.this.add(mainMenu);
					ClientPanels.this.revalidate();
				}
			}
		);
	}
	public ChooseTeams getChooseTeamGUI(){
		return chooseTeams;
	}
	
	public void switchToField()
	{
		this.removeAll();
		this.repaint();
		this.add(field);
		if(field != null) 
		{
			counterThread = new CounterThread(field,gameSimulation);
			counterThread.start();
			field.addCounterThread(counterThread);
			clock = new Clock(gameSimulation.getTime(), field, getClientPanels());
			//clock = new Clock(1, field, getClientPanels());
			field.addClock(clock);
			clock.start();
			aiThread = new AIThread(gameSimulation);
			aiThread.start();
		}
		this.revalidate();		
	}
	
	public void switchToResults(){
		this.removeAll();
		this.repaint();
		this.add(resultPage);
		resultPage.setGameSimulation(gameSimulation);
		if(resultPage.storeData()){
			mainMenu.updateExpAndCoin();
		}
		this.revalidate();
	}
	public GameSimulation getSimulation(){
		return gameSimulation;
	}
	public static void setOnline(boolean online){
		onlineGame = online;
	}
	
	public MainMenu getMainMenu(){
		return mainMenu;
	}
	
}
