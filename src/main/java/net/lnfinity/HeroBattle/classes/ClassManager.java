package net.lnfinity.HeroBattle.classes;

import net.lnfinity.HeroBattle.*;
import net.lnfinity.HeroBattle.classes.displayers.eastereggs.*;
import net.lnfinity.HeroBattle.classes.displayers.free.*;
import net.lnfinity.HeroBattle.classes.displayers.paid.*;
import net.lnfinity.HeroBattle.game.*;
import net.md_5.bungee.api.*;
import net.zyuiop.MasterBundle.*;
import org.bukkit.entity.*;

import java.util.*;


public class ClassManager {

	private final HeroBattle p;
	private List<PlayerClassType> totalClasses = new ArrayList<PlayerClassType>();

	private List<PlayerClass> availableClasses = new ArrayList<PlayerClass>();

	private Set<UUID> dewoitineUnlocks = new HashSet<>();
	private Set<UUID> pommeUnlocks = new HashSet<>();
	private Set<UUID> pikachuUnlocks = new HashSet<>();

	public ClassManager(HeroBattle plugin) {

		p = plugin;

		// TODO Merge these registers
		
		// Registers classes
		registerClass(new BruteClass(p));
		registerClass(new GuerrierClass(p));
		registerClass(new ArcherClass(p));
		registerClass(new MageClass(p));
		registerClass(new MinerClass(p));
		registerClass(new DruideClass(p));
		registerClass(new CryogenieClass(p));
		registerClass(new PyrobarbareClass(p));

		totalClasses.add(PlayerClassType.BRUTE);
		totalClasses.add(PlayerClassType.GUERRIER);
		totalClasses.add(PlayerClassType.ARCHER);
		totalClasses.add(PlayerClassType.MAGE);
		totalClasses.add(PlayerClassType.DRUIDE);
		totalClasses.add(PlayerClassType.MINEUR);
		totalClasses.add(PlayerClassType.CRYOGENIE);
		totalClasses.add(PlayerClassType.PYROBARBARE);

	}

	/**
	 * Registers a new player class in the game.
	 * 
	 * @param playerClass
	 *            The class.
	 * @return {@code true} if the class was added (i.e. not already
	 *         registered).
	 */
	public boolean registerClass(PlayerClass playerClass) {
		return availableClasses.add(playerClass);
	}

	/**
	 * Returns the classes currently registered in the game.
	 * 
	 * @return
	 */
	public List<PlayerClass> getAvailableClasses() {
		return availableClasses;
	}

	/**
	 * Returns a player class from its name.
	 * 
	 * @param name
	 *            The name of the class.
	 * @return The class; {@code null} if there isn't any class registered with
	 *         this name.
	 */
	public PlayerClass getClassFromName(Player player, String name) {
		HeroBattlePlayer heroBattlePlayer = p.getGamePlayer(player);
		return getClassFromName(heroBattlePlayer, name);
	}

	public PlayerClass getClassFromName(HeroBattlePlayer heroBattlePlayer, String name)
	{
		for (PlayerClass theClass : heroBattlePlayer.getAvaibleClasses())
		{
			if (theClass != null && theClass.getName().equalsIgnoreCase(name)) {
				return theClass;
			}
		}
		for (PlayerClass theClass : this.availableClasses) {
			if (theClass != null && theClass.getName().equalsIgnoreCase(name)) {
				return theClass;
			}
		}
		return null;
	}

	public void addPlayerClasses(final Player player) {
		// TODO Warning, this may cause problems if the request is lost (somehow)
		final HeroBattlePlayer heroBattlePlayer = p.getGamePlayer(player);
		final String prefix = "shops:" + HeroBattle.GAME_NAME_WHITE + ":";
		final String sufix = ":" + player.getUniqueId();
		final String currentStr = ":current";
		final String has = ".has";
		final String cooldown = ".cooldown";
		final String power = ".power";
		final String tools = ".tools";
		
		for(PlayerClass theClass : availableClasses) {
			final String className = theClass.getType().toString().toLowerCase();
			final PlayerClass current = theClass;
			p.getServer().getScheduler().runTaskAsynchronously(p, new Runnable() {
				@Override
				public void run() {
					if (MasterBundle.isDbEnabled) {
						String data = FastJedis.get(prefix + className + has + sufix);
						if((data != null && data.equals("1")) || className.equals("brute") || className.equals("guerrier") || className.equals("archer") || className.equals("mage") || className.equals("mineur")) {
							try {
							String A = FastJedis.get(prefix + className + cooldown + sufix + currentStr);
							if(A == null || A.equals("")) {
								A = "0";
							}
							String B = FastJedis.get(prefix + className + power + sufix + currentStr);
							if(B == null || B.equals("")) {
								B = "0";
							}
							String C = FastJedis.get(prefix + className + tools + sufix + currentStr);
							if(C == null || C.equals("")) {
								C = "0";
							}
								heroBattlePlayer.addAvaibleClass(constructPlayerClass(current.getType(), Integer.parseInt(A), Integer.parseInt(B), Integer.parseInt(C)));
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						} else {
							// Player doesn't have that class !
						}
					} else {
					// Default
						heroBattlePlayer.addAvaibleClass(new BruteClass(p, 0, 0, 0));
						heroBattlePlayer.addAvaibleClass(new GuerrierClass(p, 0, 0, 0));
						heroBattlePlayer.addAvaibleClass(new ArcherClass(p, 0, 0, 0));
						heroBattlePlayer.addAvaibleClass(new MageClass(p, 0, 0, 0));
						heroBattlePlayer.addAvaibleClass(new MinerClass(p, 0, 0, 0));
					}
				}
			});
		}
	}

	private PlayerClass constructPlayerClass(PlayerClassType type, int arg1, int arg2, int arg3) {
		switch (type) {
		case BRUTE:
			return new BruteClass(p, arg1, arg2, arg3);
		case GUERRIER:
			return new GuerrierClass(p, arg1, arg2, arg3);
		case ARCHER:
			return new ArcherClass(p, arg1, arg2, arg3);
		case MAGE:
			return new MageClass(p, arg1, arg2, arg3);
		case MINEUR:
			return new MinerClass(p, arg1, arg2, arg3);
		case DRUIDE:
			return new DruideClass(p, arg1, arg2, arg3);
		case CRYOGENIE: // /!\ Inverted with token `pyrobarbare` /!\
			return new PyrobarbareClass(p, arg1, arg2, arg3);
		case PYROBARBARE: // /!\ Inverted with token `cryogenie` /!\
			return new CryogenieClass(p, arg1, arg2, arg3);
		default:
			return new BruteClass(p, arg1, arg2, arg3);
		}
	}

	public PlayerClassType getPlayerClassType(String type) {
		return PlayerClassType.valueOf(type.toUpperCase());
	}

	public List<PlayerClassType> getTotalClasses() {
		return totalClasses;
	}

	public boolean playerHasClass(HeroBattlePlayer heroBattlePlayer, PlayerClassType type)
	{
		if (heroBattlePlayer != null)
		{
			for (int i = 0; i < heroBattlePlayer.getAvaibleClasses().size(); ++i)
			{
				if (heroBattlePlayer.getAvaibleClasses() != null && heroBattlePlayer.getAvaibleClasses().get(i) != null && heroBattlePlayer.getAvaibleClasses().get(i).getType() == type)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void setPlayerClass(Player player, PlayerClass theClass, boolean notify)
	{
		if(player == null) return;

		p.getGamePlayer(player).setPlayerClass(theClass);


		if(notify)
		{
			if (theClass != null)
			{
				player.sendMessage(HeroBattle.GAME_TAG + ChatColor.GREEN + "Vous avez choisi la classe "
						+ ChatColor.DARK_GREEN + theClass.getName() + ChatColor.GREEN + " !");
			}
			else
			{
				player.sendMessage(HeroBattle.GAME_TAG + ChatColor.GREEN + "Vous avez choisi une classe "
						+ ChatColor.DARK_GREEN + "aléatoire" + ChatColor.GREEN + " !");
			}
		}
	}

	public PlayerClass getAnyClassByFriendlyName(String friendlyName, HeroBattlePlayer target)
	{
		switch(friendlyName.toLowerCase()) {
			case "maite":
			case "maïte":
			case "maité":
			case "maïté":
				return new MaiteClass(p);

			case "dewoitine":
				return new DewoitineClass(p, 0, 0, 0);

			case "dewoitined550":
				return new DewoitineD550Class(p, 0, 0, 0);

			case "pooomme":
				return new PommeClass();

			case "pikachu":
				return new PikachuClass();

			default:
				PlayerClass playerClass = p.getClassManager().getClassFromName(target, friendlyName);

				if(playerClass != null)
				{
					return playerClass;
				}

				return null;
		}
	}


	public Set<UUID> getDewoitineUnlocks()
	{
		return dewoitineUnlocks;
	}

	public Set<UUID> getPommeUnlocks()
	{
		return pommeUnlocks;
	}

	public Set<UUID> getPikachuUnlocks()
	{
		return pikachuUnlocks;
	}
}
