package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.controls.inventory.IGuiInventory;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManager.GuiManagerType;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiLayer extends GuiParent {
    
    public static final int MINIMUM_LAYER_SPACING = 10;
    
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
        super(name, GuiFlow.STACK_X);
        setDim(width, height);
        this.rect = new Rect(0, 0, width, height);
        if (CreativeCore.loader().getOverallSide().isClient())
            this.style = GuiStyle.getStyle(name);
    }
    
    protected static void collectInventories(Iterable<GuiChildControl> parent, List<IGuiInventory> inventories) {
        for (GuiChildControl child : parent) {
            if (child.control instanceof IGuiInventory)
                inventories.add((IGuiInventory) child.control);
            else if (child.control instanceof GuiParent)
                collectInventories((GuiParent) child.control, inventories);
        }
    }
    
    public Iterable<IGuiInventory> inventoriesToInsert() {
        List<IGuiInventory> inventories = new ArrayList<>();
        collectInventories(this, inventories);
        return inventories;
    }
    
    public Iterable<IGuiInventory> inventoriesToExract() {
        List<IGuiInventory> inventories = new ArrayList<>();
        collectInventories(this, inventories);
        Collections.reverse(inventories);
        return inventories;
    }
    
    public boolean has(GuiManagerType type) {
        if (managers == null)
            return false;
        return managers.containsKey(type);
    }
    
    public <T extends GuiManager> T getOrCreate(GuiManagerType<T> type) {
        if (managers == null)
            managers = new HashMap<>();
        T manager = (T) managers.get(type);
        if (manager == null)
            managers.put(type, manager = type.factory().apply(this));
        return manager;
    }
    
    public GuiManagerItem itemManager() {
        return getOrCreate(GuiManager.ITEM);
    }
    
    public Iterable<GuiManager> managers() {
        if (managers == null)
            return Collections.EMPTY_LIST;
        return managers.values();
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
        
        Rect screen = Rect.getScreenRect();
        int screenWidth = (int) screen.getWidth() - getContentOffset() * 2 - MINIMUM_LAYER_SPACING;
        int preferredWidth = -1;
        
        if (preferred != null)
            preferredWidth = preferred.preferredWidth(this, screenWidth);
        if (preferredWidth == -1)
            if (isExpandableX())
                preferredWidth = screenWidth;
            else
                preferredWidth = preferredWidth(screenWidth);
        if (preferred != null) {
            int minWidth = preferred.minWidth(this, screenWidth);
            if (minWidth != -1)
                preferredWidth = Math.max(preferredWidth, minWidth);
            int maxWidth = preferred.maxWidth(this, screenWidth);
            if (maxWidth != -1)
                preferredWidth = Math.min(preferredWidth, maxWidth);
        }
        rect.maxX = preferredWidth + getContentOffset() * 2;
        flowX(preferredWidth, preferredWidth);
        
        int screenHeight = (int) screen.getHeight() - getContentOffset() * 2 - MINIMUM_LAYER_SPACING;
        int preferredHeight = -1;
        if (preferred != null)
            preferredHeight = preferred.preferredHeight(this, preferredWidth, screenHeight);
        if (preferredHeight == -1)
            if (isExpandableY())
                preferredHeight = screenHeight;
            else
                preferredHeight = preferredHeight((int) rect.getWidth() + getContentOffset() * 2, screenHeight);
        if (preferred != null) {
            int minHeight = preferred.minHeight(this, preferredWidth, screenHeight);
            if (minHeight != -1)
                preferredHeight = Math.max(preferredHeight, minHeight);
            int maxHeight = preferred.maxHeight(this, preferredWidth, screenHeight);
            if (maxHeight != -1)
                preferredHeight = Math.min(preferredHeight, maxHeight);
        }
        rect.maxY = preferredHeight + getContentOffset() * 2;
        flowY(preferredWidth, preferredHeight, preferredHeight(preferredWidth, screenHeight));
    }
    
    public abstract void create();
    
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
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        for (GuiManager manager : managers())
            manager.renderOverlay(matrix, control, rect, mouseX - (int) rect.minX, mouseY - (int) rect.minY);
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
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (!this.rect.inside(x, y) && !isMouseOverHovered(x, y)) {
            looseFocus();
            for (GuiManager manager : managers())
                manager.mouseClickedOutside(x, y);
            return false;
        }
        return super.mouseClicked(rect, x, y, button);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
        
        for (GuiManager manager : managers())
            manager.mouseReleased(x, y, button);
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
    
    @Override
    public void tick() {
        for (GuiManager manager : managers())
            manager.tick();
        super.tick();
    }
    
    @Override
    public void closed() {
        for (GuiManager manager : managers())
            manager.closed();
        super.closed();
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        GuiChildControl child = find(control);
        if (child == null)
            return rect;
        rect.move(child.rect.minX + getOffsetX(), child.rect.minY + getOffsetY());
        return rect;
    }
}
