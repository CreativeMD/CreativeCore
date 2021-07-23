package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiCheckBox;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiScrollBox;
import team.creative.creativecore.common.gui.controls.layout.GuiLeftRightBox;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.type.CheckTree;

public class ClientSyncGuiLayer extends GuiLayer {
    
    public final CheckTree<ConfigKey> tree;
    public final ICreativeConfigHolder root;
    public CheckTree<ConfigKey>.CheckTreeEntry currentView;
    
    public boolean changed = false;
    
    public int nextAction;
    public boolean force;
    
    public ClientSyncGuiLayer(ICreativeConfigHolder holder) {
        super("client-sync", 300, 200);
        this.root = holder;
        
        registerEvent(GuiControlChangedEvent.class, x -> {
            changed = true;
            if (x.control instanceof GuiTreeCheckBox && !((GuiTreeCheckBox) x.control).value)
                ((GuiTreeCheckBox) x.control).partial = ((GuiTreeCheckBox) x.control).entry.isChildEnabled();
        });
        
        BiConsumer<ConfigKey, Boolean> setter = (x, y) -> x.forceSynchronization = y;
        Function<ConfigKey, Boolean> getter = (x) -> x.forceSynchronization;
        Function<ConfigKey, Collection<? extends ConfigKey>> getChildren = (x) -> {
            if (x.getDefault() instanceof ICreativeConfigHolder) {
                List<ConfigKey> keys = new ArrayList<>();
                for (ConfigKey key : ((ICreativeConfigHolder) x.getDefault()).fields())
                    if (key.isWithoutForce(Dist.CLIENT)) {
                        Object object = key.get();
                        if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) object).isEmptyWithoutForce(Dist.CLIENT))
                            keys.add(key);
                    }
                return keys;
            }
            return null;
        };
        
        List<ConfigKey> keys = new ArrayList<>();
        for (ConfigKey key : holder.fields())
            if (key.isWithoutForce(Dist.CLIENT)) {
                Object object = key.get();
                if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) object).isEmptyWithoutForce(Dist.CLIENT))
                    keys.add(key);
            }
        this.tree = new CheckTree<>(keys, setter, getter, getChildren);
        this.currentView = tree.root;
    }
    
    @Override
    public void create() {
        load(currentView);
    }
    
    public void save() {
        CreativeCore.NETWORK.sendToServer(new ConfigurationClientPacket(root, tree));
    }
    
    public void load(CheckTree<ConfigKey>.CheckTreeEntry entry) {
        if (!isEmpty())
            clear();
        
        ICreativeConfigHolder holder = entry.content == null ? root : (ICreativeConfigHolder) entry.content.get();
        
        GuiLeftRightBox upperBox = new GuiLeftRightBox("upperBox", 0, 0);
        upperBox.add(new GuiLabel("path", 0, 0).setTitle(new TextComponent("/" + String.join("/", holder.path()))));
        if (entry.parent != null)
            upperBox.addRight(new GuiButton("back", 170, 0, x -> {
                load(entry.parent);
            }).setTitle(new TranslatableComponent("gui.back")));
        add(upperBox);
        this.currentView = entry;
        
        GuiScrollBox box = new GuiScrollBox("box", 0, 17, 286, 152);
        add(box);
        
        int offsetX = 20;
        int offsetY = 1;
        for (CheckTree<ConfigKey>.CheckTreeEntry key : currentView.children) {
            box.add(new GuiTreeCheckBox(key, 5, offsetY + 3));
            
            String caption = translateOrDefault("config." + String.join(".", holder.path() + "." + key.content.name + ".name"), key.content.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.content.name + ".comment";
            if (key.content != null && key.content.get() instanceof ICreativeConfigHolder) {
                
                box.add(new GuiButton(caption, offsetX, offsetY, x -> {
                    load(key);
                }).setTitle(new TextComponent(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
                offsetY += 21;
            } else {
                GuiLabel label = new GuiLabel(caption, offsetX, offsetY + 2).setTitle(new TextComponent(caption));
                box.add(label.setTooltip(new TextBuilder().translateIfCan(comment).build()));
                offsetY += 16;
            }
        }
        
        GuiLeftRightBox lowerBox = new GuiLeftRightBox("lowerBox", 0, 171);
        lowerBox.add(new GuiButton("cancel", 0, 180, x -> {
            nextAction = 0;
            closeTopLayer();
        }).setTitle(new TranslatableComponent("gui.cancel")));
        
        lowerBox.add(new GuiButton("config", 40, 180, x -> {
            nextAction = 1;
            closeTopLayer();
        }).setTitle(new TranslatableComponent("gui.config")));
        
        lowerBox.addRight(new GuiButton("save", 170, 180, x -> {
            nextAction = 0;
            force = true;
            save();
            closeTopLayer();
        }).setTitle(new TranslatableComponent("gui.save")));
        add(lowerBox);
        reinit();
    }
    
    @Override
    public void closeTopLayer() {
        if (force || !changed) {
            if (nextAction == 0)
                super.closeTopLayer();
            else if (nextAction == 1)
                GuiContainerHandler.openGui(getPlayer(), "config");
        } else
            GuiDialogHandler.openDialog(getParent(), "savechanges", (x, y) -> {
                if (y == DialogButton.YES) {
                    save();
                }
                if (y != DialogButton.CANCEL) {
                    force = true;
                    closeTopLayer();
                }
            }, DialogButton.YES, DialogButton.NO, DialogButton.CANCEL);
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
        public boolean mouseClicked(double x, double y, int button) {
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
