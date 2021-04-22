package team.creative.creativecore.client.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import team.creative.creativecore.CreativeCore;

public class ClientCommandRegistry {
    
    private static Minecraft mc = Minecraft.getInstance();
    
    private static CommandDispatcher<ISuggestionProvider> clientDispatcher = new CommandDispatcher<>();
    private static CombinedCommandDispatcher<ISuggestionProvider> combined = null;
    
    public static CommandDispatcher<ISuggestionProvider> getDispatcher(CommandDispatcher<ISuggestionProvider> vanillaDispatcher) {
        if (combined == null)
            combined = new CombinedCommandDispatcher<>(vanillaDispatcher, clientDispatcher);
        combined.set(vanillaDispatcher, clientDispatcher);
        return combined;
    }
    
    public static LiteralCommandNode<ISuggestionProvider> register(final LiteralArgumentBuilder<ISuggestionProvider> command) {
        return clientDispatcher.register(command);
    }
    
    public static int handleCommand(CommandSource source, String command) {
        StringReader stringreader = new StringReader(command);
        if (stringreader.canRead() && stringreader.peek() == '/')
            stringreader.skip();
        
        mc.getProfiler().push(command);
        
        try {
            try {
                ParseResults<ISuggestionProvider> parse = clientDispatcher.parse(stringreader, source);
                return clientDispatcher.execute(parse);
            } catch (CommandException commandexception) {
                source.sendFailure(commandexception.getComponent());
                return 0;
            } catch (CommandSyntaxException commandsyntaxexception) {
                if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand())
                    return -1;
                source.sendFailure(TextComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
                if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                    int k = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                    IFormattableTextComponent itextcomponent1 = (new StringTextComponent("")).withStyle(TextFormatting.GRAY).withStyle((p_211705_1_) -> {
                        return p_211705_1_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                    });
                    if (k > 10) {
                        itextcomponent1.append(new StringTextComponent("..."));
                    }
                    
                    itextcomponent1.append(new StringTextComponent(commandsyntaxexception.getInput().substring(Math.max(0, k - 10), k)));
                    if (k < commandsyntaxexception.getInput().length()) {
                        ITextComponent itextcomponent2 = (new StringTextComponent(commandsyntaxexception.getInput().substring(k)))
                                .withStyle(new TextFormatting[] { TextFormatting.RED, TextFormatting.UNDERLINE });
                        itextcomponent1.append(itextcomponent2);
                    }
                    
                    itextcomponent1.append((new TranslationTextComponent("command.context.here")).withStyle(new TextFormatting[] { TextFormatting.RED, TextFormatting.ITALIC }));
                    source.sendFailure(itextcomponent1);
                }
            } catch (Exception exception) {
                StringTextComponent stringtextcomponent = new StringTextComponent(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
                IFormattableTextComponent itextcomponent = stringtextcomponent;
                if (CreativeCore.LOGGER.isDebugEnabled()) {
                    StackTraceElement[] astacktraceelement = exception.getStackTrace();
                    
                    for (int j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                        itextcomponent.append("\n\n").append(astacktraceelement[j].getMethodName()).append("\n ").append(astacktraceelement[j].getFileName()).append(":")
                                .append(String.valueOf(astacktraceelement[j].getLineNumber()));
                    }
                }
                
                source.sendFailure((new TranslationTextComponent("command.failed")).withStyle((p_211704_1_) -> {
                    return p_211704_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent));
                }));
                return 0;
            }
            
            return 0;
        } finally {
            mc.getProfiler().pop();
        }
    }
    
    private static final Field consumerField = ObfuscationReflectionHelper.findField(CommandDispatcher.class, "consumer");
    
    public static class CombinedCommandDispatcher<S> extends CommandDispatcher<S> {
        
        public CommandDispatcher<S> vanilla;
        public CommandDispatcher<S> extra;
        
        public CombinedCommandDispatcher(CommandDispatcher<S> vanilla, CommandDispatcher<S> extra) {
            super();
            set(vanilla, extra);
        }
        
        public void set(CommandDispatcher<S> vanilla, CommandDispatcher<S> extra) {
            this.vanilla = vanilla;
            this.extra = extra;
        }
        
        public ResultConsumer<S> getConsumer() {
            try {
                return (ResultConsumer<S>) consumerField.get(vanilla);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int execute(ParseResults<S> parse) throws CommandSyntaxException {
            if (parse instanceof ParseResultsCombined)
                if (((ParseResultsCombined) parse).isPrimary())
                    return vanilla.execute(parse);
                else
                    return extra.execute(((ParseResultsCombined) parse).secondary);
            return vanilla.execute(parse);
        }
        
        @Override
        public ParseResults<S> parse(final StringReader command, final S source) {
            int cursor = command.getCursor();
            ParseResults<S> results = vanilla.parse(command, source);
            command.setCursor(cursor);
            ParseResults<S> otherResults = extra.parse(command, source);
            return new ParseResultsCombined<>(results, otherResults);
        }
        
        @Override
        public String[] getAllUsage(final CommandNode<S> node, final S source, final boolean restricted) {
            if (vanilla.getPath(node).isEmpty())
                return extra.getAllUsage(node, source, restricted);
            return vanilla.getAllUsage(node, source, restricted);
        }
        
        @Override
        public Map<CommandNode<S>, String> getSmartUsage(final CommandNode<S> node, final S source) {
            if (vanilla.getPath(node).isEmpty())
                return extra.getSmartUsage(node, source);
            return vanilla.getSmartUsage(node, source);
        }
        
        @Override
        public CompletableFuture<Suggestions> getCompletionSuggestions(final ParseResults<S> parse, int cursor) {
            if (parse instanceof ParseResultsCombined) {
                CompletableFuture<Suggestions> resultVanilla = vanilla.getCompletionSuggestions(parse, cursor);
                CompletableFuture<Suggestions> resultExtra = extra.getCompletionSuggestions(((ParseResultsCombined) parse).secondary, cursor);
                
                final CompletableFuture<Suggestions> result = new CompletableFuture<>();
                final List<Suggestions> suggestions = new ArrayList<>();
                suggestions.add(resultVanilla.join());
                suggestions.add(resultExtra.join());
                result.complete(Suggestions.merge(parse.getReader().getString(), suggestions));
                return result;
            } else
                return vanilla.getCompletionSuggestions(parse, cursor);
        }
        
        @Override
        public Collection<String> getPath(final CommandNode<S> target) {
            Collection<String> path = vanilla.getPath(target);
            if (path.isEmpty())
                return extra.getPath(target);
            return path;
        }
        
        @Override
        public CommandNode<S> findNode(final Collection<String> path) {
            CommandNode<S> node = vanilla.findNode(path);
            if (node == null)
                return extra.findNode(path);
            return node;
        }
        
        @Override
        public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
            vanilla.findAmbiguities(consumer);
            extra.findAmbiguities(consumer);
        }
        
        public boolean is(CommandDispatcher<S> vanilla, CommandDispatcher<S> extra) {
            return this.vanilla == vanilla && this.extra == extra;
        }
        
    }
    
    public static class ParseResultsCombined<S> extends ParseResults<S> {
        
        public ParseResults<S> secondary;
        
        public ParseResultsCombined(ParseResults<S> primary, ParseResults<S> secondary) {
            super(primary.getContext(), primary.getReader(), primary.getExceptions());
            this.secondary = secondary;
        }
        
        public boolean isPrimary() {
            if (getContext().getCommand() != null)
                return true;
            else if (secondary.getContext().getCommand() != null)
                return false;
            if (getContext().getNodes().size() >= secondary.getContext().getNodes().size())
                return true;
            return false;
        }
        
    }
}
