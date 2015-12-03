package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MusicPlayer extends Thread{
	Player player;
	boolean close;
	public void run(){
		close = false;
		while(true){
			if(close)break;
			if(player == null || player.isComplete()){
					try {
			            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream("resource/sound/The_Nights.mp3"));
			            player = new Player(buffer);
			            player.play();
			        } catch (Exception e) {
			            System.out.println(e);
			        }
				}
			try {
				sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void stopMusic(){
		player.close();
		close = true;
	}

}
