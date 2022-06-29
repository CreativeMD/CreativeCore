package team.creative.creativecore.common.gui.integration;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;

public class GuiScreenIntegration extends Screen implements IGuiIntegratedParent, IScaleableGuiScreen {
    
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
    public int getWidth() {
        int width = 0;
        for (GuiLayer layer : layers)
            width = Math.max(width, layer.getWidth());
        return width;
    }
    
    @Override
    public int getHeight() {
        int height = 0;
        for (GuiLayer layer : layers)
            height = Math.max(height, layer.getHeight());
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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        render(stack, this, listener, mouseX, mouseY);
    }
    
    @Override
    public List<GuiLayer> getLayers() {
        return layers;
    }
    
    @Override
    public GuiLayer getTopLayer() {
        if (layers.isEmpty())
            return EMPTY;
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
        for (int i = 0; i <= layer; i++)
            layers.get(i).closed();
        layers = layers.subList(0, layer);
        if (layers.isEmpty())
            onClose();
    }
    
    @Override
    public void closeTopLayer() {
        closeLayer(layers.size() - 1);
    }
    
    @Override
    public GuiLayer openLayer(LayerOpenPacket packet) {
        packet.executeClient(getPlayer());
        return layers.get(layers.size() - 1);
    }
    
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
