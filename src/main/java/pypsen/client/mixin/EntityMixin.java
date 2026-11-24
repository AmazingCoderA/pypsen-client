package pypsen.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pypsen.client.PypsenClient;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getTargetingMargin", at = @At("RETURN"), cancellable = true)
    private void pypsen$hitbox(CallbackInfoReturnable<Float> cir) {
        if (PypsenClient.hitboxOn && (Object) this instanceof LivingEntity && !((Object) this instanceof ClientPlayerEntity)) {
            cir.setReturnValue(PypsenClient.hitboxVal);
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void pypsen$getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (PypsenClient.hitboxOn && (Object) this instanceof LivingEntity && !((Object) this instanceof ClientPlayerEntity)) {
            cir.setReturnValue(cir.getReturnValue().scaled(PypsenClient.hitboxVal));
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void pypsen$pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (PypsenClient.velocityOn && (Object) this instanceof ClientPlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void pypsen$isInvisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (PypsenClient.seeInvisOn) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true)
    private void pypsen$slowMovement(net.minecraft.block.BlockState state, net.minecraft.util.math.Vec3d multiplier, CallbackInfo ci) {
        if (PypsenClient.noWebOn && (Object) this instanceof ClientPlayerEntity) {
            if (PypsenClient.noWebMode == 0 || PypsenClient.noWebMode == 1) {
                ci.cancel();
            } else if (PypsenClient.noWebMode == 2) {
                // Grim: just reduce the effect slightly, handled in tick if needed, or just cancel here
                ci.cancel();
            }
        }
    }
}

