package server;

import java.io.Serializable;
import java.util.Vector;

import game.Ball;
import game.Team;

//custom class for server client communication
public class Communicator implements Serializable{
	public static final long serialVersionUID = 1;
	private Team myTeam;
	private Ball myBall;
	private int TeamChoice;
	private int score1;
	private int score2;
	private int Exp;
	boolean requestExp;
	private String username;
	private String password;
	private int usrExpPoint;
	private int usrCoinPoint;
	private boolean login;
	private boolean signUpFail;
	private boolean updateExpPoint;
	

	{
		updateExpPoint = false;
		signUpFail = false;
		login = false;
		username = null;
		password = null;
		score1 = -1;
		TeamChoice = -1;//default
	}
	
	public void setUpdateExpPoint(){
		updateExpPoint = true;
	}
	public boolean getUpdateExpPoint(){
		return updateExpPoint;
	}
	public void setSignUpFail(boolean sign){
		signUpFail = sign;
	}
	public boolean getSignUpFail(){
		return signUpFail;
	}
	public void setLogin(boolean login){
		this.login = login;
	}
	public boolean getLogin(){
		return login;
	}
	public void setExperiencePoint(int exp){
		usrExpPoint = exp;
	}
	public void setCoinPoint(int coin){
		usrCoinPoint = coin;
	}
	public int getExperiecePoint(){
		return usrExpPoint;
	}
	public int getCoinPoint(){
		return usrCoinPoint;
	}
	
	public void setUsername(String name){
		username = name;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public String getUsername(){
		return username;
	}
	public String getPassword(){
		return password;
	}
	public void setTeamChoice(int newChoice){
		TeamChoice = newChoice;
	}
	
	public int getTeamChoice(){
		return TeamChoice;
	}
	
	public void setTeam(Team team){
		myTeam = team;
	}
	
	public Team getTeam(){
		return myTeam;
	}
	
	public void setBall(Ball ball){
		myBall = ball;
	}
	
	public Ball getBall(){
		return myBall;
	}
	public void setScore1(int score){
		score1 = score;
	}
	public void setScore2(int score){
		score2 = score;
	}
	public int getScore1(){
		return score1;
	}
	public int getScore2(){
		return score2;
	}
}
