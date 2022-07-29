package team.creative.creativecore.common.util.text.content;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

public interface FormattedSingleSink extends FormattedCharSink {
    
    @Override
    boolean accept(int index, Style style, int character);
    
    boolean accept(Style style, AdvancedContent content);
    
}
