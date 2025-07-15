package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import team.creative.creativecore.common.gui.GuiLayer.GuiLayerDistHandler;
import team.creative.creativecore.common.gui.control.inventory.IGuiInventory;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManager.GuiManagerType;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;

public abstract class GuiLayer<D extends GuiLayerDistHandler> extends GuiParent<D> {
    
    protected static void collectInventories(Iterable<GuiControl> parent, List<IGuiInventory> inventories) {
        for (GuiControl control : parent)
            if (control instanceof IGuiInventory i)
                inventories.add(i);
            else if (control instanceof GuiParent p)
                collectInventories(p, inventories);
    }
    
    private final GuiSyncHolderLayer sync = new GuiSyncHolderLayer(this);
    private HashMap<GuiManagerType, GuiManager> managers;
    
    public GuiLayer(IGuiParent parent, String name) {
        super(parent, name, GuiFlow.STACK_X);
    }
    
    public GuiLayer(IGuiParent parent, String name, int width, int height) {
        super(parent, name, GuiFlow.STACK_X);
        setDim(width, height);
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
    
    public abstract void create();
    
    /** called when a layer is removed and this layer is the new top layer */
    public void becameTopLayer() {}
    
    @Override
    public GuiLayer getLayer() {
        return this;
    }
    
    public boolean closeLayerUsingEscape() {
        return true;
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
    public void reflow() {
        dist.reflow();
    }
    
    public static interface GuiLayerDistHandler extends GuiParent.GuiParentDistHandler {
        
        public void reflow();
        
    }
    
}
