package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import game.GamePanelCoords;
import game.GameSimulation;
import game.Player;
import game.Team;
import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;
import util.Pair;

public class ChooseFormation extends JPanel{

	private static final long serialVersionUID = 1;

	private final int playerCt = 5;
	private Image backgroundImage;
	private final String imageURL = "./resource/honda.jpg";
	
	private JLabel title;
	
	private JComboBox<String> formationList;
	
	private JPanel formationBox;
	private JPanel offenseBox;
	private JPanel midfieldBox;
	private JPanel defenseBox;
	private JPanel keeperBox;
	private FormationLabel [] squad;
	
	private JPanel playerStats;
	private JLabel physicalLabel;
	private JLabel physical;
	private JLabel offenseLabel;
	private JLabel offense;
	private JLabel defenseLabel;
	private JLabel defense;
	
	private JLabel messageLabel;
	
	private JPanel playerGrid;
	
	private JButton continueButton;
	
	private ButtonGroup bg;
	
	private final String[] formationOptions={"2-0-2","2-1-1","1-2-1"};
	private Vector<Player> players;
	private Vector<PlayerLabel> playerLabels;
	private final Color translucentBlack = new Color(0, 0, 0, 90);
	private final Color translucentGreen = new Color(0, 255, 0, 90);
	
	private CalculatePlayerStats calculate;
	
	private GameSimulation mGameSimulation;
	private Team team;

	private GamePanelCoords gamePanelCoords = null;
	
	public ChooseFormation(ClientPanels cp, GameSimulation gameSimulation)
	{
		mGameSimulation=gameSimulation;
		initializeComponents();
		createGUI();
		addEvents(cp);
	}
	
	private void initializeComponents()
	{
		title= new JLabel("Formation Setting");
		
		playerLabels = new Vector<PlayerLabel>();
		
		formationBox = new JPanel();
		offenseBox = new JPanel();
		midfieldBox = new JPanel();
		defenseBox = new JPanel();
		keeperBox = new JPanel();
		
		playerGrid = new JPanel();
		playerStats = new JPanel();
		physical = new JLabel("No Player Selected");
		offense = new JLabel("No Player Selected");
		defense = new JLabel("No Player Selected");
		physicalLabel = new JLabel("Physical: ");
		offenseLabel = new JLabel("Offense: ");
		defenseLabel = new JLabel("Defense: ");
		
		messageLabel = new JLabel("Hover over a player to see his stats");
		//messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		formationList= new JComboBox<String>(formationOptions);
		
		continueButton = new JButton("Continue");
		squad= new FormationLabel[playerCt];
		bg= new ButtonGroup();
		//need to read from the database in the future;
		for(int i=0;i<playerCt;i++){
			squad[i] = new FormationLabel();
			bg.add(squad[i]);
		}
		
		//not components
		calculate = new CalculatePlayerStats();
		
	}
	
	private void createGUI()
	{
		//set fonts
		ProjectFonts.setMainFont(title, 50);
		ProjectFonts.setMainFont(physical, 20);
		ProjectFonts.setMainFont(offense, 20);
		ProjectFonts.setMainFont(defense, 20);
		ProjectFonts.setMainFont(continueButton, 30);
		ProjectFonts.setMainFont(formationList, 30);
		ProjectFonts.setMainFont(physicalLabel, 20);
		ProjectFonts.setMainFont(offenseLabel, 20);
		ProjectFonts.setMainFont(defenseLabel, 20);
		ProjectFonts.setMainFont(messageLabel, 20);
		
		//set color font
		physical.setForeground(Color.WHITE);
		physicalLabel.setForeground(Color.WHITE);
		offense.setForeground(Color.WHITE);
		offenseLabel.setForeground(Color.WHITE);
		defense.setForeground(Color.WHITE);
		defenseLabel.setForeground(Color.WHITE);
		defense.setForeground(Color.WHITE);
		messageLabel.setForeground(Color.WHITE);
	
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		//padding for every inner panel
		gbc.insets = new Insets(5,5,5,5);
		this.setPreferredSize(Constants.screenSize);
		
		title.setOpaque(false);
		title.setForeground(Color.lightGray);
		gbc.gridwidth = 3;
		this.add(title,gbc);
		
		//adding the drop-down box for choosing formation
		formationList.setOpaque(true);
		formationList.setBackground(Color.white);
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		this.add(formationList,gbc);
		
		//formation display
		//we want to expand each row to full width so use gridbaglayout
		formationBox.setLayout(new GridBagLayout());
		//we use 'gbc' already so use 'gc'
		GridBagConstraints gc = new GridBagConstraints();
		formationBox.add(offenseBox,gc);
		gc.gridy = 1;
		formationBox.add(midfieldBox,gc);
		gc.gridy = 2;
		formationBox.add(defenseBox,gc);
		gc.gridy = 3;
		formationBox.add(keeperBox,gc);
		offenseBox.setLayout(new FlowLayout(FlowLayout.CENTER));
		midfieldBox.setLayout(new FlowLayout(FlowLayout.CENTER));
		defenseBox.setLayout(new FlowLayout(FlowLayout.CENTER));
		keeperBox.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		String defaultFormation = formationOptions[0];
		setFormation(defaultFormation);
		
		midfieldBox.setBorder(new EmptyBorder(50,0,0,0));
		defenseBox.setBorder(new EmptyBorder(50,0,0,0));
		keeperBox.setBorder(new EmptyBorder(50,0,0,0));
		
		formationBox.setBackground(translucentGreen);
		formationBox.setForeground(Color.WHITE);
		offenseBox.setOpaque(false);
		midfieldBox.setOpaque(false);
		defenseBox.setOpaque(false);
		keeperBox.setOpaque(false);
		
		gbc.gridx = 1;
		this.add(formationBox, gbc);
		
		//player stats panel		
		playerStats.setLayout(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		playerStats.add(physicalLabel, gbc1);
		gbc1.gridx = 1;
		playerStats.add(physical, gbc1);
		gbc1.gridx = 0;
		gbc1.gridy = 1;
		playerStats.add(offenseLabel, gbc1);
		gbc1.gridx = 1;
		playerStats.add(offense, gbc1);
		gbc1.gridx = 0;
		gbc1.gridy = 2;
		playerStats.add(defenseLabel, gbc1);
		gbc1.gridx = 1;
		playerStats.add(defense, gbc1);

		gbc1.gridx = 0;
		gbc1.gridwidth = 2;
		gbc1.gridy = 4;
		//gbc1.fill = GridBagConstraints.BOTH;
		gbc1.insets = new Insets(20, 0, 0, 0);
		playerStats.add(messageLabel, gbc1);
		
		playerStats.setBackground(translucentBlack);
		playerStats.setForeground(Color.WHITE);
		
		gbc.gridx = 2;
		this.add(playerStats, gbc);
		playerGrid.setBackground(translucentBlack);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		this.add(playerGrid, gbc);
		
		continueButton.setPreferredSize(new Dimension(250,250));
		continueButton.setOpaque(false);
		continueButton.setContentAreaFilled(false);
		continueButton.setVerticalTextPosition(SwingConstants.CENTER);
		continueButton.setHorizontalTextPosition(SwingConstants.CENTER);
		continueButton.setBorderPainted(false);
		continueButton.setForeground(Color.white);
		gbc.gridy = 3;
		this.add(continueButton,gbc);
		
	}
	
	private void addEvents(ClientPanels cp)
	{
		continueButton.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//check if all positions are filled up
				Boolean filledUp = true;
				for (FormationLabel fl : squad)
				{
					if (fl.getPlayer() == null)
					{
						filledUp = false;
						break;
					}
				}
				
				if (!filledUp)
				{
					//return;
					
					//THIS IS ONLY FOR TESTING!! WILL CHANGE IT BACK!!
					mGameSimulation.setTeamFormation();
				}
				else{
					//debug
					//make sure gamepanelcoords is passed in
					GamePanelCoords gpc = ChooseFormation.this.gamePanelCoords;
					if (gpc == null)
					{
						System.out.println("game panel coords not passed in");
						return;
					}
					
					// now set the starting players
					int fwCt = 0;
					int mfCt = 0;
					int dfCt = 0;
					String formation = (String)(( ChooseFormation.this.formationList).getSelectedItem() );
					String[] formationArray = formation.split("-");
					int totalFwCt = Integer.parseInt(formationArray[2]);
					int totalMfCt = Integer.parseInt(formationArray[1]);
					int totalDfCt = Integer.parseInt(formationArray[0]);
					
					Vector<Player> start= new Vector<Player>();
					for(FormationLabel f:squad){
						Player p=f.getPlayer();
						//set to starting vector
						start.add(p);
						
						//set base position
						//left to right
						if (p.getPosition().equals(Constants.forward))
						{
							double baseX = gpc.getLeftBound() + (gpc.getWidth() * 5 / 6);
							double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)fwCt*2+1) / ((double)totalFwCt*2) );
							p.setBaseLocation(new Pair((int)baseX, (int)baseY));
							p.setLocation(new Pair((int)baseX, (int)baseY));
							if(fwCt==0){
								p.setIsMainPlayer(true);//set mainPlayer
								p.setHasBall(true);
							}
							fwCt++;
						} else if (p.getPosition().equals(Constants.midfield))
						{
							double baseX = gpc.getLeftBound() + (gpc.getWidth() * 3 / 6);
							double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)mfCt*2+1) / ((double)totalMfCt*2) );
							p.setBaseLocation(new Pair((int)baseX, (int)baseY));
							p.setLocation(new Pair((int)baseX, (int)baseY));
							mfCt++;
						}else if (p.getPosition().equals(Constants.defender))
						{
							double baseX = gpc.getLeftBound() + (gpc.getWidth() * 1 / 6);
							double baseY = gpc.getUpperBound() + gpc.getHeight() * ( ((double)dfCt*2+1) / ((double)totalDfCt*2) );
							p.setBaseLocation(new Pair((int)baseX, (int)baseY));
							p.setLocation(new Pair((int)baseX, (int)baseY));
							dfCt++;
						}else if (p.getPosition().equals(Constants.goalkeeper))
						{
							double baseX = gpc.getLeftBound();
							double baseY = gpc.getUpperBound() + gpc.getHeight()/2;
							p.setBaseLocation(new Pair((int)baseX, (int)baseY));
							p.setLocation(new Pair((int)baseX, (int)baseY));
						} else
						{
							System.out.println("Position not set for formation choosing");
							return;
						}
					}
					
					team.setStarting(start);
					
				}
				//testing
				mGameSimulation.setAIFormation();
				
				mGameSimulation.prepareForKickOff(mGameSimulation.getTeam1());
				
				cp.switchToField();
			}
			
		});
		formationList.addActionListener(
			
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					String formation = (String)formationList.getSelectedItem();
					setFormation(formation);
				}
			}
		);
		
		
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
	
	private void setFormation(String formation)
	{
		//turn string '2-0-2' into array of ints int{2,0,2}
		String[] split = formation.split("-");
		int arraySize = split.length;
		int[] formationArray = new int[arraySize];
		for(int i=0; i<arraySize; i++)
		{
			int stringToInt = Integer.parseInt(split[i]);
			formationArray[i] = stringToInt;
		}
		
		//divided into offense, midfield, and defense
		//add radiobuttons for each jpanel
		for(int i=0; i<formationArray[0]; i++)
		{
			defenseBox.add(squad[i]);
			squad[i].setPosition(Constants.defender);
		}
		
		for (int i=formationArray[0]; i< (formationArray[0] + formationArray[1]) ; i++ )
		{
			midfieldBox.add(squad[i]);
			squad[i].setPosition(Constants.midfield);
		}
		
		for (int i= (formationArray[0] + formationArray[1]) ; i < (playerCt - 1); i++)
		{
			offenseBox.add(squad[i]);
			squad[i].setPosition(Constants.forward);
		}
		
		//last position is always keeper
		keeperBox.add(squad[4]);
		squad[4].setPosition(Constants.goalkeeper);
	}
	
	public void passInGamePanelCoords(GamePanelCoords gpc)
	{
		this.gamePanelCoords  = gpc;
	}
	
	//called after player has chosen a team
	public void updateTeam()
	{
		playerGrid.removeAll();
		team=mGameSimulation.getTeam1();
		players = team.getSubs();
		
		//creating a big enough grid to fit all players
		Vector<Player> forwards = new Vector<Player>();
		Vector<Player> midfields = new Vector<Player>();
		Vector<Player> defenders = new Vector<Player>();
		Vector<Player> goalkeepers = new Vector<Player>();
		
		for(Player sub : players)
		{
			switch(sub.getPosition())
			{
				case Constants.forward:
					forwards.add(sub); break;
				case Constants.midfield:
					midfields.add(sub); break;
				case Constants.defender:
					defenders.add(sub); break;
				case Constants.goalkeeper:
					goalkeepers.add(sub); break;
				
			}
		}
		
		int max = forwards.size();
		if (max < midfields.size())
		{
			max = midfields.size();
		}
		if (max < defenders.size())
		{
			max = defenders.size();
		}
		if (max < goalkeepers.size())
		{
			max = goalkeepers.size();
		}
		
		//need one row for labels
		int totalRows = max + 1;
		playerGrid.setLayout(new GridLayout(totalRows, Constants.positions.length));
		JLabel fwLabel = new JLabel(Constants.forward);
		JLabel mfLabel = new JLabel(Constants.midfield);
		JLabel dfLabel = new JLabel(Constants.defender);
		JLabel gkLabel = new JLabel(Constants.goalkeeper);
		fwLabel.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(fwLabel, 20);
		mfLabel.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(mfLabel, 20);
		dfLabel.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(dfLabel, 20);
		gkLabel.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(gkLabel, 20);
		
		
		playerGrid.add(fwLabel);
		playerGrid.add(mfLabel);
		playerGrid.add(dfLabel);
		playerGrid.add(gkLabel);
		
		for (int i=0; i<max; i++)
		{
			if (i < forwards.size())
			{
				PlayerLabel nameLabel = new PlayerLabel(forwards.get(i));
				playerLabels.add(nameLabel);
				playerGrid.add(nameLabel);
			} else
			{
				playerGrid.add(new JLabel());
			}
			
			if (i < midfields.size())
			{
				PlayerLabel nameLabel = new PlayerLabel(midfields.get(i));
				playerLabels.add(nameLabel);
				playerGrid.add(nameLabel);
			} else
			{
				playerGrid.add(new JLabel());
			}
			
			if (i < defenders.size())
			{
				PlayerLabel nameLabel = new PlayerLabel(defenders.get(i));
				playerLabels.add(nameLabel);
				playerGrid.add(nameLabel);
			} else
			{
				playerGrid.add(new JLabel());
			}
			
			if (i < goalkeepers.size())
			{
				PlayerLabel nameLabel = new PlayerLabel(goalkeepers.get(i));
				playerLabels.add(nameLabel);
				playerGrid.add(nameLabel);
			} else
			{
				playerGrid.add(new JLabel());
			}
		}
	}
	
	private class FormationLabel extends JRadioButton
	{
		private static final long serialVersionUID = 1L;
		private Player p = null;
		private String position = null;
		
		public FormationLabel()
		{
			this.setForeground(Color.WHITE);
			this.setOpaque(false);
			ProjectFonts.setMainFont(this, 30);
		}
		
		public void setPosition(String pos)
		{
			String [] positions = Constants.positions;
			Boolean match = false;
			for(String thisPos : positions)
			{
				if (thisPos.equals(pos))
					match = true;
			}
			
			//not a position
			if (!match)
			{
				return;
			}
			
			//overwrite existing player
			if (p != null)
			{
				//first enable the player label associated with it
				for (PlayerLabel pl : playerLabels)
				{
					if (pl.getPlayer().equals(p))
					{
						pl.setEnabled(true);
						break;
					}
				}
				//now remove
				this.removePlayer();
			}
			
			this.position = pos;
			this.setText(pos);
			
			return;
			
		}
		
		public void setPlayer(Player p)
		{
			this.p = p;
			this.setText(this.p.getName());
			
		}
		
		public Player getPlayer()
		{
			return this.p;
		}
		
		public String getPosition()
		{
			return this.position;
		}
		
		public void removePlayer()
		{
			this.p = null;
		}
	}
	
	private class PlayerLabel extends JLabel
	{
		private static final long serialVersionUID = 1L;
		private Player p;
		public PlayerLabel(Player p)
		{
			this.p = p;
			this.setText(p.getName());
			this.setOpaque(false);
			this.setForeground(Color.WHITE);
			ProjectFonts.setMainFont(this, 20);
			this.setBorder(new EmptyBorder(0,20,0,0));
			addAction();
		}
		
		public Player getPlayer()
		{
			return this.p;
		}
		
		private void addAction()
		{
			//have not tested yet
			this.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public void mouseEntered(MouseEvent me)
					{
						
						physical.setText("" + calculate.physical(p));
						//physical.setHorizontalAlignment(SwingConstants.CENTER);
						offense.setText("" + calculate.offense(p));
						//offense.setHorizontalAlignment(SwingConstants.CENTER);
						defense.setText("" + calculate.defense(p));
						//defense.setHorizontalAlignment(SwingConstants.CENTER);
						
						if (PlayerLabel.this.isEnabled())
						{

						}
					}
					
					@Override
					public void mouseExited(MouseEvent me)
					{
						physical.setText("No Player Selected");
						offense.setText("No Player Selected");
						defense.setText("No Player Selected");
						
						
						
						if (PlayerLabel.this.isEnabled())
						{
							
						}
					}
					
					@Override
					public void mouseClicked(MouseEvent me)
					{
						if (PlayerLabel.this.isEnabled())
						{
							//buttongroup already makes sure only one can be selected at a time
							FormationLabel set = null;
							for (FormationLabel fl : squad)
							{
								if (fl.isSelected())
								{
									set = fl;
									break;
								}
							}
							
							if (set != null)
							{
								//a FW position must be filled by a FW player
								String setPosition = set.getPosition();
								String thisPosition = PlayerLabel.this.getPlayer().getPosition();
								
								if (!setPosition.equals(thisPosition))
								{
									return;
								}
								
								
								PlayerLabel.this.setEnabled(false);
								
								//if a player was already set
								//we should enable that player
								Player setPlayer = set.getPlayer();
								if(setPlayer != null)
								{
									//disable that player's choice
									for (PlayerLabel pl : playerLabels)
									{
										if (pl.p.equals(setPlayer))
										{
											pl.setEnabled(true);
											break;
										}
									}
								}
								
								set.setPlayer(PlayerLabel.this.p);
							}
						} //end if enabled
					}
				}
					
			);
		}
	}
	
	private class CalculatePlayerStats
	{
		public CalculatePlayerStats()
		{

		}
		
		public int offense(Player p)
		{
			int total = 0;
			total += p.getShooting();
			total += p.getPassing();
			total += p.getDribbling();
			total /= 3;
			return total;
		}
		
		public int defense(Player p)
		{
			return p.getDefending();
		}
		
		public int physical(Player p)
		{
			int total = 0;
			total += p.getSpeed();
			total += p.getStrength();
			total /= 2;
			return total;
		}
	}
	
}
