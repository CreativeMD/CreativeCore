package team.creative.creativecore.common.util.text;

import org.apache.commons.lang3.mutable.MutableFloat;

import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import team.creative.creativecore.common.util.text.content.AdvancedContent;
import team.creative.creativecore.common.util.text.content.FormattedSingleSink;
import team.creative.creativecore.mixin.StringSplitterAccessor;

public class AdvancedStringSplitter extends StringSplitter {
    
    public final WidthProvider width;
    public final int lineHeight;
    
    public AdvancedStringSplitter(Font font) {
        super(((StringSplitterAccessor) font.getSplitter()).getWidthProvider());
        this.width = ((StringSplitterAccessor) font.getSplitter()).getWidthProvider();
        this.lineHeight = font.lineHeight;
    }
    
    @Override
    public float stringWidth(FormattedText text) {
        if (text instanceof Component component) {
            MutableFloat mutablefloat = new MutableFloat();
            AdvancedComponentHelper.iterateFormatted(component, Style.EMPTY, new FormattedSingleSink() {
                
                @Override
                public boolean accept(Style style, AdvancedContent content) {
                    mutablefloat.add(content.width(width, style));
                    return true;
                }
                
                @Override
                public boolean accept(int index, Style style, int character) {
                    mutablefloat.add(width.getWidth(character, style));
                    return true;
                }
            });
            return mutablefloat.floatValue();
        }
        return super.stringWidth(text);
        
    }
    
}
