package team.creative.creativecore.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class FakeChunkGenerator extends ChunkGenerator {
    
    public static final Codec<FakeChunkGenerator> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(FakeChunkGenerator::new, FakeChunkGenerator::biomes).stable()
            .codec();
    private final Registry<Biome> biomes;
    
    public FakeChunkGenerator(Registry<Biome> biomes) {
        super(new SingleBiomeProvider(biomes.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
        this.biomes = biomes;
    }
    
    public Registry<Biome> biomes() {
        return this.biomes;
    }
    
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    
    @Override
    public ChunkGenerator withSeed(long p_230349_1_) {
        return this;
    }
    
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {}
    
    @Override
    public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {}
    
    @Override
    public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureManager p_230351_2_) {}
    
    @Override
    public int getBaseHeight(int p_222529_1_, int p_222529_2_, Type p_222529_3_) {
        return 0;
    }
    
    @Override
    public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
        return new Blockreader(new BlockState[0]);
    }
    
    @Override
    public void createStructures(DynamicRegistries p_242707_1_, StructureManager p_242707_2_, IChunk p_242707_3_, TemplateManager p_242707_4_, long p_242707_5_) {
        
    }
    
}
