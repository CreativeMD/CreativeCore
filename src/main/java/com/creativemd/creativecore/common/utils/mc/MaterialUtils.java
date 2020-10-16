package com.creativemd.creativecore.common.utils.mc;

import java.util.HashMap;

import net.minecraft.block.material.Material;

public class MaterialUtils {
	
	private static HashMap<Material, String> materialNames = new HashMap<>();
	private static HashMap<String, Material> materials = new HashMap<>();
	
	public static String getNameOrDefault(Material material, String alternative) {
		return materialNames.getOrDefault(material, alternative);
	}
	
	public static String getName(Material material) {
		return materialNames.get(material);
	}
	
	public static Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public static void registerMaterial(String name, Material material) {
		materialNames.put(material, name);
		materials.put(name, material);
	}
	
	static {
		registerMaterial("air", Material.AIR);
		registerMaterial("grass", Material.GRASS);
		registerMaterial("ground", Material.GROUND);
		registerMaterial("wood", Material.WOOD);
		registerMaterial("rock", Material.ROCK);
		registerMaterial("iron", Material.IRON);
		registerMaterial("anvil", Material.ANVIL);
		registerMaterial("water", Material.WATER);
		registerMaterial("lava", Material.LAVA);
		registerMaterial("leaves", Material.LEAVES);
		registerMaterial("plants", Material.PLANTS);
		registerMaterial("vine", Material.VINE);
		registerMaterial("sponge", Material.SPONGE);
		registerMaterial("cloth", Material.CLOTH);
		registerMaterial("fire", Material.FIRE);
		registerMaterial("sand", Material.SAND);
		registerMaterial("circuits", Material.CIRCUITS);
		registerMaterial("carpet", Material.CARPET);
		registerMaterial("glass", Material.GLASS);
		registerMaterial("redstone_light", Material.REDSTONE_LIGHT);
		registerMaterial("tnt", Material.TNT);
		registerMaterial("coral", Material.CORAL);
		registerMaterial("ice", Material.ICE);
		registerMaterial("packed_ice", Material.PACKED_ICE);
		registerMaterial("snow", Material.SNOW);
		registerMaterial("crafted_snow", Material.CRAFTED_SNOW);
		registerMaterial("cactus", Material.CACTUS);
		registerMaterial("clay", Material.CLAY);
		registerMaterial("gourd", Material.GOURD);
		registerMaterial("dragon_egg", Material.DRAGON_EGG);
		registerMaterial("portal", Material.PORTAL);
		registerMaterial("cake", Material.CAKE);
		registerMaterial("web", Material.WEB);
		registerMaterial("PISTON", Material.PISTON);
		registerMaterial("barrier", Material.BARRIER);
		registerMaterial("structure_void", Material.STRUCTURE_VOID);
	}
	
}
