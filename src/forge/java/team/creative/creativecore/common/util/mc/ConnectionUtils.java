package team.creative.creativecore.common.util.mc;

import net.minecraft.network.Connection;
import net.minecraft.network.ProtocolInfo;

public class ConnectionUtils {
    
    public static ProtocolInfo<?> getProtocolInfo(Connection connection) {
        return connection.getInboundProtocol();
    }
    
}
