package team.creative.creativecore.common.util.mc;

import net.minecraft.client.resources.language.I18n;

public class LanguageUtils {
    
    public static boolean can(String name) {
        return I18n.exists(name);
    }
    
    public static String translate(String name) {
        return I18n.get(name);
    }
    
    public static String translateOr(String name, String defaultString) {
        String result = I18n.get(name);
        if (name.equals(result))
            return defaultString;
        return name;
    }
    
    public static String translate(String name, Object... args) {
        return I18n.get(name, args);
    }
    
}
