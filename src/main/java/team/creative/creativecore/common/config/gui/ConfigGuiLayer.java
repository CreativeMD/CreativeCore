package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiLeftRightBox;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.parent.GuiTable;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.mc.JsonUtils;
import team.creative.creativecore.common.util.text.TextBuilder;

public class ConfigGuiLayer extends GuiLayer {
    
    public JsonObject ROOT = new JsonObject();
    public Side side;
    
    public final ICreativeConfigHolder rootHolder;
    public ICreativeConfigHolder holder;
    
    public int nextAction;
    public boolean force;
    
    public ConfigGuiLayer(ICreativeConfigHolder holder, Side side) {
        super("config", 420, 234);
        this.flow = GuiFlow.STACK_Y;
        this.rootHolder = holder;
        this.holder = holder;
        this.side = side;
    }
    
    @Override
    public void create() {
        loadHolder(holder);
    }
    
    public void savePage() {
        GuiTable table = (GuiTable) get("box.table");
        JsonObject parent = null;
        for (GuiChildControl child : table)
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
        upperBox.addLeft(new GuiLabel("path").setTitle(Component.literal("/" + String.join("/", holder.path()))));
        
        upperBox.addRight(new GuiButton("back", x -> {
            loadHolder(holder.parent());
        }).setTranslate("gui.back").setEnabled(holder != rootHolder));
        this.holder = holder;
        
        add(upperBox);
        GuiScrollY box = new GuiScrollY("box").setDim(100, 100).setExpandable();
        add(box);
        
        GuiTable table = new GuiTable("table").setExpandable();
        box.add(table);
        JsonObject json = JsonUtils.tryGet(ROOT, holder.path());
        
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            Object value = key.get();
            
            String path = "config." + String.join(".", holder.path());
            if (!path.endsWith("."))
                path += ".";
            String caption = translateOrDefault(path + key.name + ".name", key.name);
            String comment = path + key.name + ".comment";
            if (value instanceof ICreativeConfigHolder) {
                if (!((ICreativeConfigHolder) value).isEmpty(side)) {
                    GuiRow row = new GuiRow();
                    table.addRow(row);
                    GuiColumn col = new GuiColumn();
                    row.addColumn(col);
                    col.add(new GuiButton(caption, x -> {
                        loadHolder((ICreativeConfigHolder) value);
                    }).setTitle(Component.literal(caption)).setTooltip(new TextBuilder().translateIfCan(comment).build()));
                }
            } else {
                if (!key.is(side))
                    continue;
                
                GuiConfigControl control = new GuiConfigControl((ConfigKeyField) key, side, caption, comment);
                table.addRow(control);
                control.init(json != null ? json.get(key.name) : null);
            }
            
        }
        
        GuiLeftRightBox lowerBox = new GuiLeftRightBox().addLeft(new GuiButton("cancel", x -> {
            nextAction = 0;
            closeTopLayer();
        }).setTitle(Component.translatable("gui.cancel")));
        
        if (side.isServer())
            lowerBox.addLeft(new GuiButton("client-config", x -> {
                nextAction = 1;
                closeTopLayer();
            }).setTitle(Component.translatable("gui.client-config")));
        
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
        if (side.isServer())
            getIntegratedParent().send(new ConfigurationChangePacket(rootHolder, ROOT));
        else {
            rootHolder.load(false, true, JsonUtils.get(ROOT, rootHolder.path()), Side.CLIENT);
            CreativeCore.CONFIG_HANDLER.save(Side.CLIENT);
        }
    }
    
    @Override
    public void closeTopLayer() {
        savePage();
        if (force || ROOT.size() == 0) {
            if (nextAction == 0)
                super.closeTopLayer();
            else if (nextAction == 1)
                CreativeCore.CONFIG_CLIENT_SYNC_OPEN.open(getPlayer());
        } else
            GuiDialogHandler.openDialog(getIntegratedParent(), "savechanges", (x, y) -> {
                if (y == DialogButton.YES) {
                    savePage();
                    sendUpdate();
                } else if (y != DialogButton.CANCEL) {
                    force = true;
                    closeTopLayer();
                }
            }, DialogButton.YES, DialogButton.NO, DialogButton.CANCEL);
    }
    
}
