package com.creativemd.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigurationClientPacket;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.type.CheckTree;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class SubGuiClientSync extends SubGui {
    
    public final CheckTree<ConfigKey> tree;
    public final ICreativeConfigHolder root;
    public CheckTree<ConfigKey>.CheckTreeEntry currentView;
    
    public boolean changed = false;
    
    public int nextAction;
    public boolean force;
    
    public SubGuiClientSync(ICreativeConfigHolder holder) {
        super(200, 200);
        this.root = holder;
        
        BiConsumer<ConfigKey, Boolean> setter = (x, y) -> x.forceSynchronization = y;
        Function<ConfigKey, Boolean> getter = (x) -> x.forceSynchronization;
        Function<ConfigKey, Collection<? extends ConfigKey>> getChildren = (x) -> {
            if (x.getDefault() instanceof ICreativeConfigHolder) {
                List<ConfigKey> keys = new ArrayList<>();
                for (ConfigKey key : ((ICreativeConfigHolder) x.getDefault()).fields())
                    if (key.isWithoutForce(Side.CLIENT)) {
                        Object object = key.get();
                        if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) object).isEmptyWithoutForce(Side.CLIENT))
                            keys.add(key);
                    }
                return keys;
            }
            return null;
        };
        
        List<ConfigKey> keys = new ArrayList<>();
        for (ConfigKey key : holder.fields())
            if (key.isWithoutForce(Side.CLIENT)) {
                Object object = key.get();
                if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) object).isEmptyWithoutForce(Side.CLIENT))
                    keys.add(key);
            }
        this.tree = new CheckTree<>(keys, setter, getter, getChildren);
        this.currentView = tree.root;
    }
    
    @Override
    public void createControls() {
        load(currentView);
    }
    
    public void save() {
        PacketHandler.sendPacketToServer(new ConfigurationClientPacket(root, tree));
    }
    
    public void load(CheckTree<ConfigKey>.CheckTreeEntry entry) {
        if (!controls.isEmpty())
            controls.clear();
        
        ICreativeConfigHolder holder = entry.content == null ? root : (ICreativeConfigHolder) entry.content.get();
        
        controls.add(new GuiLabel("/" + String.join("/", holder.path()), 0, 0));
        if (entry.parent != null)
            controls.add(new GuiButton("back", 170, 0) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    load(entry.parent);
                }
            });
        this.currentView = entry;
        
        GuiScrollBox box = new GuiScrollBox("box", 0, 21, 194, 152);
        controls.add(box);
        
        int offsetX = 20;
        int offsetY = 1;
        for (CheckTree<ConfigKey>.CheckTreeEntry key : currentView.children) {
            box.addControl(new GuiTreeCheckBox(key, 2, offsetY + 3));
            
            String caption = translateOrDefault("config." + String.join(".", holder.path() + "." + key.content.name + ".name"), key.content.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.content.name + ".comment";
            if (key.content != null && key.content.get() instanceof ICreativeConfigHolder) {
                
                box.addControl(new GuiButton(caption, offsetX, offsetY) {
                    
                    @Override
                    public void onClicked(int x, int y, int button) {
                        load(key);
                    }
                }.setLangTooltip(comment));
                offsetY += 21;
            } else {
                GuiLabel label = new GuiLabel(caption, offsetX, offsetY + 2);
                box.addControl(label.setLangTooltip(comment));
                offsetY += label.height + 1;
            }
            
        }
        
        controls.add(new GuiButton("cancel", 0, 180) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                nextAction = 0;
                closeGui();
            }
        });
        
        controls.add(new GuiButton("config", 40, 180) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                nextAction = 1;
                closeGui();
            }
        });
        
        controls.add(new GuiButton("save", 170, 180) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                nextAction = 0;
                force = true;
                save();
                closeGui();
            }
        });
        
        refreshControls();
    }
    
    @Override
    public void onDialogClosed(String text, String[] buttons, String clicked) {
        if (clicked.equals("Yes"))
            save();
        if (!clicked.equals("Cancel")) {
            force = true;
            closeGui();
        }
    }
    
    @Override
    public void closeGui() {
        if (force || !changed) {
            if (nextAction == 0)
                super.closeGui();
            else if (nextAction == 1)
                GuiHandler.openGui("config", new NBTTagCompound());
        } else
            openButtonDialogDialog("Do you want to save your changes?", "Yes", "No", "Cancel");
    }
    
    @CustomEventSubscribe
    public void changed(GuiControlChangedEvent event) {
        changed = true;
        if (event.source instanceof GuiTreeCheckBox && !((GuiTreeCheckBox) event.source).value)
            ((GuiTreeCheckBox) event.source).partial = ((GuiTreeCheckBox) event.source).entry.isChildEnabled();
    }
    
    public static class GuiTreeCheckBox extends GuiCheckBox {
        
        public final CheckTree<ConfigKey>.CheckTreeEntry entry;
        
        public GuiTreeCheckBox(CheckTree<ConfigKey>.CheckTreeEntry entry, int x, int y) {
            super(entry.content.name, "", x, y, entry.isEnabled());
            this.entry = entry;
            if (!value)
                partial = entry.isChildEnabled();
        }
        
        @Override
        public boolean mousePressed(int posX, int posY, int button) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            this.value = !value;
            
            if (value)
                entry.enable();
            else
                entry.disable();
            raiseEvent(new GuiControlChangedEvent(this));
            return true;
        }
        
    }
    
}
