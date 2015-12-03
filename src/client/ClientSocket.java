package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import game.Ball;
import game.GameSimulation;
import game.Team;
import server.Communicator;
import server.CommunicatorDatabase;

public class ClientSocket extends Thread{
	private  ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	private boolean connected = false;
	public ChooseTeams chooseTeams;
	private GameSimulation gameSimulation;
	private Team opponentTeam;
	private Ball ball;
	private MainMenu mainMenu;
	private int score1;
	private int score2;
	private Vector<String> userNamesVector;
	private Vector<Integer> expPonitsVector;
	
	ClientSocket(String hostname, int port){
		ClientPanels.isServer = false;
		opponentTeam = null;
		ball = null;
		try{
			s = new Socket(hostname, port);
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.print("portNum:" + port);
			connected = true;
		} catch (IOException e){
			System.out.println("ioe: "+e.getMessage());
		}
	}
	public Team getNewOpponentTeam(){
		return opponentTeam;
	}
	public Ball getNewBall(){
		return ball;
	}
	public void run(){
		try{
			while(true){
				Communicator commIn = (Communicator) ois.readObject();
				if(commIn.getTeamChoice() != -1){
					chooseTeams.setTeam(commIn.getTeamChoice());
				}
				//username existed
				else if(commIn.getUsername() != null){
					if(commIn.getSignUpFail()){
						mainMenu.signInFail();
					}
					else{
						mainMenu.updateLogin(commIn.getUsername(), commIn.getLogin(),commIn.getExperiecePoint(),commIn.getCoinPoint());
					}
				}
				
				//in game
				else if (commIn.getTeam() != null){
					opponentTeam = commIn.getTeam();
					ball = commIn.getBall();
					score1 = commIn.getScore1();
					score2 = commIn.getScore2();
				}
				else{
					CommunicatorDatabase commIndata = (CommunicatorDatabase) commIn;
					userNamesVector = commIndata.getUserNameVector();
					expPonitsVector = commIndata.getExpVector();
				}
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
				if( s != null){
					s.close();
				}
				if(ois != null){
					ois.close();
				}
				if(oos != null){
					oos.close();
				}
			} catch (IOException ioe){
				System.out.println("ioe closing streams: " + ioe.getMessage());
			}
		}
	}
	public void addMainmenu(MainMenu mainMenu){
		this.mainMenu = mainMenu;
	}
	
	public void sendObject(Communicator commOut){
		try{
			oos.reset();
			oos.writeObject(commOut);
			oos.flush();
		} catch (IOException e){
			System.out.println("ioe: " + e.getMessage());
		}
	}
	
	public void updateTeamSelection(int teamNum){
		Communicator commOut = new Communicator();
		commOut.setTeamChoice(teamNum);
		sendObject(commOut);
	}

	public void insertChooseTeam(ChooseTeams chooseTeams){
		this.chooseTeams = chooseTeams;
	}
	
	public boolean getConnected(){
		return connected;
	}
	
	public void insertSimulation(GameSimulation simulation) {
		gameSimulation = simulation;
	}
	
	public void updateServerGame(GameSimulation gameSimulation){
		Communicator commOut = new Communicator();
		commOut.setBall(gameSimulation.getBall());
		Team tempTeam = gameSimulation.getTeam2();
		tempTeam.setIconToNull();
		commOut.setTeam(tempTeam);
		sendObject(commOut);
	}
	public void checkPassword(String username, String password){
		Communicator commOut = new Communicator();
		commOut.setUsername(username);
		commOut.setPassword(password);
		commOut.setLogin(true);
		sendObject(commOut);
	}
	public void checkUser(String username, String password){
		Communicator commOut = new Communicator();
		commOut.setUsername(username);
		commOut.setPassword(password);
		sendObject(commOut);
	}
	public int getScore1(){
		return score1;
	}
	public int getScore2(){
		return score2;
	}
	public void updateClientExpAndCoin(String user, int expPoint, int coins) {
		Communicator commOut = new Communicator();
		commOut.setUsername(user);
		commOut.setCoinPoint(coins);
		commOut.setExperiencePoint(expPoint);
		commOut.setUpdateExpPoint();
		sendObject(commOut);
	}
	public Vector<String> getAllUsers(){
		return userNamesVector;
	}
	public Vector<Integer> getAllScores(){
		return expPonitsVector;
	}
}

