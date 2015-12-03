package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class MainMenu extends JPanel{

	private static final long serialVersionUID = 1;
	
	private Image backgroundImage;
	private final String imageURL = "./resource/soccer_pitch.jpg";
	
	private JPanel mainPanel;
	private JPanel upPanel;
	private JPanel downPanel;
	private JButton FGButton;
	private JButton OGButton;
	private JPanel loginPanel;
	//we switch between loggedInPanel and loggedOutPanel
	private JPanel loggedInPanel;
	private JPanel loggedOutPanel;
	private JLabel loggedInLabel;
	private JLabel usernameTFLabel;
	private JLabel passwordTFLabel;
	private JTextField usernameTF;
	private JTextField passwordTF;
	private JLabel expLabel;
	private JLabel exp;
	private JLabel coinsLabel;
	private JLabel coins;
	private JButton loginButton;
	private JButton logoutButton;
	private JLabel usernameLabel;
	private JButton ContinueButton;
	private boolean online;
	private JButton leaderBoardButton;
	private boolean friendlyGame;
	
	private final String onlineString = "Online Game";
	private final String friendlyString = "Friendly Game";
	private final String selectedString = "Selected!";
	
	private JDBC jdbc = null;
	private GridBagConstraints gbc;
	private JLabel errMsg = new JLabel();
	
	public static String currentUser = null;
	public static int userExp = 0;
	public static int userCoin = 0;
	private JButton signupButton;
	private String password;

	private boolean isLoggedIn;//check if a user has logged in.
	
	public MainMenu(ActionListener al){
		initializeComponents();
		createGUI();
		addEvents(al);
	}

	private void initializeComponents() {
		// initialize components
		online = false;
		this.setLayout(new BorderLayout());
		mainPanel= new JPanel();
		mainPanel.setOpaque(false);
		//mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		upPanel= new JPanel();
		upPanel.setOpaque(false);
		downPanel= new JPanel();
		downPanel.setOpaque(false);
		Image img1=ImageLibrary.getImage("./resource/italy.jpg");
		ImageIcon imgicon1=new ImageIcon(img1);
		FGButton= new JButton(friendlyString,imgicon1);
		
	    Image img2=ImageLibrary.getImage("./resource/henry.jpg");
		ImageIcon imgicon2=new ImageIcon(img2);
		OGButton= new JButton(onlineString, imgicon2);
		
		
		//Image img3=ImageLibrary.getImage("./resource/controller.jpg");
		//ImageIcon imgicon3=new ImageIcon(img3);
		loginPanel = new JPanel();
		loggedInPanel = new JPanel();
		loggedOutPanel = new JPanel();
		loggedInLabel = new JLabel("Welcome ");
		usernameTFLabel = new JLabel("Username:");
		passwordTFLabel = new JLabel("Password");
		usernameTF = new JTextField(7);
		passwordTF = new JPasswordField(9);
		usernameLabel = new JLabel();
		expLabel = new JLabel("EXP:");
		exp = new JLabel();
		coinsLabel = new JLabel("COINS:");
		coins = new JLabel();
		loginButton = new JButton("Log in");
		logoutButton = new JButton("Log out");
		signupButton = new JButton("Sign up");
		
		Image img4=ImageLibrary.getImage("./resource/soccerball.png");
		ImageIcon imgicon4=new ImageIcon(img4);
		ContinueButton= new JButton("Continue",imgicon4);
		
		leaderBoardButton = new JButton("Leaderboard");
		
		
		
	}

	private void createGUI() {
		this.setPreferredSize(Constants.screenSize);
		//set font
		ProjectFonts.setMainFont(FGButton, 40);
		ProjectFonts.setMainFont(OGButton, 40);
		ProjectFonts.setMainFont(ContinueButton, 40);
		ProjectFonts.setMainFont(loggedInLabel, 20);
		ProjectFonts.setMainFont(usernameLabel, 20);
		ProjectFonts.setMainFont(loginButton, 20);
		ProjectFonts.setMainFont(logoutButton, 20);
		ProjectFonts.setMainFont(usernameTF, 20);
		ProjectFonts.setMainFont(usernameTFLabel, 20);
		//ProjectFonts.setMainFont(passwordTF, 20);
		ProjectFonts.setMainFont(passwordTFLabel, 20);
		ProjectFonts.setMainFont(expLabel, 20);
		ProjectFonts.setMainFont(exp, 20);
		ProjectFonts.setMainFont(coinsLabel, 20);
		ProjectFonts.setMainFont(coins, 20);
		ProjectFonts.setMainFont(leaderBoardButton, 20);
		ProjectFonts.setMainFont(signupButton, 20);
		
		//demo user
		/*usernameLabel.setText(currentUser);
		exp.setText("" + userExp);
		coins.setText("" + userCoin);*/
		
		//set up login
		loginPanel.setOpaque(false);
		loggedInPanel.setOpaque(false);
		loggedOutPanel.setOpaque(false);
		//want login to be on top right
		loginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		//want login to be left to right
		loggedInPanel.setLayout(new FlowLayout());
		loggedOutPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		loggedInPanel.add(leaderBoardButton);
		loggedInPanel.add(loggedInLabel);
		usernameLabel.setForeground(Color.RED);
		loggedInPanel.add(usernameLabel);
		loggedInPanel.add(expLabel);
		loggedInPanel.add(exp);
		loggedInPanel.add(coinsLabel);
		loggedInPanel.add(coins);
		logoutButton.setOpaque(false);
		logoutButton.setContentAreaFilled(false);
		logoutButton.setBorderPainted(false);
		loggedInPanel.add(logoutButton);
		leaderBoardButton.setOpaque(false);
		leaderBoardButton.setContentAreaFilled(false);
		leaderBoardButton.setBorderPainted(false);
		gbc.gridx = 0;
		gbc.gridy = 0;
		loggedOutPanel.add(usernameTFLabel, gbc);
		gbc.gridx = 1;
		loggedOutPanel.add(usernameTF, gbc);
		gbc.gridx = 2;
		loggedOutPanel.add(passwordTFLabel, gbc);
		gbc.gridx = 3;
		loggedOutPanel.add(passwordTF, gbc);
		loginButton.setOpaque(false);
		loginButton.setContentAreaFilled(false);
		loginButton.setBorderPainted(false);
		gbc.gridx = 4;
		loggedOutPanel.add(loginButton, gbc);
		gbc.gridx = 5;
		loggedOutPanel.add(signupButton, gbc);

		
		loginPanel.add(loggedOutPanel);
		
		this.add(loginPanel,BorderLayout.NORTH);
		
		//set up buttons
		FGButton.setPreferredSize(new Dimension(400,400));
		FGButton.setOpaque(false);
		FGButton.setContentAreaFilled(false);
		FGButton.setVerticalTextPosition(SwingConstants.CENTER);
	    FGButton.setHorizontalTextPosition(SwingConstants.CENTER);
	    FGButton.setBorderPainted(false);
		OGButton.setPreferredSize(new Dimension(400,400));
		OGButton.setOpaque(false);
		OGButton.setContentAreaFilled(false);
		OGButton.setVerticalTextPosition(SwingConstants.CENTER);
	    OGButton.setHorizontalTextPosition(SwingConstants.CENTER);
	    OGButton.setBorderPainted(false);
	    OGButton.setEnabled(false);
		mainPanel.add(FGButton);
		JLabel temp= new JLabel("");
		temp.setPreferredSize(new Dimension(500,200));
		mainPanel.add(temp);
		mainPanel.add(OGButton);		
		
		ContinueButton.setPreferredSize(new Dimension(250,200));
		ContinueButton.setContentAreaFilled(false);
		ContinueButton.setVerticalTextPosition(SwingConstants.CENTER);
		ContinueButton.setHorizontalTextPosition(SwingConstants.CENTER);
		ContinueButton.setBorderPainted(false);
		ContinueButton.setEnabled(false);
		downPanel.add(ContinueButton);
		
		//add to layout
		//this.add(upPanel,BorderLayout.NORTH);
		this.add(mainPanel,BorderLayout.CENTER);
		this.add(downPanel,BorderLayout.SOUTH);
		
		
		
	}

	private void addEvents(ActionListener al) {
		//add actionlisteners to each button
		FGButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ContinueButton.setEnabled(true);
				FGButton.setText(selectedString);
				FGButton.setForeground(Color.red);
				OGButton.setText(onlineString);
				OGButton.setForeground(Color.DARK_GRAY);
				online = false;
				friendlyGame = true;
				ClientPanels.setOnline(false);
			}
		});
		OGButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ContinueButton.setEnabled(true);
				OGButton.setText(selectedString);
				OGButton.setForeground(Color.red);
				FGButton.setText(friendlyString);
				FGButton.setForeground(Color.DARK_GRAY);
				online = true;
				friendlyGame = false;
				ClientPanels.setOnline(true);
			}
		});
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(usernameTF.getText() != null && passwordTF.getText() != null){
					String user = usernameTF.getText();
					password = passwordTF.getText();

					//hash the password
					try {
						byte[] bytesOfMessage = password.getBytes("UTF-8");
						MessageDigest md = MessageDigest.getInstance("MD5");
						byte[] hashedBytes = md.digest(bytesOfMessage);
						
						StringBuffer stringBuffer = new StringBuffer();
						for (int i = 0; i < hashedBytes.length; i++) {
							stringBuffer.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16)
									.substring(1));
						}
						password = stringBuffer.toString();
						
					} catch (UnsupportedEncodingException uee) {
						uee.printStackTrace();
					} catch (NoSuchAlgorithmException nsae) {
						nsae.printStackTrace();
					}
					//if server grab data from local sql else from server
					if(ClientPanels.isServer){
						jdbc = new JDBC();
	
						
						if(jdbc.checkPassword(user, password)){
							currentUser = user;
							userExp = jdbc.getExp(user);
							userCoin = jdbc.getCoin(user);
	
							usernameLabel.setText(currentUser);
							exp.setText("" + userExp);
							coins.setText("" + userCoin);
							
							usernameTF.setText("");
							passwordTF.setText("");			
							isLoggedIn = true;
							if(isLoggedIn) OGButton.setEnabled(true); // if the user has logged in, enable online game
							loginPanel.remove(loggedOutPanel);
							loginPanel.add(loggedInPanel);
							loginPanel.repaint();
							loginPanel.revalidate();
						}
						else{
							gbc.gridx = 0;
							gbc.gridy = 1;
							gbc.gridwidth = 6;
							errMsg = new JLabel("Username and password do not match.");
							ProjectFonts.setMainFont(errMsg, 20);
							errMsg.setForeground(Color.white);
							loggedOutPanel.add(errMsg, gbc);
							loginPanel.repaint();
							loginPanel.revalidate();
						}
						jdbc.close();
	
					}else{
						ClientPortGUI.clientSocket.checkPassword(user,password);
					}	
				}
				//after validating login

			}
		});
		logoutButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				isLoggedIn = false;
				if(!isLoggedIn) {
					OGButton.setEnabled(false); // if the user has logged in, enable online game
					OGButton.setText(onlineString);
					OGButton.setForeground(Color.DARK_GRAY);
				}
				loginPanel.remove(loggedInPanel);
				if(errMsg != null) loggedOutPanel.remove(errMsg);
				loginPanel.add(loggedOutPanel);
				loginPanel.repaint();
				loginPanel.revalidate();
			}
		});

		
		ContinueButton.addActionListener(al);
		signupButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(usernameTF.getText() != null && passwordTF.getText() != null){
					String user = usernameTF.getText();
					String password = passwordTF.getText();

					//hash the password
					try {
						byte[] bytesOfMessage = password.getBytes("UTF-8");
						MessageDigest md = MessageDigest.getInstance("MD5");
						byte[] hashedBytes = md.digest(bytesOfMessage);
						
						StringBuffer stringBuffer = new StringBuffer();
						for (int i = 0; i < hashedBytes.length; i++) {
							stringBuffer.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16)
									.substring(1));
						}
						password = stringBuffer.toString();
						
					} catch (UnsupportedEncodingException uee) {
						uee.printStackTrace();
					} catch (NoSuchAlgorithmException nsae) {
						nsae.printStackTrace();
					}
					//if server grab data from local sql else from server
					if(ClientPanels.isServer){
						jdbc = new JDBC();
	
						
						if(jdbc.checkIfUsernameExists(user)){
							signInFail();
							
						}
						else{
							
							currentUser = user;
							userExp = 0;
							userCoin = 200;
							jdbc.add(user, password, userExp, userCoin);
							usernameLabel.setText(currentUser);
							exp.setText("" + userExp);
							coins.setText("" + userCoin);
							
							usernameTF.setText("");
							passwordTF.setText("");			
							isLoggedIn = true;
							if(isLoggedIn) OGButton.setEnabled(true); // if the user has logged in, enable online game
							loginPanel.remove(loggedOutPanel);
							loginPanel.add(loggedInPanel);
							loginPanel.repaint();
							loginPanel.revalidate();
						}
						jdbc.close();
	
					}else{
						ClientPortGUI.clientSocket.checkUser(user,password);
					}	
				}
			}
		});
		
		
		
	}
	public void addLeaderBoardEvent(ActionListener al){
		leaderBoardButton.addActionListener(al);
	}
	
	public boolean getOnline(){
		return online;
	}
	
	public boolean getLoggedIn(){
		return isLoggedIn;
	}
	public boolean getFriendly(){
		return friendlyGame;
	}
	
	public void setLoggedIn(boolean input){
		isLoggedIn=input;
	}
	public void signInFail(){
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 6;
		errMsg.setText("Username already exist.");
		ProjectFonts.setMainFont(errMsg, 20);
		errMsg.setForeground(Color.white);
		loggedOutPanel.add(errMsg, gbc);
		loginPanel.repaint();
		loginPanel.revalidate();
	}
	public void updateLogin(String user, boolean login, int experiP, int coinP){
		System.out.println("user:"+user);
		System.out.println("experiP"+experiP);
		if(login){
			currentUser = user;
			userExp = experiP;
			userCoin = coinP;

			usernameLabel.setText(currentUser);
			exp.setText("" + userExp);
			coins.setText("" + userCoin);
			
			usernameTF.setText("");
			passwordTF.setText("");			
			isLoggedIn = true;
			if(isLoggedIn) OGButton.setEnabled(true); // if the user has logged in, enable online game
			loginPanel.remove(loggedOutPanel);
			loginPanel.add(loggedInPanel);
			loginPanel.repaint();
			loginPanel.revalidate();
		}
		else{
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 6;
			errMsg.setText("Username and password do not match.");
			ProjectFonts.setMainFont(errMsg, 20);
			errMsg.setForeground(Color.white);
			loggedOutPanel.add(errMsg, gbc);
			loginPanel.repaint();
			loginPanel.revalidate();
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
	public void updateExpAndCoin(){
		if(ClientPanels.isServer){
			jdbc = new JDBC();
			userExp = jdbc.getExp(currentUser);
			userCoin = jdbc.getCoin(currentUser);

			usernameLabel.setText(currentUser);
			exp.setText("" + userExp);
			coins.setText("" + userCoin);
			
			usernameTF.setText("");
			passwordTF.setText("");			
			isLoggedIn = true;
			
			loginPanel.remove(loggedOutPanel);
			loginPanel.add(loggedInPanel);
			loginPanel.repaint();
			loginPanel.revalidate();
		}
		else{
			ClientPortGUI.clientSocket.checkPassword(currentUser,password);
		}
	}
	
}
