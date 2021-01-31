package team.creative.creativecore.common.gui.integration;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import team.creative.creativecore.common.gui.IGuiIntegratedParent;

public class ScreenEventListener implements IGuiEventListener {
	
	private static final Field eventTime = ObfuscationReflectionHelper.findField(MouseHelper.class, "field_198045_j");
	public static final int DOUBLE_CLICK_TIME = 200;
	
	private final IGuiIntegratedParent gui;
	private int doubleClickButton = -1;
	private double time;
	private double x;
	private double y;
	private boolean released = false;
	
	public ScreenEventListener(IGuiIntegratedParent gui) {
		this.gui = gui;
	}
	
	public void tick() {
		if (doubleClickButton != -1 && getEventTime() - time > DOUBLE_CLICK_TIME)
			fireRemaingEvents();
	}
	
	public double getEventTime() {
		try {
			return eventTime.getDouble(Minecraft.getInstance().mouseHelper);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	protected void fireRemaingEvents() {
		if (doubleClickButton != -1) {
			gui.getTopLayer().mouseClicked(x, y, doubleClickButton);
			if (released)
				gui.getTopLayer().mouseReleased(x, y, doubleClickButton);
			doubleClickButton = -1;
			released = false;
		}
	}
	
	@Override
	public void mouseMoved(double x, double y) {
		gui.getTopLayer().mouseMoved(x, y);
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (doubleClickButton == button) {
			released = false;
			doubleClickButton = -1;
			return gui.getTopLayer().mouseDoubleClicked(x, y, button);
		}
		fireRemaingEvents();
		doubleClickButton = button;
		time = getEventTime();
		return true;
	}
	
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		if (doubleClickButton == button) {
			released = true;
			return true;
		}
		fireRemaingEvents();
		return gui.getTopLayer().mouseReleased(x, y, button);
	}
	
	@Override
	public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
		if (doubleClickButton == -1)
			return gui.getTopLayer().mouseDragged(x, y, button, dragX, dragY, getEventTime());
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double delta) {
		return gui.getTopLayer().mouseScrolled(x, y, delta);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return gui.getTopLayer().keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return gui.getTopLayer().keyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return gui.getTopLayer().charTyped(codePoint, modifiers);
	}
	
	@Override
	public boolean changeFocus(boolean focus) {
		return false;
	}
	
}
