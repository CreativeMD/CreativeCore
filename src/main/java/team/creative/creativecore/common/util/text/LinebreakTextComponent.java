package team.creative.creativecore.common.util.text;

import net.minecraft.util.text.TextComponent;

public class LinebreakTextComponent extends TextComponent {
	
	@Override
	public TextComponent copyRaw() {
		return new LinebreakTextComponent();
	}
	
}
