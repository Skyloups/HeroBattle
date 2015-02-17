package net.lnfinity.HeroBattle.Game;

import java.util.UUID;

import net.lnfinity.HeroBattle.HeroBattle;
import net.lnfinity.HeroBattle.Tools.PlayerTool;
import net.lnfinity.HeroBattle.Utils.WinnerFirework;
import net.md_5.bungee.api.ChatColor;

import net.samagames.gameapi.GameAPI;
import net.samagames.gameapi.json.Status;
import net.samagames.gameapi.types.GameArena;
import net.zyuiop.MasterBundle.MasterBundle;
import net.zyuiop.MasterBundle.StarsManager;
import net.zyuiop.coinsManager.CoinsManager;
import net.zyuiop.statsapi.StatsApi;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

public class Game implements GameArena {

	private HeroBattle p;
	private boolean waiting = true;

	public Game(HeroBattle plugin) {
		p = plugin;
	}

	public void teleportPlayers() {
		int loc = 1;
		for (Player player : p.getServer().getOnlinePlayers()) {
			p.addGamePlayer(player);
			player.teleport(new Location(player.getWorld(), p.getConfig().getInt("locations.point" + loc + ".x"), p
					.getConfig().getInt("locations.point" + loc + ".y"), p.getConfig().getInt(
					"locations.point" + loc + ".z")));

			player.getInventory().clear();
			/*
			 * for (int i = 0; i <= 8; i++) { if
			 * (p.getGamePlayer(player).getPlayerClass().getItem(i) != null) {
			 * player.getInventory().setItem(i,
			 * p.getGamePlayer(player).getPlayerClass().getItem(i)); } }
			 */

			GamePlayer hbPlayer = p.getGamePlayer(player);

			if (hbPlayer.getPlayerClass() == null) {
				// TODO Random class.
				hbPlayer.setPlayerClass(p.getClassManager().getClassFromName("Brute"));
			}

			int i = 0;
			for (PlayerTool tool : hbPlayer.getPlayerClass().getTools()) {
				player.getInventory().setItem(i, tool.generateCompleteItem());
				i++;
			}

			player.setGameMode(GameMode.ADVENTURE);
			player.setMaxHealth(hbPlayer.getPlayerClass().getLives() * 2);
			player.setHealth(hbPlayer.getPlayerClass().getLives() * 2d);

			loc++;
		}
		waiting = false;
	}

	public void teleportHub(UUID id) {
		Player player = p.getServer().getPlayer(id);
		player.teleport(new Location(player.getWorld(), p.getConfig().getInt("locations.hub.x"), p.getConfig().getInt(
				"locations.hub.y"), p.getConfig().getInt("locations.hub.z")));
	}

	public void teleportRandomSpot(UUID id) {
		Player player = p.getServer().getPlayer(id);
		int loc = (int) (Math.random() * ((4 - 1) + 1));
		player.teleport(new Location(player.getWorld(), p.getConfig().getInt("locations.point" + loc + ".x"), p
				.getConfig().getInt("locations.point" + loc + ".y"), p.getConfig().getInt(
				"locations.point" + loc + ".z")));
	}

	public void onPlayerDeath(UUID id) {
		Player player = p.getServer().getPlayer(id);
		GamePlayer HBplayer = p.getGamePlayer(player);
		Damageable d = (Damageable) player;
		HBplayer.setPercentage(0);
		player.setExp(0);
		player.setLevel(0);
		player.setTotalExperience(0);
		if (d.getHealth() > 2) {
			HBplayer.setLives(HBplayer.getLives() - 1);
			player.setHealth(HBplayer.getLives() * 2);
			teleportRandomSpot(player.getUniqueId());
		} else {
			player.setGameMode(GameMode.SPECTATOR);
			HBplayer.setPlaying(false);
			teleportHub(player.getUniqueId());
			player.getInventory().clear();
			String s = "s";
			if (p.getPlayingPlayerCount() == 1) {
				s = "";
			}
			p.getServer().broadcastMessage(
					HeroBattle.NAME + ChatColor.YELLOW + player.getName() + ChatColor.YELLOW + " a perdu ! "
							+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + p.getPlayingPlayerCount()
							+ ChatColor.DARK_GRAY + " joueur" + s + " restant" + s + ChatColor.DARK_GRAY + "]");
			if (p.getPlayingPlayerCount() == 1) {
				for (Player pl : p.getServer().getOnlinePlayers()) {
					if (p.getGamePlayer(pl.getUniqueId()).isPlaying()) {
						onPlayerWin(pl.getUniqueId());
						return;
					}
				}
			}
		}

		StatsApi.increaseStat(player, p.getName(), "deaths", 1);
	}

	public void onPlayerWin(UUID id) {
		Player player = p.getServer().getPlayer(id);
		GamePlayer HBplayer = p.getGamePlayer(player);
		HBplayer.setPlaying(false);
		p.getServer().broadcastMessage(
				HeroBattle.NAME + ChatColor.YELLOW + player.getDisplayName() + " a remporté la partie !");
		new WinnerFirework(p, 30, player);
		p.getServer().getScheduler().runTaskLater(p, new Runnable() {
			public void run() {
				// p.getServer().shutdown();
			}
		}, 300L);

		StarsManager.creditJoueur(player, 1, "Victoire !");
		CoinsManager.creditJoueur(player.getUniqueId(), 5, true, true, "Victoire !");
		StatsApi.increaseStat(player, p.getName(), "wins", 1);

		if(MasterBundle.isDbEnabled) {
			Bukkit.getServer().getScheduler().runTaskLater(p, new Runnable() {
				@Override
				public void run() {
					for(Player player : p.getServer().getOnlinePlayers()) {
						player.kickPlayer("");
					}
				}
			}, 25*20L);

			Bukkit.getServer().getScheduler().runTaskLater(p, new Runnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			}, 30*20L);
		}
	}

	public void start() {
		p.getScoreboardManager().init();
		teleportPlayers();

		GameAPI.getManager().sendArena();
	}

	public boolean isWaiting() {
		return waiting;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}


	@Override
	public int countGamePlayers() {
		int count = 0;
		for(Player player : p.getServer().getOnlinePlayers()) {
			if(player.getGameMode() != GameMode.SPECTATOR) count++;
		}
		return count;
	}

	@Override
	public int getMaxPlayers() {
		return 84 - 21;
	}

	@Override
	public int getTotalMaxPlayers() {
		return 84;
	}

	@Override
	public int getVIPSlots() {
		return 21;
	}

	@Override
	public Status getStatus() {
		if(isWaiting()) {
			return Status.Available;
		}
		else {
			return Status.InGame;
		}
	}

	@Override
	public void setStatus(Status status) {

	}

	@Override
	public String getMapName() {
		return p.getConfig().getString("mapName");
	}

	@Override
	public boolean hasPlayer(UUID uuid) {
		Player player = p.getServer().getPlayer(uuid);
		return player != null && player.getGameMode() != GameMode.SPECTATOR;
	}
}