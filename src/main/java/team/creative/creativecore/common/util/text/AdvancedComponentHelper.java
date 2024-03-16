package team.creative.creativecore.common.util.text;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;
import net.minecraft.util.Unit;
import team.creative.creativecore.common.util.text.content.AdvancedContent;
import team.creative.creativecore.common.util.text.content.AdvancedContentConsumer;
import team.creative.creativecore.common.util.text.content.FormattedSingleSink;

public class AdvancedComponentHelper {
    
    public static final AdvancedStringSplitter SPLITTER = new AdvancedStringSplitter(Minecraft.getInstance().font);
    private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);
    
    public static Component copy(Component component) {
        MutableComponent copy = MutableComponent.create(component.getContents());
        
        for (Component sibling : component.getSiblings())
            copy.getSiblings().add(copy(sibling));
        
        copy.setStyle(component.getStyle());
        return copy;
    }
    
    public static boolean iterateFormatted(Component text, Style style, FormattedSingleSink sink) {
        return visit(text, new AdvancedContentConsumer() {
            
            @Override
            public Optional accept(Style style, AdvancedContent content) {
                return sink.accept(style, content) ? Optional.empty() : STOP_ITERATION;
            }
            
            @Override
            public Optional accept(Style style, String content) {
                return StringDecomposer.iterateFormatted(content, 0, style, style, sink) ? Optional.empty() : STOP_ITERATION;
            }
            
        }, style).isEmpty();
    }
    
    public static <T> Optional<T> visit(Component text, AdvancedContentConsumer<T> consumer, Style defaultStyle) {
        Style style = text.getStyle().applyTo(defaultStyle);
        Optional<T> optional = visit(text.getContents(), consumer, style);
        if (optional.isPresent())
            return optional;
        else {
            for (Component component : text.getSiblings()) {
                Optional<T> optional1 = visit(component, consumer, style);
                if (optional1.isPresent())
                    return optional1;
            }
            return Optional.empty();
        }
    }
    
    public static <T> Optional<T> visit(ComponentContents content, AdvancedContentConsumer<T> consumer, Style style) {
        if (content instanceof AdvancedContent adv)
            return adv.visit(consumer, style);
        return content.visit(consumer, style);
    }
    
}
