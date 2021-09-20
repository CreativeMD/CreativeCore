package team.creative.creativecore.common.gui.integration;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class ContainerScreenIntegration extends AbstractContainerScreen<ContainerIntegration> implements IScaleableGuiScreen {
    
    protected ScreenEventListener listener;
    
    public ContainerScreenIntegration(ContainerIntegration screenContainer, Inventory inv) {
        super(screenContainer, inv, new TextComponent("gui-api"));
        listener = new ScreenEventListener(this.getMenu(), this);
    }
    
    @Override
    protected void init() {
        this.addWidget(listener);
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        getMenu().render(stack, this, listener, mouseX, mouseY);
    }
    
    @Override
    public void clientTick() {
        for (GuiLayer layer : getMenu().getLayers())
            layer.tick();
    }
    
    @Override
    public int getWidth() {
        int width = 0;
        for (GuiLayer layer : getMenu().getLayers())
            width = Math.max(width, layer.getWidth());
        return width;
    }
    
    @Override
    public int getHeight() {
        int height = 0;
        for (GuiLayer layer : getMenu().getLayers())
            height = Math.max(height, layer.getHeight());
        return height;
    }
    
    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int x, int y) {}
    
    @Override
    public void mouseMoved(double x, double y) {
        listener.mouseMoved(x, y);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listener.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (listener.keyReleased(keyCode, scanCode, modifiers))
            return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (listener.charTyped(codePoint, modifiers))
            return true;
        return super.charTyped(codePoint, modifiers);
    }
    
}
