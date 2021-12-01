package team.creative.creativecore.common.level;

import javax.annotation.Nullable;

import com.mojang.math.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.matrix.ChildVecOrigin;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.VecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class SubLevel extends CreativeLevel {
    
    public Level parentLevel;
    public IVecOrigin origin;
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean shouldRender;
    
    public static SubLevel createFakeWorld(Level level) {
        if (level instanceof ServerLevel)
            return new SubServerLevel(level, 6);
        return new SubLevel(level, 6);
    }
    
    protected SubLevel(Level parent, int radius) {
        super((WritableLevelData) parent.getLevelData(), radius, parent.getProfilerSupplier(), parent.isClientSide, parent.isDebug(), 0);
        this.parentLevel = parent;
        this.gatherCapabilities();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
    }
    
    @Override
    public IVecOrigin getOrigin() {
        return origin;
    }
    
    @Override
    public void setOrigin(Vec3d center) {
        if (parentLevel instanceof IOrientatedLevel)
            this.origin = new ChildVecOrigin(((IOrientatedLevel) parentLevel).getOrigin(), center);
        else
            this.origin = new VecOrigin(center);
    }
    
    @Override
    public boolean hasParent() {
        return true;
    }
    
    @Override
    public Level getParent() {
        return parentLevel;
    }
    
    @Override
    public Level getRealLevel() {
        if (parentLevel instanceof SubLevel)
            return ((SubLevel) parentLevel).getRealLevel();
        return parentLevel;
    }
    
    @Override
    public void playSound(@Nullable Player p_184133_1_, BlockPos pos, SoundEvent p_184133_3_, SoundSource p_184133_4_, float p_184133_5_, float p_184133_6_) {
        if (getOrigin() == null)
            return;
        getRealLevel().playSound(p_184133_1_, transformToRealWorld(pos), p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
    }
    
    @Override
    public void playSound(@Nullable Player p_184148_1_, double x, double y, double z, SoundEvent p_184148_8_, SoundSource p_184148_9_, float p_184148_10_, float p_184148_11_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = new Vector3d(x, y, z);
        getOrigin().transformPointToWorld(vec);
        getRealLevel().playSound(p_184148_1_, vec.x, vec.y, vec.z, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
    }
    
    @Override
    public void playSound(@Nullable Player p_217384_1_, Entity entity, SoundEvent p_217384_3_, SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_) {
        if (getOrigin() == null)
            return;
        Vec3 vec = getOrigin().transformPointToWorld(entity.getPosition(1.0F));
        getRealLevel().playSound(p_217384_1_, vec.x, vec.y, vec.z, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
    }
    
    @Override
    public void playLocalSound(double x, double y, double z, SoundEvent p_184134_7_, SoundSource p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealLevel().playLocalSound(vec.x, vec.y, vec.z, p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, p_184134_11_);
    }
    
    @Override
    public void addParticle(ParticleOptions p_195594_1_, double x, double y, double z, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealLevel().addParticle(p_195594_1_, vec.x, vec.y, vec.z, p_195594_8_, p_195594_10_, p_195594_12_);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addParticle(ParticleOptions p_195590_1_, boolean p_195590_2_, double x, double y, double z, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealLevel().addParticle(p_195590_1_, p_195590_2_, vec.x, vec.y, vec.z, p_195590_9_, p_195590_11_, p_195590_13_);
    }
    
    @Override
    public void addAlwaysVisibleParticle(ParticleOptions p_195589_1_, double x, double y, double z, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealLevel().addAlwaysVisibleParticle(p_195589_1_, vec.x, vec.y, vec.z, p_195589_8_, p_195589_10_, p_195589_12_);
    }
    
    @Override
    public void addAlwaysVisibleParticle(ParticleOptions p_217404_1_, boolean p_217404_2_, double x, double y, double z, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealLevel().addAlwaysVisibleParticle(p_217404_1_, p_217404_2_, vec.x, vec.y, vec.z, p_217404_9_, p_217404_11_, p_217404_13_);
    }
    
    @Override
    public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return getRealLevel().getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
    }
    
    @Override
    public float getShade(Direction direction, boolean p_230487_2_) {
        return getRealLevel().getShade(direction, p_230487_2_);
    }
    
    @Override
    public Scoreboard getScoreboard() {
        return getRealLevel().getScoreboard();
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return getRealLevel().getRecipeManager();
    }
    
    @Override
    public TagContainer getTagManager() {
        return getRealLevel().getTagManager();
    }
    
    @Override
    public void levelEvent(Player player, int p_217378_2_, BlockPos pos, int p_217378_4_) {
        getRealLevel().levelEvent(player, p_217378_2_, pos, p_217378_4_);
    }
    
    @Override
    public RegistryAccess registryAccess() {
        return getRealLevel().registryAccess();
    }
    
    @Override
    public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        getRealLevel().gameEvent(p_151549_, p_151550_, p_151551_);
    }

   
    
}
