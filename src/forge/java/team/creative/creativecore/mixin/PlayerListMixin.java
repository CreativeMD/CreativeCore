package team.creative.creativecore.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.UnboundProtocol;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import team.creative.creativecore.common.network.CreativeByteBuf;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    
    @WrapOperation(
            method = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/UnboundProtocol;bind(Ljava/util/function/Function;Ljava/lang/Object;)Lnet/minecraft/network/ProtocolInfo;"),
            require = 1)
    private ProtocolInfo bind(UnboundProtocol unbound, Function function, Object context, Operation<ProtocolInfo> operation) {
        ServerGamePacketListenerImpl l = (ServerGamePacketListenerImpl) context;
        return unbound.bind(x -> new CreativeByteBuf((ByteBuf) x, l.player.registryAccess(), l.getConnectionType(), l.getConnection()), context);
    }
    
}
