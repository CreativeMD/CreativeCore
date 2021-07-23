package team.creative.creativecore.client.render.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.text.IAdvancedTextComponent;
import team.creative.creativecore.common.util.type.SingletonList;

public class CompiledText {
    
    private static final Font font = Minecraft.getInstance().font;
    
    private int maxWidth;
    private int maxHeight;
    public int usedWidth;
    public int usedHeight;
    public int lineSpacing = 2;
    public boolean shadow = true;
    public int defaultColor = ColorUtils.WHITE;
    public Align alignment = Align.LEFT;
    private List<CompiledLine> lines;
    private List<Component> original;
    
    public CompiledText(int width, int height) {
        this.maxWidth = width;
        this.maxHeight = height;
        setText(Collections.EMPTY_LIST);
    }
    
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }
    
    public void setDimension(int width, int height) {
        this.maxWidth = width;
        this.maxHeight = height;
        compile();
    }
    
    public int getMaxWidht() {
        return maxWidth;
    }
    
    public int getMaxHeight() {
        return maxHeight;
    }
    
    public void setText(Component component) {
        setText(new SingletonList<Component>(component));
    }
    
    public void setText(List<Component> components) {
        this.original = components;
        compile();
    }
    
    private void compile() {
        List<Component> copy = new ArrayList<>();
        for (Component component : original)
            copy.add(component.plainCopy());
        lines = new ArrayList<>();
        compileNext(null, true, copy);
    }
    
    private CompiledLine compileNext(CompiledLine currentLine, boolean newLine, List<? extends FormattedText> components) {
        for (FormattedText component : components) {
            if (newLine)
                lines.add(currentLine = new CompiledLine());
            currentLine = compileNext(currentLine, component);
        }
        return currentLine;
    }
    
    private CompiledLine compileNext(CompiledLine currentLine, FormattedText component) {
        List<Component> siblings = null;
        if (component instanceof Component && !((Component) component).getSiblings().isEmpty()) {
            siblings = new ArrayList<>(((Component) component).getSiblings());
            ((Component) component).getSiblings().clear();
        }
        List<FormattedText> properties = currentLine.add(component);
        
        if (properties != null) {
            lines.add(currentLine = new CompiledLine());
            currentLine = compileNext(currentLine, false, properties);
        }
        
        if (siblings != null)
            currentLine = compileNext(currentLine, false, siblings);
        return currentLine;
    }
    
    public int getTotalHeight() {
        int height = -lineSpacing;
        for (CompiledLine line : lines)
            height += line.height + lineSpacing;
        return height;
    }
    
    public void calculateDimensions() {
        if (lines == null)
            return;
        
        usedWidth = 0;
        usedHeight = -lineSpacing;
        
        for (CompiledLine line : lines) {
            switch (alignment) {
            case LEFT:
                usedWidth = Math.max(usedWidth, line.width);
                break;
            case CENTER:
                usedWidth = Math.max(usedWidth, maxWidth);
                break;
            case RIGHT:
                usedWidth = Math.max(usedWidth, maxWidth);
                break;
            case STRETCH:
                break;
            default:
                break;
            }
            int height = line.height + lineSpacing;
            usedHeight += height;
            
            if (usedHeight > maxHeight)
                break;
        }
    }
    
    public void render(PoseStack stack) {
        if (lines == null)
            return;
        
        usedWidth = 0;
        usedHeight = -lineSpacing;
        
        stack.pushPose();
        for (CompiledLine line : lines) {
            switch (alignment) {
            case LEFT:
                line.render(stack);
                usedWidth = Math.max(usedWidth, line.width);
                break;
            case CENTER:
                stack.pushPose();
                stack.translate(maxWidth / 2 - line.width / 2, 0, 0);
                line.render(stack);
                usedWidth = Math.max(usedWidth, maxWidth);
                stack.popPose();
                break;
            case RIGHT:
                stack.pushPose();
                stack.translate(maxWidth - line.width, 0, 0);
                line.render(stack);
                usedWidth = Math.max(usedWidth, maxWidth);
                stack.popPose();
                break;
            case STRETCH:
                break;
            default:
                break;
            }
            int height = line.height + lineSpacing;
            stack.translate(0, height, 0);
            usedHeight += height;
            
            if (usedHeight > maxHeight)
                break;
        }
        
        stack.popPose();
    }
    
    public class CompiledLine {
        
        private List<FormattedText> components = new ArrayList<>();
        private int height = 0;
        private int width = 0;
        
        public CompiledLine() {
            
        }
        
        public void render(PoseStack stack) {
            int xOffset = 0;
            MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            for (FormattedText text : components) {
                int height;
                int width;
                if (text instanceof IAdvancedTextComponent) {
                    width = ((IAdvancedTextComponent) text).getWidth(font);
                    height = ((IAdvancedTextComponent) text).getHeight(font);
                } else {
                    width = font.width(text);
                    height = font.lineHeight;
                }
                
                int yOffset = 0;
                if (height < this.height)
                    yOffset = (this.height - height) / 2;
                stack.pushPose();
                stack.translate(xOffset, yOffset, 0);
                if (text instanceof IAdvancedTextComponent)
                    ((IAdvancedTextComponent) text).render(stack, font, defaultColor);
                else {
                    font.drawInBatch(Language.getInstance().getVisualOrder(text), 0, 0, defaultColor, shadow, stack.last().pose(), renderType, false, 0, 15728880);
                    renderType.endBatch();
                }
                stack.popPose();
                xOffset += width;
            }
        }
        
        public void updateDimension(int width, int height) {
            this.width = Math.max(width, this.width);
            this.height = Math.max(height, this.height);
        }
        
        public List<FormattedText> add(FormattedText component) {
            int remainingWidth = maxWidth - width;
            if (component instanceof IAdvancedTextComponent) {
                IAdvancedTextComponent advanced = (IAdvancedTextComponent) component;
                if (advanced.isEmpty())
                    return null;
                
                int textWidth = advanced.getWidth(font);
                if (remainingWidth > textWidth) {
                    components.add(advanced);
                    updateDimension(width + textWidth, advanced.getHeight(font));
                    return null;
                } else if (advanced.canSplit()) {
                    List<IAdvancedTextComponent> remaining = advanced.split(remainingWidth, width == 0);
                    IAdvancedTextComponent toAdd = remaining.remove(0);
                    components.add(toAdd);
                    updateDimension(width + toAdd.getWidth(font), toAdd.getHeight(font));
                    if (remaining.isEmpty())
                        return null;
                    else
                        return new SingletonList<>(remaining.get(0));
                } else if (width == 0) {
                    components.add(advanced);
                    updateDimension(width + textWidth, advanced.getHeight(font));
                    return null;
                } else
                    return new SingletonList<>(advanced);
            }
            
            int textWidth = font.width(component);
            
            if (remainingWidth > textWidth) {
                components.add(component);
                updateDimension(width + textWidth, font.lineHeight);
                return null;
            } else if (width == 0) {
                List<FormattedText> wrappedLines = font.getSplitter().splitLines(component, maxWidth - width, Style.EMPTY);
                updateDimension(width + font.width(wrappedLines.get(0)), font.lineHeight);
                if (wrappedLines.isEmpty())
                    return null;
                components.add(wrappedLines.get(0));
                wrappedLines.remove(0);
                if (wrappedLines.isEmpty())
                    return null;
                return wrappedLines;
            } else
                return new SingletonList<>(component);
        }
        
    }
    
    public int getTotalWidth() {
        return calculateWidth(0, true, original);
    }
    
    private int calculateWidth(int width, boolean newLine, List<? extends FormattedText> components) {
        for (FormattedText component : components) {
            int result = calculateWidth(component);
            if (newLine)
                width = Math.max(width, result);
            else
                width += result;
        }
        return width;
    }
    
    private int calculateWidth(FormattedText component) {
        int width = 0;
        if (component instanceof IAdvancedTextComponent) {
            IAdvancedTextComponent advanced = (IAdvancedTextComponent) component;
            if (!advanced.isEmpty())
                width += advanced.getWidth(font);
        } else
            width += font.width(component);
        
        if (component instanceof Component && !((Component) component).getSiblings().isEmpty())
            width += calculateWidth(0, false, ((Component) component).getSiblings());
        return width;
    }
    
    public static CompiledText createAnySize() {
        return new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
}
