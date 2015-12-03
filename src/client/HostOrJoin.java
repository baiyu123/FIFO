package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class HostOrJoin extends JPanel{
	private JButton hostGame, joinGame;
	private Image backgroundImage;
	private final String imageURL = "./resource/soccer_pitch.jpg";

	HostOrJoin(ActionListener clientPort, ActionListener serverPort){
		initializeComponents();
		createGUI();
		addEvents(clientPort,serverPort);
	}
	private void initializeComponents(){
		hostGame = new JButton("Host Game");
		joinGame = new JButton("Join Game");
		hostGame.setOpaque(false);
		joinGame.setOpaque(false);
		hostGame.setBorderPainted(false);
		joinGame.setBorderPainted(false);
		joinGame.setForeground(Color.BLACK);
		hostGame.setForeground(Color.BLACK);
		hostGame.setContentAreaFilled(false);
		joinGame.setContentAreaFilled(false);
		ProjectFonts.setMainFont(hostGame, 40);
		hostGame.setAlignmentX(CENTER_ALIGNMENT);
		joinGame.setAlignmentX(CENTER_ALIGNMENT);
		ProjectFonts.setMainFont(joinGame, 40);
		hostGame.setPreferredSize(new Dimension(150,100));
		joinGame.setPreferredSize(new Dimension(150,100));
		
	}
	private void createGUI(){
		this.setPreferredSize(Constants.screenSize);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(Box.createGlue());
		add(hostGame);
		add(joinGame);
		add(Box.createGlue());
	}
	private void addEvents(ActionListener clientPort, ActionListener serverPort){
		joinGame.addActionListener(clientPort);
		hostGame.addActionListener(serverPort);
	}
	public void paintComponent(Graphics g){
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
