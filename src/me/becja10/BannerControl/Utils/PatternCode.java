package me.becja10.BannerControl.Utils;

import org.bukkit.block.banner.PatternType;

public enum PatternCode {
	gra  ("p"),
	gru  ("K"),
	bri  ("e"),
	hh   ("q"),
	hhb  ("L"),
	vh   ("H"),
	vhr  ("M"),
	ts   ("E"),
	bs   ("f"),
	ls   ("s"),
	rs   ("y"),
	ld   ("r"),
	rud  ("J"),
	lud  ("l"),
	rd   ("x"),
	cr   ("j"),
	dls  ("m"),
	drs  ("n"),
	sc   ("z"),
	cs   ("I"),
	ms   ("w"),
	tl   ("C"),
	bl   ("b"),
	tr   ("D"),
	br   ("d"),
	tt   ("F"),
	bt   ("g"),
	mr   ("v"),
	mc   ("t"),
	bts  ("h"),
	tts  ("G"),
	ss   ("B"),
	bo   ("c"),
	cbo  ("i"),
	flo  ("o"),
	cre  ("k"),
	sku  ("A"),
	moj  ("u");
	
	private final String code;
	PatternCode(String code)
	{
		this.code = code;
	}
	
	String getCode()
	{
		return code;
	}
	
	public static PatternType getTypeByCode(String code)
	{
		//loop through all colorCodes
		for(PatternCode c : values())
		{
			//System.out.println(c.getCode() + " code: " + code);
			//if the code we sent is the code for the enum, return it
			if(c.getCode().equals(code))
				return PatternType.getByIdentifier(c.name());
		}
		
		return null;
	}

}
