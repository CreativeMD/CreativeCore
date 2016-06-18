package com.creativemd.creativecore.gui.controls.gui;

import java.util.ArrayList;

import javax.vecmath.Vector4d;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiComboBox extends GuiLabel{
	
	public GuiComboBoxExtension extension;
	public ArrayList<String> lines;
	public int index;
	
	public GuiComboBox(String name, int x, int y, int width, ArrayList<String> lines) {
		super(name, x, y, width, 14, ColorUtils.WHITE);
		if(lines.size() > 0)
		{
			this.caption = lines.get(0);
			this.index = 0;
		}
		else{
			this.caption = "";
			this.index = -1;
		}
		this.lines = lines;
	}
	
	@Override
	public boolean hasBorder()
	{
		return true;
	}
	
	@Override
	public boolean hasBackground()
	{
		return true;
	}

	@Override
	public boolean mousePressed(int posX, int posY, int button){
		if(extension == null)
			openBox();
		else
			closeBox();
		playSound(SoundEvents.UI_BUTTON_CLICK);
		return true;
	}
	
	public void openBox()
	{
		extension = new GuiComboBoxExtension(name + "extension", this, posX, posY+height, width-getContentOffset()*2, 100, lines);
		getParent().controls.add(extension);
		
		extension.parent = parent;
		extension.moveControlToTop();
		extension.onOpened();
		parent.refreshControls();
		extension.rotation = rotation;
	}
	
	public void closeBox()
	{
		if(extension != null)
		{
			getParent().controls.remove(extension);
			extension = null;
		}
	}
}
