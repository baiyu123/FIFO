package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import game.Ball;
import game.Player;
import library.ImageLibrary;
import library.ProjectFonts;

public class test extends JPanel{
	/*test(){
		add(new JButton("hello"));
	}*/
	/*
	public void paintComponent(Graphics g){
		Image backgroundImage;
		String imageURL = "./resource/soccer_pitch.jpg";
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
		g.drawString("hello",5,5);
	}
	*/
	
	private static final long serialVersionUID = 1;
	private Image backgroundImage;
	private JLabel titleLabel;
	private JLabel startLabel;
	private final String imageURL = "./resource/field_background.png";
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	

	private Player demo;
	
	private final String fieldBgURL = "./resource/arsenal.png";
	private Image fieldBg;
	private JPanel field;
	private Ball ball;
	
	public test()
	{
		addGUI();
	}
	
	
	
	private void addGUI()
	{
		this.setPreferredSize(screenSize);
		
		//demo person
		demo = new Player("Casey",null,"FW",0,0,0,0,0,0);
		demo.setLocation(100, 100);
		this.add(new JLabel("SCORE"),BorderLayout.NORTH);
		
		fieldBg = ImageLibrary.getImage(fieldBgURL);
		//- 100 arbitrary number
		//will adjust later
		fieldBg = fieldBg.getScaledInstance(screenSize.width, screenSize.height-100, Image.SCALE_SMOOTH);
		field = new JPanel(){
			@Override
			public void paintComponents(Graphics g)
			{
				g.drawImage(fieldBg,0,0,this);
			}
		};
		field.setPreferredSize(new Dimension(screenSize.width,screenSize.height));
		field.add(new JLabel("TEST 2"));
		field.setVisible(true);
		this.add(field, BorderLayout.CENTER);
		
		
		this.setVisible(true);
		
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
