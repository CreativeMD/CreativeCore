package team.creative.creativecore.common.gui.sync;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

public abstract class GuiSyncHolder {
    
    public static GuiSync followPath(String path, IGuiIntegratedParent parent) {
        GuiSyncHolder holder;
        String holderName;
        if (path.startsWith("global:")) {
            holder = GLOBAL;
            holderName = path.substring(7);
        } else {
            int index = path.indexOf(':');
            String syncpath = path.substring(0, index);
            holderName = path.substring(index + 1);
            GuiControl control = parent.get(syncpath);
            if (control instanceof GuiLayer layer)
                holder = layer.getSyncHolder();
            else
                holder = null;
        }
        
        if (holder == null)
            throw new RuntimeException("Could not find holder for " + path);
        
        GuiSync sync = holder.get(holderName);
        if (sync == null)
            throw new RuntimeException("Could not find sync for " + path);
        return sync;
    }
    
    public static final GuiSyncHolder GLOBAL = new GuiSyncHolderGlobal();
    
    protected final NamedHandlerRegistry<GuiSync> REGISTRY = new NamedHandlerRegistry<GuiSync>(null);
    
    public abstract String path();
    
    public GuiSync get(String id) {
        return REGISTRY.get(id);
    }
    
    public static class GuiSyncHolderGlobal extends GuiSyncHolder {
        
        public <C extends GuiControl, T extends Tag> GuiSyncGlobal<C, T> register(String id, BiConsumer<C, T> consumer) {
            GuiSyncGlobal<C, T> sync = new GuiSyncGlobal<C, T>(this, id, consumer);
            REGISTRY.register(id, sync);
            return sync;
        }
        
        @Override
        public String path() {
            return "global:";
        }
        
    }
    
    public static class GuiSyncHolderLayer extends GuiSyncHolder {
        
        public final GuiLayer parent;
        
        public GuiSyncHolderLayer(GuiLayer layer) {
            this.parent = layer;
        }
        
        public <T extends Tag> GuiSyncLocal<T> register(String id, Consumer<T> consumer) {
            GuiSyncLocal<T> sync = new GuiSyncLocal<T>(this, id, consumer);
            REGISTRY.register(id, sync);
            return sync;
        }
        
        @Override
        public String path() {
            return parent.getNestedName() + ":";
        }
        
    }
    
}
