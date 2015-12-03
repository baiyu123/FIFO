package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import game.GameSimulation;
import game.Player;
import game.Team;
import library.ImageLibrary;
import library.ProjectFonts;
import server.ServerPortGUI;
import util.Constants;

public class ChooseTeams extends JPanel{

	private static final long serialVersionUID = 1;
	private JLabel iconTeam1;
	private JLabel iconTeam2;
	private JLabel nameTeam1;
	private JLabel nameTeam2;
	private JButton left1;
	private JButton right1;
	private JButton left2;
	private JButton right2;
	private JButton submitButton;
	
	private final String bgURL = "./resource/honda.jpg";
	private Image bg;
	private final String leftURL = "./resource/left.png";
	private final String rightURL = "./resource/right.png";
	private Image leftIcon;
	private Image rightIcon;
	
	private final String teamFileURL = "./database/teams.txt";
	
	//indexes for vector
	private int choice1 = 0;
	private int choice2 = 1;
	
	private Vector<Team> teams;
	private Team chosenTeam;
	private Team chosenAITeam;
	
	private GameSimulation mGameSimulation;
	
	public ChooseTeams(ActionListener al, GameSimulation gameSimulation){
		mGameSimulation=gameSimulation;
		initializeComponents();
		readFile(teamFileURL);
		createGUI();
		addEvents(al);
	}
	
	private void initializeComponents()
	{
		iconTeam1 = new JLabel();
		iconTeam2 = new JLabel();
		nameTeam1 = new JLabel();
		nameTeam2 = new JLabel();
		left1 = new JButton("");
		right1 = new JButton("");
		left2 = new JButton("");
		right2 = new JButton("");
		submitButton = new JButton("Continue");
	}
	
	private void createGUI()
	{
		this.setPreferredSize(Constants.screenSize);
		//set font
		ProjectFonts.setMainFont(nameTeam1, 40);
		ProjectFonts.setMainFont(nameTeam2, 40);
		nameTeam1.setForeground(Color.WHITE);
		nameTeam2.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(submitButton, 40);
		
		//change button images
		leftIcon = ImageLibrary.getImage(leftURL);
		rightIcon = ImageLibrary.getImage(rightURL);
		leftIcon = leftIcon.getScaledInstance(30, 30, Image.SCALE_FAST);
		rightIcon = rightIcon.getScaledInstance(30, 30, Image.SCALE_FAST);
		left1.setIcon(new ImageIcon(leftIcon));
		right1.setIcon(new ImageIcon(rightIcon));
		left2.setIcon(new ImageIcon(leftIcon));
		right2.setIcon(new ImageIcon(rightIcon));
		//just making background invisible
		left1.setOpaque(false);
		left1.setContentAreaFilled(false);
		left1.setBorderPainted(false);
		left2.setOpaque(false);
		left2.setContentAreaFilled(false);
		left2.setBorderPainted(false);
		right1.setOpaque(false);
		right1.setContentAreaFilled(false);
		right1.setBorderPainted(false);
		right2.setOpaque(false);
		right2.setContentAreaFilled(false);
		right2.setBorderPainted(false);
		submitButton.setOpaque(false);
		submitButton.setContentAreaFilled(false);
		submitButton.setBorderPainted(false);
		
		submitButton.setForeground(Color.WHITE);
		
		//default teams
		Team defaultTeam1 = teams.elementAt(choice1);
		chosenTeam = defaultTeam1;
		Team defaultTeam2 = teams.elementAt(choice2);
		chosenAITeam=defaultTeam2;
		iconTeam1.setIcon(defaultTeam1.getIcon());
		nameTeam1.setText(defaultTeam1.getName());
		iconTeam2.setIcon(defaultTeam2.getIcon());
		nameTeam2.setText(defaultTeam2.getName());
		
		//setting layout and adding components
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 3;
		this.add(iconTeam1,gbc);
		gbc.gridx = 3;
		this.add(iconTeam2,gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		this.add(left1, gbc);
		gbc.gridx = 1;
		this.add(nameTeam1,gbc);
		gbc.gridx = 2;
		this.add(right1,  gbc);
		gbc.gridx = 3;
		this.add(left2, gbc);
		gbc.gridx = 4;
		this.add(nameTeam2,gbc);
		gbc.gridx = 5;
		this.add(right2, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 6;
		this.add(submitButton,gbc);
	}
	
	private void addEvents(ActionListener al)
	{
		submitButton.addActionListener(al);
		submitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(chosenTeam==chosenAITeam) return;
				mGameSimulation.setteam1(chosenTeam);
				mGameSimulation.setteam2(chosenAITeam);
				mGameSimulation.constructAI();
			}
			
		});
		AbstractAction changeTeamR = new AbstractAction(){
			
			private static final long serialVersionUID = 1;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice1++;
				if(choice1 == choice2){
					choice1++;
				}
				if(choice1 == teams.size()){
					choice1 = 0;
				}
				//send team info to server or client
				if(ClientPanels.onlineGame){
					if(ClientPanels.isServer){
						ServerPortGUI.gameServer.updateTeamSelection(choice1);
					}
					else{
						ClientPortGUI.clientSocket.updateTeamSelection(choice1);
					}
				}
				iconTeam1.setIcon(teams.elementAt(choice1).getIcon());
				nameTeam1.setText(teams.elementAt(choice1).getName());
				chosenTeam=teams.elementAt(choice1);
				
				repaint();
				revalidate();
			}
		};
		AbstractAction changeTeamL = new AbstractAction(){
			private static final long serialVersionUID = 1;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice1--;
				if(choice1 == choice2){
					choice1--;
				}
				if(choice1 == -1){
					choice1 = teams.size()-1;
				}
				
				//send team info to server or client
				if(ClientPanels.onlineGame){
					if(ClientPanels.isServer){
						ServerPortGUI.gameServer.updateTeamSelection(choice1);
					}
					else{
						ClientPortGUI.clientSocket.updateTeamSelection(choice1);
					}
				}
				iconTeam1.setIcon(teams.elementAt(choice1).getIcon());
				nameTeam1.setText(teams.elementAt(choice1).getName());
				chosenTeam=teams.elementAt(choice1);
				
				repaint();
				revalidate();
			}
		};
		right1.addActionListener(changeTeamR);
		left1.addActionListener(changeTeamL);
		right1.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,0), "D_pressed");
		left1.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,0), "A_pressed");
		right1.getActionMap().put("D_pressed", changeTeamR);
		left1.getActionMap().put("A_pressed", changeTeamL);
		
	}
	
	private void readFile(String fileName)
	{
		
		//read text file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			addTeams(br);
		} catch (FileNotFoundException fnfe)
		{
			System.out.println("Cannot read team file");
		} catch (IOException ioe)
		{
			System.out.println("Cannot read team file content");
		}
		finally
		{
			try{
				if (br != null)
				{
					br.close();
				}
			} catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	private void addTeams(BufferedReader br) throws IOException
	{
		teams = new Vector<Team>();
		ArrayList<String> positions = new ArrayList<String>();
		positions.add(Constants.forward);
		positions.add(Constants.midfield);
		positions.add(Constants.defender);
		positions.add(Constants.goalkeeper);
		String line = br.readLine();
		
		while (line != null)
		{
			if (line.startsWith("-"))
			{
				//team name
				String teamName = line.substring(1, line.length());
				Team newTeam = new Team(teamName);
				teams.add(newTeam);
			} else if (line.equals("") || line.startsWith("//"))
			{
				//do nothing
				//have space for aesthetic reasons
				// "//" is for comments
			} else
			{
				//player
				//will always belong to the last team in the vector
				Team team = teams.lastElement();
				String playerName = "";
				String position = "";
				int statsIndex = 0;

				String[] data = line.split(" ");
				for (int i=0; i<data.length; i++)
				{
					for (int j=0; j<(Constants.positions).length; j++)
					{
						//keep on reading words until we meet the position
						//assuming that no player's names are FW, MF, DF, or GK
						if (Constants.positions[j].equals(data[i]))
						{
							position = data[i];
							//last index of player's name will be unnecessary space
							playerName = playerName.substring(0,playerName.length()-1);
							statsIndex = i+1;
							break;
						}
					}
					if (statsIndex != 0)
						break;
					playerName += data[i] + " ";
				}
				
				Player newPlayer = null;
				int defending = 0;
				int passing = 0;
				int dribbling = 0;
				int shooting = 0;
				int speed = 0;
				int strength = 0;
				if (position.equals(Constants.goalkeeper))
				{
					try
					{
						defending = Integer.parseInt(data[statsIndex]);
						speed = Integer.parseInt(data[statsIndex+1]);
						passing = Integer.parseInt(data[statsIndex+2]);
					} catch (NumberFormatException nfe)
					{
						System.out.println("Number format incorrect when reading in team info");
					} catch (ArrayIndexOutOfBoundsException aioobe)
					{
						System.out.println("Array out of bounds when reading in team info");
					}
					
				} else
				{
					try
					{
						//now read in stats
						defending = Integer.parseInt(data[statsIndex]);
						passing = Integer.parseInt(data[statsIndex+1]);
						dribbling = Integer.parseInt(data[statsIndex+2]);
						shooting = Integer.parseInt(data[statsIndex+3]);
						speed = Integer.parseInt(data[statsIndex+4]);
						strength = Integer.parseInt(data[statsIndex+5]);
					} catch (NumberFormatException nfe)
					{
						System.out.println("Number format incorrect when reading in team info");
					} catch (ArrayIndexOutOfBoundsException aioobe)
					{
						System.out.println("Array out of bounds when reading in team info");
					}
				}
				
				newPlayer = new Player(playerName, team, position, defending, passing, dribbling, shooting, speed, strength);
				
				team.addPlayer(newPlayer);
			}
			line = br.readLine();
		}
	}
	
	public void setTeam(int newChoice2){
		choice2 = newChoice2;
		iconTeam2.setIcon(teams.elementAt(choice2).getIcon());
		nameTeam2.setText(teams.elementAt(choice2).getName());
		repaint();
		revalidate();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		bg = ImageLibrary.getImage(bgURL);
		bg = bg.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(bg, 0, 0, this);
	}
}
