package team.creative.creativecore.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CreativeBakedBoxModelTranslucent extends CreativeBakedBoxModel {
    
    public CreativeBakedBoxModelTranslucent(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block) {
        super(location, item, block, false);
    }
    
    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(RenderTypeHelper.getEntityRenderType(RenderType.translucent()));
    }
    
    @Override
    public boolean translucent() {
        return true;
    }
    
}
