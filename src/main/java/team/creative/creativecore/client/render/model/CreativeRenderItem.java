package team.creative.creativecore.client.render.model;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.math.base.Facing;

@OnlyIn(value = Dist.CLIENT)
public abstract class CreativeRenderItem {
    
    public static Minecraft mc = Minecraft.getInstance();
    
    public static final RenderType[] STANDARD = new RenderType[] { Sheets.cutoutBlockSheet() };
    public static final RenderType[] ADVANCED = new RenderType[] { Sheets.cutoutBlockSheet(), Sheets.translucentCullBlockSheet() };
    
    public abstract List<? extends RenderBox> getBoxes(ItemStack stack, RenderType layer);
    
    public RenderType[] getLayers(ItemStack stack, boolean fabulous) {
        if (hasTranslucentLayer(stack))
            return ADVANCED;
        return STANDARD;
    }
    
    public boolean hasTranslucentLayer(ItemStack stack) {
        return false;
    }
    
    public void applyCustomOpenGLHackery(PoseStack pose, ItemStack stack, TransformType cameraTransformType) {}
    
    public List<BakedQuad> getCachedModel(Facing facing, RenderType layer, ItemStack stack, boolean threaded) {
        return null;
    }
    
    public void saveCachedModel(Facing facing, RenderType layer, List<BakedQuad> cachedQuads, ItemStack stack, boolean threaded) {}
    
}