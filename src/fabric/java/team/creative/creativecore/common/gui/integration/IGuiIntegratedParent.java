package team.creative.creativecore.common.gui.integration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiIntegratedParent extends IGuiParent {
    
    GuiLayer EMPTY = new GuiLayer("empty") {
        
        @Override
        public void create() {}
    };
    
    public HolderLookup.Provider provider();
    
    List<GuiLayer> getLayers();
    
    GuiLayer getTopLayer();
    
    default boolean isOpen(Class<? extends GuiLayer> clazz) {
        for (GuiLayer layer : getLayers())
            if (clazz.isInstance(layer))
                return true;
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public default void render(GuiGraphics graphics, Screen screen, ScreenEventListener listener, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        int width = screen.width;
        int height = screen.height;
        
        listener.tick();
        Rect screenRect = Rect.getScreenRect();
        
        //RenderSystem.enableDepthTest();
        //RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();
        
        List<GuiLayer> layers = getLayers();
        for (int i = 0; i < layers.size(); i++) {
            GuiLayer layer = layers.get(i);
            
            if (i == layers.size() - 1) {
                //RenderSystem.disableDepthTest();
                if (layer.hasGrayBackground())
                    ((CreativeGuiGraphics) graphics).verticalGradientRect(0, 0, width, height, -1072689136, -804253680);
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
                List<ClientTooltipComponent> list = gatherTooltipComponents(null, event.tooltip, Optional.empty(), mouseX, graphics.guiWidth(), graphics.guiHeight(), font);
                graphics.renderTooltip(font, list, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
            }
        }
    }
    
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream().map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left).collect(
            Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));
        return gatherTooltipComponentsFromElements(stack, elements, mouseX, screenWidth, screenHeight, fallbackFont);
    }
    
    public static List<ClientTooltipComponent> gatherTooltipComponentsFromElements(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        Font font = fallbackFont;
        
        // text wrapping
        int tooltipTextWidth = elements.stream().mapToInt(either -> either.map(font::width, component -> 0)).max().orElse(0);
        
        boolean needsWrap = false;
        
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }
        
        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
            return elements.stream().flatMap(either -> either.map(text -> splitLine(text, font, tooltipTextWidthF), component -> Stream.of(ClientTooltipComponent.create(
                component)))).toList();
        }
        return elements.stream().map(either -> either.map(text -> ClientTooltipComponent.create(text instanceof Component c ? c.getVisualOrderText() : Language.getInstance()
                .getVisualOrder(text)), ClientTooltipComponent::create)).toList();
    }
    
    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        if (text instanceof Component component && component.getString().isEmpty()) {
            return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
        }
        return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
    }
    
    public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder) {
        Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
        return (typeOne, typeTwo) -> {
            boolean vanillaOne = renderOrder.contains(typeOne);
            boolean vanillaTwo = renderOrder.contains(typeTwo);
            
            if (vanillaOne && vanillaTwo) {
                return vanillaComparator.compare(typeOne, typeTwo);
            }
            if (!vanillaOne && !vanillaTwo) {
                return Integer.compare(System.identityHashCode(typeOne), System.identityHashCode(typeTwo));
            }
            return vanillaOne ? -1 : 1;
        };
    }
    
    @Override
    default void raiseEvent(GuiEvent event) {}
    
    @Override
    default void reflow() {}
    
    void openLayer(GuiLayer layer);
    
    void closeLayer(int layer);
    
    @Override
    default boolean isParent(IGuiParent parent) {
        return parent == this;
    }
    
    @Override
    default boolean hasGui() {
        return true;
    }
    
    default GuiControl get(String control) {
        for (GuiLayer layer : getLayers())
            if (control.startsWith(layer.getNestedName()))
                if (control.equals(layer.getNestedName()))
                    return layer;
                else
                    return layer.get(control.substring(layer.getNestedName().length() + 1));
        return null;
    }
    
    @Override
    default Rect toScreenRect(GuiControl control, Rect rect) {
        if (control instanceof GuiLayer layer) {
            int offX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - layer.getWidth()) / 2;
            int offY = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - layer.getHeight()) / 2;
            rect.move(offX, offY);
        }
        return rect;
    }
    
    @Override
    default Rect toLayerRect(GuiControl control, Rect rect) {
        return rect;
    }
    
    @Override
    public default IGuiIntegratedParent getIntegratedParent() {
        return this;
    }
    
    public void send(CreativePacket message);
    
}
