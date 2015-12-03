package server;

import java.util.Vector;

public class CommunicatorDatabase extends Communicator {
	private Vector<String> userNameVector = null;
	private Vector<Integer> expVector = null;
	public void setUserNameAndExp(Vector<String> userName, Vector<Integer> exp){
		userNameVector = userName;
		this.expVector = exp;
	}
	public Vector<String> getUserNameVector(){
		return userNameVector;
	}
	public Vector<Integer> getExpVector(){
		return expVector;
	}
}
