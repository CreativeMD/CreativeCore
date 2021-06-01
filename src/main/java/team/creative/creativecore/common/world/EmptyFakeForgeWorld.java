package team.creative.creativecore.common.world;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.ForgeWorldType;

public class EmptyFakeForgeWorld extends ForgeWorldType {
    
    public EmptyFakeForgeWorld() {
        super(new IChunkGeneratorFactory() {
            
            @Override
            public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings) {
                return new FakeChunkGenerator(biomes);
            }
        });
    }
    
}
