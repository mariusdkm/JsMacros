package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinPlayerListHud implements IPlayerListHud {
    
    
    @Shadow private IChatComponent header;
    
    @Shadow private IChatComponent footer;
    
    @Override
    public IChatComponent getHeader() {
        return this.header;
    }
    
    @Override
    public IChatComponent getFooter() {
        return this.footer;
    }
    
}
