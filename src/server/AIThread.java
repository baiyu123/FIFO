package server;

import client.ClientPanels;
import game.GameSimulation;

public class AIThread extends Thread {
	private GameSimulation mGameSimulation;
	
	public AIThread(GameSimulation inGameSimulation){
		mGameSimulation = inGameSimulation;
	}
	
	public void run(){
		while(true){
			try {
				AIThread.sleep(33);
				if(ClientPanels.onlineGame){
					if(ClientPanels.isServer){
						mGameSimulation.team1Move();
					}else{
						mGameSimulation.team2Move();
					}
				}
				else{
					mGameSimulation.team1Move();
					mGameSimulation.team2Move();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
