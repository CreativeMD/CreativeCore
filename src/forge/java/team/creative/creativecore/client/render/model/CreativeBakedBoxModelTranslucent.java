package team.creative.creativecore.client.render.model;

import java.util.List;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderTypeHelper;

public class CreativeBakedBoxModelTranslucent extends CreativeBakedBoxModel {
    
    public CreativeBakedBoxModelTranslucent(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block) {
        super(location, item, block, false);
    }
    
    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return List.of(RenderTypeHelper.getEntityRenderType(RenderType.translucent(), fabulous));
    }
    
    @Override
    public boolean translucent() {
        return true;
    }
    
}
