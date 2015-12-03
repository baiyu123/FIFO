package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import library.ImageLibrary;
import library.ProjectFonts;
import server.GameServer;
import util.Constants;

public class ClientPortGUI extends JPanel{
	public static final long serialVersionUID = 1;
	private JTextField portTextField, hostnameTextField;
	private JLabel descriptionLabel, portLabel, hostnameLabel, errorLabel;
	private JButton connectButton;
	private Image backgroundImage;
	private final String imageURL = "./resource/soccer_pitch.jpg";
	private final Color translucentBlack = new Color(0, 0, 0, 90);
	private ClientPanels clientPane;
	public static ClientSocket clientSocket;
	public static boolean isServer = false;
	
	public ClientPortGUI(ClientPanels clientPane) {
		this.clientPane = clientPane;
		initializeVariables();
		createGUI();
		addActionAdapters();
		setVisible(true);
	}
	
	private void initializeVariables() {
		descriptionLabel = new JLabel("Enter a Host and a port");
		descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);
		ProjectFonts.setMainFont(descriptionLabel, 40);
		
		portLabel = new JLabel(" Port: ");
		portLabel.setForeground(Color.WHITE);
		portLabel.setBackground(translucentBlack);
		portLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		portLabel.setOpaque(true);
		ProjectFonts.setMainFont(portLabel, 40);
		hostnameLabel = new JLabel(" Host Name: ");
		hostnameLabel.setForeground(Color.WHITE);
		hostnameLabel.setOpaque(true);
		hostnameLabel.setBackground(translucentBlack);
		hostnameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		ProjectFonts.setMainFont(hostnameLabel, 40);
		
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setOpaque(false);
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ProjectFonts.setMainFont(errorLabel, 40);
		
		portTextField = new JTextField(20);
		portTextField.setFont(new Font("Arial", Font.PLAIN, 40));
		portTextField.setText("" + 6789);
		portTextField.setBackground(translucentBlack);
		portTextField.setForeground(Color.WHITE);
		hostnameTextField = new JTextField(20);
		hostnameTextField.setFont(new Font("Arial", Font.PLAIN, 40));
		hostnameTextField.setText("localhost");
		hostnameTextField.setBackground(translucentBlack);
		hostnameTextField.setForeground(Color.WHITE);
		
		connectButton = new JButton("Connect");
		connectButton.setPreferredSize(new Dimension(150,100));
		connectButton.setBorderPainted(false);
		connectButton.setBackground(translucentBlack);
		connectButton.setForeground(Color.WHITE);
		ProjectFonts.setMainFont(connectButton, 40);
		connectButton.setAlignmentX(CENTER_ALIGNMENT);
	
		
	}
	
	private void createGUI() {
		this.setPreferredSize(Constants.screenSize);
		setLayout(new GridLayout(5, 1));
		JPanel descriptLayout = new JPanel();
		descriptLayout.setOpaque(false);
		descriptLayout.add(descriptionLabel);
		add(descriptLayout);
		add(errorLabel);
		JPanel hostFieldPanel = new JPanel();
		hostFieldPanel.setOpaque(false);
		hostFieldPanel.setLayout(new FlowLayout());
		hostFieldPanel.add(hostnameLabel);
		hostFieldPanel.add(hostnameTextField);
		add(hostFieldPanel);
		JPanel portFieldPanel = new JPanel();
		portFieldPanel.setOpaque(false);
		portFieldPanel.setLayout(new FlowLayout());
		portFieldPanel.add(portLabel);
		portFieldPanel.add(portTextField);
		add(portFieldPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
		buttonPanel.add(connectButton);
		buttonPanel.setOpaque(false);
		add(buttonPanel);
	}
	
	private void addActionAdapters() {
		class ConnectListener implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				String portStr = portTextField.getText();
				String hostnameStr = hostnameTextField.getText();
				int portInt = -1;
				try {
					portInt = Integer.parseInt(portStr);
				} catch (Exception e) {
					errorLabel.setText("Please enter a number!");
					return;
				}
				if (portInt > 1024 && portInt < 49151) {
					// try to connect
					clientSocket = new ClientSocket(hostnameStr,portInt);
					clientSocket.insertChooseTeam(clientPane.getChooseTeamGUI());
					clientSocket.insertSimulation(clientPane.getSimulation());
					clientSocket.addMainmenu(clientPane.getMainMenu());
					clientSocket.start();
					if(clientSocket.getConnected()){
						clientPane.removeAll();
						clientPane.repaint();
						clientPane.add(clientPane.mainMenu);
						clientPane.revalidate();
					}
					else{
						errorLabel.setText("Cannot connect to this Server!");
						clientPane.repaint();
						clientPane.revalidate();
					}
				}
				else { // port value out of range
					errorLabel.setText("Port out of range");
					return;
				}
			}
		}
		connectButton.addActionListener(new ConnectListener());
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		backgroundImage = ImageLibrary.getImage(imageURL);
		backgroundImage = backgroundImage.getScaledInstance(Constants.screenSize.width, Constants.screenSize.height, Image.SCALE_SMOOTH);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
