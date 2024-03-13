package team.creative.creativecore.common.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import team.creative.creativecore.Side;

public class InvalidSideException extends RuntimeException {
    
    public InvalidSideException(Side side) {
        super("This cannot not be executed on " + side.name() + " side");
    }
    
    public InvalidSideException(boolean client) {
        this(client ? Side.CLIENT : Side.SERVER);
    }
    
    public InvalidSideException(LevelAccessor level) {
        this(level.isClientSide());
    }
    
    public InvalidSideException(Player player) {
        this(player.level.isClientSide);
    }
    
}
