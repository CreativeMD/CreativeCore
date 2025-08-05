package team.creative.creativecore.client.gui;

import java.util.List;
import java.util.Optional;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.client.gui.integration.ScreenEventListener;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiClientIntegratedParent extends IGuiIntegratedParent {
    
    public static void render(IGuiIntegratedParent parent, GuiGraphics graphics, Screen screen, ScreenEventListener listener, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        int width = screen.width;
        int height = screen.height;
        
        listener.tick();
        Rect screenRect = GuiClientControl.getScreenRect();
        
        List<GuiLayer> layers = parent.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            GuiLayer layer = layers.get(i);
            GuiClientLayer cLayer = (GuiClientLayer) layer.dist();
            
            if (i == layers.size() - 1) {
                if (cLayer.hasGrayBackground())
                    ((CreativeGuiGraphics) graphics).verticalGradientRect(0, 0, width, height, -1072689136, -804253680);
                CreativeCoreClient.postBackgroundEvent(screen, graphics, mouseX, mouseY);
            }
            
            pose.pushMatrix();
            int offX = (width - cLayer.getWidth()) / 2;
            int offY = (height - cLayer.getHeight()) / 2;
            pose.translate(offX, offY);
            
            Rect controlRect = new Rect(offX, offY, offX + cLayer.getWidth(), offY + cLayer.getHeight());
            cLayer.render(graphics, controlRect, screenRect.intersection(controlRect), 1, mouseX, mouseY);
            pose.popMatrix();
            
            RenderSystem.disableScissorForRenderTypeDraws();
        }
        
        if (layers.isEmpty())
            return;
        
        GuiLayer layer = parent.getTopLayer();
        GuiClientLayer cLayer = (GuiClientLayer) layer.dist();
        
        GuiTooltipEvent event = cLayer.getTooltipEvent(mouseX - listener.getOffsetX(), mouseY - listener.getOffsetY());
        if (event != null) {
            layer.raiseEvent(event);
            if (!event.isCanceled()) {
                var font = Minecraft.getInstance().font;
                List<ClientTooltipComponent> list = CreativeCoreClient.gatherTooltipComponents(null, event.tooltip, Optional.empty(), mouseX, graphics.guiWidth(), graphics
                        .guiHeight(), font);
                graphics.renderTooltip(font, list, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
            }
        }
    }
    
    @Override
    public default Rect toScreenRect(GuiControl control, Rect rect) {
        if (control instanceof GuiLayer layer) {
            GuiClientLayer cLayer = (GuiClientLayer) layer.dist();
            int offX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - cLayer.getWidth()) / 2;
            int offY = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - cLayer.getHeight()) / 2;
            rect.move(offX, offY);
        }
        return rect;
    }
    
}
