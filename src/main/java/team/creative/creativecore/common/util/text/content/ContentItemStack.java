package team.creative.creativecore.common.util.text.content;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.StringSplitter.WidthProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.GuiRenderHelper;

public record ContentItemStack(ItemStack stack) implements AdvancedContent {
    
    public static final MapCodec<ContentItemStack> CODEC = RecordCodecBuilder.mapCodec(content -> content.group(ItemStack.CODEC.fieldOf("stack").forGetter(ContentItemStack::stack))
            .apply(content, ContentItemStack::new));
    
    public static final ComponentContents.Type<ContentItemStack> TYPE = new Type<>(CODEC, "stack");
    
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
            PoseStack pose = graphics.pose();
            pose.pushPose();
            
            pose.translate(0, -2, 10);
            pose.scale(0.8F, 0.8F, 0.8F);
            GuiRenderHelper.drawItemStack(graphics, content.stack, 1);
            pose.popPose();
        }
        
    }
    
    @Override
    public Type<?> type() {
        return TYPE;
    }
    
}
