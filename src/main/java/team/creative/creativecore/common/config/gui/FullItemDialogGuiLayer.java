package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.SoundEvents;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiComboBox;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiScrollBox;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.GuiCreativeIngredientHandler;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class FullItemDialogGuiLayer extends GuiLayer {
    
    public static List<CreativeIngredient> latest = new ArrayList<CreativeIngredient>();
    
    public final GuiInfoStackButton button;
    
    public FullItemDialogGuiLayer(GuiInfoStackButton button) {
        super("info", 150, 230);
        this.button = button;
        registerEventChanged(x -> {
            if (x.control.is("type")) {
                create();
            } else
                handler.onChanged(this, x);
        });
    }
    
    public GuiCreativeIngredientHandler handler;
    
    @Override
    public void create() {
        CreativeIngredient info = button.get();
        handler = GuiCreativeIngredientHandler.getHandler(info);
        
        GuiComboBox box = (GuiComboBox) get("type");
        if (box != null)
            handler = GuiCreativeIngredientHandler.get(box.getIndex());
        
        clear();
        List<String> lines = new ArrayList<>(GuiCreativeIngredientHandler.getNames());
        box = new GuiComboBox("type", 0, 0, new TextListBuilder().add(lines));
        box.select(lines.indexOf(handler.getName()));
        add(box);
        
        handler.createControls(this, info);
        
        GuiScrollBox scroll = new GuiScrollBox("latest", 0, 155, 144, 65);
        int latestPerRow = 4;
        for (int i = 0; i < latest.size(); i++) {
            int row = i / latestPerRow;
            int cell = i - (row * latestPerRow);
            
            GuiLabel label = new GuiLabel("" + i, cell * 32, row * 18) {
                
                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    FullItemDialogGuiLayer.this.button.set(latest.get(Integer.parseInt(name)));
                    closeTopLayer();
                    playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                }
            }.setTitle(new TextBuilder().stack(latest.get(i).getExample()).build());
            scroll.add(label);
        }
        add(scroll);
        
        add(new GuiButton("Cancel", 0, 130, x -> closeTopLayer()));
        add(new GuiButton("Save", 100, 130, x -> {
            CreativeIngredient parsedInfo = handler.parseInfo(FullItemDialogGuiLayer.this);
            if (parsedInfo != null) {
                FullItemDialogGuiLayer.this.button.set(parsedInfo);
                if (!latest.contains(parsedInfo))
                    latest.add(0, parsedInfo.copy());
                closeTopLayer();
            }
        }));
    }
    
}
