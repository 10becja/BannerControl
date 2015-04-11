package me.becja10.BannerControl.Utils;

import org.bukkit.DyeColor;

public enum ColorCode 
{
	BLACK ("a"),
	BLUE ("e"),
	BROWN ("d"),
	CYAN ("g"),
	GRAY ("i"),
	GREEN ("c"),
	LIGHT_BLUE ("m"),
	LIME ("k"),
	MAGENTA ("n"),
	ORANGE ("o"),
	PINK ("j"),
	PURPLE ("f"),
	RED ("b"),
	SILVER ("h"),
	WHITE ("p"),
	YELLOW ("l");
	
	private final String code;
	
	ColorCode(String code)
	{
		this.code = code;
	}
	
	String getCode()
	{
		return code;
	}
	
	public static DyeColor getColorByCode(String code)
	{
		//loop through all colorCodes
		for(ColorCode c : values())
		{
			//if the code we sent is the code for the enum, return it
			if(c.getCode().equals(code))
				return DyeColor.valueOf(c.name());
		}
		
		return null;
	}
}
