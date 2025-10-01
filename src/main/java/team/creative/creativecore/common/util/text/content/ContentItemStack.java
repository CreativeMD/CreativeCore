package team.creative.creativecore.common.util.text.content;

import java.util.Optional;

import org.joml.Matrix3x2fStack;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.StringSplitter.WidthProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record ContentItemStack(ItemStack stack) implements AdvancedContent {
    
    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> consumer, Style style) {
        return stack.getHoverName().visit(consumer, style);
    }
    
    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> consumer) {
        return stack.getHoverName().visit(consumer);
    }
    
    @Override
    public int width(WidthProvider widthProvider, Style style) {
        return 14;
    }
    
    @Override
    public int height() {
        return 8;
    }
    
    @Override
    public FormattedText asText() {
        return new ContentItemStackText(this);
    }
    
    private static record ContentItemStackText(ContentItemStack content) implements AdvancedFormattedText {
        
        @Override
        public int width(WidthProvider widthProvider, Style style) {
            return content.width(widthProvider, style);
        }
        
        @Override
        public int height() {
            return content.height();
        }
        
        @Override
        public void render(GuiGraphics graphics, int defaultColor) {
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            
            pose.translate(0, -2);
            pose.scale(0.8F, 0.8F);
            graphics.renderItem(content.stack, 0, 0);
            pose.popMatrix();
        }
        
    }
    
    @Override
    public MapCodec<? extends ComponentContents> codec() {
        return null;
    }
    
}
