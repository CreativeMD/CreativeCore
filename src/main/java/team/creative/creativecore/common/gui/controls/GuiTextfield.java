package team.creative.creativecore.common.gui.controls;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.Rect;

public class GuiTextfield extends GuiFocusControl {
    
    private String text = "";
    private int maxStringLength = 128;
    private int cursorCounter;
    private boolean shift;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private String suggestion;
    /** Called to check if the text is valid */
    private Predicate<String> validator = Objects::nonNull;
    private BiFunction<String, Integer, IReorderingProcessor> textFormatter = (text, pos) -> {
        return IReorderingProcessor.fromString(text, Style.EMPTY);
    };
    
    public GuiTextfield(String name, int x, int y, int width) {
        this(name, x, y, width, 20);
    }
    
    public GuiTextfield(String name, int x, int y, int width, int height) {
        this(name, "", x, y, width, height);
    }
    
    public GuiTextfield(String name, String text, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        setText(text);
    }
    
    public GuiTextfield setFloatOnly() {
        validator = (x) -> {
            if (x.isEmpty())
                return true;
            try {
                Float.parseFloat(x);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
        return this;
    }
    
    public GuiTextfield setNumbersIncludingNegativeOnly() {
        validator = (x) -> {
            if (x.isEmpty())
                return true;
            try {
                Integer.parseInt(x);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
        return this;
    }
    
    public GuiTextfield setNumbersOnly() {
        validator = (x) -> {
            if (x.isEmpty())
                return true;
            try {
                return Integer.parseInt(x) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        };
        return this;
    }
    
    public float parseFloat() {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int parseInteger() {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {
        ++this.cursorCounter;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        FontRenderer fontRenderer = GuiRenderHelper.getFont();
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        GuiStyle style = getStyle();
        int color = enabled ? style.fontColor.toInt() : style.fontColorDisabled.toInt();
        String s = fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), (int) rect.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && flag;
        int i1 = 0;
        int j1 = 0;
        //if (k > s.length())
        //k = s.length();
        
        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = fontRenderer.func_238407_a_(matrix, this.textFormatter.apply(s1, this.lineScrollOffset), 0, i1, color);
        }
        
        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? this.getWidth() : 0;
        } else if (flag2) {
            k1 = j1 - 1;
            --j1;
        }
        
        if (!s.isEmpty() && flag && j < s.length())
            fontRenderer.func_238407_a_(matrix, this.textFormatter.apply(s.substring(j), this.cursorPosition), j1, i1, color);
        
        if (!flag2 && this.suggestion != null)
            fontRenderer.drawStringWithShadow(matrix, this.suggestion, k1 - 1, i1, -8355712);
        
        if (flag1)
            if (flag2)
                AbstractGui.fill(matrix, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            else
                fontRenderer.drawStringWithShadow(matrix, "_", k1, i1, color);
            
        if (k != j) {
            int l1 = fontRenderer.getStringWidth(s.substring(0, k));
            this.drawSelectionBox(matrix.getLast().getMatrix(), k1, i1 - 1, l1 - 1, i1 + 1 + 9);
        }
    }
    
    public void setText(String textIn) {
        if (this.validator.test(textIn)) {
            if (textIn.length() > this.maxStringLength)
                this.text = textIn.substring(0, this.maxStringLength);
            else
                this.text = textIn;
            
            this.setCursorPositionEnd();
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(textIn);
        }
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getSelectedText() {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }
    
    public void setValidator(Predicate<String> validatorIn) {
        this.validator = validatorIn;
    }
    
    /** Adds the given text after the cursor, or replaces the currently selected text if there is a selection. */
    public void writeText(String textToWrite) {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);
        String s = SharedConstants.filterAllowedCharacters(textToWrite);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }
        
        String s1 = (new StringBuilder(this.text)).replace(i, j, s).toString();
        if (this.validator.test(s1)) {
            this.text = s1;
            this.clampCursorPosition(i + l);
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(this.text);
        }
    }
    
    private void onTextChanged(String newText) {
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    private void delete(int p_212950_1_) {
        if (Screen.hasControlDown())
            this.deleteWords(p_212950_1_);
        else
            this.deleteFromCursor(p_212950_1_);
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
                int i = this.func_238516_r_(num);
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
        this.setCursorPosition(this.func_238516_r_(num));
    }
    
    private int func_238516_r_(int p_238516_1_) {
        return Util.func_240980_a_(this.text, this.cursorPosition, p_238516_1_);
    }
    
    public void setCursorPosition(int pos) {
        this.clampCursorPosition(pos);
        if (!this.shift)
            this.setSelectionPos(this.cursorPosition);
        
        this.onTextChanged(this.text);
    }
    
    public void clampCursorPosition(int pos) {
        this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
    }
    
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }
    
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.canWrite())
            return false;
        else {
            this.shift = Screen.hasShiftDown();
            if (Screen.isSelectAll(keyCode)) {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            } else if (Screen.isCopy(keyCode)) {
                Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
                return true;
            } else if (Screen.isPaste(keyCode)) {
                this.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
                
                return true;
            } else if (Screen.isCut(keyCode)) {
                Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
                this.writeText("");
                
                return true;
            } else {
                switch (keyCode) {
                case 259:
                    this.shift = false;
                    this.delete(-1);
                    this.shift = Screen.hasShiftDown();
                    
                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    return false;
                case 261:
                    this.shift = false;
                    this.delete(1);
                    this.shift = Screen.hasShiftDown();
                    
                    return true;
                case 262:
                    if (Screen.hasControlDown())
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    else
                        this.moveCursorBy(1);
                    
                    return true;
                case 263:
                    if (Screen.hasControlDown())
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
    }
    
    public boolean canWrite() {
        return isFocused();
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canWrite())
            return false;
        else if (SharedConstants.isAllowedCharacter(codePoint)) {
            this.writeText(Character.toString(codePoint));
            return true;
        } else
            return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        
        if (button == 0) {
            int i = MathHelper.floor(mouseX);
            FontRenderer fontRenderer = GuiRenderHelper.getFont();
            String s = fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), getContentWidth());
            this.setCursorPosition(fontRenderer.func_238412_a_(s, i).length() + this.lineScrollOffset);
            return true;
        }
        return false;
    }
    
    private void drawSelectionBox(Matrix4f matrix, int startX, int startY, int endX, int endY) {
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
        
        if (endX > this.getX() + this.getWidth())
            endX = this.getX() + this.getWidth();
        
        if (startX > this.getX() + this.getWidth())
            startX = this.getX() + this.getWidth();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(matrix, startX, endY, 0).endVertex();
        bufferbuilder.pos(matrix, endX, endY, 0).endVertex();
        bufferbuilder.pos(matrix, endX, startY, 0).endVertex();
        bufferbuilder.pos(matrix, startX, startY, 0).endVertex();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
    
    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
            this.onTextChanged(this.text);
        }
        
    }
    
    private int getMaxStringLength() {
        return this.maxStringLength;
    }
    
    public int getCursorPosition() {
        return this.cursorPosition;
    }
    
    @Override
    protected void focusChanged() {
        if (isFocused())
            this.cursorCounter = 0;
    }
    
    public void setSelectionPos(int position) {
        int textLength = this.text.length();
        this.selectionEnd = MathHelper.clamp(position, 0, textLength);
        if (getParent() == null || !hasLayer())
            return;
        FontRenderer fontRenderer = GuiRenderHelper.getFont();
        if (fontRenderer != null) {
            if (this.lineScrollOffset > textLength)
                this.lineScrollOffset = textLength;
            
            int j = this.getContentWidth();
            String s = fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            if (this.selectionEnd == this.lineScrollOffset)
                this.lineScrollOffset -= fontRenderer.func_238413_a_(this.text, j, true).length();
            
            if (this.selectionEnd > k)
                this.lineScrollOffset += this.selectionEnd - k;
            else if (this.selectionEnd <= this.lineScrollOffset)
                this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
            
            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, textLength);
        }
        
    }
    
    public void setSuggestion(@Nullable String p_195612_1_) {
        this.suggestion = p_195612_1_;
    }
}
