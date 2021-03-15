package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.function.Consumer;

/**
 * 
 * Functions for getting and modifying the player's state.
 * 
 * An instance of this class is passed to scripts as the {@code player} variable.
 * 
 * @author Wagyourtail
 */
 @Library("player")
 @SuppressWarnings("unused")
public class FPlayer extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getMinecraft();
    /**
     * @see xyz.wagyourtail.jsmacros.client.api.classes.Inventory
     * 
     * @return the Inventory handler
     */
    public Inventory openInventory() {
        assert mc.thePlayer != null && mc.thePlayer.inventory != null;
        return Inventory.create();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper
     * 
     * @since 1.0.3
     * 
     * @return the player entity wrapper.
     */
    public ClientPlayerEntityHelper<EntityPlayerSP> getPlayer() {
        assert mc.thePlayer != null;
        return new ClientPlayerEntityHelper<>(mc.thePlayer);
    }

    /**
     * @since 1.0.9
     * 
     * @return the player's current gamemode.
     */
    public String getGameMode() {
        assert mc.playerController != null;
        WorldSettings.GameType mode = mc.playerController.getCurrentGameType();
        if (mode == null) mode = WorldSettings.GameType.NOT_SET;
        return mode.getName();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper
     * 
     * @since 1.0.5
     * 
     * @param distance
     * @param fluid
     * @return the block/liquid the player is currently looking at.
     */
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        assert mc.theWorld != null;
        assert mc.thePlayer != null;
        Vec3 vec3 = mc.thePlayer.getPositionEyes(0);
        Vec3 vec31 = mc.thePlayer.getLook(0);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
        MovingObjectPosition h = mc.theWorld.rayTraceBlocks(vec3, vec32, fluid, false, true);
        if (h.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) return null;
        IBlockState b = mc.theWorld.getBlockState(h.getBlockPos());
        TileEntity t = mc.theWorld.getTileEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.air)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper
     * 
     * @since 1.0.5
     * 
     * @return the entity the player is currently looking at.
     */
    public EntityHelper<Entity> rayTraceEntity() {
        if (mc.pointedEntity != null) return new EntityHelper<>(mc.pointedEntity);
        else return null;
    }

    /**
     * Write to a sign screen if a sign screen is currently open.
     * 
     * @since 1.2.2
     * 
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @return {@link java.lang.Boolean boolean} of success.
     */
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof GuiEditSign) {
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(3, l4);
            return true;
        }
        return false;
    }

    /**
     * @see #takeScreenshot(String, String, MethodWrapper)
     *
     * @since 1.2.6
     * @param folder
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null;
        mc.addScheduledTask(() -> {
            IChatComponent text = ScreenShotHelper.saveScreenshot(new File(Core.instance.config.macroFolder, folder), mc.getFramebuffer().framebufferWidth, mc.getFramebuffer().framebufferHeight, mc.getFramebuffer());
            if (callback != null) callback.accept(new TextHelper(text));
        });
    }
    
    /**
     * Take a screenshot and save to a file.
     *
     * {@code file} is the optional one, typescript doesn't like it not being the last one that's optional
     *
     * @since 1.2.6
     * 
     * @param folder
     * @param file
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, String file, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null && file != null;
        mc.addScheduledTask(() -> {
            IChatComponent text = ScreenShotHelper.saveScreenshot(new File(Core.instance.config.macroFolder, folder), file, mc.getFramebuffer().framebufferWidth, mc.getFramebuffer().framebufferHeight, mc.getFramebuffer());
            if (callback != null) callback.accept(new TextHelper(text));
        });
    }
}
