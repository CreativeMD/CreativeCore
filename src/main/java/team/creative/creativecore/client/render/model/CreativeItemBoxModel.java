package team.creative.creativecore.client.render.model;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.math.base.Facing;

@OnlyIn(Dist.CLIENT)
public abstract class CreativeItemBoxModel extends CreativeItemModel {
    
    public static final Minecraft mc = Minecraft.getInstance();
    
    public static final List<RenderType> STANDARD = List.of(Sheets.cutoutBlockSheet());
    public static final List<RenderType> ADVANCED = List.of(Sheets.cutoutBlockSheet(), Sheets.translucentCullBlockSheet());
    
    public static final CreativeItemBoxModel EMPTY = new CreativeItemBoxModel(new ModelResourceLocation("minecraft", "stone", "inventory")) {
        
        @Override
        public List<? extends RenderBox> getBoxes(ItemStack stack, RenderType layer) {
            return Collections.EMPTY_LIST;
        }
    };
    
    public CreativeItemBoxModel(ModelResourceLocation location) {
        super(location);
    }
    
    public abstract List<? extends RenderBox> getBoxes(ItemStack stack, RenderType layer);
    
    public List<RenderType> getLayers(ItemStack stack, boolean fabulous) {
        if (hasTranslucentLayer(stack))
            return ADVANCED;
        return STANDARD;
    }
    
    public boolean hasTranslucentLayer(ItemStack stack) {
        return false;
    }
    
    public List<BakedQuad> getCachedModel(Facing facing, RenderType layer, ItemStack stack, boolean threaded) {
        return null;
    }
    
    public void saveCachedModel(Facing facing, RenderType layer, List<BakedQuad> cachedQuads, ItemStack stack, boolean threaded) {}
    
    public void reload() {}
    
    @Override
    public CreativeBakedModel create(CreativeBlockModel block) {
        return new CreativeBakedBoxModel(location, this, block);
    }
    
}