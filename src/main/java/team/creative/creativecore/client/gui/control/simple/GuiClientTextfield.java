package team.creative.creativecore.client.gui.control.simple;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Util;
import team.creative.creativecore.client.gui.control.GuiFocusControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield.GuiTextfieldDist;
import team.creative.creativecore.common.gui.event.GuiTextUpdateEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;

public class GuiClientTextfield<T extends GuiTextfield> extends GuiFocusControl<T> implements GuiTextfieldDist {
    
    private String text = "";
    
    private int maxStringLength = 128;
    /** Called to check if the text is valid */
    
    private String suggestion = "";
    
    private int frame;
    private boolean shift;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int cachedWidth;
    
    private final BiFunction<String, Integer, FormattedCharSequence> textFormatter = (text, pos) -> FormattedCharSequence.forward(text, Style.EMPTY);
    private Predicate<String> validator = Objects::nonNull;
    
    public GuiClientTextfield(T control) {
        super(control);
    }
    
    @Override
    public void setFloatOnly() {
        validator = (x) -> {
            if (x.isEmpty() || x.equalsIgnoreCase("-"))
                return true;
            try {
                Float.parseFloat(x);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
    
    @Override
    public void setNumbersIncludingNegativeOnly() {
        validator = (x) -> {
            if (x.isEmpty() || x.equalsIgnoreCase("-"))
                return true;
            try {
                Integer.parseInt(x);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
    
    @Override
    public void setNumbersOnly() {
        validator = (x) -> {
            if (x.isEmpty())
                return true;
            try {
                return Integer.parseInt(x) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
    
    @Override
    public void setText(String textIn, boolean notify) {
        if (this.validator.test(textIn)) {
            if (textIn.length() > this.maxStringLength)
                this.text = textIn.substring(0, this.maxStringLength);
            else
                this.text = textIn;
            
            this.setCursorPositionZero();
            this.setSelectionPos(this.cursorPosition);
            if (notify)
                this.onTextChanged(textIn);
        }
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    private void onTextChanged(String newText) {
        this.raiseEvent(new GuiTextUpdateEvent(control));
    }
    
    @Override
    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }
    
    @Override
    public void tick() {
        ++this.frame;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        var font = ((CreativeGuiGraphics) graphics).font();
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        GuiStyle style = getStyle();
        int color = enabled ? style.fontColor.toInt() : style.fontColorDisabled.toInt();
        String s = font.plainSubstrByWidth(text.substring(this.lineScrollOffset), rect.getContentWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
        int yOffset = 0;
        int xOffset = 0;
        if (k > s.length())
            k = s.length();
        
        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            var textFormatted = this.textFormatter.apply(s1, this.lineScrollOffset);
            graphics.drawString(font, textFormatted, xOffset, yOffset, color, false);
            xOffset = font.width(textFormatted) + 1;
        }
        
        boolean flag2 = this.cursorPosition < text.length() || text.length() >= maxStringLength;
        int k1 = xOffset;
        if (!flag)
            k1 = j > 0 ? rect.getContentWidth() : 0;
        else if (flag2) {
            k1 = xOffset - 1;
            --xOffset;
        }
        
        if (!s.isEmpty() && flag && j < s.length())
            graphics.drawString(font, this.textFormatter.apply(s.substring(j), this.cursorPosition), xOffset, yOffset, color, false);
        
        if (text.isEmpty() && !this.suggestion.isEmpty())
            graphics.drawString(font, this.suggestion, k1 - 1, yOffset, -8355712);
        
        if (flag1)
            if (flag2)
                graphics.fill(k1, yOffset - 1, k1 + 1, yOffset + 1 + 9, -3092272);
            else
                graphics.drawString(font, "_", k1, yOffset, color);
            
        if (k != j) {
            int l1 = font.width(s.substring(0, k));
            this.drawSelectionBox(graphics, k1, yOffset - 1, l1 - 1, yOffset + 1 + 9);
        }
    }
    
    private void drawSelectionBox(GuiGraphics graphics, int startX, int startY, int endX, int endY) {
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
        
        if (endX > rect.getRight())
            endX = rect.getRight();
        
        if (startX > rect.getRight())
            startX = rect.getRight();
        
        graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, startX, startY, endX, endY, -16776961);
    }
    
    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return text.substring(i, j);
    }
    
    /** Adds the given text after the cursor, or replaces the currently selected text if there is a selection. */
    public void writeText(String textToWrite) {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = maxStringLength - text.length() - (i - j);
        String s = StringUtil.filterText(textToWrite);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }
        
        String s1 = (new StringBuilder(text)).replace(i, j, s).toString();
        if (this.validator.test(s1)) {
            this.text = s1;
            this.clampCursorPosition(i + l);
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(this.text);
        }
    }
    
    private void delete(int p_212950_1_, KeyEvent key) {
        if (key.hasControlDown())
            this.deleteWords(p_212950_1_);
        else
            this.deleteFromCursor(p_212950_1_);
        this.onTextChanged(text);
    }
    
    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition)
                this.writeText("");
            else
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
        }
    }
    
    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition)
                this.writeText("");
            else {
                int i = this.getCursorPos(num);
                int j = Math.min(i, this.cursorPosition);
                int k = Math.max(i, this.cursorPosition);
                if (j != k) {
                    String s = (new StringBuilder(this.text)).delete(j, k).toString();
                    if (this.validator.test(s)) {
                        this.text = s;
                        this.setCursorPosition(j);
                    }
                }
            }
        }
    }
    
    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }
    
    private int getNthWordFromPos(int n, int pos) {
        return this.getNthWordFromPosWS(n, pos, true);
    }
    
    private int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);
        
        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1)
                    i = l;
                else
                    while (skipWs && i < l && this.text.charAt(i) == ' ')
                        ++i;
            } else {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ')
                    --i;
                
                while (i > 0 && this.text.charAt(i - 1) != ' ')
                    --i;
                
            }
        }
        
        return i;
    }
    
    public void moveCursorBy(int num) {
        this.setCursorPosition(this.getCursorPos(num));
    }
    
    private int getCursorPos(int p_238516_1_) {
        return Util.offsetByCodepoints(this.text, this.cursorPosition, p_238516_1_);
    }
    
    public void setCursorPosition(int pos) {
        this.clampCursorPosition(pos);
        if (!this.shift)
            this.setSelectionPos(this.cursorPosition);
        
        //this.onTextChanged(this.text);
    }
    
    public void clampCursorPosition(int pos) {
        this.cursorPosition = Mth.clamp(pos, 0, this.text.length());
    }
    
    @Override
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }
    
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    
    @Override
    public boolean keyPressed(KeyEvent key) {
        if (!this.canWrite())
            return false;
        this.shift = key.hasShiftDown();
        if (key.isSelectAll()) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (key.isCopy()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        } else if (key.isPaste()) {
            this.writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
            
            return true;
        } else if (key.isCut()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.writeText("");
            
            return true;
        } else {
            switch (key.key()) {
                case 259:
                    this.shift = false;
                    this.delete(-1, key);
                    this.shift = key.hasShiftDown();
                    
                    return true;
                case 258:
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                    return false;
                default:
                    return StringUtil.isAllowedChatCharacter((char) key.key());
                case 261:
                    this.shift = false;
                    this.delete(1, key);
                    this.shift = key.hasShiftDown();
                    
                    return true;
                case 262:
                    if (key.hasControlDown())
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    else
                        this.moveCursorBy(1);
                    
                    return true;
                case 263:
                    if (key.hasControlDown())
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    else
                        this.moveCursorBy(-1);
                    
                    return true;
                case 268:
                    this.setCursorPositionZero();
                    return true;
                case 269:
                    this.setCursorPositionEnd();
                    return true;
            }
        }
    }
    
    public boolean canWrite() {
        return isFocused();
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.canWrite())
            return false;
        else if (StringUtil.isAllowedChatCharacter(event.codepoint())) {
            this.writeText(Character.toString(event.codepoint()));
            return true;
        } else
            return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, MouseButtonInfo info) {
        super.mouseClicked(mouseX, mouseY, info);
        
        if (info.button() == 0) {
            int i = Mth.floor(mouseX);
            Font fontRenderer = Minecraft.getInstance().font;
            String s = fontRenderer.plainSubstrByWidth(text.substring(this.lineScrollOffset), rect.getContentWidth());
            this.shift = info.hasShiftDown();
            this.setCursorPosition(fontRenderer.plainSubstrByWidth(s, i).length() + this.lineScrollOffset);
            return true;
        }
        return false;
    }
    
    public int getCursorPosition() {
        return this.cursorPosition;
    }
    
    @Override
    protected void focusChanged() {
        if (isFocused())
            this.frame = 0;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        cachedWidth = width - getContentOffset() * 2;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 10;
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 40;
    }
    
    public void setSelectionPos(int position) {
        int textLength = text.length();
        this.selectionEnd = Mth.clamp(position, 0, textLength);
        Font fontRenderer = Minecraft.getInstance().font;
        if (fontRenderer != null) {
            if (this.lineScrollOffset > textLength)
                this.lineScrollOffset = textLength;
            
            int j = cachedWidth;
            String s = fontRenderer.plainSubstrByWidth(text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            if (this.selectionEnd == this.lineScrollOffset)
                this.lineScrollOffset -= fontRenderer.plainSubstrByWidth(text, j, true).length();
            
            if (this.selectionEnd > k)
                this.lineScrollOffset += this.selectionEnd - k;
            else if (this.selectionEnd <= this.lineScrollOffset)
                this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
            
            this.lineScrollOffset = Mth.clamp(this.lineScrollOffset, 0, textLength);
        }
        
    }
    
    @Override
    public void setValidator(Predicate<String> validatorIn) {
        this.validator = validatorIn;
    }
    
    @Override
    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
            this.onTextChanged(this.text);
        }
    }
    
}
