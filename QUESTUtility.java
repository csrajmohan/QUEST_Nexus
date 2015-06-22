package quest;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.*;
public class QUESTUtility 
{
	static boolean clearCacheFlag = true;
	static JButton createButton(String name, int fontsize)
	{
		JButton b = new JButton(name);
	
		Font f = new Font("Times New Roman", Font.BOLD, fontsize);
		b.setFont(f);
		return b;
	}
	
	static JButton createBigButton(String name)
	{
		JButton b = new JButton(name);
		Font f = new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, QUESTConstants.HEADING_FONT_SIZE);

		return b;
	}
	static JLabel createLabel(String name, int fontsize)
	{
		JLabel l = new JLabel(name);
		Font f = new Font("Times New Roman", Font.BOLD, fontsize);//16);
		l.setFont(f);
		return(l);
	}
	static JLabel createInfoLable(String name)
	{
		JLabel l = new JLabel(name);
		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE);

		l.setFont(f);
		return(l);
	}
	static JLabel createHeadingLabel(String name)
	{
		JLabel l = new JLabel(name);
		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.MEDIUM_FONT_SIZE);		//For demo
		
		l.setFont(f);
		return(l);
	}
	static JLabel createContourLabel(String name)
	{
		JLabel l = new JLabel(name);
		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.MEDIUM_FONT_SIZE);		//For demo
		
		l.setFont(f);
		return(l);
	}
	static JLabel createBigHeadingLabel(String name)
	{
		JLabel l = new JLabel(name);
		Font f = new Font(QUESTConstants.TEXT_FONT, Font.BOLD, 24);			//For demo
		l.setFont(f);
		return(l);
	}
	static JRadioButton createRadioButton(String name, int color)
	{
		JRadioButton r = new JRadioButton(name);
		r.setForeground(new Color(color));
		Font f = new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, QUESTConstants.LARGE_FONT_SIZE);		//For demo
		r.setFont(f);
		return(r);
	}
	public static boolean clearCache() 
	{
		boolean success = false;
		if(clearCacheFlag)
		{
			String[] cmd = {
					"/bin/sh",
					"-c",
					"echo 3 | sudo tee /proc/sys/vm/drop_caches"
			};
			Process p;
			try 
			{
				Runtime r = Runtime.getRuntime();
				p = r.exec(cmd);
				p.waitFor();
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";			
				while ((line = reader.readLine())!= null) 
				{
					if(line.equals("3"))
					{
						success = true;
					}
					System.out.println(line);
				}

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return(success);
	}
}
