package net.lnfinity.HeroBattle.Class;

import net.lnfinity.HeroBattle.HeroBattle;
import net.lnfinity.HeroBattle.Tools.Tool;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BruteClass extends PlayerClass {

	public BruteClass(HeroBattle plugin) {

		super(plugin);

		addTool(p.getToolsManager().getTool(Tool.SWORD));
		addTool(p.getToolsManager().getTool(Tool.SPEED));
		addTool(p.getToolsManager().getTool(Tool.POWER));
		addTool(p.getToolsManager().getTool(Tool.EARTHQUAKE));
		addTool(p.getToolsManager().getTool(Tool.INK));
	}

	@Override
	public String getName() {
		return "Brute";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.DIAMOND_CHESTPLATE);
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("Pour le plaisir de faire des dégâts.");
	}

	@Override
	public int getMinDamages() {
		return 1;
	}

	@Override
	public int getMaxDamages() {
		return 8;
	}

	@Override
	public int getLives() {
		return 3;
	}

}