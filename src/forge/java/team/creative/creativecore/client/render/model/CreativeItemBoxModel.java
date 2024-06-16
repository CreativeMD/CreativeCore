package team.creative.creativecore.client.render.model;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;

@OnlyIn(Dist.CLIENT)
public abstract class CreativeItemBoxModel extends CreativeItemModel {
    
    public static final Minecraft mc = Minecraft.getInstance();
    
    public static final CreativeItemBoxModel EMPTY = new CreativeItemBoxModel(ModelResourceLocation.vanilla("stone", "inventory")) {
        
        @Override
        public List<? extends RenderBox> getBoxes(ItemStack stack, boolean translucent) {
            return Collections.EMPTY_LIST;
        }
    };
    
    public CreativeItemBoxModel(ModelResourceLocation location) {
        super(location);
    }
    
    public abstract List<? extends RenderBox> getBoxes(ItemStack stack, boolean translucent);
    
    public boolean hasTranslucentLayer(ItemStack stack) {
        return false;
    }
    
    public List<BakedQuad> getCachedModel(boolean translucent, ItemStack stack, boolean threaded) {
        return null;
    }
    
    public void saveCachedModel(boolean translucent, List<BakedQuad> cachedQuads, ItemStack stack, boolean threaded) {}
    
    public void reload() {}
    
    @Override
    public CreativeBakedModel create(CreativeBlockModel block) {
        return new CreativeBakedBoxModel(location, this, block);
    }
    
}