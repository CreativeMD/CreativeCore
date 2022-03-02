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
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiLeftRightBox;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.type.tree.CheckTree;

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
        this.flow = GuiFlow.STACK_Y;
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
        
        add(new GuiLeftRightBox().addLeft(new GuiLabel("path").setTitle(new TextComponent("/" + String.join("/", holder.path()))))
                .addRight(new GuiButton("back", x -> load(entry.parent)).setTranslate("gui.back").setEnabled(entry.parent != null)));
        this.currentView = entry;
        
        GuiScrollY box = new GuiScrollY("", 100, 100).setExpandable();
        add(box);
        
        for (CheckTree<ConfigKey>.CheckTreeEntry key : currentView.children) {
            GuiRow row = new GuiRow();
            box.add(row);
            GuiColumn first = new GuiColumn(20);
            row.addColumn(first);
            first.valign = VAlign.CENTER;
            first.align = Align.CENTER;
            first.add(new GuiTreeCheckBox(key));
            
            GuiColumn second = (GuiColumn) new GuiColumn().setExpandableX();
            row.addColumn(second);
            String caption = translateOrDefault("config." + String.join(".", holder.path() + "." + key.content.name + ".name"), key.content.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.content.name + ".comment";
            if (key.content != null && key.content.get() instanceof ICreativeConfigHolder)
                second.add(new GuiButton(caption, x -> {
                    load(key);
                }).setTitle(new TextComponent(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
            else
                second.add(new GuiLabel(caption).setTitle(new TextComponent(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
        }
        
        add(new GuiLeftRightBox().addLeft(new GuiButton("cancel", x -> {
            nextAction = 0;
            closeTopLayer();
        }).setTranslate("gui.cancel")).addLeft(new GuiButton("config", x -> {
            nextAction = 1;
            closeTopLayer();
        }).setTranslate("gui.config")).addRight(new GuiButton("save", x -> {
            nextAction = 0;
            force = true;
            save();
            closeTopLayer();
        }).setTitle(new TranslatableComponent("gui.save"))));
        reinit();
    }
    
    @Override
    public void closeTopLayer() {
        if (force || !changed) {
            if (nextAction == 0)
                super.closeTopLayer();
            else if (nextAction == 1)
                CreativeCore.CONFIG_OPEN.open(getPlayer());
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
        
        public GuiTreeCheckBox(CheckTree<ConfigKey>.CheckTreeEntry entry) {
            super(entry.content.name, "", entry.isEnabled());
            this.entry = entry;
            if (!value)
                partial = entry.isChildEnabled();
        }
        
        @Override
        public boolean mouseClicked(Rect rect, double x, double y, int button) {
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
