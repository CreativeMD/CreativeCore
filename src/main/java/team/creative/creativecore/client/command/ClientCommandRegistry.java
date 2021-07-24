package team.creative.creativecore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import team.creative.creativecore.CreativeCore;

public class ClientCommandRegistry {
    
    private static Minecraft mc = Minecraft.getInstance();
    
    private static CommandDispatcher<SharedSuggestionProvider> clientDispatcher = new CommandDispatcher<>();
    private static CombinedCommandDispatcher<SharedSuggestionProvider> combined = null;
    
    public static CommandDispatcher<SharedSuggestionProvider> getDispatcher(CommandDispatcher<SharedSuggestionProvider> vanillaDispatcher) {
        if (combined == null || !combined.is(vanillaDispatcher, clientDispatcher))
            combined = new CombinedCommandDispatcher<>(vanillaDispatcher, clientDispatcher);
        return combined;
    }
    
    public static LiteralCommandNode<SharedSuggestionProvider> register(final LiteralArgumentBuilder<SharedSuggestionProvider> command) {
        return clientDispatcher.register(command);
    }
    
    public static int handleCommand(CommandSourceStack source, String command) {
        StringReader stringreader = new StringReader(command);
        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }
        
        mc.getProfiler().push(command);
        
        try {
            try {
                ParseResults<SharedSuggestionProvider> parse = clientDispatcher.parse(stringreader, source);
                return clientDispatcher.execute(parse);
                /*} catch (CommandException commandexception) {
                source.sendFailure(commandexception.getComponent());
                return 0;*/
            } catch (CommandSyntaxException commandsyntaxexception) {
                if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand())
                    return -1;
                source.sendFailure(ComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
                if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                    int k = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                    MutableComponent itextcomponent1 = (new TextComponent("")).withStyle(ChatFormatting.GRAY).withStyle((p_211705_1_) -> {
                        return p_211705_1_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                    });
                    if (k > 10) {
                        itextcomponent1.append(new TextComponent("..."));
                    }
                    
                    itextcomponent1.append(new TextComponent(commandsyntaxexception.getInput().substring(Math.max(0, k - 10), k)));
                    if (k < commandsyntaxexception.getInput().length()) {
                        Component itextcomponent2 = (new TextComponent(commandsyntaxexception.getInput().substring(k)))
                                .withStyle(new ChatFormatting[] { ChatFormatting.RED, ChatFormatting.UNDERLINE });
                        itextcomponent1.append(itextcomponent2);
                    }
                    
                    itextcomponent1.append((new TranslatableComponent("command.context.here")).withStyle(new ChatFormatting[] { ChatFormatting.RED, ChatFormatting.ITALIC }));
                    source.sendFailure(itextcomponent1);
                }
            } catch (Exception exception) {
                TextComponent TextComponent = new TextComponent(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
                MutableComponent itextcomponent = TextComponent;
                if (CreativeCore.LOGGER.isDebugEnabled()) {
                    StackTraceElement[] astacktraceelement = exception.getStackTrace();
                    
                    for (int j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                        itextcomponent.append("\n\n").append(astacktraceelement[j].getMethodName()).append("\n ").append(astacktraceelement[j].getFileName()).append(":")
                                .append(String.valueOf(astacktraceelement[j].getLineNumber()));
                    }
                }
                
                source.sendFailure((new TranslatableComponent("command.failed")).withStyle((p_211704_1_) -> {
                    return p_211704_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent));
                }));
                return 0;
            }
            
            return 0;
        } finally {
            mc.getProfiler().pop();
        }
    }
    
    public static class CombinedCommandDispatcher<T> extends CommandDispatcher<T> {
        
        public final CommandDispatcher<T> first;
        public final CommandDispatcher<T> second;
        
        public CombinedCommandDispatcher(CommandDispatcher<T> first, CommandDispatcher<T> second) {
            super();
            this.first = first;
            this.second = second;
            RootCommandNode<T> root = getRoot();
            for (CommandNode<T> node : first.getRoot().getChildren())
                root.addChild(node);
            for (CommandNode<T> node : second.getRoot().getChildren())
                root.addChild(node);
            
        }
        
        public boolean is(CommandDispatcher<T> first, CommandDispatcher<T> second) {
            return this.first == first && this.second == second;
        }
        
    }
}
