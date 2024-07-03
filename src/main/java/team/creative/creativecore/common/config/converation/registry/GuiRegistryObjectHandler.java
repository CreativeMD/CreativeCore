package team.creative.creativecore.common.config.converation.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.collection.GuiStackSelector;
import team.creative.creativecore.common.util.registry.FilteredHandlerRegistry;
import team.creative.creativecore.common.util.text.TextMapBuilder;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
public abstract class GuiRegistryObjectHandler {
    
    public static final FilteredHandlerRegistry<Registry, GuiRegistryObjectHandler> REGISTRY = new FilteredHandlerRegistry<Registry, GuiRegistryObjectHandler>(new GuiRegistryObjectHandler() {
        
        @Override
        public void createControls(GuiParent parent, Registry registry) {
            parent.add(new GuiComboBoxMapped<ResourceLocation>("elements", new TextMapBuilder<ResourceLocation>().addComponent(registry.keySet(), x -> {
                if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                    return Component.literal(x.getPath());
                return Component.literal(x.toString());
            })));
        }
        
        @Override
        public void loadValue(GuiParent parent, Registry registry, ResourceLocation location) {
            GuiComboBoxMapped<ResourceLocation> box = parent.get("elements");
            box.select(location);
        }
        
        @Override
        public ResourceLocation saveValue(GuiParent parent, Registry registry) {
            GuiComboBoxMapped<ResourceLocation> box = parent.get("elements");
            return box.getSelected();
        }
        
    });
    
    static {
        REGISTRY.register(BuiltInRegistries.BLOCK, new GuiRegistryObjectHandler() {
            
            @Override
            public void createControls(GuiParent parent, Registry registry) {
                parent.add(new GuiStackSelector("content", null, new GuiStackSelector.CreativeCollector(new GuiStackSelector.GuiBlockSelector())));
            }
            
            @Override
            public void loadValue(GuiParent parent, Registry registry, ResourceLocation location) {
                GuiStackSelector selector = parent.get("content");
                Block block = (Block) registry.get(location);
                selector.setSelectedForce(new ItemStack(block));
            }
            
            @Override
            public ResourceLocation saveValue(GuiParent parent, Registry registry) {
                GuiStackSelector selector = parent.get("content");
                Block block = Block.byItem(selector.getSelected().getItem());
                return block.builtInRegistryHolder().unwrapKey().get().location();
            }
            
        });
    }
    
    public abstract void createControls(GuiParent parent, Registry registry);
    
    public abstract void loadValue(GuiParent parent, Registry registry, ResourceLocation location);
    
    public abstract ResourceLocation saveValue(GuiParent parent, Registry registry);
    
}
