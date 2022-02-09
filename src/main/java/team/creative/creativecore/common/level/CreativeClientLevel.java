package team.creative.creativecore.common.level;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import team.creative.creativecore.client.render.level.IRenderChunkSupplier;

public abstract class CreativeClientLevel extends CreativeLevel {
    
    public IRenderChunkSupplier renderChunkSupplier;
    
    final EntityTickList tickingEntities = new EntityTickList();
    private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<>(Entity.class, new CreativeClientLevel.EntityCallbacks());
    final List<AbstractClientPlayer> players = Lists.newArrayList();
    private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
    
    protected CreativeClientLevel(WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean debug, long seed) {
        super(worldInfo, radius, supplier, true, debug, seed);
    }
    
    @Override
    public List<? extends Player> players() {
        return players;
    }
    
    @Override
    public Entity getEntity(int id) {
        return this.getEntities().get(id);
    }
    
    @Override
    public LevelEntityGetter<Entity> getEntities() {
        return this.entityStorage.getEntityGetter();
    }
    
    @Override
    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + this.getChunkSource().gatherStats() + " E: " + this.entityStorage.gatherStats();
    }
    
    @Override
    public MapItemSavedData getMapData(String data) {
        return this.mapData.get(data);
    }
    
    @Override
    public void setMapData(String id, MapItemSavedData data) {
        this.mapData.put(id, data);
    }
    
    final class EntityCallbacks implements LevelCallback<Entity> {
        
        @Override
        public void onCreated(Entity entity) {}
        
        @Override
        public void onDestroyed(Entity entity) {}
        
        @Override
        public void onTickingStart(Entity entity) {
            CreativeClientLevel.this.tickingEntities.add(entity);
        }
        
        @Override
        public void onTickingEnd(Entity entity) {
            CreativeClientLevel.this.tickingEntities.remove(entity);
        }
        
        @Override
        public void onTrackingStart(Entity entity) {
            if (entity instanceof AbstractClientPlayer)
                CreativeClientLevel.this.players.add((AbstractClientPlayer) entity);
        }
        
        @Override
        public void onTrackingEnd(Entity entity) {
            entity.unRide();
            CreativeClientLevel.this.players.remove(entity);
            
            entity.onRemovedFromWorld();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveWorldEvent(entity, CreativeClientLevel.this));
        }
    }
    
}
