package team.creative.creativecore.common.gui.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.packet.LayerClosePacket;
import team.creative.creativecore.common.network.CreativePacket;

public class ContainerIntegration extends AbstractContainerMenu implements IGuiIntegratedParent {
    
    private List<GuiLayer> layers = new ArrayList<>();
    private final Player player;
    
    public ContainerIntegration(MenuType<ContainerIntegration> type, int id, Player player, GuiLayer layer) {
        super(type, id);
        this.player = player;
        layer.setParent(this);
        this.layers.add(layer);
    }
    
    public ContainerIntegration(MenuType<ContainerIntegration> type, int id, Player player) {
        super(type, id);
        this.player = player;
    }
    
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        tick();
    }
    
    public void tick() {
        for (GuiLayer layer : layers)
            layer.tick();
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
    public boolean isContainer() {
        return true;
    }
    
    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        for (GuiLayer layer : layers)
            layer.closed();
    }
    
    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public boolean isClient() {
        return player.level.isClientSide;
    }
    
    @Override
    public void openLayer(GuiLayer layer) {
        layer.setParent(this);
        layers.add(layer);
        layer.init();
    }
    
    @Override
    public void closeTopLayer() {
        int index = layers.size() - 1;
        if (index != -1)
            closeLayer(index);
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
        send(new LayerClosePacket(layer));
        layers = layers.subList(0, layer);
        if (layers.isEmpty())
            if (isClient())
                Minecraft.getInstance().setScreen(null);
            else
                player.closeContainer();
        else
            getTopLayer().becameTopLayer();
    }
    
    @Override
    public void send(CreativePacket packet) {
        if (isClient())
            CreativeCore.NETWORK.sendToServer(packet);
        else
            CreativeCore.NETWORK.sendToClient(packet, (ServerPlayer) player);
    }
    
    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }
    
}
