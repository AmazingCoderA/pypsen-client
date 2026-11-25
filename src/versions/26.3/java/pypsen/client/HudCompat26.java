package pypsen.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/** Bridges the old HudRenderCallback registration to the 26.3 HudElement API. */
public final class HudCompat26 {
    private HudCompat26() {}

    public interface Callback {
        void render(DrawContext26 context, RenderTickCounter counter);
    }

    public static void register(Callback callback) {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("pypsen", "overlay"),
            (graphics, counter) -> callback.render(new DrawContext26(graphics), counter));
    }
}
