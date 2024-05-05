package team.creative.creativecore.common.util.mc;

import java.util.HashMap;

import net.minecraft.world.level.material.Material;

public class MaterialUtils {
    
    private static final HashMap<Material, String> materialNames = new HashMap<>();
    private static final HashMap<String, Material> materials = new HashMap<>();
    
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
        registerMaterial("structural_air", Material.STRUCTURAL_AIR);
        registerMaterial("portal", Material.PORTAL);
        registerMaterial("cloth_decoration", Material.CLOTH_DECORATION);
        registerMaterial("plant", Material.PLANT);
        registerMaterial("water_plant", Material.WATER_PLANT);
        registerMaterial("replaceable_plant", Material.REPLACEABLE_PLANT);
        registerMaterial("replaceable_fireproof_plant", Material.REPLACEABLE_FIREPROOF_PLANT);
        registerMaterial("replaceable_water_plant", Material.REPLACEABLE_WATER_PLANT);
        registerMaterial("water", Material.WATER);
        registerMaterial("bubble_column", Material.BUBBLE_COLUMN);
        registerMaterial("lava", Material.LAVA);
        registerMaterial("top_snow", Material.TOP_SNOW);
        registerMaterial("fire", Material.FIRE);
        registerMaterial("decoration", Material.DECORATION);
        registerMaterial("web", Material.WEB);
        registerMaterial("buildable_glass", Material.BUILDABLE_GLASS);
        registerMaterial("clay", Material.CLAY);
        registerMaterial("dirt", Material.DIRT);
        registerMaterial("grass", Material.GRASS);
        registerMaterial("ice_solid", Material.ICE_SOLID);
        registerMaterial("sand", Material.SAND);
        registerMaterial("sponge", Material.SPONGE);
        registerMaterial("shulker_shell", Material.SHULKER_SHELL);
        registerMaterial("wood", Material.WOOD);
        registerMaterial("nether_wood", Material.NETHER_WOOD);
        registerMaterial("bamboo_sapling", Material.BAMBOO_SAPLING);
        registerMaterial("bamboo", Material.BAMBOO);
        registerMaterial("wool", Material.WOOL);
        registerMaterial("explosive", Material.EXPLOSIVE);
        registerMaterial("leaves", Material.LEAVES);
        registerMaterial("glass", Material.GLASS);
        registerMaterial("ice", Material.ICE);
        registerMaterial("cactus", Material.CACTUS);
        registerMaterial("stone", Material.STONE);
        registerMaterial("metal", Material.METAL);
        registerMaterial("snow", Material.SNOW);
        registerMaterial("heavy_metal", Material.HEAVY_METAL);
        registerMaterial("barrier", Material.BARRIER);
        registerMaterial("piston", Material.PISTON);
        registerMaterial("coral", Material.MOSS);
        registerMaterial("vegetable", Material.VEGETABLE);
        registerMaterial("egg", Material.EGG);
        registerMaterial("cake", Material.CAKE);
        registerMaterial("amethyst", Material.AMETHYST);
        registerMaterial("powder_snow", Material.POWDER_SNOW);
    }
    
}