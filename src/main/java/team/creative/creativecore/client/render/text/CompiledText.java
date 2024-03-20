package team.creative.creativecore.client.render.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.text.AdvancedComponent;
import team.creative.creativecore.common.util.type.list.SingletonList;

public class CompiledText {
    public static final CompiledText EMPTY = new CompiledText(0, 0);

    private int maxWidth;
    private int maxHeight;
    public int usedWidth;
    public int usedHeight;
    public int lineSpacing = 2;
    public boolean shadow = true;
    public int defaultColor = ColorUtils.WHITE;
    public Align alignment = Align.LEFT;
    public VAlign vAlign = VAlign.TOP;
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
        setText(new SingletonList<>(component));
    }
    
    public void setText(List<Component> components) {
        this.original = components;
        compile();
    }

    public CompiledText setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        return this;
    }

    public CompiledText setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public CompiledText setVAlign(VAlign vAlign) {
        this.vAlign = vAlign;
        return this;
    }

    public boolean contains(String value) {
        for (Component c: original) {
            if (c.getContents().contains(value)) return true;
        }
        return false;
    }

    private void compile() {
        if (CreativeCore.loader().getOverallSide().isServer())
            return;
        
        List<Component> copy = new ArrayList<>();
        for (Component component : original)
            copy.add(component.copy());
        lines = new ArrayList<>();
        compileNext(null, true, copy);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private CompiledLine compileNext(CompiledLine currentLine, boolean newLine, List<? extends FormattedText> components) {
        for (FormattedText component : components) {
            if (newLine)
                lines.add(currentLine = new CompiledLine());
            currentLine = compileNext(currentLine, component);
        }
        return currentLine;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private CompiledLine compileNext(CompiledLine currentLine, boolean newLine, FormattedText component) {
        if (newLine)
            lines.add(currentLine = new CompiledLine());
        return compileNext(currentLine, component);
    }

    public CompiledText setAlign(Align alignment) {
        this.alignment = alignment;
        return this;
    }

    private CompiledLine compileNext(CompiledLine currentLine, FormattedText component) {
        List<Component> siblings = null;
        if (component instanceof Component && !((Component) component).getSiblings().isEmpty()) {
            siblings = new ArrayList<>(((Component) component).getSiblings());
            ((Component) component).getSiblings().clear();
        }
        FormattedText next = currentLine.add(component);
        
        if (next != null) {
            lines.add(currentLine = new CompiledLine());
            currentLine = compileNext(currentLine, false, next);
        }
        
        if (siblings != null)
            currentLine = compileNext(currentLine, false, siblings);
        return currentLine;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public int getTotalHeight() {
        int height = -lineSpacing;
        for (CompiledLine line : lines)
            height += line.height + lineSpacing;
        return height;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack stack) {
        if (lines == null)
            return;
        
        usedWidth = 0;
        usedHeight = -lineSpacing;

        stack.pushPose();
        float y = Math.max(0, switch (this.vAlign) {
            case CENTER -> maxHeight / 2 - getTotalWidth() / 2;
            case BOTTOM -> maxHeight - getTotalHeight();
            default -> 0;
        });
        stack.translate(0, y, 0);
        usedHeight += (int) y;

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
        
        private final List<FormattedText> components = new ArrayList<>();
        private int height = 0;
        private int width = 0;
        
        public CompiledLine() {}

        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void render(PoseStack stack) {
            Font font = Minecraft.getInstance().font;
            int xOffset = 0;
            MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            for (FormattedText text : components) {
                int height;
                int width;
                if (text instanceof AdvancedComponent) {
                    width = ((AdvancedComponent) text).getWidth(font);
                    height = ((AdvancedComponent) text).getHeight(font);
                } else {
                    width = font.width(text);
                    height = font.lineHeight;
                }
                
                int yOffset = 0;
                if (height < this.height)
                    yOffset = (this.height - height) / 2;
                stack.pushPose();
                stack.translate(xOffset, yOffset, 0);
                if (text instanceof AdvancedComponent)
                    ((AdvancedComponent) text).render(stack, font, defaultColor);
                else {
                    font.drawInBatch(Language.getInstance().getVisualOrder(text), 0, 0, defaultColor, shadow, stack.last().pose(), renderType, false, 0, 15728880);
                    renderType.endBatch();
                }
                stack.popPose();
                xOffset += width;
            }
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void updateDimension(int width, int height) {
            this.width = Math.max(width, this.width);
            this.height = Math.max(height, this.height);
        }
        
        public FormattedText add(FormattedText component) {
            Font font = Minecraft.getInstance().font;
            int remainingWidth = maxWidth - width;
            if (component instanceof AdvancedComponent advanced) {
                if (advanced.isEmpty())
                    return null;

                int textWidth = advanced.getWidth(font);
                if (remainingWidth > textWidth) {
                    components.add(advanced);
                    updateDimension(width + textWidth, advanced.getHeight(font));
                    return null;
                } else if (advanced.canSplit()) {
                    List<AdvancedComponent> remaining = advanced.split(remainingWidth, width == 0);
                    AdvancedComponent toAdd = remaining.remove(0);
                    components.add(toAdd);
                    updateDimension(width + toAdd.getWidth(font), toAdd.getHeight(font));
                    if (remaining.isEmpty())
                        return null;
                    else
                        return remaining.get(0);
                } else if (width == 0) {
                    components.add(advanced);
                    updateDimension(width + textWidth, advanced.getHeight(font));
                    return null;
                } else
                    return advanced;
            }

            int textWidth = font.width(component);
            if (remainingWidth >= textWidth) {
                components.add(component);
                updateDimension(width + textWidth, font.lineHeight);
                return null;
            } else {
                FormattedTextSplit split = splitByWidth(component, remainingWidth, Style.EMPTY, width == 0);
                if (split != null && (split.head != null || width == 0)) {
                    if (split.head != null) {
                        updateDimension(width + font.width(split.head), font.lineHeight);
                        components.add(split.head);
                        return split.tail;
                    }
                    updateDimension(width + font.width(split.tail), font.lineHeight);
                    components.add(split.tail);
                    return null;
                } else
                    return component;
            }
        }
        
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public FormattedTextSplit splitByWidth(FormattedText text, int width, Style style, boolean force) {
        Font font = Minecraft.getInstance().font;
        final WidthLimitedCharSink charSink = new WidthLimitedCharSink(width, font.getSplitter());
        ComponentCollector head = new ComponentCollector();
        ComponentCollector tail = new ComponentCollector();
        text.visit(new FormattedText.StyledContentConsumer<FormattedText>() {
            @Override
            public Optional<FormattedText> accept(Style style, String text) {
                charSink.resetPosition();
                if (!StringDecomposer.iterateFormatted(text, style, charSink)) {
                    Linebreaker breaker = charSink.lastBreaker();
                    if (force || breaker != null) {
                        String sHead;
                        String sTail;
                        if (breaker != null) {
                            int pos = charSink.lastBreakerPos();
                            sHead = text.substring(0, pos + (breaker.includeChar && breaker.head ? 1 : 0));
                            sTail = text.substring(pos + (breaker.includeChar && !breaker.head ? 0 : 1));
                        } else {
                            sHead = text.substring(0, charSink.getPosition());
                            sTail = text.substring(charSink.getPosition());
                        }
                        if (!sHead.isEmpty())
                            head.append(FormattedText.of(sHead, style));
                        if (!sTail.isEmpty())
                            tail.append(FormattedText.of(sTail, style));
                    } else
                        tail.append(FormattedText.of(text, style));
                } else if (!text.isEmpty())
                    head.append(FormattedText.of(text, style));
                return Optional.empty();
            }
        }, style);

        FormattedText headText = head.getResult();
        FormattedText tailText = tail.getResult();

        if (headText == null && tailText == null)
            return null;

        return new FormattedTextSplit(headText, tailText);
    }

    public record FormattedTextSplit(FormattedText head, FormattedText tail) {};
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public int getTotalWidth() {
        return calculateWidth(0, true, original);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
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
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private int calculateWidth(FormattedText text) {
        Font font = Minecraft.getInstance().font;
        int width = 0;
        if (text instanceof AdvancedComponent advanced) {
            if (!advanced.isEmpty())
                width += advanced.getWidth(font);
        } else
            width += font.width(text);

        if (text instanceof Component component && !component.getSiblings().isEmpty())
            width += calculateWidth(0, false, component.getSiblings());
        return width;
    }

    public CompiledText copy() {
        CompiledText copy = new CompiledText(maxWidth, maxHeight);
        copy.alignment = alignment;
        copy.lineSpacing = lineSpacing;
        copy.shadow = shadow;
        List<Component> components = new ArrayList<>();
        for (Component component: original) {
            components.add(component.copy());
        }
        copy.setText(components);
        return copy;
    }
    
    public static CompiledText createAnySize() {
        return new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

}
