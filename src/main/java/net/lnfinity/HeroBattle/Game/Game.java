package net.lnfinity.HeroBattle.Game;

import java.util.UUID;

import net.lnfinity.HeroBattle.HeroBattle;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Game {

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
			for (int i = 0; i <= 8; i++) {
				if (p.getGamePlayer(player).getPlayerClass().getItem(i) != null) {
					player.getInventory().setItem(i, p.getGamePlayer(player).getPlayerClass().getItem(i));
				}
			}

			player.setGameMode(GameMode.ADVENTURE);
			player.setMaxHealth(6);
			player.setHealth(6);

			loc++;
		}
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();

		Objective objective = board.registerNewObjective("percentage", "dummy");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("%");

		for (Player online : Bukkit.getOnlinePlayers()) {
			Score score = objective.getScore(online);
			score.setScore(p.getGamePlayer(online).getPercentage());
		}

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
		}
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
			p.getServer().broadcastMessage(
					HeroBattle.NAME + ChatColor.YELLOW + player.getDisplayName() + ChatColor.YELLOW + " a perdu ! "
							+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + p.getPlayingPlayerCount()
							+ ChatColor.DARK_GRAY + " joueurs restants" + ChatColor.DARK_GRAY + "]");
		}
	}

	public void start() {
		teleportPlayers();
	}

	public boolean isWaiting() {
		return waiting;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}
}
