package team.creative.creativecore.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Either;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.client.gui.integration.ContainerScreenIntegration;
import team.creative.creativecore.client.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.style.GuiStyle;

public class CreativeCoreClient implements ClientModInitializer {
    
    private static final Minecraft mc = Minecraft.getInstance();
    
    private static final List<String> MOD_CONFIGS = new ArrayList<>();
    
    public static Iterable<String> getModConfigs() {
        return MOD_CONFIGS;
    }
    
    public static void registerClientConfig(String modid) {
        MOD_CONFIGS.add(modid);
    }
    
    public static float getFrameTime() {
        if (mc.isPaused())
            return 1.0F;
        return mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }
    
    public static void commands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommands.literal("cmdclientconfig").executes(x -> {
            GuiEventHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(true, CreativeConfigRegistry.ROOT, Side.CLIENT)));
            return 0;
        }));
    }
    
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(GuiEventHandler::onTick);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> GuiStyle.reload());
        MenuScreens.register(CreativeCore.GUI_CONTAINER, new ScreenConstructor<ContainerIntegration, ContainerScreenIntegration>() {
            
            @Override
            public ContainerScreenIntegration create(ContainerIntegration container, Inventory inventory, Component p_create_3_) {
                return new ContainerScreenIntegration(container, inventory);
            }
        });
        ClientCommandRegistrationCallback.EVENT.register(CreativeCoreClient::commands);
        ClientTickEvents.START_CLIENT_TICK.register(CreativeCoreClient::clientTick);
    }
    
    public static void clientTick(Minecraft client) {
        if (client.screen instanceof IScaleableGuiScreen scaleableGuiScreen)
            scaleableGuiScreen.clientTick();
    }
    
    public static void postBackgroundEvent(Screen screen, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {}
    
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream().map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left).collect(
            Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));
        return gatherTooltipComponentsFromElements(stack, elements, mouseX, screenWidth, screenHeight, fallbackFont);
    }
    
    public static List<ClientTooltipComponent> gatherTooltipComponentsFromElements(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        Font font = fallbackFont;
        
        // text wrapping
        int tooltipTextWidth = elements.stream().mapToInt(either -> either.map(font::width, component -> 0)).max().orElse(0);
        
        boolean needsWrap = false;
        
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }
        
        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
            return elements.stream().flatMap(either -> either.map(text -> splitLine(text, font, tooltipTextWidthF), component -> Stream.of(ClientTooltipComponent.create(
                component)))).toList();
        }
        return elements.stream().map(either -> either.map(text -> ClientTooltipComponent.create(text instanceof Component c ? c.getVisualOrderText() : Language.getInstance()
                .getVisualOrder(text)), ClientTooltipComponent::create)).toList();
    }
    
    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        if (text instanceof Component component && component.getString().isEmpty()) {
            return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
        }
        return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
    }
    
    public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder) {
        Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
        return (typeOne, typeTwo) -> {
            boolean vanillaOne = renderOrder.contains(typeOne);
            boolean vanillaTwo = renderOrder.contains(typeTwo);
            
            if (vanillaOne && vanillaTwo) {
                return vanillaComparator.compare(typeOne, typeTwo);
            }
            if (!vanillaOne && !vanillaTwo) {
                return Integer.compare(System.identityHashCode(typeOne), System.identityHashCode(typeTwo));
            }
            return vanillaOne ? -1 : 1;
        };
    }
}
