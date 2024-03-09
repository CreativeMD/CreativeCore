package team.creative.creativecore.common.util.text.content;

import net.minecraft.client.StringSplitter.WidthProvider;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.Optional;

public interface AdvancedContent extends FormattedText {
    
    public default <T> Optional<T> visit(AdvancedContentConsumer<T> consumer, Style style) {
        return consumer.accept(style, this);
    }
    
    public int width(WidthProvider widthProvider, Style style);
    
    public int height();
    
    public FormattedText asText();
}
