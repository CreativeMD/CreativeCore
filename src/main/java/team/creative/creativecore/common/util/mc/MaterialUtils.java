package team.creative.creativecore.common.util.mc;

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
        registerMaterial("structure_void", Material.STRUCTURE_VOID);
        registerMaterial("portal", Material.PORTAL);
        registerMaterial("carpet", Material.CARPET);
        registerMaterial("plants", Material.PLANTS);
        registerMaterial("ocean_plant", Material.OCEAN_PLANT);
        registerMaterial("tall_plants", Material.TALL_PLANTS);
        registerMaterial("nether_plants", Material.NETHER_PLANTS);
        registerMaterial("sea_grass", Material.SEA_GRASS);
        registerMaterial("water", Material.WATER);
        registerMaterial("bubble_column", Material.BUBBLE_COLUMN);
        registerMaterial("lava", Material.LAVA);
        registerMaterial("snow", Material.SNOW);
        registerMaterial("fire", Material.FIRE);
        registerMaterial("miscellaneous", Material.MISCELLANEOUS);
        registerMaterial("web", Material.WEB);
        registerMaterial("redstone_light", Material.REDSTONE_LIGHT);
        registerMaterial("clay", Material.CLAY);
        registerMaterial("earth", Material.EARTH);
        registerMaterial("organic", Material.ORGANIC);
        registerMaterial("packed_ice", Material.PACKED_ICE);
        registerMaterial("sand", Material.SAND);
        registerMaterial("sponge", Material.SPONGE);
        registerMaterial("shulker", Material.SHULKER);
        registerMaterial("wood", Material.WOOD);
        registerMaterial("nether_wood", Material.NETHER_WOOD);
        registerMaterial("bamboo_sapling", Material.BAMBOO_SAPLING);
        registerMaterial("bamboo", Material.BAMBOO);
        registerMaterial("wool", Material.WOOL);
        registerMaterial("tnt", Material.TNT);
        registerMaterial("leaves", Material.LEAVES);
        registerMaterial("glass", Material.GLASS);
        registerMaterial("ice", Material.ICE);
        registerMaterial("cactus", Material.CACTUS);
        registerMaterial("rock", Material.ROCK);
        registerMaterial("iron", Material.IRON);
        registerMaterial("snow_block", Material.SNOW_BLOCK);
        registerMaterial("anvil", Material.ANVIL);
        registerMaterial("barrier", Material.BARRIER);
        registerMaterial("piston", Material.PISTON);
        registerMaterial("coral", Material.CORAL);
        registerMaterial("gourd", Material.GOURD);
        registerMaterial("dragon_egg", Material.DRAGON_EGG);
        registerMaterial("cake", Material.CAKE);
    }
    
}
