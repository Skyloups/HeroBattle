package net.lnfinity.HeroBattle.listeners.commands;

import net.lnfinity.HeroBattle.HeroBattle;
import net.lnfinity.HeroBattle.classes.PlayerClass;
import net.lnfinity.HeroBattle.classes.displayers.eastereggs.DewoitineClass;
import net.lnfinity.HeroBattle.classes.displayers.eastereggs.DewoitineD550Class;
import net.lnfinity.HeroBattle.classes.displayers.eastereggs.PikachuClass;
import net.lnfinity.HeroBattle.game.HeroBattlePlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class ClassSelectionCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only as a player.");
			return true;
		}

		if (args == null || args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "/" + command.getName() + " <classe>");
		}

		else if (args.length == 1)
		{
			Player player = (Player) sender;

			if (!HeroBattle.get().getGame().isGameStarted())
			{
				HeroBattlePlayer heroBattlePlayer = HeroBattle.get().getGamePlayer(player);

				if (args[0].equalsIgnoreCase("ArsenalVG50"))
				{
					if (HeroBattle.get().getClassManager().getDewoitineUnlocks().contains(player.getUniqueId()))
					{
						HeroBattle.get().getClassManager().setPlayerClass(player, new DewoitineClass(HeroBattle.get(), 0, 0, 0), true);
						return true;
					}
				}

				else if (args[0].equalsIgnoreCase("ArsenalVG39"))
				{
					if (HeroBattle.get().getClassManager().getDewoitineUnlocks().contains(player.getUniqueId()))
					{
						HeroBattle.get().getClassManager().setPlayerClass(player, new DewoitineD550Class(HeroBattle.get(), 0, 0, 0), true);
						return true;
					}
				}

				else if (args[0].equalsIgnoreCase("PokemonJaune"))
				{
					if (HeroBattle.get().getClassManager().getPikachuUnlocks().contains(player.getUniqueId()))
					{
						HeroBattle.get().getClassManager().setPlayerClass(player, new PikachuClass(), true);
						return true;
					}
				}

				else if (args[0].equalsIgnoreCase("Pommeeeh"))
				{
					HeroBattle.get().getClassManager().getPommeUnlocks().add(player.getUniqueId());
					player.sendMessage(ChatColor.RED + "Vous ne possédez pas cette classe, ou elle n'existe pas !");

					return true;
				}

				for (PlayerClass theClass : heroBattlePlayer.getAvailableClasses())
				{
					if (args[0].equalsIgnoreCase(theClass.getType().getId()))
					{
						heroBattlePlayer.setPlayerClass(theClass);
						player.sendMessage(HeroBattle.GAME_TAG + ChatColor.GREEN + "Vous avez choisi la classe "
								+ ChatColor.DARK_GREEN + theClass.getName() + ChatColor.GREEN + " !");
						return true;
					}
				}

				player.sendMessage(ChatColor.RED + "Vous ne possédez pas cette classe, ou elle n'existe pas !");
			}

			else
			{
				player.sendMessage(ChatColor.RED + "Le jeu est déjà commencé !");
			}
		}

		else
		{
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "Lolnope.");
				return true;
			}

			HeroBattlePlayer target = HeroBattle.get().getGamePlayer(HeroBattle.get().getServer().getPlayer(args[1]));
			if (target == null)
			{
				sender.sendMessage(ChatColor.RED + "Target lost.");
				return true;
			}

			PlayerClass pClass = HeroBattle.get().getClassManager().getAnyClassByFriendlyName(args[0], target);
			if (pClass != null)
			{
				HeroBattle.get().getClassManager().setPlayerClass(Bukkit.getPlayer(target.getUUID()), pClass, true);
				sender.sendMessage(ChatColor.GREEN + "Classe modifiée (normalement, cf. tab).");
			}
		}

		return true;
	}
}
