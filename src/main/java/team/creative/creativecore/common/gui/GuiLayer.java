package team.creative.creativecore.common.gui;

import java.util.HashMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManager.GuiManagerType;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiLayer extends GuiParent {
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public GuiStyle style;
    public final Rect rect;
    private final GuiSyncHolderLayer sync = new GuiSyncHolderLayer(this);
    private HashMap<GuiManagerType, GuiManager> managers;
    
    public GuiLayer(String name) {
        super(name, GuiFlow.STACK_X);
        this.rect = new Rect(0, 0, 0, 0);
        if (CreativeCore.loader().getOverallSide().isClient())
            this.style = GuiStyle.getStyle(name);
    }
    
    public GuiLayer(String name, int width, int height) {
        super(name, GuiFlow.STACK_X, width, height);
        this.rect = new Rect(0, 0, width, height);
        if (CreativeCore.loader().getOverallSide().isClient())
            this.style = GuiStyle.getStyle(name);
    }
    
    public boolean has(GuiManagerType type) {
        return managers.containsKey(type);
    }
    
    public <T extends GuiManager> T getOrCreate(GuiManagerType<T> type) {
        T manager = (T) managers.get(type);
        if (manager == null)
            managers.put(type, manager = type.factory().apply(this));
        return manager;
    }
    
    public GuiManagerItem itemManager() {
        return getOrCreate(GuiManager.ITEM);
    }
    
    public GuiSyncHolderLayer getSyncHolder() {
        return sync;
    }
    
    public int getWidth() {
        return (int) rect.getWidth();
    }
    
    public int getHeight() {
        return (int) rect.getHeight();
    }
    
    @Override
    public void init() {
        createSync(sync);
        create();
        super.init();
        reflow();
    }
    
    public void reinit() {
        super.init();
        reflow();
    }
    
    @Override
    public void reflow() {
        if (CreativeCore.loader().getOverallSide().isServer())
            return;
        
        if (!hasPreferredDimensions) {
            rect.maxX = preferredWidth() + getContentOffset() * 2;
            flowX((int) rect.getWidth() - getContentOffset() * 2, preferredWidth());
            rect.maxY = preferredHeight() + getContentOffset() * 2;
            flowY((int) rect.getHeight() - getContentOffset() * 2, preferredHeight());
        } else {
            flowX((int) rect.getWidth() - getContentOffset() * 2, preferredWidth());
            flowY((int) rect.getHeight() - getContentOffset() * 2, preferredHeight());
        }
    }
    
    public abstract void create();
    
    public void createSync(GuiSyncHolder holder) {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.GUI;
    }
    
    @Override
    public String getNestedName() {
        return "gui." + super.getNestedName();
    }
    
    @Override
    public GuiLayer getLayer() {
        return this;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public GuiStyle getStyle() {
        return style;
    }
    
    public boolean closeLayerUsingEscape() {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Options getSettings() {
        return Minecraft.getInstance().options;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean hasGrayBackground() {
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (closeLayerUsingEscape())
                closeTopLayer();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;
        if (getSettings().keyInventory.matches(keyCode, scanCode)) {
            closeTopLayer();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasLayer() {
        return true;
    }
}
