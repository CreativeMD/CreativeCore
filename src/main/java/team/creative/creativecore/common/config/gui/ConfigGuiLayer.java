package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiButtonFixed;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiLabelFixed;
import team.creative.creativecore.common.gui.controls.GuiScrollBox;
import team.creative.creativecore.common.gui.controls.layout.GuiLeftRightBox;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
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
        GuiScrollBox box = (GuiScrollBox) get("box");
        JsonObject parent = null;
        for (GuiControl control : box)
            if (control instanceof GuiConfigControl) {
                JsonElement element = ((GuiConfigControl) control).save();
                if (element != null) {
                    if (parent == null)
                        parent = JsonUtils.get(ROOT, holder.path());
                    parent.add(((GuiConfigControl) control).field.name, element);
                }
            }
    }
    
    public void loadHolder(ICreativeConfigHolder holder) {
        if (!isEmpty()) {
            savePage();
            clear();
        }
        GuiLeftRightBox upperBox = new GuiLeftRightBox("upperbox", 0, 0);
        upperBox.add(new GuiLabel("path", 0, 2).setTitle(new StringTextComponent("/" + String.join("/", holder.path()))));
        
        if (holder != rootHolder)
            upperBox.addRight(new GuiButton("back", 0, 0, x -> {
                loadHolder(holder.parent());
            }).setTitle(new TranslationTextComponent("gui.back")));
        this.holder = holder;
        
        add(upperBox);
        GuiScrollBox box = new GuiScrollBox("box", 0, 17, 406, 186);
        add(box);
        
        JsonObject json = JsonUtils.tryGet(ROOT, holder.path());
        
        int offsetX = 1;
        int offsetY = 1;
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            Object value = key.get();
            String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
            if (value instanceof ICreativeConfigHolder) {
                if (!((ICreativeConfigHolder) value).isEmpty(side)) {
                    box.add(new GuiButtonFixed(caption, offsetX, offsetY, 100, 20, x -> {
                        loadHolder((ICreativeConfigHolder) value);
                    }).setTitle(new StringTextComponent(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
                    offsetY += 21;
                }
            } else {
                if (!key.is(side))
                    continue;
                
                GuiLabel label = new GuiLabelFixed(caption + ":", offsetX, offsetY + 2, 100, 16).setTitle(new StringTextComponent(caption + ":"));
                
                GuiConfigControl config = new GuiConfigControl((ConfigKeyField) key, 0, offsetY, 100, 14, side);
                GuiButton resetButton = (GuiButton) new GuiButtonFixed("r", offsetX + 370, offsetY, 14, 14, x -> {
                    config.reset();
                    ConfigGuiLayer.this.changed = true;
                }).setTitle(new StringTextComponent("r"));
                
                int labelWidth = 110;
                config.setX(label.getX() + labelWidth + 2);
                config.setWidth(380 - config.getX());
                config.init(json != null ? json.get(key.name) : null);
                box.add(label.setTooltip(new TextBuilder().translateIfCan(comment).build()));
                box.add(config);
                box.add(resetButton.setTooltip(new TextBuilder().text("reset to default").build()));
                config.setResetButton(resetButton);
                offsetY += config.getHeight() + 1;
            }
            
        }
        
        GuiLeftRightBox lowerBox = new GuiLeftRightBox("lowerBox", 0, 205);
        lowerBox.add(new GuiButton("cancel", 0, 205, x -> {
            nextAction = 0;
            closeTopLayer();
        }).setTitle(new TranslationTextComponent("gui.cancel")));
        
        if (side == Dist.DEDICATED_SERVER)
            lowerBox.add(new GuiButton("client-config", 40, 205, x -> {
                nextAction = 1;
                closeTopLayer();
            }).setTitle(new TranslationTextComponent("gui.client-config")));
        
        lowerBox.addRight(new GuiButton("save", 370, 205, x -> {
            nextAction = 0;
            savePage();
            sendUpdate();
            force = true;
            closeTopLayer();
        }).setTitle(new TranslationTextComponent("gui.save")));
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
