package server;

import client.ClientPanels;
import client.FieldPanel;

public class Clock extends Thread{
	private int gameRealTimeSeconds;
	private long start;
	private String time = "0:00";
	private final int half = 20;
	private Boolean isHalfTime;
	private Boolean secondHalf = false;
	private final String halfTimeString = "HALF TIME";
	private final String finalTimeString = "FINAL TIME";
	
	private FieldPanel fieldPanel;
	private ClientPanels mClientPanels;
	
	private boolean isPaused = false;
	int counter = 0;
	
	public boolean getIsPaused(){
		return isPaused;
	}
	
	
	public Clock(int gameRealTimeMinutes, FieldPanel inFieldPanel, ClientPanels inClientPanels)
	{
		this.gameRealTimeSeconds = gameRealTimeMinutes * 60;
		fieldPanel = inFieldPanel;
		mClientPanels = inClientPanels;
		isHalfTime = false;
		start = System.currentTimeMillis();
	}
	
	public String getTime()
	{
		return time;
	}
	
	public void setPause(boolean pause){
		isPaused = pause;
	}
	
	public boolean end(){
		return isHalfTime;
	}
	
	private void calculateTime()
	{
		double lapsedSeconds = (System.currentTimeMillis() - start ) / 1000.0;
		double display = half * (lapsedSeconds / (gameRealTimeSeconds / 2) );
		if (half <= (int)display)
		{
			isHalfTime = true;
			if (!secondHalf)
			{
				time = halfTimeString;
				secondHalf = true;
			}
			else
				time = finalTimeString;
			return;
		}
		
		long integer = (long)display;
		double decimal = display - integer;
		//minutes and seconds passed
		int seconds = (int)(60 * decimal);
		int minutes = (int)integer;
		String secondsString = "";
		String minutesString = "";
		if (seconds < 10)
		{
			secondsString = "0";
		}
		if (minutes < 10)
		{
			minutesString = "0";
		}
		time = minutesString + minutes + ":" + secondsString + seconds;
		
	}
	
	public void endHalfTime()
	{
		start = System.currentTimeMillis();
		time = "0:00";
		isHalfTime = false;
		counter++;
	}
	
	public void startSecondHalf(){
		fieldPanel.reset();
		start = System.currentTimeMillis();
		time = "0:00";
	}
	
	public void run()
	{
		while(true){
			if (isPaused == false)
			{
				try
				{
					Thread.sleep(500);
					if (!isHalfTime)
					{
						calculateTime();
						fieldPanel.updateClock(time);
					}
					else if(isHalfTime && counter == 0)
					{
						Thread.sleep(100);
						fieldPanel.reset();
						this.endHalfTime();
						fieldPanel.setHalfTime();
						//mClientPanels.switchToResults();
					}
					else{
						Thread.sleep(500);
						mClientPanels.switchToResults();
						break;
					}
				} catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
			}
			else{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
