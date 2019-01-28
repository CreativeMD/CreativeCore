package com.creativemd.creativecore.common.utils.mc;

import java.util.Locale;

public enum ChatFormatting {
	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	OBFUSCATED('k', true),
	BOLD('l', true),
	STRIKETHROUGH('m', true),
	UNDERLINE('n', true),
	ITALIC('o', true),
	RESET('r');
	
	public static final char PREFIX_CODE = '\u00A7';
	private final char code;
	private final boolean isFormat;
	private final String toString;
	
	private ChatFormatting(char code) {
		this(code, false);
	}
	
	private ChatFormatting(char code, boolean isFormat) {
		this.code = code;
		this.isFormat = isFormat;
		
		this.toString = PREFIX_CODE + "" + code;
	}
	
	public char getChar() {
		return this.code;
	}
	
	public boolean isFormat() {
		return this.isFormat;
	}
	
	public boolean isColor() {
		return !this.isFormat && this != RESET;
	}
	
	public String getName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
	
	public String toString() {
		return this.toString;
	}
}