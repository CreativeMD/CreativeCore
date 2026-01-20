package team.creative.creativecore.client.render.model;

import java.util.List;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CreativeBakedBoxModelTranslucent extends CreativeBakedBoxModel {
    
    private RenderType type;
    
    public CreativeBakedBoxModelTranslucent(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block) {
        super(location, item, block, false);
    }
    
    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        type = RenderTypeHelper.getEntityRenderType(RenderType.translucent(), fabulous);
        return List.of(type);
    }
    
    @Override
    public boolean translucent() {
        return true;
    }
    
    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource rand) {
        return getQuads(state, direction, rand, ModelData.EMPTY, type);
    }
    
}
