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
import org.bukkit.OfflinePlayer;
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
			@SuppressWarnings("deprecation")
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
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
					ItemStack inHand = ((Player)sender).getInventory().getItemInMainHand();
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
						if(base == null)
						{
							sender.sendMessage(ChatColor.RED + "Could not fetch banner's base color. "
									+ "This happens when trying to assign a hand crafted banner. Please use "
									+ ChatColor.GREEN + "www.needcoolshoes.com/banner" + ChatColor.RED
									+ " to generate a banner code and use that.");
							return true;
						}
						List<Pattern> patterns = meta.getPatterns();
						
						//now find the code from the pattern and base
						code = Parser.getCode(base, patterns);
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
					if(target.isOnline())
						((Player) target).sendMessage(ChatColor.RED + err + ". Please contact staff");
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
}
