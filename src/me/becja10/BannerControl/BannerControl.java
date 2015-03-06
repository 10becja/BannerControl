package me.becja10.BannerControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.becja10.BannerControl.commands.AssignBannerCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BannerControl extends JavaPlugin implements Listener
{
	public final Logger logger = Logger.getLogger("Minecraft");
	private static BannerControl plugin;
	private List<Player>players = new ArrayList<Player>();

	//config stuff
	private String configPath;
	private FileConfiguration config;
	private FileConfiguration outConfig;
	
	private String config_message;			//The message to display when a player fails to craft a banner
	private int config_numLayers;			//the number of layers a player can craft;
	private boolean config_allowCopy;			//whether or not players can copy banners
	
	private void loadConfig()
	{
		//get config options, or defaults
		config_message = config.getString("Failure Message", ChatColor.RED + "Special Banners can not be crafted!");
		config_numLayers = config.getInt("Number of Layers", 2);
		config_allowCopy = config.getBoolean("Allow banner copy", true);
		
		//write to outConfig
		outConfig.set("Failure Message", config_message);
		outConfig.set("Number of Layers", config_numLayers);
		outConfig.set("Allow banner copy", config_allowCopy);
		
		//save config
		save();
	}
	
	public void onEnable()
	{
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		plugin = this;
		
		//load/generate config file
		configPath = plugin.getDataFolder() + File.separator + "config.yml"; //here if breaks
		
		config = YamlConfiguration.loadConfiguration(new File(configPath));
		outConfig = new YamlConfiguration();
		loadConfig();
		PlayerManager.saveDefaultPlayers();
		BannerManager.saveDefaultBanners();
	}

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Has Been Disabled!");
		//save config
		save();
		PlayerManager.savePlayers();
		BannerManager.saveBanners();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("assignbanner"))
		{
			AssignBannerCommand.handleCommand(sender, args);
		}
		return true;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCraft(PrepareItemCraftEvent event)
	{
		/*
		//don't care about anything other than banners right now
		if(event.getRecipe().getResult().getType() != Material.BANNER) return;
		int banners = 0; //track how many banners are being used to craft
		for(ItemStack i : event.getInventory().getMatrix())
			if(i != null && i.getType() == Material.BANNER)
				banners++;
		//0 == crafting blank banner; 2 == copying banner
		if(!(banners == 0 || banners == 2))
		{
			event.getInventory().setResult(new ItemStack(Material.AIR)); //set the result to air
			for(HumanEntity p : event.getViewers())
				if(p instanceof Player)
					p.sendMessage(ChatColor.RED + "Special banners can not be crafted, you must earn them!");
		}
		*/
		
		//only care if a banner is being crafted
		if(event.getRecipe().getResult().getType() != Material.BANNER) return;
		int banners = 0; //used to see if they are copying a banner or not.
		
		//loop over all ingredients
		for(ItemStack i : event.getInventory().getMatrix())
			if(i != null && i.getType() == Material.BANNER)
				banners++;
		
		//if players can copy, and there are two banners in the recipe, just return
		if(banners == 2 && config_allowCopy) return;
		
		//get the item that will be crafted
		ItemStack item = event.getInventory().getResult();
		
		//get the associated metadata
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof BannerMeta)
		{
			BannerMeta bm = (BannerMeta) meta;
			//check how many patterns will be on the finished product.
			if(bm.numberOfPatterns() > config_numLayers)
			{
				//Prevent crafting
				event.getInventory().setResult(new ItemStack(Material.AIR));
				//tell anyone in the window that it's not allowed.
				for(HumanEntity p : event.getViewers())
				{
					if(p instanceof Player)
					{
						final Player player = (Player)p;
						//if this player has permission to craft, replace the item
						if(player.hasPermission("bannercontrol.craftall"))
						{
							event.getInventory().setResult(item);
							return;
						}
						
						//check if this player was warned recently
						if(players.contains(player)) continue;
						else 
						{
							p.sendMessage(config_message);
							players.add(player);
							//remove them from the list after 5 seconds. 
							//This prevents spammy messages with the many crafting options
							Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
								{public void run(){players.remove(player);}}, 20L * 5);
						}
					}
				}
			}
		}
	}
	
	private void save()
	{
        try
        {
            outConfig.save(configPath);
        }
        catch(IOException exception)
        {
            logger.info("Unable to write to the configuration file at \"" + configPath + "\"");
        }
	}

	public static JavaPlugin getInstance() {return plugin;}
}
