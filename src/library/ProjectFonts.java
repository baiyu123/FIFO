package library;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;

//static class for fonts

public class ProjectFonts {
	//need to change urls
	private static final String mainURL = "./resource/EASPORTS15.ttf";
	private static final String secondaryURL = "./resources/fonts/kenvector_future.ttf";
	
	//disable constructor
	private ProjectFonts(){}
	
	public static void setMainFont(JComponent component, int size)
	{
		try {
			//read font from file
			InputStream is = new FileInputStream(mainURL);
			Font f = Font.createFont(Font.TRUETYPE_FONT, is);
			f = f.deriveFont((float)size);
			//set font to component
			component.setFont(f);
			
		} catch (IOException io)
		{
			System.out.println("Cannot read font");
		} catch (FontFormatException ffe)
		{
			System.out.println("Cannot format font");
		}
	}
	
	public static void setSecondaryFont(JComponent component, int size)
	{
		try {
			//read font from file
			InputStream is = new FileInputStream(secondaryURL);
			Font f = Font.createFont(Font.TRUETYPE_FONT, is);
			f = f.deriveFont((float)size);
			//set font to component
			component.setFont(f);
			
		} catch (IOException io)
		{
			System.out.println("Cannot read font");
		} catch (FontFormatException ffe)
		{
			System.out.println("Cannot format font");
		}
	}
	




}
