package me.becja10.BannerControl.commands;

import java.util.ArrayList;
import java.util.List;

import me.becja10.BannerControl.Utils.BannerManager;
import me.becja10.BannerControl.Utils.Parser;
import me.becja10.BannerControl.Utils.PlayerManager;
import me.becja10.BannerControl.Utils.ColorCode;
import me.becja10.BannerControl.Utils.PatternCode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class ShowBannersCommand 
{

	public static boolean handleCommand(CommandSender sender, String[] args) 
	{
		boolean isPlayer = false;
		if(sender instanceof Player)
			isPlayer = true;

		if(!isPlayer)
			sender.sendMessage("You aren't a player derpy derp");

		else if(isPlayer && !sender.hasPermission("bannercontrol.show"))
			sender.sendMessage(ChatColor.DARK_RED+"You do not have access to this command.");

		else
		{
			//handle different cases
			switch (args.length)
			{

			case 0: //Check own banners
				if(!getPlayerBanners((Player)sender, (Player)sender))
					sender.sendMessage(ChatColor.RED+"You have no banners.");
				break;		

			case 1: //Check another players banners
				//make sure they have permission if they are a player
				if(isPlayer && !sender.hasPermission("bannercontrol.show.others"))
					sender.sendMessage(ChatColor.DARK_RED+"You do not have access to this command.");
				//they have permission
				else
				{
					Player target = Bukkit.getPlayer(args[0]);
					if(target == null)
						sender.sendMessage(ChatColor.DARK_RED+"Player not found.");
					else
						//returns false if the player can't be found
						if(!getPlayerBanners(target, (Player) sender))
							sender.sendMessage(ChatColor.RED+target.getName()+" has no banners.");
				}
				break;

			default: //they messed up
				return false;
			}		
		}
		return true;
	}

	public static boolean getPlayerBanners(Player player, Player sender)
	{
		String id = player.getUniqueId().toString();
		//see if they are in the database
		if(PlayerManager.getPlayers().contains(id))
		{
			//the list of banners owned by this player in code form
			List<String> banners = PlayerManager.getPlayers().getStringList(id+".banners");

			//create a new inventory to display the banners in
			Inventory inventory = Bukkit.createInventory(null, 54, player.getName()+ "'s Banners.");
			for(String s : banners)
			{
				//create a temporary banner itemstack to add to the inventory
				ItemStack temp = new ItemStack(Material.BANNER);
				//grab temp's banner meta
				BannerMeta bm = (BannerMeta) temp.getItemMeta();
				//grab the base color code from the current bannercode
				String baseCode = Parser.getBaseColor(s);

				bm.setBaseColor(ColorCode.getColorByCode(baseCode));

				//loop through all patterns and add them to meta
				String[] patterns = Parser.splitCode(s);
				for(int i = 1; i < patterns.length; i++)
				{
					Character colorChar = patterns[i].charAt(0);
					Character patternChar = patterns[i].charAt(1);
					DyeColor color = ColorCode.getColorByCode(colorChar.toString());
					PatternType pattern = PatternCode.getTypeByCode(patternChar.toString());
					Pattern p = new Pattern(color, pattern);
					bm.addPattern(p);
					List<String> lore = new ArrayList<String>();
					lore.add(BannerManager.getBanners().getString(s+".lore"));
					bm.setLore(lore);
					bm.setDisplayName(player.getName()+"'s flag");
				}

				//add meta back to ItemStack
				temp.setItemMeta(bm);
				try{inventory.addItem(temp);}
				catch(IllegalArgumentException e)
				{
					System.out.println("bad item. BAD!");
					continue;
				}
			}
			sender.openInventory(inventory);
			return true;
		}
		return false;
	}

}
