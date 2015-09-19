package me.becja10.BannerControl.commands;

import java.util.List;

import me.becja10.BannerControl.Utils.BannerManager;
import me.becja10.BannerControl.Utils.Parser;
import me.becja10.BannerControl.Utils.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class DeleteBannerCommand 
{
	public static boolean handleCommand(CommandSender sender, String[] args) 
	{
		boolean isPlayer = false;
		if(sender instanceof Player)
			isPlayer = true;
		
		if(!isPlayer)
			sender.sendMessage("This command can only be run by a player!");
		
		else if(isPlayer && !sender.hasPermission("bannercontrol.delete"))
			sender.sendMessage(ChatColor.DARK_RED+"You do not have access to this command.");
		
		else
		{
			ItemStack inHand = ((Player)sender).getItemInHand();
			if(inHand.getType() != Material.BANNER)
			{
				sender.sendMessage(ChatColor.DARK_RED+"This is not a banner!");
				return true;
			}
			
			String code = "";
			
			//probably don't need this check, but better safe than sorry
			if(inHand.getItemMeta() instanceof BannerMeta)
			{
				BannerMeta meta = (BannerMeta)inHand.getItemMeta();
				
				DyeColor base = meta.getBaseColor();
				List<Pattern> patterns = meta.getPatterns();
				
				code = Parser.getCode(base, patterns);
			}
			
			if(code == "")
				sender.sendMessage(ChatColor.DARK_RED+"Something went wrong, couldn't parse banner code");
			
			else
			{
				//if the banner hasn't been assigned, no delete 4 U
				if(!BannerManager.getBanners().contains(code))
				{
					sender.sendMessage(ChatColor.RED + "This banner has not been assigned to anyone");
					return true;
				}
				
				String bannerOwnerId = BannerManager.getBanners().getString(code+ ".id");
				
				List<String> playerBanners = PlayerManager.getPlayers().getStringList(bannerOwnerId+".banners");
				playerBanners.remove(code);
				
				BannerManager.getBanners().set(code, null);
				PlayerManager.getPlayers().set(bannerOwnerId+".banners", playerBanners);
				
				BannerManager.saveBanners();
				PlayerManager.savePlayers();
				
				sender.sendMessage(ChatColor.GREEN + "Banner removed");
			}
		}
		
		return true;
	}
}
