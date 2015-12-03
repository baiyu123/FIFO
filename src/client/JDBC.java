package client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class JDBC {
	
	private Connection conn;
	private Statement st;
	private int coin = -1;
	
	
	public void print(){
		try {
			ResultSet rs = st.executeQuery("SELECT userID, username, password, exp, coin FROM UserInfo;");
			while(rs.next()){
				int userID = rs.getInt("userID");
				String username = rs.getString("username");
				String password = rs.getString("password");
				int exp = rs.getInt("exp");
				int coin = rs.getInt("coin");
				System.out.println(userID + ": " + username + " " + password + " " + exp + " " + coin);
			}
			rs.close();
			//st.close();
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}

	}
	
	public void add(String username, String password, int exp, int coin){
		//check if username doesn't exist
		//this will be done in main
		//so that the username passed in must not be in the database already
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("INSERT INTO USERINFO(USERNAME,PASSWORD, EXP, COIN) VALUES(?,?,?,?)");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setInt(3, exp);
			ps.setInt(4, coin);
			ps.executeUpdate();	
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
	}
	
	public void updateExpAndCoin(String user, int exp, int coin){		
		PreparedStatement ps;		
		try {
			ps = conn.prepareStatement("UPDATE USERINFO set exp = ?, coin = ? WHERE username = '" + user + "'");
			ps.setInt(1, exp);
			ps.setInt(2, coin);
			ps.executeUpdate();	
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
	}
	
	public int getExp(String user){
		String queryCheck = "SELECT username, exp, coin from USERINFO WHERE USERNAME = '" + user + "'";
		ResultSet rs;
		try {
			rs = st.executeQuery(queryCheck);
			while(rs.next()){
				rs.getString("username");
				int exp = rs.getInt("exp");
				coin = rs.getInt("coin");
				return exp;
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
		return -1;
	}
	
	//must be called right after getExp()
	public int getCoin(String user){
		return coin;
	}

	
	public void close(){
		try{
			if(conn != null){
				conn.close();
			}
		} catch (SQLException sqle){
			System.out.println("sqle closing conn: " + sqle.getMessage());
		}
	}
	
	public boolean checkIfUsernameExists(String user){
		String queryCheck = "SELECT username from USERINFO WHERE USERNAME = '" + user + "'";
		ResultSet rs;
		boolean toReturn = false;
		try {
			rs = st.executeQuery(queryCheck);
			while(rs.next()){
				rs.getString("username");
				toReturn = true;
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
		return toReturn;
	}
	
	public boolean checkPassword(String user, String password_){
		String queryCheck = "SELECT username, password from USERINFO WHERE USERNAME = '" + user + "'";
		ResultSet rs;
		try {
			rs = st.executeQuery(queryCheck);
			while(rs.next()){
				rs.getString("username");
				String password = rs.getString("password");
				if(password.equals(password_))
					return true;
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
		return false;	
	}
	public Vector<String> getAllUsers(){
		Vector<String> userName = new Vector<String>();
		try {
			ResultSet rs = st.executeQuery("SELECT userID, username, password, exp, coin FROM UserInfo ORDER BY exp DESC;");
			while(rs.next()){
				int userID = rs.getInt("userID");
				String username = rs.getString("username");
				userName.addElement(username);
			}
			rs.close();
			//st.close();
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
		return userName;
	}
	public Vector<Integer> getAllScores(){
		Vector<Integer> expPoints = new Vector<Integer>();
		try {
			ResultSet rs = st.executeQuery("SELECT userID, username, password, exp, coin FROM UserInfo ORDER BY exp DESC;");
			while(rs.next()){
				int exp = rs.getInt("exp");
				expPoints.addElement(exp);
			}
			rs.close();
			//st.close();
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
		return expPoints;
	}
	
	public JDBC(){
		conn = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=mysql12345Qq");
			st = conn.createStatement();
			
			
			/*
			 * for testing purposes
			 * System.out.println(checkPassword("casey", "caseypassword"));
			 * add("casey", "caseypassword", 15, 200);
			 * System.out.println(checkPassword("casey", "caseypassword"));
			 * print();			
			 */
			

		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (SQLException sqle) {
			System.out.println("sqle: "+ sqle.getMessage());
		}
	}	
}
