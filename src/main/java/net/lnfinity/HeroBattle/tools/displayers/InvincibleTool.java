package net.lnfinity.HeroBattle.tools.displayers;

import net.lnfinity.HeroBattle.HeroBattle;
import net.lnfinity.HeroBattle.game.HeroBattlePlayer;
import net.lnfinity.HeroBattle.tools.PlayerTool;
import net.lnfinity.HeroBattle.utils.ItemCooldown;
import net.lnfinity.HeroBattle.utils.ToolsUtils;
import net.lnfinity.HeroBattle.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/*
 * This file is part of HeroBattle.
 *
 * HeroBattle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HeroBattle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HeroBattle.  If not, see <http://www.gnu.org/licenses/>.
 */
public class InvincibleTool extends PlayerTool
{

	private final int COOLDOWN; // seconds
	private final int DURATION; // seconds

	public InvincibleTool(HeroBattle plugin, int cooldown, int duration)
	{
		super(plugin);
		COOLDOWN = cooldown;
		DURATION = duration;
	}

	@Override
	public String getToolID()
	{
		return "tool.invinsible";
	}

	@Override
	public String getName()
	{
		return ChatColor.BLUE + "" + ChatColor.BOLD + "Givre";
	}

	@Override
	public List<String> getDescription()
	{
		return Utils.getToolDescription(ChatColor.GRAY + "Créé une couche de givre qui absorbe une bonne partie des dégâts pendant " + ChatColor.GOLD + DURATION + " " + ChatColor.GRAY + "secondes. Ne peut être utilisé que toutes les " + ChatColor.GOLD + COOLDOWN + " " + ChatColor.GRAY + "secondes.");
	}

	@Override
	public ItemStack getItem()
	{
		ItemStack item = new ItemStack(Material.SNOW_BALL);
		ToolsUtils.resetTool(item);
		return item;
	}

	@Override
	public void onRightClick(Player player, ItemStack tool, PlayerInteractEvent event)
	{
		if (ToolsUtils.isToolAvailable(tool))
		{
			new ItemCooldown(p, player, this, COOLDOWN);
			player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 1F, 1.5F);
			for (double i = 0; i <= 2; i += 0.05)
			{
				player.getWorld().playEffect(new Location(player.getWorld(), player.getLocation().getX() + Math.sin(i * Math.PI), player.getLocation().getY() + 1.5, player.getLocation().getZ() + Math.cos(i * Math.PI)), Effect.WATERDRIP, 0);
				player.getWorld().playEffect(new Location(player.getWorld(), player.getLocation().getX() + Math.sin(i * Math.PI), player.getLocation().getY() + 1, player.getLocation().getZ() + Math.cos(i * Math.PI)), Effect.WATERDRIP, 0);
				player.getWorld().playEffect(new Location(player.getWorld(), player.getLocation().getX() + Math.sin(i * Math.PI), player.getLocation().getY() + 0.5, player.getLocation().getZ() + Math.cos(i * Math.PI)), Effect.WATERDRIP, 0);
			}

			final HeroBattlePlayer heroBattlePlayer = p.getGamePlayer(player);
			heroBattlePlayer.addRemainingReducedIncomingDamages(DURATION);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Vous êtes trop fatigué pour réutiliser ça maintenant");
		}
	}
}
