package team.creative.creativecore.common.gui.sync;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
        
        GuiSync sync = holder.getSync(holderName);
        if (sync == null)
            throw new RuntimeException("Could not find sync for " + path);
        return sync;
    }
    
    public static GuiSyncControl followPathControl(String path, IGuiIntegratedParent parent) {
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
        
        GuiSyncControl sync = holder.getControlSync(holderName);
        if (sync == null)
            throw new RuntimeException("Could not find sync for " + path);
        return sync;
    }
    
    public static final GuiSyncHolderGlobal GLOBAL = new GuiSyncHolderGlobal();
    
    public abstract String path();
    
    public abstract GuiSync getSync(String id);
    
    public abstract GuiSyncControl getControlSync(String id);
    
    public static class GuiSyncHolderGlobal extends GuiSyncHolder {
        
        protected final NamedHandlerRegistry<GuiSync> SYNC_REGISTRY = new NamedHandlerRegistry<GuiSync>(null);
        
        protected final NamedHandlerRegistry<GuiSyncControl> SYNC_CONTROL_REGISTRY = new NamedHandlerRegistry<GuiSyncControl>(null);
        
        public <C extends GuiControl, T extends Tag> GuiSyncGlobal<C, T> register(String id, BiConsumer<C, T> consumer) {
            GuiSyncGlobal<C, T> sync = new GuiSyncGlobal<C, T>(this, id, consumer);
            SYNC_CONTROL_REGISTRY.register(id, sync);
            return sync;
        }
        
        public <T extends GuiLayer> GuiSyncGlobalLayer<T> layer(String id, BiFunction<HolderLookup.Provider, CompoundTag, T> creator) {
            GuiSyncGlobalLayer<T> sync = new GuiSyncGlobalLayer<T>(this, id, creator);
            SYNC_REGISTRY.register(id, sync);
            return sync;
        }
        
        @Override
        public String path() {
            return "global:";
        }
        
        @Override
        public GuiSync getSync(String id) {
            return SYNC_REGISTRY.get(id);
        }
        
        @Override
        public GuiSyncControl getControlSync(String id) {
            return SYNC_CONTROL_REGISTRY.get(id);
        }
        
    }
    
    public static class GuiSyncHolderLayer extends GuiSyncHolder {
        
        protected final NamedHandlerRegistry<GuiSyncControl> SYNC_CONTROL_REGISTRY = new NamedHandlerRegistry<GuiSyncControl>(null);
        
        public final GuiLayer parent;
        
        public GuiSyncHolderLayer(GuiLayer layer) {
            this.parent = layer;
        }
        
        public <T extends Tag> GuiSyncLocal<T> register(String id, Consumer<T> consumer) {
            GuiSyncLocal<T> sync = new GuiSyncLocal<T>(this, id, consumer);
            SYNC_CONTROL_REGISTRY.register(id, sync);
            return sync;
        }
        
        public <T extends GuiLayer> GuiSyncLocalLayer<T> layer(String id, Function<CompoundTag, T> creator) {
            GuiSyncLocalLayer<T> sync = new GuiSyncLocalLayer<T>(this, id, creator);
            SYNC_CONTROL_REGISTRY.register(id, sync);
            return sync;
        }
        
        @Override
        public String path() {
            return parent.getNestedName() + ":";
        }
        
        @Override
        public GuiSync getSync(String id) {
            return null;
        }
        
        @Override
        public GuiSyncControl getControlSync(String id) {
            return SYNC_CONTROL_REGISTRY.get(id);
        }
        
    }
    
}
