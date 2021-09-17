package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.parent.GuiLeftRightBox;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler;
import team.creative.creativecore.common.util.mc.JsonUtils;
import team.creative.creativecore.common.util.text.TextBuilder;

public class ConfigGuiLayer extends GuiLayer {
    
    public JsonObject ROOT = new JsonObject();
    public Dist side;
    
    public final ICreativeConfigHolder rootHolder;
    public ICreativeConfigHolder holder;
    
    public boolean changed = false;
    
    public int nextAction;
    public boolean force;
    
    public ConfigGuiLayer(ICreativeConfigHolder holder, Dist side) {
        super("config", 420, 234);
        this.flow = GuiFlow.STACK_Y;
        this.rootHolder = holder;
        this.holder = holder;
        this.side = side;
        registerEvent(GuiControlChangedEvent.class, x -> {
            GuiConfigControl config = getConfigControl(x.control);
            if (config != null) {
                changed = true;
                config.changed();
            }
        });
    }
    
    @Override
    public void create() {
        loadHolder(holder);
    }
    
    public void savePage() {
        GuiScrollY box = (GuiScrollY) get("box");
        JsonObject parent = null;
        for (GuiChildControl child : box)
            if (child.control instanceof GuiConfigControl) {
                JsonElement element = ((GuiConfigControl) child.control).save();
                if (element != null) {
                    if (parent == null)
                        parent = JsonUtils.get(ROOT, holder.path());
                    parent.add(((GuiConfigControl) child.control).field.name, element);
                }
            }
    }
    
    public void loadHolder(ICreativeConfigHolder holder) {
        if (!isEmpty()) {
            savePage();
            clear();
        }
        GuiLeftRightBox upperBox = new GuiLeftRightBox();
        upperBox.addLeft(new GuiLabel("path").setTitle(new TextComponent("/" + String.join("/", holder.path()))));
        
        upperBox.addRight(new GuiButton("back", x -> {
            loadHolder(holder.parent());
        }).setTranslate("gui.back").setEnabled(holder != rootHolder));
        this.holder = holder;
        
        add(upperBox);
        GuiScrollY box = new GuiScrollY("box", 100, 100).setExpandable();
        add(box);
        
        JsonObject json = JsonUtils.tryGet(ROOT, holder.path());
        
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            Object value = key.get();
            String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
            if (value instanceof ICreativeConfigHolder) {
                if (!((ICreativeConfigHolder) value).isEmpty(side)) {
                    box.add(new GuiButton(caption, x -> {
                        loadHolder((ICreativeConfigHolder) value);
                    }).setTitle(new TextComponent(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
                }
            } else {
                if (!key.is(side))
                    continue;
                
                GuiConfigControl control = new GuiConfigControl(this, (ConfigKeyField) key, side, caption, comment);
                box.add(control);
                control.init(json != null ? json.get(key.name) : null);
            }
            
        }
        
        GuiLeftRightBox lowerBox = new GuiLeftRightBox().addLeft(new GuiButton("cancel", x -> {
            nextAction = 0;
            closeTopLayer();
        }).setTitle(new TranslatableComponent("gui.cancel")));
        
        if (side == Dist.DEDICATED_SERVER)
            lowerBox.addLeft(new GuiButton("client-config", x -> {
                nextAction = 1;
                closeTopLayer();
            }).setTitle(new TranslatableComponent("gui.client-config")));
        
        lowerBox.addRight(new GuiButton("save", x -> {
            nextAction = 0;
            savePage();
            sendUpdate();
            force = true;
            closeTopLayer();
        }).setTranslate("gui.save"));
        add(lowerBox);
        
        reinit();
    }
    
    public void sendUpdate() {
        if (side == Dist.DEDICATED_SERVER)
            CreativeCore.NETWORK.sendToServer(new ConfigurationChangePacket(rootHolder, ROOT));
        else {
            rootHolder.load(false, true, JsonUtils.get(ROOT, rootHolder.path()), Dist.CLIENT);
            CreativeCore.CONFIG_HANDLER.save(Dist.CLIENT);
        }
    }
    
    @Override
    public void closeTopLayer() {
        if (force || !changed) {
            if (nextAction == 0)
                super.closeTopLayer();
            else if (nextAction == 1)
                GuiContainerHandler.openGui(getPlayer(), "clientconfig");
        } else
            GuiDialogHandler.openDialog(getParent(), "savechanges", (x, y) -> {
                if (y == DialogButton.YES) {
                    savePage();
                    sendUpdate();
                }
                if (y != DialogButton.CANCEL) {
                    force = true;
                    closeTopLayer();
                }
            }, DialogButton.YES, DialogButton.NO, DialogButton.CANCEL);
    }
    
    private static GuiConfigControl getConfigControl(GuiControl control) {
        if (control instanceof GuiConfigControl)
            return (GuiConfigControl) control;
        if (control.getParent() != null)
            return getConfigControl((GuiControl) control.getParent());
        return null;
    }
    
}
