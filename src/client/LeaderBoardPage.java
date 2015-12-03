package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import library.ImageLibrary;
import library.ProjectFonts;
import util.Constants;

public class LeaderBoardPage extends JPanel{

	private static final long serialVersionUID = 1032380180747058474L;

	private JLabel leaderBoardLabel;
	private JButton backButton;
	
	private JTable leaderBoardTable;
	private JScrollPane leaderBoardScrollPane;
	
	private final String bgURL = "./resource/soccer_pitch.jpg";
	private Image bg;
	private final Color translucentBlack = new Color(0, 0, 0, 90);
	private final Color translucentGold = new Color(234,242,2,150);
	private JTableHeader header;
	private ActionListener al;
	private DefaultTableModel model;
	
	public LeaderBoardPage(ActionListener al)
	{
		initializeComponents();
		this.al = al;
		createGUI();
		addEvents(al);
	}
	
	private void initializeComponents()
	{
		leaderBoardLabel = new JLabel("Leader Board");
		backButton = new JButton("Back");
	
		
		Object [] names = new Object[] {"Username", "Experience Point"}; 
		model = new DefaultTableModel(names,0);
					
		leaderBoardTable = new JTable(model);

		
		
		leaderBoardScrollPane = new JScrollPane(leaderBoardTable);
	}
	
	private void createGUI()
	{
		//set font
		ProjectFonts.setMainFont(leaderBoardLabel, 30);
		ProjectFonts.setMainFont(backButton, 50);
		ProjectFonts.setMainFont(leaderBoardTable, 40);
		//header.setFont(new Font("Aerial",Font.PLAIN,10));
		
		
		leaderBoardTable.setRowHeight(100);
		leaderBoardTable.setGridColor(Color.GREEN);
		leaderBoardTable.setBackground(translucentBlack);
		leaderBoardTable.setForeground(Color.WHITE);
		header = leaderBoardTable.getTableHeader();
		header.setBackground(translucentBlack);
		header.setForeground(translucentGold);
		leaderBoardLabel.setAlignmentX(CENTER_ALIGNMENT);
		backButton.setAlignmentX(CENTER_ALIGNMENT);
		
		//top border
		leaderBoardLabel.setBorder(new EmptyBorder(60,60,60,60));
		backButton.setBorder(new EmptyBorder(60,60,60,60));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		this.setForeground(Color.WHITE);
		this.setPreferredSize(Constants.screenSize);
		this.add(leaderBoardLabel, gbc);
		gbc.gridy = 1;
		gbc.ipadx = this.getWidth()/2;
		gbc.ipady = this.getHeight()/2;
		leaderBoardScrollPane.setOpaque(false);
		this.add(leaderBoardScrollPane, gbc);
		
		gbc.gridy = 2;
		backButton.setOpaque(false);
		backButton.setContentAreaFilled(false);
		backButton.setBorderPainted(false);
		//backButton.setForeground(Color.WHITE);
		this.add(backButton, gbc);
	}
	
	private void addEvents(ActionListener al)
	{
		backButton.addActionListener(al);
	}
	
	public void paintComponent(Graphics g)
	{
		bg = ImageLibrary.getImage(bgURL);
		bg = bg.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(bg, 0, 0, this);
	}
	public void updateLeaderBoard(){
		
		if(ClientPanels.isServer){
			JDBC jdbc = new JDBC();
			Vector<String> userName = jdbc.getAllUsers();
			Vector<Integer> expPoints = jdbc.getAllScores();
			for(int i = 0; i < userName.size(); i++){
				model.addRow(new Object[]{expPoints.get(i),userName.get(i)});
			}

			jdbc.close();
		}
		else{
			Vector<String> userName = ClientPortGUI.clientSocket.getAllUsers();
			Vector<Integer> expPoints = ClientPortGUI.clientSocket.getAllScores();
			for(int i = 0; i < userName.size(); i++){
				model.addRow(new Object[]{expPoints.get(i),userName.get(i)});
			}

		}
		
	}
	
	
}
