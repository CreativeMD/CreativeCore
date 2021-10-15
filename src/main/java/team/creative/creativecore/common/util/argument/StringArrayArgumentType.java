package team.creative.creativecore.common.util.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StringArrayArgumentType implements ArgumentType<String[]> {
    
    public static final List<String> EXAMPLES = Arrays.asList(new String[] { "name1", "name1,name2", "name1,name2,name3" });
    
    public static String[] getStringArray(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String[].class);
    }
    
    public static StringArrayArgumentType stringArray() {
        return new StringArrayArgumentType();
    }
    
    @Override
    public String[] parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString().split(",");
    }
    
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
    
}
