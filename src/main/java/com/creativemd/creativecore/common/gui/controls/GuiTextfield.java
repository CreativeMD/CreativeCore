package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.ChangedCharSetException;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import scala.Char;

import com.creativemd.creativecore.common.gui.event.ControlChangedEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextfield extends GuiFocusControl{
	
	public static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	
	public String text = "";
	
	/**also selStart**/
	public int cursorPosition;
	
	/** The current character index that should be used as start of the rendered text. */
    private int scrollOffset = 0;
    
    public int maxLength = 32;
	
	public int selEnd = 0;
	
	public char[] allowedChars;
	
	private int cursorCounter;

	private int enabledColor = 14737632;
    private int disabledColor = 7368816;
	
	public GuiTextfield(String name, String text, int x, int y, int width, int height) {
		this(name, text, x, y, width, height, 0);
	}
	
	public GuiTextfield(String name, String text, int x, int y, int width, int height, int rotation) {
		super(name, x, y, width, height, rotation);
		this.text = text;
		
	}
	
	public GuiTextfield setFloatOnly()
	{
		this.allowedChars = new char[]{'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
		return this;
	}
	
	public GuiTextfield setNumbersOnly()
	{
		this.allowedChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		return this;
	}
	
	public int getScrollOffset()
	{
		return scrollOffset;
	}
	
	public boolean hasSelectedText()
	{
		return selEnd != -1 && selEnd != cursorPosition;
	}
	
	public String getSelectedText()
	{
		if(hasSelectedText() && text != null)
			return text.substring(Math.min(cursorPosition, selEnd), Math.max(cursorPosition, selEnd));
		else
			return "";
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		boolean wasFocused = focused;
		super.mousePressed(posX, posY, button);
		if(focused && !wasFocused)
			cursorCounter = 0;
		
		int l = posX - (this.posX-this.width/2);

        String s = fontRenderer.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
        this.setCursorPosition(fontRenderer.trimStringToWidth(s, l).length() + this.scrollOffset);
        
		return true;
	}
	
	//pub
	
	//public void onKeyPressed(int key){}
	
	public void writeText(String text)
    {
        String s1 = "";
        String s2 = ChatAllowedCharacters.filerAllowedCharacters(text);
        String s3 = "";
        char[] chars = s2.toCharArray();
        if(allowedChars != null)
        {
	        for (int i = 0; i < chars.length; i++) {
				if(ArrayUtils.contains(allowedChars, chars[i]))
					s3 += chars[i];
			}
	        s2 = s3;
        }
        
        int i = this.cursorPosition < this.selEnd ? this.cursorPosition : this.selEnd;
        int j = this.cursorPosition < this.selEnd ? this.selEnd : this.cursorPosition;
        int k = this.maxLength - this.text.length() - (i - this.selEnd);
        boolean flag = false;

        if (this.text.length() > 0)
        {
            s1 = s1 + this.text.substring(0, i);
        }

        int l;

        if (k < s2.length())
        {
            s1 = s1 + s2.substring(0, k);
            l = k;
        }
        else
        {
            s1 = s1 + s2;
            l = s2.length();
        }

        if (this.text.length() > 0 && j < this.text.length())
        {
            s1 = s1 + this.text.substring(j);
        }

        this.text = s1;
        this.moveCursorBy(i - this.selEnd + l);
        
        raiseEvent(new ControlChangedEvent(this));
    }
	
	public void moveCursorBy(int offset)
    {
        this.setCursorPosition(this.selEnd + offset);
    }
	
	public int getNthWordFromCursor(int pos)
    {
        return this.getNthWordFromPos(pos, this.cursorPosition);
    }
	
	public int getNthWordFromPos(int pos, int cursorPos)
    {
        return this.func_146197_a(pos, this.cursorPosition, true);
    }
	
	public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_)
    {
        int k = p_146197_2_;
        boolean flag1 = p_146197_1_ < 0;
        int l = Math.abs(p_146197_1_);

        for (int i1 = 0; i1 < l; ++i1)
        {
            if (flag1)
            {
                while (p_146197_3_ && k > 0 && this.text.charAt(k - 1) == 32)
                {
                    --k;
                }

                while (k > 0 && this.text.charAt(k - 1) != 32)
                {
                    --k;
                }
            }
            else
            {
                int j1 = this.text.length();
                k = this.text.indexOf(32, k);

                if (k == -1)
                {
                    k = j1;
                }
                else
                {
                    while (p_146197_3_ && k < j1 && this.text.charAt(k) == 32)
                    {
                        ++k;
                    }
                }
            }
        }

        return k;
    }
	
	public void deleteWords(int pos)
    {
        if (this.text.length() != 0)
        {
            if (this.selEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(pos) - this.cursorPosition);
            }
        }
        raiseEvent(new ControlChangedEvent(this));
    }
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> strings = new ArrayList<String>();
		
		return strings;
	}
	
    public void deleteFromCursor(int pos)
    {
        if (this.text.length() != 0)
        {
            if (this.selEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                boolean flag = pos < 0;
                int j = flag ? this.cursorPosition + pos : this.cursorPosition;
                int k = flag ? this.cursorPosition : this.cursorPosition + pos;
                String s = "";

                if (j >= 0)
                {
                    s = this.text.substring(0, j);
                }

                if (k < this.text.length())
                {
                    s = s + this.text.substring(k);
                }

                this.text = s;

                if (flag)
                {
                    this.moveCursorBy(pos);
                }
            }
        }
        raiseEvent(new ControlChangedEvent(this));
    }
	
	public int getWidth()
    {
		return width-8;
    }
	
	public void setSelectionPos(int p_146199_1_)
    {
        int j = this.text.length();

        if (p_146199_1_ > j)
        {
            p_146199_1_ = j;
        }

        if (p_146199_1_ < 0)
        {
            p_146199_1_ = 0;
        }

        this.selEnd = p_146199_1_;

        if (this.fontRenderer != null)
        {
            if (this.scrollOffset > j)
            {
                this.scrollOffset = j;
            }

            int k = this.getWidth();
            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.scrollOffset), k);
            int l = s.length() + this.scrollOffset;

            if (p_146199_1_ == this.scrollOffset)
            {
                this.scrollOffset -= this.fontRenderer.trimStringToWidth(this.text, k, true).length();
            }

            if (p_146199_1_ > l)
            {
                this.scrollOffset += p_146199_1_ - l;
            }
            else if (p_146199_1_ <= this.scrollOffset)
            {
                this.scrollOffset -= this.scrollOffset - p_146199_1_;
            }

            if (this.scrollOffset < 0)
            {
                this.scrollOffset = 0;
            }

            if (this.scrollOffset > j)
            {
                this.scrollOffset = j;
            }
        }
    }
	
	public void setCursorPosition(int pos)
    {
        this.cursorPosition = pos;
        int j = this.text.length();

        if (this.cursorPosition < 0)
        {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > j)
        {
            this.cursorPosition = j;
        }

        this.setSelectionPos(this.cursorPosition);
    }
	
	public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }
	
	public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }
	
	@Override
	public boolean onKeyPressed(char character, int key){
		if(!focused)
			return false;
		switch (character)
        {
            case 1:
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            case 3:
                GuiScreen.setClipboardString(this.getSelectedText());
                return true;
            case 22:
                if (this.enabled)
                {
                    this.writeText(GuiScreen.getClipboardString());
                }

                return true;
            case 24:
                GuiScreen.setClipboardString(this.getSelectedText());

                if (this.enabled)
                {
                    this.writeText("");
                }

                return true;
            default:
                switch (key)
                {
                    case 14:
                        if (GuiScreen.isCtrlKeyDown())
                        {
                            if (this.enabled)
                            {
                                this.deleteWords(-1);
                            }
                        }
                        else if (this.enabled)
                        {
                            this.deleteFromCursor(-1);
                        }
                        
                        return true;
                    case 199:
                        if (GuiScreen.isShiftKeyDown())
                        {
                            this.setSelectionPos(0);
                        }
                        else
                        {
                            this.setCursorPositionZero();
                        }

                        return true;
                    case 203:
                        if (GuiScreen.isShiftKeyDown())
                        {
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setSelectionPos(this.getNthWordFromPos(-1, this.selEnd));
                            }
                            else
                            {
                                this.setSelectionPos(this.selEnd - 1);
                            }
                        }
                        else if (GuiScreen.isCtrlKeyDown())
                        {
                            this.setCursorPosition(this.getNthWordFromCursor(-1));
                        }
                        else
                        {
                            this.moveCursorBy(-1);
                        }

                        return true;
                    case 205:
                        if (GuiScreen.isShiftKeyDown())
                        {
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setSelectionPos(this.getNthWordFromPos(1, this.selEnd));
                            }
                            else
                            {
                                this.setSelectionPos(this.selEnd + 1);
                            }
                        }
                        else if (GuiScreen.isCtrlKeyDown())
                        {
                            this.setCursorPosition(this.getNthWordFromCursor(1));
                        }
                        else
                        {
                            this.moveCursorBy(1);
                        }

                        return true;
                    case 207:
                        if (GuiScreen.isShiftKeyDown())
                        {
                            this.setSelectionPos(this.text.length());
                        }
                        else
                        {
                            this.setCursorPositionEnd();
                        }

                        return true;
                    case 211:
                        if (GuiScreen.isCtrlKeyDown())
                        {
                            if (this.enabled)
                            {
                                this.deleteWords(1);
                            }
                        }
                        else if (this.enabled)
                        {
                            this.deleteFromCursor(1);
                        }

                        return true;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(character))
                        {
                            if (this.enabled)
                            {
                                this.writeText(Character.toString(character));
                            }

                            return true;
                        }
                        else
                        {
                            return false;
                        }
                }
        }
	}
	
	public void updateCursorCounter()
    {
        ++this.cursorCounter;
    }
	//public void onKeyUp(int key){}
	
	public void onTextChange(){}

	@Override
	public void drawControl(FontRenderer renderer) {
		//if (this.getEnableBackgroundDrawing())
        //{
            Gui.drawRect(0 - 1, 0 - 1, 0 + this.width + 1, 0 + this.height + 1, -6250336);
            Gui.drawRect(0, 0, 0 + this.width, 0 + this.height, -16777216);
        //}

        int i = this.enabled ? this.enabledColor : this.disabledColor;
        int j = this.cursorPosition - this.scrollOffset;
        int k = this.selEnd - this.scrollOffset;
        String s = fontRenderer.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;

		boolean enableBackgroundDrawing = true;
        int l = enableBackgroundDrawing ? 4 : 0;
        int i1 = enableBackgroundDrawing ? (this.height - 8) / 2 : 0;
        int j1 = l;

        if (k > s.length())
        {
            k = s.length();
        }

        if (s.length() > 0)
        {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i);
        }

        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxLength;
        int k1 = j1;

        if (!flag)
        {
            k1 = j > 0 ? l + this.width : l;
        }
        else if (flag2)
        {
            k1 = j1 - 1;
            --j1;
        }

        if (s.length() > 0 && flag && j < s.length())
        {
            fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i);
        }

        if (flag1)
        {
            if (flag2)
            {
                Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 +fontRenderer.FONT_HEIGHT, -3092272);
            }
            else
            {
            	fontRenderer.drawStringWithShadow("_", k1, i1, i);
            }
        }

        if (k != j)
        {
            int l1 = l + fontRenderer.getStringWidth(s.substring(0, k));
            this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + fontRenderer.FONT_HEIGHT);
        }
		
		//int l = 14737632;
		
		//renderer.drawString(text, width / 2 - renderer.getStringWidth(text) / 2, (this.height - 8) / 2, l);
	}
	
	public void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_)
    {
        int i1;

        if (p_146188_1_ < p_146188_3_)
        {
            i1 = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i1;
        }

        if (p_146188_2_ < p_146188_4_)
        {
            i1 = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = i1;
        }

        if (p_146188_3_ > this.posX + this.width)
        {
            p_146188_3_ = this.posX + this.width;
        }

        if (p_146188_1_ > this.posX + this.width)
        {
            p_146188_1_ = this.posX + this.width;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)p_146188_1_, (double)p_146188_4_, 0.0D);
        tessellator.addVertex((double)p_146188_3_, (double)p_146188_4_, 0.0D);
        tessellator.addVertex((double)p_146188_3_, (double)p_146188_2_, 0.0D);
        tessellator.addVertex((double)p_146188_1_, (double)p_146188_2_, 0.0D);
        tessellator.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
