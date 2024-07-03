package team.creative.creativecore.common.util.text.content;

import java.util.Optional;

import net.minecraft.client.StringSplitter.WidthProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface AdvancedFormattedText extends FormattedText {
    
    @Override
    default <T> Optional<T> visit(ContentConsumer<T> consumer) {
        return Optional.empty();
    }
    
    @Override
    default <T> Optional<T> visit(StyledContentConsumer<T> consumer, Style style) {
        return Optional.empty();
    }
    
    public int width(WidthProvider widthProvider, Style style);
    
    public int height();
    
    public void render(GuiGraphics graphics, int defaultColor);
    
}
