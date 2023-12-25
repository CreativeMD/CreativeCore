package team.creative.creativecore.common.gui.controls.simple;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.controls.GuiFocusControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiTextfield extends GuiFocusControl {
    
    private String text = "";
    private int maxStringLength = 128;
    private int frame;
    private boolean shift;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private String suggestion;
    /** Called to check if the text is valid */
    private Predicate<String> validator = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> textFormatter = (text, pos) -> {
        return FormattedCharSequence.forward(text, Style.EMPTY);
    };
    private int cachedWidth;
    
    public GuiTextfield(String name) {
        super(name);
        setText("");
    }
    
    public GuiTextfield(String name, String text) {
        super(name);
        setText(text);
    }
    
    @Override
    public GuiTextfield setDim(int width, int height) {
        return (GuiTextfield) super.setDim(width, height);
    }
    
    public GuiTextfield setDim(int width) {
        return (GuiTextfield) super.setDim(width, 10);
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
    
    public double parseDouble() {
        try {
            return Double.parseDouble(text);
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
        ++this.frame;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        Font font = GuiRenderHelper.getFont();
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        GuiStyle style = getStyle();
        int color = enabled ? style.fontColor.toInt() : style.fontColorDisabled.toInt();
        String s = font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), (int) rect.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
        int yOffset = 0;
        int xOffset = 0;
        if (k > s.length())
            k = s.length();
        
        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            xOffset = graphics.drawString(font, this.textFormatter.apply(s1, this.lineScrollOffset), xOffset, yOffset, color, false) + 1;
        }
        
        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
        int k1 = xOffset;
        if (!flag)
            k1 = j > 0 ? control.getWidth() : 0;
        else if (flag2) {
            k1 = xOffset - 1;
            --xOffset;
        }
        
        if (!s.isEmpty() && flag && j < s.length())
            graphics.drawString(font, this.textFormatter.apply(s.substring(j), this.cursorPosition), xOffset, yOffset, color, false);
        
        if (!flag2 && this.suggestion != null)
            graphics.drawString(font, this.suggestion, k1 - 1, yOffset, -8355712);
        
        if (flag1)
            if (flag2)
                graphics.fill(k1, yOffset - 1, k1 + 1, yOffset + 1 + 9, -3092272);
            else
                graphics.drawString(font, "_", k1, yOffset, color);
            
        if (k != j) {
            int l1 = font.width(s.substring(0, k));
            this.drawSelectionBox(control, pose.last().pose(), k1, yOffset - 1, l1 - 1, yOffset + 1 + 9);
        }
    }
    
    public void setText(String textIn) {
        if (this.validator.test(textIn)) {
            if (textIn.length() > this.maxStringLength)
                this.text = textIn.substring(0, this.maxStringLength);
            else
                this.text = textIn;
            
            this.setCursorPositionZero();
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
        String s = SharedConstants.filterText(textToWrite);
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
        onTextChanged(text);
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
        this.shift = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
            
            return true;
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.writeText("");
            
            return true;
        } else {
            switch (keyCode) {
                case 259:
                    this.shift = false;
                    this.delete(-1);
                    this.shift = Screen.hasShiftDown();
                    
                    return true;
                case 258:
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                    return false;
                default:
                    return SharedConstants.isAllowedChatCharacter((char) keyCode);
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
    
    public boolean canWrite() {
        return isFocused();
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canWrite())
            return false;
        else if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            this.writeText(Character.toString(codePoint));
            return true;
        } else
            return false;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double mouseX, double mouseY, int button) {
        super.mouseClicked(rect, mouseX, mouseY, button);
        
        if (button == 0) {
            int i = Mth.floor(mouseX);
            Font fontRenderer = GuiRenderHelper.getFont();
            String s = fontRenderer.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), (int) rect.getWidth());
            this.shift = Screen.hasShiftDown();
            this.setCursorPosition(fontRenderer.plainSubstrByWidth(s, i).length() + this.lineScrollOffset);
            return true;
        }
        return false;
    }
    
    private void drawSelectionBox(GuiChildControl control, Matrix4f matrix, int startX, int startY, int endX, int endY) {
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
        
        if (endX > control.rect.maxX)
            endX = (int) control.rect.maxX;
        
        if (startX > control.rect.maxX)
            startX = (int) control.rect.maxX;
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(matrix, startX, endY, 0).endVertex();
        bufferbuilder.vertex(matrix, endX, endY, 0).endVertex();
        bufferbuilder.vertex(matrix, endX, startY, 0).endVertex();
        bufferbuilder.vertex(matrix, startX, startY, 0).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
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
        int textLength = this.text.length();
        this.selectionEnd = Mth.clamp(position, 0, textLength);
        if (getParent() == null || !hasLayer() || !isClient())
            return;
        Font fontRenderer = GuiRenderHelper.getFont();
        if (fontRenderer != null) {
            if (this.lineScrollOffset > textLength)
                this.lineScrollOffset = textLength;
            
            int j = cachedWidth;
            String s = fontRenderer.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            if (this.selectionEnd == this.lineScrollOffset)
                this.lineScrollOffset -= fontRenderer.plainSubstrByWidth(this.text, j, true).length();
            
            if (this.selectionEnd > k)
                this.lineScrollOffset += this.selectionEnd - k;
            else if (this.selectionEnd <= this.lineScrollOffset)
                this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
            
            this.lineScrollOffset = Mth.clamp(this.lineScrollOffset, 0, textLength);
        }
        
    }
    
    public void setSuggestion(@Nullable String p_195612_1_) {
        this.suggestion = p_195612_1_;
    }
}
