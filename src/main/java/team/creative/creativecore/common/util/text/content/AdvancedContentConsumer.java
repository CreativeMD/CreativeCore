package team.creative.creativecore.common.util.text.content;

import java.util.Optional;

import net.minecraft.network.chat.FormattedText.StyledContentConsumer;
import net.minecraft.network.chat.Style;

public interface AdvancedContentConsumer<T> extends StyledContentConsumer<T> {
    
    public Optional<T> accept(Style style, AdvancedContent content);
    
    @Override
    public Optional<T> accept(Style style, String content);
    
}
