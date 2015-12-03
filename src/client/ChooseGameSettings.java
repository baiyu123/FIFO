package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.GameSimulation;
import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class ChooseGameSettings extends JPanel{
	
	private static final long serialVersionUID = 1;
	
	private Image backgroundImage;
	private final String imageURL = "./resource/honda.jpg";
	
	private final String[] weatheroption={Constants.sunny,Constants.rainy,Constants.snowy};
	private final String[] fieldoption={Constants.normalGrass,Constants.muddyGrass,Constants.street};
	private final String[] timeoption={"5 mins","6 mins","7 mins"};
	private final String[] fouloption={"yes","no"};
	
	private JPanel upPanel;
	private JPanel mainPanel;
	private JPanel downPanel;
	private JButton ContinueButton;
	private JLabel title;
	private JLabel wlabel;
	private JComboBox<String> weatherlist;
	private JLabel fieldlabel;
	private JComboBox<String> fieldlist;
	private JLabel timelabel;
	private JComboBox<String> timelist;
	private JLabel foullabel;
	private JComboBox<String> foulbox;
	private boolean isClient;
	
	private GameSimulation mGameSimulation;
	
	public ChooseGameSettings(ActionListener al, GameSimulation gameSimulation)
	{
		mGameSimulation=gameSimulation;
		initializeComponents();
		createGUI();
		addEvents(al);
		isClient=mGameSimulation.isClient();
	}
	
	private void initializeComponents()
	{
		this.setLayout(new BorderLayout());
		
		mainPanel= new JPanel();
		mainPanel.setLayout(new GridLayout(4,2));
		upPanel= new JPanel();
		downPanel= new JPanel();
		title= new JLabel("Game Setting");
		wlabel= new JLabel("Weather");
		weatherlist= new JComboBox<String>(weatheroption);
		fieldlabel=new JLabel("Field");
		fieldlist= new JComboBox<String>(fieldoption);
		timelabel= new JLabel("Time");
		timelist=new JComboBox<String>(timeoption);;
		foullabel= new JLabel("Foul Allowed");
		foulbox= new JComboBox<String>(fouloption);
		
		ContinueButton= new JButton("Continue");
	}
	
	private void createGUI()
	{
		ProjectFonts.setMainFont(title, 50);
		ProjectFonts.setMainFont(wlabel, 40);
		ProjectFonts.setMainFont(fieldlabel, 40);
		ProjectFonts.setMainFont(weatherlist, 40);
		ProjectFonts.setMainFont(fieldlist, 40);
		ProjectFonts.setMainFont(timelabel, 40);
		ProjectFonts.setMainFont(timelist, 40);
		ProjectFonts.setMainFont(foullabel, 40);
		ProjectFonts.setMainFont(foulbox, 40);
		ProjectFonts.setMainFont(ContinueButton, 40);
		
		this.setPreferredSize(Constants.screenSize);
		upPanel.setOpaque(false);
		title.setOpaque(false);
		title.setForeground(Color.lightGray);
		upPanel.add(title);
		
		mainPanel.setOpaque(false);
		wlabel.setOpaque(false);
		wlabel.setForeground(Color.lightGray);
		mainPanel.add(wlabel);
		weatherlist.setOpaque(true);
		weatherlist.setBackground(Color.black);
		mainPanel.add(weatherlist);
		fieldlabel.setOpaque(false);
		fieldlabel.setForeground(Color.lightGray);
		mainPanel.add(fieldlabel);
		fieldlist.setOpaque(true);
		fieldlist.setBackground(Color.black);
		mainPanel.add(fieldlist);
		timelabel.setOpaque(false);
		timelabel.setForeground(Color.lightGray);
		mainPanel.add(timelabel);
		timelist.setOpaque(true);
		timelist.setBackground(Color.black);
		mainPanel.add(timelist);
		foullabel.setOpaque(false);
		foullabel.setForeground(Color.lightGray);
		mainPanel.add(foullabel);
		foulbox.setOpaque(true);
		foulbox.setBackground(Color.black);
		mainPanel.add(foulbox);
		
		ContinueButton.setPreferredSize(new Dimension(250,250));
		ContinueButton.setOpaque(false);
		ContinueButton.setContentAreaFilled(false);
		ContinueButton.setVerticalTextPosition(SwingConstants.CENTER);
		ContinueButton.setHorizontalTextPosition(SwingConstants.CENTER);
		ContinueButton.setBorderPainted(false);
		ContinueButton.setForeground(Color.white);
		downPanel.setOpaque(false);
		downPanel.add(ContinueButton);
		
		if(isClient){
			fieldlist.setEnabled(false);
			weatherlist.setEnabled(false);
			timelist.setEnabled(false);
			foulbox.setEnabled(false);
		}
		
		add(upPanel,BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		add(downPanel,BorderLayout.SOUTH);
		
	}
	
	private void addEvents(ActionListener al)
	{
		ContinueButton.addActionListener(al);
		ContinueButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String weather = (String) weatherlist.getSelectedItem();
				String field=(String) fieldlist.getSelectedItem();
				String time=(String) timelist.getSelectedItem();
				String foul= (String) foulbox.getSelectedItem();
				
				mGameSimulation.setUp(weather, field, time, foul);
			}
			
		});
	}
	
	
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
