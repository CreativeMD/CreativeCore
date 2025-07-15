package team.creative.creativecore.common.gui.integration;

import java.util.List;
import java.util.Optional;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.HolderLookup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Background;
import net.neoforged.neoforge.common.NeoForge;
import team.creative.creativecore.client.gui.integration.ScreenEventListener;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
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
        Matrix3x2fStack pose = graphics.pose();
        int width = screen.width;
        int height = screen.height;
        
        listener.tick();
        Rect screenRect = Rect.getScreenRect();
        
        List<GuiLayer> layers = getLayers();
        for (int i = 0; i < layers.size(); i++) {
            GuiLayer layer = layers.get(i);
            
            if (i == layers.size() - 1) {
                if (layer.hasGrayBackground())
                    ((CreativeGuiGraphics) graphics).verticalGradientRect(0, 0, width, height, -1072689136, -804253680);
                if (screen instanceof AbstractContainerScreen)
                    NeoForge.EVENT_BUS.post(new Background((AbstractContainerScreen<?>) screen, graphics, mouseX, mouseY));
            }
            
            pose.pushMatrix();
            int offX = (width - layer.getWidth()) / 2;
            int offY = (height - layer.getHeight()) / 2;
            pose.translate(offX, offY);
            
            Rect controlRect = new Rect(offX, offY, offX + layer.getWidth(), offY + layer.getHeight());
            layer.render(graphics, controlRect, screenRect.intersection(controlRect), 1, mouseX, mouseY);
            pose.popMatrix();
            
            RenderSystem.disableScissorForRenderTypeDraws();
        }
        
        if (layers.isEmpty())
            return;
        
        GuiLayer layer = getTopLayer();
        GuiTooltipEvent event = layer.getTooltipEvent(mouseX - listener.getOffsetX(), mouseY - listener.getOffsetY());
        if (event != null) {
            layer.raiseEvent(event);
            if (!event.isCanceled()) {
                var font = Minecraft.getInstance().font;
                List<ClientTooltipComponent> list = ClientHooks.gatherTooltipComponents(null, event.tooltip, Optional.empty(), mouseX, graphics.guiWidth(), graphics.guiHeight(),
                    font);
                graphics.renderTooltip(font, list, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
            }
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
