package team.creative.creativecore.common.world;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.VecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class FakeWorld extends CreativeWorld {
    
    public MinecraftServer server;
    public IVecOrigin origin;
    
    private final Scoreboard scoreboard;
    private DimensionRenderInfo renderInfo;
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean shouldRender;
    
    public static FakeWorld createFakeWorld(MinecraftServer server, String name, boolean client) {
        FakeWorldInfo info = new FakeWorldInfo(Difficulty.PEACEFUL, false, true);
        if (!client)
            return new FakeWorld(info, 6, server::getProfiler, false, false, 0);
        return createFakeWorldClient(name, info, 6);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static FakeWorld createFakeWorldClient(String name, FakeWorldInfo info, int radius) {
        return new FakeWorld(info, radius, Minecraft.getInstance()::getProfiler, true, false, 0);
    }
    
    protected FakeWorld(ISpawnWorldInfo worldInfo, int radius, Supplier<IProfiler> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, radius, supplier, client, debug, seed);
        this.scoreboard = new Scoreboard();
        if (client)
            renderInfo = DimensionRenderInfo.forType(dimensionType());
    }
    
    @Override
    public IVecOrigin getOrigin() {
        return origin;
    }
    
    @Override
    public void setOrigin(Vec3d vec) {
        this.origin = new VecOrigin(vec);
    }
    
    @Override
    public boolean hasParent() {
        return false;
    }
    
    @Override
    public World getParent() {
        return null;
    }
    
    @Override
    public World getRealWorld() {
        return null;
    }
    
    @Override
    public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
    }
    
    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        boolean flag = renderInfo.constantAmbientLight();
        if (!p_230487_2_) {
            return flag ? 0.9F : 1.0F;
        } else {
            switch (p_230487_1_) {
            case DOWN:
                return flag ? 0.9F : 0.5F;
            case UP:
                return flag ? 0.9F : 1.0F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
            }
        }
    }
    
    @Override
    public void playSound(PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {}
    
    @Override
    public void playSound(PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {}
    
    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        if (isClientSide)
            return Minecraft.getInstance().getConnection().getRecipeManager();
        return getServer().getRecipeManager();
    }
    
    @Override
    public ITagCollectionSupplier getTagManager() {
        if (isClientSide)
            return Minecraft.getInstance().getConnection().getTags();
        return getServer().getTags();
    }
    
    @Override
    public void levelEvent(PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {}
    
    @Override
    public DynamicRegistries registryAccess() {
        if (isClientSide)
            return Minecraft.getInstance().getConnection().registryAccess();
        return getServer().registryAccess();
    }
    
}
