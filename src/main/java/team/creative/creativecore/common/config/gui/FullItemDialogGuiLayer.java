package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiComboBox;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiLabelFixed;
import team.creative.creativecore.common.gui.controls.GuiScrollBox;
import team.creative.creativecore.common.gui.controls.layout.GuiLeftRightBox;
import team.creative.creativecore.common.gui.controls.layout.GuiVBox;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.GuiCreativeIngredientHandler;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class FullItemDialogGuiLayer extends GuiLayer {
    
    public static List<CreativeIngredient> latest = new ArrayList<CreativeIngredient>();
    
    public GuiInfoStackButton button;
    
    public FullItemDialogGuiLayer() {
        super("info", 250, 230);
        registerEventChanged(x -> {
            if (x.control.is("type")) {
                init();
            } else
                handler.onChanged(this, x);
        });
    }
    
    public GuiCreativeIngredientHandler handler;
    
    @Override
    public void create() {
        if (button == null)
            return;
        CreativeIngredient info = button.get();
        handler = GuiCreativeIngredientHandler.getHandler(info);
        
        GuiComboBox box = (GuiComboBox) get("type");
        if (box != null)
            handler = GuiCreativeIngredientHandler.get(box.getIndex());
        
        clear();
        GuiVBox upperBox = new GuiVBox("upperBox", 0, 0, Align.STRETCH);
        List<String> lines = new ArrayList<>(GuiCreativeIngredientHandler.getNames());
        box = new GuiComboBox("type", 0, 0, new TextListBuilder().add(lines));
        box.select(lines.indexOf(handler.getName()));
        upperBox.add(box);
        add(upperBox);
        handler.createControls(this, info);
        
        GuiScrollBox scroll = new GuiScrollBox("latest", 0, 150, 136, 65);
        int latestPerRow = 4;
        for (int i = 0; i < latest.size(); i++) {
            int row = i / latestPerRow;
            int cell = i - (row * latestPerRow);
            
            GuiLabel label = (GuiLabel) new GuiLabelFixed("" + i, cell * 18, row * 18, 18, 18) {
                
                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    FullItemDialogGuiLayer.this.button.set(latest.get(Integer.parseInt(name)));
                    closeTopLayer();
                    playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                }
            }.setTitle(new TextBuilder().stack(latest.get(i).getExample()).build()).setTooltip(GuiInfoStackButton.getLabelText(latest.get(i)));
            scroll.add(label);
        }
        add(scroll);
        
        GuiLeftRightBox actionBox = new GuiLeftRightBox("actionBox", 0, 130);
        actionBox.add(new GuiButton("cancel", 0, 130, x -> closeTopLayer()).setTitle(new TranslatableComponent("gui.cancel")));
        actionBox.addRight(new GuiButton("save", 100, 130, x -> {
            CreativeIngredient parsedInfo = handler.parseInfo(FullItemDialogGuiLayer.this);
            if (parsedInfo != null) {
                FullItemDialogGuiLayer.this.button.set(parsedInfo);
                if (!latest.contains(parsedInfo))
                    latest.add(0, parsedInfo.copy());
                closeTopLayer();
            }
        }).setTitle(new TranslatableComponent("gui.save")));
        add(actionBox);
    }
    
}
