package team.creative.creativecore.common.test;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class CreativeTestArgument implements ArgumentType<String> {
    
    public static CreativeTestArgument test() {
        return new CreativeTestArgument();
    }
    
    public static CreativeTest getTest(final CommandContext<?> context, final String name) {
        var testName = context.getArgument(name, String.class);
        return CreativeTestRegistry.REGISTRY.get(testName);
    }
    
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final String result = reader.readString();
        if (!isAllowed(result)) {
            reader.setCursor(start);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Invalid Test")), Component.translatable("invalid_test"));
        }
        
        return result;
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(getAll(), builder) : Suggestions.empty();
    }
    
    @Override
    public Collection<String> getExamples() {
        return getAll();
    }
    
    public Collection<String> getAll() {
        return CreativeTestRegistry.REGISTRY.keys();
    }
    
    public boolean isAllowed(String result) {
        return CreativeTestRegistry.REGISTRY.contains(result);
    }
    
}
