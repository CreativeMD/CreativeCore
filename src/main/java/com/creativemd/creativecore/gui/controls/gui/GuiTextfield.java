package com.creativemd.creativecore.gui.controls.gui;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextfield extends GuiFocusControl {

	public String text = "";

	/** also selStart **/
	public int cursorPosition;

	/**
	 * The current character index that should be used as start of the rendered
	 * text.
	 */
	private int scrollOffset = 0;

	public int maxLength = 32;

	public int selEnd = 0;

	public char[] allowedChars;

	private int cursorCounter;

	private int enabledColor = 14737632;
	private int disabledColor = 7368816;

	public GuiTextfield(String text, int x, int y, int width) {
		this(text, x, y, width, 16);
	}

	public GuiTextfield(String text, int x, int y, int width, int height) {
		this(text, text, x, y, width, height);
	}

	public GuiTextfield(String name, String text, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		this.text = text;
	}

	public GuiTextfield setFloatOnly() {
		this.allowedChars = new char[] { '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' };
		return this;
	}

	public GuiTextfield setNumbersIncludingNegativeOnly() {
		this.allowedChars = new char[] { '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		return this;
	}

	public GuiTextfield setNumbersOnly() {
		this.allowedChars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		return this;
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public boolean hasSelectedText() {
		return selEnd != -1 && selEnd != cursorPosition;
	}

	public String getSelectedText() {
		if (hasSelectedText() && text != null)
			return text.substring(Math.min(cursorPosition, selEnd), Math.max(cursorPosition, selEnd));
		else
			return "";
	}

	@Override
	public boolean mousePressed(int posX, int posY, int button) {
		boolean wasFocused = focused;
		super.mousePressed(posX, posY, button);
		if (focused && !wasFocused)
			cursorCounter = 0;

		int l = posX - (this.posX - this.width / 2);

		String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
		this.setCursorPosition(GuiRenderHelper.instance.font.trimStringToWidth(s, l).length() + this.scrollOffset);

		return true;
	}

	public void writeText(String text) {
		String s1 = "";
		String s2 = ChatAllowedCharacters.filterAllowedCharacters(text);
		String s3 = "";
		char[] chars = s2.toCharArray();
		if (allowedChars != null) {
			for (int i = 0; i < chars.length; i++) {
				if (ArrayUtils.contains(allowedChars, chars[i]))
					s3 += chars[i];
			}
			s2 = s3;
		}

		int i = this.cursorPosition < this.selEnd ? this.cursorPosition : this.selEnd;
		int j = this.cursorPosition < this.selEnd ? this.selEnd : this.cursorPosition;
		int k = this.maxLength - this.text.length() - (i - this.selEnd);
		boolean flag = false;

		if (this.text.length() > 0) {
			s1 = s1 + this.text.substring(0, i);
		}

		int l;

		if (k < s2.length()) {
			s1 = s1 + s2.substring(0, k);
			l = k;
		} else {
			s1 = s1 + s2;
			l = s2.length();
		}

		if (this.text.length() > 0 && j < this.text.length()) {
			s1 = s1 + this.text.substring(j);
		}

		this.text = s1;
		this.moveCursorBy(i - this.selEnd + l);

		raiseEvent(new GuiControlChangedEvent(this));
	}

	public void moveCursorBy(int offset) {
		this.setCursorPosition(this.selEnd + offset);
	}

	public int getNthWordFromCursor(int pos) {
		return this.getNthWordFromPos(pos, this.cursorPosition);
	}

	public int getNthWordFromPos(int pos, int cursorPos) {
		return this.func_146197_a(pos, this.cursorPosition, true);
	}

	public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
		int k = p_146197_2_;
		boolean flag1 = p_146197_1_ < 0;
		int l = Math.abs(p_146197_1_);

		for (int i1 = 0; i1 < l; ++i1) {
			if (flag1) {
				while (p_146197_3_ && k > 0 && this.text.charAt(k - 1) == 32) {
					--k;
				}

				while (k > 0 && this.text.charAt(k - 1) != 32) {
					--k;
				}
			} else {
				int j1 = this.text.length();
				k = this.text.indexOf(32, k);

				if (k == -1) {
					k = j1;
				} else {
					while (p_146197_3_ && k < j1 && this.text.charAt(k) == 32) {
						++k;
					}
				}
			}
		}

		return k;
	}

	public void deleteWords(int pos) {
		if (this.text.length() != 0) {
			if (this.selEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(pos) - this.cursorPosition);
			}
		}
		raiseEvent(new GuiControlChangedEvent(this));
	}

	@Override
	public ArrayList<String> getTooltip() {
		ArrayList<String> strings = new ArrayList<String>();

		return strings;
	}

	public void deleteFromCursor(int pos) {
		if (this.text.length() != 0) {
			if (this.selEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = pos < 0;
				int j = flag ? this.cursorPosition + pos : this.cursorPosition;
				int k = flag ? this.cursorPosition : this.cursorPosition + pos;
				String s = "";

				if (j >= 0) {
					s = this.text.substring(0, j);
				}

				if (k < this.text.length()) {
					s = s + this.text.substring(k);
				}

				this.text = s;

				if (flag) {
					this.moveCursorBy(pos);
				}
			}
		}
		raiseEvent(new GuiControlChangedEvent(this));
	}

	public int getWidth() {
		return width - 8;
	}

	public void setSelectionPos(int p_146199_1_) {
		int j = this.text.length();

		if (p_146199_1_ > j) {
			p_146199_1_ = j;
		}

		if (p_146199_1_ < 0) {
			p_146199_1_ = 0;
		}

		this.selEnd = p_146199_1_;

		if (GuiRenderHelper.instance.font != null) {
			if (this.scrollOffset > j) {
				this.scrollOffset = j;
			}

			int k = this.getWidth();
			String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), k);
			int l = s.length() + this.scrollOffset;

			if (p_146199_1_ == this.scrollOffset) {
				this.scrollOffset -= GuiRenderHelper.instance.font.trimStringToWidth(this.text, k, true).length();
			}

			if (p_146199_1_ > l) {
				this.scrollOffset += p_146199_1_ - l;
			} else if (p_146199_1_ <= this.scrollOffset) {
				this.scrollOffset -= this.scrollOffset - p_146199_1_;
			}

			if (this.scrollOffset < 0) {
				this.scrollOffset = 0;
			}

			if (this.scrollOffset > j) {
				this.scrollOffset = j;
			}
		}
	}

	public void setCursorPosition(int pos) {
		this.cursorPosition = pos;
		int j = this.text.length();

		if (this.cursorPosition < 0) {
			this.cursorPosition = 0;
		}

		if (this.cursorPosition > j) {
			this.cursorPosition = j;
		}

		this.setSelectionPos(this.cursorPosition);
	}

	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	public void setCursorPositionEnd() {
		this.setCursorPosition(this.text.length());
	}

	@Override
	public boolean onKeyPressed(char character, int key) {
		if (!focused)
			return false;
		else if (GuiScreen.isKeyComboCtrlA(key)) {
			this.setCursorPositionEnd();
			this.setSelectionPos(0);
			return true;
		} else if (GuiScreen.isKeyComboCtrlC(key)) {
			GuiScreen.setClipboardString(this.getSelectedText());
			return true;
		} else if (GuiScreen.isKeyComboCtrlV(key)) {
			if (this.enabled) {
				this.writeText(GuiScreen.getClipboardString());
			}

			return true;
		} else if (GuiScreen.isKeyComboCtrlX(key)) {
			GuiScreen.setClipboardString(this.getSelectedText());

			if (this.enabled) {
				this.writeText("");
			}

			return true;
		} else {
			switch (key) {
			case 14:

				if (GuiScreen.isCtrlKeyDown()) {
					if (this.enabled) {
						this.deleteWords(-1);
					}
				} else if (this.enabled) {
					this.deleteFromCursor(-1);
				}

				return true;
			case 199:

				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(0);
				} else {
					this.setCursorPositionZero();
				}

				return true;
			case 203:

				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(-1, selEnd));
					} else {
						this.setSelectionPos(selEnd - 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(-1));
				} else {
					this.moveCursorBy(-1);
				}

				return true;
			case 205:

				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(1, selEnd));
					} else {
						this.setSelectionPos(selEnd + 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(1));
				} else {
					this.moveCursorBy(1);
				}

				return true;
			case 207:

				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(this.text.length());
				} else {
					this.setCursorPositionEnd();
				}

				return true;
			case 211:

				if (GuiScreen.isCtrlKeyDown()) {
					if (this.enabled) {
						this.deleteWords(1);
					}
				} else if (this.enabled) {
					this.deleteFromCursor(1);
				}

				return true;
			default:

				if (ChatAllowedCharacters.isAllowedCharacter(character)) {
					if (this.enabled) {
						this.writeText(Character.toString(character));
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void updateCursorCounter() {
		++this.cursorCounter;
	}
	// public void onKeyUp(int key){}

	public void onTextChange() {
	}

	private void drawCursorVertical(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > this.posX + this.width) {
			endX = this.posX + this.width;
		}

		if (startX > this.posX + this.width) {
			startX = this.posX + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) startX, (double) endY, 0.0D).endVertex();
		vertexbuffer.pos((double) endX, (double) endY, 0.0D).endVertex();
		vertexbuffer.pos((double) endX, (double) startY, 0.0D).endVertex();
		vertexbuffer.pos((double) startX, (double) startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		int i = this.enabled ? this.enabledColor : this.disabledColor;
		int j = this.cursorPosition - this.scrollOffset;
		int k = this.selEnd - this.scrollOffset;
		String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
		boolean flag = j >= 0 && j <= s.length();
		boolean flag1 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;

		int l = 4;
		int i1 = (this.height - getContentOffset() - GuiRenderHelper.instance.getFontHeight()) / 2;
		int j1 = l;

		if (k > s.length()) {
			k = s.length();
		}

		if (s.length() > 0) {
			String s1 = flag ? s.substring(0, j) : s;
			j1 = GuiRenderHelper.instance.font.drawStringWithShadow(s1, l, i1, i);
		}

		boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxLength;
		int k1 = j1;

		if (!flag) {
			k1 = j > 0 ? l + this.width : l;
		} else if (flag2) {
			k1 = j1 - 1;
			--j1;
		}

		if (s.length() > 0 && flag && j < s.length()) {
			GuiRenderHelper.instance.font.drawStringWithShadow(s.substring(j), j1, i1, i);
		}

		if (flag1) {
			if (flag2) {
				Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + GuiRenderHelper.instance.getFontHeight(), -3092272);
			} else {
				GuiRenderHelper.instance.font.drawStringWithShadow("_", k1, i1, i);
			}
		}

		if (k != j) {
			int l1 = l + GuiRenderHelper.instance.font.getStringWidth(s.substring(0, k));
			this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + GuiRenderHelper.instance.getFontHeight());
		}
	}

}
