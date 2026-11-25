package pypsen.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Small reflection bridge for members removed in 26.3. */
public final class Compat26 {
    private Compat26() {}

    public static long windowHandle(Window window) {
        try {
            Method m = window.getClass().getMethod("getHandle");
            Object value = m.invoke(window);
            return value instanceof Number n ? n.longValue() : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static Screen currentScreen(MinecraftClient mc) {
        for (String name : new String[]{"screen", "currentScreen"}) {
            try {
                Field f = mc.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object value = f.get(mc);
                if (value instanceof Screen s) return s;
            } catch (Exception ignored) {
                // try the next field name
            }
        }
        return null;
    }

    /** Inventory clicks were moved out of the interaction manager in 26.3. */
    public static void clickSlot(Object interactionManager, int syncId, int slot,
                                 int button, int action, PlayerEntity player) {
        try {
            Class<?> clickType = Class.forName("net.minecraft.util.ClickType");
            Object click = clickType.getField(action == 0 ? "PRIMARY" : "SECONDARY").get(null);
            for (Method m : interactionManager.getClass().getMethods()) {
                Class<?>[] args = m.getParameterTypes();
                if (!m.getName().equals("clickSlot") || args.length != 5) continue;
                if (args[0] != int.class || args[1] != int.class || args[2] != int.class) continue;
                m.invoke(interactionManager, syncId, slot, button, click, player);
                return;
            }
        } catch (Exception ignored) {
            // 26.3 no longer exposes this call; keep the client alive.
        }
    }

    /** 26.3 requires an explicit swing animation component. */
    public static void swing(LivingEntity entity) {
        try {
            entity.swing(Hand.MAIN_HAND, SwingAnimationComponent.DEFAULT, false);
        } catch (Exception ignored) {
            // Rendering-only helper; never let it break gameplay.
        }
    }

    /** The old camera exposed a right vector; 26.3 stores the left one. */
    public static Vector3fc cameraRight(Camera camera) {
        return new Vector3f(camera.leftVector()).negate();
    }
}
