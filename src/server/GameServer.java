package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import client.ChooseTeams;
import client.ClientPanels;
import client.JDBC;
import client.MainMenu;
import game.Ball;
import game.GameSimulation;
import game.Team;

public class GameServer extends Thread{
	private int portNum;
	private Socket s;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Communicator commIn; // communication input from client
	private ChooseTeams chooseTeams;
	private boolean connected;
	private Team opponentTeam;
	private Ball ball;
	private JDBC jdbc;


	public GameServer(int portNum){
		jdbc = new JDBC();
		ClientPanels.isServer = true;
		connected = false;
		System.out.println("Server port:" + portNum);
		this.portNum = portNum;
	}
	public void run(){

		ServerSocket ss = null;
		try{
			ss = new ServerSocket(portNum);
			System.out.println("Listening");
			s = ss.accept();
			connected = true;
			System.out.println("Connected!");
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			sendLeaderBoardData();
			//connected to a client
			while(true){
				commIn = (Communicator) ois.readObject();
				if(commIn.getTeamChoice() != -1){
					System.out.println("Team:"+commIn.getTeamChoice());
					chooseTeams.setTeam(commIn.getTeamChoice());
				}
				else if(commIn.getUsername() != null){
					//client trying to login
					if(commIn.getLogin()){
						if(jdbc.checkPassword(commIn.getUsername(), commIn.getPassword())){
							int userExp = jdbc.getExp(commIn.getUsername());
							int userCoin = jdbc.getCoin(commIn.getUsername());
							System.out.println("exist");
							updateClientLogin(commIn.getUsername(),userExp,userCoin,true);
						}
						else{
							updateClientLogin(commIn.getUsername(),0,0,false);
						}
					}
					else if(commIn.getUpdateExpPoint()){
						jdbc.updateExpAndCoin(commIn.getUsername(), commIn.getExperiecePoint(), commIn.getCoinPoint());
					}
					//client trying to sign up
					else{
						if(jdbc.checkIfUsernameExists(commIn.getUsername())){
							updateClientSignUpFail(commIn.getUsername());
						}
						else{
							jdbc.add(commIn.getUsername(), commIn.getPassword(), 0, 200);
							int userExp = jdbc.getExp(commIn.getUsername());
							int userCoin = jdbc.getCoin(commIn.getUsername());
							updateClientLogin(commIn.getUsername(),userExp,userCoin,true);
						}
					}
				}
				else{
					System.out.println(commIn.getBall().getX());
					System.out.println(commIn.getBall().getY());
					opponentTeam = commIn.getTeam();
					ball = commIn.getBall();
				}
			}
			
		}catch(IOException | ClassNotFoundException ioe){
			
		}finally{
			try{
				if(ois != null){
					ois.close();
				}
				if(oos != null){
					oos.close();
				}
				if(s != null){
					s.close();
				}
				if(ss != null){
					ss.close();
				}
			} catch(IOException ioe){
				System.out.println("ioe closing ss: "+ ioe.getMessage());
			}
		}
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
	
	public void insertChooseTeam(ChooseTeams chooseTeams){
		this.chooseTeams = chooseTeams;
	}
	//update team selection in chooseteam panel
	public void updateTeamSelection(int teamNum){
		if(connected){
			Communicator commOut = new Communicator();
			commOut.setTeamChoice(teamNum);
			sendObject(commOut);
		}
	}
	//update player position and ball position
	public void updateClientGame(GameSimulation gameSimulation){
		Communicator commOut = new Communicator();
		commOut.setBall(gameSimulation.getBall());
		Team tempTeam = gameSimulation.getTeam1();
		tempTeam.setIconToNull();
		commOut.setTeam(tempTeam);
		commOut.setScore1(gameSimulation.getTeam1Score());
		commOut.setScore2(gameSimulation.getTeam2Score());
		sendObject(commOut);
	}
	public Ball getNewBall() {
		return ball;
	}
	public Team getNewOpponentTeam() {
		return opponentTeam;
	}
	public void updateClientLogin(String usrName, int usrEx, int usrCoin,boolean loginSuccess){
		Communicator commOut = new Communicator();
		commOut.setLogin(loginSuccess);
		commOut.setUsername(usrName);
		commOut.setExperiencePoint(usrEx);
		commOut.setCoinPoint(usrCoin);
		sendObject(commOut);
	}
	public void updateClientSignUpFail(String usrName){
		Communicator commOut = new Communicator();
		commOut.setUsername(usrName);
		commOut.setSignUpFail(true);
		sendObject(commOut);
	}
	
	private void sendLeaderBoardData(){
		
		Vector<String> userName = jdbc.getAllUsers();
		Vector<Integer> expPoints = jdbc.getAllScores();
		CommunicatorDatabase commOut = new CommunicatorDatabase();
		commOut.setUserNameAndExp(userName, expPoints);
		sendObject(commOut);
	}
}
