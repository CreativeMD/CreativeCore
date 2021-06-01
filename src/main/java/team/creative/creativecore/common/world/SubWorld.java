package team.creative.creativecore.common.world;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.matrix.ChildVecOrigin;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.VecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class SubWorld extends CreativeWorld {
    
    public World parentWorld;
    public IVecOrigin origin;
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean shouldRender;
    
    public static SubWorld createFakeWorld(World world) {
        if (world instanceof ServerWorld)
            return new SubWorldServer(world, 6);
        return new SubWorld(world, 6);
    }
    
    protected SubWorld(World parent, int radius) {
        super((ISpawnWorldInfo) parent.getLevelData(), radius, parent.getProfilerSupplier(), parent.isClientSide, parent.isDebug(), 0);
        this.parentWorld = parent;
        this.gatherCapabilities();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
    }
    
    @Override
    public IVecOrigin getOrigin() {
        return origin;
    }
    
    @Override
    public void setOrigin(Vec3d center) {
        if (parentWorld instanceof IOrientatedWorld)
            this.origin = new ChildVecOrigin(((IOrientatedWorld) parentWorld).getOrigin(), center);
        else
            this.origin = new VecOrigin(center);
    }
    
    @Override
    public boolean hasParent() {
        return true;
    }
    
    @Override
    public World getParent() {
        return parentWorld;
    }
    
    @Override
    public World getRealWorld() {
        if (parentWorld instanceof SubWorld)
            return ((SubWorld) parentWorld).getRealWorld();
        return parentWorld;
    }
    
    @Override
    public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos pos, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
        if (getOrigin() == null)
            return;
        getRealWorld().playSound(p_184133_1_, transformToRealWorld(pos), p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
    }
    
    @Override
    public void playSound(@Nullable PlayerEntity p_184148_1_, double x, double y, double z, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = new Vector3d(x, y, z);
        getOrigin().transformPointToWorld(vec);
        getRealWorld().playSound(p_184148_1_, vec.x, vec.y, vec.z, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
    }
    
    @Override
    public void playSound(@Nullable PlayerEntity p_217384_1_, Entity entity, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(entity.getPosition(1.0F));
        getRealWorld().playSound(p_217384_1_, vec.x, vec.y, vec.z, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
    }
    
    @Override
    public void playLocalSound(double x, double y, double z, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealWorld().playLocalSound(vec.x, vec.y, vec.z, p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, p_184134_11_);
    }
    
    @Override
    public void addParticle(IParticleData p_195594_1_, double x, double y, double z, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealWorld().addParticle(p_195594_1_, vec.x, vec.y, vec.z, p_195594_8_, p_195594_10_, p_195594_12_);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double x, double y, double z, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealWorld().addParticle(p_195590_1_, p_195590_2_, vec.x, vec.y, vec.z, p_195590_9_, p_195590_11_, p_195590_13_);
    }
    
    @Override
    public void addAlwaysVisibleParticle(IParticleData p_195589_1_, double x, double y, double z, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealWorld().addAlwaysVisibleParticle(p_195589_1_, vec.x, vec.y, vec.z, p_195589_8_, p_195589_10_, p_195589_12_);
    }
    
    @Override
    public void addAlwaysVisibleParticle(IParticleData p_217404_1_, boolean p_217404_2_, double x, double y, double z, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
        if (getOrigin() == null)
            return;
        Vector3d vec = getOrigin().transformPointToWorld(new Vector3d(x, y, z));
        getRealWorld().addAlwaysVisibleParticle(p_217404_1_, p_217404_2_, vec.x, vec.y, vec.z, p_217404_9_, p_217404_11_, p_217404_13_);
    }
    
    @Override
    public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return getRealWorld().getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
    }
    
    @Override
    public float getShade(Direction direction, boolean p_230487_2_) {
        return getRealWorld().getShade(direction, p_230487_2_);
    }
    
    @Override
    public Scoreboard getScoreboard() {
        return getRealWorld().getScoreboard();
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return getRealWorld().getRecipeManager();
    }
    
    @Override
    public ITagCollectionSupplier getTagManager() {
        return getRealWorld().getTagManager();
    }
    
    @Override
    public void levelEvent(PlayerEntity player, int p_217378_2_, BlockPos pos, int p_217378_4_) {
        getRealWorld().levelEvent(player, p_217378_2_, pos, p_217378_4_);
    }
    
    @Override
    public DynamicRegistries registryAccess() {
        return getRealWorld().registryAccess();
    }
}
