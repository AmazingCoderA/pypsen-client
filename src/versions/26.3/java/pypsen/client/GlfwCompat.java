package pypsen.client;

/**
 * Drop-in replacement for the GLFW constants and input queries used by the
 * shared source tree. 26.3 replaced GLFW with SDL, so the queries fall back to
 * neutral values; key binds can still be configured in the GUI.
 */
public final class GlfwCompat {
    private GlfwCompat() {}

    public static final int GLFW_KEY_UNKNOWN = -1;
    public static final int GLFW_KEY_SPACE = 32;
    public static final int GLFW_KEY_MINUS = 45;
    public static final int GLFW_KEY_TAB = 258;
    public static final int GLFW_KEY_ESCAPE = 256;
    public static final int GLFW_KEY_LEFT_CONTROL = 341;
    public static final int GLFW_KEY_LEFT_SHIFT = 340;
    public static final int GLFW_KEY_RIGHT_SHIFT = 344;
    public static final int GLFW_KEY_G = 71;
    public static final int GLFW_KEY_H = 72;
    public static final int GLFW_KEY_R = 82;
    public static final int GLFW_KEY_V = 86;
    public static final int GLFW_KEY_X = 88;

    public static final int GLFW_MOUSE_BUTTON_MIDDLE = 2;
    public static final int GLFW_PRESS = 1;
    public static final int GLFW_RELEASE = 0;

    public static int glfwGetKey(long window, int key) {
        return GLFW_RELEASE;
    }

    public static int glfwGetMouseButton(long window, int button) {
        return GLFW_RELEASE;
    }

    public static String glfwGetKeyName(int key, int scancode) {
        return null;
    }
}
