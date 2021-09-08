package team.creative.creativecore.client.test;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiProgressbar;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiTest extends GuiLayer {
    
    public GuiTest(int width, int height) {
        super("test", width, height);
    }
    
    @Override
    public void create() {
        add(new GuiProgressbar("bar", Math.random(), 1));
        add(new GuiButton("test", null).setTitle(new TextBuilder().stack(new ItemStack(Items.PAPER)).text("My Label test").build()));
        add(new GuiLabel("label").setTitle(new TextBuilder().stack(new ItemStack(Items.PAPER)).text("My Label test").build()));
    }
    
}
