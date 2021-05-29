package team.creative.creativecore.common.gui.integration;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiIntegratedParent extends IGuiParent {
    
    public List<GuiLayer> getLayers();
    
    public GuiLayer getTopLayer();
    
    public default boolean isOpen(Class<? extends GuiLayer> clazz) {
        for (GuiLayer layer : getLayers())
            if (clazz.isInstance(layer))
                return true;
        return false;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void render(MatrixStack matrixStack, Screen screen, ScreenEventListener listener, int mouseX, int mouseY) {
        int width = screen.width;
        int height = screen.height;
        
        listener.tick();
        Rect screenRect = Rect.getScreenRect();
        
        List<GuiLayer> layers = getLayers();
        for (int i = 0; i < layers.size(); i++) {
            GuiLayer layer = layers.get(i);
            
            if (i == layers.size() - 1) {
                if (layer.hasGrayBackground())
                    GuiRenderHelper.fillGradient(matrixStack, 0, 0, width, height, -1072689136, -804253680);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, matrixStack));
            }
            
            matrixStack.pushPose();
            int offX = (width - layer.getWidth()) / 2;
            int offY = (height - layer.getHeight()) / 2;
            matrixStack.translate(offX, offY, 0);
            
            RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
            Rect controlRect = new Rect(offX, offY, offX + layer.getWidth(), offY + layer.getHeight());
            layer.render(matrixStack, controlRect, screenRect.intersection(controlRect), mouseX, mouseY);
            matrixStack.popPose();
            
            RenderSystem.disableScissor();
        }
        
        if (layers.isEmpty())
            return;
        
        GuiLayer layer = getTopLayer();
        GuiTooltipEvent event = layer.getTooltipEvent(mouseX - listener.getOffsetX(), mouseY - listener.getOffsetY());
        if (event != null) {
            layer.raiseEvent(event);
            if (!event.isCanceled())
                GuiUtils.drawHoveringText(matrixStack, event.tooltip, mouseX, mouseY, width, height, -1, Minecraft.getInstance().font);
        }
    }
    
    @Override
    public default void moveBehind(GuiControl toMove, GuiControl reference) {
        List<GuiLayer> layers = getLayers();
        layers.remove(toMove);
        int index = layers.indexOf(reference);
        if (index != -1 && index < layers.size() - 1)
            layers.add(index + 1, (GuiLayer) toMove);
        else
            moveBottom(toMove);
    }
    
    @Override
    public default void moveInFront(GuiControl toMove, GuiControl reference) {
        List<GuiLayer> layers = getLayers();
        layers.remove(toMove);
        int index = layers.indexOf(reference);
        if (index != -1)
            layers.add(index, (GuiLayer) toMove);
        else
            moveTop(toMove);
    }
    
    @Override
    public default void moveTop(GuiControl toMove) {
        List<GuiLayer> layers = getLayers();
        layers.remove(toMove);
        layers.add(0, (GuiLayer) toMove);
    }
    
    @Override
    public default void moveBottom(GuiControl toMove) {
        List<GuiLayer> layers = getLayers();
        layers.remove(toMove);
        layers.add((GuiLayer) toMove);
    }
    
    @Override
    public default void raiseEvent(GuiEvent event) {}
    
    @Override
    public default void initiateLayoutUpdate() {}
    
    public void openLayer(GuiLayer layer);
    
    public void closeLayer(int layer);
}
