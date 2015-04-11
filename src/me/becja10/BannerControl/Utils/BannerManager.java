package me.becja10.BannerControl.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.becja10.BannerControl.BannerControl;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BannerManager
{
	private static FileConfiguration config = null;
	private static File banners = null;

	/*
	 * Get information about the players stored in players.yml
	 */
	public static FileConfiguration getBanners() {
		/*
		 * <code>:
		 *   id: 09128098123
		 *   name: blah
		 */
		if (config == null)
			reloadBanners();
		return config;
	}
	
	/*
	 * Reloads the player.yml file
	 */
	public static void reloadBanners() {
		if (banners == null)
			banners = new File(BannerControl.getInstance().getDataFolder(), "banners.yml");
		config = YamlConfiguration.loadConfiguration(banners);

		InputStream defConfigStream = BannerControl.getInstance().getResource("banners.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	/*
	 * saves information to player.yml
	 */
	public static void saveBanners() {
		if ((config == null) || (banners == null))
			return;
		try {
			getBanners().save(banners);
		} catch (IOException ex) {
			System.out.println("[BannerControl] Could not save config to " + banners);
		}
	}

	/*
	 * Creates the default, empty player.yml file
	 */
	public static void saveDefaultBanners() {
		if (banners == null)
			banners = new File(BannerControl.getInstance().getDataFolder(), "banners.yml");
		if (!banners.exists())
			BannerControl.getInstance().saveResource("banners.yml", false);
	}
}