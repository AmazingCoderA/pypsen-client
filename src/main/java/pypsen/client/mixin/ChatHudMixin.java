package pypsen.client.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pypsen.client.PypsenClient;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text modifyChatMessage(Text message) {
        if (PypsenClient.nameHiderOn && message != null && MinecraftClient.getInstance().getSession() != null) {
            String myName = MinecraftClient.getInstance().getSession().getUsername();
            String str = message.getString();
            if (str.contains(myName)) {
                return Text.literal(str.replace(myName, PypsenClient.fakeName));
            }
        }
        return message;
    }
}
