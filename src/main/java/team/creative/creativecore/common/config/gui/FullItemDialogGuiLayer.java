package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.parent.GuiLeftRightBox;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.parent.GuiTopBottomBox;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.GuiCreativeIngredientHandler;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class FullItemDialogGuiLayer extends GuiLayer {
    
    public static List<CreativeIngredient> latest = new ArrayList<CreativeIngredient>();
    
    public GuiInfoStackButton button;
    
    public GuiCreativeIngredientHandler handler;
    
    public FullItemDialogGuiLayer() {
        super("info", 250, 230);
        this.flow = GuiFlow.STACK_Y;
        registerEventChanged(x -> {
            if (x.control.is("type")) {
                init();
            } else
                handler.onChanged(this, x);
        });
    }
    
    @Override
    public void create() {
        if (button == null)
            return;
        CreativeIngredient info = button.get();
        handler = GuiCreativeIngredientHandler.find(info);
        
        GuiComboBoxMapped<GuiCreativeIngredientHandler> box = get("type");
        if (box != null)
            handler = box.getSelected();
        
        clear();
        
        GuiTopBottomBox topBottom = new GuiTopBottomBox();
        add(topBottom);
        box = new GuiComboBoxMapped<GuiCreativeIngredientHandler>("type", new TextMapBuilder<GuiCreativeIngredientHandler>().addEntrySet(GuiCreativeIngredientHandler.REGISTRY
                .entrySet(), x -> Component.literal(x.getKey())));
        box.setExpandableX();
        box.select(handler);
        topBottom.addTop(box);
        
        handler.createControls(topBottom.top, info);
        GuiScrollY scroll = (GuiScrollY) new GuiScrollY("latest").setDim(100, 80).setExpandableX();
        for (int i = 0; i < latest.size(); i++) {
            final int id = i;
            scroll.add(new GuiButton("" + i, x -> {
                FullItemDialogGuiLayer.this.button.set(latest.get(id));
                closeTopLayer();
                playSound(SoundEvents.UI_BUTTON_CLICK);
            }).setTitle(GuiInfoStackButton.getLabelText(latest.get(i))).setAlign(Align.CENTER).setExpandableX());
        }
        topBottom.addBottom(scroll);
        
        GuiLeftRightBox actionBox = new GuiLeftRightBox().addLeft(new GuiButton("cancel", x -> closeTopLayer()).setTitle(Component.translatable("gui.cancel"))).addRight(
            new GuiButton("save", x -> {
                CreativeIngredient parsedInfo = handler.parseControls(topBottom.top);
                if (parsedInfo != null) {
                    FullItemDialogGuiLayer.this.button.set(parsedInfo);
                    if (!latest.contains(parsedInfo))
                        latest.add(0, parsedInfo.copy());
                    closeTopLayer();
                }
            }).setTitle(Component.translatable("gui.save")));
        topBottom.addBottom(actionBox);
    }
    
}
