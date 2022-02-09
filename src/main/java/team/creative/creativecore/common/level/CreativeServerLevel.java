package team.creative.creativecore.common.level;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.storage.WritableLevelData;

public abstract class CreativeServerLevel extends CreativeLevel {
    
    private final MinecraftServer server;
    final List<ServerPlayer> players = Lists.newArrayList();
    final EntityTickList entityTickList = new EntityTickList();
    private final PersistentEntitySectionManager<Entity> entityManager;
    public boolean noSave;
    
    protected CreativeServerLevel(MinecraftServer server, WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean debug, long seed) {
        super(worldInfo, radius, supplier, false, debug, seed);
        this.server = server;
        this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new CreativeServerLevel.EntityCallbacks(), new EntityPersistentStorage<>() {
            
            @Override
            public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos pos) {
                return CompletableFuture.completedFuture(new ChunkEntities<Entity>(pos, Collections.EMPTY_LIST));
            }
            
            @Override
            public void storeEntities(ChunkEntities<Entity> chunk) {}
            
            @Override
            public void flush(boolean p_182503_) {}
        });
    }
    
    @Override
    public MinecraftServer getServer() {
        return server;
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
        return this.entityManager.getEntityGetter();
    }
    
    @Override
    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + this.getChunkSource().gatherStats() + " E: " + this.entityManager.gatherStats();
    }
    
    public boolean areEntitiesLoaded(long p_143320_) {
        return this.entityManager.areEntitiesLoaded(p_143320_);
    }
    
    public boolean isPositionEntityTicking(BlockPos p_143341_) {
        return this.entityManager.isPositionTicking(p_143341_);
    }
    
    public boolean isPositionEntityTicking(ChunkPos p_143276_) {
        return this.entityManager.isPositionTicking(p_143276_);
    }
    
    final class EntityCallbacks implements LevelCallback<Entity> {
        @Override
        public void onCreated(Entity p_143355_) {}
        
        @Override
        public void onDestroyed(Entity p_143359_) {
            CreativeServerLevel.this.getScoreboard().entityRemoved(p_143359_);
        }
        
        @Override
        public void onTickingStart(Entity p_143363_) {
            CreativeServerLevel.this.entityTickList.add(p_143363_);
        }
        
        @Override
        public void onTickingEnd(Entity p_143367_) {
            CreativeServerLevel.this.entityTickList.remove(p_143367_);
        }
        
        @Override
        public void onTrackingStart(Entity p_143371_) {
            CreativeServerLevel.this.getChunkSource().addEntity(p_143371_);
            
        }
        
        @Override
        public void onTrackingEnd(Entity p_143375_) {
            CreativeServerLevel.this.getChunkSource().removeEntity(p_143375_);
            
            GameEventListenerRegistrar gameeventlistenerregistrar = p_143375_.getGameEventListenerRegistrar();
            if (gameeventlistenerregistrar != null) {
                gameeventlistenerregistrar.onListenerRemoved(p_143375_.level);
            }
            
        }
    }
    
}
