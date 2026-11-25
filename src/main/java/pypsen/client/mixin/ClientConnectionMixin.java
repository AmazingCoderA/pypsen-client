package pypsen.client.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pypsen.client.PypsenClient;
import pypsen.client.PypsenFeatures;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (PypsenFeatures.onSendPacket(packet)) {
            ci.cancel();
            return;
        }
        if (PypsenClient.freecamOn && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();
        }
        if (PypsenClient.freezeOn && PypsenClient.freezeMode == 1 && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();
        }
    }
}
