package team.creative.creativecore.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.neoforged.neoforge.client.GlStateBackup;

public class CreativePlatformHooks {
    
    private static final GlStateBackup stateBackup = new GlStateBackup();
    
    public static void backupRenderState() {
        RenderSystem.backupGlState(stateBackup);
    }
    
    public static void restoreRenderState() {
        RenderSystem.restoreGlState(stateBackup);
    }
    
}
