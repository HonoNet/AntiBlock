package net.simplyrin.hononet.antiblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

/**
 * Created by SimplyRin on 2018/02/03.
 */
public class Main extends JavaPlugin implements Listener {

	/**
	 * このプラグインは 2017/08/08 に最終更新されたプラグインを元に再構成されたものです。
	 */
	@Getter
	private static Main plugin;
	@Getter
	private String prefix = "§7[§cAntiBlock§7] §r";

	@Override
	public void onEnable() {
		plugin = this;
		if(!plugin.getDescription().getAuthors().contains("SimplyRin")) {
			plugin.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		plugin.saveDefaultConfig();
		plugin.getServer().getPluginManager().registerEvents(this, this);
		plugin.getCommand("antiblock").setExecutor(this);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();

		if(player.hasPermission("antiblock.bypass")) {
			return;
		}

		for(String b : getConfig().getStringList("Anti-Break." + world.getName())) {
			if(block.getType() == Material.getMaterial(b)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();

		if(player.hasPermission("antiblock.bypass")) {
			return;
		}

		for(String name : getConfig().getStringList("Anti-Place." + world.getName())) {
			if(block.getType() == Material.getMaterial(name)) {
				event.setCancelled(true);
			}
		}

		for(String name : getConfig().getStringList("Warning-Blocks." + world.getName())) {
			if(block.getType() == Material.getMaterial(name)) {
				for(Player target : Bukkit.getOnlinePlayers()) {
					if(target.hasPermission("antiblock.view")) {
						target.sendMessage(this.getPrefix() + "§c" + player.getName() + " が " + block.getType().name() + " を設置しました。");
					}
				}

				plugin.getServer().getConsoleSender().sendMessage(this.getPrefix() + "§c" + player.getName() + " が " + block.getType().name() + " を設置しました。");
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("antiblock.use")) {
			sender.sendMessage(this.getPrefix() + "§cYou do not have access to this command");
			sender.sendMessage(this.getPrefix() + "§cGitHub: §nhttps://github.com/HonoNet/AntiBlock");
			return true;
		}

		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("reload")) {
				plugin.reloadConfig();
				sender.sendMessage(this.getPrefix() + "§aConfig ファイルをリロードしました。");
				return true;
			}
		}

		sender.sendMessage(this.getPrefix() + "§cUsage: /" + cmd.getName() + " <reload>");
		return true;
	}

}
