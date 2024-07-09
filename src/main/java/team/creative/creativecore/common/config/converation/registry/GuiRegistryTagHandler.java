package team.creative.creativecore.common.config.converation.registry;

import java.util.ArrayList;
import java.util.Iterator;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.collection.GuiStackSelector;
import team.creative.creativecore.common.util.registry.FilteredHandlerRegistry;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.map.HashMapList;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
public abstract class GuiRegistryTagHandler {
    
    public static final FilteredHandlerRegistry<Registry, GuiRegistryTagHandler> REGISTRY = new FilteredHandlerRegistry<Registry, GuiRegistryTagHandler>(new GuiRegistryTagHandler() {
        
        @Override
        public void createControls(GuiParent parent, Registry registry) {
            parent.add(new GuiComboBoxMapped<ResourceLocation>("elements", new TextMapBuilder<ResourceLocation>().addComponent(registry.getTagNames().iterator(), x -> {
                if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                    return Component.literal(x.getPath());
                return Component.literal(x.toString());
            })));
        }
        
        @Override
        public void loadValue(GuiParent parent, Registry registry, TagKey tag) {
            GuiComboBoxMapped<ResourceLocation> box = parent.get("elements");
            box.select(tag.location());
        }
        
        @Override
        public TagKey saveValue(GuiParent parent, Registry registry) {
            GuiComboBoxMapped<ResourceLocation> box = parent.get("elements");
            return TagKey.create(registry.key(), box.getSelected());
        }
        
    });
    
    static {
        REGISTRY.register(BuiltInRegistries.BLOCK, new GuiRegistryTagHandler() {
            
            private static final RandomSource RANDOM = RandomSource.create();
            
            @Override
            public void createControls(GuiParent parent, Registry registry) {
                parent.add(new GuiStackSelector("content", null, new GuiStackSelector.StackCollector(new GuiStackSelector.GuiBlockSelector()) {
                    
                    @Override
                    public HashMapList<String, ItemStack> collect(Player player) {
                        HashMapList<String, ItemStack> map = new HashMapList<>();
                        for (Iterator<Pair<TagKey, HolderSet.Named>> iterator = registry.getTags().iterator(); iterator.hasNext();) {
                            Pair<TagKey, HolderSet.Named> pair = iterator.next();
                            var o = pair.getSecond().getRandomElement(RANDOM);
                            if (o.isEmpty())
                                continue;
                            ItemStack stack = new ItemStack(((Holder<Block>) o.get()).value());
                            if (stack.isEmpty())
                                stack = new ItemStack(Blocks.BARRIER);
                            stack.setHoverName(Component.literal(pair.getFirst().location().toString()));
                            
                            map.add("tags", stack);
                        }
                        return map;
                    }
                    
                }));
            }
            
            private String getName(ItemStack stack) {
                MutableComponent comp = (MutableComponent) stack.getHoverName();
                return ((LiteralContents) comp.getContents()).text();
            }
            
            @Override
            public void loadValue(GuiParent parent, Registry registry, TagKey tag) {
                GuiStackSelector selector = parent.get("content");
                String text = tag.location().toString();
                for (ArrayList<ItemStack> stacks : selector.getStacks().values())
                    for (ItemStack stack : stacks)
                        if (getName(stack).equals(text)) {
                            selector.setSelectedForce(stack);
                            return;
                        }
                var o = registry.getTag(tag).flatMap(x -> ((Named) x).getRandomElement(RANDOM));
                if (o.isEmpty())
                    return;
                ItemStack stack = new ItemStack(((Holder<Block>) o.get()).value());
                stack.setHoverName(Component.literal(tag.location().toString()));
                selector.setSelectedForce(stack);
            }
            
            @Override
            public TagKey saveValue(GuiParent parent, Registry registry) {
                GuiStackSelector selector = parent.get("content");
                return TagKey.create(registry.key(), new ResourceLocation(getName(selector.getSelected())));
            }
            
        });
    }
    
    public abstract void createControls(GuiParent parent, Registry registry);
    
    public abstract void loadValue(GuiParent parent, Registry registry, TagKey tag);
    
    public abstract TagKey saveValue(GuiParent parent, Registry registry);
    
}
