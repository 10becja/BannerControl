package me.becja10.BannerControl;

import org.bukkit.DyeColor;

public class Parser 
{

	public static String getColorCode(DyeColor color)
	{
		switch (color)
		{
		case BLACK:
			return "a";
			
		case BLUE:
			return "e";
		
		case BROWN:
			return "d";
		
		case CYAN:
			return "g";
		
		case GRAY:
			return "i";
		
		case GREEN:
			return "c";
		
		case LIGHT_BLUE:
			return "m";
		
		case LIME:
			return "k";
		
		case MAGENTA:
			return "n";
		
		case ORANGE:
			return "o";
		
		case PINK:
			return "j";
		
		case PURPLE:
			return "f";
		
		case RED:
			return "b";
		
		case SILVER:
			return "h";
		
		case WHITE:
			return "p";
		
		case YELLOW:
			return "l";
		
		default:
			return "";
		
		}
	}
	
	public static String getPatternCode(String pattern)
	{
		switch(pattern)
		{
		case "gra": return "p";
		case "gru": return "K";
		case "bri": return "e";
		case "hh" : return "q";
		case "hhb": return "L";
		case "vh" : return "H";
		case "vhr": return "M";
		case "ts" : return "E";
		case "bs" : return "f";
		case "ls" : return "s";
		case "rs" : return "y";
		case "ld" : return "r";
		case "rud": return "J";
		case "lud": return "l";
		case "rd" : return "x";
		case "cr" : return "j";
		case "dls": return "m";
		case "drs": return "n";
		case "sc" : return "z";
		case "cs" : return "l";
		case "ms" : return "w";
		case "tl" : return "C";
		case "bl" : return "b";
		case "tr" : return "D";
		case "br" : return "d";
		case "tt" : return "F";
		case "bt" : return "g";
		case "mr" : return "v";
		case "mc" : return "t";
		case "bts": return "h";
		case "tts": return "G";
		case "ss" : return "B";
		case "bo" : return "c";
		case "cbo": return "i";
		case "flo": return "o";
		case "cre": return "k";
		case "sku": return "A";
		case "moj": return "u";
		default: return "";
		}
	}
	
	private static boolean verifyColor(Character c)
	{
		return c.toString().matches("a-p");
	}
	
	private static boolean verifyPattern(Character c)
	{
		return c.toString().matches("pKeqLHMEfsyrJlxjmnzlwCbDdFgvthGBciokAu");
	}
	

	public static boolean verifyCode(String string) 
	{
		//this should split the input into a bunch of substring length 2. 
		String[] split = string.split("(?<=\\G.{2})");
		
		//check the first split (base)
		if(!verifyColor(split[0].charAt(0)))
		{
			return false;
		}
		for(int i = 1; i < split.length; i++)
		{
			//if not every split is 2 long, something went wrong
			if(split[i].length() != 2) return false;
			
			//check each layer's color and pattern
			if(!(verifyColor(split[i].charAt(0)) && verifyPattern(split[i].charAt(1))))
				return false;			
		}
		return true;
	}
}
