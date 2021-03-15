package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends EntityPlayerSP> extends PlayerEntityHelper<T> {

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp_double(pitch, -90.0D, 90.0D);
        base.prevRotationPitch = base.rotationPitch;
        base.prevRotationYaw = base.rotationYaw;
        base.rotationPitch = (float) pitch;
        base.rotationYaw = (float) MathHelper.wrapAngleTo180_double(yaw);
        return this;
    }

    /**
     * look at the specified coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.2.8
     */
    public ClientPlayerEntityHelper<T> lookAt(double x, double y, double z) {
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.posX, base.posY + base.getEyeHeight(), base.posZ, x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getFoodStats().getFoodLevel();
    }
    
    public String toString() {
        return "Client" + super.toString();
    }
}
