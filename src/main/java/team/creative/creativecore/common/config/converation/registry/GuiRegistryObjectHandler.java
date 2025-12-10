package team.creative.creativecore.common.config.converation.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector;
import team.creative.creativecore.common.util.registry.FilteredHandlerRegistry;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public abstract class GuiRegistryObjectHandler {
    
    public static final FilteredHandlerRegistry<Registry, GuiRegistryObjectHandler> REGISTRY = new FilteredHandlerRegistry<Registry, GuiRegistryObjectHandler>(new GuiRegistryObjectHandler() {
        
        @Override
        public void createControls(GuiParent parent, Registry registry) {
            parent.add(new GuiComboBox<Identifier>(parent, "elements", new TextMapBuilder<Identifier>().addComponent(registry.keySet(), x -> {
                if (x.getNamespace().equals(Identifier.DEFAULT_NAMESPACE))
                    return Component.literal(x.getPath());
                return Component.literal(x.toString());
            })));
        }
        
        @Override
        public void loadValue(GuiParent parent, Registry registry, Identifier location) {
            GuiComboBox<Identifier> box = parent.get("elements");
            box.select(location);
        }
        
        @Override
        public Identifier saveValue(GuiParent parent, Registry registry) {
            GuiComboBox<Identifier> box = parent.get("elements");
            return box.selected();
        }
        
    });
    
    static {
        REGISTRY.register(BuiltInRegistries.BLOCK, new GuiRegistryObjectHandler() {
            
            @Override
            public void createControls(GuiParent parent, Registry registry) {
                parent.add(new GuiStackSelector(parent, "content", new GuiStackSelector.CreativeCollector(new GuiStackSelector.GuiBlockSelector())));
            }
            
            @Override
            public void loadValue(GuiParent parent, Registry registry, Identifier location) {
                GuiStackSelector selector = parent.get("content");
                Block block = (Block) registry.getValue(location);
                selector.setSelectedForce(new ItemStack(block));
            }
            
            @Override
            public Identifier saveValue(GuiParent parent, Registry registry) {
                GuiStackSelector selector = parent.get("content");
                Block block = Block.byItem(selector.getSelected().getItem());
                return block.builtInRegistryHolder().unwrapKey().get().identifier();
            }
            
        });
    }
    
    public abstract void createControls(GuiParent parent, Registry registry);
    
    public abstract void loadValue(GuiParent parent, Registry registry, Identifier location);
    
    public abstract Identifier saveValue(GuiParent parent, Registry registry);
    
}
