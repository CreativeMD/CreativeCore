package team.creative.creativecore.client.render.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.text.AdvancedComponentHelper;
import team.creative.creativecore.common.util.text.content.AdvancedContent;
import team.creative.creativecore.common.util.text.content.AdvancedContentConsumer;
import team.creative.creativecore.common.util.text.content.AdvancedFormattedText;
import team.creative.creativecore.common.util.type.list.SingletonList;

public class CompiledText {
    
    private static int width(FormattedText text) {
        if (text instanceof AdvancedFormattedText adv)
            return adv.width(AdvancedComponentHelper.SPLITTER.width, Style.EMPTY);
        return Mth.ceil(AdvancedComponentHelper.SPLITTER.stringWidth(text));
    }
    
    private static int lineHeight(FormattedText text) {
        if (text instanceof AdvancedContent adv)
            return adv.height();
        if (text instanceof Component comp && comp.getContents() instanceof AdvancedContent adv)
            return adv.height();
        return AdvancedComponentHelper.SPLITTER.lineHeight;
    }
    
    public static final CompiledText EMPTY = new CompiledText(0, 0) {
        
        {
            original = Collections.EMPTY_LIST;
            lines = Collections.EMPTY_LIST;
        }
        
        @Override
        public void setText(Component component) {}
        
        @Override
        public void setText(List<Component> components) {}
        
        @Override
        protected void compile() {}
        
        @Override
        public int getTotalHeight() {
            return 0;
        }
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void render(PoseStack stack) {}
    };
    
    private int maxWidth;
    private int maxHeight;
    private int usedWidth;
    private int usedHeight;
    private int lineSpacing = 2;
    private boolean shadow = true;
    private int defaultColor = ColorUtils.WHITE;
    private Align align = Align.LEFT;
    private VAlign valign = VAlign.TOP;
    protected List<CompiledLine> lines;
    protected List<Component> original;
    
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
    
    public void setDefaultColor(int color) {
        this.defaultColor = color;
    }
    
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
    
    public void setAlign(Align align) {
        this.align = align;
    }
    
    public void setVAlign(VAlign valign) {
        this.valign = valign;
    }
    
    public void setText(Component component) {
        setText(new SingletonList<>(component));
    }
    
    public void setText(List<Component> components) {
        this.original = components;
        compile();
    }
    
    protected void compile() {
        if (CreativeCore.loader().getOverallSide().isServer())
            return;
        
        List<Component> copy = new ArrayList<>();
        for (Component component : original)
            copy.add(AdvancedComponentHelper.copy(component));
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
        
        int totalHeight = getTotalHeight();
        
        stack.pushPose();
        float y = Math.max(0, switch (valign) {
            case CENTER -> maxHeight / 2 - totalHeight / 2;
            case BOTTOM -> maxHeight - totalHeight;
            default -> 0;
        });
        stack.translate(0, y, 0);
        usedHeight += (int) y;
        
        for (CompiledLine line : lines) {
            switch (align) {
                case CENTER -> {
                    int x = maxWidth / 2 - line.width / 2;
                    stack.translate(x, 0, 0);
                    line.render(stack);
                    stack.translate(-x, 0, 0);
                    usedWidth = Math.max(usedWidth, maxWidth);
                }
                case RIGHT -> {
                    int x = maxWidth - line.width;
                    stack.translate(x, 0, 0);
                    line.render(stack);
                    stack.translate(-x, 0, 0);
                    usedWidth = Math.max(usedWidth, maxWidth);
                }
                default -> {
                    line.render(stack);
                    usedWidth = Math.max(usedWidth, line.width);
                }
            };
            
            int height = line.height + lineSpacing;
            stack.translate(0, height, 0);
            usedHeight += height;
            
            if (usedHeight > maxHeight)
                break;
        }
        
        stack.popPose();
        RenderSystem.enableBlend();
    }
    
    public class CompiledLine {
        
        private final List<FormattedText> components = new ArrayList<>();
        private int height = 0;
        private int width = 0;
        
        public CompiledLine() {}
        
        public boolean contains(String search) {
            for (FormattedText text : components)
                if (text.getString().contains(search))
                    return true;
            return false;
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void render(PoseStack pose) {
            Font font = Minecraft.getInstance().font;
            int xOffset = 0;
            MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            for (FormattedText text : components) {
                int height = lineHeight(text);
                int width = width(text);
                
                int yOffset = 0;
                if (height < this.height)
                    yOffset = (this.height - height) / 2;
                pose.pushPose();
                pose.translate(xOffset, yOffset, 0);
                if (text instanceof AdvancedFormattedText adv)
                    adv.render(pose, defaultColor);
                else {
                    font.drawInBatch(Language.getInstance().getVisualOrder(text), 0, 0, defaultColor, shadow, pose.last().pose(), renderType, DisplayMode.NORMAL, 0, 15728880);
                    renderType.endBatch();
                }
                pose.popPose();
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
            int remainingWidth = maxWidth - width;
            int textWidth = width(component);
            if (remainingWidth >= textWidth) {
                if (component instanceof Component comp && comp.getContents() instanceof AdvancedContent adv)
                    components.add(adv.asText());
                else
                    components.add(component);
                updateDimension(width + textWidth, lineHeight(component));
                return null;
            } else {
                FormattedTextSplit split = splitByWidth(component, remainingWidth, Style.EMPTY, width == 0);
                if (split != null && (split.head != null || width == 0)) {
                    if (split.head != null) {
                        updateDimension(width + width(split.head), lineHeight(split.head));
                        components.add(split.head);
                        return split.tail;
                    }
                    updateDimension(width + width(split.tail), lineHeight(split.tail));
                    components.add(split.tail);
                    return null;
                } else if (width == 0)
                    return null;
                else
                    return component;
            }
        }
        
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public FormattedTextSplit splitByWidth(FormattedText text, int width, Style style, boolean force) {
        final WidthLimitedCharSink charSink = new WidthLimitedCharSink(width, Minecraft.getInstance().font.getSplitter());
        ComponentCollector head = new ComponentCollector();
        ComponentCollector tail = new ComponentCollector();
        
        if (text instanceof Component comp) {
            AdvancedComponentHelper.visit(comp, new AdvancedContentConsumer() {
                
                @Override
                public Optional accept(Style style, AdvancedContent content) {
                    if (charSink.accept(style, content) || force)
                        head.append(content.asText());
                    else
                        tail.append(content.asText());
                    return Optional.empty();
                }
                
                @Override
                public Optional accept(Style style, String text) {
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
        } else {
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
        }
        
        FormattedText headText = head.getResult();
        FormattedText tailText = tail.getResult();
        
        if (headText == null && tailText == null)
            return null;
        return new FormattedTextSplit(headText, tailText);
    }

    public record FormattedTextSplit(FormattedText head, FormattedText tail) {

    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public int getTotalWidth() {
        return calculateWidth(0, true, original);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private int calculateWidth(int width, boolean newLine, List<? extends FormattedText> components) {
        for (FormattedText component : components) {
            int result = width(component);
            if (newLine)
                width = Math.max(width, result);
            else
                width += result;
        }
        return width;
    }
    
    public CompiledText copy() {
        CompiledText copy = new CompiledText(maxWidth, maxHeight);
        copy.align = align;
        copy.valign = valign;
        copy.defaultColor = defaultColor;
        copy.lineSpacing = lineSpacing;
        copy.shadow = shadow;
        List<Component> components = new ArrayList<>();
        for (Component component : original)
            components.add(component.copy());
        copy.setText(components);
        return copy;
    }
    
    public static CompiledText createAnySize() {
        return new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public boolean contains(String search) {
        for (CompiledLine line : lines)
            if (line.contains(search))
                return true;
        return false;
    }
    
    public int getUsedWidth() {
        return usedWidth;
    }
    
    public int getUsedHeight() {
        return usedHeight;
    }
    
}
