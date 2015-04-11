package me.becja10.BannerControl.commands;

import java.util.ArrayList;
import java.util.List;

import me.becja10.BannerControl.Utils.BannerManager;
import me.becja10.BannerControl.Utils.Parser;
import me.becja10.BannerControl.Utils.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class AssignBannerCommand 
{

	public static boolean handleCommand(CommandSender sender, String[] args)
	{
		boolean isPlayer = false;
		if(sender instanceof Player)
			isPlayer = true;
		
		//if they are a player, see if they have permission
		if(isPlayer && !sender.hasPermission("bannercontrol.assign"))
			sender.sendMessage(ChatColor.DARK_RED+"You do not have access to this command.");
		else
		{
			//must provide target player
			if(args.length < 1) return false;
						
			//get target
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null)
			{
				sender.sendMessage(ChatColor.DARK_RED+"Player not found.");
				return true;
			}
				
				
			//the banner code that will be assigned to the player
			String code = "";
			String lore = "";
			
			switch (args.length)
			{
			//only one arg means just target players name, use flag in hand
			case 1:
				if(isPlayer)
				{
					ItemStack inHand = ((Player)sender).getItemInHand();
					if(inHand.getType() != Material.BANNER)
					{
						sender.sendMessage(ChatColor.DARK_RED+"This is not a banner!");
						return true;
					}
					
					//probably don't need this check, but better safe than sorry
					if(inHand.getItemMeta() instanceof BannerMeta)
					{
						BannerMeta meta = (BannerMeta)inHand.getItemMeta();
						
						//get the Base color and list of patterns
						DyeColor base = meta.getBaseColor();
						List<Pattern> patterns = meta.getPatterns();
						
						//now find the code from the pattern and base
						code = getCode(base, patterns);
					}
				}
				else
					sender.sendMessage(ChatColor.RED+"This can only be run by a player.");
				break;
				
			//2 args means they sent a banner code
			case 2:
				code = (Parser.verifyCode(args[1])) ? args[1] : code;
				break;
				
			//if there are more than 2 arguments, then they sent lore to be saved as well. create a lore string.
			default:
				code = (Parser.verifyCode(args[1])) ? args[1] : code;
				for(int i = 2; i < args.length; i++)
					lore += args[i] + " ";
			}
			
			if(code == "")
				sender.sendMessage(ChatColor.DARK_RED+"Something went wrong, is the code correct?");
			
			//a proper banner code was entered, check to see if it exists in the database, then store it to the player.
			else
			{
				//check if that banner has been assigned
				if(BannerManager.getBanners().contains(code))
				{
					//let people know that the banner has already been used.
					String err = "[BannerControl] The requested code " + code + " has already been taken by " + 
							BannerManager.getBanners().getString(code + ".name");
					System.out.println(err);
					sender.sendMessage(ChatColor.RED + err);
					target.sendMessage(ChatColor.RED + err + ". Please contact staff");
					return true;
				}
				
				//not assigned, so store it to player
				String id = target.getUniqueId().toString();
				
				//check to see if this player is already in database, and get their previous banners, otherwise create a new entry
				List<String> ownedBanners;
				if(PlayerManager.getPlayers().contains(id))
				{
					ownedBanners = PlayerManager.getPlayers().getStringList(id+".banners");
				}
				else
				{
					ownedBanners = new ArrayList<String>();
				}
				
				//add the new code to the list
				ownedBanners.add(code);
				PlayerManager.getPlayers().set(id+".name", target.getName());
				PlayerManager.getPlayers().set(id+".banners", ownedBanners);
				
				BannerManager.getBanners().set(code+".name", target.getName());
				BannerManager.getBanners().set(code+".id", id);
				BannerManager.getBanners().set(code+".lore", lore);
				
				BannerManager.saveBanners();
				PlayerManager.savePlayers();
				
				sender.sendMessage(ChatColor.GREEN + "Banner assigned");
			}
		}//end permission or console else
		return true;
	}
	
	/*
	 * This takes a base and a list of patterns and generates a code from them.
	 */
	private static String getCode(DyeColor base, List<Pattern> patterns) 
	{
		String code = "";
		
		//code will be like this <baseColor>a<paternColor><pattern>....
		
		code += (Parser.getColorCode(base) != "") ? Parser.getColorCode(base)+"a" : "" ; //add base color
		
		for(Pattern pattern : patterns)
		{
			//add pattern colors and patterns
			code += Parser.getColorCode(pattern.getColor()) + Parser.getPatternCode(pattern.getPattern().getIdentifier());
		}
		
		return code;
	}
	
}
