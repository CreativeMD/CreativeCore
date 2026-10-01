package team.creative.creativecore.client.gui.control.simple;

import java.util.HexFormat;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
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
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiClientTextfield<T extends GuiTextfield> extends GuiFocusControl<T> implements GuiTextfieldDist {
    
    private String text = "";
    
    private int maxLength = 128;
    
    private String suggestion = "";
    
    private boolean invertHighlightedTextColor = true;
    
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    
    private long focusedTime = Util.getMillis();
    
    private Function<String, String> modifyPaste = null;
    private final BiFunction<String, Integer, FormattedCharSequence> textFormatter = (text, pos) -> FormattedCharSequence.forward(text, Style.EMPTY);
    /** Called to check if the text is valid */
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
                return x.equals(".");
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
    public void setHexOnly() {
        modifyPaste = x -> x.replace("#", "");
        validator = x -> {
            try {
                HexFormat.fromHexDigits(x);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
    
    @Override
    public void setText(String value, boolean notify, boolean keepCursor) {
        if (this.validator.test(value)) {
            if (value.length() > this.maxLength)
                this.text = value.substring(0, this.maxLength);
            else
                this.text = value;
            
            if (!keepCursor) {
                this.moveCursorToEnd(false);
                this.setHighlightPos(this.cursorPos);
            }
            if (notify)
                this.onTextChanged(value);
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
    
    public String getHighlighted() {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        return this.text.substring(start, end);
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    protected void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
    
    @Override
    protected void renderContent(GuiGraphicsExtractor graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        var font = ((CreativeGuiGraphics) graphics).font();
        GuiStyle style = getStyle();
        int color = enabled ? style.fontColor.toInt() : style.fontColorDisabled.toInt();
        int relCursorPos = this.cursorPos - this.displayPos;
        String displayed = font.plainSubstrByWidth(this.text.substring(this.displayPos), rect.getContentWidth());
        boolean cursorOnScreen = relCursorPos >= 0 && relCursorPos <= displayed.length();
        boolean showCursor = this.isFocused() && TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime) && cursorOnScreen;
        int drawX = 0;
        int drawY = 0;
        int relHighlightPos = Mth.clamp(this.highlightPos - this.displayPos, 0, displayed.length());
        if (!displayed.isEmpty()) {
            String half = cursorOnScreen ? displayed.substring(0, relCursorPos) : displayed;
            FormattedCharSequence charSequence = this.textFormatter.apply(half, this.displayPos);
            graphics.text(font, charSequence, drawX, drawY, color, false);
            drawX += font.width(charSequence) + 1;
        }
        
        boolean insert = this.cursorPos < this.text.length() || this.text.length() >= this.maxLength;
        int cursorX = drawX;
        if (!cursorOnScreen)
            cursorX = relCursorPos > 0 ? rect.getContentWidth() : 0;
        else if (insert) {
            cursorX--;
            drawX--;
        }
        
        if (!displayed.isEmpty() && cursorOnScreen && relCursorPos < displayed.length())
            graphics.text(font, this.textFormatter.apply(displayed.substring(relCursorPos), this.cursorPos), drawX, drawY, color, false);
        
        if (!insert && this.suggestion != null)
            graphics.text(font, this.suggestion, cursorX - 1, drawY, -8355712, false);
        
        if (relHighlightPos != relCursorPos) {
            int highlightX = font.width(displayed.substring(0, relHighlightPos));
            graphics.textHighlight(Math.min(cursorX, rect.getRight()), drawY - 1, Math.min(highlightX - 1, rect.getRight()), drawY + 1 + 9, this.invertHighlightedTextColor);
        }
        
        if (showCursor)
            if (insert)
                TextCursorUtils.extractInsertCursor(graphics, cursorX, drawY, color, 9 + 1);
            else
                TextCursorUtils.extractAppendCursor(graphics, font, cursorX, drawY, color, false);
            
        if (realRect.inside(mouseX, mouseY))
            graphics.requestCursor(CursorTypes.IBEAM);
    }
    
    /** Adds the given text after the cursor, or replaces the currently selected text if there is a selection. */
    public void writeText(String textToWrite) {
        if (!this.validator.test(textToWrite))
            return;
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        int maxInsertionLength = this.maxLength - this.text.length() - (start - end);
        if (maxInsertionLength > 0) {
            String text = StringUtil.filterText(textToWrite);
            int insertionLength = text.length();
            if (maxInsertionLength < insertionLength) {
                if (Character.isHighSurrogate(text.charAt(maxInsertionLength - 1))) {
                    maxInsertionLength--;
                }
                
                text = text.substring(0, maxInsertionLength);
                insertionLength = maxInsertionLength;
            }
            
            this.text = new StringBuilder(this.text).replace(start, end, text).toString();
            this.setCursorPosition(start + insertionLength);
            this.setHighlightPos(this.cursorPos);
            this.onTextChanged(this.text);
        }
    }
    
    private void deleteText(int dir, boolean wholeWord) {
        if (wholeWord)
            this.deleteWords(dir);
        else
            this.deleteChars(dir);
    }
    
    public void deleteWords(int dir) {
        if (!this.text.isEmpty()) {
            if (this.highlightPos != this.cursorPos)
                this.writeText("");
            else
                this.deleteCharsToPos(this.getWordPosition(dir));
        }
    }
    
    public void deleteChars(int dir) {
        this.deleteCharsToPos(this.getCursorPos(dir));
    }
    
    public void deleteCharsToPos(int pos) {
        if (this.text.isEmpty())
            return;
        if (this.highlightPos != this.cursorPos)
            this.writeText("");
        else {
            int start = Math.min(pos, this.cursorPos);
            int end = Math.max(pos, this.cursorPos);
            if (start != end) {
                this.text = new StringBuilder(text).delete(start, end).toString();
                this.setCursorPosition(start);
                this.onTextChanged(this.text);
                this.moveCursorTo(start, false);
            }
        }
        
    }
    
    public int getWordPosition(int dir) {
        return this.getWordPosition(dir, this.getCursorPosition());
    }
    
    private int getWordPosition(int dir, int from) {
        return this.getWordPosition(dir, from, true);
    }
    
    private int getWordPosition(int dir, int from, boolean stripSpaces) {
        int result = from;
        boolean reverse = dir < 0;
        int abs = Math.abs(dir);
        
        for (int i = 0; i < abs; i++) {
            if (!reverse) {
                int length = this.text.length();
                result = this.text.indexOf(32, result);
                if (result == -1) {
                    result = length;
                } else {
                    while (stripSpaces && result < length && this.text.charAt(result) == ' ') {
                        result++;
                    }
                }
            } else {
                while (stripSpaces && result > 0 && this.text.charAt(result - 1) == ' ') {
                    result--;
                }
                
                while (result > 0 && this.text.charAt(result - 1) != ' ') {
                    result--;
                }
            }
        }
        
        return result;
    }
    
    public void moveCursor(int dir, boolean hasShiftDown) {
        this.moveCursorTo(this.getCursorPos(dir), hasShiftDown);
    }
    
    private int getCursorPos(int dir) {
        return Util.offsetByCodepoints(this.text, this.cursorPos, dir);
    }
    
    public void moveCursorTo(int dir, boolean extendSelection) {
        this.setCursorPosition(dir);
        if (!extendSelection)
            this.setHighlightPos(this.cursorPos);
    }
    
    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.text.length());
        this.scrollTo(this.cursorPos);
    }
    
    public void moveCursorToStart(boolean hasShiftDown) {
        this.moveCursorTo(0, hasShiftDown);
    }
    
    @Override
    public void moveCursorToEnd(boolean hasShiftDown) {
        this.moveCursorTo(this.text.length(), hasShiftDown);
    }
    
    @Override
    public boolean keyPressed(KeyEvent key) {
        if (!this.canWrite())
            return false;
        
        switch (key.shortcutKey()) {
            case InputConstants.KEYCODE_BACKSPACE -> deleteText(-1, key.hasControlDownWithQuirk());
            case InputConstants.KEYCODE_DELETE -> deleteText(1, key.hasControlDownWithQuirk());
            case InputConstants.KEYCODE_HOME -> moveCursorToStart(key.hasShiftDown());
            case InputConstants.KEYCODE_END -> moveCursorToEnd(key.hasShiftDown());
            case InputConstants.KEYCODE_RIGHT -> {
                if (key.hasControlDownWithQuirk())
                    this.moveCursorTo(this.getWordPosition(1), key.hasShiftDown());
                else
                    this.moveCursor(1, key.hasShiftDown());
            }
            case InputConstants.KEYCODE_LEFT -> {
                if (key.hasControlDownWithQuirk())
                    this.moveCursorTo(this.getWordPosition(-1), key.hasShiftDown());
                else
                    this.moveCursor(-1, key.hasShiftDown());
            }
            default -> {
                if (key.isSelectAll()) {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                } else if (key.isCopy()) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    return true;
                } else if (key.isPaste()) {
                    var s = Minecraft.getInstance().keyboardHandler.getClipboard();
                    if (modifyPaste != null)
                        s = modifyPaste.apply(s);
                    this.writeText(s);
                } else if (key.isCut()) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    this.writeText("");
                } else
                    return StringUtil.isAllowedChatCharacter((char) key.keycode());
            }
        }
        
        return true;
    }
    
    public boolean canWrite() {
        return isFocused();
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.canWrite())
            return false;
        else if (event.isAllowedChatCharacter()) {
            this.writeText(Character.toString(event.codepoint()));
            return true;
        } else
            return false;
    }
    
    private int findClickedPositionInText(double x, MouseButtonInfo event) {
        int i = Mth.floor(x);
        Font font = Minecraft.getInstance().font;
        String s = font.plainSubstrByWidth(text.substring(this.displayPos), rect.getContentWidth());
        return font.plainSubstrByWidth(s, i).length() + this.displayPos;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        super.mouseClicked(x, y, info);
        
        if (info.button() == 1) {
            this.moveCursorTo(findClickedPositionInText(x, info), info.hasShiftDown());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseDoubleClicked(double x, double y, MouseButtonInfo info) {
        int clickedPosition = this.findClickedPositionInText(x, info);
        int wordStart = this.getWordPosition(-1, clickedPosition);
        int wordEnd = this.getWordPosition(1, clickedPosition);
        this.moveCursorTo(wordStart, false);
        this.moveCursorTo(wordEnd, true);
        return true;
    }
    
    @Override
    public void mouseDragged(double x, double y, MouseButtonInfo info, double dragX, double dragY, double time) {
        if (isFocused())
            this.moveCursorTo(this.findClickedPositionInText(x, info), true);
    }
    
    @Override
    public boolean testForDoubleClick(double x, double y, MouseButtonInfo info) {
        return true;
    }
    
    public int getCursorPosition() {
        return this.cursorPos;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
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
    
    public void setHighlightPos(int pos) {
        this.highlightPos = Mth.clamp(pos, 0, this.text.length());
        this.scrollTo(this.highlightPos);
    }
    
    public void scrollTo(int pos) {
        var font = Minecraft.getInstance().font;
        if (font != null && rect.getContentWidth() > 0) {
            this.displayPos = Math.min(this.displayPos, this.text.length());
            String displayed = font.plainSubstrByWidth(this.text.substring(this.displayPos), rect.getContentWidth());
            int lastPos = displayed.length() + this.displayPos;
            if (pos == this.displayPos)
                this.displayPos = this.displayPos - font.plainSubstrByWidth(this.text, rect.getContentWidth(), true).length();
            
            if (pos > lastPos)
                this.displayPos += pos - lastPos;
            else if (pos <= this.displayPos)
                this.displayPos = this.displayPos - (this.displayPos - pos);
            
            this.displayPos = Mth.clamp(this.displayPos, 0, this.text.length());
        }
    }
    
    @Override
    public void setValidator(Predicate<String> validatorIn) {
        this.validator = validatorIn;
    }
    
    @Override
    public void setMaxStringLength(int length) {
        this.maxLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
            this.onTextChanged(this.text);
        }
    }
    
}
