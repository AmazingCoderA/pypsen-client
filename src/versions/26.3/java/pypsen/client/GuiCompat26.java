package pypsen.client;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Method;
import java.util.ArrayList;

/** Bridges the 1.20.1 screen widget helpers to the 26.3 API. */
public final class GuiCompat26 {
    private GuiCompat26() {}

    public static Object add(Screen screen, Object widget) {
        for (Method m : Screen.class.getDeclaredMethods()) {
            if (!m.getName().equals("addRenderableWidget") || m.getParameterCount() != 1) continue;
            try {
                m.setAccessible(true);
                m.invoke(screen, widget);
            } catch (Exception ignored) {
                // Keep menu construction alive even if this widget type changed.
            }
            return widget;
        }
        return widget;
    }

    public static void clear(Screen screen) {
        for (Method m : Screen.class.getDeclaredMethods()) {
            if (!m.getName().equals("removeWidget") || m.getParameterCount() != 1) continue;
            try {
                m.setAccessible(true);
                for (Element child : new ArrayList<>(screen.children())) {
                    m.invoke(screen, child);
                }
            } catch (Exception ignored) {
                // Best-effort cleanup only.
            }
            return;
        }
    }
}
