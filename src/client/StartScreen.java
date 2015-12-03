package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class StartScreen extends JPanel{

	private static final long serialVersionUID = 1;
	private Image backgroundImage;
	private JLabel titleLabel;
	private JLabel startLabel;
	private final String imageURL = "./resource/kagawa.jpg";
	
	public StartScreen(MouseListener ml)
	{
		initializeComponents();
		addGUI();
		addEvents(ml);
	}
	
	private void initializeComponents()
	{
		titleLabel = new JLabel("FIFO201");
		startLabel = new JLabel("START GAME");
	}
	
	private void addGUI()
	{
		this.setLayout(new BorderLayout());
		//expand to full screen
		this.setPreferredSize(Constants.screenSize);
		//set font
		ProjectFonts.setMainFont(titleLabel, 150);
		ProjectFonts.setMainFont(startLabel, 60);
		//set font color
		titleLabel.setForeground(new Color(196,18,30));
		//align text
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		startLabel.setBorder(new EmptyBorder(0,0,100,0));
		startLabel.setHorizontalAlignment(JLabel.CENTER);
		//make start label have finger cursor
		startLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		//jlabel will expand full width of screen
		//so wrap around gridbaglayout
		JPanel wrapper = new JPanel();
		wrapper.setOpaque(false);
		wrapper.setLayout(new GridBagLayout());
		wrapper.add(startLabel);
		//add labels
		this.add(titleLabel,BorderLayout.CENTER);
		this.add(wrapper,BorderLayout.SOUTH);
	}
	
	private void addEvents(MouseListener ml)
	{
		startLabel.addMouseListener(ml);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
