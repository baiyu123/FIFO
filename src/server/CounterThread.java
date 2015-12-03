package server;

import com.mysql.fabric.Server;

import client.ClientPanels;
import client.ClientPortGUI;
import client.FieldPanel;
import game.Ball;
import game.GameSimulation;
import game.Team;

public class CounterThread extends Thread{
	
	private FieldPanel fieldPanel;
	private GameSimulation gameSimulation;
	public static boolean serverHoldingBall;
	private boolean isPaused;
	
	public CounterThread(FieldPanel inFieldPanel, GameSimulation gameSimulation){
		this.fieldPanel = inFieldPanel;
		this.gameSimulation = gameSimulation;
		serverHoldingBall = true;
	}
	
	public void run(){
		while(true){
			if(!isPaused){
				try {
					Thread.sleep(33);
					gameSimulation = fieldPanel.getGameSimulation();
					if(ClientPanels.onlineGame){
						if(ClientPanels.isServer){
							//gameSimulation.team1Move();
							//server send data
							ServerPortGUI.gameServer.updateClientGame(gameSimulation);
							//server get data

							Ball ball = ServerPortGUI.gameServer.getNewBall();
							Team opponentTeam = ServerPortGUI.gameServer.getNewOpponentTeam();
							if(gameSimulation.getTeam1().hasBall()){
								serverHoldingBall = true;
							}
							if(gameSimulation.getTeam2().hasBall()){
								serverHoldingBall = false;
							}
							//if server doesn't have ball, update it from client
							if(ball != null&&!gameSimulation.getTeam1().hasBall()){
								if(!serverHoldingBall&&!opponentTeam.hasBall()){
									gameSimulation.setBall(ball);
								}
								if(opponentTeam.hasBall()){
									gameSimulation.setBall(ball);
								}
							}
							if(opponentTeam != null){
								gameSimulation.setteam2(opponentTeam);
								//gameSimulation.getTeam2().SetMainPlayer(opponentTeam.getMainPlayer());;
							}
							
							fieldPanel.refresh();
						}
						else{
							//client send data
							ClientPortGUI.clientSocket.updateServerGame(gameSimulation);
							//client get data
							//gameSimulation.team2Move();

							
							Ball ball = ClientPortGUI.clientSocket.getNewBall();
							Team opponentTeam = ClientPortGUI.clientSocket.getNewOpponentTeam();
							int score1 = ClientPortGUI.clientSocket.getScore1();
							int score2 = ClientPortGUI.clientSocket.getScore2();
							fieldPanel.setTeam1Score(score1);
							fieldPanel.setTeam2Score(score2);
							
							if(gameSimulation.getTeam1().hasBall()){
								serverHoldingBall = true;
							}
							if(gameSimulation.getTeam2().hasBall()){
								serverHoldingBall = false;
							}
							
							//if client doesn't has ball update it from server
							if(ball != null&&!gameSimulation.getTeam2().hasBall()){
								if(serverHoldingBall&&!opponentTeam.hasBall()){
									gameSimulation.setBall(ball);
								}
								if(opponentTeam.hasBall()){
									gameSimulation.setBall(ball);
								}
							}
							if(opponentTeam != null){
								gameSimulation.setteam1(opponentTeam);
							}
							fieldPanel.refresh();
						}
					}
					else{
						fieldPanel.refresh();
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public void setPause(boolean pause){
		isPaused = pause;
	}
}
