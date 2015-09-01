package me.becja10.BannerControl.Utils;

import org.bukkit.DyeColor;

public class Parser 
{
	public static String getColorCode(DyeColor dye)
	{
		String color = dye.name();
		ColorCode cc;
		try
		{
			cc = ColorCode.valueOf(color);
		}
		catch(IllegalArgumentException e){return "";}
		catch(NullPointerException e){return "";}
		
		return cc.getCode();
	}
	
	public static String getPatternCode(String pattern)
	{
		PatternCode pat;
		try
		{
			pat = PatternCode.valueOf(pattern);
		}
		catch(IllegalArgumentException e){return "";}
		catch(NullPointerException e){return "";}
		
		return pat.getCode();
		
	}
	
	private static boolean verifyColor(Character c)
	{
		return c.toString().matches("[a-p]");
	}
	
	private static boolean verifyPattern(Character c)
	{
		return c.toString().matches("[pKeqLHMEfsyrJlxjmnzlwCbDdFgvthGBciokAu]");
	}
	

	public static boolean verifyCode(String string) 
	{
		//this should split the input into a bunch of substring length 2. 
		String[] split = splitCode(string);

		
		//check the first split (base)
		if(!verifyColor(split[0].charAt(0)))
		{
			return false;
		}
		for(int i = 1; i < split.length; i++)
		{
			//if not every split is 2 long, something went wrong
			if(split[i].length() != 2) 
				return false;
			
			//check each layer's color and pattern
			if(!(verifyColor(split[i].charAt(0)) && verifyPattern(split[i].charAt(1))))
				return false;			
		}
		return true;
	}
	
	public static String[] splitCode(String code)
	{
		String[] ret = code.split("(?<=\\G.{2})");
		return ret;
	}

	public static String getBaseColor(String s) 
	{
		Character ret = s.charAt(0);
		return ret.toString();
	}
}
