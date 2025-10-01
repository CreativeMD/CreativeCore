package team.creative.creativecore.client.gui.integration;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.client.gui.IGuiClientIntegratedParent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerDist;
import team.creative.creativecore.common.network.CreativePacket;

public class ContainerScreenIntegration extends AbstractContainerScreen<ContainerIntegration> implements IScaleableGuiScreen, IGuiClientIntegratedParent {
    
    protected ScreenEventListener listener;
    
    public ContainerScreenIntegration(ContainerIntegration screenContainer, Inventory inv) {
        super(screenContainer, inv, Component.literal("gui-api"));
        listener = new ScreenEventListener(this.getMenu(), this);
        screenContainer.setScreen(this);
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
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return this.getFocused() != null && this.isDragging() && event.button() == 0 && this.getFocused().mouseDragged(event, dragX, dragY);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        IGuiClientIntegratedParent.render(getMenu(), graphics, this, listener, mouseX, mouseY);
        
        // Update dimensions for JEI
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (GuiLayer layer : getMenu().getLayers()) {
            GuiClientLayer c = (GuiClientLayer) layer.dist();
            int offsetX = (width - c.getWidth()) / 2;
            int offsetY = (height - c.getHeight()) / 2;
            minX = Math.min(minX, offsetX + c.rect.getX());
            minY = Math.min(minY, offsetY + c.rect.getY());
            maxX = Math.max(maxX, offsetX + c.rect.getRight());
            maxY = Math.max(maxY, offsetY + c.rect.getBottom());
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
            width = Math.max(width, ((GuiClientLayer) layer.dist()).getWidth());
        return width;
    }
    
    @Override
    public int getHeight() {
        int height = 0;
        for (GuiLayer layer : getMenu().getLayers())
            height = Math.max(height, ((GuiClientLayer) layer.dist()).getHeight());
        return height;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {}
    
    @Override
    public void mouseMoved(double x, double y) {
        listener.mouseMoved(x, y);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listener.keyPressed(event))
            return true;
        return super.keyPressed(event);
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (listener.keyReleased(event))
            return true;
        return super.keyReleased(event);
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (listener.charTyped(event))
            return true;
        return super.charTyped(event);
    }
    
    @Override
    public Provider provider() {
        return menu.provider();
    }
    
    @Override
    public List<GuiLayer> getLayers() {
        return menu.getLayers();
    }
    
    @Override
    public GuiLayer getTopLayer() {
        return menu.getTopLayer();
    }
    
    @Override
    public void openLayer(GuiLayer layer) {
        menu.openLayer(layer);
    }
    
    @Override
    public void closeLayer(int layer) {
        menu.closeLayer(layer);
    }
    
    @Override
    public void send(CreativePacket message) {
        menu.send(message);
    }
    
    @Override
    public boolean isContainer() {
        return menu.isContainer();
    }
    
    @Override
    public boolean isClient() {
        return menu.isClient();
    }
    
    @Override
    public Player getPlayer() {
        return menu.getPlayer();
    }
    
    @Override
    public void closeTopLayer() {
        menu.closeTopLayer();
    }
    
    @Override
    public void closeLayer(GuiLayer layer) {
        menu.closeLayer(layer);
    }
    
    @Override
    public GuiControlDistHandler createDist(GuiControl control) {
        return menu.createDist(control);
    }
    
    @Override
    public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
        return menu.createDist(manager);
    }
    
}
