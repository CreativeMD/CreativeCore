package com.creativemd.creativecore.common.gui.controls.gui.text;

import java.awt.Toolkit;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.gui.GuiFocusControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.Pair;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

/** NOTES :: IMPORTANT!!!
 * 
 * for coordinate vectors I use an int[2] {column, row} array. This might be counterintuitive, but column = x and row = y. hence this choice.
 * 
 * 
 * Logical layout:
 * 
 * keyboard-input - native handling \
 * | - TextareaChange - update
 * mouse-input - native handling /
 * 
 * 
 * The native handling is supported by:
 * - IWordMatcher
 * - IWordWrapAlgorithm
 * 
 * The TextareaChange is meant for thirdparty users to give full control of the textarea-input-behavior
 * 
 * 
 * TODO:
 * - Rendering */

public class GuiTextarea extends GuiFocusControl {
    
    private static final int cursorBlinkRate = getBlinkRate();
    
    private static int getBlinkRate() {
        Integer br = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.cursorBlinkRate");
        return br != null ? br : 350;
    }
    
    public static final int DEFAULT_LINE_SPACING = 2;
    public static final int DEFAULT_TEXT_PADDING = 8;
    public static final Consumer<TextareaChange> DEFAULT_CONTENT_FORMATTER = x -> {};
    
    protected GuiTextareaContent content;
    protected GuiTextareaSelection selection;
    protected int lineSpacing = GuiTextarea.DEFAULT_LINE_SPACING;
    
    protected IWordWrapAlgorithm wordWrapAlgorithm = IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM;
    protected IWordMatcher wordMatcher = IWordMatcher.DEFAULT_MATCHER;
    protected Consumer<TextareaChange> contentFormatter = GuiTextarea.DEFAULT_CONTENT_FORMATTER;
    
    /** whether tabs should be captured as character input or not (is a traverse key otherwise) */
    protected boolean allowTabs = false;
    /** whether tabs should indent/outdent when a line/multiple lines are selected */
    protected boolean allowIndentation = false;
    /** whether to replace characters or just append */
    protected boolean replaceModeActive = false;
    
    protected int enabledColor = 14737632;
    protected int disabledColor = 7368816;
    
    public GuiTextarea(String text, int x, int y, int width) {
        this(text, x, y, width, 16);
    }
    
    public GuiTextarea(String text, int x, int y, int width, int height) {
        this(text, text, x, y, width, height);
    }
    
    public GuiTextarea(String name, String text, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.selection = new GuiTextareaSelection(this);
        this.content = new GuiTextareaContent(this, text);
    }
    
    // GETTERS
    
    public GuiTextareaContent getContent() {
        return this.content;
    }
    
    public GuiTextareaSelection getSelection() {
        return this.selection;
    }
    
    public String getText() {
        return this.content.getText();
    }
    
    public boolean hasSelectedText() {
        return this.selection.hasSelectedText();
    }
    
    public String getSelectedText() {
        return this.selection.getSelectedText();
    }
    
    public IWordWrapAlgorithm getWordWrapAlgorithm() {
        return this.wordWrapAlgorithm != null ? this.wordWrapAlgorithm : IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM;
    }
    
    public IWordMatcher getWordMatcher() {
        return this.wordMatcher != null ? this.wordMatcher : IWordMatcher.DEFAULT_MATCHER;
    }
    
    public Consumer<TextareaChange> getContentFormatter() {
        return this.contentFormatter != null ? this.contentFormatter : GuiTextarea.DEFAULT_CONTENT_FORMATTER;
    }
    
    public void setContentFormatter(Consumer<TextareaChange> formatter) {
        this.contentFormatter = formatter;
        this.onContentChange(new TextareaChange(this, this.content.getText(), 0, this.content.getText().length(), this.selection.getCursorOffset(), this.selection.getAnchorOffset()));
    }
    
    @Override
    public boolean mousePressed(int posX, int posY, int button) {
        boolean wasFocused = focused;
        super.mousePressed(posX, posY, button);
        
        TextareaVec vec = this.rayTrace(posX, posY);
        
        if (GuiScreen.isShiftKeyDown())
            this.selection.selectTo(vec.row, vec.column);
        else
            this.selection.setCursorPosition(vec.row, vec.column);
        
        return true;
    }
    
    @Override
    public boolean onKeyPressed(char character, int key) {
        
        if (!this.focused)
            return false;
        
        if (GuiScreen.isKeyComboCtrlA(key)) {
            this.selection.selectAll();
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(key)) {
            if (this.selection.hasSelectedText())
                GuiScreen.setClipboardString(this.getSelectedText());
            else
                GuiScreen.setClipboardString(this.content.getLine(this.selection.getCursorPosition().row));
            return true;
        } else if (GuiScreen.isKeyComboCtrlX(key)) {
            if (this.selection.hasSelectedText())
                GuiScreen.setClipboardString(this.getSelectedText());
            else
                GuiScreen.setClipboardString(this.content.getLine(this.selection.getCursorPosition().row));
            
            if (this.enabled)
                this.content.replaceSelection("");
        } else if (GuiScreen.isKeyComboCtrlV(key)) {
            if (this.enabled)
                this.content.replaceSelection(GuiScreen.getClipboardString());
            return true;
        }
        
        switch (key) {
        case Keyboard.KEY_BACK:
            if (this.enabled) {
                if (GuiScreen.isCtrlKeyDown())
                    this.content.backspaceWordAtCursor();
                else
                    this.content.backspaceAtCursor();
            }
            return true;
        
        case Keyboard.KEY_DELETE:
            if (this.enabled) {
                if (GuiScreen.isCtrlKeyDown())
                    this.content.deleteWordAtCursor();
                else
                    this.content.deleteAtCursor();
            }
            return true;
        
        case Keyboard.KEY_INSERT:
            this.replaceModeActive = !this.replaceModeActive;
            return true;
        
        case Keyboard.KEY_TAB:
            if (this.enabled) {
                if (this.allowIndentation) {
                    if (GuiScreen.isShiftKeyDown()) {
                        this.content.outdentSelection();
                        return true;
                    } else {
                        Pair<TextareaVec, TextareaVec> sel = this.selection.getSelectionPositions();
                        if (sel.key.row != sel.value.row || (sel.key.column == 0 && sel.value.column == this.content.getLine(sel.value.row).length())) {
                            this.content.indentSelection();
                            return true;
                        }
                    }
                }
                
                if (this.allowTabs) {
                    this.content.typeText(character);
                    return true;
                }
            }
            return false;
        
        case Keyboard.KEY_PRIOR:
            if (GuiScreen.isShiftKeyDown())
                this.selection.selectToStart();
            else
                this.selection.moveCursorToStart();
            return true;
        
        case Keyboard.KEY_NEXT:
            if (GuiScreen.isShiftKeyDown())
                this.selection.selectToEnd();
            else
                this.selection.moveCursorToEnd();
            return true;
        
        case Keyboard.KEY_HOME:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToStart();
                else
                    this.selection.moveCursorToStart();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToLineStart();
                else
                    this.selection.moveCursorToLineStart();
            }
            return true;
        
        case Keyboard.KEY_END:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToEnd();
                else
                    this.selection.moveCursorToEnd();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToLineEnd();
                else
                    this.selection.moveCursorToLineEnd();
            }
            return true;
        
        case Keyboard.KEY_UP:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToLineEnd();
                else
                    this.selection.moveCursorToLineEnd();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectUp();
                else
                    this.selection.moveCursorUp();
            }
            return true;
        
        case Keyboard.KEY_DOWN:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToLineStart();
                else
                    this.selection.moveCursorToLineStart();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectDown();
                else
                    this.selection.moveCursorDown();
            }
            return true;
        
        case Keyboard.KEY_LEFT:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToPreviousWord();
                else
                    this.selection.moveCursorToPreviousWordStart();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectLeft();
                else {
                    if (this.selection.hasSelectedText())
                        this.selection.moveCursorToSelectionStart();
                    else
                        this.selection.moveCursorLeft();
                }
            }
            return true;
        
        case Keyboard.KEY_RIGHT:
            if (GuiScreen.isCtrlKeyDown()) {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectToNextWord();
                else
                    this.selection.moveCursorToNextWordEnd();
            } else {
                if (GuiScreen.isShiftKeyDown())
                    this.selection.selectRight();
                else {
                    if (this.selection.hasSelectedText())
                        this.selection.moveCursorToSelectionEnd();
                    else
                        this.selection.moveCursorRight();
                }
            }
            return true;
        
        case Keyboard.KEY_NONE:
            return false;
        
        case Keyboard.KEY_A:
            if (GuiScreen.isCtrlKeyDown()) {
                this.selection.selectAll();
                return true;
            }
        default:
            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                this.content.typeText(character);
                return true;
            }
            return false;
        }
    }
    
    protected void drawInverted(int startX, int startY, int endX, int endY) {
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
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(startX, endY, 0.0D).endVertex();
        vertexbuffer.pos(endX, endY, 0.0D).endVertex();
        vertexbuffer.pos(endX, startY, 0.0D).endVertex();
        vertexbuffer.pos(startX, startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
    
    @Override
    protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
        
        List<String> ls = this.content.getLines();
        Pair<TextareaVec, TextareaVec> sel = this.selection.getSelectionRangePositions();
        boolean hasSelection = this.selection.hasSelectedText();
        TextareaVec cur = this.selection.getCursorPosition();
        int color = this.enabled ? this.enabledColor : this.disabledColor;
        int lh = GuiRenderHelper.instance.getFontHeight();
        
        int y = 0 - (this.lineSpacing / 2);
        for (int i = 0; i < ls.size(); i++) {
            
            String l = ls.get(i);
            GuiRenderHelper.instance.font.drawString(l, 0, y, color);
            
            if (hasSelection && i >= sel.key.row && i <= sel.value.row) {
                int ss = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, (sel.key.row == i ? sel.key.column : 0)));
                int se = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, (sel.value.row == i ? sel.value.column : l.length())));
                Gui.drawRect(ss - 1, y - 1, se + 1, y + 1 + lh, focused ? ColorUtils.RGBAToInt(208, 208, 255, 128) : ColorUtils.RGBAToInt(208, 208, 208, 208));
            }
            
            // cursor
            if (focused && (System.currentTimeMillis() / this.cursorBlinkRate) % 2 == 0 && i == cur.row) {
                int cs = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, cur.column));
                if (this.replaceModeActive) {
                    int ce = cs + GuiRenderHelper.instance.font.getCharWidth(l.charAt(cur.column));
                    this.drawInverted(cs - 1, y - 1, ce + 1, y + 1 + lh);
                } else {
                    if (this.selection.getCursorOffset() == this.content.getText().length()) {
                        int nlw = GuiRenderHelper.instance.font.getStringWidth(l + "_");
                        if (nlw > this.width - (this.getContentOffset() * 2))
                            GuiRenderHelper.instance.font.drawStringWithShadow("_", 0, y + lh + this.lineSpacing, color);
                        else
                            GuiRenderHelper.instance.font.drawStringWithShadow("_", nlw - GuiRenderHelper.instance.font.getCharWidth('_'), y, color);
                    } else
                        this.drawInverted(cs, y - 1, cs + 1, y + 1 + lh);
                }
            }
            
            y += lh + this.lineSpacing;
        }
    }
    
    // INTERNAL
    
    protected void onContentChange(TextareaChange change) {
        if (change != null) {
            this.getContentFormatter().accept(change);
            
            change.validate();
            
            this.content.doContentChanged(change);
            this.selection.doSelectionChanged(change);
            
            raiseEvent(new GuiControlChangedEvent(this));
        }
    }
    
    protected TextareaVec rayTrace(int posX, int posY) {
        int row = this.rayTraceLine(posY);
        int column = this.rayTraceColumn(row, posX);
        return new TextareaVec(column, row);
    }
    
    protected int rayTraceLine(int posY) {
        int y = posY - (this.posY);
        int lh = GuiRenderHelper.instance.getFontHeight();
        
        int row;
        if (y < this.marginWidth)
            return 0;
        else
            return Math.min((y - (this.lineSpacing / 2)) / (lh + this.lineSpacing), this.getContent().getLineCount() - 1);
    }
    
    protected int rayTraceColumn(int row, int posX) {
        int x = posX - (this.posX);
        
        int column;
        if (x < this.marginWidth)
            return 0;
        else {
            String rs = this.getContent().getLine(row);
            String s = GuiRenderHelper.instance.font.trimStringToWidth(rs, x - this.marginWidth);
            return Math.min(s.length(), rs.length() - (this.replaceModeActive && row != this.content.getLineCount() - 1 ? 1 : 0));
        }
    }
    
    protected TextareaVec positionToCoordinates(int row, int column) {
        return new TextareaVec(this.columnToXpos(row, column), this.rowToYpos(row));
    }
    
    protected int rowToYpos(int row) {
        int lh = GuiRenderHelper.instance.getFontHeight();
        return this.marginWidth + (row * (lh + this.lineSpacing));
    }
    
    protected int columnToXpos(int row, int column) {
        String s = this.content.getLine(row).substring(0, column);
        return this.marginWidth + GuiRenderHelper.instance.font.getStringWidth(s);
    }
}