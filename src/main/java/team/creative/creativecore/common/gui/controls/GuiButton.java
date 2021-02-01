package team.creative.creativecore.common.gui.controls;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.SoundEvents;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiButton extends GuiControl {
	
	private String title;
	public int color = ColorUtils.WHITE;
	private final Consumer<Integer> pressed;
	private String key;
	
	public GuiButton(String name, int x, int y, int width, int height, Consumer<Integer> pressed) {
		super(name, x, y, width, height);
		this.pressed = pressed;
	}
	
	public void setTitle(String title) {
		this.key = title;
		this.title = translate(title);
	}
	
	public void setTitle(String title, Object... parameters) {
		this.key = title;
		this.title = translate(title, parameters);
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		playSound(SoundEvents.UI_BUTTON_CLICK);
		if (pressed != null)
			pressed.accept(button);
		return true;
	}
	
	@Override
	public void init() {
		if (key == null)
			setTitle(getNestedName());
	}
	
	@Override
	public void closed() {}
	
	@Override
	public void tick() {}
	
	@Override
	public ControlFormatting getControlFormatting() {
		return ControlFormatting.CLICKABLE;
	}
	
	@Override
	protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
		GuiRenderHelper.drawStringCentered(matrix, title, rect.getWidth(), rect.getHeight(), color, true);
	}
	
}
