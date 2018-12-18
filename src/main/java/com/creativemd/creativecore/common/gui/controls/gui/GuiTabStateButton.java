package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class GuiTabStateButton extends GuiControl {
	
	public String[] states;
	public int selected;
	
	public GuiTabStateButton(String name, int selected, int x, int y, int height, String... states) {
		super(name, x, y, GuiRenderHelper.instance.getStringWidth(String.join("", states)) + 5 * states.length - 5, height);
		this.selected = selected;
		this.states = states;
		this.marginWidth = 0;
	}
	
	public void setState(int index) {
		if (selected == index)
			return;
		this.selected = index;
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	public int getState() {
		return selected;
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button) {
		int x = 0;
		for (int i = 0; i < states.length; i++) {
			int buttonWidth = GuiRenderHelper.instance.getStringWidth(states[i]) + 4;
			if (posX >= this.posX + x && posX <= this.posX + x + buttonWidth) {
				setState(i);
				playSound(SoundEvents.UI_BUTTON_CLICK);
				return true;
			}
			x += buttonWidth + 1;
		}
		
		return false;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		int posX = 0;
		Vec3d pos = rotateMouseVec(getParent().getMousePos());
		for (int i = 0; i < states.length; i++) {
			int buttonWidth = helper.getStringWidth(states[i]) + 4;
			if (i > 0)
				getStyle().getBorder(this).renderStyle(posX - 1, 0, helper, 1, height);
			
			getStyle().getBackground(null).renderStyle(posX, 0, helper, buttonWidth, height);
			
			if (i == selected) {
				if (isMouseOver((int) pos.x, (int) pos.y) && pos.x >= this.posX + posX && pos.x <= this.posX + posX + buttonWidth)
					getStyle().getMouseOverBackground(this).renderStyle(posX, 0, helper, buttonWidth, height);
				
			} else
				getStyle().getDisableEffect(this).renderStyle(posX, 0, helper, buttonWidth, height);
			
			helper.font.drawStringWithShadow(states[i], i + posX + 2, 4, ColorUtils.WHITE);
			posX += buttonWidth + 1;
		}
		
	}
	
}
