package team.creative.creativecore.client.render.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class CreativeBakedBoxModel extends CreativeBakedModel {
    
    public static Minecraft mc = Minecraft.getInstance();
    
    public static List<BakedQuad> compileBoxes(List<? extends RenderBox> boxes, Facing side, RenderType layer, RandomSource rand, boolean item, List<BakedQuad> baked) {
        if (side == null)
            return Collections.EMPTY_LIST;
        
        for (int i = 0; i < boxes.size(); i++) {
            RenderBox box = boxes.get(i);
            
            if (!box.shouldRenderFace(side))
                continue;
            
            BlockState state = Blocks.AIR.defaultBlockState();
            if (box.state != null)
                state = box.state;
            
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
            
            int defaultColor = ColorUtils.WHITE;
            if (item)
                defaultColor = mc.getItemColors().getColor(new ItemStack(state.getBlock()), defaultColor);
            
            baked.addAll(box.getBakedQuad(null, null, box.getOffset(), state, blockModel, side, layer, rand, true, defaultColor));
        }
        for (BakedQuad quad : baked)
            if (quad instanceof CreativeBakedQuad c)
                c.updateAlpha();
            
        return baked;
    }
    
    private final List<BakedModel> both;
    private final CreativeBakedBoxModelTranslucent pairModel;
    public CreativeBlockModel block;
    
    public CreativeBakedBoxModel(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block) {
        super(location, item);
        this.block = block;
        this.pairModel = new CreativeBakedBoxModelTranslucent(location, item, block);
        this.both = Arrays.asList(this, pairModel);
    }
    
    CreativeBakedBoxModel(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block, boolean outside) {
        super(location, item);
        this.block = block;
        if (outside) {
            this.pairModel = new CreativeBakedBoxModelTranslucent(location, item, block);
            this.both = Arrays.asList(this, pairModel);
        } else {
            this.pairModel = null;
            this.both = null;
        }
    }
    
    public ItemOverrides customOverride = new ItemOverrides() {
        
        @Override
        public BakedModel resolve(BakedModel original, ItemStack stack, ClientLevel level, LivingEntity entity, int p_173469_) {
            renderedStack = stack;
            return super.resolve(original, stack, level, entity, p_173469_);
        }
    };
    
    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        if (((CreativeItemBoxModel) item).hasTranslucentLayer(itemStack)) {
            pairModel.renderedStack = renderedStack;
            return both;
        }
        return super.getRenderPasses(itemStack, fabulous);
    }
    
    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        if (block != null)
            return block.getModelData(level, pos, state, modelData);
        return modelData;
    }
    
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType layer) {
        
        Facing facing = Facing.get(direction);
        if (state != null) {
            if (block != null)
                return compileBoxes(block.getBoxes(state, extraData, rand), facing, layer, rand, false, new ArrayList<>());
            return Collections.EMPTY_LIST;
        }
        
        if (renderedStack == null || renderedStack.isEmpty() || direction != null)
            return Collections.EMPTY_LIST;
        
        try {
            List<BakedQuad> cached = ((CreativeItemBoxModel) item).getCachedModel(translucent(), renderedStack, false);
            if (cached != null)
                return cached;
            List<? extends RenderBox> boxes = ((CreativeItemBoxModel) item).getBoxes(renderedStack, translucent());
            if (boxes != null) {
                cached = new ArrayList<>();
                for (int i = 0; i < Facing.VALUES.length; i++)
                    compileBoxes(boxes, Facing.VALUES[i], layer, rand, true, cached);
                ((CreativeItemBoxModel) item).saveCachedModel(translucent(), cached, renderedStack, false);
                return cached;
            }
            
            return Collections.EMPTY_LIST;
        } finally {
            renderedStack = null;
        }
    }
    
    public boolean translucent() {
        return false;
    }
    
    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource rand) {
        return getQuads(state, direction, rand, ModelData.EMPTY, Sheets.cutoutBlockSheet());
    }
    
}
