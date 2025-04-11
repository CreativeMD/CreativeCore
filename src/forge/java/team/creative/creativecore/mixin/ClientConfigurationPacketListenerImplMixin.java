package team.creative.creativecore.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;
import team.creative.creativecore.common.network.CreativeByteBuf;

@Mixin(ClientConfigurationPacketListenerImpl.class)
public class ClientConfigurationPacketListenerImplMixin {
    
    @Redirect(method = "handleConfigurationFinished(Lnet/minecraft/network/protocol/configuration/ClientboundFinishConfigurationPacket;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;decorator(Lnet/minecraft/core/RegistryAccess;Lnet/neoforged/neoforge/network/connection/ConnectionType;)Ljava/util/function/Function;"),
            require = 2)
    private Function<ByteBuf, RegistryFriendlyByteBuf> handleConfigurationFinished(RegistryAccess access, ConnectionType connectionType) {
        return x -> new CreativeByteBuf(x, access, connectionType, ((ClientConfigurationPacketListenerImpl) (Object) this).getConnection());
    }
    
}
