package team.creative.creativecore.client.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiProgressbar;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiTest extends GuiLayer {
	
	public GuiTest(int width, int height) {
		super("test", width, height);
	}
	
	@Override
	public void create() {
		add(new GuiProgressbar("bar", 0, 0, 80, 14, Math.random(), 1));
		add(new GuiButton("test", 82, 0, (x) -> getParent().openLayer(new GuiTest(150, 150))).setTitle(new TextBuilder().stack(new ItemStack(Items.PAPER)).text("My Label test").build()));
		add(new GuiLabel("label", 0, 30).setTitle(new TextBuilder().stack(new ItemStack(Items.PAPER)).text("My Label test").build()));
	}
	
}
