package team.creative.creativecore.common.gui.integration;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class ContainerScreenIntegration extends AbstractContainerScreen<ContainerIntegration> implements IScaleableGuiScreen {
    
    protected ScreenEventListener listener;
    
    public ContainerScreenIntegration(ContainerIntegration screenContainer, Inventory inv) {
        super(screenContainer, inv, Component.literal("gui-api"));
        listener = new ScreenEventListener(this.getMenu(), this);
    }
    
    @Override
    protected void init() {
        this.addWidget(listener);
    }
    
    @Override
    protected void rebuildWidgets() {
        super.rebuildWidgets();
        for (GuiLayer layer : getMenu().getLayers())
            layer.reflow();
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        return this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(x, y, button, dragX, dragY);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        getMenu().render(graphics, this, listener, mouseX, mouseY);
        
        // Update dimensions for JEI
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (GuiLayer layer : getMenu().getLayers()) {
            int offsetX = (width - layer.getWidth()) / 2;
            int offsetY = (height - layer.getHeight()) / 2;
            minX = Math.min(minX, offsetX + (int) layer.rect.minX);
            minY = Math.min(minY, offsetY + (int) layer.rect.minY);
            maxX = Math.max(maxX, offsetX + (int) layer.rect.maxX);
            maxY = Math.max(maxY, offsetY + (int) layer.rect.maxY);
        }
        
        leftPos = minX;
        topPos = minY;
        imageWidth = maxX - minX;
        imageHeight = maxY - minY;
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
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {}
    
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
