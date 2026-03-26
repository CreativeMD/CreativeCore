package team.creative.creativecore.client.gui.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.client.gui.IGuiClientIntegratedParent;
import team.creative.creativecore.client.gui.registry.GuiClientRegistry;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerDist;
import team.creative.creativecore.common.network.CreativePacket;

public class GuiScreenIntegration extends Screen implements IGuiClientIntegratedParent, IScaleableGuiScreen {
    
    public final Minecraft mc = Minecraft.getInstance();
    private List<GuiLayer> layers = new ArrayList<>();
    protected ScreenEventListener listener;
    
    public GuiScreenIntegration(GuiLayer layer) {
        super(Component.literal("gui-api"));
        layer.setParent(this);
        this.layers.add(layer);
        layer.init();
    }
    
    @Override
    protected void init() {
        if (listener == null)
            listener = new ScreenEventListener(this, this);
        this.addWidget(listener);
    }
    
    @Override
    protected void rebuildWidgets() {
        super.rebuildWidgets();
        for (GuiLayer layer : layers)
            layer.reflow();
    }
    
    @Override
    public int getWidth() {
        int width = 0;
        for (GuiLayer layer : layers)
            width = Math.max(width, ((GuiClientLayer) layer.dist()).getWidth());
        return width;
    }
    
    @Override
    public int getHeight() {
        int height = 0;
        for (GuiLayer layer : layers)
            height = Math.max(height, ((GuiClientLayer) layer.dist()).getHeight());
        return height;
    }
    
    @Override
    public void clientTick() {
        for (GuiLayer layer : layers)
            layer.tick();
    }
    
    @Override
    public void removed() {
        for (GuiLayer layer : layers)
            layer.closed();
    }
    
    @Override
    public boolean isContainer() {
        return false;
    }
    
    @Override
    public boolean isClient() {
        return true;
    }
    
    @Override
    public Player getPlayer() {
        return mc.player;
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        IGuiClientIntegratedParent.render(this, graphics, this, listener, mouseX, mouseY);
    }
    
    @Override
    public List<GuiLayer> getLayers() {
        return layers;
    }
    
    @Override
    public GuiLayer getTopLayer() {
        if (layers.isEmpty())
            return ContainerIntegration.EMPTY_CLIENT;
        return layers.get(layers.size() - 1);
    }
    
    @Override
    public void openLayer(GuiLayer layer) {
        layer.setParent(this);
        layers.add(layer);
        layer.init();
    }
    
    @Override
    public void closeLayer(GuiLayer layer) {
        int index = layers.indexOf(layer);
        if (index != -1)
            closeLayer(index);
    }
    
    @Override
    public void closeLayer(int layer) {
        for (int i = layer; i < layers.size(); i++)
            layers.get(i).closed();
        layers = layers.subList(0, layer);
        if (layers.isEmpty())
            onClose();
        else
            getTopLayer().becameTopLayer();
    }
    
    @Override
    public void closeTopLayer() {
        closeLayer(layers.size() - 1);
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        listener.mouseMoved(x, y);
    }
    
    @Override
    public boolean keyPressed(KeyEvent key) {
        if (listener.keyPressed(key))
            return true;
        return super.keyPressed(key);
    }
    
    @Override
    public boolean keyReleased(KeyEvent key) {
        if (listener.keyReleased(key))
            return true;
        return super.keyReleased(key);
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (listener.charTyped(event))
            return true;
        return super.charTyped(event);
    }
    
    @Override
    public void send(CreativePacket message) {}
    
    @Override
    public Provider provider() {
        if (mc.player != null)
            return mc.player.registryAccess();
        return null;
    }
    
    @Override
    public GuiControlDistHandler createDist(GuiControl control) {
        return GuiClientRegistry.create(control);
    }
    
    @Override
    public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
        return (T) GuiClientRegistry.create(manager);
    }
    
}
