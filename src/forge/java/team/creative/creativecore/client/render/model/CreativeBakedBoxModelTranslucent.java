package team.creative.creativecore.client.render.model;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CreativeBakedBoxModelTranslucent extends CreativeBakedBoxModel {
    
    public CreativeBakedBoxModelTranslucent(ModelResourceLocation location, CreativeItemBoxModel item, CreativeBlockModel block) {
        super(location, item, block, false);
    }

    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return List.of(getEntityRenderType(RenderType.translucent(), fabulous));
    }
    
    @Override
    public boolean translucent() {
        return true;
    }

    public static @NotNull RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
        if (chunkRenderType != RenderType.translucent()) {
            return Sheets.cutoutBlockSheet();
        } else {
            return !cull && Minecraft.useShaderTransparency() ? Sheets.translucentItemSheet() : Sheets.translucentCullBlockSheet();
        }
    }
}
