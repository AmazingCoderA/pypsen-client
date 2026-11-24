package pypsen.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pypsen.client.PypsenClient;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (PypsenClient.playerEspOn && entity instanceof PlayerEntity) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text modifyLabel(Text text, T entity) {
        if (PypsenClient.nameHiderOn && text != null && MinecraftClient.getInstance().getSession() != null) {
            String myName = MinecraftClient.getInstance().getSession().getUsername();
            String str = text.getString();
            if (str.contains(myName)) {
                return Text.literal(str.replace(myName, PypsenClient.fakeName));
            }
        }
        return text;
    }
}
