package team.creative.creativecore;

import net.minecraft.world.entity.player.Player;

public interface ICreativeAttachmentType<T> {
	T get(Player player);
	void set(Player player, T value);
}
