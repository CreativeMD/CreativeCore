package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.client.gui.registry.GuiClientRegistry;
import team.creative.creativecore.common.gui.control.inventory.IGuiInventory;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManager.GuiManagerType;
import team.creative.creativecore.common.gui.manager.GuiManagerDist;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.server.gui.registry.GuiServerRegistry;

public abstract class GuiLayer extends GuiParent {
    
    public static final GuiLayerDistination CLIENT = new GuiLayerDistination() {
        
        @Override
        public boolean isClient() {
            return true;
        }
        
        @Override
        public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
            return (T) GuiClientRegistry.create(manager);
        }
        
        @Override
        public GuiControlDistHandler createDist(GuiControl control) {
            return GuiClientRegistry.create(control);
        }
    };
    
    public static final GuiLayerDistination SERVER = new GuiLayerDistination() {
        
        @Override
        public boolean isClient() {
            return false;
        }
        
        @Override
        public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
            return (T) GuiServerRegistry.create(manager);
        }
        
        @Override
        public GuiControlDistHandler createDist(GuiControl control) {
            return GuiServerRegistry.create(control);
        }
    };
    
    protected static void collectInventories(Iterable<GuiControl> parent, List<IGuiInventory> inventories) {
        for (GuiControl control : parent)
            if (control instanceof IGuiInventory i)
                inventories.add(i);
            else if (control instanceof GuiParent p)
                collectInventories(p, inventories);
    }
    
    private final GuiSyncHolderLayer sync = new GuiSyncHolderLayer(this);
    private HashMap<GuiManagerType, GuiManager> managers;
    
    public GuiLayer(boolean client, String name) {
        super(client ? CLIENT : SERVER, name, GuiFlow.STACK_X);
    }
    
    public GuiLayer(boolean client, String name, int width, int height) {
        super(client ? CLIENT : SERVER, name, GuiFlow.STACK_X);
        setDim(width, height);
    }
    
    @Override
    public GuiLayerDistHandler dist() {
        return (GuiLayerDistHandler) super.dist();
    }
    
    public void setParent(IGuiIntegratedParent parent) {
        setParent(this, parent);
    }
    
    public Iterable<IGuiInventory> inventoriesToInsert() {
        List<IGuiInventory> inventories = new ArrayList<>();
        collectInventories(this, inventories);
        return inventories;
    }
    
    public Iterable<IGuiInventory> inventoriesToExtract() {
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
        if (dist() != null)
            dist().reflow();
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        if (dist() != null)
            dist().applyOffset(control, rect);
        return rect;
    }
    
    public static interface GuiLayerDistHandler extends GuiParent.GuiParentDistHandler {
        
        public void reflow();
        
    }
    
    public static abstract class GuiLayerDistination implements IGuiParent {
        
        @Override
        public boolean isContainer() {
            return false;
        }
        
        @Override
        public Player getPlayer() {
            return null;
        }
        
        @Override
        public void closeTopLayer() {}
        
        @Override
        public void closeLayer(GuiLayer layer) {}
        
        @Override
        public void raiseEvent(GuiEvent event) {}
        
        @Override
        public void reflow() {}
        
        @Override
        public boolean hasGui() {
            return false;
        }
        
        @Override
        public boolean isParent(IGuiParent parent) {
            return false;
        }
        
        @Override
        public Rect toScreenRect(GuiControl control, Rect rect) {
            return null;
        }
        
        @Override
        public Rect toLayerRect(GuiControl control, Rect rect) {
            return null;
        }
        
        @Override
        public IGuiIntegratedParent getIntegratedParent() {
            return null;
        }
        
    }
    
}
