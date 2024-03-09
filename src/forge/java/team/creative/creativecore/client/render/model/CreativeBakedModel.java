package team.creative.creativecore.client.render.model;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CreativeBakedModel implements BakedModel {
    
    public final ModelResourceLocation location;
    public final CreativeItemModel item;
    protected ItemStack renderedStack = null;
    private BakedModel modelCache;
    
    public ItemOverrides customOverride = new ItemOverrides() {
        
        @Override
        public BakedModel resolve(BakedModel original, ItemStack stack, ClientLevel level, LivingEntity entity, int p_173469_) {
            renderedStack = stack;
            return super.resolve(original, stack, level, entity, p_173469_);
        }
    };
    
    public CreativeBakedModel(ModelResourceLocation location, CreativeItemModel item) {
        this.location = location;
        this.item = item;
    }
    
    public BakedModel get() {
        if (modelCache == null)
            modelCache = Minecraft.getInstance().getModelManager().getModel(location);
        return modelCache;
    }
    
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        return get().getQuads(state, side, rand, data, renderType);
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return get().useAmbientOcclusion();
    }
    
    @Override
    public boolean isGui3d() {
        return get().isGui3d();
    }
    
    @Override
    public boolean usesBlockLight() {
        return get().usesBlockLight();
    }
    
    @Override
    public boolean isCustomRenderer() {
        return false;
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return get().getParticleIcon();
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return customOverride;
    }
    
    @Override
    public ItemTransforms getTransforms() {
        return get().getTransforms();
    }
    
    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        if (renderedStack != null)
            item.applyCustomOpenGLHackery(poseStack, renderedStack, transformType);
        return BakedModel.super.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction direction, Random source) {
        return get().getQuads(state, direction, source);
    }
    
}
