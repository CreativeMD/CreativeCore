package team.creative.creativecore.client.sound;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;

public interface SpecialSoundInstance extends SoundInstance {
    
    public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, ResourceLocation location, boolean looping);
    
    @Override
    default CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return getAudioStream(soundBuffers, sound.getPath(), looping);
    }
    
}
