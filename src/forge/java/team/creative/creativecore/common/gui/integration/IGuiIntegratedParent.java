package team.creative.creativecore.common.gui.integration;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent.Render.Background;
import net.minecraftforge.common.MinecraftForge;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiIntegratedParent extends IGuiParent {
    
    public GuiLayer EMPTY = new GuiLayer("empty") {
        
        @Override
        public void create() {}
    };
    
    public HolderLookup.Provider provider();
    
    public List<GuiLayer> getLayers();
    
    public GuiLayer getTopLayer();
    
    public default boolean isOpen(Class<? extends GuiLayer> clazz) {
        for (GuiLayer layer : getLayers())
            if (clazz.isInstance(layer))
                return true;
        return false;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void render(GuiGraphics graphics, Screen screen, ScreenEventListener listener, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        int width = screen.width;
        int height = screen.height;
        
        listener.tick();
        Rect screenRect = Rect.getScreenRect();
        
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        List<GuiLayer> layers = getLayers();
        for (int i = 0; i < layers.size(); i++) {
            GuiLayer layer = layers.get(i);
            
            if (i == layers.size() - 1) {
                RenderSystem.disableDepthTest();
                if (layer.hasGrayBackground())
                    GuiRenderHelper.verticalGradientRect(pose, 0, 0, width, height, -1072689136, -804253680);
                if (screen instanceof AbstractContainerScreen)
                    MinecraftForge.EVENT_BUS.post(new Background((AbstractContainerScreen<?>) screen, graphics, mouseX, mouseY));
            }
            
            pose.pushPose();
            int offX = (width - layer.getWidth()) / 2;
            int offY = (height - layer.getHeight()) / 2;
            pose.translate(offX, offY, 0);
            
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Rect controlRect = new Rect(offX, offY, offX + layer.getWidth(), offY + layer.getHeight());
            layer.render(graphics, null, controlRect, screenRect.intersection(controlRect), 1, mouseX, mouseY);
            pose.popPose();
            
            RenderSystem.disableScissor();
        }
        
        if (layers.isEmpty())
            return;
        
        GuiLayer layer = getTopLayer();
        GuiTooltipEvent event = layer.getTooltipEvent(null, mouseX - listener.getOffsetX(), mouseY - listener.getOffsetY());
        if (event != null) {
            layer.raiseEvent(event);
            if (!event.isCanceled())
                graphics.renderTooltip(Minecraft.getInstance().font, event.tooltip, Optional.empty(), mouseX, mouseY);
        }
    }
    
    @Override
    public default void raiseEvent(GuiEvent event) {}
    
    @Override
    public default void reflow() {}
    
    public void openLayer(GuiLayer layer);
    
    public void closeLayer(int layer);
    
    @Override
    public default boolean isParent(IGuiParent parent) {
        return parent == this;
    }
    
    @Override
    public default boolean hasGui() {
        return true;
    }
    
    public default GuiControl get(String control) {
        for (GuiLayer layer : getLayers())
            if (control.startsWith(layer.getNestedName()))
                if (control.equals(layer.getNestedName()))
                    return layer;
                else
                    return layer.get(control.substring(layer.getNestedName().length() + 1));
        return null;
    }
    
    @Override
    public default Rect toScreenRect(GuiControl control, Rect rect) {
        if (control instanceof GuiLayer layer) {
            int offX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - layer.getWidth()) / 2;
            int offY = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - layer.getHeight()) / 2;
            rect.move(offX, offY);
        }
        return rect;
    }
    
    @Override
    public default Rect toLayerRect(GuiControl control, Rect rect) {
        return rect;
    }
    
    @Override
    public default IGuiIntegratedParent getIntegratedParent() {
        return this;
    }
    
    public void send(CreativePacket message);
    
}
