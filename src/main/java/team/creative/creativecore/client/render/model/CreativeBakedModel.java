package team.creative.creativecore.client.render.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class CreativeBakedModel implements BakedModel {
    
    public static Minecraft mc = Minecraft.getInstance();
    
    public static ItemColors itemColores = null;
    
    public static void lateInit() {
        itemColores = mc.getItemColors();
    }
    
    public static TextureAtlasSprite woodenTexture;
    
    private static ItemStack lastItemStack = null;
    
    public static void setLastItemStack(ItemStack stack) {
        lastItemStack = stack;
    }
    
    public static ItemOverrides customOverride = new ItemOverrides() {
        
        @Override
        public BakedModel resolve(BakedModel original, ItemStack stack, ClientLevel level, LivingEntity entity, int p_173469_) {
            lastItemStack = stack;
            return super.resolve(original, stack, level, entity, p_173469_);
        }
    };
    
    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite getWoodenTexture() {
        if (woodenTexture == null)
            woodenTexture = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("minecraft", "blocks/planks_oak"));
        return woodenTexture;
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        return getQuads(state, Facing.get(side), rand, extraData, false);
    }
    
    public static List<BakedQuad> compileBoxes(List<? extends RenderBox> boxes, Facing side, RenderType layer, Random rand, boolean item) {
        List<BakedQuad> baked = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            RenderBox box = boxes.get(i);
            
            if (!box.renderSide(side))
                continue;
            
            BlockState state = Blocks.AIR.defaultBlockState();
            if (box.state != null)
                state = box.state;
            
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
            
            int defaultColor = ColorUtils.WHITE;
            if (item)
                defaultColor = itemColores.getColor(new ItemStack(state.getBlock()), defaultColor);
            
            baked.addAll(box.getBakedQuad(null, null, box.getOffset(), state, blockModel, side, layer, rand, true, defaultColor));
        }
        return baked;
    }
    
    public static List<BakedQuad> getQuads(BlockState state, Facing facing, Random rand, IModelData extraData, boolean threaded) {
        if (state != null) {
            CreativeRenderBlock renderer = CreativeCoreClient.RENDERED_BLOCKS.get(state.getBlock());
            if (renderer != null)
                return compileBoxes(renderer.getBoxes(state), facing, MinecraftForgeClient.getRenderLayer(), rand, false);
            return Collections.EMPTY_LIST;
        }
        
        ItemStack stack = lastItemStack;
        CreativeRenderItem renderer = CreativeCoreClient.RENDERED_ITEMS.get(stack.getItem());
        
        if (renderer != null) {
            RenderType layer = MinecraftForgeClient.getRenderLayer();
            List<BakedQuad> cached = renderer.getCachedModel(facing, layer, stack, threaded);
            if (cached != null)
                return cached;
            List<? extends RenderBox> boxes = renderer.getBoxes(stack, layer);
            if (boxes != null) {
                cached = compileBoxes(boxes, facing, layer, rand, true);
                renderer.saveCachedModel(facing, layer, cached, stack, threaded);
                return cached;
            }
        }
        
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }
    
    @Override
    public boolean isGui3d() {
        return true;
    }
    
    @Override
    public boolean isCustomRenderer() {
        return false;
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon(IModelData data) {
        return getWoodenTexture();
    }
    
    @Override
    public BakedModel handlePerspective(TransformType cameraTransformType, PoseStack poseStack) {
        if (lastItemStack != null) {
            CreativeRenderItem renderer = CreativeCoreClient.RENDERED_ITEMS.get(lastItemStack.getItem());
            if (renderer != null)
                renderer.applyCustomOpenGLHackery(poseStack, lastItemStack, cameraTransformType);
        }
        return BakedModel.super.handlePerspective(cameraTransformType, poseStack);
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return customOverride;
    }
    
    @Override
    public boolean isLayered() {
        return true;
    }
    
    @Override
    public List<com.mojang.datafixers.util.Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous) {
        CreativeRenderItem renderer = CreativeCoreClient.RENDERED_ITEMS.get(lastItemStack.getItem());
        if (renderer != null) {
            RenderType[] itemLayers = renderer.getLayers(itemStack, fabulous);
            List<com.mojang.datafixers.util.Pair<BakedModel, RenderType>> layers = new ArrayList<>(itemLayers.length);
            for (int i = 0; i < itemLayers.length; i++)
                layers.add(new Pair<BakedModel, RenderType>(this, itemLayers[i]));
            return layers;
            
        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState state, Direction direction, Random rand) {
        return null;
    }
    
    @Override
    public boolean usesBlockLight() {
        return true;
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getWoodenTexture();
    }
    
}
