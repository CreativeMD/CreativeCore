package team.creative.creativecore.client.test;

import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiProgressbar;

public class GuiTest extends GuiLayer {
	
	public GuiTest(int width, int height) {
		super("test", width, height);
	}
	
	@Override
	public void create() {
		add(new GuiProgressbar("bar", 0, 0, 100, 14, Math.random(), 1));
		add(new GuiButton("test", 102, 0, 40, 14, (x) -> getParent().openLayer(new GuiTest(150, 150))));
	}
	
}
