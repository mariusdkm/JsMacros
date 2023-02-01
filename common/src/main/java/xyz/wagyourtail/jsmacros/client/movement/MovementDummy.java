package xyz.wagyourtail.jsmacros.client.movement;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("EntityConstructor")
public class MovementDummy extends EntityPlayer {

    private List<Vec3> coordsHistory = new ArrayList<>();
    private List<PlayerInput> inputs = new ArrayList<>();

    private PlayerInput currentInput;
    private int jumpingCooldown;
    private ItemStack heldItem = null;
    private final List<ItemStack> armorStack = new ArrayList<>(4);

    public MovementDummy(MovementDummy player) {
        this(player.world, player.getGameProfile(), player.getPos(), new Vec3(player.velocityX, player.velocityY, player.velocityZ), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());

        this.inputs = new ArrayList<>(player.getInputs());
        this.coordsHistory = new ArrayList<>(player.getCoordsHistory());
        this.jumpingCooldown = player.jumpingCooldown;
        this.armorStack.addAll(player.armorStack);
    }

    public MovementDummy(EntityPlayerSP player) {
        this(player.world, player.getGameProfile(), player.getPos(), new Vec3(player.velocityX, player.velocityY, player.velocityZ), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());

        this.armorStack.addAll(Arrays.stream(player.getArmorStacks()).map((e) -> {
            if (e != null) return e.copy();
            return null;
        }).collect(Collectors.toList()));
    }

    public MovementDummy(World world, GameProfile gameProfile, Vec3 pos, Vec3 velocity, AxisAlignedBB hitBox, boolean onGround, boolean isSprinting, boolean isSneaking) {
        super(world, gameProfile);

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.velocityX = velocity.x;
        this.velocityY = velocity.y;
        this.velocityZ = velocity.z;
        this.setBoundingBox(hitBox);
        this.setSprinting(isSprinting);
        this.setSneaking(isSneaking);
        this.stepHeight = 0.6F;
        this.onGround = onGround;
        this.coordsHistory.add(this.getPos());
    }

    public List<Vec3> getCoordsHistory() {
        return coordsHistory;
    }

    public List<PlayerInput> getInputs() {
        return inputs;
    }

    public Vec3 applyInput(PlayerInput input) {
        inputs.add(input); // We use this and not the clone, since the clone may be modified?
        PlayerInput currentInput = input.clone();
        this.yaw = currentInput.yaw;

        Vec3 velocity = new Vec3(this.velocityX, this.velocityY, this.velocityZ);
        double velX = velocity.x;
        double velY = velocity.y;
        double velZ = velocity.z;
        if (Math.abs(velocity.x) < 0.005D) {
            velX = 0.0D;
        }
        if (Math.abs(velocity.y) < 0.005D) {
            velY = 0.0D;
        }
        if (Math.abs(velocity.z) < 0.005D) {
            velZ = 0.0D;
        }
        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;

        /** Sneaking start **/
        // For consistency with addInput(s), creator of PlayerInput must
        // multiply movementForward/movementStrafe when sneaking
        // if (currentInput.sneaking) {
        //     // Yeah this looks dumb, but that is the way minecraft does it
        //     currentInput.movementSideways = (float) ((double) currentInput.movementSideways * 0.3D);
        //     currentInput.movementForward = (float) ((double) currentInput.movementForward * 0.3D);
        // }
        this.setSneaking(currentInput.sneaking);
        /** Sneaking end **/

        /** Sprinting start **/
        boolean hasHungerToSprint = true;
        if (!this.isSprinting() && currentInput.movementForward > 0.8F && hasHungerToSprint && !this.hasStatusEffect(Potion.BLINDNESS) && currentInput.sprinting) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (currentInput.movementForward < 0.8F || this.horizontalCollision)) {
            this.setSprinting(false);
        }
        /** Sprinting end **/

        /** Jumping start **/
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (currentInput.jumping) {
            if (this.onGround && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        /** Juming END **/

        this.travel(currentInput.movementSideways * 0.98F, currentInput.movementForward * 0.98F);

        /* flyingSpeed only gets set after travel */
        this.flyingSpeed = this.isSprinting() ? 0.026F : 0.02F;

        return this.getPos();
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
    @Override
    public void move(double x, double y, double z) {
        if (this.isClimbing() && y < 0.0D && this.isSneaking()) {
            this.velocityX = x;
            this.velocityY = 0.0D;
            this.velocityZ = z;
        }
        super.move(x, y, z);
    }

    @Override
    protected boolean canClimb() {
        return !this.onGround || !this.isSneaking();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }
    @Override
    public ItemStack[] getArmorStacks() {
        return new ItemStack[0];
    }

    @Override
    public ItemStack getStackInHand() {
        return null;
    }

    @Override
    public ItemStack getMainSlot(int i) {
        return null;
    }

    @Override
    public ItemStack func_82169_q(int i) {
        return null;
    }

    @Override
    public void setArmorSlot(int i, ItemStack itemStack) {

    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MovementDummy clone() {
        return new MovementDummy(this);
    }
}
