package team.creative.creativecore.common.level;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.VecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class FakeLevel extends CreativeLevel {
    
    public MinecraftServer server;
    public IVecOrigin origin;
    
    private final Scoreboard scoreboard;
    private DimensionSpecialEffects effects;
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean shouldRender;
    
    public static FakeLevel createFakeWorld(MinecraftServer server, String name, boolean client) {
        FakeLevelInfo info = new FakeLevelInfo(Difficulty.PEACEFUL, false, true);
        if (!client)
            return new FakeLevel(info, 6, server::getProfiler, false, false, 0);
        return createFakeWorldClient(name, info, 6);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static FakeLevel createFakeWorldClient(String name, FakeLevelInfo info, int radius) {
        return new FakeLevel(info, radius, Minecraft.getInstance()::getProfiler, true, false, 0);
    }
    
    protected FakeLevel(WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, radius, supplier, client, debug, seed);
        this.scoreboard = new Scoreboard();
        if (client)
            effects = DimensionSpecialEffects.forType(dimensionType());
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
    public Level getParent() {
        return null;
    }
    
    @Override
    public Level getRealLevel() {
        return null;
    }
    
    @Override
    public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
    }
    
    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        boolean flag = effects.constantAmbientLight();
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
    public void playSound(Player p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundSource p_184148_9_, float p_184148_10_, float p_184148_11_) {}
    
    @Override
    public void playSound(Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_) {}
    
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
    public TagContainer getTagManager() {
        if (isClientSide)
            return Minecraft.getInstance().getConnection().getTags();
        return getServer().getTags();
    }
    
    @Override
    public void levelEvent(Player p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {}
    
    @Override
    public RegistryAccess registryAccess() {
        if (isClientSide)
            return Minecraft.getInstance().getConnection().registryAccess();
        return getServer().registryAccess();
    }
    
    @Override
    public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {}
    
}
