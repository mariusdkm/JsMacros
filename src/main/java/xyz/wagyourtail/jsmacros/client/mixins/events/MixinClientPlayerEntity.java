package xyz.wagyourtail.jsmacros.client.mixins.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventAirChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDamage;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEXPChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSignEdit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntityPlayerSP.class)
class MixinClientPlayerEntity extends AbstractClientPlayer {
    
    @Shadow
    protected Minecraft mc;
    
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    
    @Override
    public void setAir(int air) {
        if (air % 20 == 0) new EventAirChange(air);
        super.setAir(air);
    }
    
    @Inject(at = @At("HEAD"), method="setXPStats")
    public void onSetExperience(float progress, int total, int level, CallbackInfo info) {
        new EventEXPChange(progress, total, level);
    }
    
    @Inject(at = @At("TAIL"), method="damageEntity")
    private void onApplyDamage(DamageSource source, float amount, final CallbackInfo info) {
        new EventDamage(source, this.getHealth(), amount);
    }
    
    @Inject(at = @At("HEAD"), method="openEditSign", cancellable= true)
    public void onOpenEditSignScreen(TileEntitySign sign, CallbackInfo info) {
        List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));
        final EventSignEdit event = new EventSignEdit(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ());
        lines = event.signText;
        if (event.closeScreen) {
            for (int i = 0; i < 4; ++i) {
                sign.signText[i] = new ChatComponentText(lines.get(i));
            }
            sign.markDirty();
            sendQueue.addToSendQueue(new C12PacketUpdateSign(sign.getPos(), lines.stream().map(ChatComponentText::new).toArray(IChatComponent[]::new)));
            info.cancel();
            return;
        }
        //this part to not info.cancel is here for more compatibility with other mods.
        boolean cancel = false;
        for (String line : lines) {
            if (!line.equals("")) {
                cancel = true;
                break;
            }
        } //else
        if (cancel) {
            final GuiEditSign signScreen = new GuiEditSign(sign);
            mc.displayGuiScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                ((ISignEditScreen)signScreen).jsmacros_setLine(i, lines.get(i));
            }
            info.cancel();
        }
    }
    
    //IGNORE
    public MixinClientPlayerEntity(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
}
