package client;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.print.attribute.standard.Media;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import javazoom.jl.player.Player;
import sun.audio.*;

public class ClientWindow extends JFrame{
	Player player;
	MusicPlayer musicPlayer;
	{ //Construct
		setTitle("FIFO201!");
		//set full screen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//GraphicsDevice gs = ge.getDefaultScreenDevice();
		//gs.setFullScreenWindow(this);
		//add the panel that controls all other panels
		setSize(1920,1080);
		add(new ClientPanels());
		//other default set-ups
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setResizable(false);
		//add ESC shortcut to exit game
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EXIT"); 
			this.getRootPane().getActionMap().put("EXIT", new AbstractAction(){ 
				public void actionPerformed(ActionEvent e)
		        {
		        	ClientWindow.this.dispose();
		        	//musicPlayer.stopMusic();
		        }
		});
			
		setVisible(true);
		musicPlayer = new MusicPlayer();
		musicPlayer.start();
	}
	
	
	public static void main(String args[])
	{
		new ClientWindow();
	}
}
