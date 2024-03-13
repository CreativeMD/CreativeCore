package team.creative.creativecore.common.util.text;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class MutableComponentCreative implements Component {
    private final String contents;
    private final List<Component> siblings;
    private Style style;
    private FormattedCharSequence visualOrderText = FormattedCharSequence.EMPTY;
    @Nullable
    private Language decomposedWith;
    
    public MutableComponentCreative(String p_237200_, List<Component> p_237201_, Style p_237202_) {
        this.contents = p_237200_;
        this.siblings = p_237201_;
        this.style = p_237202_;
    }
    
    public static MutableComponentCreative create(String p_237205_) {
        return new MutableComponentCreative(p_237205_, Lists.newArrayList(), Style.EMPTY);
    }
    
    @Override
    public String getContents() {
        return this.contents;
    }
    
    @Override
    public List<Component> getSiblings() {
        return this.siblings;
    }

    @Override
    public MutableComponent plainCopy() {
        return null;
    }

    @Override
    public MutableComponent copy() {
        return null;
    }

    public MutableComponentCreative setStyle(Style p_130943_) {
        this.style = p_130943_;
        return this;
    }
    
    @Override
    public Style getStyle() {
        return this.style;
    }
    
    public MutableComponentCreative append(String p_130947_) {
        return this.append(new TextComponent(p_130947_));
    }
    
    public MutableComponentCreative append(Component p_130942_) {
        this.siblings.add(p_130942_);
        return this;
    }
    
    public MutableComponentCreative withStyle(UnaryOperator<Style> p_130939_) {
        this.setStyle(p_130939_.apply(this.getStyle()));
        return this;
    }
    
    public MutableComponentCreative withStyle(Style p_130949_) {
        this.setStyle(p_130949_.applyTo(this.getStyle()));
        return this;
    }
    
    public MutableComponentCreative withStyle(ChatFormatting... p_130945_) {
        this.setStyle(this.getStyle().applyFormats(p_130945_));
        return this;
    }
    
    public MutableComponentCreative withStyle(ChatFormatting p_130941_) {
        this.setStyle(this.getStyle().applyFormat(p_130941_));
        return this;
    }
    
    @Override
    public FormattedCharSequence getVisualOrderText() {
        Language language = Language.getInstance();
        if (this.decomposedWith != language) {
            this.visualOrderText = language.getVisualOrder(this);
            this.decomposedWith = language;
        }
        
        return this.visualOrderText;
    }
    
    @Override
    public boolean equals(Object p_237209_) {
        if (this == p_237209_) {
            return true;
        } else if (!(p_237209_ instanceof MutableComponentCreative)) {
            return false;
        } else {
            MutableComponentCreative mutablecomponent = (MutableComponentCreative) p_237209_;
            return this.contents.equals(mutablecomponent.contents) && this.style.equals(mutablecomponent.style) && this.siblings.equals(mutablecomponent.siblings);
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.contents, this.style, this.siblings);
    }
    
    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(this.contents.toString());
        boolean flag = !this.style.isEmpty();
        boolean flag1 = !this.siblings.isEmpty();
        if (flag || flag1) {
            stringbuilder.append('[');
            if (flag) {
                stringbuilder.append("style=");
                stringbuilder.append(this.style);
            }
            
            if (flag && flag1) {
                stringbuilder.append(", ");
            }
            
            if (flag1) {
                stringbuilder.append("siblings=");
                stringbuilder.append(this.siblings);
            }
            
            stringbuilder.append(']');
        }
        
        return stringbuilder.toString();
    }
}