package pypsen.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Extra feature pack: AI aura, crystal aura, food, fake lag and advanced visuals.
 * Kept in its own class so every existing module stays intact.
 */
public final class PypsenFeatures {

    // ── TOGGLES ──
    public static boolean aiAuraOn = false, crystalAuraOn = false, fakeLagOn = false;
    public static boolean autoEatOn = false, noclipProOn = false, spiderProOn = false;
    public static boolean advancedVisualsOn = true, nameTagDetailsOn = true, hudRainbow = false;
    // AI KillAura upgrades
    public static boolean aiUseLearning = true, aiAutoTrain = true, aiAutoSave = false;
    public static boolean aiFocusTarget = true, aiOnlyPlayers = false;
    // Crystal Aura upgrades
    public static boolean crystalSafety = true, crystalAutoSwitch = true, crystalRotate = true;
    public static boolean crystalOffHand = false, crystalOnlyPlayers = false;
    // Visual FX
    public static boolean fxOn = true, hitMarkerOn = false, damageFlashOn = false;
    public static boolean targetTrailOn = false, comboCounterOn = false, killFeedOn = false;
    public static boolean skeletonEspOn = false, fxScanlinesOn = false;
    // Stealth / obfuscation (client-side only)
    public static boolean stealthOn = false, stealthHideHud = false, stealthNeutralLabels = false;
    public static boolean stealthRandomDelays = false, stealthRotatingName = false;

    // ── VALUES ──
    public static float aiRange = 6f, aiCps = 10f, crystalRange = 6f, crystalCps = 8f;
    public static float fakeLagDelay = 120f, autoEatHealth = 14f, autoEatHunger = 12f;
    public static float spiderBoost = 0.14f, noclipStep = 0.35f;
    public static int aiSamples = 300;
    // AI learning values
    public static float aiLearningRate = 0.15f, aiModelInfluence = 0.7f, aiSmooth = 1f;
    public static float aiJitter = 0f, aiTimingJitter = 20f, aiSwitchDelay = 180f;
    public static int aiTrainRounds = 60;
    // Crystal placement values
    public static float crystalPlaceRange = 5f, crystalPlaceDelay = 160f, crystalPredict = 0f;
    public static float crystalMinHp = 0f, crystalSelfDmg = 8f, crystalTotemHp = 0.5f;
    public static float crystalFaceArmor = 6f, crystalFaceHp = 8f;
    public static int crystalMaxCrystals = 2;
    // Visual FX values
    public static float hitMarkerTime = 180f, damageFlashAlpha = 0.25f;
    public static float comboReset = 3000f, skeletonRange = 48f;
    public static int fxThickness = 1;

    // ── MODES ──
    public static int aiPreset = 0, crystalMode = 0, fakeLagMode = 0, spiderProMode = 0;
    public static int noclipProMode = 0, hudStyle = 1;
    public static int aiTargetMode = 0, aiRotationMode = 0;
    public static int crystalTargetMode = 0, crystalPlaceMode = 0;
    public static int crosshairStyle = 1, fxTheme = 0, hitMarkerStyle = 0, stealthMode = 0;
    public static final String[] AI_PRESETS = {"Balanced", "Aggressive", "Legit", "HvH", "Smart"};
    public static final String[] AI_TARGET_MODES = {"AI Score", "Lowest HP", "Closest", "Shield First", "Armor Break"};
    public static final String[] AI_ROTATION_MODES = {"Instant", "Smooth", "Jitter", "No Rotate"};
    public static final String[] CRYSTAL_MODES = {"Attack + Place", "Attack Only", "Smart Danger",
        "AntiTotem Rush", "FacePlace", "Crystal Storm", "Safe Hybrid"};
    public static final String[] CRYSTAL_TARGET_MODES = {"Smart", "Closest", "Lowest HP", "No Shield", "High Armor"};
    public static final String[] CRYSTAL_PLACE_MODES = {"Feet", "Head", "Offset", "Smart Angle"};
    public static final String[] CROSSHAIR_STYLES = {"Default", "Dot", "Cross", "Circle", "Dynamic", "Hidden"};
    public static final String[] FX_THEMES = {"Green", "Red", "Cyan", "Purple", "Gold", "Rainbow"};
    public static final String[] HITMARKER_STYLES = {"X", "Corners", "Square", "Pulse"};
    public static final String[] STEALTH_MODES = {"Clean HUD", "Neutral Labels", "Random Delays", "Full Stealth"};
    public static final String[] STEALTH_NAMES = {"Protected", "Player", "Guest", "Steve", "Alex", "Anonymous"};
    public static final String[] FAKELAG_MODES = {"Motion", "All C2S", "Burst"};
    public static final String[] SPIDER_PRO_MODES = {"Wall Climb", "AntiStuck", "Phase Jump", "Turbo"};
    public static final String[] NOCLIP_PRO_MODES = {"Phantom", "Phase Step", "Compact", "Ghost"};
    public static final String[] HUD_STYLES = {"Classic", "Modern", "Minimal", "Compact", "Rainbow"};

    // ── KEYBINDS ──
    public static int keyAiAura = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyCrystalAura = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyFakeLag = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyAutoEat = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyNoclipPro = GLFW.GLFW_KEY_UNKNOWN;
    public static int keySpiderPro = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyVisualsPro = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyFx = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyStealth = GLFW.GLFW_KEY_UNKNOWN;

    public static String aiTrainFile = "pypsen_ai.txt";

    // ── RUNTIME STATE ──
    private static final List<Packet<?>> packets = new ArrayList<>();
    private static final Set<Integer> pressedFeatureKeys = new HashSet<>();
    private static final List<double[]> training = new ArrayList<>();
    private static final List<String> killFeed = new ArrayList<>();
    private static final List<Long> killFeedTime = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean flushing = false;
    private static double[] presetWeights = {0.35, 0.25, 0.20, 0.10, 0.10, 0.20, 0.02, 1.0};
    private static double[] weights = presetWeights.clone();
    private static LivingEntity aiTarget = null;
    private static File runDir = null;
    private static long lastFlush = 0L, lastAiAttack = 0L, lastCrystal = 0L, lastEat = 0L;
    private static long lastAiSwitch = 0L, lastCrystalPlace = 0L, lastAutoTrain = 0L;
    private static long lastHitMarker = 0L, lastDamage = 0L, lastNameChange = 0L;
    private static float lastPlayerHealth = -1f;
    private static int aiTick = 0, samplesLoaded = 0, samplesLearned = 0;
    private static int combo = 0, totalKills = 0;
    public static double aiConfidence = 0.0;

    private PypsenFeatures() {}

    /** Called once by the main mod initializer. */
    public static void init(MinecraftClient mc) {
        runDir = mc.runDirectory;
        applyPreset(aiPreset);
        loadAiFile(mc.runDirectory);
        if (aiAutoTrain) trainModel(aiTrainRounds);
        if (stealthOn) applyStealthMode(stealthMode);
    }

    /** Called from the main client tick handler. */
    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        var p = mc.player;

        handleFakeLag(mc);
        tickAutoEat(mc, p);
        tickSpiderPro(p);
        tickNoclipPro(p);
        tickAiAura(mc, p);
        tickCrystalAura(mc, p);
        tickHudRainbow(mc);
        tickFx(p);
        tickStealth();
    }

    /** Called from the main HUD render callback. */
    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        if (fxOn) drawFx(ctx, mc);
        if (!advancedVisualsOn) return;
        LivingEntity target = aiTarget != null && aiTarget.isAlive() ? aiTarget : PypsenClient.getAuraTarget();
        if (target == null) return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int x = hudStyle == 2 ? 4 : sw / 2 - 110;
        int y = hudStyle == 2 ? sh - 74 : sh / 2 - 74;
        int w = hudStyle == 3 ? 120 : 220;
        int h = hudStyle == 2 ? 46 : 70;

        int bg = hudRainbow ? rainbow(0x55) : 0x90081014;
        int accent = hudRainbow ? rainbow(0) : 0xFF55FF55;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + w, y + 2, accent);

        float hp = target.getHealth();
        float maxHp = target.getMaxHealth();
        String name = target.getName().getString();
        String line1 = "\u00a7f" + name;
        String line2 = "\u00a7c\u2764 " + String.format("%.1f", hp) + "/" + String.format("%.0f", maxHp)
            + " \u00a77| \u00a7b" + String.format("%.1fm", mc.player.distanceTo(target));
        String line3 = "\u00a77AI \u00a7f" + AI_PRESETS[clamp(aiPreset, 0, AI_PRESETS.length - 1)]
            + " \u00a77samples \u00a7f" + (samplesLoaded + samplesLearned);

        if (hudStyle == 0 || hudStyle == 1 || hudStyle == 4) {
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line1), x + 6, y + 8, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line2), x + 6, y + 22, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line3), x + 6, y + 36, 0xFFFFFF);
        } else {
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line1), x + 6, y + 8, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line2), x + 6, y + 22, 0xFFFFFF);
        }

        int barW = w - 12;
        int filled = maxHp <= 0 ? 0 : (int) (barW * Math.max(0f, Math.min(1f, hp / maxHp)));
        ctx.fill(x + 6, y + h - 12, x + 6 + barW, y + h - 6, 0xFF333333);
        ctx.fill(x + 6, y + h - 12, x + 6 + filled, y + h - 6, accent);

        if (nameTagDetailsOn && target instanceof PlayerEntity tp) {
            String tag = (PypsenClient.friends.contains(tp.getName().getString()) ? "\u00a7aFRIEND" : "\u00a7cENEMY")
                + (tp.isBlocking() ? " \u00a7e[SHIELD]" : "")
                + " \u00a77armor " + tp.getArmor();
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(tag), x + 6, y + h - 28, 0xFFFFFF);
        }
    }

    // ══════════ VISUAL FX ══════════

    private static void tickFx(PlayerEntity p) {
        long now = System.currentTimeMillis();
        float hp = p.getHealth() + p.getAbsorptionAmount();
        if (lastPlayerHealth < 0f) lastPlayerHealth = hp;
        if (hp < lastPlayerHealth - 0.05f) lastDamage = now;
        lastPlayerHealth = hp;
        if (combo > 0 && now - lastHitMarker > Math.max(0f, comboReset)) combo = 0;
        while (!killFeed.isEmpty() && now - killFeedTime.get(0) > 5000L) {
            killFeed.remove(0);
            killFeedTime.remove(0);
        }
    }

    private static void tickStealth() {
        if (!stealthOn || !stealthRotatingName) return;
        long now = System.currentTimeMillis();
        if (now - lastNameChange < 10000L) return;
        lastNameChange = now;
        PypsenClient.fakeName = STEALTH_NAMES[random.nextInt(STEALTH_NAMES.length)];
    }

    private static void registerHit() {
        lastHitMarker = System.currentTimeMillis();
        combo++;
    }

    private static void registerHit(LivingEntity target) {
        registerHit();
        if (target == null || target.isAlive()) return;
        totalKills++;
        killFeed.add("\u00a7cKilled \u00a7f" + target.getName().getString());
        killFeedTime.add(System.currentTimeMillis());
        while (killFeed.size() > 5) {
            killFeed.remove(0);
            killFeedTime.remove(0);
        }
    }

    private static float stealthJitter() {
        return stealthOn && stealthRandomDelays ? (float) (random.nextDouble() * 45.0) : 0f;
    }

    public static boolean hudVisible() {
        return !(stealthOn && stealthHideHud);
    }

    public static String label(String real, String neutral) {
        return stealthOn && stealthNeutralLabels ? neutral : real;
    }

    public static int comboCount() {
        return combo;
    }

    public static int killCount() {
        return totalKills;
    }

    public static void applyStealthMode(int mode) {
        int m = clamp(mode, 0, STEALTH_MODES.length - 1);
        stealthOn = true;
        stealthHideHud = m == 0 || m == 3;
        stealthNeutralLabels = m == 1 || m == 3;
        stealthRandomDelays = m == 2 || m == 3;
    }

    private static void drawFx(DrawContext ctx, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int cx = sw / 2;
        int cy = sh / 2;
        int color = fxColor(0xFF);
        long now = System.currentTimeMillis();

        if (fxScanlinesOn) {
            for (int yy = 0; yy < sh; yy += 4) ctx.fill(0, yy, sw, yy + 1, 0x08000000);
        }

        drawCrosshair(ctx, mc, cx, cy, color);
        if (hitMarkerOn && now - lastHitMarker <= Math.max(0f, hitMarkerTime)) {
            drawHitMarker(ctx, cx, cy, now - lastHitMarker, color);
        }
        if (damageFlashOn && lastDamage > 0L && now - lastDamage < 350L) {
            int a = (int) (clamp(damageFlashAlpha, 0f, 1f) * 255f);
            int flash = a << 24 | 0xFF2020;
            ctx.fill(0, 0, sw, 3, flash);
            ctx.fill(0, sh - 3, sw, sh, flash);
            ctx.fill(0, 0, 3, sh, flash);
            ctx.fill(sw - 3, 0, sw, sh, flash);
        }
        if (comboCounterOn && combo > 1 && now - lastHitMarker <= Math.max(0f, comboReset)) {
            drawCenteredText(ctx, mc, "\u00a7eCOMBO x" + combo, cx, cy + 24, color);
        }
        if (killFeedOn && !killFeed.isEmpty()) {
            int yy = 6;
            for (int i = killFeed.size() - 1; i >= 0; i--) {
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal(killFeed.get(i)), 6, yy, 0xFFFFFF);
                yy += 12;
            }
        }
        if (targetTrailOn) drawTargetTrail(ctx, mc, cx, cy, color);
        if (skeletonEspOn) drawSkeletonEsp(ctx, mc, color);
    }

    private static void drawCrosshair(DrawContext ctx, MinecraftClient mc, int cx, int cy, int color) {
        int style = clamp(crosshairStyle, 0, CROSSHAIR_STYLES.length - 1);
        if (style == 0) return;
        int t = Math.max(1, clamp(fxThickness, 1, 4));
        if (style == 1) {
            ctx.fill(cx - t, cy - t, cx + t + 1, cy + t + 1, color);
        } else if (style == 2 || style == 4) {
            double speed = Math.abs(mc.player.getVelocity().x) + Math.abs(mc.player.getVelocity().z);
            int gap = style == 4 ? 3 + (int) Math.min(8.0, speed * 8.0) : 4;
            int len = 5;
            ctx.fill(cx - gap - len, cy, cx - gap, cy + t, color);
            ctx.fill(cx + gap, cy, cx + gap + len, cy + t, color);
            ctx.fill(cx, cy - gap - len, cx + t, cy - gap, color);
            ctx.fill(cx, cy + gap, cx + t, cy + gap + len, color);
        } else if (style == 3) {
            for (int a = 0; a < 360; a += 45) {
                double rad = Math.toRadians(a);
                int dx = (int) Math.round(Math.cos(rad) * 6.0);
                int dy = (int) Math.round(Math.sin(rad) * 6.0);
                ctx.fill(cx + dx - t, cy + dy - t, cx + dx + t + 1, cy + dy + t + 1, color);
            }
        } else {
            ctx.fill(cx - 8, cy - 8, cx + 8, cy + 8, 0x66000000);
        }
    }

    private static void drawHitMarker(DrawContext ctx, int cx, int cy, long age, int color) {
        int style = clamp(hitMarkerStyle, 0, HITMARKER_STYLES.length - 1);
        int r = 6 + (int) Math.min(8.0, age / 22.0);
        if (style == 0) {
            drawEspLine(ctx, cx - r, cy - r, cx + r, cy + r, color);
            drawEspLine(ctx, cx - r, cy + r, cx + r, cy - r, color);
        } else if (style == 1) {
            int g = r + 3;
            drawEspLine(ctx, cx - g, cy - g, cx - g + 4, cy - g, color);
            drawEspLine(ctx, cx - g, cy - g, cx - g, cy - g + 4, color);
            drawEspLine(ctx, cx + g, cy - g, cx + g - 4, cy - g, color);
            drawEspLine(ctx, cx + g, cy - g, cx + g, cy - g + 4, color);
            drawEspLine(ctx, cx - g, cy + g, cx - g + 4, cy + g, color);
            drawEspLine(ctx, cx - g, cy + g, cx - g, cy + g - 4, color);
            drawEspLine(ctx, cx + g, cy + g, cx + g - 4, cy + g, color);
            drawEspLine(ctx, cx + g, cy + g, cx + g, cy + g - 4, color);
        } else if (style == 2) {
            ctx.fill(cx - r, cy - r, cx + r, cy - r + 1, color);
            ctx.fill(cx - r, cy + r, cx + r, cy + r + 1, color);
            ctx.fill(cx - r, cy - r, cx - r + 1, cy + r, color);
            ctx.fill(cx + r, cy - r, cx + r + 1, cy + r, color);
        } else {
            for (int a = 0; a < 360; a += 60) {
                double rad = Math.toRadians(a);
                int dx = (int) Math.round(Math.cos(rad) * r);
                int dy = (int) Math.round(Math.sin(rad) * r);
                ctx.fill(cx + dx - 1, cy + dy - 1, cx + dx + 1, cy + dy + 1, color);
            }
        }
    }

    private static void drawCenteredText(DrawContext ctx, MinecraftClient mc, String text, int cx, int y, int color) {
        int w = mc.textRenderer.getWidth(Text.literal(text));
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(text), cx - w / 2, y, color);
    }

    private static int fxColor(int alpha) {
        int theme = clamp(fxTheme, 0, FX_THEMES.length - 1);
        return switch (theme) {
            case 1 -> alpha << 24 | 0xFF5555;
            case 2 -> alpha << 24 | 0x55FFFF;
            case 3 -> alpha << 24 | 0xFF55FF;
            case 4 -> alpha << 24 | 0xFFD700;
            case 5 -> rainbow(alpha);
            default -> alpha << 24 | 0x55FF55;
        };
    }

    private static float[] projectPoint(Vec3d pos, Vec3d camPos, org.joml.Vector3f right,
                                        org.joml.Vector3f up, org.joml.Vector3f forward,
                                        double focal, int sw, int sh) {
        double rx = pos.x - camPos.x;
        double ry = pos.y - camPos.y;
        double rz = pos.z - camPos.z;
        double cx = rx * right.x + ry * right.y + rz * right.z;
        double cy = rx * up.x + ry * up.y + rz * up.z;
        double cz = rx * forward.x + ry * forward.y + rz * forward.z;
        if (cz <= 0.1) return null;
        float sx = (float) (sw * 0.5 + (cx / cz) * focal);
        float sy = (float) (sh * 0.5 - (cy / cz) * focal);
        if (sx < -4096f || sx > 4096f || sy < -4096f || sy > 4096f) return null;
        return new float[]{sx, sy, (float) cz};
    }

    private static void drawEspLine(DrawContext ctx, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            ctx.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = err * 2;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

    private static boolean cameraBasis(MinecraftClient mc, int[] size, double[] focal,
                                       Vec3d[] camPos, org.joml.Vector3f[] basis) {
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return false;
        camPos[0] = camera.getPos();
        basis[0] = camera.getDiagonalPlane();
        basis[1] = camera.getVerticalPlane();
        basis[2] = camera.getHorizontalPlane();
        size[0] = mc.getWindow().getScaledWidth();
        size[1] = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double half = Math.toRadians(fov) * 0.5;
        double f = (size[1] * 0.5) / Math.tan(half);
        if (!Double.isFinite(f) || f <= 0 || half <= 0 || half >= Math.PI * 0.5) return false;
        focal[0] = f;
        return true;
    }

    private static void drawTargetTrail(DrawContext ctx, MinecraftClient mc, int cx, int cy, int color) {
        LivingEntity target = aiTarget != null && aiTarget.isAlive() ? aiTarget : PypsenClient.getAuraTarget();
        if (target == null) return;
        int[] size = new int[2];
        double[] focal = new double[1];
        Vec3d[] camPos = new Vec3d[1];
        org.joml.Vector3f[] basis = new org.joml.Vector3f[3];
        if (!cameraBasis(mc, size, focal, camPos, basis)) return;
        float[] p = projectPoint(eye(target), camPos[0], basis[0], basis[1], basis[2], focal[0], size[0], size[1]);
        if (p != null) drawEspLine(ctx, cx, cy, (int) p[0], (int) p[1], color);
    }

    private static void drawSkeletonEsp(DrawContext ctx, MinecraftClient mc, int color) {
        if (mc.world == null) return;
        int[] size = new int[2];
        double[] focal = new double[1];
        Vec3d[] camPos = new Vec3d[1];
        org.joml.Vector3f[] basis = new org.joml.Vector3f[3];
        if (!cameraBasis(mc, size, focal, camPos, basis)) return;
        float range = Math.max(4f, skeletonRange);
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity living) || e == mc.player || !living.isAlive() || e.isSpectator()) continue;
            if (PypsenClient.friends.contains(living.getName().getString())) continue;
            if (mc.player.distanceTo(e) > range) continue;
            Vec3d head = eye(living);
            Vec3d feet = new Vec3d(e.getX(), e.getY(), e.getZ());
            double w = e.getWidth() * 0.42;
            double shoulderY = head.y - living.getHeight() * 0.28;
            Vec3d left = new Vec3d(head.x - basis[0].x * w, shoulderY - basis[0].y * w, head.z - basis[0].z * w);
            Vec3d right = new Vec3d(head.x + basis[0].x * w, shoulderY + basis[0].y * w, head.z + basis[0].z * w);
            float[] ph = projectPoint(head, camPos[0], basis[0], basis[1], basis[2], focal[0], size[0], size[1]);
            float[] pf = projectPoint(feet, camPos[0], basis[0], basis[1], basis[2], focal[0], size[0], size[1]);
            float[] pl = projectPoint(left, camPos[0], basis[0], basis[1], basis[2], focal[0], size[0], size[1]);
            float[] pr = projectPoint(right, camPos[0], basis[0], basis[1], basis[2], focal[0], size[0], size[1]);
            if (ph == null || pf == null || pl == null || pr == null) continue;
            drawEspLine(ctx, (int) ph[0], (int) ph[1], (int) pf[0], (int) pf[1], color);
            drawEspLine(ctx, (int) pl[0], (int) pl[1], (int) pr[0], (int) pr[1], color);
            drawEspLine(ctx, (int) pl[0], (int) pl[1], (int) pf[0], (int) pf[1], color);
            drawEspLine(ctx, (int) pr[0], (int) pr[1], (int) pf[0], (int) pf[1], color);
        }
    }

    private static int rainbow(int alpha) {
        int hue = (int) ((System.currentTimeMillis() / 12L) % 360);
        float r = 0.5f + 0.5f * (float) Math.cos(Math.toRadians(hue));
        float g = 0.5f + 0.5f * (float) Math.cos(Math.toRadians(hue + 120));
        float b = 0.5f + 0.5f * (float) Math.cos(Math.toRadians(hue + 240));
        int rr = (int) (r * 255), gg = (int) (g * 255), bb = (int) (b * 255);
        return alpha << 24 | rr << 16 | gg << 8 | bb;
    }

    // ══════════ AI KILLAURA ══════════

    private static void tickAiAura(MinecraftClient mc, PlayerEntity p) {
        if (!aiAuraOn) { aiTarget = null; return; }
        if (!aiUseLearning) weights = presetWeights.clone();

        float range = Math.max(1f, aiRange);
        List<LivingEntity> candidates = new ArrayList<>();
        for (Entity e : mc.world.getOtherEntities(p, p.getBoundingBox().expand(range), entity -> true)) {
            if (!(e instanceof LivingEntity living) || !living.isAlive() || e == p || e.isSpectator()) continue;
            if (PypsenClient.friends.contains(living.getName().getString())) continue;
            if (aiOnlyPlayers && !(living instanceof PlayerEntity)) continue;
            if (p.distanceTo(living) <= range) candidates.add(living);
        }

        LivingEntity best = null;
        double bestScore = -1e9;
        for (LivingEntity living : candidates) {
            double score = scoreTarget(p, living, p.distanceTo(living), range);
            if (score > bestScore) { bestScore = score; best = living; }
        }

        long now = System.currentTimeMillis();
        if (aiFocusTarget && aiTarget != null && aiTarget.isAlive() && candidates.contains(aiTarget)
            && now - lastAiSwitch < (long) Math.max(0f, aiSwitchDelay)) {
            best = aiTarget;
        }
        if (best != aiTarget) {
            lastAiSwitch = now;
            aiTarget = best;
        }
        if (best == null) return;

        // Negative examples teach the model which targets NOT to pick, rate-limited.
        if (aiUseLearning && aiTick % 20 == 0 && candidates.size() > 1) {
            int added = 0;
            for (LivingEntity living : candidates) {
                if (living == best || added >= 2) continue;
                recordSample(p, living, 0);
                added++;
            }
        }

        if (clamp(aiRotationMode, 0, AI_ROTATION_MODES.length - 1) != 3) {
            float[] rot = smartRotations(p, best);
            p.setYaw(rot[0]);
            p.setPitch(rot[1]);
        }

        long cooldown = (long) Math.max(1L, Math.round(20L / Math.max(1f, aiCps))) * 50L;
        long jitter = (long) Math.max(0f, aiTimingJitter);
        if (jitter > 0L) cooldown += (long) (random.nextDouble() * jitter);
        if (now - lastAiAttack >= cooldown + (long) stealthJitter()) {
            lastAiAttack = now;
            mc.interactionManager.attackEntity(p, best);
            p.swingHand(Hand.MAIN_HAND);
            registerHit(best);
            if (aiUseLearning) recordSample(p, best, 1);
            int autoEvery = Math.max(100, clamp(aiSamples, 100, 2000) / 2);
            if (aiAutoTrain && samplesLearned > 0 && samplesLearned % autoEvery == 0) trainModel(aiTrainRounds);
        }
        aiTick++;
    }

    private static float[] smartRotations(PlayerEntity p, LivingEntity t) {
        float[] target = rotations(p, t, eye(t));
        int mode = clamp(aiRotationMode, 0, AI_ROTATION_MODES.length - 1);
        if (mode == 0) return target;
        if (mode == 1) {
            float f = clamp(aiSmooth, 0f, 1f);
            return new float[]{
                p.getYaw() + MathHelperWrapDegrees(target[0] - p.getYaw()) * f,
                p.getPitch() + (target[1] - p.getPitch()) * f
            };
        }
        if (mode == 2) {
            float j = Math.max(0f, aiJitter);
            return new float[]{
                target[0] + (float) (random.nextDouble() * 2.0 - 1.0) * j,
                clamp(target[1] + (float) (random.nextDouble() * 2.0 - 1.0) * j, -90f, 90f)
            };
        }
        return target;
    }

    private static double scoreTarget(PlayerEntity p, LivingEntity t, double dist, float range) {
        int mode = clamp(aiTargetMode, 0, AI_TARGET_MODES.length - 1);
        double hpPart = 1.0 - t.getHealth() / Math.max(1f, t.getMaxHealth());
        double closePart = 1.0 - dist / Math.max(1f, range);
        if (mode == 1) return hpPart * 1000.0 + closePart * 50.0 - t.getArmor();
        if (mode == 2) return -dist;
        if (mode == 3) return (t.isBlocking() ? 1000.0 : 0.0) + hpPart * 200.0 + closePart * 50.0;
        if (mode == 4) return t.getArmor() * 25.0 + hpPart * 300.0 + closePart * 50.0;

        double yawPart = 1.0 - Math.abs(MathHelperWrapDegrees(p.getYaw() - rotations(p, t, eye(t))[0])) / 180.0;
        double base = weights[0] * closePart
            + weights[1] * hpPart
            + weights[2] * yawPart
            + weights[3] * Math.min(1.0, t.getArmor() / 20.0)
            + weights[4] * (t.isBlocking() ? 1.0 : 0.0)
            + weights[5] * hpPart
            + weights[6] * Math.min(1.0, (Math.abs(t.getVelocity().x) + Math.abs(t.getVelocity().z)) / 0.5)
            + weights[7] * 0.5;
        if (!aiUseLearning) return base * 100.0;

        double[] f = features(p, t);
        double model = sigmoid(dot(f, weights));
        aiConfidence = model;
        float influence = clamp(aiModelInfluence, 0f, 1f);
        return model * 100.0 * influence + base * 100.0 * (1.0 - influence);
    }

    private static Vec3d eye(LivingEntity e) {
        return new Vec3d(e.getX(), e.getY() + e.getHeight() * 0.5, e.getZ());
    }

    private static float MathHelperWrapDegrees(float v) {
        while (v > 180f) v -= 360f;
        while (v < -180f) v += 360f;
        return v;
    }

    private static float[] rotations(PlayerEntity p, Entity target, Vec3d pos) {
        double dx = pos.x - p.getX();
        double dy = pos.y - (p.getY() + p.getEyeHeight(p.getPose()));
        double dz = pos.z - p.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.max(0.0001, dist)));
        return new float[]{MathHelperWrapDegrees(yaw), Math.max(-90f, Math.min(90f, pitch))};
    }

    private static double[] features(PlayerEntity p, LivingEntity t) {
        double d = p.distanceTo(t);
        float yawDiff = Math.abs(MathHelperWrapDegrees(p.getYaw() - rotations(p, t, eye(t))[0]));
        float maxHp = Math.max(1f, t.getMaxHealth());
        double lowHp = 1.0 - t.getHealth() / maxHp;
        return new double[]{
            Math.max(0.0, 1.0 - d / Math.max(1f, aiRange)),
            lowHp,
            1.0 - yawDiff / 180.0,
            Math.min(1.0, t.getArmor() / 20.0),
            t.isBlocking() ? 1.0 : 0.0,
            lowHp,
            Math.min(1.0, (Math.abs(t.getVelocity().x) + Math.abs(t.getVelocity().z)) / 0.5),
            1.0
        };
    }

    private static void recordSample(PlayerEntity p, LivingEntity t, double label) {
        double[] f = features(p, t);
        double[] s = new double[f.length + 1];
        System.arraycopy(f, 0, s, 0, f.length);
        s[f.length] = label;
        training.add(s);
        samplesLearned++;
        int max = clamp(aiSamples, 100, 2000);
        while (training.size() > max) training.remove(0);
        if (!aiUseLearning) return;
        double pred = sigmoid(dot(f, weights));
        double lr = clamp(aiLearningRate, 0f, 2f) / Math.max(1, training.size());
        for (int i = 0; i < weights.length && i < f.length; i++) {
            weights[i] += lr * (label - pred) * f[i];
        }
        normalizeWeights();
    }

    private static void normalizeWeights() {
        double sum = 0;
        for (int i = 0; i < weights.length; i++) sum += Math.max(0.001, weights[i]);
        for (int i = 0; i < weights.length; i++) weights[i] = Math.max(0.001, weights[i]) / sum;
    }

    private static double dot(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length && i < b.length; i++) result += a[i] * b[i];
        return result;
    }

    private static double sigmoid(double x) {
        double v = Math.max(-12.0, Math.min(12.0, x));
        return 1.0 / (1.0 + Math.exp(-v));
    }

    /** Batch-retrains the live model on the recorded sample file. */
    public static void trainModel(int rounds) {
        if (training.isEmpty()) return;
        int r = clamp(rounds, 1, 2000);
        double lr = clamp(aiLearningRate, 0f, 2f) * 0.25;
        for (int round = 0; round < r; round++) {
            for (double[] s : training) {
                if (s.length < 2) continue;
                double[] f = new double[s.length - 1];
                System.arraycopy(s, 0, f, 0, f.length);
                double label = s[s.length - 1];
                double pred = sigmoid(dot(f, weights));
                for (int i = 0; i < weights.length && i < f.length; i++) {
                    weights[i] += lr * (label - pred) * f[i];
                }
            }
            normalizeWeights();
        }
        lastAutoTrain = System.currentTimeMillis();
    }

    public static void resetLearning() {
        weights = presetWeights.clone();
        training.clear();
        samplesLoaded = 0;
        samplesLearned = 0;
        aiConfidence = 0.0;
    }

    public static int sampleCount() {
        return samplesLoaded + samplesLearned;
    }

    public static void applyPreset(int preset) {
        switch (clamp(preset, 0, AI_PRESETS.length - 1)) {
            case 1 -> presetWeights = new double[]{0.48, 0.28, 0.10, 0.06, 0.08, 0.28, 0.03, 1.0};
            case 2 -> presetWeights = new double[]{0.18, 0.14, 0.56, 0.06, 0.06, 0.14, 0.02, 1.0};
            case 3 -> presetWeights = new double[]{0.30, 0.20, 0.12, 0.24, 0.14, 0.22, 0.06, 1.0};
            case 4 -> presetWeights = new double[]{0.34, 0.32, 0.18, 0.10, 0.06, 0.34, 0.04, 1.0};
            default -> presetWeights = new double[]{0.35, 0.25, 0.20, 0.10, 0.10, 0.25, 0.02, 1.0};
        }
        weights = presetWeights.clone();
        normalizeWeights();
        presetWeights = weights.clone();
    }

    public static void loadAiFile(File runDir) {
        File f = new File(runDir, safeName(aiTrainFile));
        training.clear();
        samplesLoaded = 0;
        if (!f.isFile()) return;
        int max = clamp(aiSamples, 100, 2000);
        try (BufferedReader r = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.trim().split(",");
                double[] s;
                if (parts.length >= 9) {
                    s = new double[9];
                } else if (parts.length == 6) {
                    // Legacy 5-feature samples from the earlier AI version.
                    s = new double[9];
                    String label = parts[5];
                    parts = new String[]{parts[0], parts[1], parts[2], parts[3], parts[4],
                        "0", "0", "1", label};
                } else {
                    continue;
                }
                boolean ok = true;
                int features = s.length - 1;
                for (int i = 0; i < features; i++) {
                    try { s[i] = Double.parseDouble(parts[i]); } catch (NumberFormatException ex) { ok = false; break; }
                }
                if (!ok) continue;
                try { s[features] = Double.parseDouble(parts[features]); } catch (NumberFormatException ex) { continue; }
                if (training.size() >= max) training.remove(0);
                training.add(s);
                samplesLoaded++;
            }
        } catch (IOException ignored) {
        }
    }

    private static double parseSafe(String value) {
        try { return Double.parseDouble(value.trim()); } catch (NumberFormatException ex) { return 0; }
    }

    public static void saveAiFile(File runDir) {
        File f = new File(runDir, safeName(aiTrainFile));
        try (FileWriter w = new FileWriter(f, false)) {
            for (double[] s : training) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < s.length; i++) {
                    if (i > 0) line.append(',');
                    line.append(String.format(Locale.ROOT, "%.5f", s[i]));
                }
                w.write(line.append('\n').toString());
            }
        } catch (IOException ignored) {
        }
    }

    private static String safeName(String name) {
        String s = name == null ? "" : name.trim();
        return s.isEmpty() ? "pypsen_ai.txt" : s.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    // ══════════ CRYSTAL AURA ══════════

    private static void tickCrystalAura(MinecraftClient mc, PlayerEntity p) {
        if (!crystalAuraOn) return;
        int mode = clamp(crystalMode, 0, CRYSTAL_MODES.length - 1);
        long now = System.currentTimeMillis();
        double range = Math.max(1f, crystalRange);

        List<EndCrystalEntity> crystals = new ArrayList<>();
        for (Entity e : mc.world.getOtherEntities(p, p.getBoundingBox().expand(range), entity -> true)) {
            if (e instanceof EndCrystalEntity crystal && p.distanceTo(crystal) <= range) crystals.add(crystal);
        }
        crystals.sort((a, b) -> Double.compare(crystalBreakScore(p, a), crystalBreakScore(p, b)));

        long cd = (long) Math.max(1L, Math.round(20L / Math.max(1f, crystalCps))) * 50L;
        if (!crystals.isEmpty() && now - lastCrystal >= cd) {
            EndCrystalEntity crystal = crystals.get(0);
            if (crystalRotate) {
                Vec3d crystalEye = new Vec3d(crystal.getX(), crystal.getY() + crystal.getHeight() * 0.5, crystal.getZ());
                float[] rot = rotations(p, crystal, crystalEye);
                p.setYaw(rot[0]);
                p.setPitch(rot[1]);
            }
            lastCrystal = now;
            mc.interactionManager.attackEntity(p, crystal);
            p.swingHand(Hand.MAIN_HAND);
            registerHit();
        }

        if (mode == 1) return;
        float placeDelay = crystalPlaceDelay;
        if (mode == 5) placeDelay *= 0.5f;
        if (now - lastCrystalPlace < Math.max(0f, placeDelay)) return;
        int maxCrystals = clamp(crystalMaxCrystals, 0, 8);
        if (maxCrystals <= 0 || crystals.size() >= maxCrystals) return;

        boolean holding = holdingCrystal(p);
        int slot = findCrystal(p);
        if (!crystalAutoSwitch && !holding) return;
        if (slot >= 0 && (crystalAutoSwitch || !holding)) selectSlot(p, slot);
        if (!holding && slot < 0) return;

        LivingEntity enemy = findCrystalTarget(mc, p, range);
        if (enemy == null) return;
        float hpRatio = enemy.getHealth() / Math.max(1f, enemy.getMaxHealth());
        if (mode == 3 && hpRatio > Math.max(0f, crystalTotemHp) && enemy.getAbsorptionAmount() <= 0f) return;
        if (mode == 4 && !(enemy.getArmor() <= crystalFaceArmor || enemy.getHealth() <= crystalFaceHp)) return;
        if (crystalMinHp > 0f && hpRatio > crystalMinHp && mode != 3 && mode != 4 && mode != 5) return;

        Vec3d placePos = crystalPosition(p, enemy);
        if (!canPlaceSafely(p, placePos, mode)) return;

        float[] rot = rotations(p, enemy, placePos);
        p.setYaw(rot[0]);
        p.setPitch(rot[1]);
        if (crystalOffHand) {
            mc.interactionManager.interactItem(p, Hand.OFF_HAND);
            p.swingHand(Hand.OFF_HAND);
        } else {
            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            p.swingHand(Hand.MAIN_HAND);
        }
        lastCrystalPlace = now;
    }

    private static double crystalBreakScore(PlayerEntity p, EndCrystalEntity crystal) {
        int mode = clamp(crystalTargetMode, 0, CRYSTAL_TARGET_MODES.length - 1);
        double d = p.distanceTo(crystal);
        if (mode == 2) return d + (crystal.isAlive() ? 0.0 : 1000.0);
        if (mode == 3) return d * 0.75;
        if (mode == 4) return d * 0.5;
        return d;
    }

    private static LivingEntity findCrystalTarget(MinecraftClient mc, PlayerEntity p, double range) {
        LivingEntity best = null;
        double bestScore = -1e9;
        int mode = clamp(crystalTargetMode, 0, CRYSTAL_TARGET_MODES.length - 1);
        double searchRange = Math.max(range, crystalPlaceRange);
        for (Entity e : mc.world.getOtherEntities(p, p.getBoundingBox().expand(searchRange), entity -> true)) {
            if (!(e instanceof LivingEntity living) || e == p || !living.isAlive() || e.isSpectator()) continue;
            if (PypsenClient.friends.contains(living.getName().getString())) continue;
            if (crystalOnlyPlayers && !(living instanceof PlayerEntity)) continue;
            double d = p.distanceTo(living);
            if (d > searchRange) continue;
            double hp = 1.0 - living.getHealth() / Math.max(1f, living.getMaxHealth());
            double close = 1.0 - d / Math.max(1f, searchRange);
            double score;
            switch (mode) {
                case 1 -> score = close * 1000.0;
                case 2 -> score = hp * 1000.0 + close * 50.0;
                case 3 -> score = (living.isBlocking() ? 1000.0 : 0.0) + hp * 300.0 + close * 50.0;
                case 4 -> score = living.getArmor() * 40.0 + hp * 300.0 + close * 50.0;
                default -> score = hp * 500.0 + close * 250.0 - living.getAbsorptionAmount() * 5.0;
            }
            if (score > bestScore) { bestScore = score; best = living; }
        }
        return best;
    }

    private static Vec3d crystalPosition(PlayerEntity p, LivingEntity enemy) {
        int mode = clamp(crystalPlaceMode, 0, CRYSTAL_PLACE_MODES.length - 1);
        double ticks = Math.max(0f, crystalPredict);
        double x = enemy.getX() + enemy.getVelocity().x * ticks;
        double y = enemy.getY() + 0.2;
        double z = enemy.getZ() + enemy.getVelocity().z * ticks;
        if (mode == 1) {
            y = enemy.getY() + enemy.getHeight();
        } else if (mode == 2) {
            x += 0.5;
            z += 0.5;
            y += 0.3;
        } else if (mode == 3) {
            double dx = p.getX() - x;
            double dz = p.getZ() - z;
            double len = Math.max(0.001, Math.sqrt(dx * dx + dz * dz));
            x -= dx / len * 0.35;
            z -= dz / len * 0.35;
            y = enemy.getY() + enemy.getHeight() * 0.5;
        }
        return new Vec3d(x, y, z);
    }

    private static boolean canPlaceSafely(PlayerEntity p, Vec3d pos, int mode) {
        if (mode == 3 || mode == 4 || mode == 5) return true;
        if (!crystalSafety && mode != 2 && mode != 6) return true;
        double dx = pos.x - p.getX();
        double dy = pos.y - p.getY();
        double dz = pos.z - p.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        float factor = Math.max(0f, 1f - (float) dist / 8f);
        float selfDamage = 12f * factor * factor;
        return p.getHealth() + p.getAbsorptionAmount() - selfDamage >= Math.max(0f, crystalSelfDmg);
    }

    private static boolean holdingCrystal(PlayerEntity p) {
        ItemStack main = p.getMainHandStack();
        ItemStack off = p.getOffHandStack();
        return (!main.isEmpty() && main.getItem() == net.minecraft.item.Items.END_CRYSTAL)
            || (!off.isEmpty() && off.getItem() == net.minecraft.item.Items.END_CRYSTAL);
    }

    private static int findCrystal(PlayerEntity p) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (!s.isEmpty() && s.getItem() == net.minecraft.item.Items.END_CRYSTAL) return i;
        }
        return -1;
    }

    // ══════════ AUTO EAT / FOOD ══════════

    private static void tickAutoEat(MinecraftClient mc, PlayerEntity p) {
        if (!autoEatOn || mc.currentScreen != null) return;
        float hp = p.getHealth() + p.getAbsorptionAmount();
        int food = p.getHungerManager().getFoodLevel();
        if (hp > autoEatHealth && food > autoEatHunger) return;
        if (System.currentTimeMillis() - lastEat < 350L) return;

        int slot = findFood(p);
        if (slot < 0) return;
        selectSlot(p, slot);
        lastEat = System.currentTimeMillis();
        mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
        p.swingHand(Hand.MAIN_HAND);
    }

    private static int findFood(PlayerEntity p) {
        int best = -1;
        float bestScore = -1f;
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            String path = Registries.ITEM.getId(s.getItem()).getPath();
            float score = foodScore(path);
            if (score > bestScore) { bestScore = score; best = i; }
        }
        return best;
    }

    private static float foodScore(String path) {
        if (path.contains("golden_apple") || path.contains("enchanted_golden_apple")) return 5f;
        if (path.contains("apple")) return 3f;
        if (path.contains("stew") || path.contains("soup")) return 2.5f;
        if (path.contains("bread") || path.contains("cake") || path.contains("cookie")) return 2f;
        if (path.contains("meat") || path.contains("fish") || path.contains("chicken")) return 1.8f;
        if (path.contains("potato") || path.contains("carrot") || path.contains("melon")) return 1.2f;
        return -1f;
    }

    private static void selectSlot(PlayerEntity p, int slot) {
        if (slot < 0 || slot > 8) return;
        try {
            var inv = p.getInventory();
            try {
                inv.getClass().getField("selectedSlot").setInt(inv, slot);
                return;
            } catch (NoSuchFieldException ignored) {
            }
            inv.getClass().getMethod("setSelectedSlot", int.class).invoke(inv, slot);
        } catch (Exception ignored) {
        }
    }

    // ══════════ FAKE LAG ══════════

    /** Called by ClientConnectionMixin. Returns true when the packet was queued. */
    public static boolean onSendPacket(Packet<?> packet) {
        if (flushing || !fakeLagOn) return false;
        if (PypsenClient.freecamOn || (PypsenClient.freezeOn && PypsenClient.freezeMode == 1)) return false;
        int mode = clamp(fakeLagMode, 0, FAKELAG_MODES.length - 1);
        boolean hold = mode == 1 || packet instanceof PlayerMoveC2SPacket;
        if (!hold) return false;
        packets.add(packet);
        int max = mode == 2 ? 6 : 24;
        if (packets.size() > max) flushPackets();
        return true;
    }

    private static void handleFakeLag(MinecraftClient mc) {
        if (!fakeLagOn || packets.isEmpty()) return;
        long delay = (long) Math.max(20L, fakeLagDelay);
        if (System.currentTimeMillis() - lastFlush >= delay) flushPackets();
    }

    public static void flushPackets() {
        if (packets.isEmpty() || flushing) return;
        flushing = true;
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getNetworkHandler() != null) {
                for (Packet<?> packet : packets) mc.getNetworkHandler().sendPacket(packet);
            }
            packets.clear();
        } finally {
            flushing = false;
        }
        lastFlush = System.currentTimeMillis();
    }

    // ══════════ SPIDER / NOCLIP PRO ══════════

    private static void tickSpiderPro(PlayerEntity p) {
        if (!spiderProOn || !p.horizontalCollision) return;
        int mode = clamp(spiderProMode, 0, SPIDER_PRO_MODES.length - 1);
        Vec3d v = p.getVelocity();
        switch (mode) {
            case 1 -> {
                double bx = Math.signum(v.x) * 0.06;
                double bz = Math.signum(v.z) * 0.06;
                p.setPosition(p.getX() + bx, p.getY(), p.getZ() + bz);
                p.setVelocity(v.x, spiderBoost, v.z);
            }
            case 2 -> {
                p.setVelocity(v.x, p.age % 4 == 0 ? spiderBoost * 1.5 : spiderBoost * 0.6, v.z);
                if (p.age % 6 == 0) p.setPosition(p.getX(), p.getY() + 0.03, p.getZ());
            }
            case 3 -> p.setVelocity(v.x * 1.08, spiderBoost * 1.35, v.z * 1.08);
            default -> p.setVelocity(v.x, spiderBoost, v.z);
        }
        p.fallDistance = 0f;
    }

    private static void tickNoclipPro(PlayerEntity p) {
        if (!noclipProOn) return;
        p.noClip = true;
        p.fallDistance = 0f;
        int mode = clamp(noclipProMode, 0, NOCLIP_PRO_MODES.length - 1);
        if (mode == 1 && p.age % 3 == 0) {
            Vec3d dir = p.getRotationVec(1f).multiply(noclipStep);
            p.setPosition(p.getX() + dir.x, p.getY() + dir.y, p.getZ() + dir.z);
        } else if (mode == 2 && p.age % 2 == 0) {
            p.setPosition(p.getX(), p.getY() + noclipStep, p.getZ());
        } else if (mode == 3) {
            Vec3d v = p.getVelocity();
            p.setVelocity(v.x * 0.98, Math.max(v.y, -0.02), v.z * 0.98);
        }
    }

    private static void tickHudRainbow(MinecraftClient mc) {
        if (!hudRainbow) return;
        // Colour is computed from wall-clock time in render(); nothing else needed.
    }

    // ══════════ KEY HANDLING ══════════

    public static void handleKey(long handle) {
        if (wasPressed(handle, keyAiAura)) aiAuraOn = !aiAuraOn;
        if (wasPressed(handle, keyCrystalAura)) crystalAuraOn = !crystalAuraOn;
        if (wasPressed(handle, keyFakeLag)) fakeLagOn = !fakeLagOn;
        if (wasPressed(handle, keyAutoEat)) autoEatOn = !autoEatOn;
        if (wasPressed(handle, keyNoclipPro)) noclipProOn = !noclipProOn;
        if (wasPressed(handle, keySpiderPro)) spiderProOn = !spiderProOn;
        if (wasPressed(handle, keyVisualsPro)) advancedVisualsOn = !advancedVisualsOn;
        if (wasPressed(handle, keyFx)) fxOn = !fxOn;
        if (wasPressed(handle, keyStealth)) {
            stealthOn = !stealthOn;
            if (stealthOn) applyStealthMode(stealthMode);
        }
    }

    private static boolean wasPressed(long handle, int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return false;
        boolean down = GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
        if (down && pressedFeatureKeys.add(key)) return true;
        if (!down) pressedFeatureKeys.remove(key);
        return false;
    }

    public static void panic() {
        aiAuraOn = false;
        crystalAuraOn = false;
        fakeLagOn = false;
        autoEatOn = false;
        noclipProOn = false;
        spiderProOn = false;
        aiTarget = null;
        flushPackets();
    }

    // ══════════ CONFIG ══════════

    public static void save(FileWriter w) throws IOException {
        w.write("aiAuraOn=" + aiAuraOn + "\n");
        w.write("crystalAuraOn=" + crystalAuraOn + "\n");
        w.write("fakeLagOn=" + fakeLagOn + "\n");
        w.write("autoEatOn=" + autoEatOn + "\n");
        w.write("noclipProOn=" + noclipProOn + "\n");
        w.write("spiderProOn=" + spiderProOn + "\n");
        w.write("advancedVisualsOn=" + advancedVisualsOn + "\n");
        w.write("nameTagDetailsOn=" + nameTagDetailsOn + "\n");
        w.write("hudRainbow=" + hudRainbow + "\n");
        w.write("aiUseLearning=" + aiUseLearning + "\n");
        w.write("aiAutoTrain=" + aiAutoTrain + "\n");
        w.write("aiAutoSave=" + aiAutoSave + "\n");
        w.write("aiFocusTarget=" + aiFocusTarget + "\n");
        w.write("aiOnlyPlayers=" + aiOnlyPlayers + "\n");
        w.write("crystalSafety=" + crystalSafety + "\n");
        w.write("crystalAutoSwitch=" + crystalAutoSwitch + "\n");
        w.write("crystalRotate=" + crystalRotate + "\n");
        w.write("crystalOffHand=" + crystalOffHand + "\n");
        w.write("crystalOnlyPlayers=" + crystalOnlyPlayers + "\n");
        w.write("aiPreset=" + aiPreset + "\n");
        w.write("aiTargetMode=" + aiTargetMode + "\n");
        w.write("aiRotationMode=" + aiRotationMode + "\n");
        w.write("crystalMode=" + crystalMode + "\n");
        w.write("crystalTargetMode=" + crystalTargetMode + "\n");
        w.write("crystalPlaceMode=" + crystalPlaceMode + "\n");
        w.write("fakeLagMode=" + fakeLagMode + "\n");
        w.write("spiderProMode=" + spiderProMode + "\n");
        w.write("noclipProMode=" + noclipProMode + "\n");
        w.write("hudStyle=" + hudStyle + "\n");
        w.write("aiSamples=" + aiSamples + "\n");
        w.write("aiTrainRounds=" + aiTrainRounds + "\n");
        w.write("aiLearningRate=" + aiLearningRate + "\n");
        w.write("aiModelInfluence=" + aiModelInfluence + "\n");
        w.write("aiSmooth=" + aiSmooth + "\n");
        w.write("aiJitter=" + aiJitter + "\n");
        w.write("aiTimingJitter=" + aiTimingJitter + "\n");
        w.write("aiSwitchDelay=" + aiSwitchDelay + "\n");
        w.write("aiRange=" + aiRange + "\n");
        w.write("aiCps=" + aiCps + "\n");
        w.write("crystalRange=" + crystalRange + "\n");
        w.write("crystalCps=" + crystalCps + "\n");
        w.write("crystalPlaceRange=" + crystalPlaceRange + "\n");
        w.write("crystalPlaceDelay=" + crystalPlaceDelay + "\n");
        w.write("crystalPredict=" + crystalPredict + "\n");
        w.write("crystalMinHp=" + crystalMinHp + "\n");
        w.write("crystalSelfDmg=" + crystalSelfDmg + "\n");
        w.write("crystalTotemHp=" + crystalTotemHp + "\n");
        w.write("crystalFaceArmor=" + crystalFaceArmor + "\n");
        w.write("crystalFaceHp=" + crystalFaceHp + "\n");
        w.write("crystalMaxCrystals=" + crystalMaxCrystals + "\n");
        w.write("fakeLagDelay=" + fakeLagDelay + "\n");
        w.write("autoEatHealth=" + autoEatHealth + "\n");
        w.write("autoEatHunger=" + autoEatHunger + "\n");
        w.write("spiderBoost=" + spiderBoost + "\n");
        w.write("noclipStep=" + noclipStep + "\n");
        w.write("aiTrainFile=" + aiTrainFile + "\n");
        w.write("keyAiAura=" + keyAiAura + "\n");
        w.write("keyCrystalAura=" + keyCrystalAura + "\n");
        w.write("keyFakeLag=" + keyFakeLag + "\n");
        w.write("keyAutoEat=" + keyAutoEat + "\n");
        w.write("keyNoclipPro=" + keyNoclipPro + "\n");
        w.write("keySpiderPro=" + keySpiderPro + "\n");
        w.write("keyVisualsPro=" + keyVisualsPro + "\n");
        w.write("fxOn=" + fxOn + "\n");
        w.write("hitMarkerOn=" + hitMarkerOn + "\n");
        w.write("damageFlashOn=" + damageFlashOn + "\n");
        w.write("targetTrailOn=" + targetTrailOn + "\n");
        w.write("comboCounterOn=" + comboCounterOn + "\n");
        w.write("killFeedOn=" + killFeedOn + "\n");
        w.write("skeletonEspOn=" + skeletonEspOn + "\n");
        w.write("fxScanlinesOn=" + fxScanlinesOn + "\n");
        w.write("stealthOn=" + stealthOn + "\n");
        w.write("stealthHideHud=" + stealthHideHud + "\n");
        w.write("stealthNeutralLabels=" + stealthNeutralLabels + "\n");
        w.write("stealthRandomDelays=" + stealthRandomDelays + "\n");
        w.write("stealthRotatingName=" + stealthRotatingName + "\n");
        w.write("crosshairStyle=" + crosshairStyle + "\n");
        w.write("fxTheme=" + fxTheme + "\n");
        w.write("hitMarkerStyle=" + hitMarkerStyle + "\n");
        w.write("stealthMode=" + stealthMode + "\n");
        w.write("hitMarkerTime=" + hitMarkerTime + "\n");
        w.write("damageFlashAlpha=" + damageFlashAlpha + "\n");
        w.write("comboReset=" + comboReset + "\n");
        w.write("skeletonRange=" + skeletonRange + "\n");
        w.write("fxThickness=" + fxThickness + "\n");
        w.write("keyFx=" + keyFx + "\n");
        w.write("keyStealth=" + keyStealth + "\n");
        if (aiAutoSave && runDir != null) saveAiFile(runDir);
    }

    public static void load(String key, String val) {
        try {
            switch (key) {
                case "aiAuraOn" -> aiAuraOn = Boolean.parseBoolean(val);
                case "crystalAuraOn" -> crystalAuraOn = Boolean.parseBoolean(val);
                case "fakeLagOn" -> fakeLagOn = Boolean.parseBoolean(val);
                case "autoEatOn" -> autoEatOn = Boolean.parseBoolean(val);
                case "noclipProOn" -> noclipProOn = Boolean.parseBoolean(val);
                case "spiderProOn" -> spiderProOn = Boolean.parseBoolean(val);
                case "advancedVisualsOn" -> advancedVisualsOn = Boolean.parseBoolean(val);
                case "nameTagDetailsOn" -> nameTagDetailsOn = Boolean.parseBoolean(val);
                case "hudRainbow" -> hudRainbow = Boolean.parseBoolean(val);
                case "aiUseLearning" -> aiUseLearning = Boolean.parseBoolean(val);
                case "aiAutoTrain" -> aiAutoTrain = Boolean.parseBoolean(val);
                case "aiAutoSave" -> aiAutoSave = Boolean.parseBoolean(val);
                case "aiFocusTarget" -> aiFocusTarget = Boolean.parseBoolean(val);
                case "aiOnlyPlayers" -> aiOnlyPlayers = Boolean.parseBoolean(val);
                case "crystalSafety" -> crystalSafety = Boolean.parseBoolean(val);
                case "crystalAutoSwitch" -> crystalAutoSwitch = Boolean.parseBoolean(val);
                case "crystalRotate" -> crystalRotate = Boolean.parseBoolean(val);
                case "crystalOffHand" -> crystalOffHand = Boolean.parseBoolean(val);
                case "crystalOnlyPlayers" -> crystalOnlyPlayers = Boolean.parseBoolean(val);
                case "aiPreset" -> aiPreset = Integer.parseInt(val);
                case "aiTargetMode" -> aiTargetMode = Integer.parseInt(val);
                case "aiRotationMode" -> aiRotationMode = Integer.parseInt(val);
                case "crystalMode" -> crystalMode = Integer.parseInt(val);
                case "crystalTargetMode" -> crystalTargetMode = Integer.parseInt(val);
                case "crystalPlaceMode" -> crystalPlaceMode = Integer.parseInt(val);
                case "fakeLagMode" -> fakeLagMode = Integer.parseInt(val);
                case "spiderProMode" -> spiderProMode = Integer.parseInt(val);
                case "noclipProMode" -> noclipProMode = Integer.parseInt(val);
                case "hudStyle" -> hudStyle = Integer.parseInt(val);
                case "aiSamples" -> aiSamples = Integer.parseInt(val);
                case "aiTrainRounds" -> aiTrainRounds = Integer.parseInt(val);
                case "aiLearningRate" -> aiLearningRate = Float.parseFloat(val);
                case "aiModelInfluence" -> aiModelInfluence = Float.parseFloat(val);
                case "aiSmooth" -> aiSmooth = Float.parseFloat(val);
                case "aiJitter" -> aiJitter = Float.parseFloat(val);
                case "aiTimingJitter" -> aiTimingJitter = Float.parseFloat(val);
                case "aiSwitchDelay" -> aiSwitchDelay = Float.parseFloat(val);
                case "aiRange" -> aiRange = Float.parseFloat(val);
                case "aiCps" -> aiCps = Float.parseFloat(val);
                case "crystalRange" -> crystalRange = Float.parseFloat(val);
                case "crystalCps" -> crystalCps = Float.parseFloat(val);
                case "crystalPlaceRange" -> crystalPlaceRange = Float.parseFloat(val);
                case "crystalPlaceDelay" -> crystalPlaceDelay = Float.parseFloat(val);
                case "crystalPredict" -> crystalPredict = Float.parseFloat(val);
                case "crystalMinHp" -> crystalMinHp = Float.parseFloat(val);
                case "crystalSelfDmg" -> crystalSelfDmg = Float.parseFloat(val);
                case "crystalTotemHp" -> crystalTotemHp = Float.parseFloat(val);
                case "crystalFaceArmor" -> crystalFaceArmor = Float.parseFloat(val);
                case "crystalFaceHp" -> crystalFaceHp = Float.parseFloat(val);
                case "crystalMaxCrystals" -> crystalMaxCrystals = Integer.parseInt(val);
                case "fakeLagDelay" -> fakeLagDelay = Float.parseFloat(val);
                case "autoEatHealth" -> autoEatHealth = Float.parseFloat(val);
                case "autoEatHunger" -> autoEatHunger = Float.parseFloat(val);
                case "spiderBoost" -> spiderBoost = Float.parseFloat(val);
                case "noclipStep" -> noclipStep = Float.parseFloat(val);
                case "aiTrainFile" -> aiTrainFile = val;
                case "keyAiAura" -> keyAiAura = Integer.parseInt(val);
                case "keyCrystalAura" -> keyCrystalAura = Integer.parseInt(val);
                case "keyFakeLag" -> keyFakeLag = Integer.parseInt(val);
                case "keyAutoEat" -> keyAutoEat = Integer.parseInt(val);
                case "keyNoclipPro" -> keyNoclipPro = Integer.parseInt(val);
                case "keySpiderPro" -> keySpiderPro = Integer.parseInt(val);
                case "keyVisualsPro" -> keyVisualsPro = Integer.parseInt(val);
                case "fxOn" -> fxOn = Boolean.parseBoolean(val);
                case "hitMarkerOn" -> hitMarkerOn = Boolean.parseBoolean(val);
                case "damageFlashOn" -> damageFlashOn = Boolean.parseBoolean(val);
                case "targetTrailOn" -> targetTrailOn = Boolean.parseBoolean(val);
                case "comboCounterOn" -> comboCounterOn = Boolean.parseBoolean(val);
                case "killFeedOn" -> killFeedOn = Boolean.parseBoolean(val);
                case "skeletonEspOn" -> skeletonEspOn = Boolean.parseBoolean(val);
                case "fxScanlinesOn" -> fxScanlinesOn = Boolean.parseBoolean(val);
                case "stealthOn" -> stealthOn = Boolean.parseBoolean(val);
                case "stealthHideHud" -> stealthHideHud = Boolean.parseBoolean(val);
                case "stealthNeutralLabels" -> stealthNeutralLabels = Boolean.parseBoolean(val);
                case "stealthRandomDelays" -> stealthRandomDelays = Boolean.parseBoolean(val);
                case "stealthRotatingName" -> stealthRotatingName = Boolean.parseBoolean(val);
                case "crosshairStyle" -> crosshairStyle = Integer.parseInt(val);
                case "fxTheme" -> fxTheme = Integer.parseInt(val);
                case "hitMarkerStyle" -> hitMarkerStyle = Integer.parseInt(val);
                case "stealthMode" -> stealthMode = Integer.parseInt(val);
                case "hitMarkerTime" -> hitMarkerTime = Float.parseFloat(val);
                case "damageFlashAlpha" -> damageFlashAlpha = Float.parseFloat(val);
                case "comboReset" -> comboReset = Float.parseFloat(val);
                case "skeletonRange" -> skeletonRange = Float.parseFloat(val);
                case "fxThickness" -> fxThickness = Integer.parseInt(val);
                case "keyFx" -> keyFx = Integer.parseInt(val);
                case "keyStealth" -> keyStealth = Integer.parseInt(val);
                default -> { }
            }
        } catch (NumberFormatException ignored) {
        }
    }

    public static void clamp() {
        aiPreset = clamp(aiPreset, 0, AI_PRESETS.length - 1);
        aiTargetMode = clamp(aiTargetMode, 0, AI_TARGET_MODES.length - 1);
        aiRotationMode = clamp(aiRotationMode, 0, AI_ROTATION_MODES.length - 1);
        crystalMode = clamp(crystalMode, 0, CRYSTAL_MODES.length - 1);
        crystalTargetMode = clamp(crystalTargetMode, 0, CRYSTAL_TARGET_MODES.length - 1);
        crystalPlaceMode = clamp(crystalPlaceMode, 0, CRYSTAL_PLACE_MODES.length - 1);
        fakeLagMode = clamp(fakeLagMode, 0, FAKELAG_MODES.length - 1);
        spiderProMode = clamp(spiderProMode, 0, SPIDER_PRO_MODES.length - 1);
        noclipProMode = clamp(noclipProMode, 0, NOCLIP_PRO_MODES.length - 1);
        hudStyle = clamp(hudStyle, 0, HUD_STYLES.length - 1);
        crosshairStyle = clamp(crosshairStyle, 0, CROSSHAIR_STYLES.length - 1);
        fxTheme = clamp(fxTheme, 0, FX_THEMES.length - 1);
        hitMarkerStyle = clamp(hitMarkerStyle, 0, HITMARKER_STYLES.length - 1);
        stealthMode = clamp(stealthMode, 0, STEALTH_MODES.length - 1);
        aiSamples = clamp(aiSamples, 100, 2000);
        aiTrainRounds = clamp(aiTrainRounds, 1, 2000);
        aiLearningRate = clamp(aiLearningRate, 0f, 2f);
        aiModelInfluence = clamp(aiModelInfluence, 0f, 1f);
        aiSmooth = clamp(aiSmooth, 0f, 1f);
        aiJitter = clamp(aiJitter, 0f, 15f);
        aiTimingJitter = clamp(aiTimingJitter, 0f, 500f);
        aiSwitchDelay = clamp(aiSwitchDelay, 0f, 2000f);
        aiRange = clamp(aiRange, 2f, 16f);
        aiCps = clamp(aiCps, 1f, 20f);
        crystalRange = clamp(crystalRange, 2f, 12f);
        crystalCps = clamp(crystalCps, 1f, 20f);
        crystalPlaceRange = clamp(crystalPlaceRange, 1f, 12f);
        crystalPlaceDelay = clamp(crystalPlaceDelay, 0f, 2000f);
        crystalPredict = clamp(crystalPredict, 0f, 10f);
        crystalMinHp = clamp(crystalMinHp, 0f, 1f);
        crystalSelfDmg = clamp(crystalSelfDmg, 0f, 20f);
        crystalTotemHp = clamp(crystalTotemHp, 0f, 1f);
        crystalFaceArmor = clamp(crystalFaceArmor, 0f, 20f);
        crystalFaceHp = clamp(crystalFaceHp, 0f, 20f);
        crystalMaxCrystals = clamp(crystalMaxCrystals, 0, 8);
        fakeLagDelay = clamp(fakeLagDelay, 20f, 1000f);
        autoEatHealth = clamp(autoEatHealth, 1f, 20f);
        autoEatHunger = clamp(autoEatHunger, 0f, 20f);
        spiderBoost = clamp(spiderBoost, 0.02f, 0.5f);
        noclipStep = clamp(noclipStep, 0.05f, 2f);
        hitMarkerTime = clamp(hitMarkerTime, 40f, 800f);
        damageFlashAlpha = clamp(damageFlashAlpha, 0f, 1f);
        comboReset = clamp(comboReset, 500f, 10000f);
        skeletonRange = clamp(skeletonRange, 8f, 128f);
        fxThickness = clamp(fxThickness, 1, 4);
    }

    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

    // ══════════ ADVANCED SETTINGS SCREEN ══════════

    public static class FeaturesScreen extends Screen {
        private final Screen parent;
        private int binding = -1;
        private int page = 0;
        private int y = 8;
        private int scroll = 0;
        private int contentHeight = 0;
        private static final int PAGE_COUNT = 8;

        public FeaturesScreen(Screen parent) {
            super(Text.literal("Pypsen Advanced Features"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int w = Math.min(300, Math.max(120, width - 16));
            int x = Math.max(8, Math.min(width / 2 - 150, width - w - 8));
            y = 8 - scroll;
            nav(x, y, w);

            switch (page) {
                case 0 -> {
                    toggle(x, y, w, "AI KillAura", aiAuraOn, v -> aiAuraOn = v);
                    cycle(x, y, w, "AI Preset", AI_PRESETS, aiPreset, v -> { aiPreset = v; applyPreset(v); });
                    cycle(x, y, w, "AI Target", AI_TARGET_MODES, aiTargetMode, v -> aiTargetMode = v);
                    cycle(x, y, w, "AI Rotation", AI_ROTATION_MODES, aiRotationMode, v -> aiRotationMode = v);
                    plusMinus(x, y, w, "AI Samples", aiSamples, 100, 2000, 100, v -> aiSamples = v);
                    plusMinusF(x, y, w, "AI Range", aiRange, 2f, 16f, 1f, v -> aiRange = v);
                    plusMinusF(x, y, w, "AI CPS", aiCps, 1f, 20f, 1f, v -> aiCps = v);
                    toggle(x, y, w, "Focus Target", aiFocusTarget, v -> aiFocusTarget = v);
                    toggle(x, y, w, "Players Only", aiOnlyPlayers, v -> aiOnlyPlayers = v);
                    bind(x, y, w, "AI Key", keyAiAura, 0);
                    addDrawable(button(x, y, w, "Load AI File", b -> loadAiFile(MinecraftClient.getInstance().runDirectory)));
                    addDrawable(button(x, y, w, "Save AI File", b -> saveAiFile(MinecraftClient.getInstance().runDirectory)));
                }
                case 1 -> {
                    toggle(x, y, w, "Use Learning", aiUseLearning, v -> aiUseLearning = v);
                    toggle(x, y, w, "Auto Train", aiAutoTrain, v -> aiAutoTrain = v);
                    toggle(x, y, w, "Auto Save AI", aiAutoSave, v -> aiAutoSave = v);
                    plusMinusF(x, y, w, "Influence", aiModelInfluence, 0f, 1f, 0.05f, v -> aiModelInfluence = v);
                    plusMinusF(x, y, w, "Learn Rate", aiLearningRate, 0f, 2f, 0.05f, v -> aiLearningRate = v);
                    plusMinus(x, y, w, "Train Rounds", aiTrainRounds, 1, 2000, 10, v -> aiTrainRounds = v);
                    plusMinusF(x, y, w, "Smooth", aiSmooth, 0f, 1f, 0.05f, v -> aiSmooth = v);
                    plusMinusF(x, y, w, "Rot Jitter", aiJitter, 0f, 15f, 0.5f, v -> aiJitter = v);
                    plusMinusF(x, y, w, "Hit Jitter ms", aiTimingJitter, 0f, 500f, 10f, v -> aiTimingJitter = v);
                    plusMinusF(x, y, w, "Switch ms", aiSwitchDelay, 0f, 2000f, 50f, v -> aiSwitchDelay = v);
                    addDrawable(button(x, y, w, "Train 100 Hits", b -> trainModel(100)));
                    addDrawable(button(x, y, w, "Train 500 Hits", b -> trainModel(500)));
                    addDrawable(button(x, y, w, "Train 2000 Hits", b -> trainModel(2000)));
                    addDrawable(button(x, y, w, "Reset Learning", b -> resetLearning()));
                }
                case 2 -> {
                    toggle(x, y, w, "Crystal Aura", crystalAuraOn, v -> crystalAuraOn = v);
                    cycle(x, y, w, "Mode", CRYSTAL_MODES, crystalMode, v -> crystalMode = v);
                    cycle(x, y, w, "Target", CRYSTAL_TARGET_MODES, crystalTargetMode, v -> crystalTargetMode = v);
                    cycle(x, y, w, "Place Pos", CRYSTAL_PLACE_MODES, crystalPlaceMode, v -> crystalPlaceMode = v);
                    plusMinusF(x, y, w, "Range", crystalRange, 2f, 12f, 1f, v -> crystalRange = v);
                    plusMinusF(x, y, w, "CPS", crystalCps, 1f, 20f, 1f, v -> crystalCps = v);
                    plusMinusF(x, y, w, "Place Range", crystalPlaceRange, 1f, 12f, 1f, v -> crystalPlaceRange = v);
                    plusMinusF(x, y, w, "Place Delay", crystalPlaceDelay, 0f, 2000f, 20f, v -> crystalPlaceDelay = v);
                    plusMinus(x, y, w, "Max Crystals", crystalMaxCrystals, 0, 8, 1, v -> crystalMaxCrystals = v);
                    toggle(x, y, w, "Auto Switch", crystalAutoSwitch, v -> crystalAutoSwitch = v);
                    toggle(x, y, w, "Rotate", crystalRotate, v -> crystalRotate = v);
                    bind(x, y, w, "Crystal Key", keyCrystalAura, 1);
                }
                case 3 -> {
                    toggle(x, y, w, "Safety Check", crystalSafety, v -> crystalSafety = v);
                    toggle(x, y, w, "Off Hand", crystalOffHand, v -> crystalOffHand = v);
                    toggle(x, y, w, "Players Only", crystalOnlyPlayers, v -> crystalOnlyPlayers = v);
                    plusMinusF(x, y, w, "Predict", crystalPredict, 0f, 10f, 0.5f, v -> crystalPredict = v);
                    plusMinusF(x, y, w, "Min HP %", crystalMinHp, 0f, 1f, 0.05f, v -> crystalMinHp = v);
                    plusMinusF(x, y, w, "Keep HP", crystalSelfDmg, 0f, 20f, 1f, v -> crystalSelfDmg = v);
                    plusMinusF(x, y, w, "Totem HP %", crystalTotemHp, 0f, 1f, 0.05f, v -> crystalTotemHp = v);
                    plusMinusF(x, y, w, "Face Armor", crystalFaceArmor, 0f, 20f, 1f, v -> crystalFaceArmor = v);
                    plusMinusF(x, y, w, "Face HP", crystalFaceHp, 0f, 20f, 1f, v -> crystalFaceHp = v);
                }
                case 4 -> {
                    toggle(x, y, w, "Fake Lag", fakeLagOn, v -> fakeLagOn = v);
                    cycle(x, y, w, "Lag Mode", FAKELAG_MODES, fakeLagMode, v -> fakeLagMode = v);
                    plusMinusF(x, y, w, "Lag Delay ms", fakeLagDelay, 20f, 1000f, 20f, v -> fakeLagDelay = v);
                    bind(x, y, w, "Lag Key", keyFakeLag, 2);
                    toggle(x, y, w, "Auto Eat", autoEatOn, v -> autoEatOn = v);
                    plusMinusF(x, y, w, "Eat at HP", autoEatHealth, 1f, 20f, 1f, v -> autoEatHealth = v);
                    plusMinusF(x, y, w, "Eat at Hunger", autoEatHunger, 0f, 20f, 1f, v -> autoEatHunger = v);
                    bind(x, y, w, "Eat Key", keyAutoEat, 3);
                    toggle(x, y, w, "Spider Pro", spiderProOn, v -> spiderProOn = v);
                    cycle(x, y, w, "Spider Mode", SPIDER_PRO_MODES, spiderProMode, v -> spiderProMode = v);
                    plusMinusF(x, y, w, "Boost", spiderBoost, 0.02f, 0.5f, 0.02f, v -> spiderBoost = v);
                    bind(x, y, w, "Spider Key", keySpiderPro, 4);
                    toggle(x, y, w, "Noclip Pro", noclipProOn, v -> noclipProOn = v);
                    cycle(x, y, w, "Noclip Mode", NOCLIP_PRO_MODES, noclipProMode, v -> noclipProMode = v);
                    plusMinusF(x, y, w, "Step", noclipStep, 0.05f, 2f, 0.05f, v -> noclipStep = v);
                    bind(x, y, w, "Noclip Key", keyNoclipPro, 5);
                }
                case 5 -> {
                    toggle(x, y, w, "Advanced HUD", advancedVisualsOn, v -> advancedVisualsOn = v);
                    toggle(x, y, w, "Tag Details", nameTagDetailsOn, v -> nameTagDetailsOn = v);
                    toggle(x, y, w, "Rainbow HUD", hudRainbow, v -> hudRainbow = v);
                    cycle(x, y, w, "HUD Style", HUD_STYLES, hudStyle, v -> hudStyle = v);
                    bind(x, y, w, "Visuals Key", keyVisualsPro, 6);
                }
                case 6 -> {
                    toggle(x, y, w, "Visual FX", fxOn, v -> fxOn = v);
                    toggle(x, y, w, "Hit Marker", hitMarkerOn, v -> hitMarkerOn = v);
                    cycle(x, y, w, "Marker Style", HITMARKER_STYLES, hitMarkerStyle, v -> hitMarkerStyle = v);
                    toggle(x, y, w, "Damage Flash", damageFlashOn, v -> damageFlashOn = v);
                    toggle(x, y, w, "Target Trail", targetTrailOn, v -> targetTrailOn = v);
                    toggle(x, y, w, "Combo Counter", comboCounterOn, v -> comboCounterOn = v);
                    toggle(x, y, w, "Kill Feed", killFeedOn, v -> killFeedOn = v);
                    toggle(x, y, w, "Skeleton ESP", skeletonEspOn, v -> skeletonEspOn = v);
                    toggle(x, y, w, "Scanlines", fxScanlinesOn, v -> fxScanlinesOn = v);
                    cycle(x, y, w, "Crosshair", CROSSHAIR_STYLES, crosshairStyle, v -> crosshairStyle = v);
                    cycle(x, y, w, "Theme", FX_THEMES, fxTheme, v -> fxTheme = v);
                    bind(x, y, w, "FX Key", keyFx, 7);
                }
                default -> {
                    plusMinusF(x, y, w, "Marker ms", hitMarkerTime, 40f, 800f, 20f, v -> hitMarkerTime = v);
                    plusMinusF(x, y, w, "Flash Alpha", damageFlashAlpha, 0f, 1f, 0.05f, v -> damageFlashAlpha = v);
                    plusMinusF(x, y, w, "Combo ms", comboReset, 500f, 10000f, 250f, v -> comboReset = v);
                    plusMinusF(x, y, w, "Skeleton m", skeletonRange, 8f, 128f, 4f, v -> skeletonRange = v);
                    plusMinus(x, y, w, "Thickness", fxThickness, 1, 4, 1, v -> fxThickness = v);
                    toggle(x, y, w, "Stealth", stealthOn, v -> stealthOn = v);
                    cycle(x, y, w, "Stealth Mode", STEALTH_MODES, stealthMode, v -> { stealthMode = v; applyStealthMode(v); });
                    toggle(x, y, w, "Hide HUD", stealthHideHud, v -> stealthHideHud = v);
                    toggle(x, y, w, "Neutral Labels", stealthNeutralLabels, v -> stealthNeutralLabels = v);
                    toggle(x, y, w, "Random Delays", stealthRandomDelays, v -> stealthRandomDelays = v);
                    toggle(x, y, w, "Rotating Name", stealthRotatingName, v -> stealthRotatingName = v);
                    bind(x, y, w, "Stealth Key", keyStealth, 8);
                }
            }
            contentHeight = y + scroll;
        }

        private void nav(int x, int y, int w) {
            addDrawableChild(button(x, y, w / 2 - 2, "\u00a7b<<< Prev / " + (page + 1) + "/" + PAGE_COUNT + " / Next >>>",
                b -> { page = (page + 1) % PAGE_COUNT; scroll = 0; rebuild(); }));
            addDrawableChild(button(x + w / 2 + 2, y, w / 2 - 2, "\u00a7aSave Config", b -> {
                PypsenClient.saveConfig();
                rebuild();
            }));
            this.y += 22;
            addDrawableChild(button(x, y, w / 2 - 2, "\u00a7cBack", b -> MinecraftClient.getInstance().setScreen(parent)));
            addDrawableChild(button(x + w / 2 + 2, y, w / 2 - 2, "\u00a75Classic Menu", b -> {
                MinecraftClient.getInstance().setScreen(new PypsenClient.MenuScreen());
            }));
            this.y += 22;
        }

        @Override
        public boolean mouseScrolled(double mx, double my, double amount) {
            if (binding >= 0) return true;
            scroll += (int) (amount * 22);
            if (scroll > 0) scroll = 0;
            int minScroll = Math.min(0, height - 10 - contentHeight);
            if (scroll < minScroll) scroll = minScroll;
            rebuild();
            return true;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            super.render(ctx, mx, my, d);
            int x = Math.max(2, Math.min(width - 6, width / 2 + 152));
            ctx.fill(x, 8, x + 3, height - 8, 0x44000000);
            if (contentHeight > height - 16) {
                int track = height - 16;
                int thumb = Math.max(18, track * track / Math.max(1, contentHeight));
                int minScroll = Math.min(0, height - 10 - contentHeight);
                float t = minScroll == 0 ? 0f : (float) scroll / minScroll;
                int top = 8 + (int) (t * (track - thumb));
                ctx.fill(x, top, x + 3, top + thumb, 0xFF55FF55);
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (binding >= 0 && keyCode != GLFW.GLFW_KEY_ESCAPE) {
                switch (binding) {
                    case 0 -> keyAiAura = keyCode;
                    case 1 -> keyCrystalAura = keyCode;
                    case 2 -> keyFakeLag = keyCode;
                    case 3 -> keyAutoEat = keyCode;
                    case 4 -> keySpiderPro = keyCode;
                    case 5 -> keyNoclipPro = keyCode;
                    case 6 -> keyVisualsPro = keyCode;
                    case 7 -> keyFx = keyCode;
                    case 8 -> keyStealth = keyCode;
                    default -> { }
                }
                binding = -1;
                rebuild();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private ButtonWidget button(int x, int y, int w, String label, java.util.function.Consumer<ButtonWidget> onClick) {
            return ButtonWidget.builder(Text.literal(label), onClick::accept).dimensions(x, y, w, 20).build();
        }

        private void addAt(int wy, ButtonWidget b) {
            if (wy >= 4 && wy + 20 <= height - 4) addDrawableChild(b);
        }

        private void addDrawable(ButtonWidget b) { addAt(y, b); y += 22; }

        private void toggle(int x, int y, int w, String label, boolean on, java.util.function.Consumer<Boolean> setter) {
            addDrawable(button(x, y, w, label + ": " + (on ? "\u00a7aON" : "\u00a7cOFF"),
                b -> { setter.accept(!on); rebuild(); }));
        }

        private void cycle(int x, int y, int w, String label, String[] options, int current, java.util.function.IntConsumer setter) {
            addDrawable(button(x, y, w, label + ": \u00a7b" + options[clamp(current, 0, options.length - 1)],
                b -> { setter.accept((current + 1) % options.length); rebuild(); }));
        }

        private void bind(int x, int y, int w, String label, int key, int id) {
            addDrawable(button(x, y, w, label + ": \u00a7e" + (binding == id ? "[...]" : keyLabel(key)),
                b -> { binding = id; rebuild(); }));
        }

        private String keyLabel(int key) {
            if (key == GLFW.GLFW_KEY_UNKNOWN) return "-";
            String name = GLFW.glfwGetKeyName(key, 0);
            return name == null || name.isEmpty() ? String.valueOf(key) : name.toUpperCase(Locale.ROOT);
        }

        private void plusMinus(int x, int y, int w, String label, int val, int min, int max, int step, java.util.function.IntConsumer setter) {
            int bw = w / 2 - 2;
            addAt(y, ButtonWidget.builder(Text.literal("\u00a7c- " + label + " \u00a7f" + val),
                b -> { setter.accept(clamp(val - step, min, max)); rebuild(); }).dimensions(x, y, bw, 20).build());
            addAt(y, ButtonWidget.builder(Text.literal("\u00a7a+"),
                b -> { setter.accept(clamp(val + step, min, max)); rebuild(); }).dimensions(x + bw + 4, y, bw, 20).build());
            this.y += 22;
        }

        private void plusMinusF(int x, int y, int w, String label, float val, float min, float max, float step, java.util.function.Consumer<Float> setter) {
            int bw = w / 2 - 2;
            String txt = String.format(Locale.ROOT, "%s %.2f", label, val);
            addAt(y, ButtonWidget.builder(Text.literal("\u00a7c- " + txt),
                b -> { setter.accept(clamp(val - step, min, max)); rebuild(); }).dimensions(x, y, bw, 20).build());
            addAt(y, ButtonWidget.builder(Text.literal("\u00a7a+"),
                b -> { setter.accept(clamp(val + step, min, max)); rebuild(); }).dimensions(x + bw + 4, y, bw, 20).build());
            this.y += 22;
        }

        private void rebuild() {
            MinecraftClient.getInstance().setScreen(this);
        }

        @Override
        public void close() {
            MinecraftClient.getInstance().setScreen(parent);
        }
    }
}
