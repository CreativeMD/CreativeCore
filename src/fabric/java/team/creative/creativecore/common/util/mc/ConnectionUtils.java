package team.creative.creativecore.common.util.mc;

import net.minecraft.network.Connection;
import net.minecraft.network.ProtocolInfo;
import team.creative.creativecore.mixin.ConnectionMixin;

public class ConnectionUtils {
    
    public static ProtocolInfo<?> getProtocolInfo(Connection connection) {
        return ((ConnectionMixin) (Object) connection).protocolInfo;
    }
    
}
