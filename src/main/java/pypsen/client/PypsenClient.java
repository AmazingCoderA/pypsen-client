package pypsen.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;

public class PypsenClient implements ClientModInitializer {

    // ══════════ TOGGLES ══════════
    public static boolean speedOn = false, auraOn = false, hitboxOn = false, flyOn = false;
    public static boolean xrayOn = false, velocityOn = false, aimOn = false, visualsOn = false;
    public static boolean strafeOn = false, noclipOn = false, antiFlagOn = false, autoClickerOn = false;
    public static boolean hudOn = true, triggerBotOn = false, targetHudOn = true, panicOn = false;
    public static boolean freecamOn = false, playerEspOn = false, auraSpinBot = false;
    public static boolean triggerWeaponOnly = false, autoTotemOn = false, aimLockOn = false;
    public static boolean antiBotOn = true, fullBrightOn = false, nameHiderOn = false;
    public static boolean spiderOn = false, noWebOn = false, freezeOn = false, useModernGui = false;
    public static boolean serverCrasherOn = false, godModeOn = false, autoArmorOn = false, fastBowOn = false;
    public static boolean bowJump = false, auraPredict = true, auraRandomize = true, dropAllItems = false;
    public static boolean jesusOn = false;

    // ══════════ VISUALS SUB-TOGGLES ══════════
    public static boolean seeInvisOn = false, noFireOn = false, noNauseaOn = false;
    public static boolean targetEspOn = false, noHurtCamOn = false;

    // ══════════ 2D ESP OVERLAY ══════════
    public static boolean espOn = false, espBoxesOn = true, espNamesOn = true, espHealthOn = true;
    public static boolean espDistanceOn = true, espItemsOn = false, espTracersOn = false, espFillOn = false;
    public static boolean espPlayersOn = true, espMobsOn = true, espAnimalsOn = false;
    public static float espRange = 64f;

    // ══════════ KILLAURA ADVANCED ══════════
    public static boolean auraJump = false, auraThroughWalls = true, auraHitInGuis = true;
    public static boolean autoWeapon = false, criticals = false, shieldBreaker = false, waterCrits = false;
    public static boolean auraRayCast = true;
    public static boolean auraTargetPlayers = true, auraTargetMobs = true, auraTargetAnimals = false;

    // ── KillAura custom layer (built-in presets stay untouched) ──
    public static boolean auraNotNear = false, auraRotationEnabled = true, auraStrafeEnabled = false;
    public static float auraNotNearRange = 2.5f;
    public static final String[] AURA_PRESETS = {"Funtime", "ReallyWorld", "Test", "HvH", "Custom"};
    public static boolean auraCustomSaved = false, auraCustomRotation = true, auraCustomStrafe = false;
    public static boolean auraCustomNotNear = false;
    public static int auraCustomMode = 0, auraCustomRotMode = 0, auraCustomBypass = 0;
    public static float auraCustomRange = 6f, auraCustomFov = 360f, auraCustomRotSpeed = 180f;
    public static float auraCustomNotNearRange = 2.5f;

    // ══════════ AIM ASSIST ══════════
    //Several real rotation methods instead of a single hard snap.
    public static boolean aimTargetPlayers = true, aimTargetMobs = true, aimTargetAnimals = false;
    public static float aimRange = 15f, aimFov = 120f, aimJitter = 1f;
    public static int aimMode = 0, aimTargetMode = 0;
    public static final String[] AIM_MODES = {"Smooth", "Linear", "Snap", "Spiral", "Jitter", "Silent"};

    // ══════════ TRIGGERBOT ADVANCED ══════════
    public static boolean triggerThroughWalls = false, triggerSmartDelay = true;
    public static boolean triggerTargetPlayers = true, triggerTargetMobs = false, triggerTargetAnimals = false;
    public static boolean triggerAutoWeapon = false, triggerCriticals = false, triggerWaitHurtTime = true;
    public static float triggerCooldown = 0.95f, triggerJitter = 10f, triggerFov = 30f;

    // ══════════ VALUES ══════════
    public static float speedVal = 5f, auraRange = 6f, hitboxVal = 1f, flySpeed = 2f;
    public static float aimSpeed = 50f, clickerCps = 12f, triggerCps = 10f;
    public static float auraAimJitter = 0.0f, triggerRange = 3.0f;
    public static float auraSmoothing = 0f;
    public static float auraStickiness = 0.0f, auraRotCurve = 0.0f, auraFov = 360f, auraRotSpeed = 180f;
    public static float autoTotemHealth = 20f;
    public static String fakeName = "Protected";

    // ══════════ MODES ══════════
    public static int auraMode = 0, auraTargetMode = 0, auraRotMode = 0;
    public static int auraTimingMode = 0, auraTargetPart = 1, visualsTargetMode = 0;
    public static int autoTotemMode = 0;
    public static int spiderMode = 0, noWebMode = 0, freezeMode = 0, crasherMode = 0, crasherTimerMode = 0;
    public static boolean airJumpOn = false, noFallOn = false;
    public static int airJumpMode = 0, noFallMode = 0, velocityMode = 0, jesusMode = 0, visualsParticles = 0;
    public static float airJumpSpeed = 1f, spiderSpeed = 1f;

    // ── Custom ambient / visual enhancements ──
    public static boolean ambientOn = false, ambientRainbow = false, spinOn = false;
    public static int ambientMode = 0, spinMode = 0;
    public static float ambientStrength = 0.35f, ambientHue = 200f;
    public static float spinSpeed = 1f, spinSize = 40f;
    public static final String[] AMBIENT_MODES = {"Tint", "Fog", "Vignette", "Gradient", "Spiral"};
    public static final String[] SPIN_MODES = {"Spin", "Reverse", "Pulse", "Helix", "Orbit"};

    // ── Optimization / convenience ──
    public static boolean optimizationOn = false, hitboxFix = false;
    public static int optProfile = 1;
    public static final String[] OPT_PROFILES = {"Eco", "Balanced", "Performance"};

    // ── XRay extras ──
    public static int xrayStealth = 0;
    public static boolean xrayHighlight = false;
    public static final String[] XRAY_STEALTH_MODES = {"Off", "Quiet", "Ghost"};

    // ── Local benchmark / creative utilities ──
    public static boolean freezeVisual = false, crasherVisual = false;
    public static boolean creativeDropOn = false, creativeDupeOn = false;
    public static int creativeDropCount = 1, creativeDupeCount = 1, dupeMode = 0;
    public static final String[] DUPE_MODES = {"Inventory Clone", "Stack Split", "Armor Clone", "Selected Item", "Held Stack Split", "Hotbar Copy"};
    public static double crasherScore = 0.0;
    public static final String[] AIRJUMP_MODES = {"Vanilla", "GhostBlock", "Packet", "Real Blocks", "Silent Real"};
    public static final String[] NOFALL_MODES = {"Packet", "WaterBucket", "Web", "Vulcan", "Grim", "LiquidBounce", "Matrix", "NCP", "Matrix Plus"};
    public static final String[] VELOCITY_MODES = {"Cancel", "Matrix", "Grim", "LiquidBounce", "AAC", "Hyper"};
    public static final String[] JESUS_MODES = {"Solid", "Bounce", "LiquidBounce", "Matrix", "Ghost"};
    public static final String[] PARTICLES_MODES = {"None", "Heart", "Flame", "Magic", "LiquidBounce"};
    public static final String[] AURA_MODES = {"1.9 (Strict Cooldown)", "1.8 (Spam)", "Adaptive"};
    public static final String[] AURA_TARGETS = {"Distance", "Health", "Armor", "Angle"};
    public static final String[] AURA_ROTS = {"None", "Snap", "Smooth", "Silent", "Matrix", "Predict", "Smart", "Legit", "HvH", "Derp", "Cinematic", "NCP", "AAC", "Sunrise", "Vulcan", "Intave", "Legit Smooth", "Looping", "LiquidBounce", "Custom", "Noise"};
    public static final String[] AURA_TIMINGS = {"All", "Fall (Crits)", "Jump (Crits)"};
    public static final String[] AURA_PARTS = {"Head", "Chest", "Legs", "Feet"};
    public static final String[] VISUALS_TARGETS = {"All", "Enemies", "Friends"};
    public static final String[] TOTEM_MODES = {"Instant", "Legit"};
    public static final String[] SPIDER_MODES = {"Vanilla", "Jump", "Grim", "GhostBlock", "Bypass Plus", "Turbo Plus", "Turbo Max", "Silent Climb",
            "Quantum", "Nebula", "Zenith", "Aero", "Pulse", "Drift", "Phantom", "Orbit", "Tunnel", "Void", "Nova", "Aurora", "Hyper", "Silent V2", "Perk"};
    public static final String[] NOWEB_MODES = {"Vanilla", "Fast", "Grim", "Intave", "EdgeTP"};
    public static final String[] FREEZE_MODES = {"Vanilla", "Packet", "Intave", "GhostBlock", "LiquidBounce", "Smooth Lock", "Silent Lock", "Hard Lock"};
    // Local benchmark/dry-run only: these modes never send packets to a server.
    public static final String[] CRASHER_MODES = {"Position Bench", "Swing Bench", "Interact Bench", "Math Benchmark", "Brand Bench", "Settings Bench", "Tab Bench"};
    public static final String[] CRASHER_TIMERS = {"Infinite", "5s", "10s", "Auto-Disable"};
    public static final String[] GODMODE_MODES = {"Vanilla", "Packet", "Vulcan", "LiquidBounce", "Hyper", "Silent"};
    public static int godModeMode = 0;

    public static int speedBypass = 0, auraBypass = 0, flyBypass = 0, noclipBypass = 0;
    public static final String[] BYPASS_NAMES = {"None", "Vanilla", "Timer", "Strafe", "NCP", "Matrix", "Grim", "Funtime", "ReallyWorld", "Vulcan", "Spartan", "Grim AC", "Polar AC", "Intave", "Hyper", "NCP+", "Grim+", "Intave+", "Vulcan+", "Matrix+", "Polar+",
            "Quantum", "Nebula", "Zenith", "Aero", "Pulse", "Drift", "Skywalker", "Phantom", "Silent Sky", "Turbo Sky", "Orbit", "Glide", "Matrix V2", "AAC+", "Spartan+", "Polar V2", "Intave V2", "Grim V2", "Hyper V2", "Vanilla Air", "Smooth Fly", "Step Fly", "Guard", "Nova", "Aurora"};
    public static final String[] NOCLIP_MODES = {"Vanilla", "VClip", "HClip", "Packet", "Phantom Step", "Ghost VClip", "Perk", "Side Step", "Sky Lift", "Silent Axis", "Smart Glide", "Matrix Steps", "Vulcan Glide", "Intave Drift", "Polar Hop", "Hyper Step", "No Fall Down", "Soft Slide", "Quantum", "Nebula", "Zenith", "Aero", "Pulse", "Drift", "Phantom", "Orbit", "Tunnel", "Ghost Lift", "Void Step", "Rapid V", "Nova", "Aurora"};
    public static final String[] NOCLIP_DIRECTIONS = {"Smart", "Up", "Down", "Forward", "Back", "Left", "Right", "Jump Up", "Sneak Down"};
    public static float noclipStep = 0.5f;
    public static int noclipDirection = 0;

    // ── Editable bypass list / account switcher ──
    public static final List<String> customBypassNames = new ArrayList<>();
    public static float flyCustomVertical = -0.01f, flyCustomPulse = 0f, spiderCustomPulse = 0f;
    public static final List<String> accountNames = new ArrayList<>();
    public static int selectedAccount = 0;
    public static String accountStatus = "";

    // ══════════ KEYBINDS ══════════
    public static int keyMenu = GLFW.GLFW_KEY_MINUS, keySpeed = GLFW.GLFW_KEY_V, keyAura = GLFW.GLFW_KEY_R;
    public static int keyFly = GLFW.GLFW_KEY_G, keyXray = GLFW.GLFW_KEY_X, keyHitbox = GLFW.GLFW_KEY_H;
    public static int keyVelocity = GLFW.GLFW_KEY_UNKNOWN, keyAim = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyVisuals = GLFW.GLFW_KEY_UNKNOWN, keyStrafe = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyNoclip = GLFW.GLFW_KEY_UNKNOWN, keyAntiFlag = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyAutoClicker = GLFW.GLFW_KEY_UNKNOWN, keyTriggerBot = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyPanic = GLFW.GLFW_KEY_UNKNOWN, keyFreecam = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyAutoTotem = GLFW.GLFW_KEY_UNKNOWN, keyAimLock = GLFW.GLFW_KEY_UNKNOWN;
    public static boolean autoClickerRight = false, autoClickerFarm = false;
    public static int keySpider = GLFW.GLFW_KEY_UNKNOWN, keyNoWeb = GLFW.GLFW_KEY_UNKNOWN, keyFreeze = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyCrasher = GLFW.GLFW_KEY_UNKNOWN, keyGodMode = GLFW.GLFW_KEY_UNKNOWN, keyAutoArmor = GLFW.GLFW_KEY_UNKNOWN, keyFastBow = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyAirJump = GLFW.GLFW_KEY_UNKNOWN, keyNoFall = GLFW.GLFW_KEY_UNKNOWN, keyJesus = GLFW.GLFW_KEY_UNKNOWN;
    public static int keyEsp = GLFW.GLFW_KEY_UNKNOWN;

    public static final Set<String> friends = new HashSet<>();
    private static boolean middleClickWasDown = false;
    private static boolean wasFullBright = false;
    private static Vec3d freecamPos = null;
    private static float freecamYaw = 0, freecamPitch = 0;
    private static int totemTimer = 0, totemStep = 0, totemTargetSlot = -1;
    private static long lastTimeUpdate = 0;
    private static long crasherStartTime = 0;
    private static float currentTps = 20.0f;

    private static final Block[] DEFAULT_XRAY_BLOCKS = {
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.ANCIENT_DEBRIS, Blocks.NETHER_GOLD_ORE, Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.SPAWNER
    };
    public static final Set<Block> XRAY_BLOCKS = new HashSet<>(Arrays.asList(DEFAULT_XRAY_BLOCKS));

    // ══════════ XRAY / BASE FINDER ══════════
    public static int xrayMode = 0, xrayBypass = 0;
    public static float xrayRange = 96f;
    public static boolean xrayFarScan = false, xrayAntiXray = false;
    public static final String[] XRAY_MODES = {"Selected", "Containers", "Ores", "Deep Scan"};
    public static final String[] XRAY_BYPASS_MODES = {"Normal", "Extra Faces", "All Faces"};

    public static boolean baseFinderOn = false, baseFinderChest = true, baseFinderShulker = true;
    public static boolean baseFinderWood = true, baseFinderCarpet = true, baseFinderUnusual = true;
    public static int baseFinderMode = 0, baseFinderRender = 0;
    public static float baseFinderRange = 128f;
    public static final String[] BASE_FINDER_MODES = {"Balanced", "Containers", "Wood/Caves", "Carpet", "Unusual"};
    public static final String[] BASE_FINDER_RENDERS = {"Points", "Boxes", "Glow", "Heatmap"};

    private static int clientTick = 0, scanCursor = 0;
    private static final Map<Long, ChunkScan> chunkScans = new LinkedHashMap<>();
    private static final List<net.minecraft.util.math.BlockPos> xrayMarks = new ArrayList<>();

    private static int auraTick = 0, clickTimer = 0, triggerTimer = 0;
    private static float triggerFloatTimer = 0f;
    private static boolean xrayWasOn = false, visualsWasOn = false;
    private static LivingEntity currentTarget = null;

    private static final class ChunkScan {
        int cx, cz;
        int xray, chest, shulker, wood, carpet, unusual;

        int score() {
            return (baseFinderChest ? chest * 3 : 0)
                    + (baseFinderShulker ? shulker * 5 : 0)
                    + (baseFinderWood ? Math.min(wood, 40) : 0)
                    + (baseFinderCarpet ? carpet * 2 : 0)
                    + (baseFinderUnusual ? unusual * 4 : 0);
        }
    }

    public static LivingEntity getAuraTarget() {
        return currentTarget;
    }

    // Smooth rotation targets
    private static float targetYaw = 0f;
    private static float targetPitch = 0f;
    private static boolean isRotating = false;

    @Override
    public void onInitializeClient() {
        loadConfig();
        PypsenFeatures.clamp();
        PypsenFeatures.init(MinecraftClient.getInstance());

        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || panicOn) return;

            // ── CUSTOM AMBIENT / FON ──
            if (ambientOn) drawAmbient(ctx, mc);
            if (spinOn) drawSpinEffects(ctx, mc);

            // ── MAIN HUD ──
            if (hudOn && PypsenFeatures.hudVisible()) {
                List<String> lines = new ArrayList<>();
                lines.add("\u00a7a\u00a7lPypsen v2 \u00a77[PRO]");
                if (speedOn)       lines.add("\u00a7bSpeed \u00a77" + String.format("%.1f", speedVal) + " [" + BYPASS_NAMES[MathHelper.clamp(speedBypass, 0, BYPASS_NAMES.length - 1)] + "]");
                if (auraOn)        lines.add("\u00a7cKillAura \u00a77" + String.format("%.1f", auraRange) + " [" + BYPASS_NAMES[MathHelper.clamp(auraBypass, 0, BYPASS_NAMES.length - 1)] + "]"
                        + (auraNotNear ? " \u00a7eNOT NEAR " + String.format("%.1f", auraNotNearRange) + "m" : ""));
                if (hitboxOn)      lines.add("\u00a7eHitbox \u00a77" + String.format("%.1f", hitboxVal));
                if (flyOn)         lines.add("\u00a7dFly \u00a77" + String.format("%.1f", flySpeed) + " [" + BYPASS_NAMES[MathHelper.clamp(flyBypass, 0, BYPASS_NAMES.length - 1)] + "]");
                if (xrayOn && xrayStealth == 0) lines.add("\u00a76XRay \u00a77[" + XRAY_MODES[MathHelper.clamp(xrayMode, 0, XRAY_MODES.length - 1)] + "]"
                        + (xrayFarScan ? " \u00a7e" + String.format("%.0fm", xrayRange) : ""));
                if (baseFinderOn)  lines.add("\u00a7aBase Finder \u00a77" + BASE_FINDER_MODES[MathHelper.clamp(baseFinderMode, 0, BASE_FINDER_MODES.length - 1)] + " \u00a7e" + String.format("%.0fm", baseFinderRange));
                if (velocityOn)    lines.add("\u00a79Velocity");
                if (aimOn)         lines.add("\u00a75AimAssist \u00a77" + String.format("%.0fm", aimRange) + " [" + AIM_MODES[MathHelper.clamp(aimMode, 0, AIM_MODES.length - 1)] + "]");
                if (visualsOn)     lines.add("\u00a7aVisuals");
                if (ambientOn)     lines.add("\u00a7dAmbient \u00a77" + AMBIENT_MODES[MathHelper.clamp(ambientMode, 0, AMBIENT_MODES.length - 1)]
                        + " \u00a7e" + String.format("%.0f%%", ambientStrength * 100f));
                if (spinOn)        lines.add("\u00a75Spins \u00a77" + SPIN_MODES[MathHelper.clamp(spinMode, 0, SPIN_MODES.length - 1)]
                        + " \u00a7e" + String.format("%.1f", spinSpeed));
                if (optimizationOn) lines.add("\u00a7aOptimization \u00a77" + OPT_PROFILES[MathHelper.clamp(optProfile, 0, OPT_PROFILES.length - 1)]);
                if (strafeOn)      lines.add("\u00a73Strafe");
                if (noclipOn)      lines.add("\u00a78Noclip");
                if (antiFlagOn)    lines.add("\u00a7eAnti-Flag");
                if (freecamOn)     lines.add("\u00a7bFreeCam");
                if (autoTotemOn)   lines.add("\u00a76AutoTotem");
                if (aimLockOn)     lines.add("\u00a75AimLock");
                if (antiBotOn)     lines.add("\u00a7aAntiBot");
                if (spiderOn)      lines.add("\u00a72Spider");
                if (noWebOn)       lines.add("\u00a7fNoWeb");
                if (freezeOn)      lines.add("\u00a7bFreeze");
                if (airJumpOn)     lines.add("\u00a7bAirJump");
                if (noFallOn)      lines.add("\u00a7aNoFall");
                if (jesusOn)       lines.add("\u00a7bJesus");
                if (serverCrasherOn)lines.add("\u00a7cServer Crasher \u00a77[LOCAL BENCH] \u00a7e" + String.format("%.2f", crasherScore));
                if (godModeOn)     lines.add("\u00a7eGodMode");
                if (autoArmorOn)   lines.add("\u00a7bAutoArmor");
                if (fastBowOn)     lines.add("\u00a7aFastBow");
                if (fullBrightOn)  lines.add("\u00a7eFullBright");
                if (nameHiderOn)   lines.add("\u00a7dNameHider");
                if (autoClickerOn) lines.add("\u00a7cAutoClicker \u00a77" + String.format("%.1f", clickerCps) + (autoClickerRight ? " [RMB]" : " [LMB]"));
                if (triggerBotOn)  lines.add("\u00a7cTriggerBot \u00a77" + String.format("%.1f", triggerCps) + " \u00a78[" + String.format("%.0f", triggerJitter) + "% jit]");
                if (espOn)         lines.add("\u00a7b2D ESP \u00a77" + String.format("%.0fm", espRange));
                if (PypsenFeatures.aiAuraOn) lines.add("\u00a75AI Aura \u00a77" + String.format("%.0fm", PypsenFeatures.aiRange)
                    + " [" + PypsenFeatures.AI_PRESETS[MathHelper.clamp(PypsenFeatures.aiPreset, 0, PypsenFeatures.AI_PRESETS.length - 1)]
                    + "/" + PypsenFeatures.AI_TARGET_MODES[MathHelper.clamp(PypsenFeatures.aiTargetMode, 0, PypsenFeatures.AI_TARGET_MODES.length - 1)]
                    + "] \u00a78" + String.format("%.0f%%", PypsenFeatures.aiConfidence * 100f)
                    + " \u00a77s" + PypsenFeatures.sampleCount());
                if (PypsenFeatures.crystalAuraOn) lines.add("\u00a7cCrystal Aura \u00a77" + String.format("%.0fm", PypsenFeatures.crystalRange)
                    + " [" + PypsenFeatures.CRYSTAL_MODES[MathHelper.clamp(PypsenFeatures.crystalMode, 0, PypsenFeatures.CRYSTAL_MODES.length - 1)]
                    + "/" + PypsenFeatures.CRYSTAL_PLACE_MODES[MathHelper.clamp(PypsenFeatures.crystalPlaceMode, 0, PypsenFeatures.CRYSTAL_PLACE_MODES.length - 1)] + "]");
                if (PypsenFeatures.fakeLagOn) lines.add("\u00a78FakeLag \u00a77" + String.format("%.0fms", PypsenFeatures.fakeLagDelay));
                if (PypsenFeatures.autoEatOn) lines.add("\u00a7aAutoEat \u00a77" + String.format("%.0fHP", PypsenFeatures.autoEatHealth));
                if (PypsenFeatures.spiderProOn) lines.add("\u00a72Spider Pro");
                if (PypsenFeatures.noclipProOn) lines.add("\u00a78Noclip Pro");
                if (PypsenFeatures.stealthOn) lines.add("\u00a78Stealth \u00a77" + PypsenFeatures.STEALTH_MODES[MathHelper.clamp(PypsenFeatures.stealthMode, 0, PypsenFeatures.STEALTH_MODES.length - 1)]);
                if (PypsenFeatures.comboCounterOn && PypsenFeatures.comboCount() > 1) lines.add("\u00a7eCombo x" + PypsenFeatures.comboCount());
                if (PypsenFeatures.killFeedOn && PypsenFeatures.killCount() > 0) lines.add("\u00a7cKills \u00a77" + PypsenFeatures.killCount());

                // TPS & Ping
                lines.add("");
                lines.add("§7TPS: §f" + String.format("%.1f", currentTps));
                if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
                    lines.add("§7Ping: §f" + mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() + "ms");
                }

                int y = 4;
                for (String s : lines) {
                    ctx.drawTextWithShadow(mc.textRenderer, Text.literal(s), 4, y, 0xFFFFFF);
                    y += 11;
                }
            }

            // ── TARGET HUD ──
            if (targetHudOn && PypsenFeatures.hudVisible() && currentTarget != null && currentTarget.isAlive() && mc.player.distanceTo(currentTarget) <= auraRange * 1.5) {
                int sw = mc.getWindow().getScaledWidth();
                int sh = mc.getWindow().getScaledHeight();
                int tx = sw / 2 + 20;
                int ty = sh / 2 + 20;

                ctx.fill(tx, ty, tx + 140, ty + 50, 0x90000000); // Background
                ctx.fill(tx, ty, tx + 140, ty + 2, 0xFF55FF55);  // Top border

                String name = PypsenFeatures.label(currentTarget.getName().getString(), "Player");
                float hp = currentTarget.getHealth();
                float maxHp = currentTarget.getMaxHealth();
                int armor = currentTarget.getArmor();
                float dist = mc.player.distanceTo(currentTarget);

                ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a7l" + name), tx + 5, ty + 6, 0xFFFFFF);
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a7c\u2764 " + String.format("%.1f", hp)), tx + 5, ty + 18, 0xFFFFFF);
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a7b\u25A0 " + armor), tx + 50, ty + 18, 0xFFFFFF);
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a77" + String.format("%.1fm", dist)), tx + 95, ty + 18, 0xFFFFFF);

                int hpBarWidth = (int) ((hp / maxHp) * 130);
                ctx.fill(tx + 5, ty + 35, tx + 5 + 130, ty + 42, 0xFF333333);
                ctx.fill(tx + 5, ty + 35, tx + 5 + hpBarWidth, ty + 42, getHealthColor(hp, maxHp));
            }

            // ── 2D ESP OVERLAY ──
            drawEsp(ctx, mc, tickDelta);

            // ── TARGET ESP 2D MARKER (independent of Glowing ESP) ──
            if (targetEspOn && currentTarget != null && currentTarget.isAlive()) {
                drawTargetMarker(ctx, mc);
            }

            // ── NEW OVERLAYS ──
            if (xrayOn && (xrayFarScan || xrayHighlight) && xrayStealth < 2) drawXrayRadar(ctx, mc);
            if (baseFinderOn) drawBaseFinderRadar(ctx, mc);
            if (freezeOn && freezeVisual) drawFreezeVisual(ctx, mc);
            if (serverCrasherOn && crasherVisual) drawCrasherVisual(ctx, mc);

            // ── ADVANCED TARGET VISUALS ──
            PypsenFeatures.render(ctx, mc);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.world == null) return;
            clientTick++;
            var p = mc.player;
            long handle = mc.getWindow().getHandle();

            // ── NAMEHIDER (TAB & SCOREBOARD) ──
            if (nameHiderOn && mc.getNetworkHandler() != null) {
                for (net.minecraft.client.network.PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                    if (entry.getProfile().getId().equals(p.getUuid())) {
                        entry.setDisplayName(Text.literal(fakeName));
                    }
                }
            }

            // ── TPS CALCULATION ──
            long now = System.currentTimeMillis();
            if (now - lastTimeUpdate > 1000) {
                if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(p.getUuid()) != null) {
                    int ping = mc.getNetworkHandler().getPlayerListEntry(p.getUuid()).getLatency();
                    // Simple approximation based on ping for visual effect if real TPS is unavailable
                    currentTps = 20.0f - (ping > 100 ? (ping / 1000.0f) : 0);
                    if (currentTps < 0) currentTps = 0;
                }
                lastTimeUpdate = now;
            }

            // ── PANIC KEY ──
            if (wasPressed(handle, keyPanic)) {
                panicOn = !panicOn;
                if (panicOn) {
                    speedOn = auraOn = hitboxOn = flyOn = xrayOn = velocityOn = aimOn = visualsOn = false;
                    strafeOn = noclipOn = antiFlagOn = autoClickerOn = triggerBotOn = freecamOn = playerEspOn = false;
                    autoTotemOn = aimLockOn = fullBrightOn = nameHiderOn = spiderOn = noWebOn = freezeOn = false;
                    jesusOn = false;
                    airJumpOn = noFallOn = serverCrasherOn = baseFinderOn = false;
                    ambientOn = spinOn = false;
                    creativeDropOn = creativeDupeOn = false;
                    PypsenFeatures.panic();
                    mc.worldRenderer.reload();
                }
            }

            if (panicOn) return;

            // ── FRIENDS (MIDDLE CLICK) ──
            boolean middleClickDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
            if (middleClickDown && !middleClickWasDown && mc.currentScreen == null) {
                if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    Entity e = ((EntityHitResult) mc.crosshairTarget).getEntity();
                    if (e instanceof net.minecraft.entity.player.PlayerEntity) {
                        String name = e.getName().getString();
                        if (friends.contains(name)) {
                            friends.remove(name);
                            p.sendMessage(Text.literal("\u00a7cRemoved friend: " + name), true);
                        } else {
                            friends.add(name);
                            p.sendMessage(Text.literal("\u00a7aAdded friend: " + name), true);
                        }
                    }
                }
            }
            middleClickWasDown = middleClickDown;

            // ── KEYBINDS ──
            if (mc.currentScreen == null) {
                if (wasPressed(handle, keyMenu)) mc.setScreen(useModernGui ? new ModernGuiScreen() : new MenuScreen());
                if (wasPressed(handle, keySpeed))       speedOn       = !speedOn;
                if (wasPressed(handle, keyAura))        auraOn        = !auraOn;
                if (wasPressed(handle, keyFly))         flyOn         = !flyOn;
                if (wasPressed(handle, keyHitbox))      hitboxOn      = !hitboxOn;
                if (wasPressed(handle, keyVelocity))    velocityOn    = !velocityOn;
                if (wasPressed(handle, keyAim))         aimOn         = !aimOn;
                if (wasPressed(handle, keyStrafe))      strafeOn      = !strafeOn;
                if (wasPressed(handle, keyVisuals))     visualsOn     = !visualsOn;
                if (wasPressed(handle, keyNoclip))      noclipOn      = !noclipOn;
                if (wasPressed(handle, keyAntiFlag))    antiFlagOn    = !antiFlagOn;
                if (wasPressed(handle, keyAutoClicker)) autoClickerOn = !autoClickerOn;
                if (wasPressed(handle, keyTriggerBot))  triggerBotOn  = !triggerBotOn;
                if (wasPressed(handle, keyEsp))         espOn         = !espOn;
                if (wasPressed(handle, keyAutoTotem))   autoTotemOn   = !autoTotemOn;
                if (wasPressed(handle, keyAimLock))     aimLockOn     = !aimLockOn;
                if (wasPressed(handle, keySpider))      spiderOn      = !spiderOn;
                if (wasPressed(handle, keyNoWeb))       noWebOn       = !noWebOn;
                if (wasPressed(handle, keyFreeze))      freezeOn      = !freezeOn;
                if (wasPressed(handle, keyAirJump))     airJumpOn     = !airJumpOn;
                if (wasPressed(handle, keyNoFall))      noFallOn      = !noFallOn;
                if (wasPressed(handle, keyJesus))       jesusOn       = !jesusOn;
                if (wasPressed(handle, keyFreecam)) {
                    freecamOn = !freecamOn;
                    if (freecamOn) {
                        freecamPos = p.getPos();
                        freecamYaw = p.getYaw();
                        freecamPitch = p.getPitch();
                    } else {
                        p.noClip = false;
                        p.getAbilities().flying = false;
                        if (freecamPos != null) {
                            p.setPosition(freecamPos);
                            p.setYaw(freecamYaw);
                            p.setPitch(freecamPitch);
                            p.setVelocity(0, 0, 0);
                        }
                    }
                }
                if (wasPressed(handle, keyXray)) {
                    xrayOn = !xrayOn;
                    mc.worldRenderer.reload();
                }
                if (wasPressed(handle, keyCrasher)) serverCrasherOn = !serverCrasherOn;
                PypsenFeatures.handleKey(handle);
            }

            mc.getEntityRenderDispatcher().setRenderHitboxes(hitboxOn && (!hitboxFix || mc.currentScreen == null));

            // ── ANTI-FLAG ──
            if (antiFlagOn) {
                if (speedVal > 2.0f) speedVal = 2.0f;
                if (flySpeed > 1.5f) flySpeed = 1.5f;
                if (auraRange > 4.5f) auraRange = 4.5f;
                if (clickerCps > 15f) clickerCps = 15f;
                if (triggerCps > 15f) triggerCps = 15f;
            }

            // ── AIRJUMP ──
            // Mode 1 imitates a block for the server (old apple-jump style).
            // Mode 3 only works when real ground is under the player.
            if (airJumpOn && mc.options.jumpKey.isPressed() && p.fallDistance > 0.1f) {
                int ajMode = MathHelper.clamp(airJumpMode, 0, AIRJUMP_MODES.length - 1);
                boolean realGround = p.isOnGround();
                if ((ajMode != 3 || realGround) && p.age % Math.max(1, 6 - (int) airJumpSpeed) == 0) {
                    if (ajMode == 0 || ajMode == 3) { // Vanilla / Real Blocks
                        p.jump();
                        p.addVelocity(0, 0.08 * airJumpSpeed, 0);
                    } else if (ajMode == 1) { // GhostBlock — server sees a block
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        p.jump();
                        p.addVelocity(0, 0.10 * airJumpSpeed, 0);
                    } else if (ajMode == 2) { // Packet — fake on-ground
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                        p.jump();
                        p.addVelocity(0, 0.06 * airJumpSpeed, 0);
                    }
                }
            }

            // ── NOFALL ──
            if (noFallOn && p.fallDistance > 2.5f) {
                if (noFallMode == 0) { // Packet
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                    p.fallDistance = 0;
                } else if (noFallMode == 1) { // WaterBucket
                    int slot = -1;
                    for (int i = 0; i < 9; i++) if (p.getInventory().getStack(i).getItem() == net.minecraft.item.Items.WATER_BUCKET) slot = i;
                    if (slot != -1) {
                        int oldSlot = p.getInventory().selectedSlot;
                        p.getInventory().selectedSlot = slot;
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(p.getYaw(), 90, p.isOnGround()));
                        mc.interactionManager.interactItem(p, net.minecraft.util.Hand.MAIN_HAND);
                        p.getInventory().selectedSlot = oldSlot;
                        p.fallDistance = 0;
                    }
                } else if (noFallMode == 2) { // Web
                    int slot = -1;
                    for (int i = 0; i < 9; i++) if (p.getInventory().getStack(i).getItem() == net.minecraft.item.Items.COBWEB) slot = i;
                    if (slot != -1) {
                        int oldSlot = p.getInventory().selectedSlot;
                        p.getInventory().selectedSlot = slot;
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(p.getYaw(), 90, p.isOnGround()));
                        mc.interactionManager.interactItem(p, net.minecraft.util.Hand.MAIN_HAND);
                        p.getInventory().selectedSlot = oldSlot;
                        p.fallDistance = 0;
                    }
                } else if (noFallMode == 3) { // Vulcan
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() - 0.1, p.getZ(), true));
                    p.fallDistance = 0;
                } else if (noFallMode == 4) { // Grim
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() + 1E-9, p.getZ(), false));
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                    p.fallDistance = 0;
                } else if (noFallMode == 5) { // LiquidBounce
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() - 1000, p.getZ(), true));
                    p.fallDistance = 0;
                } else if (noFallMode == 6) { // Matrix
                    if (p.fallDistance > 3f) {
                        p.setVelocity(p.getVelocity().x, -0.05, p.getVelocity().z);
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                        p.fallDistance = 0;
                    }
                } else if (noFallMode == 7) { // NCP
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() - 0.01, p.getZ(), true));
                    p.fallDistance = 0;
                }
            }

            // ── JESUS ──
            if (jesusOn && p.isTouchingWater()) {
                if (jesusMode == 0) { // Solid
                    p.setVelocity(p.getVelocity().x, 0, p.getVelocity().z);
                    p.setOnGround(true);
                } else if (jesusMode == 1) { // Bounce
                    p.setVelocity(p.getVelocity().x, 0.2, p.getVelocity().z);
                } else if (jesusMode == 2) { // LiquidBounce
                    if (p.age % 2 == 0) p.setVelocity(p.getVelocity().x, 0.05, p.getVelocity().z);
                    else p.setVelocity(p.getVelocity().x, -0.05, p.getVelocity().z);
                } else if (jesusMode == 3) { // Matrix
                    if (p.isTouchingWater()) {
                        p.setVelocity(p.getVelocity().x, 0.1, p.getVelocity().z);
                        p.setSprinting(false);
                    }
                }
            }

            // ── FREEZE ──
            if (freezeOn) {
                if (freezeMode == 0) { // Vanilla
                    p.setVelocity(0, 0, 0);
                    p.fallDistance = 0; // Prevent fall damage accumulation
                } else if (freezeMode == 1) { // Packet
                    p.setVelocity(0, 0, 0);
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                } else if (freezeMode == 2) { // Intave
                    // Micro-falling to bypass fly checks
                    p.setVelocity(0, -0.01, 0);
                    p.fallDistance = 0;
                } else if (freezeMode == 3) { // GhostBlock
                    if (p.age % 4 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.DROP_ITEM, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        p.setVelocity(p.getVelocity().x, 0.2, p.getVelocity().z);
                    } else {
                        p.setVelocity(p.getVelocity().x, 0, p.getVelocity().z); // Hover between jumps
                    }
                    p.fallDistance = 0;
                } else if (freezeMode == 4) { // LiquidBounce
                    p.setVelocity(0, 0, 0);
                    if (p.age % 2 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() + 1E-5, p.getZ(), p.isOnGround()));
                    }
                } else if (freezeMode == 5) { // Smooth Lock
                    Vec3d v = p.getVelocity();
                    p.setVelocity(v.x * 0.65, v.y * 0.35, v.z * 0.65);
                    p.fallDistance = 0;
                } else if (freezeMode == 6) { // Silent Lock
                    p.setVelocity(0, 0, 0);
                    p.fallDistance = 0;
                    if (clientTick % 12 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                    }
                }
            }

            // ── SPIDER ──
            if (spiderOn && p.horizontalCollision) {
                float spd = MathHelper.clamp(spiderSpeed, 0.5f, 4f);
                if (spiderMode == 0) { // Vanilla
                    Vec3d v = p.getVelocity();
                    p.setVelocity(v.x, 0.2 * spd, v.z);
                } else if (spiderMode == 1) { // Jump
                    if (p.isOnGround()) {
                        p.jump();
                        p.addVelocity(0, 0.05 * spd, 0);
                    }
                } else if (spiderMode == 2) { // Grim
                    if (p.age % 4 == 0) {
                        Vec3d v = p.getVelocity();
                        p.setVelocity(v.x, 0.4 * spd, v.z);
                    }
                } else if (spiderMode == 3) { // GhostBlock (F+Q Exploit)
                    if (p.age % 4 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.DROP_ITEM, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        p.jump();
                    }
                } else if (spiderMode == 4) { // Bypass Plus — stepped climb
                    Vec3d v = p.getVelocity();
                    p.setVelocity(v.x * (1f + 0.03f * spd), (p.age % 3 == 0 ? 0.28f : 0.12f) * spd, v.z * (1f + 0.03f * spd));
                    p.fallDistance = 0;
                } else if (spiderMode == 5) { // Turbo Plus — continuous boost
                    Vec3d v = p.getVelocity();
                    p.setVelocity(v.x * 1.12f * spd, 0.34f * spd, v.z * 1.12f * spd);
                    p.fallDistance = 0;
                } else if (spiderMode >= SPIDER_MODES.length) {
                    Vec3d v = p.getVelocity();
                    float wave = 0.22f + (float)(Math.sin(p.age * 0.12) * spiderCustomPulse * 0.12f);
                    p.setVelocity(v.x * 1.08f, wave * spd, v.z * 1.08f);
                    p.fallDistance = 0;
                } else {
                    Vec3d v = p.getVelocity();
                    float climb;
                    if (spiderMode == 6) climb = 0.24f; // Quantum
                    else if (spiderMode == 7) climb = p.age % 3 == 0 ? 0.34f : 0.14f; // Nebula
                    else if (spiderMode == 8) climb = 0.3f; // Zenith
                    else if (spiderMode == 9) climb = 0.18f + (p.age % 4 == 0 ? 0.15f : 0); // Aero
                    else if (spiderMode == 10) climb = 0.28f + (float)(Math.sin(p.age * 0.2) * 0.06f); // Pulse
                    else if (spiderMode == 11) climb = 0.2f; // Drift
                    else if (spiderMode == 12) climb = 0.4f; // Phantom
                    else if (spiderMode == 13) climb = 0.26f; // Orbit
                    else if (spiderMode == 14) climb = 0.22f; // Tunnel
                    else if (spiderMode == 15) climb = 0.36f; // Void
                    else if (spiderMode == 16) climb = 0.3f; // Nova
                    else if (spiderMode == 17) climb = 0.19f; // Aurora
                    else if (spiderMode == 18) climb = 0.32f; // Hyper
                    else if (spiderMode == 19) climb = 0.25f; // Silent V2
                    else climb = 0.29f; // Perk
                    p.setVelocity(v.x * 1.06f, climb * spd, v.z * 1.06f);
                    p.fallDistance = 0;
                }
            }

            // ── NOWEB (Fast mode logic) ──
            if (noWebOn) {
                Block block = mc.world.getBlockState(p.getBlockPos()).getBlock();
                boolean isWeb = block == net.minecraft.block.Blocks.COBWEB || block == net.minecraft.block.Blocks.SWEET_BERRY_BUSH;

                if (isWeb) {
                    if (noWebMode == 1) { // Fast
                        p.setVelocity(p.getVelocity().x, -0.1, p.getVelocity().z);
                    } else if (noWebMode == 3) { // Intave
                        // Shift sideways slightly to desync web collision on server, and fall fast
                        p.setVelocity(0.05, -0.4, 0.05);
                    } else if (noWebMode == 4) { // EdgeTP
                        boolean foundEdge = false;
                        for (int x = -3; x <= 3 && !foundEdge; x++) {
                            for (int z = -3; z <= 3 && !foundEdge; z++) {
                                if (x == 0 && z == 0) continue;
                                net.minecraft.util.math.BlockPos checkPos = p.getBlockPos().add(x, 0, z);
                                Block checkBlock = mc.world.getBlockState(checkPos).getBlock();
                                if (checkBlock != net.minecraft.block.Blocks.COBWEB && checkBlock != net.minecraft.block.Blocks.SWEET_BERRY_BUSH) {
                                    Vec3d target = new Vec3d(checkPos.getX() + 0.5, checkPos.getY(), checkPos.getZ() + 0.5);
                                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(target.x, target.y, target.z, p.isOnGround()));
                                    p.setPosition(target);
                                    p.setVelocity(0, p.getVelocity().y, 0);
                                    foundEdge = true;
                                }
                            }
                        }
                        if (!foundEdge) {
                            p.setVelocity(0, -0.4, 0); // Fallback if no edge found
                        }
                    }
                }
            }

            // ── AUTOTOTEM ──
            if (autoTotemOn && mc.currentScreen == null) {
                float hp = p.getHealth() + p.getAbsorptionAmount();
                if (hp <= autoTotemHealth && p.getOffHandStack().getItem() != net.minecraft.item.Items.TOTEM_OF_UNDYING) {
                    if (autoTotemMode == 0) { // Instant
                        int slot = -1;
                        for (int i = 9; i < 45; i++) {
                            if (p.playerScreenHandler.getSlot(i).getStack().getItem() == net.minecraft.item.Items.TOTEM_OF_UNDYING) {
                                slot = i;
                                break;
                            }
                        }
                        if (slot != -1) {
                            mc.interactionManager.clickSlot(0, slot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                            mc.interactionManager.clickSlot(0, 45, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                            mc.interactionManager.clickSlot(0, slot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                        }
                    } else { // Legit (Step-by-step)
                        if (totemTimer > 0) {
                            totemTimer--;
                        } else {
                            if (totemStep == 0) {
                                totemTargetSlot = -1;
                                for (int i = 9; i < 45; i++) {
                                    if (p.playerScreenHandler.getSlot(i).getStack().getItem() == net.minecraft.item.Items.TOTEM_OF_UNDYING) {
                                        totemTargetSlot = i;
                                        break;
                                    }
                                }
                                if (totemTargetSlot != -1) {
                                    mc.interactionManager.clickSlot(0, totemTargetSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                                    totemStep = 1;
                                    totemTimer = 2; // 2 ticks delay
                                }
                            } else if (totemStep == 1) {
                                mc.interactionManager.clickSlot(0, 45, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                                totemStep = 2;
                                totemTimer = 2;
                            } else if (totemStep == 2) {
                                mc.interactionManager.clickSlot(0, totemTargetSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, p);
                                totemStep = 0;
                                totemTimer = 10; // Cooldown
                            }
                        }
                    }
                } else {
                    totemStep = 0; // Reset if we have a totem or health is high
                }
            }

            // ── NOCLIP ──
            if (noclipOn) {
                p.noClip = true;
                p.fallDistance = 0;
                double step = MathHelper.clamp(noclipStep, 0.05f, 4f);
                Vec3d delta = noclipMoveDelta(p, step);
                if (noclipBypass == 0) { // Vanilla: full 3-axis movement
                    p.setVelocity(delta.x, delta.y, delta.z);
                } else if (noclipBypass == 1) { // VClip: vertical, but horizontal keys also work
                    p.setPosition(p.getX() + delta.x, p.getY() + delta.y, p.getZ() + delta.z);
                } else if (noclipBypass == 2) { // HClip: horizontal, with vertical keys when asked
                    p.setPosition(p.getX() + delta.x, p.getY() + delta.y, p.getZ() + delta.z);
                } else if (noclipBypass == 3) { // Packet desync: gentle correction, no forced downward push
                    p.setVelocity(delta.x, delta.y, delta.z);
                    if (p.age % 6 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), false));
                    }
                } else if (noclipBypass >= NOCLIP_MODES.length) {
                    // User-created list entries use the editable direction/step settings.
                    p.setPosition(p.getX() + delta.x, p.getY() + delta.y, p.getZ() + delta.z);
                } else {
                    // Perk, Side Step, Sky Lift and the extended profiles are all
                    // client-side movement shapes. Up/sideways are always available.
                    if (noclipBypass == 6) {
                        p.setVelocity(delta.x, delta.y + 0.02, delta.z);
                    } else if (noclipBypass == 7) {
                        p.setPosition(p.getX() + delta.x * 1.4, p.getY() + delta.y, p.getZ() + delta.z * 1.4);
                    } else if (noclipBypass == 8 || noclipBypass == 27) {
                        p.setVelocity(delta.x, delta.y + step * 0.35, delta.z);
                    } else if (noclipBypass == 9 || noclipBypass == 12) {
                        p.setVelocity(delta.x * 0.8, delta.y, delta.z * 0.8);
                    } else if (noclipBypass == 10 || noclipBypass == 22 || noclipBypass == 31) {
                        p.setVelocity(delta.x * 1.15, delta.y * 1.15, delta.z * 1.15);
                    } else if (noclipBypass == 11 || noclipBypass == 15 || noclipBypass == 30) {
                        p.setPosition(p.getX() + delta.x, p.getY() + delta.y, p.getZ() + delta.z);
                    } else if (noclipBypass == 13 || noclipBypass == 23) {
                        p.setVelocity(delta.x * 1.05, delta.y, delta.z * 1.05);
                    } else if (noclipBypass == 14 || noclipBypass == 16) {
                        p.setVelocity(delta.x, Math.max(-step, delta.y), delta.z);
                    } else if (noclipBypass == 17) {
                        p.setPosition(p.getX() + delta.x, p.getY() + Math.max(0, delta.y), p.getZ() + delta.z);
                    } else if (noclipBypass == 18) {
                        p.setVelocity(delta.x * 0.6, delta.y * 0.6, delta.z * 0.6);
                    } else {
                        double wave = Math.sin(clientTick * 0.18) * step * 0.15;
                        p.setVelocity(delta.x, delta.y + wave, delta.z);
                    }
                }
                p.fallDistance = 0;
            } else if (!freecamOn) {
                p.noClip = false;
            }

            // ── FREECAM ──
            if (freecamOn) {
                p.noClip = true;
                p.getAbilities().flying = true;
                double vy = 0;
                if (mc.options.jumpKey.isPressed()) vy = 0.5;
                if (mc.options.sneakKey.isPressed()) vy = -0.5;
                float fw = p.input.movementForward;
                float st = p.input.movementSideways;
                double vx = 0, vz = 0;
                if (fw != 0 || st != 0) {
                    double yaw = Math.toRadians(p.getYaw());
                    double angle = yaw - Math.atan2(fw, st) + Math.PI / 2;
                    vx = -Math.sin(angle) * 0.5;
                    vz = Math.cos(angle) * 0.5;
                }
                p.setVelocity(vx, vy, vz);
            }

            // ── SERVER CRASHER ──
            if (serverCrasherOn) {
                if (crasherStartTime == 0) crasherStartTime = System.currentTimeMillis();
                long elapsed = System.currentTimeMillis() - crasherStartTime;

                if ((crasherTimerMode == 1 && elapsed > 5000) || (crasherTimerMode == 2 && elapsed > 10000)) {
                    serverCrasherOn = false;
                    crasherStartTime = 0;
                } else if (mc.getNetworkHandler() == null && crasherTimerMode == 3) {
                    serverCrasherOn = false;
                    crasherStartTime = 0;
                }
            } else {
                crasherStartTime = 0;
            }

            // Safe local benchmark. Nothing is sent to the server, so this cannot
            // kick the player or spam packets; it only measures client CPU cost.
            if (serverCrasherOn && clientTick % 10 == 0) runCrasherBenchmark();

            // ── GODMODE ──
            if (godModeOn) {
                if (godModeMode == 0) { // Vanilla
                    // Do nothing, client can't enforce godmode in vanilla without packets
                } else if (godModeMode == 1) { // Packet
                    if (p.age % 10 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() - 0.1, p.getZ(), false));
                    }
                } else if (godModeMode == 2) { // Vulcan
                    if (p.age % 5 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() + 1337.0, p.getZ(), true));
                    }
                } else if (godModeMode == 3) { // LiquidBounce (Riding/Dismount exploit)
                    if (p.age % 3 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(p, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(p, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                    }
                }
            }

            // ── DROP ALL ITEMS ──
            if (dropAllItems) {
                for (int i = 9; i < 45; i++) {
                    if (!p.playerScreenHandler.getSlot(i).getStack().isEmpty()) {
                        mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, i, 1, net.minecraft.screen.slot.SlotActionType.THROW, p);
                    }
                }
                dropAllItems = false;
            }

            // ── CREATIVE UTILITIES (local/creative only) ──
            if (creativeDropOn) {
                creativeDropItems(mc, p);
                creativeDropOn = false;
            }
            if (creativeDupeOn) {
                creativeDupeItems(p);
                creativeDupeOn = false;
            }

            // ── AUTOARMOR ──
            if (autoArmorOn && p.age % 4 == 0 && mc.currentScreen == null && mc.interactionManager != null) {
                for (int type = 0; type < 4; type++) {
                    net.minecraft.item.ItemStack currentArmor = p.getInventory().getArmorStack(type);
                    int bestSlot = -1;
                    int bestScore = currentArmor.isEmpty() ? -1 : getArmorScore(currentArmor);

                    for (int i = 9; i < 36; i++) {
                        net.minecraft.item.ItemStack stack = p.getInventory().getStack(i);
                        if (!stack.isEmpty() && stack.getItem() instanceof net.minecraft.item.ArmorItem armorItem) {
                            if (armorItem.getSlotType().getEntitySlotId() == type) {
                                int score = getArmorScore(stack);
                                if (score > bestScore) {
                                    bestScore = score;
                                    bestSlot = i;
                                }
                            }
                        }
                    }

                    if (bestSlot != -1) {
                        if (!currentArmor.isEmpty()) {
                            mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, 8 - type, 0, net.minecraft.screen.slot.SlotActionType.QUICK_MOVE, p);
                        }
                        mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, bestSlot, 0, net.minecraft.screen.slot.SlotActionType.QUICK_MOVE, p);
                        break;
                    }
                }
            }

            // ── FASTBOW ──
            if (fastBowOn && p.getMainHandStack().getItem() == net.minecraft.item.Items.BOW && mc.options.useKey.isPressed()) {
                mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));

                if (bowJump && p.isOnGround()) {
                    p.jump();
                    p.setPitch(-90);
                }
            }

            // ── AUTOCLICKER ──
            boolean isClicking = autoClickerRight ? mc.options.useKey.isPressed() : mc.options.attackKey.isPressed();
            if (autoClickerOn && isClicking && mc.currentScreen == null) {
                clickTimer++;
                int delay = (int) (20f / Math.max(1f, clickerCps));
                if (clickTimer >= delay) {
                    clickTimer = 0;
                    if (autoClickerRight) {
                        if (autoClickerFarm && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                            net.minecraft.util.math.BlockPos pos = ((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos();
                            Block block = mc.world.getBlockState(pos).getBlock();
                            if (block == net.minecraft.block.Blocks.WHEAT || block == net.minecraft.block.Blocks.CARROTS || block == net.minecraft.block.Blocks.POTATOES) {
                                mc.interactionManager.interactBlock(p, Hand.MAIN_HAND, (net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget);
                            }
                        } else {
                            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
                        }
                        p.swingHand(Hand.MAIN_HAND);
                    } else {
                        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                            mc.interactionManager.attackEntity(p, ((EntityHitResult) mc.crosshairTarget).getEntity());
                        }
                        p.swingHand(Hand.MAIN_HAND);
                    }
                }
            } else {
                clickTimer = 0;
            }

            // ── TRIGGERBOT ──
            if (triggerBotOn && mc.currentScreen == null && mc.interactionManager != null) {
                triggerFloatTimer += 1f;
                float ticksPerHit = 20f / Math.max(1f, triggerCps);
                if (triggerJitter > 0f) {
                    ticksPerHit *= 1f + (float) (Math.random() - 0.5) * 2f * (triggerJitter / 100f);
                }
                ticksPerHit = Math.max(1f, ticksPerHit);

                if (triggerFloatTimer >= ticksPerHit) {
                    triggerFloatTimer = 0f;
                    triggerTimer++;

                    Entity target = null;
                    if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                        Entity aimed = ((EntityHitResult) mc.crosshairTarget).getEntity();
                        if (isValidTriggerTarget(aimed, p, mc)) target = aimed;
                    }

                    // Through-walls mode scans for the closest entity inside the configured cone.
                    if (target == null && triggerThroughWalls) {
                        double bestAngle = Double.MAX_VALUE;
                        for (Entity candidate : mc.world.getEntities()) {
                            if (!isValidTriggerTarget(candidate, p, mc)) continue;
                            double angle = getAngleDifference(p, candidate);
                            if (angle < bestAngle) {
                                bestAngle = angle;
                                target = candidate;
                            }
                        }
                        if (target != null && bestAngle > MathHelper.clamp(triggerFov, 1f, 180f) * 0.5f) target = null;
                    }

                    if (target != null) {
                        boolean isWeapon = p.getMainHandStack().getItem() instanceof SwordItem || p.getMainHandStack().getItem() instanceof AxeItem;
                        if (!triggerWeaponOnly || isWeapon) {
                            boolean cooldownOk = !triggerSmartDelay || p.getAttackCooldownProgress(0.0f) >= MathHelper.clamp(triggerCooldown, 0f, 1f);
                            boolean targetReady = !triggerWaitHurtTime || ((LivingEntity) target).hurtTime == 0;
                            if (cooldownOk && targetReady) {
                                if (triggerAutoWeapon) {
                                    int slot = findBestWeapon(p, p.getMainHandStack().getItem() instanceof AxeItem);
                                    if (slot >= 0) p.getInventory().selectedSlot = slot;
                                }
                                if (triggerCriticals && (p.isOnGround() || p.isTouchingWater())) {
                                    p.addVelocity(0, 0.1, 0);
                                    p.fallDistance = 0.1f;
                                    p.setOnGround(false);
                                }
                                mc.interactionManager.attackEntity(p, target);
                                p.swingHand(Hand.MAIN_HAND);
                                p.resetLastAttackedTicks();
                            }
                        }
                    }
                }
            } else {
                triggerTimer = 0;
                triggerFloatTimer = 0f;
            }

            // ── FULLBRIGHT ──
            if (fullBrightOn) {
                p.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.NIGHT_VISION, 1000, 0, false, false, false));
                wasFullBright = true;
            } else if (wasFullBright) {
                if (p.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.NIGHT_VISION)) {
                    p.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.NIGHT_VISION);
                }
                wasFullBright = false;
            }

            // ── VISUALS (ESP & SUB-FUNCTIONS) ──
            if (visualsOn) {
                mc.world.getEntities().forEach(e -> {
                    if (e instanceof LivingEntity && e != p) {
                        boolean isFriend = friends.contains(e.getName().getString());
                        boolean shouldGlow = false;
                        if (visualsTargetMode == 0) shouldGlow = true;
                        else if (visualsTargetMode == 1 && !isFriend) shouldGlow = true;
                        else if (visualsTargetMode == 2 && isFriend) shouldGlow = true;
                        e.setGlowing(shouldGlow);
                    }
                });
                visualsWasOn = true;

                if (noFireOn && p.isOnFire()) {
                    p.setFireTicks(0);
                }
                if (noNauseaOn && p.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.NAUSEA)) {
                    p.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.NAUSEA);
                }
            } else if (visualsWasOn) {
                mc.world.getEntities().forEach(e -> {
                    if (e instanceof LivingEntity && e != p) e.setGlowing(false);
                });
                visualsWasOn = false;
            }

            // ── TARGET ESP (works even when Visuals are OFF) ──
            if (targetEspOn && currentTarget != null && currentTarget.isAlive()) {
                double r = currentTarget.getWidth() * 1.25;
                long time = System.currentTimeMillis();
                for (int i = 0; i < 8; i++) {
                    double angle = (time / 6.0 + i * 45) % 360;
                    double rad = Math.toRadians(angle);
                    double px = currentTarget.getX() + Math.cos(rad) * r;
                    double pz = currentTarget.getZ() + Math.sin(rad) * r;

                    mc.world.addParticle(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, px, currentTarget.getY() + 0.05, pz, 0, 0, 0);
                    mc.world.addParticle(net.minecraft.particle.ParticleTypes.END_ROD, px, currentTarget.getY() + currentTarget.getHeight() + 0.3, pz, 0, 0, 0);
                    if (i % 2 == 0) {
                        mc.world.addParticle(net.minecraft.particle.ParticleTypes.FLAME, px, currentTarget.getY() + currentTarget.getHeight() * 0.5, pz, 0, 0, 0);
                    }
                }
            }

            // ── AIM ASSIST & AIMLOCK ──
            if (mc.currentScreen == null && !auraOn && (aimOn || aimLockOn)) {
                LivingEntity aimTarget = findAimTarget(mc, p);
                if (aimTarget != null) {
                    currentTarget = aimTarget;
                    float[] rots = getRotations(p, aimTarget, false);
                    if (aimLockOn) {
                        p.setYaw(rots[0]);
                        p.setPitch(rots[1]);
                    } else {
                        float yawDiff = MathHelper.wrapDegrees(rots[0] - p.getYaw());
                        float pitchDiff = rots[1] - p.getPitch();
                        int mode = MathHelper.clamp(aimMode, 0, AIM_MODES.length - 1);
                        switch (mode) {
                            case 0 -> { // Smooth — gradual, no snapping or sharp jerks
                                float t = MathHelper.clamp(aimSpeed / 100f, 0.01f, 1f);
                                p.setYaw(p.getYaw() + yawDiff * t);
                                p.setPitch(p.getPitch() + pitchDiff * t);
                            }
                            case 1 -> { // Linear — fixed degrees per tick
                                float step = 0.25f + aimSpeed * 0.25f;
                                p.setYaw(p.getYaw() + MathHelper.clamp(yawDiff, -step, step));
                                p.setPitch(p.getPitch() + MathHelper.clamp(pitchDiff, -step, step));
                            }
                            case 2 -> { // Snap — instant, for HvH
                                p.setYaw(rots[0]);
                                p.setPitch(rots[1]);
                            }
                            case 3 -> { // Spiral — smooth with a slow circular offset
                                float t = MathHelper.clamp(aimSpeed / 100f, 0.01f, 1f);
                                float wobble = (float) (Math.sin(auraTick * 0.25) * aimJitter);
                                p.setYaw(p.getYaw() + yawDiff * t + wobble);
                                p.setPitch(p.getPitch() + pitchDiff * t + Math.abs(wobble) * 0.5f);
                            }
                            case 4 -> { // Jitter — smooth with tiny random offsets
                                float t = MathHelper.clamp(aimSpeed / 100f, 0.01f, 1f);
                                p.setYaw(p.getYaw() + yawDiff * t + (float) ((Math.random() - 0.5) * aimJitter));
                                p.setPitch(p.getPitch() + pitchDiff * t + (float) ((Math.random() - 0.5) * aimJitter));
                            }
                            case 5 -> { // Silent — only the server sees the rotation
                                mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(rots[0], rots[1], p.isOnGround()));
                            }
                            default -> { }
                        }
                    }
                } else {
                    currentTarget = null;
                }
            }

            // ── STRAFE ──
            if (strafeOn && mc.currentScreen == null) {
                if (p.input.movementForward != 0 || p.input.movementSideways != 0) {
                    if (p.isOnGround()) p.jump();
                    double yaw = Math.toRadians(p.getYaw());
                    double angle = yaw - Math.atan2(p.input.movementForward, p.input.movementSideways) + Math.PI / 2;
                    double spd = 0.26;
                    p.setVelocity(-Math.sin(angle) * spd, p.getVelocity().y, Math.cos(angle) * spd);
                }
            }

            // ── SPEED ──
            if (speedOn && mc.currentScreen == null && !strafeOn) {
                float fw = p.input.movementForward;
                float st = p.input.movementSideways;
                if (fw != 0 || st != 0) {
                    double yaw   = Math.toRadians(p.getYaw());
                    double spd   = speedVal / 20.0;
                    double angle = yaw - Math.atan2(fw, st) + Math.PI / 2;
                    double vx = -Math.sin(angle) * spd;
                    double vz =  Math.cos(angle) * spd;

                    switch (speedBypass) {
                        case 4 -> { // NCP
                            if (p.isOnGround()) p.jump();
                            else { vx *= 1.02; vz *= 1.02; p.setVelocity(p.getVelocity().x, p.getVelocity().y - 0.02, p.getVelocity().z); }
                        }
                        case 5 -> { // Matrix
                            if (p.isOnGround()) p.jump();
                            vx *= 1.01; vz *= 1.01;
                        }
                        case 6 -> { // Grim
                            spd = 0.28;
                            vx = -Math.sin(angle) * spd; vz = Math.cos(angle) * spd;
                        }
                        case 7 -> { // Funtime
                            if (p.isOnGround()) p.jump();
                            vx *= 1.005; vz *= 1.005;
                        }
                        case 8 -> { // ReallyWorld
                            spd = 0.26;
                            vx = -Math.sin(angle) * spd; vz = Math.cos(angle) * spd;
                        }
                        case 9 -> { // Vulcan
                            if (p.isOnGround()) p.jump();
                            if (p.age % 2 == 0) { vx *= 1.01; vz *= 1.01; }
                        }
                        case 10 -> { // Spartan
                            spd = 0.25;
                            vx = -Math.sin(angle) * spd; vz = Math.cos(angle) * spd;
                        }
                    }
                    p.setVelocity(vx, p.getVelocity().y, vz);
                }
            }

            // ── FLY ──
            if (flyOn) {
                p.getAbilities().flying = true;
                double vy = 0;
                if (mc.options.jumpKey.isPressed())   vy =  flySpeed / 10.0;
                if (mc.options.sneakKey.isPressed())   vy = -flySpeed / 10.0;

                float fw = p.input.movementForward;
                float st = p.input.movementSideways;
                double vx = p.getVelocity().x;
                double vz = p.getVelocity().z;

                if (fw != 0 || st != 0) {
                    double yaw   = Math.toRadians(p.getYaw());
                    double spd   = flySpeed / 20.0;
                    double angle = yaw - Math.atan2(fw, st) + Math.PI / 2;
                    vx = -Math.sin(angle) * spd;
                    vz =  Math.cos(angle) * spd;
                } else { vx = 0; vz = 0; }

                switch (flyBypass) {
                    case 4 -> vy = -0.04; // NCP
                    case 5 -> { if (p.age % 10 == 0) vy = 0.1; else vy = -0.05; } // Matrix
                    case 6 -> vy = 0; // Grim
                    case 7 -> vy = -0.01; // Funtime
                    case 8 -> vy = -0.03; // ReallyWorld
                    case 9 -> { if (p.age % 5 == 0) vy = 0.05; else vy = -0.05; } // Vulcan
                    case 10 -> vy = -0.02; // Spartan
                    case 11 -> vy = Math.sin(p.age * 0.12) * 0.015; // Grim AC
                    case 12 -> vy = -0.015; // Polar AC
                    case 13 -> vy = -0.02 + Math.sin(p.age * 0.08) * 0.01; // Intave
                    case 14 -> vy = 0.06; // Hyper
                    case 15 -> vy = -0.01 + (p.age % 3 == 0 ? 0.03 : 0); // NCP+
                    case 16 -> vy = -0.02 + (p.age % 7 == 0 ? 0.08 : 0); // Grim+
                    case 17 -> vy = Math.cos(p.age * 0.1) * 0.02; // Intave+
                    case 18 -> vy = -0.03; // Vulcan+
                    case 19 -> vy = -0.01; // Matrix+
                    case 20 -> vy = p.age % 4 == 0 ? 0.04 : -0.01; // Polar+
                    case 21 -> vy = Math.sin(p.age * 0.07) * 0.04; // Quantum
                    case 22 -> vy = 0.03; // Nebula
                    case 23 -> vy = -0.005; // Zenith
                    case 24 -> vy = -0.02 + Math.sin(p.age * 0.15) * 0.02; // Aero
                    case 25 -> vy = p.age % 6 == 0 ? 0.09 : -0.03; // Pulse
                    case 26 -> vy = Math.cos(p.age * 0.05) * 0.025; // Drift
                    case 27 -> vy = 0.08; // Skywalker
                    case 28 -> vy = -0.015 + Math.sin(p.age * 0.2) * 0.005; // Phantom
                    case 29 -> vy = 0; // Silent Sky
                    case 30 -> vy = -0.04; // Turbo Sky
                    case 31 -> vy = Math.sin(p.age * 0.09) * 0.03; // Orbit
                    case 32 -> vy = -0.01; // Glide
                    case 33 -> vy = p.age % 8 == 0 ? 0.07 : -0.02; // Matrix V2
                    case 34 -> vy = Math.cos(p.age * 0.11) * 0.015; // AAC+
                    case 35 -> vy = -0.025; // Spartan+
                    case 36 -> vy = 0.01; // Polar V2
                    case 37 -> vy = -0.01 + Math.sin(p.age * 0.13) * 0.01; // Intave V2
                    case 38 -> vy = -0.02 + (p.age % 5 == 0 ? 0.06 : 0); // Grim V2
                    case 39 -> vy = 0.05; // Hyper V2
                    case 40 -> vy = 0; // Vanilla Air
                    case 41 -> vy *= 0.35; // Smooth Fly
                    case 42 -> vy = p.age % 2 == 0 ? 0.02 : -0.01; // Step Fly
                    case 43 -> vy = -0.015; // Guard
                    case 44 -> vy = Math.sin(p.age * 0.04) * 0.05; // Nova
                    case 45 -> vy = Math.cos(p.age * 0.06) * 0.03; // Aurora
                    default -> vy = flyCustomVertical + Math.sin(p.age * 0.1) * flyCustomPulse;
                }
                if (flyBypass >= BYPASS_NAMES.length) vy = flyCustomVertical + Math.sin(p.age * 0.1) * flyCustomPulse;
                p.setVelocity(vx, vy, vz);
            } else {
                if (mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
                    mc.player.getAbilities().flying = false;
                }
            }

            // ── KILLAURA (ROBUST 1.9+ LOGIC) ──
            if (auraOn) {
                auraTick++;

                // Smooth rotation logic
                if (auraRotationEnabled && isRotating && (auraRotMode == 2 || auraRotMode == 16 || auraRotMode == 17)) {
                    if (auraRotMode == 2) {
                        p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.4f);
                        p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.4f);
                    } else if (auraRotMode == 16) {
                        float smoothFactor = 0.01f + (auraStickiness / 100f) * 0.99f; // 0.01 to 1.0
                        float diffYaw = targetYaw - p.getYaw();
                        float diffPitch = targetPitch - p.getPitch();

                        // Apply curve
                        if (auraRotCurve > 0) {
                            float curve = (auraRotCurve / 100f) * 30.0f;
                            diffYaw += (float)(Math.sin(auraTick * 0.2) * curve);
                            diffPitch += (float)(Math.cos(auraTick * 0.2) * curve);
                        }

                        p.setYaw(p.getYaw() + diffYaw * smoothFactor);
                        p.setPitch(p.getPitch() + diffPitch * smoothFactor);
                    } else if (auraRotMode == 17) {
                        float diffYaw = targetYaw - p.getYaw();
                        float diffPitch = targetPitch - p.getPitch();

                        // Base smoothing
                        float smoothFactor = 0.05f + (auraStickiness / 100f) * 0.95f;

                        // Add looping effect (figure-8 pattern)
                        float loopSize = (auraRotCurve / 100f) * 40.0f;
                        float loopSpeed = 0.3f;

                        diffYaw += (float)(Math.sin(auraTick * loopSpeed) * loopSize);
                        diffPitch += (float)(Math.sin(auraTick * loopSpeed * 2.0) * loopSize * 0.5f);

                        p.setYaw(p.getYaw() + diffYaw * smoothFactor);
                        p.setPitch(p.getPitch() + diffPitch * smoothFactor);
                    }
                }

                if (auraHitInGuis || mc.currentScreen == null) {
                    var list = mc.world.getOtherEntities(p, p.getBoundingBox().expand(auraRange), e -> {
                        if (!(e instanceof LivingEntity) || !e.isAlive() || e == p || e.isSpectator()) return false;
                        if (friends.contains(e.getName().getString())) return false;

                        boolean isPlayer = e instanceof net.minecraft.entity.player.PlayerEntity;
                        boolean isMob = e instanceof net.minecraft.entity.mob.HostileEntity;
                        boolean isAnimal = e instanceof net.minecraft.entity.passive.AnimalEntity;

                        if (isPlayer && !auraTargetPlayers) return false;
                        if (isMob && !auraTargetMobs) return false;
                        if (isAnimal && !auraTargetAnimals) return false;

                        // AntiBot checks must not discard ordinary mobs: names such as
                        // "Iron Golem" contain spaces and mob age starts at zero.
                        if (antiBotOn && isPlayer) {
                            if (e.isInvisible() || e.age < 20 || e.getName().getString().isEmpty()) return false;
                        }

                        // NOT NEAR: ignore targets inside the keep-away radius.
                        if (auraNotNear && p.distanceTo(e) < Math.max(0.5f, auraNotNearRange)) return false;

                        // FOV Check — use clean rotations, never jittered ones.
                        if (auraFov < 360f) {
                            float[] rots = getRotations(p, e, false);
                            float yawDiff = Math.abs(net.minecraft.util.math.MathHelper.wrapDegrees(p.getYaw() - rots[0]));
                            if (yawDiff > auraFov / 2f) return false;
                        }

                        return true;
                    });

                    LivingEntity target = null;
                    if (!list.isEmpty()) {
                        switch (auraTargetMode) {
                            case 0 -> target = (LivingEntity) list.stream().min(Comparator.comparingDouble(e -> e.distanceTo(p))).orElse(null); // Distance
                            case 1 -> target = (LivingEntity) list.stream().min(Comparator.comparingDouble(e -> ((LivingEntity)e).getHealth())).orElse(null); // Health
                            case 2 -> target = (LivingEntity) list.stream().min(Comparator.comparingDouble(e -> ((LivingEntity)e).getArmor())).orElse(null); // Armor
                            case 3 -> target = (LivingEntity) list.stream().min(Comparator.comparingDouble(e -> getAngleDifference(p, e))).orElse(null); // Angle
                        }
                    }

                    currentTarget = target;

                    if (target != null && p.distanceTo(target) <= auraRange) {
                        if (!auraThroughWalls && !p.canSee(target)) {
                            target = null;
                            currentTarget = null;
                            isRotating = false;
                        }

                        if (target != null) {

                            // 1. Calculate Rotations
                            float[] rots = getRotations(p, target);
                            float idealYaw = rots[0];
                            float idealPitch = rots[1];

                            // Apply auraRotSpeed limit
                            if (auraRotSpeed < 180.0f) {
                                float diffYaw = net.minecraft.util.math.MathHelper.wrapDegrees(idealYaw - p.getYaw());
                                float diffPitch = idealPitch - p.getPitch();

                                if (diffYaw > auraRotSpeed) diffYaw = auraRotSpeed;
                                if (diffYaw < -auraRotSpeed) diffYaw = -auraRotSpeed;
                                if (diffPitch > auraRotSpeed) diffPitch = auraRotSpeed;
                                if (diffPitch < -auraRotSpeed) diffPitch = -auraRotSpeed;

                                targetYaw = p.getYaw() + diffYaw;
                                targetPitch = p.getPitch() + diffPitch;
                            } else {
                                targetYaw = idealYaw;
                                targetPitch = idealPitch;
                            }
                            isRotating = true;

                            // STRAFE can be disabled independently in a custom preset.
                            if (auraStrafeEnabled) {
                                double circleAngle = Math.toRadians(targetYaw + 90.0f);
                                p.setVelocity(-Math.sin(circleAngle) * 0.18, p.getVelocity().y, Math.cos(circleAngle) * 0.18);
                            }

                            // Apply Rotations based on mode — can be disabled in custom presets.
                            if (auraRotationEnabled) switch (auraRotMode) {
                                case 1 -> { // Snap
                                    p.setYaw(targetYaw);
                                    p.setPitch(targetPitch);
                                }
                                case 2 -> { // Smooth (handled above)
                                }
                                case 3 -> { // Silent
                                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(targetYaw, targetPitch, p.isOnGround()));
                                }
                                case 4 -> { // Matrix (Randomized Jitter)
                                    p.setYaw(targetYaw + (float)(Math.random() * 6 - 3));
                                    p.setPitch(targetPitch + (float)(Math.random() * 6 - 3));
                                }
                                case 5 -> { // Predict
                                    double predX = target.getX() + (target.getX() - target.prevX) * 2.0;
                                    double predZ = target.getZ() + (target.getZ() - target.prevZ) * 2.0;
                                    double dx = predX - p.getX();
                                    double dz = predZ - p.getZ();
                                    double dy = (target.getY() + target.getEyeHeight(target.getPose())) - (p.getY() + p.getEyeHeight(p.getPose()));
                                    double dist = Math.sqrt(dx * dx + dz * dz);
                                    targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
                                    targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.5f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.5f);
                                }
                                case 6 -> { // Smart (Super smooth, free but snapping)
                                    targetYaw += (float)((Math.random() - 0.5) * 4.0);
                                    targetPitch += (float)((Math.random() - 0.5) * 4.0);
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.25f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.25f);
                                }
                                case 7 -> { // Legit (Smooth, no jitter, precise)
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.15f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.15f);
                                }
                                case 8 -> { // HvH (Instant snap, max precision)
                                    p.setYaw(targetYaw);
                                    p.setPitch(targetPitch);
                                }
                                case 9 -> { // Derp (Random crazy rotations, but hits target)
                                    targetYaw += (float)((Math.random() - 0.5) * 360.0);
                                    targetPitch += (float)((Math.random() - 0.5) * 180.0);
                                    p.setYaw(targetYaw);
                                    p.setPitch(targetPitch);
                                }
                                case 10 -> { // Cinematic (Extremely smooth)
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.05f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.05f);
                                }
                                case 11 -> { // NCP (Strict limits on rotation speed)
                                    float diffYaw = targetYaw - p.getYaw();
                                    float diffPitch = targetPitch - p.getPitch();
                                    diffYaw = Math.max(-30f, Math.min(30f, diffYaw));
                                    diffPitch = Math.max(-15f, Math.min(15f, diffPitch));
                                    p.setYaw(p.getYaw() + diffYaw);
                                    p.setPitch(p.getPitch() + diffPitch);
                                }
                                case 12 -> { // AAC (Specific jitter)
                                    p.setYaw(targetYaw + (float)(Math.sin(System.currentTimeMillis() / 100.0) * 2.0));
                                    p.setPitch(targetPitch + (float)(Math.cos(System.currentTimeMillis() / 100.0) * 2.0));
                                }
                                case 13 -> { // Sunrise (Specific RU server bypass)
                                    targetYaw += (float)((Math.random() - 0.5) * 1.5);
                                    targetPitch += (float)((Math.random() - 0.5) * 1.5);
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.4f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.4f);
                                }
                                case 14 -> { // Vulcan (Vulcan AC specific rotations)
                                    float diffYaw = targetYaw - p.getYaw();
                                    diffYaw = Math.round(diffYaw / 0.5f) * 0.5f; // Round to nearest 0.5
                                    p.setYaw(p.getYaw() + diffYaw * 0.8f);
                                    p.setPitch(targetPitch);
                                }
                                case 15 -> { // Intave (Very strict, needs to be perfectly aligned but with slight realistic jitter, and no snapping)
                                    p.setYaw(p.getYaw() + (targetYaw - p.getYaw()) * 0.1f + (float)(Math.random() - 0.5) * 0.5f);
                                    p.setPitch(p.getPitch() + (targetPitch - p.getPitch()) * 0.1f + (float)(Math.random() - 0.5) * 0.5f);
                                }
                                case 16 -> { // Legit Smooth (handled above)
                                }
                                case 17 -> { // Looping (handled above)
                                }
                                case 18 -> { // LiquidBounce
                                    float diffYaw = targetYaw - p.getYaw();
                                    float diffPitch = targetPitch - p.getPitch();

                                    // LiquidBounce uses a specific randomized step
                                    float step = 40.0f + (float)Math.random() * 10.0f;
                                    diffYaw = Math.max(-step, Math.min(step, diffYaw));
                                    diffPitch = Math.max(-step, Math.min(step, diffPitch));

                                    p.setYaw(p.getYaw() + diffYaw);
                                    p.setPitch(p.getPitch() + diffPitch);
                                }
                                case 19 -> { // Custom
                                    float diffYaw = targetYaw - p.getYaw();
                                    float diffPitch = targetPitch - p.getPitch();
                                    float smooth = 0.01f + (auraStickiness / 100f) * 0.99f;
                                    p.setYaw(p.getYaw() + diffYaw * smooth);
                                    p.setPitch(p.getPitch() + diffPitch * smooth);
                                }
                                case 20 -> { // Noise
                                    float noiseYaw = (float)(Math.sin(auraTick * 0.5) * (auraRotCurve / 100f) * 20.0f);
                                    float noisePitch = (float)(Math.cos(auraTick * 0.5) * (auraRotCurve / 100f) * 20.0f);
                                    p.setYaw(targetYaw + noiseYaw);
                                    p.setPitch(targetPitch + noisePitch);
                                }
                            }

                            // 2. Check if we can hit
                            boolean canHit = false;

                            // STRICT 1.9 COOLDOWN CHECK
                            float cooldown = p.getAttackCooldownProgress(0.0f);

                            switch (auraMode) {
                                case 0 -> { // 1.9 Strict
                                    // Small tolerance: float cooldown can land at 0.9999 on some clients.
                                    if (cooldown >= 0.95f) {
                                        canHit = true;
                                    }
                                }
                                case 1 -> { // 1.8 Spam
                                    canHit = auraTick % 2 == 0;
                                }
                                case 2 -> { // Adaptive (Randomized cooldown to bypass anti-cheats)
                                    if (cooldown >= (0.85f + Math.random() * 0.15f)) {
                                        canHit = true;
                                    }
                                }
                            }

                            // Timing Check (Jump Phase)
                            if (canHit) {
                                if (auraTimingMode == 1) { // Fall (Crits)
                                    if (p.getVelocity().y >= 0 || p.isOnGround()) canHit = false;
                                } else if (auraTimingMode == 2) { // Jump (Crits)
                                    if (p.isOnGround() || p.getVelocity().y <= 0) canHit = false;
                                }
                            }

                            // Raycast check (if enabled, ensure we are actually looking at them)
                            if (canHit && auraRayCast && auraRotMode != 3) { // If not silent
                                double dToT = p.distanceTo(target);
                                Vec3d eyePos = p.getCameraPosVec(1.0f);
                                Vec3d lookVec = p.getRotationVec(1.0f);
                                Vec3d traceEnd = eyePos.add(lookVec.x * dToT, lookVec.y * dToT, lookVec.z * dToT);
                                HitResult hit = mc.world.raycast(new RaycastContext(eyePos, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, p));
                                // If we hit a block before the entity, don't hit (unless through walls is on)
                                if (!auraThroughWalls && hit.getType() == HitResult.Type.BLOCK) {
                                    canHit = false;
                                }
                            }

                            // 3. Bypasses
                            if (canHit) {
                                switch (auraBypass) {
                                    case 2 -> { if (auraTick % 3 != 0) canHit = false; }
                                    case 3 -> { // Strafe around — respects the separate strafe toggle
                                        if (auraStrafeEnabled) {
                                            double circleAngle = Math.toRadians(targetYaw + 90);
                                            p.setVelocity(-Math.sin(circleAngle) * 0.2, p.getVelocity().y, Math.cos(circleAngle) * 0.2);
                                        }
                                    }
                                    case 5 -> { if (Math.random() > 0.8) canHit = false; } // Matrix
                                    case 6 -> { if (!p.canSee(target)) canHit = false; } // Grim
                                    case 7 -> { // Funtime
                                        if (Math.random() > 0.9) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                    }
                                    case 8 -> { // ReallyWorld
                                        if (cooldown < 0.95f) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                    }
                                    case 9 -> { // Vulcan
                                        if (p.age % 2 != 0) canHit = false;
                                    }
                                    case 10 -> { // Spartan
                                        if (p.distanceTo(target) > 3.5f) canHit = false;
                                    }
                                    case 11 -> { // Grim AC (Strict Raycast & Timing)
                                        if (cooldown < 0.95f) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                        if (Math.abs(p.getYaw() - targetYaw) > 15) canHit = false; // Must be looking precisely
                                    }
                                    case 12 -> { // Polar AC (Randomized Delays & Smooth Rots)
                                        if (cooldown < 0.95f + Math.random() * 0.05f) canHit = false;
                                        if (auraTick % (2 + (int)(Math.random() * 2)) != 0) canHit = false; // Random hits
                                    }
                                    case 13 -> { // Intave
                                        if (cooldown < 0.95f) canHit = false;
                                        if (p.distanceTo(target) > 3.0f) canHit = false; // Strict reach
                                        if (Math.abs(p.getYaw() - targetYaw) > 20) canHit = false; // Must be looking at target
                                    }
                                }
                            }

                            // 4. Attack
                            if (canHit) {
                                // Weapon selection: Shield Breaker works even with AutoWeapon off.
                                boolean blockingPlayer = shieldBreaker && target instanceof net.minecraft.entity.player.PlayerEntity tp && tp.isBlocking();
                                if (autoWeapon || blockingPlayer) {
                                    int bestSlot = findBestWeapon(p, blockingPlayer);
                                    if (bestSlot >= 0) p.getInventory().selectedSlot = bestSlot;
                                }

                                // Criticals (Mini-jump)
                                if (criticals && (p.isOnGround() || (waterCrits && p.isTouchingWater()))) {
                                    p.addVelocity(0, 0.1, 0);
                                    p.fallDistance = 0.1f;
                                    p.setOnGround(false);
                                }

                                // Visuals Particles
                                if (visualsOn && visualsParticles > 0) {
                                    net.minecraft.particle.ParticleEffect particle = net.minecraft.particle.ParticleTypes.CRIT;
                                    if (visualsParticles == 2) particle = net.minecraft.particle.ParticleTypes.FLAME;
                                    else if (visualsParticles == 3) particle = net.minecraft.particle.ParticleTypes.WITCH;
                                    else if (visualsParticles == 4) particle = net.minecraft.particle.ParticleTypes.ENCHANTED_HIT;

                                    int particleCount = optimizationOn ? 2 : 5;
                                    for (int i = 0; i < particleCount; i++) {
                                        mc.world.addParticle(particle, target.getX() + (Math.random() - 0.5), target.getY() + Math.random() * target.getHeight(), target.getZ() + (Math.random() - 0.5), 0, 0, 0);
                                    }
                                }

                                if (auraJump && p.isOnGround()) p.jump();

                                mc.interactionManager.attackEntity(p, target);
                                p.swingHand(Hand.MAIN_HAND);
                                p.resetLastAttackedTicks(); // Reset cooldown
                            }
                        }
                    } else {
                        currentTarget = null;
                        isRotating = false;
                        if (auraSpinBot) p.setYaw(p.getYaw() + 45f);
                    }
                }
            } else {
                if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    Entity e = ((EntityHitResult) mc.crosshairTarget).getEntity();
                    if (e instanceof LivingEntity) currentTarget = (LivingEntity) e;
                } else {
                    currentTarget = null;
                }
                isRotating = false;
                if (auraSpinBot) p.setYaw(p.getYaw() + 45f);
            }

            PypsenFeatures.tick(mc);

            // ── CHUNK ANALYSIS (XRay far scan + Base Finder) ──
            if ((xrayOn && (xrayFarScan || xrayHighlight)) || baseFinderOn) {
                int scanEvery = optimizationOn ? 6 + MathHelper.clamp(optProfile, 0, OPT_PROFILES.length - 1) * 6 : 6;
                if (clientTick % scanEvery == 0) scanChunks(mc, p);
                if (baseFinderOn && clientTick % 80 == 0) emitBaseFinderGlow(mc, p);
            }

            if (xrayOn != xrayWasOn) {
                mc.worldRenderer.reload();
                xrayWasOn = xrayOn;
            }
        });
    }

    // ══════════ CUSTOM AMBIENT / SPIN EFFECTS ══════════

    private static int hsvToRgb(float h, float s, float v) {
        h = ((h % 1f) + 1f) % 1f;
        int i = (int) (h * 6f);
        float f = h * 6f - i;
        float p = v * (1f - s);
        float q = v * (1f - f * s);
        float t = v * (1f - (1f - f) * s);
        float r, g, b;
        if (i == 0) { r = v; g = t; b = p; }
        else if (i == 1) { r = q; g = v; b = p; }
        else if (i == 2) { r = p; g = v; b = t; }
        else if (i == 3) { r = p; g = q; b = v; }
        else if (i == 4) { r = t; g = p; b = v; }
        else { r = v; g = p; b = q; }
        return ((int) (r * 255f) << 16) | ((int) (g * 255f) << 8) | (int) (b * 255f);
    }

    private static int ambientColor() {
        float h = ambientRainbow
                ? ((System.currentTimeMillis() % 4000L) / 4000f)
                : MathHelper.clamp(ambientHue, 0f, 360f) / 360f;
        return hsvToRgb(h, 0.78f, 1f);
    }

    private static int withAlpha(int rgb, float alpha) {
        return ((MathHelper.clamp((int) (alpha * 255f), 0, 255) & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }

    private static void drawRing(DrawContext ctx, int cx, int cy, int radius, double angle,
                                 int rgb, float alpha) {
        int segments = 18;
        int dot = MathHelper.clamp(radius / 10, 2, 9);
        for (int i = 0; i < segments; i++) {
            double a = angle + i * (Math.PI * 2.0 / segments);
            int x = cx + (int) Math.round(Math.cos(a) * radius);
            int y = cy + (int) Math.round(Math.sin(a) * radius);
            ctx.fill(x - dot, y - dot, x + dot, y + dot, withAlpha(rgb, alpha));
        }
    }

    private static void drawAmbient(DrawContext ctx, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        float strength = MathHelper.clamp(ambientStrength, 0f, 1f);
        int rgb = ambientColor();
        int mode = MathHelper.clamp(ambientMode, 0, AMBIENT_MODES.length - 1);

        if (mode == 0) {
            ctx.fill(0, 0, sw, sh, withAlpha(rgb, strength * 0.45f));
        } else if (mode == 1) {
            int bands = 14;
            int maxInset = Math.min(sw, sh) / 2;
            for (int i = 0; i < bands; i++) {
                float t = i / (float) bands;
                int inset = (int) (maxInset * t);
                ctx.fill(inset, inset, sw - inset, sh - inset, withAlpha(rgb, strength * 0.11f * (1f - t)));
            }
        } else if (mode == 2) {
            int step = Math.max(2, Math.min(sw, sh) / 80);
            for (int i = 0; i < 12; i++) {
                int inset = i * step;
                int a = withAlpha(0x000000, strength * 0.22f * (1f - i / 12f));
                ctx.fill(inset, inset, sw - inset, inset + 2, a);
                ctx.fill(inset, sh - inset - 2, sw - inset, sh - inset, a);
                ctx.fill(inset, inset, inset + 2, sh - inset, a);
                ctx.fill(sw - inset - 2, inset, sw - inset, sh - inset, a);
            }
        } else if (mode == 3) {
            for (int i = 0; i < 16; i++) {
                int y0 = sh * i / 16;
                int y1 = sh * (i + 1) / 16;
                ctx.fill(0, y0, sw, y1, withAlpha(rgb, strength * 0.32f * (i / 15f)));
            }
        } else {
            drawRing(ctx, sw / 2, sh / 2, Math.min(sw, sh) / 3, clientTick * 0.035, rgb, strength * 0.5f);
            drawRing(ctx, sw / 2, sh / 2, Math.min(sw, sh) / 5, -clientTick * 0.05, rgb, strength * 0.35f);
        }
    }

    private static void drawSpinEffects(DrawContext ctx, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int rgb = ambientColor();
        int mode = MathHelper.clamp(spinMode, 0, SPIN_MODES.length - 1);
        float speed = MathHelper.clamp(spinSpeed, 0f, 8f);
        int radius = (int) MathHelper.clamp(spinSize, 12f, 160f);
        double angle = clientTick * 0.045 * speed;

        if (mode == 4) {
            drawRing(ctx, sw / 2, sh / 2, radius, angle, rgb, 0.65f);
            drawRing(ctx, sw / 2, sh / 2, radius / 2, -angle * 1.4, rgb, 0.45f);
        } else if (mode == 3) {
            for (int i = 0; i < 3; i++) {
                drawRing(ctx, sw / 2, sh / 2, Math.max(12, radius - i * 10), angle + i * 0.8, rgb, 0.7f - i * 0.18f);
            }
        } else if (mode == 2) {
            int pulse = (int) (radius * (0.7 + 0.3 * Math.sin(clientTick * 0.12)));
            drawRing(ctx, sw / 2, sh / 2, pulse, angle, rgb, 0.7f);
        } else {
            drawRing(ctx, sw / 2, sh / 2, radius, mode == 1 ? -angle : angle, rgb, 0.7f);
        }

        if (mc.world != null && currentTarget != null && currentTarget.isAlive()) {
            Camera camera = mc.gameRenderer.getCamera();
            if (camera.isReady()) {
                Vec3d camPos = camera.getPos();
                org.joml.Vector3f right = camera.getDiagonalPlane();
                org.joml.Vector3f up = camera.getVerticalPlane();
                org.joml.Vector3f forward = camera.getHorizontalPlane();
                double fov = mc.options.getFov().getValue().floatValue();
                double focal = (sh * 0.5) / Math.tan(Math.toRadians(fov) * 0.5);
                if (Double.isFinite(focal) && focal > 0) {
                    float[] s = projectToScreen(new Vec3d(currentTarget.getX(), currentTarget.getY() + currentTarget.getHeight() * 0.5,
                            currentTarget.getZ()), camPos, right, up, forward, focal, sw, sh);
                    if (s != null) {
                        drawRing(ctx, (int) s[0], (int) s[1], Math.max(14, radius / 2), -angle * 0.7, rgb, 0.55f);
                    }
                }
            }
        }
    }

    // ══════════ XRAY / BASE FINDER / LOCAL UTILITIES ══════════

    private static String blockName(Block block) {
        try {
            return block.getName().getString().toLowerCase(Locale.ROOT);
        } catch (Exception ignored) {
            return "";
        }
    }

    private static boolean isOreBlock(Block block) {
        String n = blockName(block);
        return n.contains("ore") || n.contains("ancient debris") || n.contains("nether gold");
    }

    private static boolean isContainerBlock(Block block) {
        String n = blockName(block);
        return n.contains("chest") || n.contains("shulker") || n.contains("barrel")
                || n.contains("hopper") || n.contains("dispenser") || n.contains("dropper");
    }

    private static boolean xrayBlockMatches(Block block) {
        int mode = MathHelper.clamp(xrayMode, 0, XRAY_MODES.length - 1);
        if (mode == 1) return isContainerBlock(block) || XRAY_BLOCKS.contains(block);
        if (mode == 2) return isOreBlock(block) || XRAY_BLOCKS.contains(block);
        if (mode == 3) return isContainerBlock(block) || isOreBlock(block) || XRAY_BLOCKS.contains(block);
        return XRAY_BLOCKS.contains(block);
    }

    /** Called from BlockMixin. No world/position state is required on newer targets. */
    public static boolean xrayShouldDrawSide(Block block, Direction side) {
        if (!xrayOn) return true;
        // Anti-xray bypass mode: stop client-side face culling entirely so hidden
        // layers cannot be filtered out by the renderer's occlusion pass.
        if (xrayAntiXray) return true;
        int bypass = MathHelper.clamp(xrayBypass, 0, XRAY_BYPASS_MODES.length - 1);
        if (!xrayBlockMatches(block)) {
            return bypass >= 2 || (bypass == 1 && (side == Direction.UP || side == Direction.DOWN));
        }
        return true;
    }

    private static String xrayBlockNames() {
        List<String> names = new ArrayList<>();
        for (Block b : XRAY_BLOCKS) names.add(b.getName().getString());
        Collections.sort(names);
        return String.join(",", names);
    }

    private static void loadXrayBlockNames(String encoded) {
        XRAY_BLOCKS.clear();
        if (encoded.trim().isEmpty()) return;
        Set<String> wanted = new HashSet<>(Arrays.asList(encoded.split(",")));
        Registries.BLOCK.forEach(b -> {
            if (wanted.contains(b.getName().getString())) XRAY_BLOCKS.add(b);
        });
    }

    public static void resetXrayBlocks() {
        XRAY_BLOCKS.clear();
        XRAY_BLOCKS.addAll(Arrays.asList(DEFAULT_XRAY_BLOCKS));
    }

    public static String[] allBypassNames() {
        String[] all = Arrays.copyOf(BYPASS_NAMES, BYPASS_NAMES.length + customBypassNames.size());
        for (int i = 0; i < customBypassNames.size(); i++) {
            all[BYPASS_NAMES.length + i] = customBypassNames.get(i);
        }
        return all;
    }

    public static String[] allNoclipModes() {
        String[] all = Arrays.copyOf(NOCLIP_MODES, NOCLIP_MODES.length + customBypassNames.size());
        for (int i = 0; i < customBypassNames.size(); i++) {
            all[NOCLIP_MODES.length + i] = customBypassNames.get(i);
        }
        return all;
    }

    public static String[] allSpiderModes() {
        String[] all = Arrays.copyOf(SPIDER_MODES, SPIDER_MODES.length + customBypassNames.size());
        for (int i = 0; i < customBypassNames.size(); i++) {
            all[SPIDER_MODES.length + i] = customBypassNames.get(i);
        }
        return all;
    }

    public static String sanitizeEntry(String value) {
        if (value == null) return "";
        String cleaned = value.trim().replaceAll("[^A-Za-z0-9_\\- .]", "_");
        if (cleaned.length() > 24) cleaned = cleaned.substring(0, 24);
        return cleaned;
    }

    public static UUID offlineUuid(String name) {
        String key = "OfflinePlayer:" + sanitizeEntry(name).toLowerCase(Locale.ROOT);
        long high = 0, low = 0;
        for (byte b : key.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
            high = 31L * high + (b & 0xff);
            low = 37L * low + (b & 0xff);
        }
        return new UUID(high, low);
    }

    /** Changes only the local client session; no token or launcher files are touched. */
    public static boolean applyAccount(int index) {
        if (index < 0 || index >= accountNames.size()) return false;
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            Object current = mc.getSession();
            if (current == null) {
                accountStatus = "\u00a7cNo session available";
                return false;
            }
            String name = sanitizeEntry(accountNames.get(index));
            if (name.isEmpty()) {
                accountStatus = "\u00a7cEmpty name";
                return false;
            }
            Object[] options = {Optional.empty(), Optional.empty()};
            Object replacement = null;
            for (java.lang.reflect.Constructor<?> c : current.getClass().getConstructors()) {
                Class<?>[] args = c.getParameterTypes();
                if (args.length == 5 && args[0] == String.class && args[1] == UUID.class
                        && args[2] == String.class && args[3] == Optional.class && args[4] == Optional.class) {
                    replacement = c.newInstance(name, offlineUuid(name), "offline", options[0], options[1]);
                    break;
                }
                if (args.length == 6 && args[0] == String.class && args[1] == String.class
                        && args[2] == String.class && args[3] == Optional.class && args[4] == Optional.class
                        && args[5].isEnum()) {
                    Object type = args[5].getEnumConstants()[0];
                    replacement = c.newInstance(name, offlineUuid(name).toString(), "offline", options[0], options[1], type);
                    break;
                }
            }
            if (replacement == null) {
                accountStatus = "\u00a7cUnsupported session API";
                return false;
            }
            boolean set = false;
            for (java.lang.reflect.Field f : mc.getClass().getDeclaredFields()) {
                if (!f.getType().isAssignableFrom(replacement.getClass())) continue;
                f.setAccessible(true);
                f.set(mc, replacement);
                set = true;
                break;
            }
            if (!set) {
                accountStatus = "\u00a7cSession field not found";
                return false;
            }
            selectedAccount = index;
            accountStatus = "\u00a7aLocal session: " + name + " \u00a77(reconnect to apply)";
            return true;
        } catch (Throwable t) {
            accountStatus = "\u00a7cSwitch failed";
            return false;
        }
    }

    public static void addAccount(String rawName) {
        String name = sanitizeEntry(rawName);
        if (name.isEmpty()) return;
        int existing = accountNames.indexOf(name);
        if (existing >= 0) {
            selectedAccount = existing;
            accountStatus = "\u00a7eAlready in list";
        } else {
            accountNames.add(name);
            selectedAccount = accountNames.size() - 1;
            accountStatus = "\u00a7aAdded: " + name;
        }
        saveConfig();
    }

    public static void removeAccount(int index) {
        if (index < 0 || index >= accountNames.size()) return;
        accountNames.remove(index);
        selectedAccount = Math.max(0, Math.min(selectedAccount, accountNames.size() - 1));
        accountStatus = "\u00a7eRemoved account";
        saveConfig();
    }

    public static void addBypassName(String rawName) {
        String name = sanitizeEntry(rawName);
        if (name.isEmpty()) return;
        if (!customBypassNames.contains(name)) {
            customBypassNames.add(name);
            saveConfig();
        }
    }

    public static void removeBypassName(String name) {
        if (customBypassNames.remove(name)) saveConfig();
    }

    private static Vec3d noclipMoveDelta(net.minecraft.client.network.ClientPlayerEntity p, double step) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double yaw = Math.toRadians(p.getYaw());
        double dx = 0, dy = 0, dz = 0;
        float fw = p.input.movementForward;
        float st = p.input.movementSideways;
        if (fw != 0 || st != 0) {
            double angle = yaw - Math.atan2(fw, st) + Math.PI / 2;
            dx = -Math.sin(angle) * step;
            dz = Math.cos(angle) * step;
        }
        if (mc.options.jumpKey.isPressed()) dy += step;
        if (mc.options.sneakKey.isPressed()) dy -= step;

        int dir = MathHelper.clamp(noclipDirection, 0, NOCLIP_DIRECTIONS.length - 1);
        if (dir == 1 || dir == 7) return new Vec3d(0, step, 0);
        if (dir == 2 || dir == 8) return new Vec3d(0, -step, 0);
        if (dir == 3) return new Vec3d(-Math.sin(yaw) * step, 0, Math.cos(yaw) * step);
        if (dir == 4) return new Vec3d(Math.sin(yaw) * step, 0, -Math.cos(yaw) * step);
        if (dir == 5) return new Vec3d(-Math.cos(yaw) * step, 0, -Math.sin(yaw) * step);
        if (dir == 6) return new Vec3d(Math.cos(yaw) * step, 0, Math.sin(yaw) * step);
        return new Vec3d(dx, dy, dz);
    }

    private static long chunkKey(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

    private static void scanChunks(MinecraftClient mc, net.minecraft.client.network.ClientPlayerEntity p) {
        if (mc.world == null) return;
        int px = p.getBlockPos().getX() >> 4;
        int pz = p.getBlockPos().getZ() >> 4;
        float scanRange = Math.max(baseFinderRange, xrayFarScan ? xrayRange : 0f);
        int span = MathHelper.clamp((int) (scanRange / 16f), 1, 40);
        int side = span * 2 + 1;
        int slot = scanCursor++ % (side * side);
        int cx = px - span + slot % side;
        int cz = pz - span + slot / side;

        ChunkScan scan = new ChunkScan();
        scan.cx = cx;
        scan.cz = cz;
        try {
            var chunk = mc.world.getChunk(cx, cz);
            for (int lx = 0; lx < 16; lx += 4) {
                for (int lz = 0; lz < 16; lz += 4) {
                    for (int y = 8; y < 132; y += 8) {
                        Block block = chunk.getBlockState(new net.minecraft.util.math.BlockPos(cx * 16 + lx, y, cz * 16 + lz)).getBlock();
                        String n = blockName(block);
                        if (xrayBlockMatches(block) && xrayMarks.size() < 400) {
                            xrayMarks.add(new net.minecraft.util.math.BlockPos(cx * 16 + lx, y, cz * 16 + lz));
                        }
                        if (n.contains("chest")) scan.chest++;
                        else if (n.contains("shulker")) scan.shulker++;
                        else if (n.contains("log") || n.contains("planks")) scan.wood++;
                        else if (n.contains("carpet")) scan.carpet++;
                        else if (n.contains("obsidian") || n.contains("spawner") || n.contains("beacon") || n.contains("enchanting")) scan.unusual++;
                    }
                }
            }
        } catch (Exception ignored) {
            return;
        }

        chunkScans.put(chunkKey(cx, cz), scan);
        if (chunkScans.size() > 512) chunkScans.clear();
        if (xrayMarks.size() > 400) xrayMarks.clear();
    }

    private static void emitBaseFinderGlow(MinecraftClient mc, net.minecraft.client.network.ClientPlayerEntity p) {
        if (mc.world == null || !baseFinderOn) return;
        for (ChunkScan scan : chunkScans.values()) {
            if (scan.score() < 10) continue;
            double dx = scan.cx * 16 + 8 - p.getX();
            double dz = scan.cz * 16 + 8 - p.getZ();
            if (Math.hypot(dx, dz) > baseFinderRange) continue;
            double wx = scan.cx * 16 + 8;
            double wz = scan.cz * 16 + 8;
            int glowCount = optimizationOn ? 1 : 3;
            for (int i = 0; i < glowCount; i++) {
                mc.world.addParticle(net.minecraft.particle.ParticleTypes.FLAME,
                        wx + (Math.random() - 0.5) * 6, p.getY() + 1 + i * 1.5, wz + (Math.random() - 0.5) * 6,
                        0, 0.01, 0);
            }
        }
    }

    private static void drawXrayRadar(DrawContext ctx, MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return;
        Vec3d camPos = camera.getPos();
        org.joml.Vector3f right = camera.getDiagonalPlane();
        org.joml.Vector3f up = camera.getVerticalPlane();
        org.joml.Vector3f forward = camera.getHorizontalPlane();
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double focal = (sh * 0.5) / Math.tan(Math.toRadians(fov) * 0.5);
        if (!Double.isFinite(focal) || focal <= 0) return;

        for (net.minecraft.util.math.BlockPos pos : new ArrayList<>(xrayMarks)) {
            double mdx = pos.getX() + 0.5 - mc.player.getX();
            double mdz = pos.getZ() + 0.5 - mc.player.getZ();
            if (Math.hypot(mdx, mdz) > xrayRange) continue;
            float[] s = projectToScreen(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                    camPos, right, up, forward, focal, sw, sh);
            if (s == null) continue;
            int x = MathHelper.clamp((int) s[0], 0, sw);
            int y = MathHelper.clamp((int) s[1], 0, sh);
            ctx.fill(x - 2, y - 2, x + 2, y + 2, 0x5500FFFF);
            ctx.fill(x - 1, y - 1, x + 1, y + 1, 0xAA00FFFF);
        }
    }

    private static void drawBaseFinderRadar(DrawContext ctx, MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return;
        Vec3d camPos = camera.getPos();
        org.joml.Vector3f right = camera.getDiagonalPlane();
        org.joml.Vector3f up = camera.getVerticalPlane();
        org.joml.Vector3f forward = camera.getHorizontalPlane();
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double focal = (sh * 0.5) / Math.tan(Math.toRadians(fov) * 0.5);
        if (!Double.isFinite(focal) || focal <= 0) return;

        int renderMode = MathHelper.clamp(baseFinderRender, 0, BASE_FINDER_RENDERS.length - 1);
        int filterMode = MathHelper.clamp(baseFinderMode, 0, BASE_FINDER_MODES.length - 1);
        for (ChunkScan scan : chunkScans.values()) {
            int score = scan.score();
            if (score < 6) continue;
            double dx = scan.cx * 16 + 8 - mc.player.getX();
            double dz = scan.cz * 16 + 8 - mc.player.getZ();
            if (Math.hypot(dx, dz) > baseFinderRange) continue;
            float[] s = projectToScreen(new Vec3d(scan.cx * 16 + 8, mc.player.getY() + 2, scan.cz * 16 + 8),
                    camPos, right, up, forward, focal, sw, sh);
            if (s == null) continue;
            int x = MathHelper.clamp((int) s[0], 0, sw);
            int y = MathHelper.clamp((int) s[1], 0, sh);

            int color = switch (filterMode) {
                case 1 -> 0x55FFFF00;
                case 2 -> 0x5500FF55;
                case 3 -> 0x55FF00FF;
                case 4 -> 0x55FF7700;
                default -> score >= 18 ? 0x55FFFF00 : 0x5555AAFF;
            };

            if (renderMode == 0) {
                ctx.fill(x - 2, y - 2, x + 2, y + 2, color);
            } else if (renderMode == 1) {
                ctx.fill(x - 10, y - 10, x + 10, y - 9, color);
                ctx.fill(x - 10, y + 9, x + 10, y + 10, color);
                ctx.fill(x - 10, y - 10, x - 9, y + 10, color);
                ctx.fill(x + 9, y - 10, x + 10, y + 10, color);
            } else if (renderMode == 2) {
                ctx.fill(x - 14, y - 14, x + 14, y + 14, (color & 0x00FFFFFF) | 0x11000000);
                ctx.fill(x - 8, y - 8, x + 8, y + 8, (color & 0x00FFFFFF) | 0x33000000);
                ctx.fill(x - 2, y - 2, x + 2, y + 2, color);
            } else {
                int half = Math.min(18, 6 + score / 3);
                ctx.fill(x - half, y - half, x + half, y + half, (color & 0x00FFFFFF) | 0x22000000);
            }

            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a7e" + score), x - 8, y - 22, 0xFFFFFF);
        }
    }

    private static void drawFreezeVisual(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return;
        Vec3d camPos = camera.getPos();
        org.joml.Vector3f right = camera.getDiagonalPlane();
        org.joml.Vector3f up = camera.getVerticalPlane();
        org.joml.Vector3f forward = camera.getHorizontalPlane();
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double focal = (sh * 0.5) / Math.tan(Math.toRadians(fov) * 0.5);
        if (!Double.isFinite(focal) || focal <= 0) return;
        float[] s = projectToScreen(new Vec3d(mc.player.getX(), mc.player.getY() + 1, mc.player.getZ()),
                camPos, right, up, forward, focal, sw, sh);
        if (s == null) return;
        int x = (int) s[0], y = (int) s[1];
        int color = freezeMode == 6 ? 0x55AA00FF : 0x5500FFFF;
        ctx.fill(x - 10, y - 10, x + 10, y - 9, color);
        ctx.fill(x - 10, y + 9, x + 10, y + 10, color);
        ctx.fill(x - 10, y - 10, x - 9, y + 10, color);
        ctx.fill(x + 9, y - 10, x + 10, y + 10, color);
    }

    private static void drawCrasherVisual(DrawContext ctx, MinecraftClient mc) {
        int w = mc.getWindow().getScaledWidth();
        ctx.fill(10, 10, 210, 24, 0x88000000);
        ctx.fill(10, 10, 10 + MathHelper.clamp((int) (crasherScore * 8), 0, 200), 24, 0x55FF5555);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("\u00a7cLOCAL BENCH \u00a77" + String.format("%.3f", crasherScore)), 12, 13, 0xFFFFFF);
    }

    private static void runCrasherBenchmark() {
        int mode = MathHelper.clamp(crasherMode, 0, CRASHER_MODES.length - 1);
        int iterations = 2000 + mode * 500;
        double acc = 0.0;
        for (int i = 0; i < iterations; i++) {
            double a = Math.sin(i * 0.017 + mode) + Math.cos(i * 0.031 - mode);
            double b = Math.sqrt(Math.abs(a * a + 1.0));
            acc += Math.log1p(b) * Math.tanh(a);
        }
        crasherScore = acc / iterations;
    }

    private static void creativeDropItems(MinecraftClient mc, net.minecraft.client.network.ClientPlayerEntity p) {
        if (!p.isCreative() || p.isSpectator() || mc.interactionManager == null) return;
        int count = MathHelper.clamp(creativeDropCount, 1, 64);
        for (int i = 9; i < 45; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, i, 1,
                    net.minecraft.screen.slot.SlotActionType.THROW, p);
            if (count > 1) {
                ItemStack extra = stack.copy();
                extra.setCount(Math.min(count, 64));
                p.getInventory().setStack(i, extra);
            }
        }
    }

    private static void creativeDupeItems(net.minecraft.client.network.ClientPlayerEntity p) {
        if (!p.isCreative() || p.isSpectator()) return;
        int copies = MathHelper.clamp(creativeDupeCount, 1, 20);
        int mode = MathHelper.clamp(dupeMode, 0, DUPE_MODES.length - 1);
        ItemStack source = ItemStack.EMPTY;
        int sourceSlot = -1;
        if (mode == 3 || mode == 4) {
            source = p.getMainHandStack().copy();
        } else {
            for (int i = 9; i < 45; i++) {
                ItemStack stack = p.getInventory().getStack(i);
                if (!stack.isEmpty()) {
                    source = stack.copy();
                    sourceSlot = i;
                    break;
                }
            }
        }
        if (mode == 2) {
            for (int type = 0; type < 4; type++) {
                ItemStack armor = p.getInventory().getArmorStack(type);
                if (!armor.isEmpty()) source = armor.copy();
            }
        }
        if (source.isEmpty()) return;
        int placed = 0;
        for (int i = 9; i < 45 && placed < copies; i++) {
            if (mode == 1 && i == sourceSlot) continue;
            if (!p.getInventory().getStack(i).isEmpty()) continue;
            ItemStack copy = source.copy();
            if ((mode == 1 || mode == 4) && sourceSlot >= 0 && p.getInventory().getStack(sourceSlot).getCount() > 1) {
                ItemStack original = p.getInventory().getStack(sourceSlot);
                int half = Math.max(1, original.getCount() / 2);
                copy.setCount(half);
                original.setCount(original.getCount() - half);
            } else if (mode == 5) {
                int hotbarSlot = placed % 9;
                ItemStack hot = p.getInventory().getStack(hotbarSlot);
                if (!hot.isEmpty()) copy = hot.copy();
            }
            p.getInventory().setStack(i, copy);
            placed++;
        }
    }

    public static void saveAuraCustom() {
        auraCustomSaved = true;
        auraCustomMode = auraMode;
        auraCustomRotMode = auraRotMode;
        auraCustomBypass = auraBypass;
        auraCustomRange = auraRange;
        auraCustomFov = auraFov;
        auraCustomRotSpeed = auraRotSpeed;
        auraCustomRotation = auraRotationEnabled;
        auraCustomStrafe = auraStrafeEnabled;
        auraCustomNotNear = auraNotNear;
        auraCustomNotNearRange = auraNotNearRange;
    }

    public static void loadAuraCustom() {
        if (!auraCustomSaved) return;
        auraMode = auraCustomMode;
        auraRotMode = auraCustomRotMode;
        auraBypass = auraCustomBypass;
        auraRange = auraCustomRange;
        auraFov = auraCustomFov;
        auraRotSpeed = auraCustomRotSpeed;
        auraRotationEnabled = auraCustomRotation;
        auraStrafeEnabled = auraCustomStrafe;
        auraNotNear = auraCustomNotNear;
        auraNotNearRange = auraCustomNotNearRange;
    }

    private static LivingEntity findAimTarget(MinecraftClient mc, net.minecraft.client.network.ClientPlayerEntity p) {
        if (mc.world == null) return null;
        var list = mc.world.getOtherEntities(p, p.getBoundingBox().expand(aimRange), e -> {
            if (!(e instanceof LivingEntity living) || !living.isAlive() || e == p || e.isSpectator()) return false;
            if (friends.contains(e.getName().getString())) return false;

            boolean isPlayer = e instanceof net.minecraft.entity.player.PlayerEntity;
            boolean isMob = e instanceof net.minecraft.entity.mob.HostileEntity;
            boolean isAnimal = e instanceof net.minecraft.entity.passive.AnimalEntity;
            if (isPlayer && !aimTargetPlayers) return false;
            if (isMob && !aimTargetMobs) return false;
            if (isAnimal && !aimTargetAnimals) return false;

            if (aimFov < 360f) {
                float[] rots = getRotations(p, e, false);
                if (Math.abs(MathHelper.wrapDegrees(p.getYaw() - rots[0])) > aimFov / 2f) return false;
            }
            return true;
        });
        if (list.isEmpty()) return null;
        return switch (MathHelper.clamp(aimTargetMode, 0, AURA_TARGETS.length - 1)) {
            case 1 -> list.stream().min(Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth())).map(e -> (LivingEntity) e).orElse(null);
            case 2 -> list.stream().min(Comparator.comparingDouble(e -> ((LivingEntity) e).getArmor())).map(e -> (LivingEntity) e).orElse(null);
            case 3 -> list.stream().min(Comparator.comparingDouble(e -> getAngleDifference(p, e))).map(e -> (LivingEntity) e).orElse(null);
            default -> list.stream().min(Comparator.comparingDouble(e -> e.distanceTo(p))).map(e -> (LivingEntity) e).orElse(null);
        };
    }

    private static float[] getRotations(Entity p, Entity target) {
        return getRotations(p, target, true);
    }

    private static float[] getRotations(Entity p, Entity target, boolean applyAuraJitter) {
        double targetX = target.getX();
        double targetZ = target.getZ();
        double targetY = target.getY();

        if (auraPredict) {
            Vec3d v = target.getVelocity();
            targetX += v.x * 2.0;
            targetZ += v.z * 2.0;
        }

        switch (auraTargetPart) {
            case 0 -> targetY += target.getEyeHeight(target.getPose()); // Head
            case 1 -> targetY += target.getHeight() * 0.6; // Chest
            case 2 -> targetY += target.getHeight() * 0.2; // Legs
            case 3 -> {} // Feet
        }

        if (applyAuraJitter && (auraAimJitter > 0 || auraRandomize)) {
            double jitter = auraRandomize ? Math.max(auraAimJitter, 0.1) : auraAimJitter;
            jitter *= 1f - MathHelper.clamp(auraSmoothing, 0f, 0.95f);
            targetX += (Math.random() - 0.5) * jitter;
            targetY += (Math.random() - 0.5) * jitter;
            targetZ += (Math.random() - 0.5) * jitter;
        }

        double dx = targetX - p.getX();
        double dz = targetZ - p.getZ();
        double dy = targetY - (p.getY() + p.getEyeHeight(p.getPose()));
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        // GCD Fix
        float f = MinecraftClient.getInstance().options.getMouseSensitivity().getValue().floatValue() * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new float[]{yaw, pitch};
    }

    private static int getArmorScore(net.minecraft.item.ItemStack stack) {
        if (stack.getItem() instanceof net.minecraft.item.ArmorItem armor) {
            int score = armor.getProtection();
            score += net.minecraft.enchantment.EnchantmentHelper.getLevel(net.minecraft.enchantment.Enchantments.PROTECTION, stack);
            return score;
        }
        return -1;
    }

    private static int findBestWeapon(net.minecraft.client.network.ClientPlayerEntity p, boolean preferAxe) {
        int bestSlot = -1;
        float bestDamage = -1f;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            float damage;
            if (stack.getItem() instanceof SwordItem sword) {
                damage = sword.getAttackDamage();
            } else if (stack.getItem() instanceof AxeItem axe) {
                damage = axe.getAttackDamage();
            } else {
                continue;
            }
            if (preferAxe && stack.getItem() instanceof AxeItem) damage += 1000f;
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private static boolean isValidTriggerTarget(Entity e, net.minecraft.client.network.ClientPlayerEntity p, MinecraftClient mc) {
        if (!(e instanceof LivingEntity living) || !living.isAlive() || e == p || e.isSpectator()) return false;
        if (friends.contains(e.getName().getString())) return false;
        if (p.distanceTo(e) > triggerRange) return false;

        boolean isPlayer = e instanceof net.minecraft.entity.player.PlayerEntity;
        boolean isMob = e instanceof net.minecraft.entity.mob.HostileEntity;
        boolean isAnimal = e instanceof net.minecraft.entity.passive.AnimalEntity;
        if (isPlayer && !triggerTargetPlayers) return false;
        if (isMob && !triggerTargetMobs) return false;
        if (isAnimal && !triggerTargetAnimals) return false;

        if (antiBotOn && isPlayer && (e.age < 20 || e.getName().getString().isEmpty() || e.getName().getString().contains(" "))) return false;
        if (!triggerThroughWalls && !p.canSee(e)) return false;
        return true;
    }

    private static int getHealthColor(float hp, float maxHp) {
        float ratio = hp / maxHp;
        if (ratio > 0.75f) return 0xFF00FF00; // Green
        if (ratio > 0.5f) return 0xFFFFFF00;  // Yellow
        if (ratio > 0.25f) return 0xFFFF8800; // Orange
        return 0xFFFF0000;                    // Red
    }

    private static double getAngleDifference(Entity p, Entity e) {
        float[] rots = getRotations(p, e, false);
        return Math.abs(MathHelper.wrapDegrees(p.getYaw() - rots[0]));
    }

    private static float[] projectToScreen(Vec3d pos, Vec3d camPos, org.joml.Vector3f right,
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

    private static void drawTargetMarker(DrawContext ctx, MinecraftClient mc) {
        if (mc.world == null || currentTarget == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return;

        Vec3d camPos = camera.getPos();
        org.joml.Vector3f right = camera.getDiagonalPlane();
        org.joml.Vector3f up = camera.getVerticalPlane();
        org.joml.Vector3f forward = camera.getHorizontalPlane();
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double halfFov = Math.toRadians(fov) * 0.5;
        double focal = (sh * 0.5) / Math.tan(halfFov);
        if (!Double.isFinite(focal) || focal <= 0 || halfFov <= 0 || halfFov >= Math.PI * 0.5) return;

        double cx = currentTarget.getX();
        double cy = currentTarget.getY();
        double cz = currentTarget.getZ();
        double hw = currentTarget.getWidth() * 0.6;
        double height = currentTarget.getHeight();
        float[][] corners = new float[8][];
        int n = 0;
        for (double ox : new double[]{-hw, hw}) {
            for (double oz : new double[]{-hw, hw}) {
                for (double oy : new double[]{0, height}) {
                    corners[n++] = projectToScreen(new Vec3d(cx + ox, cy + oy, cz + oz), camPos, right, up, forward, focal, sw, sh);
                }
            }
        }
        int color = 0xFF00FFFF;
        int[][] edges = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {2, 6}, {6, 4}, {4, 0}, {1, 3}, {3, 7}, {7, 5}, {5, 1}};
        for (int[] edge : edges) {
            float[] a = corners[edge[0]];
            float[] b = corners[edge[1]];
            if (a != null && b != null) drawEspLine(ctx, (int) a[0], (int) a[1], (int) b[0], (int) b[1], color);
        }
        float[] top = corners[7];
        if (top != null) {
            String text = "\u00a7b" + currentTarget.getName().getString() + " \u00a7f" + String.format("%.1f", currentTarget.getHealth()) + "HP";
            int tw = mc.textRenderer.getWidth(Text.literal(text));
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(text), (int) top[0] - tw / 2, (int) top[1] - 16, 0xFFFFFF);
        }
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

    private static int espColor(Entity e) {
        if (e == currentTarget) return 0xFFFFFF00;
        if (e instanceof net.minecraft.entity.player.PlayerEntity) {
            return friends.contains(e.getName().getString()) ? 0xFF55FF55 : 0xFFFF5555;
        }
        if (e instanceof ItemEntity) return 0xFF55FFFF;
        if (e instanceof net.minecraft.entity.mob.HostileEntity) return 0xFFFF8800;
        return 0xFF55FF55;
    }

    private static void drawEsp(DrawContext ctx, MinecraftClient mc, float tickDelta) {
        if (!espOn || mc.world == null || mc.player == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (!camera.isReady()) return;

        Vec3d camPos = camera.getPos();
        org.joml.Vector3f right = camera.getDiagonalPlane();
        org.joml.Vector3f up = camera.getVerticalPlane();
        org.joml.Vector3f forward = camera.getHorizontalPlane();

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        double fov = mc.options.getFov().getValue().floatValue();
        double halfFov = Math.toRadians(fov) * 0.5;
        double focal = (sh * 0.5) / Math.tan(halfFov);
        if (!Double.isFinite(focal) || focal <= 0 || halfFov <= 0 || halfFov >= Math.PI * 0.5) return;

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || e.isSpectator()) continue;

            boolean isItem = e instanceof ItemEntity;
            if (!isItem && !(e instanceof LivingEntity)) continue;
            if (e instanceof LivingEntity && !((LivingEntity) e).isAlive()) continue;

            if (isItem) {
                if (!espItemsOn) continue;
            } else {
                boolean isPlayer = e instanceof net.minecraft.entity.player.PlayerEntity;
                boolean isMob = e instanceof net.minecraft.entity.mob.HostileEntity;
                boolean isAnimal = e instanceof net.minecraft.entity.passive.AnimalEntity;
                if (isPlayer && !espPlayersOn) continue;
                if (isMob && !espMobsOn) continue;
                if (isAnimal && !espAnimalsOn) continue;
            }

            double dist = mc.player.distanceTo(e);
            if (dist > espRange) continue;

            Box box = e.getBoundingBox();
            double ox = MathHelper.lerp(tickDelta, e.prevX, e.getX()) - e.getX();
            double oy = MathHelper.lerp(tickDelta, e.prevY, e.getY()) - e.getY();
            double oz = MathHelper.lerp(tickDelta, e.prevZ, e.getZ()) - e.getZ();
            double minX = box.minX + ox, minY = box.minY + oy, minZ = box.minZ + oz;
            double maxX = box.maxX + ox, maxY = box.maxY + oy, maxZ = box.maxZ + oz;

            float minSx = Float.POSITIVE_INFINITY, minSy = Float.POSITIVE_INFINITY;
            float maxSx = Float.NEGATIVE_INFINITY, maxSy = Float.NEGATIVE_INFINITY;
            boolean fullyVisible = true;

            for (int i = 0; i < 8; i++) {
                double px = (i & 1) == 0 ? minX : maxX;
                double py = (i & 2) == 0 ? minY : maxY;
                double pz = (i & 4) == 0 ? minZ : maxZ;
                float[] s = projectToScreen(new Vec3d(px, py, pz), camPos, right, up, forward, focal, sw, sh);
                if (s == null) { fullyVisible = false; break; }
                minSx = Math.min(minSx, s[0]);
                minSy = Math.min(minSy, s[1]);
                maxSx = Math.max(maxSx, s[0]);
                maxSy = Math.max(maxSy, s[1]);
            }
            if (!fullyVisible || maxSx < minSx || maxSy < minSy) continue;

            int x1 = MathHelper.clamp((int) minSx, 0, sw);
            int y1 = MathHelper.clamp((int) minSy, 0, sh);
            int x2 = MathHelper.clamp((int) maxSx, 0, sw);
            int y2 = MathHelper.clamp((int) maxSy, 0, sh);
            if (x2 - x1 < 2 || y2 - y1 < 2) continue;

            int color = espColor(e);
            if (espFillOn) ctx.fill(x1, y1, x2, y2, (color & 0x00FFFFFF) | 0x22000000);

            if (espBoxesOn || isItem) {
                ctx.fill(x1, y1, x2, y1 + 1, color);
                ctx.fill(x1, y2 - 1, x2, y2, color);
                ctx.fill(x1, y1, x1 + 1, y2, color);
                ctx.fill(x2 - 1, y1, x2, y2, color);
            }

            if (espTracersOn) {
                drawEspLine(ctx, sw / 2, sh - 1, (x1 + x2) / 2, (y1 + y2) / 2, (color & 0x00FFFFFF) | 0x88000000);
            }

            String label;
            if (isItem) {
                ItemStack stack = ((ItemEntity) e).getStack();
                label = stack.getName().getString() + " x" + stack.getCount();
            } else {
                label = PypsenFeatures.label(e.getName().getString(), "Entity");
            }

            int textY = Math.max(2, y1 - 11);
            if (espNamesOn) {
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal(label), x1, textY, color);
                textY -= 10;
            }
            if (espDistanceOn) {
                ctx.drawTextWithShadow(mc.textRenderer, Text.literal(String.format("%.1fm", dist)), x2 + 3, y1, 0xFFAAAAAA);
            }
            if (espHealthOn && e instanceof LivingEntity living) {
                float ratio = living.getMaxHealth() > 0 ? living.getHealth() / living.getMaxHealth() : 0f;
                ratio = MathHelper.clamp(ratio, 0f, 1f);
                int barW = Math.max(1, x2 - x1);
                ctx.fill(x1, y2 + 2, x1 + barW, y2 + 5, 0xFF333333);
                ctx.fill(x1, y2 + 2, x1 + (int) (barW * ratio), y2 + 5, getHealthColor(living.getHealth(), living.getMaxHealth()));
            }
        }
    }

    private static final Set<Integer> pressedKeys = new HashSet<>();
    private static boolean wasPressed(long handle, int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return false;
        boolean down = GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
        if (down && pressedKeys.add(key)) return true;
        if (!down) pressedKeys.remove(key);
        return false;
    }

    public static String keyName(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "NONE";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();
        return switch (key) {
            case GLFW.GLFW_KEY_MINUS       -> "MINUS";
            case GLFW.GLFW_KEY_LEFT_SHIFT  -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL-> "LCTRL";
            case GLFW.GLFW_KEY_SPACE       -> "SPACE";
            case GLFW.GLFW_KEY_TAB         -> "TAB";
            default -> "KEY_" + key;
        };
    }

    // ══════════ CONFIG SYSTEM ══════════
    public static void saveConfig() {
        try {
            File file = new File(MinecraftClient.getInstance().runDirectory, "pypsen_config.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("speedVal=" + speedVal + "\n");
            writer.write("auraRange=" + auraRange + "\n");
            writer.write("hitboxVal=" + hitboxVal + "\n");
            writer.write("flySpeed=" + flySpeed + "\n");
            writer.write("clickerCps=" + clickerCps + "\n");
            writer.write("triggerCps=" + triggerCps + "\n");
            writer.write("auraMode=" + auraMode + "\n");
            writer.write("auraBypass=" + auraBypass + "\n");
            writer.write("speedBypass=" + speedBypass + "\n");
            writer.write("flyBypass=" + flyBypass + "\n");
            writer.write("auraRotMode=" + auraRotMode + "\n");
            writer.write("auraTargetMode=" + auraTargetMode + "\n");
            writer.write("auraTimingMode=" + auraTimingMode + "\n");
            writer.write("auraTargetPart=" + auraTargetPart + "\n");
            writer.write("visualsTargetMode=" + visualsTargetMode + "\n");
            writer.write("auraAimJitter=" + auraAimJitter + "\n");
            writer.write("auraSmoothing=" + auraSmoothing + "\n");
            writer.write("auraStickiness=" + auraStickiness + "\n");
            writer.write("auraRotCurve=" + auraRotCurve + "\n");
            writer.write("auraRotSpeed=" + auraRotSpeed + "\n");
            writer.write("auraFov=" + auraFov + "\n");
            writer.write("triggerRange=" + triggerRange + "\n");
            writer.write("auraTargetPlayers=" + auraTargetPlayers + "\n");
            writer.write("auraTargetMobs=" + auraTargetMobs + "\n");
            writer.write("auraTargetAnimals=" + auraTargetAnimals + "\n");
            writer.write("auraNotNear=" + auraNotNear + "\n");
            writer.write("auraNotNearRange=" + auraNotNearRange + "\n");
            writer.write("auraRotationEnabled=" + auraRotationEnabled + "\n");
            writer.write("auraStrafeEnabled=" + auraStrafeEnabled + "\n");
            writer.write("auraPredict=" + auraPredict + "\n");
            writer.write("auraRandomize=" + auraRandomize + "\n");
            writer.write("auraRayCast=" + auraRayCast + "\n");
            writer.write("auraCustomSaved=" + auraCustomSaved + "\n");
            writer.write("auraCustomMode=" + auraCustomMode + "\n");
            writer.write("auraCustomRotMode=" + auraCustomRotMode + "\n");
            writer.write("auraCustomBypass=" + auraCustomBypass + "\n");
            writer.write("auraCustomRange=" + auraCustomRange + "\n");
            writer.write("auraCustomFov=" + auraCustomFov + "\n");
            writer.write("auraCustomRotSpeed=" + auraCustomRotSpeed + "\n");
            writer.write("auraCustomRotation=" + auraCustomRotation + "\n");
            writer.write("auraCustomStrafe=" + auraCustomStrafe + "\n");
            writer.write("auraCustomNotNear=" + auraCustomNotNear + "\n");
            writer.write("auraCustomNotNearRange=" + auraCustomNotNearRange + "\n");
            writer.write("autoWeapon=" + autoWeapon + "\n");
            writer.write("criticals=" + criticals + "\n");
            writer.write("auraThroughWalls=" + auraThroughWalls + "\n");
            writer.write("auraHitInGuis=" + auraHitInGuis + "\n");
            writer.write("auraSpinBot=" + auraSpinBot + "\n");
            writer.write("triggerWeaponOnly=" + triggerWeaponOnly + "\n");
            writer.write("autoTotemOn=" + autoTotemOn + "\n");
            writer.write("autoTotemHealth=" + autoTotemHealth + "\n");
            writer.write("autoTotemMode=" + autoTotemMode + "\n");
            writer.write("aimLockOn=" + aimLockOn + "\n");
            writer.write("aimOn=" + aimOn + "\n");
            writer.write("aimSpeed=" + aimSpeed + "\n");
            writer.write("aimMode=" + aimMode + "\n");
            writer.write("aimRange=" + aimRange + "\n");
            writer.write("aimFov=" + aimFov + "\n");
            writer.write("aimJitter=" + aimJitter + "\n");
            writer.write("aimTargetMode=" + aimTargetMode + "\n");
            writer.write("aimTargetPlayers=" + aimTargetPlayers + "\n");
            writer.write("aimTargetMobs=" + aimTargetMobs + "\n");
            writer.write("aimTargetAnimals=" + aimTargetAnimals + "\n");
            writer.write("antiBotOn=" + antiBotOn + "\n");
            writer.write("fullBrightOn=" + fullBrightOn + "\n");
            writer.write("nameHiderOn=" + nameHiderOn + "\n");
            writer.write("spiderOn=" + spiderOn + "\n");
            writer.write("spiderMode=" + spiderMode + "\n");
            writer.write("spiderSpeed=" + spiderSpeed + "\n");
            writer.write("noWebOn=" + noWebOn + "\n");
            writer.write("noWebMode=" + noWebMode + "\n");
            writer.write("noclipOn=" + noclipOn + "\n");
            writer.write("noclipBypass=" + noclipBypass + "\n");
            writer.write("noclipStep=" + noclipStep + "\n");
            writer.write("noclipDirection=" + noclipDirection + "\n");
            writer.write("freezeOn=" + freezeOn + "\n");
            writer.write("freezeMode=" + freezeMode + "\n");
            writer.write("freezeVisual=" + freezeVisual + "\n");
            writer.write("airJumpOn=" + airJumpOn + "\n");
            writer.write("airJumpMode=" + airJumpMode + "\n");
            writer.write("airJumpSpeed=" + airJumpSpeed + "\n");
            writer.write("noFallOn=" + noFallOn + "\n");
            writer.write("noFallMode=" + noFallMode + "\n");
            writer.write("velocityMode=" + velocityMode + "\n");
            writer.write("jesusOn=" + jesusOn + "\n");
            writer.write("jesusMode=" + jesusMode + "\n");
            writer.write("visualsParticles=" + visualsParticles + "\n");
            writer.write("ambientOn=" + ambientOn + "\n");
            writer.write("ambientRainbow=" + ambientRainbow + "\n");
            writer.write("ambientMode=" + ambientMode + "\n");
            writer.write("ambientStrength=" + ambientStrength + "\n");
            writer.write("ambientHue=" + ambientHue + "\n");
            writer.write("spinOn=" + spinOn + "\n");
            writer.write("spinMode=" + spinMode + "\n");
            writer.write("spinSpeed=" + spinSpeed + "\n");
            writer.write("spinSize=" + spinSize + "\n");
            writer.write("optimizationOn=" + optimizationOn + "\n");
            writer.write("optProfile=" + optProfile + "\n");
            writer.write("hitboxFix=" + hitboxFix + "\n");
            writer.write("xrayStealth=" + xrayStealth + "\n");
            writer.write("xrayHighlight=" + xrayHighlight + "\n");
            writer.write("flyCustomVertical=" + flyCustomVertical + "\n");
            writer.write("flyCustomPulse=" + flyCustomPulse + "\n");
            writer.write("spiderCustomPulse=" + spiderCustomPulse + "\n");
            for (String name : customBypassNames) writer.write("customBypass=" + sanitizeEntry(name) + "\n");
            writer.write("selectedAccount=" + selectedAccount + "\n");
            for (String name : accountNames) writer.write("accountName=" + sanitizeEntry(name) + "\n");
            writer.write("waterCrits=" + waterCrits + "\n");
            writer.write("crasherTimerMode=" + crasherTimerMode + "\n");
            writer.write("crasherMode=" + crasherMode + "\n");
            writer.write("crasherVisual=" + crasherVisual + "\n");
            writer.write("serverCrasherOn=" + serverCrasherOn + "\n");
            writer.write("xrayOn=" + xrayOn + "\n");
            writer.write("xrayMode=" + xrayMode + "\n");
            writer.write("xrayBypass=" + xrayBypass + "\n");
            writer.write("xrayRange=" + xrayRange + "\n");
            writer.write("xrayFarScan=" + xrayFarScan + "\n");
            writer.write("xrayAntiXray=" + xrayAntiXray + "\n");
            writer.write("xrayBlocks=" + xrayBlockNames() + "\n");
            writer.write("baseFinderOn=" + baseFinderOn + "\n");
            writer.write("baseFinderMode=" + baseFinderMode + "\n");
            writer.write("baseFinderRender=" + baseFinderRender + "\n");
            writer.write("baseFinderRange=" + baseFinderRange + "\n");
            writer.write("baseFinderChest=" + baseFinderChest + "\n");
            writer.write("baseFinderShulker=" + baseFinderShulker + "\n");
            writer.write("baseFinderWood=" + baseFinderWood + "\n");
            writer.write("baseFinderCarpet=" + baseFinderCarpet + "\n");
            writer.write("baseFinderUnusual=" + baseFinderUnusual + "\n");
            writer.write("creativeDropCount=" + creativeDropCount + "\n");
            writer.write("creativeDupeCount=" + creativeDupeCount + "\n");
            writer.write("dupeMode=" + dupeMode + "\n");
            writer.write("godModeMode=" + godModeMode + "\n");
            writer.write("seeInvisOn=" + seeInvisOn + "\n");
            writer.write("noFireOn=" + noFireOn + "\n");
            writer.write("noNauseaOn=" + noNauseaOn + "\n");
            writer.write("targetEspOn=" + targetEspOn + "\n");
            writer.write("noHurtCamOn=" + noHurtCamOn + "\n");
            writer.write("playerEspOn=" + playerEspOn + "\n");
            writer.write("shieldBreaker=" + shieldBreaker + "\n");
            writer.write("keyEsp=" + keyEsp + "\n");
            writer.write("hudOn=" + hudOn + "\n");
            writer.write("targetHudOn=" + targetHudOn + "\n");
            writer.write("fakeName=" + fakeName + "\n");
            writer.write("useModernGui=" + useModernGui + "\n");
            writer.write("espOn=" + espOn + "\n");
            writer.write("espBoxesOn=" + espBoxesOn + "\n");
            writer.write("espNamesOn=" + espNamesOn + "\n");
            writer.write("espHealthOn=" + espHealthOn + "\n");
            writer.write("espDistanceOn=" + espDistanceOn + "\n");
            writer.write("espItemsOn=" + espItemsOn + "\n");
            writer.write("espTracersOn=" + espTracersOn + "\n");
            writer.write("espFillOn=" + espFillOn + "\n");
            writer.write("espPlayersOn=" + espPlayersOn + "\n");
            writer.write("espMobsOn=" + espMobsOn + "\n");
            writer.write("espAnimalsOn=" + espAnimalsOn + "\n");
            writer.write("espRange=" + espRange + "\n");
            writer.write("triggerThroughWalls=" + triggerThroughWalls + "\n");
            writer.write("triggerSmartDelay=" + triggerSmartDelay + "\n");
            writer.write("triggerTargetPlayers=" + triggerTargetPlayers + "\n");
            writer.write("triggerTargetMobs=" + triggerTargetMobs + "\n");
            writer.write("triggerTargetAnimals=" + triggerTargetAnimals + "\n");
            writer.write("triggerAutoWeapon=" + triggerAutoWeapon + "\n");
            writer.write("triggerCriticals=" + triggerCriticals + "\n");
            writer.write("triggerWaitHurtTime=" + triggerWaitHurtTime + "\n");
            writer.write("triggerCooldown=" + triggerCooldown + "\n");
            writer.write("triggerJitter=" + triggerJitter + "\n");
            writer.write("triggerFov=" + triggerFov + "\n");
            writer.write("friends=" + String.join(",", friends) + "\n");
            PypsenFeatures.save(writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            File file = new File(MinecraftClient.getInstance().runDirectory, "pypsen_config.txt");
            if (!file.exists()) return;
            List<String> lines = Files.readAllLines(file.toPath());
            customBypassNames.clear();
            accountNames.clear();
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length < 2) continue;
                String key = parts[0];
                String val = parts[1];
                switch (key) {
                    case "speedVal" -> speedVal = Float.parseFloat(val);
                    case "auraRange" -> auraRange = Float.parseFloat(val);
                    case "hitboxVal" -> hitboxVal = Float.parseFloat(val);
                    case "flySpeed" -> flySpeed = Float.parseFloat(val);
                    case "clickerCps" -> clickerCps = Float.parseFloat(val);
                    case "triggerCps" -> triggerCps = Float.parseFloat(val);
                    case "auraMode" -> auraMode = Integer.parseInt(val);
                    case "auraBypass" -> auraBypass = Integer.parseInt(val);
                    case "speedBypass" -> speedBypass = Integer.parseInt(val);
                    case "flyBypass" -> flyBypass = Integer.parseInt(val);
                    case "auraRotMode" -> auraRotMode = Integer.parseInt(val);
                    case "auraTargetMode" -> auraTargetMode = Integer.parseInt(val);
                    case "auraTimingMode" -> auraTimingMode = Integer.parseInt(val);
                    case "auraTargetPart" -> auraTargetPart = Integer.parseInt(val);
                    case "visualsTargetMode" -> visualsTargetMode = Integer.parseInt(val);
                    case "auraAimJitter" -> auraAimJitter = Float.parseFloat(val);
                    case "auraSmoothing" -> auraSmoothing = Float.parseFloat(val);
                    case "auraStickiness" -> auraStickiness = Float.parseFloat(val);
                    case "auraRotCurve" -> auraRotCurve = Float.parseFloat(val);
                    case "auraRotSpeed" -> auraRotSpeed = Float.parseFloat(val);
                    case "auraFov" -> auraFov = Float.parseFloat(val);
                    case "triggerRange" -> triggerRange = Float.parseFloat(val);
                    case "auraTargetPlayers" -> auraTargetPlayers = Boolean.parseBoolean(val);
                    case "auraTargetMobs" -> auraTargetMobs = Boolean.parseBoolean(val);
                    case "auraTargetAnimals" -> auraTargetAnimals = Boolean.parseBoolean(val);
                    case "auraNotNear" -> auraNotNear = Boolean.parseBoolean(val);
                    case "auraNotNearRange" -> auraNotNearRange = Float.parseFloat(val);
                    case "auraRotationEnabled" -> auraRotationEnabled = Boolean.parseBoolean(val);
                    case "auraStrafeEnabled" -> auraStrafeEnabled = Boolean.parseBoolean(val);
                    case "auraPredict" -> auraPredict = Boolean.parseBoolean(val);
                    case "auraRandomize" -> auraRandomize = Boolean.parseBoolean(val);
                    case "auraRayCast" -> auraRayCast = Boolean.parseBoolean(val);
                    case "auraCustomSaved" -> auraCustomSaved = Boolean.parseBoolean(val);
                    case "auraCustomMode" -> auraCustomMode = Integer.parseInt(val);
                    case "auraCustomRotMode" -> auraCustomRotMode = Integer.parseInt(val);
                    case "auraCustomBypass" -> auraCustomBypass = Integer.parseInt(val);
                    case "auraCustomRange" -> auraCustomRange = Float.parseFloat(val);
                    case "auraCustomFov" -> auraCustomFov = Float.parseFloat(val);
                    case "auraCustomRotSpeed" -> auraCustomRotSpeed = Float.parseFloat(val);
                    case "auraCustomRotation" -> auraCustomRotation = Boolean.parseBoolean(val);
                    case "auraCustomStrafe" -> auraCustomStrafe = Boolean.parseBoolean(val);
                    case "auraCustomNotNear" -> auraCustomNotNear = Boolean.parseBoolean(val);
                    case "auraCustomNotNearRange" -> auraCustomNotNearRange = Float.parseFloat(val);
                    case "autoWeapon" -> autoWeapon = Boolean.parseBoolean(val);
                    case "criticals" -> criticals = Boolean.parseBoolean(val);
                    case "auraThroughWalls" -> auraThroughWalls = Boolean.parseBoolean(val);
                    case "auraHitInGuis" -> auraHitInGuis = Boolean.parseBoolean(val);
                    case "auraSpinBot" -> auraSpinBot = Boolean.parseBoolean(val);
                    case "triggerWeaponOnly" -> triggerWeaponOnly = Boolean.parseBoolean(val);
                    case "autoTotemOn" -> autoTotemOn = Boolean.parseBoolean(val);
                    case "autoTotemHealth" -> autoTotemHealth = Float.parseFloat(val);
                    case "autoTotemMode" -> autoTotemMode = Integer.parseInt(val);
                    case "aimLockOn" -> aimLockOn = Boolean.parseBoolean(val);
                    case "aimOn" -> aimOn = Boolean.parseBoolean(val);
                    case "aimSpeed" -> aimSpeed = Float.parseFloat(val);
                    case "aimMode" -> aimMode = Integer.parseInt(val);
                    case "aimRange" -> aimRange = Float.parseFloat(val);
                    case "aimFov" -> aimFov = Float.parseFloat(val);
                    case "aimJitter" -> aimJitter = Float.parseFloat(val);
                    case "aimTargetMode" -> aimTargetMode = Integer.parseInt(val);
                    case "aimTargetPlayers" -> aimTargetPlayers = Boolean.parseBoolean(val);
                    case "aimTargetMobs" -> aimTargetMobs = Boolean.parseBoolean(val);
                    case "aimTargetAnimals" -> aimTargetAnimals = Boolean.parseBoolean(val);
                    case "antiBotOn" -> antiBotOn = Boolean.parseBoolean(val);
                    case "fullBrightOn" -> fullBrightOn = Boolean.parseBoolean(val);
                    case "nameHiderOn" -> nameHiderOn = Boolean.parseBoolean(val);
                    case "spiderOn" -> spiderOn = Boolean.parseBoolean(val);
                    case "spiderMode" -> spiderMode = Integer.parseInt(val);
                    case "spiderSpeed" -> spiderSpeed = Float.parseFloat(val);
                    case "noWebOn" -> noWebOn = Boolean.parseBoolean(val);
                    case "noWebMode" -> noWebMode = Integer.parseInt(val);
                    case "noclipOn" -> noclipOn = Boolean.parseBoolean(val);
                    case "noclipBypass" -> noclipBypass = Integer.parseInt(val);
                    case "noclipStep" -> noclipStep = Float.parseFloat(val);
                    case "noclipDirection" -> noclipDirection = Integer.parseInt(val);
                    case "freezeOn" -> freezeOn = Boolean.parseBoolean(val);
                    case "freezeMode" -> freezeMode = Integer.parseInt(val);
                    case "freezeVisual" -> freezeVisual = Boolean.parseBoolean(val);
                    case "airJumpOn" -> airJumpOn = Boolean.parseBoolean(val);
                    case "airJumpMode" -> airJumpMode = Integer.parseInt(val);
                    case "airJumpSpeed" -> airJumpSpeed = Float.parseFloat(val);
                    case "noFallOn" -> noFallOn = Boolean.parseBoolean(val);
                    case "noFallMode" -> noFallMode = Integer.parseInt(val);
                    case "velocityMode" -> velocityMode = Integer.parseInt(val);
                    case "jesusOn" -> jesusOn = Boolean.parseBoolean(val);
                    case "jesusMode" -> jesusMode = Integer.parseInt(val);
                    case "visualsParticles" -> visualsParticles = Integer.parseInt(val);
                    case "ambientOn" -> ambientOn = Boolean.parseBoolean(val);
                    case "ambientRainbow" -> ambientRainbow = Boolean.parseBoolean(val);
                    case "ambientMode" -> ambientMode = Integer.parseInt(val);
                    case "ambientStrength" -> ambientStrength = Float.parseFloat(val);
                    case "ambientHue" -> ambientHue = Float.parseFloat(val);
                    case "spinOn" -> spinOn = Boolean.parseBoolean(val);
                    case "spinMode" -> spinMode = Integer.parseInt(val);
                    case "spinSpeed" -> spinSpeed = Float.parseFloat(val);
                    case "spinSize" -> spinSize = Float.parseFloat(val);
                    case "optimizationOn" -> optimizationOn = Boolean.parseBoolean(val);
                    case "optProfile" -> optProfile = Integer.parseInt(val);
                    case "hitboxFix" -> hitboxFix = Boolean.parseBoolean(val);
                    case "xrayStealth" -> xrayStealth = Integer.parseInt(val);
                    case "xrayHighlight" -> xrayHighlight = Boolean.parseBoolean(val);
                    case "flyCustomVertical" -> flyCustomVertical = Float.parseFloat(val);
                    case "flyCustomPulse" -> flyCustomPulse = Float.parseFloat(val);
                    case "spiderCustomPulse" -> spiderCustomPulse = Float.parseFloat(val);
                    case "customBypass" -> {
                        String name = sanitizeEntry(val);
                        if (!name.isEmpty() && !customBypassNames.contains(name)) customBypassNames.add(name);
                    }
                    case "selectedAccount" -> selectedAccount = Integer.parseInt(val);
                    case "accountName" -> {
                        String name = sanitizeEntry(val);
                        if (!name.isEmpty()) accountNames.add(name);
                    }
                    case "waterCrits" -> waterCrits = Boolean.parseBoolean(val);
                    case "crasherTimerMode" -> crasherTimerMode = Integer.parseInt(val);
                    case "crasherMode" -> crasherMode = Integer.parseInt(val);
                    case "crasherVisual" -> crasherVisual = Boolean.parseBoolean(val);
                    case "serverCrasherOn" -> serverCrasherOn = Boolean.parseBoolean(val);
                    case "xrayOn" -> xrayOn = Boolean.parseBoolean(val);
                    case "xrayMode" -> xrayMode = Integer.parseInt(val);
                    case "xrayBypass" -> xrayBypass = Integer.parseInt(val);
                    case "xrayRange" -> xrayRange = Float.parseFloat(val);
                    case "xrayFarScan" -> xrayFarScan = Boolean.parseBoolean(val);
                    case "xrayAntiXray" -> xrayAntiXray = Boolean.parseBoolean(val);
                    case "xrayBlocks" -> loadXrayBlockNames(val);
                    case "baseFinderOn" -> baseFinderOn = Boolean.parseBoolean(val);
                    case "baseFinderMode" -> baseFinderMode = Integer.parseInt(val);
                    case "baseFinderRender" -> baseFinderRender = Integer.parseInt(val);
                    case "baseFinderRange" -> baseFinderRange = Float.parseFloat(val);
                    case "baseFinderChest" -> baseFinderChest = Boolean.parseBoolean(val);
                    case "baseFinderShulker" -> baseFinderShulker = Boolean.parseBoolean(val);
                    case "baseFinderWood" -> baseFinderWood = Boolean.parseBoolean(val);
                    case "baseFinderCarpet" -> baseFinderCarpet = Boolean.parseBoolean(val);
                    case "baseFinderUnusual" -> baseFinderUnusual = Boolean.parseBoolean(val);
                    case "creativeDropCount" -> creativeDropCount = Integer.parseInt(val);
                    case "creativeDupeCount" -> creativeDupeCount = Integer.parseInt(val);
                    case "dupeMode" -> dupeMode = Integer.parseInt(val);
                    case "godModeMode" -> godModeMode = Integer.parseInt(val);
                    case "seeInvisOn" -> seeInvisOn = Boolean.parseBoolean(val);
                    case "noFireOn" -> noFireOn = Boolean.parseBoolean(val);
                    case "noNauseaOn" -> noNauseaOn = Boolean.parseBoolean(val);
                    case "targetEspOn" -> targetEspOn = Boolean.parseBoolean(val);
                    case "noHurtCamOn" -> noHurtCamOn = Boolean.parseBoolean(val);
                    case "playerEspOn" -> playerEspOn = Boolean.parseBoolean(val);
                    case "shieldBreaker" -> shieldBreaker = Boolean.parseBoolean(val);
                    case "keyEsp" -> keyEsp = Integer.parseInt(val);
                    case "hudOn" -> hudOn = Boolean.parseBoolean(val);
                    case "targetHudOn" -> targetHudOn = Boolean.parseBoolean(val);
                    case "fakeName" -> fakeName = val;
                    case "useModernGui" -> useModernGui = Boolean.parseBoolean(val);
                    case "espOn" -> espOn = Boolean.parseBoolean(val);
                    case "espBoxesOn" -> espBoxesOn = Boolean.parseBoolean(val);
                    case "espNamesOn" -> espNamesOn = Boolean.parseBoolean(val);
                    case "espHealthOn" -> espHealthOn = Boolean.parseBoolean(val);
                    case "espDistanceOn" -> espDistanceOn = Boolean.parseBoolean(val);
                    case "espItemsOn" -> espItemsOn = Boolean.parseBoolean(val);
                    case "espTracersOn" -> espTracersOn = Boolean.parseBoolean(val);
                    case "espFillOn" -> espFillOn = Boolean.parseBoolean(val);
                    case "espPlayersOn" -> espPlayersOn = Boolean.parseBoolean(val);
                    case "espMobsOn" -> espMobsOn = Boolean.parseBoolean(val);
                    case "espAnimalsOn" -> espAnimalsOn = Boolean.parseBoolean(val);
                    case "espRange" -> espRange = Float.parseFloat(val);
                    case "triggerThroughWalls" -> triggerThroughWalls = Boolean.parseBoolean(val);
                    case "triggerSmartDelay" -> triggerSmartDelay = Boolean.parseBoolean(val);
                    case "triggerTargetPlayers" -> triggerTargetPlayers = Boolean.parseBoolean(val);
                    case "triggerTargetMobs" -> triggerTargetMobs = Boolean.parseBoolean(val);
                    case "triggerTargetAnimals" -> triggerTargetAnimals = Boolean.parseBoolean(val);
                    case "triggerAutoWeapon" -> triggerAutoWeapon = Boolean.parseBoolean(val);
                    case "triggerCriticals" -> triggerCriticals = Boolean.parseBoolean(val);
                    case "triggerWaitHurtTime" -> triggerWaitHurtTime = Boolean.parseBoolean(val);
                    case "triggerCooldown" -> triggerCooldown = Float.parseFloat(val);
                    case "triggerJitter" -> triggerJitter = Float.parseFloat(val);
                    case "triggerFov" -> triggerFov = Float.parseFloat(val);
                    case "friends" -> {
                        friends.clear();
                        if (!val.isEmpty()) friends.addAll(Arrays.asList(val.split(",")));
                    }
                    default -> PypsenFeatures.load(key, val);
                }
            }

            // Keep every parsed value inside the range the GUI and modes expect.
            espRange = MathHelper.clamp(espRange, 5f, 256f);
            triggerRange = MathHelper.clamp(triggerRange, 1f, 12f);
            triggerCps = MathHelper.clamp(triggerCps, 1f, 50f);
            triggerCooldown = MathHelper.clamp(triggerCooldown, 0f, 1f);
            triggerJitter = MathHelper.clamp(triggerJitter, 0f, 100f);
            triggerFov = MathHelper.clamp(triggerFov, 1f, 180f);
            auraMode = MathHelper.clamp(auraMode, 0, AURA_MODES.length - 1);
            auraRotMode = MathHelper.clamp(auraRotMode, 0, AURA_ROTS.length - 1);
            auraTimingMode = MathHelper.clamp(auraTimingMode, 0, AURA_TIMINGS.length - 1);
            auraTargetPart = MathHelper.clamp(auraTargetPart, 0, AURA_PARTS.length - 1);
            auraTargetMode = MathHelper.clamp(auraTargetMode, 0, AURA_TARGETS.length - 1);
            aimMode = MathHelper.clamp(aimMode, 0, AIM_MODES.length - 1);
            aimTargetMode = MathHelper.clamp(aimTargetMode, 0, AURA_TARGETS.length - 1);
            aimRange = MathHelper.clamp(aimRange, 3f, 32f);
            aimFov = MathHelper.clamp(aimFov, 10f, 360f);
            aimJitter = MathHelper.clamp(aimJitter, 0f, 10f);

            // New settings: never let an old or hand-edited config crash the GUI.
            auraNotNearRange = MathHelper.clamp(auraNotNearRange, 0.5f, auraRange);
            auraCustomNotNearRange = MathHelper.clamp(auraCustomNotNearRange, 0.5f, auraCustomRange);
            auraCustomMode = MathHelper.clamp(auraCustomMode, 0, AURA_MODES.length - 1);
            auraCustomRotMode = MathHelper.clamp(auraCustomRotMode, 0, AURA_ROTS.length - 1);
            auraCustomBypass = MathHelper.clamp(auraCustomBypass, 0, allBypassNames().length - 1);
            auraCustomRange = MathHelper.clamp(auraCustomRange, 1f, 20f);
            auraCustomFov = MathHelper.clamp(auraCustomFov, 10f, 360f);
            auraCustomRotSpeed = MathHelper.clamp(auraCustomRotSpeed, 1f, 180f);
            spiderMode = MathHelper.clamp(spiderMode, 0, SPIDER_MODES.length + customBypassNames.size() - 1);
            freezeMode = MathHelper.clamp(freezeMode, 0, FREEZE_MODES.length - 1);
            airJumpMode = MathHelper.clamp(airJumpMode, 0, AIRJUMP_MODES.length - 1);
            noFallMode = MathHelper.clamp(noFallMode, 0, NOFALL_MODES.length - 1);
            velocityMode = MathHelper.clamp(velocityMode, 0, VELOCITY_MODES.length - 1);
            jesusMode = MathHelper.clamp(jesusMode, 0, JESUS_MODES.length - 1);
            visualsParticles = MathHelper.clamp(visualsParticles, 0, PARTICLES_MODES.length - 1);
            ambientMode = MathHelper.clamp(ambientMode, 0, AMBIENT_MODES.length - 1);
            ambientStrength = MathHelper.clamp(ambientStrength, 0f, 1f);
            ambientHue = MathHelper.clamp(ambientHue, 0f, 360f);
            spinMode = MathHelper.clamp(spinMode, 0, SPIN_MODES.length - 1);
            spinSpeed = MathHelper.clamp(spinSpeed, 0f, 8f);
            spinSize = MathHelper.clamp(spinSize, 12f, 160f);
            auraSmoothing = MathHelper.clamp(auraSmoothing, 0f, 0.95f);
            optProfile = MathHelper.clamp(optProfile, 0, OPT_PROFILES.length - 1);
            xrayStealth = MathHelper.clamp(xrayStealth, 0, XRAY_STEALTH_MODES.length - 1);
            int bypassCount = allBypassNames().length;
            speedBypass = MathHelper.clamp(speedBypass, 0, bypassCount - 1);
            auraBypass = MathHelper.clamp(auraBypass, 0, bypassCount - 1);
            flyBypass = MathHelper.clamp(flyBypass, 0, bypassCount - 1);
            godModeMode = MathHelper.clamp(godModeMode, 0, GODMODE_MODES.length - 1);
            noWebMode = MathHelper.clamp(noWebMode, 0, NOWEB_MODES.length - 1);
            noclipBypass = MathHelper.clamp(noclipBypass, 0, NOCLIP_MODES.length + customBypassNames.size() - 1);
            noclipDirection = MathHelper.clamp(noclipDirection, 0, NOCLIP_DIRECTIONS.length - 1);
            noclipStep = MathHelper.clamp(noclipStep, 0.05f, 4f);
            flyCustomVertical = MathHelper.clamp(flyCustomVertical, -1f, 1f);
            flyCustomPulse = MathHelper.clamp(flyCustomPulse, 0f, 1f);
            spiderCustomPulse = MathHelper.clamp(spiderCustomPulse, 0f, 1f);
            selectedAccount = MathHelper.clamp(selectedAccount, 0, Math.max(0, accountNames.size() - 1));
            crasherMode = MathHelper.clamp(crasherMode, 0, CRASHER_MODES.length - 1);
            crasherTimerMode = MathHelper.clamp(crasherTimerMode, 0, CRASHER_TIMERS.length - 1);
            spiderSpeed = MathHelper.clamp(spiderSpeed, 0.5f, 4f);
            airJumpSpeed = MathHelper.clamp(airJumpSpeed, 0.5f, 4f);
            xrayMode = MathHelper.clamp(xrayMode, 0, XRAY_MODES.length - 1);
            xrayBypass = MathHelper.clamp(xrayBypass, 0, XRAY_BYPASS_MODES.length - 1);
            xrayRange = MathHelper.clamp(xrayRange, 16f, 1024f);
            baseFinderMode = MathHelper.clamp(baseFinderMode, 0, BASE_FINDER_MODES.length - 1);
            baseFinderRender = MathHelper.clamp(baseFinderRender, 0, BASE_FINDER_RENDERS.length - 1);
            baseFinderRange = MathHelper.clamp(baseFinderRange, 16f, 1024f);
            creativeDropCount = MathHelper.clamp(creativeDropCount, 1, 64);
            creativeDupeCount = MathHelper.clamp(creativeDupeCount, 1, 20);
            dupeMode = MathHelper.clamp(dupeMode, 0, DUPE_MODES.length - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ══════════ XRAY SCREEN ══════════
    public static class XrayScreen extends Screen {
        private final Screen parent;
        private net.minecraft.client.gui.widget.TextFieldWidget searchField;
        private int scroll = 0;
        private final List<Block> allBlocks = new ArrayList<>();
        private final List<Block> filteredBlocks = new ArrayList<>();

        public XrayScreen(Screen parent) {
            super(Text.literal("XRay Blocks"));
            this.parent = parent;
            Registries.BLOCK.forEach(b -> {
                if (b != Blocks.AIR && b != Blocks.CAVE_AIR && b != Blocks.VOID_AIR) {
                    allBlocks.add(b);
                }
            });
            allBlocks.sort(Comparator.comparing(b -> b.getName().getString()));
            filteredBlocks.addAll(allBlocks);
        }

        @Override
        protected void init() {
            addDrawableChild(ButtonWidget.builder(Text.literal("<- Save & Back"), b -> {
                saveConfig();
                MinecraftClient.getInstance().setScreen(parent);
                MinecraftClient.getInstance().worldRenderer.reload();
            }).dimensions(10, 10, 100, 20).build());

            searchField = new net.minecraft.client.gui.widget.TextFieldWidget(textRenderer, width / 2 - 100, 10, 200, 20, Text.literal("Search"));
            searchField.setChangedListener(text -> {
                filteredBlocks.clear();
                String q = text.toLowerCase();
                for (Block b : allBlocks) {
                    if (b.getName().getString().toLowerCase().contains(q)) {
                        filteredBlocks.add(b);
                    }
                }
                scroll = 0;
            });
            addDrawableChild(searchField);
            addDrawableChild(ButtonWidget.builder(Text.literal("Highlight"), b -> { xrayHighlight = !xrayHighlight; }).dimensions(width - 175, 10, 80, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> { resetXrayBlocks(); saveConfig(); }).dimensions(width - 90, 10, 80, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            super.render(ctx, mx, my, d);

            int y = 40 + scroll;
            int x = width / 2 - 100;

            for (int i = 0; i < filteredBlocks.size(); i++) {
                if (y > 30 && y < height - 20) {
                    Block b = filteredBlocks.get(i);
                    boolean on = XRAY_BLOCKS.contains(b);
                    String name = b.getName().getString();
                    if (name.length() > 25) name = name.substring(0, 25) + "..";

                    boolean hovered = mx >= x && mx <= x + 200 && my >= y && my <= y + 20;
                    ctx.fill(x, y, x + 200, y + 20, on ? 0x7700FF00 : (hovered ? 0x77555555 : 0x77000000));
                    ctx.drawTextWithShadow(textRenderer, name, x + 5, y + 6, 0xFFFFFF);
                    ctx.drawTextWithShadow(textRenderer, on ? "ON" : "OFF", x + 175, y + 6, on ? 0x55FF55 : 0xFF5555);
                }
                y += 22;
            }
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            if (super.mouseClicked(mx, my, button)) return true;
            int y = 40 + scroll;
            int x = width / 2 - 100;
            for (int i = 0; i < filteredBlocks.size(); i++) {
                if (y > 30 && y < height - 20) {
                    if (mx >= x && mx <= x + 200 && my >= y && my <= y + 20) {
                        Block b = filteredBlocks.get(i);
                        if (XRAY_BLOCKS.contains(b)) XRAY_BLOCKS.remove(b);
                        else XRAY_BLOCKS.add(b);
                        return true;
                    }
                }
                y += 22;
            }
            return false;
        }

        @Override
        public boolean mouseScrolled(double mx, double my, double amount) {
            scroll += (int)(amount * 22);
            if (scroll > 0) scroll = 0;
            int maxScroll = -(filteredBlocks.size() * 22 - (height - 60));
            if (maxScroll > 0) maxScroll = 0;
            if (scroll < maxScroll) scroll = maxScroll;
            return true;
        }
    }

    // ══════════ ACCOUNT SWITCHER ══════════
    public static class AccountScreen extends Screen {
        private final Screen parent;
        private net.minecraft.client.gui.widget.TextFieldWidget nameField;
        private int page = 0;

        public AccountScreen(Screen parent) {
            super(Text.literal("Account Switcher"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            addDrawableChild(ButtonWidget.builder(Text.literal("<- Back"), b -> {
                saveConfig();
                MinecraftClient.getInstance().setScreen(parent);
            }).dimensions(10, 10, 100, 20).build());

            int fieldW = Math.max(80, Math.min(180, width - 20));
            nameField = new net.minecraft.client.gui.widget.TextFieldWidget(textRenderer, 10, 38, fieldW, 20, Text.literal("Account name"));
            if (selectedAccount >= 0 && selectedAccount < accountNames.size()) {
                nameField.setText(accountNames.get(selectedAccount));
            }
            addDrawableChild(nameField);

            int bw = Math.max(60, (width - 24) / 3);
            addDrawableChild(ButtonWidget.builder(Text.literal("Add"), b -> {
                addAccount(nameField.getText());
                MinecraftClient.getInstance().setScreen(new AccountScreen(parent));
            }).dimensions(10, 66, bw, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Switch Local"), b -> applyAccount(selectedAccount))
                    .dimensions(14 + bw, 66, bw, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Remove"), b -> {
                removeAccount(selectedAccount);
                MinecraftClient.getInstance().setScreen(new AccountScreen(parent));
            }).dimensions(18 + bw * 2, 66, bw, 20).build());

            int perPage = 9;
            if (page < 0) page = 0;
            if (page > Math.max(0, (accountNames.size() - 1) / perPage)) {
                page = Math.max(0, (accountNames.size() - 1) / perPage);
            }
            for (int i = 0; i < perPage; i++) {
                int index = page * perPage + i;
                if (index >= accountNames.size()) break;
                int y = 96 + i * 22;
                String mark = index == selectedAccount ? "\u00a7a*" : "\u00a77 ";
                addDrawableChild(ButtonWidget.builder(Text.literal(mark + accountNames.get(index)), b -> {
                    selectedAccount = index;
                    applyAccount(index);
                }).dimensions(10, y, Math.max(60, width - 80), 20).build());
                addDrawableChild(ButtonWidget.builder(Text.literal("x"), b -> {
                    removeAccount(index);
                    MinecraftClient.getInstance().setScreen(new AccountScreen(parent));
                }).dimensions(Math.max(76, width - 64), y, 54, 20).build());
            }
            int navY = 96 + perPage * 22 + 4;
            addDrawableChild(ButtonWidget.builder(Text.literal("<<"), b -> {
                page = Math.max(0, page - 1);
                MinecraftClient.getInstance().setScreen(new AccountScreen(parent));
            }).dimensions(10, navY, 54, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">>"), b -> {
                page++;
                MinecraftClient.getInstance().setScreen(new AccountScreen(parent));
            }).dimensions(70, navY, 54, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            super.render(ctx, mx, my, d);
            ctx.drawTextWithShadow(textRenderer, "\u00a78Local/offline session only - no launcher tokens touched", 10, 88, 0xFFFFFF);
            if (!accountStatus.isEmpty()) {
                ctx.drawTextWithShadow(textRenderer, accountStatus, 10, height - 24, 0xFFFFFF);
            }
        }
    }

    // ══════════ EDITABLE BYPASS LIST ══════════
    public static class BypassScreen extends Screen {
        private final Screen parent;
        private net.minecraft.client.gui.widget.TextFieldWidget nameField;
        private int page = 0;

        public BypassScreen(Screen parent) {
            super(Text.literal("Custom Bypass List"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            addDrawableChild(ButtonWidget.builder(Text.literal("<- Back"), b -> {
                saveConfig();
                MinecraftClient.getInstance().setScreen(parent);
            }).dimensions(10, 10, 100, 20).build());

            int fieldW = Math.max(80, Math.min(180, width - 20));
            nameField = new net.minecraft.client.gui.widget.TextFieldWidget(textRenderer, 10, 38, fieldW, 20, Text.literal("Bypass name"));
            addDrawableChild(nameField);
            int bw = Math.max(70, (width - 20) / 2);
            addDrawableChild(ButtonWidget.builder(Text.literal("Add name"), b -> {
                addBypassName(nameField.getText());
                MinecraftClient.getInstance().setScreen(new BypassScreen(parent));
            }).dimensions(10, 66, bw, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Clear all"), b -> {
                customBypassNames.clear();
                saveConfig();
                MinecraftClient.getInstance().setScreen(new BypassScreen(parent));
            }).dimensions(14 + bw, 66, bw, 20).build());

            int perPage = 9;
            if (page > Math.max(0, (customBypassNames.size() - 1) / perPage)) {
                page = Math.max(0, (customBypassNames.size() - 1) / perPage);
            }
            for (int i = 0; i < perPage; i++) {
                int index = page * perPage + i;
                if (index >= customBypassNames.size()) break;
                String name = customBypassNames.get(index);
                int y = 96 + i * 22;
                addDrawableChild(ButtonWidget.builder(Text.literal(name), b -> {
                    flyBypass = BYPASS_NAMES.length + index;
                    noclipBypass = NOCLIP_MODES.length + index;
                    spiderMode = SPIDER_MODES.length + index;
                    saveConfig();
                }).dimensions(10, y, Math.max(60, width - 80), 20).build());
                addDrawableChild(ButtonWidget.builder(Text.literal("x"), b -> {
                    removeBypassName(name);
                    MinecraftClient.getInstance().setScreen(new BypassScreen(parent));
                }).dimensions(Math.max(76, width - 64), y, 54, 20).build());
            }
            int navY = 96 + perPage * 22 + 4;
            addDrawableChild(ButtonWidget.builder(Text.literal("<<"), b -> {
                page = Math.max(0, page - 1);
                MinecraftClient.getInstance().setScreen(new BypassScreen(parent));
            }).dimensions(10, navY, 54, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">>"), b -> {
                page++;
                MinecraftClient.getInstance().setScreen(new BypassScreen(parent));
            }).dimensions(70, navY, 54, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            super.render(ctx, mx, my, d);
            ctx.drawTextWithShadow(textRenderer, "\u00a78Custom names use the editable fly/spider/noclip values", 10, 88, 0xFFFFFF);
        }
    }

    // ══════════ MAIN MENU SCREEN ══════════
    public static class MenuScreen extends Screen {
        private int scroll = 0;
        private int contentHeight = 0;
        private int bindingTarget = -1;

        public MenuScreen() { super(Text.literal("Pypsen v2")); }

        @Override
        protected void init() { buildWidgets(); }

        private void applyPreset(String preset) {
            switch (preset) {
                case "Funtime" -> {
                    auraRange = 3.0f; auraMode = 0; auraRotMode = 2; auraBypass = 7;
                    hitboxVal = 0.1f; velocityOn = true; antiFlagOn = true; auraThroughWalls = false;
                    speedBypass = 7; flyBypass = 7; auraHitInGuis = false; autoWeapon = true; criticals = true;
                }
                case "ReallyWorld" -> {
                    auraRange = 3.0f; auraMode = 0; auraRotMode = 1; auraBypass = 8;
                    hitboxVal = 0.1f; velocityOn = true; antiFlagOn = true; auraThroughWalls = false;
                    speedBypass = 8; flyBypass = 8; auraHitInGuis = false; autoWeapon = true; criticals = true;
                }
                case "Test" -> {
                    auraRange = 3.5f; auraMode = 0; auraRotMode = 2; auraBypass = 4;
                    hitboxVal = 0.5f; velocityOn = false; antiFlagOn = true; auraThroughWalls = false;
                    speedBypass = 4; flyBypass = 4; auraHitInGuis = true; autoWeapon = false; criticals = false;
                }
                case "HvH" -> {
                    auraRange = 6.0f; auraMode = 1; auraRotMode = 3; auraBypass = 0;
                    hitboxVal = 2.0f; velocityOn = true; antiFlagOn = false; auraThroughWalls = true;
                    speedBypass = 0; flyBypass = 0; auraHitInGuis = true; autoWeapon = true; criticals = true;
                }
            }
            rebuild();
        }

        private void buildWidgets() {
            clearChildren();
            int w = Math.min(210, Math.max(120, width - 12));
            int x = Math.max(6, Math.min(width / 2 - 105, width - w - 6));
            int y = 30 + scroll;

            // Save Config Button
            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7b\u25BC Save Config to Documents \u25BC"), b -> {
                saveConfig();
            }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7d\u2728 Switch to Modern GUI \u2728"), b -> {
                useModernGui = true;
                if (client != null) client.setScreen(new ModernGuiScreen());
            }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("Account Switcher (local)"), b -> {
                if (client != null) client.setScreen(new AccountScreen(this));
            }).dimensions(x, y, w / 2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Bypass List"), b -> {
                if (client != null) client.setScreen(new BypassScreen(this));
            }).dimensions(x + w / 2 + 2, y, w / 2 - 2, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7e--- PRESETS ---"), b -> {}).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Funtime"), b -> applyPreset("Funtime")).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("ReallyWorld"), b -> applyPreset("ReallyWorld")).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Test"), b -> applyPreset("Test")).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("HvH"), b -> applyPreset("HvH")).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Custom: Save"), b -> { saveAuraCustom(); rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Custom: Load"), b -> { loadAuraCustom(); rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 26;

            y = section(x, y, w, "KillAura", auraOn, b -> { auraOn = !auraOn; rebuild(); }, keyAura, 1);
            addDrawableChild(slider(x, y, w, "Range", 1f, 20f, auraRange, v -> auraRange = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Mode", AURA_MODES, auraMode, v -> { auraMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Target", AURA_TARGETS, auraTargetMode, v -> { auraTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Rotation", AURA_ROTS, auraRotMode, v -> { auraRotMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Timing", AURA_TIMINGS, auraTimingMode, v -> { auraTimingMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Aim Part", AURA_PARTS, auraTargetPart, v -> { auraTargetPart = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Aim Jitter", 0f, 1f, auraAimJitter, v -> auraAimJitter = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Jitter Smoothing", 0f, 0.95f, auraSmoothing, v -> auraSmoothing = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Stickiness", 0f, 100f, auraStickiness, v -> auraStickiness = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Rot Curve", 0f, 100f, auraRotCurve, v -> auraRotCurve = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Rot Speed", 1f, 180f, auraRotSpeed, v -> auraRotSpeed = v)); y += 22;
            addDrawableChild(slider(x, y, w, "FOV", 10f, 360f, auraFov, v -> auraFov = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Predict: " + (auraPredict ? "§aON" : "§cOFF")), b -> { auraPredict = !auraPredict; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Randomize: " + (auraRandomize ? "§aON" : "§cOFF")), b -> { auraRandomize = !auraRandomize; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", allBypassNames(), auraBypass, v -> { auraBypass = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("NOT NEAR: " + (auraNotNear ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraNotNear = !auraNotNear; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(slider(x, y, w, "Keep Distance", 0.5f, 8f, auraNotNearRange, v -> auraNotNearRange = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Rotation: " + (auraRotationEnabled ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraRotationEnabled = !auraRotationEnabled; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Strafe: " + (auraStrafeEnabled ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraStrafeEnabled = !auraStrafeEnabled; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Players: " + (auraTargetPlayers ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetPlayers = !auraTargetPlayers; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Mobs: " + (auraTargetMobs ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetMobs = !auraTargetMobs; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Animals: " + (auraTargetAnimals ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetAnimals = !auraTargetAnimals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("AutoWeapon: " + (autoWeapon ? "\u00a7aON" : "\u00a7cOFF")), b -> { autoWeapon = !autoWeapon; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Criticals: " + (criticals ? "\u00a7aON" : "\u00a7cOFF")), b -> { criticals = !criticals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Shield Breaker: " + (shieldBreaker ? "\u00a7aON" : "\u00a7cOFF")), b -> { shieldBreaker = !shieldBreaker; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Water Crits: " + (waterCrits ? "\u00a7aON" : "\u00a7cOFF")), b -> { waterCrits = !waterCrits; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Jump & Attack: " + (auraJump ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraJump = !auraJump; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Through Walls: " + (auraThroughWalls ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraThroughWalls = !auraThroughWalls; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Hit in GUIs: " + (auraHitInGuis ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraHitInGuis = !auraHitInGuis; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("AntiBot: " + (antiBotOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { antiBotOn = !antiBotOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("SpinBot (Idle): " + (auraSpinBot ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraSpinBot = !auraSpinBot; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Visuals (ESP)", visualsOn, b -> { visualsOn = !visualsOn; rebuild(); }, keyVisuals, 8);
            addDrawableChild(cycleBtn(x, y, w, "Target", VISUALS_TARGETS, visualsTargetMode, v -> { visualsTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Particles", PARTICLES_MODES, visualsParticles, v -> { visualsParticles = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Custom Ambient: " + (ambientOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { ambientOn = !ambientOn; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Rainbow: " + (ambientRainbow ? "\u00a7aON" : "\u00a7cOFF")), b -> { ambientRainbow = !ambientRainbow; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Ambient Mode", AMBIENT_MODES, ambientMode, v -> { ambientMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Ambient Strength", 0f, 1f, ambientStrength, v -> ambientStrength = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Ambient Hue", 0f, 360f, ambientHue, v -> ambientHue = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Spins: " + (spinOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { spinOn = !spinOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Spin Mode", SPIN_MODES, spinMode, v -> { spinMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Spin Speed", 0f, 8f, spinSpeed, v -> spinSpeed = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Spin Size", 12f, 160f, spinSize, v -> spinSize = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Player ESP (Tags): " + (playerEspOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { playerEspOn = !playerEspOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("FullBright: " + (fullBrightOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { fullBrightOn = !fullBrightOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("NameHider (Record): " + (nameHiderOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { nameHiderOn = !nameHiderOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("See Invisibles: " + (seeInvisOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { seeInvisOn = !seeInvisOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("No Fire: " + (noFireOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noFireOn = !noFireOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("No Nausea: " + (noNauseaOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noNauseaOn = !noNauseaOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target ESP: " + (targetEspOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { targetEspOn = !targetEspOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("NoHurtCam: " + (noHurtCamOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noHurtCamOn = !noHurtCamOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "2D ESP", espOn, b -> { espOn = !espOn; rebuild(); }, keyEsp, 28);
            addDrawableChild(slider(x, y, w, "Range", 5f, 256f, espRange, v -> espRange = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Boxes: " + (espBoxesOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espBoxesOn = !espBoxesOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Names: " + (espNamesOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espNamesOn = !espNamesOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Health: " + (espHealthOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espHealthOn = !espHealthOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Distance: " + (espDistanceOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espDistanceOn = !espDistanceOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Fill: " + (espFillOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espFillOn = !espFillOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Tracers: " + (espTracersOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espTracersOn = !espTracersOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Players: " + (espPlayersOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espPlayersOn = !espPlayersOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Mobs: " + (espMobsOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espMobsOn = !espMobsOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Animals: " + (espAnimalsOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espAnimalsOn = !espAnimalsOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Items: " + (espItemsOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { espItemsOn = !espItemsOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "TriggerBot", triggerBotOn, b -> { triggerBotOn = !triggerBotOn; rebuild(); }, keyTriggerBot, 13);
            addDrawableChild(slider(x, y, w, "CPS", 1f, 50f, triggerCps, v -> triggerCps = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Range", 1f, 12f, triggerRange, v -> triggerRange = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Cooldown", 0f, 1f, triggerCooldown, v -> triggerCooldown = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Jitter %", 0f, 100f, triggerJitter, v -> triggerJitter = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Scan FOV", 1f, 180f, triggerFov, v -> triggerFov = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Weapon Only: " + (triggerWeaponOnly ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerWeaponOnly = !triggerWeaponOnly; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Smart Delay: " + (triggerSmartDelay ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerSmartDelay = !triggerSmartDelay; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Wait Hurt Time: " + (triggerWaitHurtTime ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerWaitHurtTime = !triggerWaitHurtTime; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Through Walls: " + (triggerThroughWalls ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerThroughWalls = !triggerThroughWalls; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Auto Weapon: " + (triggerAutoWeapon ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerAutoWeapon = !triggerAutoWeapon; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Criticals: " + (triggerCriticals ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerCriticals = !triggerCriticals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Players: " + (triggerTargetPlayers ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerTargetPlayers = !triggerTargetPlayers; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Mobs: " + (triggerTargetMobs ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerTargetMobs = !triggerTargetMobs; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Animals: " + (triggerTargetAnimals ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerTargetAnimals = !triggerTargetAnimals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "AutoTotem", autoTotemOn, b -> { autoTotemOn = !autoTotemOn; rebuild(); }, keyAutoTotem, 16);
            addDrawableChild(slider(x, y, w, "Health", 1f, 20f, autoTotemHealth, v -> autoTotemHealth = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Mode", TOTEM_MODES, autoTotemMode, v -> { autoTotemMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "AutoClicker", autoClickerOn, b -> { autoClickerOn = !autoClickerOn; rebuild(); }, keyAutoClicker, 10);
            addDrawableChild(slider(x, y, w, "CPS", 1f, 50f, clickerCps, v -> clickerCps = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Right Click: " + (autoClickerRight ? "§aON" : "§cOFF")), b -> { autoClickerRight = !autoClickerRight; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Farm Mode: " + (autoClickerFarm ? "§aON" : "§cOFF")), b -> { autoClickerFarm = !autoClickerFarm; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "FreeCam", freecamOn, b -> {
                freecamOn = !freecamOn;
                if (freecamOn && MinecraftClient.getInstance().player != null) {
                    freecamPos = MinecraftClient.getInstance().player.getPos();
                    freecamYaw = MinecraftClient.getInstance().player.getYaw();
                    freecamPitch = MinecraftClient.getInstance().player.getPitch();
                } else if (!freecamOn && MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.noClip = false;
                    MinecraftClient.getInstance().player.getAbilities().flying = false;
                    if (freecamPos != null) {
                        MinecraftClient.getInstance().player.setPosition(freecamPos);
                        MinecraftClient.getInstance().player.setYaw(freecamYaw);
                        MinecraftClient.getInstance().player.setPitch(freecamPitch);
                        MinecraftClient.getInstance().player.setVelocity(0, 0, 0);
                    }
                }
                rebuild();
            }, keyFreecam, 15); y += 26;

            y = section(x, y, w, "AimAssist", aimOn, b -> { aimOn = !aimOn; rebuild(); }, keyAim, 6);
            addDrawableChild(cycleBtn(x, y, w, "Aim Method", AIM_MODES, aimMode, v -> { aimMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Range", 3f, 32f, aimRange, v -> aimRange = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Speed", 1f, 100f, aimSpeed, v -> aimSpeed = v)); y += 22;
            addDrawableChild(slider(x, y, w, "FOV", 10f, 360f, aimFov, v -> aimFov = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Wobble", 0f, 10f, aimJitter, v -> aimJitter = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Priority", AURA_TARGETS, aimTargetMode, v -> { aimTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Aim Players: " + (aimTargetPlayers ? "§aON" : "§cOFF")), b -> { aimTargetPlayers = !aimTargetPlayers; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Mobs: " + (aimTargetMobs ? "§aON" : "§cOFF")), b -> { aimTargetMobs = !aimTargetMobs; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Animals: " + (aimTargetAnimals ? "§aON" : "§cOFF")), b -> { aimTargetAnimals = !aimTargetAnimals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "\u00a75AimLock (Hard Lock)", aimLockOn, b -> { aimLockOn = !aimLockOn; rebuild(); }, keyAimLock, 17); y += 4;

            y = section(x, y, w, "Hitbox", hitboxOn, b -> { hitboxOn = !hitboxOn; rebuild(); }, keyHitbox, 2);
            addDrawableChild(slider(x, y, w, "Size (Cube)", 0.1f, 1000f, hitboxVal, v -> hitboxVal = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Render Fix: " + (hitboxFix ? "\u00a7aON" : "\u00a7cOFF")), b -> { hitboxFix = !hitboxFix; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Velocity (Anti-KB)", velocityOn, b -> { velocityOn = !velocityOn; rebuild(); }, keyVelocity, 5);
            addDrawableChild(cycleBtn(x, y, w, "Mode", VELOCITY_MODES, velocityMode, v -> { velocityMode = v; rebuild(); })); y += 26;
            y = section(x, y, w, "Strafe", strafeOn, b -> { strafeOn = !strafeOn; rebuild(); }, keyStrafe, 7); y += 4;
            y = section(x, y, w, "Noclip", noclipOn, b -> { noclipOn = !noclipOn; rebuild(); }, keyNoclip, 11);
            addDrawableChild(cycleBtn(x, y, w, "Mode", allNoclipModes(), noclipBypass, v -> { noclipBypass = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Direction", NOCLIP_DIRECTIONS, noclipDirection, v -> { noclipDirection = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Step", 0.05f, 4f, noclipStep, v -> noclipStep = v)); y += 26;
            y = section(x, y, w, "Anti-Flag", antiFlagOn, b -> { antiFlagOn = !antiFlagOn; rebuild(); }, keyAntiFlag, 12); y += 4;

            y = section(x, y, w, "Spider", spiderOn, b -> { spiderOn = !spiderOn; rebuild(); }, keySpider, 18);
            addDrawableChild(cycleBtn(x, y, w, "Mode", allSpiderModes(), spiderMode, v -> { spiderMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Speed", 0.5f, 4f, spiderSpeed, v -> spiderSpeed = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Custom Pulse", 0f, 1f, spiderCustomPulse, v -> spiderCustomPulse = v)); y += 26;

            y = section(x, y, w, "NoWeb", noWebOn, b -> { noWebOn = !noWebOn; rebuild(); }, keyNoWeb, 19);
            addDrawableChild(cycleBtn(x, y, w, "Mode", NOWEB_MODES, noWebMode, v -> { noWebMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Freeze", freezeOn, b -> { freezeOn = !freezeOn; rebuild(); }, keyFreeze, 20);
            addDrawableChild(cycleBtn(x, y, w, "Mode", FREEZE_MODES, freezeMode, v -> { freezeMode = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Visual: " + (freezeVisual ? "\u00a7aON" : "\u00a7cOFF")), b -> { freezeVisual = !freezeVisual; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "AirJump", airJumpOn, b -> { airJumpOn = !airJumpOn; rebuild(); }, keyAirJump, 25);
            addDrawableChild(cycleBtn(x, y, w, "Mode", AIRJUMP_MODES, airJumpMode, v -> { airJumpMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Speed", 0.5f, 4f, airJumpSpeed, v -> airJumpSpeed = v)); y += 26;

            y = section(x, y, w, "NoFall", noFallOn, b -> { noFallOn = !noFallOn; rebuild(); }, keyNoFall, 26);
            addDrawableChild(cycleBtn(x, y, w, "Mode", NOFALL_MODES, noFallMode, v -> { noFallMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Jesus", jesusOn, b -> { jesusOn = !jesusOn; rebuild(); }, keyJesus, 27);
            addDrawableChild(cycleBtn(x, y, w, "Mode", JESUS_MODES, jesusMode, v -> { jesusMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Speed", speedOn, b -> { speedOn = !speedOn; rebuild(); }, keySpeed, 0);
            addDrawableChild(slider(x, y, w, "Speed", 0.1f, 100f, speedVal, v -> speedVal = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", allBypassNames(), speedBypass, v -> { speedBypass = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Fly", flyOn, b -> { flyOn = !flyOn; rebuild(); }, keyFly, 3);
            addDrawableChild(slider(x, y, w, "Fly Speed", 0.1f, 50f, flySpeed, v -> flySpeed = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", allBypassNames(), flyBypass, v -> { flyBypass = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Custom Vertical", -1f, 1f, flyCustomVertical, v -> flyCustomVertical = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Custom Pulse", 0f, 1f, flyCustomPulse, v -> flyCustomPulse = v)); y += 26;

            y = section(x, y, w, "XRay", xrayOn, b -> { xrayOn = !xrayOn; MinecraftClient.getInstance().worldRenderer.reload(); rebuild(); }, keyXray, 4);
            addDrawableChild(cycleBtn(x, y, w, "Mode", XRAY_MODES, xrayMode, v -> { xrayMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", XRAY_BYPASS_MODES, xrayBypass, v -> { xrayBypass = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Far Range", 16f, 1024f, xrayRange, v -> xrayRange = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Far Scan: " + (xrayFarScan ? "\u00a7aON" : "\u00a7cOFF")), b -> { xrayFarScan = !xrayFarScan; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Anti-XRay: " + (xrayAntiXray ? "\u00a7aON" : "\u00a7cOFF")), b -> { xrayAntiXray = !xrayAntiXray; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Stealth", XRAY_STEALTH_MODES, xrayStealth, v -> { xrayStealth = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Block Highlight: " + (xrayHighlight ? "\u00a7aON" : "\u00a7cOFF")), b -> { xrayHighlight = !xrayHighlight; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Select XRay Blocks..."), b -> {
                MinecraftClient.getInstance().setScreen(new XrayScreen(this));
            }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Base Finder", baseFinderOn, b -> { baseFinderOn = !baseFinderOn; rebuild(); }, GLFW.GLFW_KEY_UNKNOWN, -1);
            addDrawableChild(cycleBtn(x, y, w, "Filter", BASE_FINDER_MODES, baseFinderMode, v -> { baseFinderMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Render", BASE_FINDER_RENDERS, baseFinderRender, v -> { baseFinderRender = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Range", 16f, 1024f, baseFinderRange, v -> baseFinderRange = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Chests: " + (baseFinderChest ? "§aON" : "§cOFF")), b -> { baseFinderChest = !baseFinderChest; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Shulkers: " + (baseFinderShulker ? "§aON" : "§cOFF")), b -> { baseFinderShulker = !baseFinderShulker; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Wood/Caves: " + (baseFinderWood ? "§aON" : "§cOFF")), b -> { baseFinderWood = !baseFinderWood; rebuild(); }).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Carpet: " + (baseFinderCarpet ? "§aON" : "§cOFF")), b -> { baseFinderCarpet = !baseFinderCarpet; rebuild(); }).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Unusual: " + (baseFinderUnusual ? "§aON" : "§cOFF")), b -> { baseFinderUnusual = !baseFinderUnusual; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "HUD Settings", hudOn, b -> { hudOn = !hudOn; rebuild(); }, GLFW.GLFW_KEY_UNKNOWN, -1);
            addDrawableChild(ButtonWidget.builder(Text.literal("Target HUD: " + (targetHudOn ? "§aON" : "§cOFF")), b -> { targetHudOn = !targetHudOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;
            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a5 Advanced Features (AI / Crystal / FakeLag / Food)"), b -> {
                MinecraftClient.getInstance().setScreen(new PypsenFeatures.FeaturesScreen(this));
            }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Server Crasher (local bench)", serverCrasherOn, b -> { serverCrasherOn = !serverCrasherOn; rebuild(); }, keyCrasher, 21);
            addDrawableChild(cycleBtn(x, y, w, "Mode", CRASHER_MODES, crasherMode, v -> { crasherMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Timer", CRASHER_TIMERS, crasherTimerMode, v -> { crasherTimerMode = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Visual: " + (crasherVisual ? "§aON" : "§cOFF")), b -> { crasherVisual = !crasherVisual; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "GodMode", godModeOn, b -> { godModeOn = !godModeOn; rebuild(); }, keyGodMode, 22);
            addDrawableChild(cycleBtn(x, y, w, "Mode", GODMODE_MODES, godModeMode, v -> { godModeMode = v; rebuild(); })); y += 26;
            y = section(x, y, w, "AutoArmor", autoArmorOn, b -> { autoArmorOn = !autoArmorOn; rebuild(); }, keyAutoArmor, 23); y += 4;

            y = section(x, y, w, "FastBow", fastBowOn, b -> { fastBowOn = !fastBowOn; rebuild(); }, keyFastBow, 24);
            addDrawableChild(ButtonWidget.builder(Text.literal("BowJump: " + (bowJump ? "§aON" : "§cOFF")), b -> { bowJump = !bowJump; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            // ── EXPERIMENTAL FEATURES (now available from the classic menu) ──
            addDrawableChild(ButtonWidget.builder(Text.literal("§d--- EXPERIMENTAL ---"), b -> {}).dimensions(x, y, w, 20).build()); y += 22;

            y = section(x, y, w, "AI KillAura", PypsenFeatures.aiAuraOn, b -> { PypsenFeatures.aiAuraOn = !PypsenFeatures.aiAuraOn; rebuild(); }, PypsenFeatures.keyAiAura, -1);
            addDrawableChild(cycleBtn(x, y, w, "AI Preset", PypsenFeatures.AI_PRESETS, PypsenFeatures.aiPreset, v -> { PypsenFeatures.aiPreset = v; PypsenFeatures.applyPreset(v); rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "AI Target", PypsenFeatures.AI_TARGET_MODES, PypsenFeatures.aiTargetMode, v -> { PypsenFeatures.aiTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "AI Range", 2f, 16f, PypsenFeatures.aiRange, v -> PypsenFeatures.aiRange = v)); y += 22;
            addDrawableChild(slider(x, y, w, "AI CPS", 1f, 20f, PypsenFeatures.aiCps, v -> PypsenFeatures.aiCps = v)); y += 26;

            y = section(x, y, w, "Crystal Aura", PypsenFeatures.crystalAuraOn, b -> { PypsenFeatures.crystalAuraOn = !PypsenFeatures.crystalAuraOn; rebuild(); }, PypsenFeatures.keyCrystalAura, -1);
            addDrawableChild(cycleBtn(x, y, w, "Mode", PypsenFeatures.CRYSTAL_MODES, PypsenFeatures.crystalMode, v -> { PypsenFeatures.crystalMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Target", PypsenFeatures.CRYSTAL_TARGET_MODES, PypsenFeatures.crystalTargetMode, v -> { PypsenFeatures.crystalTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Range", 2f, 12f, PypsenFeatures.crystalRange, v -> PypsenFeatures.crystalRange = v)); y += 22;
            addDrawableChild(slider(x, y, w, "CPS", 1f, 20f, PypsenFeatures.crystalCps, v -> PypsenFeatures.crystalCps = v)); y += 26;

            y = section(x, y, w, "FakeLag", PypsenFeatures.fakeLagOn, b -> { PypsenFeatures.fakeLagOn = !PypsenFeatures.fakeLagOn; rebuild(); }, PypsenFeatures.keyFakeLag, -1);
            addDrawableChild(slider(x, y, w, "Delay ms", 20f, 1000f, PypsenFeatures.fakeLagDelay, v -> PypsenFeatures.fakeLagDelay = v)); y += 26;

            y = section(x, y, w, "AutoEat", PypsenFeatures.autoEatOn, b -> { PypsenFeatures.autoEatOn = !PypsenFeatures.autoEatOn; rebuild(); }, PypsenFeatures.keyAutoEat, -1);
            addDrawableChild(slider(x, y, w, "Eat at HP", 1f, 20f, PypsenFeatures.autoEatHealth, v -> PypsenFeatures.autoEatHealth = v)); y += 26;

            y = section(x, y, w, "Visual FX", PypsenFeatures.fxOn, b -> { PypsenFeatures.fxOn = !PypsenFeatures.fxOn; rebuild(); }, PypsenFeatures.keyFx, -1);
            addDrawableChild(ButtonWidget.builder(Text.literal("More experimental settings >>>"), b -> {
                MinecraftClient.getInstance().setScreen(new PypsenFeatures.FeaturesScreen(this));
            }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Optimization", optimizationOn, b -> { optimizationOn = !optimizationOn; rebuild(); }, GLFW.GLFW_KEY_UNKNOWN, -1);
            addDrawableChild(cycleBtn(x, y, w, "Profile", OPT_PROFILES, optProfile, v -> { optProfile = v; rebuild(); })); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("Drop All Items"), b -> { dropAllItems = true; }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Creative Drop: " + (creativeDropOn ? "§aQUEUED" : "§7READY")), b -> { creativeDropOn = true; }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(slider(x, y, w, "Drop Count", 1f, 64f, creativeDropCount, v -> creativeDropCount = v.intValue())); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Creative Duplicate: " + (creativeDupeOn ? "§aQUEUED" : "§7READY")), b -> { creativeDupeOn = true; }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Duplicate Held Item"), b -> { dupeMode = 3; creativeDupeOn = true; }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Dupe", DUPE_MODES, dupeMode, v -> { dupeMode = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Copies", 1f, 20f, creativeDupeCount, v -> creativeDupeCount = v.intValue())); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("§a§lSAVE CONFIG"), b -> {
                saveConfig();
                if (client != null) client.setScreen(new MenuScreen());
            }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(bindBtn(x, y, w, "Panic Key", keyPanic, 14)); y += 22;
            addDrawableChild(bindBtn(x, y, w, "Menu Key", keyMenu, 9)); y += 22;

            contentHeight = y - scroll;
        }

        private int section(int x, int y, int w, String name, boolean on, java.util.function.Consumer<ButtonWidget> toggle, int key, int bindId) {
            int halfW = w / 2 - 2;
            addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (on ? "\u00a7aON" : "\u00a7cOFF")), b -> toggle.accept(b)).dimensions(x, y, halfW, 20).build());
            if (bindId != -1) {
                addDrawableChild(bindBtn(x + halfW + 4, y, halfW, "Key", key, bindId));
            }
            return y + 22;
        }

        private ButtonWidget bindBtn(int x, int y, int w, String label, int key, int id) {
            String text = (bindingTarget == id) ? label + ": \u00a7e[...]" : label + ": \u00a7f" + keyName(key);
            return ButtonWidget.builder(Text.literal(text), b -> { bindingTarget = id; rebuild(); }).dimensions(x, y, w, 20).build();
        }

        private ButtonWidget cycleBtn(int x, int y, int w, String label, String[] options, int current, java.util.function.Consumer<Integer> setter) {
            return ButtonWidget.builder(Text.literal(label + ": \u00a7b" + options[current]), b -> setter.accept((current + 1) % options.length)).dimensions(x, y, w, 20).build();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (bindingTarget >= 0 && keyCode != GLFW.GLFW_KEY_ESCAPE) {
                switch (bindingTarget) {
                    case 0 -> keySpeed       = keyCode;
                    case 1 -> keyAura        = keyCode;
                    case 2 -> keyHitbox      = keyCode;
                    case 3 -> keyFly         = keyCode;
                    case 4 -> keyXray        = keyCode;
                    case 5 -> keyVelocity    = keyCode;
                    case 6 -> keyAim         = keyCode;
                    case 7 -> keyStrafe      = keyCode;
                    case 8 -> keyVisuals     = keyCode;
                    case 9 -> keyMenu        = keyCode;
                    case 10 -> keyAutoClicker= keyCode;
                    case 11 -> keyNoclip     = keyCode;
                    case 12 -> keyAntiFlag   = keyCode;
                    case 13 -> keyTriggerBot = keyCode;
                    case 14 -> keyPanic      = keyCode;
                    case 15 -> keyFreecam    = keyCode;
                    case 16 -> keyAutoTotem  = keyCode;
                    case 17 -> keyAimLock    = keyCode;
                    case 18 -> keySpider     = keyCode;
                    case 19 -> keyNoWeb      = keyCode;
                    case 20 -> keyFreeze     = keyCode;
                    case 21 -> keyCrasher    = keyCode;
                    case 22 -> keyGodMode    = keyCode;
                    case 23 -> keyAutoArmor  = keyCode;
                    case 24 -> keyFastBow    = keyCode;
                    case 25 -> keyAirJump    = keyCode;
                    case 26 -> keyNoFall     = keyCode;
                    case 27 -> keyJesus      = keyCode;
                    case 28 -> keyEsp        = keyCode;
                }
                bindingTarget = -1;
                rebuild();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE && bindingTarget >= 0) {
                bindingTarget = -1;
                rebuild();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (bindingTarget >= 0) {
                switch (bindingTarget) {
                    case 0 -> keySpeed       = GLFW.GLFW_KEY_UNKNOWN;
                    case 1 -> keyAura        = GLFW.GLFW_KEY_UNKNOWN;
                    case 2 -> keyHitbox      = GLFW.GLFW_KEY_UNKNOWN;
                    case 3 -> keyFly         = GLFW.GLFW_KEY_UNKNOWN;
                    case 4 -> keyXray        = GLFW.GLFW_KEY_UNKNOWN;
                    case 5 -> keyVelocity    = GLFW.GLFW_KEY_UNKNOWN;
                    case 6 -> keyAim         = GLFW.GLFW_KEY_UNKNOWN;
                    case 7 -> keyStrafe      = GLFW.GLFW_KEY_UNKNOWN;
                    case 8 -> keyVisuals     = GLFW.GLFW_KEY_UNKNOWN;
                    case 9 -> keyMenu        = GLFW.GLFW_KEY_UNKNOWN;
                    case 10 -> keyAutoClicker= GLFW.GLFW_KEY_UNKNOWN;
                    case 11 -> keyNoclip     = GLFW.GLFW_KEY_UNKNOWN;
                    case 12 -> keyAntiFlag   = GLFW.GLFW_KEY_UNKNOWN;
                    case 13 -> keyTriggerBot = GLFW.GLFW_KEY_UNKNOWN;
                    case 14 -> keyPanic      = GLFW.GLFW_KEY_UNKNOWN;
                    case 15 -> keyFreecam    = GLFW.GLFW_KEY_UNKNOWN;
                    case 16 -> keyAutoTotem  = GLFW.GLFW_KEY_UNKNOWN;
                    case 17 -> keyAimLock    = GLFW.GLFW_KEY_UNKNOWN;
                    case 18 -> keySpider     = GLFW.GLFW_KEY_UNKNOWN;
                    case 19 -> keyNoWeb      = GLFW.GLFW_KEY_UNKNOWN;
                    case 20 -> keyFreeze     = GLFW.GLFW_KEY_UNKNOWN;
                    case 21 -> keyCrasher    = GLFW.GLFW_KEY_UNKNOWN;
                    case 22 -> keyGodMode    = GLFW.GLFW_KEY_UNKNOWN;
                    case 23 -> keyAutoArmor  = GLFW.GLFW_KEY_UNKNOWN;
                    case 24 -> keyFastBow    = GLFW.GLFW_KEY_UNKNOWN;
                    case 25 -> keyAirJump    = GLFW.GLFW_KEY_UNKNOWN;
                    case 26 -> keyNoFall     = GLFW.GLFW_KEY_UNKNOWN;
                    case 27 -> keyJesus      = GLFW.GLFW_KEY_UNKNOWN;
                    case 28 -> keyEsp        = GLFW.GLFW_KEY_UNKNOWN;
                }
                bindingTarget = -1;
                rebuild();
                return true;
            }

            scroll += (int)(amount * 25);
            if (scroll > 0) scroll = 0;
            int minScroll = Math.min(0, height - 40 - contentHeight);
            if (scroll < minScroll) scroll = minScroll;
            rebuild();
            return true;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            ctx.drawCenteredTextWithShadow(textRenderer, "\u00a7a\u00a7l\u2588 Pypsen v2 \u2588 \u00a77(scroll for all settings)", width / 2, 10, 0x55FF55);
            int trackX = Math.max(2, Math.min(width - 6, width / 2 + 112));
            ctx.fill(trackX, 28, trackX + 3, height - 8, 0x44000000);
            if (contentHeight > height - 36) {
                int track = height - 36;
                int thumb = Math.max(18, track * track / contentHeight);
                int minScroll = Math.min(0, height - 40 - contentHeight);
                float t = minScroll == 0 ? 0f : (float) scroll / minScroll;
                int top = 28 + (int) (t * (track - thumb));
                ctx.fill(trackX, top, trackX + 3, top + thumb, 0xFF55FF55);
            }
            super.render(ctx, mx, my, d);
        }

        @Override public boolean shouldPause() { return false; }

        private void rebuild() {
            buildWidgets();
        }
    }

    // ══════════ MODERN GUI SCREEN ══════════
    public static class ModernGuiScreen extends Screen {
        public static int[] cX = {20, 180, 340, 500};
        public static int[] cY = {40, 40, 40, 40};
        public static Set<String> expanded = new HashSet<>();
        private static boolean layoutInitialized = false;
        private int[] curY = new int[4];
        private int[] contentHeight = new int[4];
        private int[] scroll = new int[4];
        private int dragging = -1;
        private int dragX, dragY;
        private int bindingTarget = -1;

        public ModernGuiScreen() { super(Text.literal("Modern GUI")); }

        private void resetLayout() {
            if (layoutInitialized) return;
            int cw = 150, gap = 8;
            int total = cw * 4 + gap * 3;
            if (width >= total + 16) {
                int start = (width - total) / 2;
                for (int i = 0; i < 4; i++) {
                    cX[i] = start + i * (cw + gap);
                    cY[i] = 40;
                }
            } else {
                // Narrow screens: 2 x 2 grid, each column scrolls independently.
                int left = 6;
                int right = Math.max(left + cw + gap, width - cw - 6);
                cX = new int[]{left, right, left, right};
                cY = new int[]{36, 36, Math.max(36, height / 2), Math.max(36, height / 2)};
            }
            layoutInitialized = true;
        }

        @Override
        protected void init() {
            resetLayout();
            for (int i = 0; i < 4; i++) {
                cX[i] = Math.max(2, Math.min(cX[i], width - 152));
                cY[i] = Math.max(24, Math.min(cY[i], Math.max(24, height - 44)));
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("§c◀ Back to Classic GUI"), b -> {
                useModernGui = false;
                if (client != null) client.setScreen(new MenuScreen());
            }).dimensions(5, 5, 150, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("§aSave Config"), b -> {
                saveConfig();
                if (client != null) client.setScreen(new ModernGuiScreen());
            }).dimensions(width - 110, 5, 105, 20).build());

            curY = new int[]{20, 20, 20, 20};

            // Combat
            addMod(0, "KillAura", auraOn, v->auraOn=v, () -> {
                addS(0, "Range", 1f, 20f, auraRange, v->auraRange=v);
                addC(0, "Mode", AURA_MODES, auraMode, v->auraMode=v);
                addC(0, "Target", AURA_TARGETS, auraTargetMode, v->auraTargetMode=v);
                addC(0, "Rotation", AURA_ROTS, auraRotMode, v->auraRotMode=v);
                addC(0, "Timing", AURA_TIMINGS, auraTimingMode, v->auraTimingMode=v);
                addC(0, "Aim Part", AURA_PARTS, auraTargetPart, v->auraTargetPart=v);
                addS(0, "Aim Jitter", 0f, 1f, auraAimJitter, v->auraAimJitter=v);
                addS(0, "Jitter Smooth", 0f, 0.95f, auraSmoothing, v->auraSmoothing=v);
                addS(0, "Stickiness", 0f, 100f, auraStickiness, v->auraStickiness=v);
                addS(0, "Rot Curve", 0f, 100f, auraRotCurve, v->auraRotCurve=v);
                addS(0, "Rot Speed", 1f, 180f, auraRotSpeed, v->auraRotSpeed=v);
                addS(0, "FOV", 10f, 360f, auraFov, v->auraFov=v);
                addB(0, "Predict", auraPredict, v->auraPredict=v);
                addB(0, "Randomize", auraRandomize, v->auraRandomize=v);
                addC(0, "Bypass", allBypassNames(), auraBypass, v->auraBypass=v);
                addB(0, "NOT NEAR", auraNotNear, v->auraNotNear=v);
                addS(0, "Keep Distance", 0.5f, 8f, auraNotNearRange, v->auraNotNearRange=v);
                addB(0, "Rotation", auraRotationEnabled, v->auraRotationEnabled=v);
                addB(0, "Strafe", auraStrafeEnabled, v->auraStrafeEnabled=v);
                addB(0, "Target Players", auraTargetPlayers, v->auraTargetPlayers=v);
                addB(0, "Target Mobs", auraTargetMobs, v->auraTargetMobs=v);
                addB(0, "Target Animals", auraTargetAnimals, v->auraTargetAnimals=v);
                addB(0, "AutoWeapon", autoWeapon, v->autoWeapon=v);
                addB(0, "Criticals", criticals, v->criticals=v);
                addB(0, "Shield Breaker", shieldBreaker, v->shieldBreaker=v);
                addB(0, "Water Crits", waterCrits, v->waterCrits=v);
                addB(0, "Jump & Attack", auraJump, v->auraJump=v);
                addB(0, "Through Walls", auraThroughWalls, v->auraThroughWalls=v);
                addB(0, "Hit in GUIs", auraHitInGuis, v->auraHitInGuis=v);
                addB(0, "AntiBot", antiBotOn, v->antiBotOn=v);
                addB(0, "SpinBot (Idle)", auraSpinBot, v->auraSpinBot=v);
                addBind(0, "Bind", 1);
            });
            addMod(0, "AutoClicker", autoClickerOn, v->autoClickerOn=v, () -> {
                addS(0, "CPS", 1f, 50f, clickerCps, v->clickerCps=v);
                addB(0, "Right Click", autoClickerRight, v->autoClickerRight=v);
                addB(0, "Farm Mode", autoClickerFarm, v->autoClickerFarm=v);
            });
            addMod(0, "TriggerBot", triggerBotOn, v->triggerBotOn=v, () -> {
                addS(0, "CPS", 1f, 50f, triggerCps, v->triggerCps=v);
                addS(0, "Range", 1f, 12f, triggerRange, v->triggerRange=v);
                addS(0, "Cooldown", 0f, 1f, triggerCooldown, v->triggerCooldown=v);
                addS(0, "Jitter %", 0f, 100f, triggerJitter, v->triggerJitter=v);
                addS(0, "Scan FOV", 1f, 180f, triggerFov, v->triggerFov=v);
                addB(0, "Weapon Only", triggerWeaponOnly, v->triggerWeaponOnly=v);
                addB(0, "Smart Delay", triggerSmartDelay, v->triggerSmartDelay=v);
                addB(0, "Wait Hurt Time", triggerWaitHurtTime, v->triggerWaitHurtTime=v);
                addB(0, "Through Walls", triggerThroughWalls, v->triggerThroughWalls=v);
                addB(0, "Auto Weapon", triggerAutoWeapon, v->triggerAutoWeapon=v);
                addB(0, "Criticals", triggerCriticals, v->triggerCriticals=v);
                addB(0, "Target Players", triggerTargetPlayers, v->triggerTargetPlayers=v);
                addB(0, "Target Mobs", triggerTargetMobs, v->triggerTargetMobs=v);
                addB(0, "Target Animals", triggerTargetAnimals, v->triggerTargetAnimals=v);
            });
            addMod(0, "AutoTotem", autoTotemOn, v->autoTotemOn=v, () -> {
                addS(0, "Health", 1f, 20f, autoTotemHealth, v->autoTotemHealth=v);
                addC(0, "Mode", TOTEM_MODES, autoTotemMode, v->autoTotemMode=v);
            });
            addMod(0, "AutoArmor", autoArmorOn, v->autoArmorOn=v, null);
            addMod(0, "FastBow", fastBowOn, v->fastBowOn=v, () -> {
                addB(0, "BowJump", bowJump, v->bowJump=v);
            });
            addMod(0, "AimAssist", aimOn, v->aimOn=v, () -> {
                addC(0, "Method", AIM_MODES, aimMode, v->aimMode=v);
                addS(0, "Range", 3f, 32f, aimRange, v->aimRange=v);
                addS(0, "Speed", 1f, 100f, aimSpeed, v->aimSpeed=v);
                addS(0, "FOV", 10f, 360f, aimFov, v->aimFov=v);
                addS(0, "Wobble", 0f, 10f, aimJitter, v->aimJitter=v);
                addC(0, "Priority", AURA_TARGETS, aimTargetMode, v->aimTargetMode=v);
                addB(0, "Players", aimTargetPlayers, v->aimTargetPlayers=v);
                addB(0, "Mobs", aimTargetMobs, v->aimTargetMobs=v);
                addB(0, "Animals", aimTargetAnimals, v->aimTargetAnimals=v);
            });
            addMod(0, "AimLock", aimLockOn, v->aimLockOn=v, null);
            addMod(0, "Hitbox", hitboxOn, v->hitboxOn=v, () -> {
                addS(0, "Size", 0.1f, 1000f, hitboxVal, v->hitboxVal=v);
                addB(0, "Render Fix", hitboxFix, v->hitboxFix=v);
            });
            addMod(0, "AntiBot", antiBotOn, v->antiBotOn=v, null);
            addMod(0, "AI KillAura", PypsenFeatures.aiAuraOn, v->PypsenFeatures.aiAuraOn=v, () -> {
                addC(0, "AI Preset", PypsenFeatures.AI_PRESETS, PypsenFeatures.aiPreset, v -> { PypsenFeatures.aiPreset = v; PypsenFeatures.applyPreset(v); });
                addS(0, "AI Range", 2f, 16f, PypsenFeatures.aiRange, v->PypsenFeatures.aiRange=v);
                addS(0, "AI CPS", 1f, 20f, PypsenFeatures.aiCps, v->PypsenFeatures.aiCps=v);
                addS(0, "AI Jitter Smooth", 0f, 0.95f, PypsenFeatures.aiJitterSmooth, v->PypsenFeatures.aiJitterSmooth=v);
            });
            addMod(0, "Crystal Aura", PypsenFeatures.crystalAuraOn, v->PypsenFeatures.crystalAuraOn=v, () -> {
                addC(0, "Mode", PypsenFeatures.CRYSTAL_MODES, PypsenFeatures.crystalMode, v->PypsenFeatures.crystalMode=v);
                addC(0, "Target", PypsenFeatures.CRYSTAL_TARGET_MODES, PypsenFeatures.crystalTargetMode, v->PypsenFeatures.crystalTargetMode=v);
                addS(0, "Range", 2f, 12f, PypsenFeatures.crystalRange, v->PypsenFeatures.crystalRange=v);
                addS(0, "CPS", 1f, 20f, PypsenFeatures.crystalCps, v->PypsenFeatures.crystalCps=v);
            });

            // Movement
            addMod(1, "Speed", speedOn, v->speedOn=v, () -> {
                addS(1, "Speed", 0.1f, 100f, speedVal, v->speedVal=v);
                addC(1, "Bypass", allBypassNames(), speedBypass, v->speedBypass=v);
            });
            addMod(1, "Fly", flyOn, v->flyOn=v, () -> {
                addS(1, "Speed", 0.1f, 50f, flySpeed, v->flySpeed=v);
                addC(1, "Bypass", allBypassNames(), flyBypass, v->flyBypass=v);
                addS(1, "Custom Vert", -1f, 1f, flyCustomVertical, v->flyCustomVertical=v);
                addS(1, "Custom Pulse", 0f, 1f, flyCustomPulse, v->flyCustomPulse=v);
            });
            addMod(1, "Spider", spiderOn, v->spiderOn=v, () -> {
                addC(1, "Mode", allSpiderModes(), spiderMode, v->spiderMode=v);
                addS(1, "Speed", 0.5f, 4f, spiderSpeed, v->spiderSpeed=v);
                addS(1, "Custom Pulse", 0f, 1f, spiderCustomPulse, v->spiderCustomPulse=v);
            });
            addMod(1, "NoWeb", noWebOn, v->noWebOn=v, () -> {
                addC(1, "Mode", NOWEB_MODES, noWebMode, v->noWebMode=v);
            });
            addMod(1, "Freeze", freezeOn, v->freezeOn=v, () -> {
                addC(1, "Mode", FREEZE_MODES, freezeMode, v->freezeMode=v);
                addB(1, "Visual", freezeVisual, v->freezeVisual=v);
                addBind(1, "Bind", 20);
            });
            addMod(1, "AirJump", airJumpOn, v->airJumpOn=v, () -> {
                addC(1, "Mode", AIRJUMP_MODES, airJumpMode, v->airJumpMode=v);
                addS(1, "Speed", 0.5f, 4f, airJumpSpeed, v->airJumpSpeed=v);
                addBind(1, "Bind", 25);
            });
            addMod(1, "NoFall", noFallOn, v->noFallOn=v, () -> {
                addC(1, "Mode", NOFALL_MODES, noFallMode, v->noFallMode=v);
                addBind(1, "Bind", 26);
            });
            addMod(1, "Jesus", jesusOn, v->jesusOn=v, () -> {
                addC(1, "Mode", JESUS_MODES, jesusMode, v->jesusMode=v);
                addBind(1, "Bind", 27);
            });
            addMod(1, "Noclip", noclipOn, v->noclipOn=v, () -> {
                addC(1, "Mode", allNoclipModes(), noclipBypass, v->noclipBypass=v);
                addC(1, "Direction", NOCLIP_DIRECTIONS, noclipDirection, v->noclipDirection=v);
                addS(1, "Step", 0.05f, 4f, noclipStep, v->noclipStep=v);
                addBind(1, "Bind", 11);
            });
            addMod(1, "Velocity", velocityOn, v->velocityOn=v, () -> {
                addC(1, "Mode", VELOCITY_MODES, velocityMode, v->velocityMode=v);
            });
            addMod(1, "Strafe", strafeOn, v->strafeOn=v, null);
            addMod(1, "Anti-Flag", antiFlagOn, v->antiFlagOn=v, null);

            // Visuals
            addMod(2, "XRay", xrayOn, v->{xrayOn=v; if(client!=null)client.worldRenderer.reload();}, () -> {
                addC(2, "Mode", XRAY_MODES, xrayMode, v->xrayMode=v);
                addC(2, "Bypass", XRAY_BYPASS_MODES, xrayBypass, v->xrayBypass=v);
                addS(2, "Far Range", 16f, 1024f, xrayRange, v->xrayRange=v);
                addB(2, "Far Scan", xrayFarScan, v->xrayFarScan=v);
                addB(2, "Anti-XRay", xrayAntiXray, v->xrayAntiXray=v);
                addC(2, "Stealth", XRAY_STEALTH_MODES, xrayStealth, v->xrayStealth=v);
                addB(2, "Highlight", xrayHighlight, v->xrayHighlight=v);
                int y = cY[2] + curY[2] + scroll[2];
                addAt(2, y, ButtonWidget.builder(Text.literal("Select Blocks..."), b -> {
                    if(client!=null) client.setScreen(new XrayScreen(this));
                }).dimensions(cX[2] + 5, y, 140, 20).build());
                curY[2] += 22;
            });
            addMod(2, "Base Finder", baseFinderOn, v->baseFinderOn=v, () -> {
                addC(2, "Filter", BASE_FINDER_MODES, baseFinderMode, v->baseFinderMode=v);
                addC(2, "Render", BASE_FINDER_RENDERS, baseFinderRender, v->baseFinderRender=v);
                addS(2, "Range", 16f, 1024f, baseFinderRange, v->baseFinderRange=v);
                addB(2, "Chests", baseFinderChest, v->baseFinderChest=v);
                addB(2, "Shulkers", baseFinderShulker, v->baseFinderShulker=v);
                addB(2, "Wood/Caves", baseFinderWood, v->baseFinderWood=v);
                addB(2, "Carpet", baseFinderCarpet, v->baseFinderCarpet=v);
                addB(2, "Unusual", baseFinderUnusual, v->baseFinderUnusual=v);
            });
            addMod(2, "Glowing ESP", visualsOn, v->visualsOn=v, () -> {
                addC(2, "Target", VISUALS_TARGETS, visualsTargetMode, v->visualsTargetMode=v);
                addBind(2, "Bind", 8);
            });
            addMod(2, "2D ESP", espOn, v->espOn=v, () -> {
                addS(2, "Range", 5f, 256f, espRange, v->espRange=v);
                addB(2, "Boxes", espBoxesOn, v->espBoxesOn=v);
                addB(2, "Names", espNamesOn, v->espNamesOn=v);
                addB(2, "Health", espHealthOn, v->espHealthOn=v);
                addB(2, "Distance", espDistanceOn, v->espDistanceOn=v);
                addB(2, "Fill", espFillOn, v->espFillOn=v);
                addB(2, "Tracers", espTracersOn, v->espTracersOn=v);
                addB(2, "Players", espPlayersOn, v->espPlayersOn=v);
                addB(2, "Mobs", espMobsOn, v->espMobsOn=v);
                addB(2, "Animals", espAnimalsOn, v->espAnimalsOn=v);
                addB(2, "Items", espItemsOn, v->espItemsOn=v);
                addBind(2, "Bind", 28);
            });
            addMod(2, "Target HUD", targetHudOn, v->targetHudOn=v, null);
            addMod(2, "Player ESP", playerEspOn, v->playerEspOn=v, null);
            addMod(2, "FullBright", fullBrightOn, v->fullBrightOn=v, null);
            addMod(2, "Ambient", ambientOn, v->ambientOn=v, () -> {
                addB(2, "Rainbow", ambientRainbow, v->ambientRainbow=v);
                addC(2, "Mode", AMBIENT_MODES, ambientMode, v->ambientMode=v);
                addS(2, "Strength", 0f, 1f, ambientStrength, v->ambientStrength=v);
                addS(2, "Hue", 0f, 360f, ambientHue, v->ambientHue=v);
            });
            addMod(2, "Spins", spinOn, v->spinOn=v, () -> {
                addC(2, "Mode", SPIN_MODES, spinMode, v->spinMode=v);
                addS(2, "Speed", 0f, 8f, spinSpeed, v->spinSpeed=v);
                addS(2, "Size", 12f, 160f, spinSize, v->spinSize=v);
            });
            addMod(2, "NameHider", nameHiderOn, v->nameHiderOn=v, null);
            addMod(2, "See Invis", seeInvisOn, v->seeInvisOn=v, null);
            addMod(2, "Target ESP", targetEspOn, v->targetEspOn=v, null);
            addMod(2, "No Fire", noFireOn, v->noFireOn=v, null);
            addMod(2, "No Nausea", noNauseaOn, v->noNauseaOn=v, null);
            addMod(2, "NoHurtCam", noHurtCamOn, v->noHurtCamOn=v, null);

            // Misc
            addMod(3, "Optimization", optimizationOn, v->optimizationOn=v, () -> {
                addC(3, "Profile", OPT_PROFILES, optProfile, v->optProfile=v);
            });
            addMod(3, "Freecam", freecamOn, v->{
                freecamOn=v;
                if (client != null && client.player != null) {
                    if (freecamOn) {
                        freecamPos = client.player.getPos();
                        freecamYaw = client.player.getYaw();
                        freecamPitch = client.player.getPitch();
                    } else {
                        client.player.noClip = false;
                        client.player.getAbilities().flying = false;
                        if (freecamPos != null) {
                            client.player.setPosition(freecamPos);
                            client.player.setYaw(freecamYaw);
                            client.player.setPitch(freecamPitch);
                            client.player.setVelocity(0, 0, 0);
                        }
                    }
                }
            }, null);
            addMod(3, "GodMode", godModeOn, v->godModeOn=v, () -> {
                addC(3, "Mode", GODMODE_MODES, godModeMode, v->godModeMode=v);
                addBind(3, "Bind", 22);
            });
            addMod(3, "Server Crasher (local bench)", serverCrasherOn, v->serverCrasherOn=v, () -> {
                addC(3, "Mode", CRASHER_MODES, crasherMode, v->crasherMode=v);
                addC(3, "Timer", CRASHER_TIMERS, crasherTimerMode, v->crasherTimerMode=v);
                addB(3, "Visual", crasherVisual, v->crasherVisual=v);
            });
            int y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Drop All Items"), b -> { dropAllItems = true; }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Creative Drop"), b -> { creativeDropOn = true; }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            addS(3, "Drop Count", 1f, 64f, creativeDropCount, v->creativeDropCount=v.intValue());
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Creative Duplicate"), b -> { creativeDupeOn = true; }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Duplicate Held"), b -> { dupeMode = 3; creativeDupeOn = true; }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            addC(3, "Dupe", DUPE_MODES, dupeMode, v->dupeMode=v);
            addS(3, "Copies", 1f, 20f, creativeDupeCount, v->creativeDupeCount=v.intValue());
            addMod(3, "Panic", panicOn, v->panicOn=v, null);
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("\u00a5 Advanced Features"), b -> {
                MinecraftClient.getInstance().setScreen(new PypsenFeatures.FeaturesScreen(this));
            }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Account Switcher"), b -> {
                MinecraftClient.getInstance().setScreen(new AccountScreen(this));
            }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;
            y3 = cY[3] + curY[3] + scroll[3];
            addAt(3, y3, ButtonWidget.builder(Text.literal("Bypass List"), b -> {
                MinecraftClient.getInstance().setScreen(new BypassScreen(this));
            }).dimensions(cX[3], y3, 150, 20).build()); curY[3] += 22;

            for (int i = 0; i < 4; i++) contentHeight[i] = curY[i];
        }

        private <T extends net.minecraft.client.gui.widget.ClickableWidget> void addAt(int col, int y, T widget) {
            // Only widgets inside the column viewport are created. Everything
            // else is reached with the mouse wheel, so nothing sticks out.
            if (y >= cY[col] + 20 && y + 20 <= height - 4) addDrawableChild(widget);
        }

        private void addMod(int col, String name, boolean val, java.util.function.Consumer<Boolean> act, Runnable settings) {
            int x = cX[col];
            int y = cY[col] + curY[col] + scroll[col];
            int w = settings != null ? 130 : 150;
            addAt(col, y, ButtonWidget.builder(Text.literal(name + ": " + (val?"§aON":"§cOFF")), b -> { act.accept(!val); rebuild(); }).dimensions(x, y, w, 20).build());
            if (settings != null) {
                boolean isExp = expanded.contains(name);
                addAt(col, y, ButtonWidget.builder(Text.literal(isExp ? "-" : "+"), b -> {
                    if (isExp) expanded.remove(name); else expanded.add(name);
                    rebuild();
                }).dimensions(x + 130, y, 20, 20).build());
                curY[col] += 22;
                if (isExp) {
                    settings.run();
                }
            } else {
                curY[col] += 22;
            }
        }

        private void addS(int col, String label, float min, float max, float val, java.util.function.Consumer<Float> setter) {
            int y = cY[col] + curY[col] + scroll[col];
            addAt(col, y, slider(cX[col] + 5, y, 140, label, min, max, val, v -> setter.accept(v)));
            curY[col] += 22;
        }

        private void addC(int col, String label, String[] options, int current, java.util.function.Consumer<Integer> setter) {
            int y = cY[col] + curY[col] + scroll[col];
            addAt(col, y, ButtonWidget.builder(Text.literal(label + ": §b" + options[MathHelper.clamp(current, 0, options.length - 1)]), b -> { setter.accept((current + 1) % options.length); rebuild(); }).dimensions(cX[col] + 5, y, 140, 20).build());
            curY[col] += 22;
        }

        private void addB(int col, String label, boolean val, java.util.function.Consumer<Boolean> act) {
            int y = cY[col] + curY[col] + scroll[col];
            addAt(col, y, ButtonWidget.builder(Text.literal(label + ": " + (val?"§aON":"§cOFF")), b -> { act.accept(!val); rebuild(); }).dimensions(cX[col] + 5, y, 140, 20).build());
            curY[col] += 22;
        }

        private int getKeyCode(int id) {
            return switch(id) {
                case 0 -> keySpeed; case 1 -> keyAura; case 2 -> keyHitbox; case 3 -> keyFly;
                case 4 -> keyXray; case 5 -> keyVelocity; case 6 -> keyAim; case 7 -> keyStrafe;
                case 8 -> keyVisuals; case 9 -> keyMenu; case 10 -> keyAutoClicker; case 11 -> keyNoclip;
                case 12 -> keyAntiFlag; case 13 -> keyTriggerBot; case 14 -> keyPanic; case 15 -> keyFreecam;
                case 16 -> keyAutoTotem; case 17 -> keyAimLock; case 18 -> keySpider; case 19 -> keyNoWeb;
                case 20 -> keyFreeze; case 21 -> keyCrasher; case 22 -> keyGodMode; case 23 -> keyAutoArmor;
                case 24 -> keyFastBow; case 25 -> keyAirJump; case 26 -> keyNoFall; case 27 -> keyJesus;
                case 28 -> keyEsp;
                default -> GLFW.GLFW_KEY_UNKNOWN;
            };
        }

        private void addBind(int col, String label, int keyId) {
            int y = cY[col] + curY[col] + scroll[col];
            int currentKey = getKeyCode(keyId);
            String text = (bindingTarget == keyId) ? label + ": §e[...]" : label + ": §f" + keyName(currentKey);
            addAt(col, y, ButtonWidget.builder(Text.literal(text), b -> { bindingTarget = keyId; rebuild(); }).dimensions(cX[col] + 5, y, 140, 20).build());
            curY[col] += 22;
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            if (bindingTarget != -1) {
                if (button == 2) { // Middle click to unbind
                    switch (bindingTarget) {
                        case 0 -> keySpeed = GLFW.GLFW_KEY_UNKNOWN; case 1 -> keyAura = GLFW.GLFW_KEY_UNKNOWN;
                        case 2 -> keyHitbox = GLFW.GLFW_KEY_UNKNOWN; case 3 -> keyFly = GLFW.GLFW_KEY_UNKNOWN;
                        case 4 -> keyXray = GLFW.GLFW_KEY_UNKNOWN; case 5 -> keyVelocity = GLFW.GLFW_KEY_UNKNOWN;
                        case 6 -> keyAim = GLFW.GLFW_KEY_UNKNOWN; case 7 -> keyStrafe = GLFW.GLFW_KEY_UNKNOWN;
                        case 8 -> keyVisuals = GLFW.GLFW_KEY_UNKNOWN; case 9 -> keyMenu = GLFW.GLFW_KEY_UNKNOWN;
                        case 10 -> keyAutoClicker = GLFW.GLFW_KEY_UNKNOWN; case 11 -> keyNoclip = GLFW.GLFW_KEY_UNKNOWN;
                        case 12 -> keyAntiFlag = GLFW.GLFW_KEY_UNKNOWN; case 13 -> keyTriggerBot = GLFW.GLFW_KEY_UNKNOWN;
                        case 14 -> keyPanic = GLFW.GLFW_KEY_UNKNOWN; case 15 -> keyFreecam = GLFW.GLFW_KEY_UNKNOWN;
                        case 16 -> keyAutoTotem = GLFW.GLFW_KEY_UNKNOWN; case 17 -> keyAimLock = GLFW.GLFW_KEY_UNKNOWN;
                        case 18 -> keySpider = GLFW.GLFW_KEY_UNKNOWN; case 19 -> keyNoWeb = GLFW.GLFW_KEY_UNKNOWN;
                        case 20 -> keyFreeze = GLFW.GLFW_KEY_UNKNOWN; case 21 -> keyCrasher = GLFW.GLFW_KEY_UNKNOWN;
                        case 22 -> keyGodMode = GLFW.GLFW_KEY_UNKNOWN; case 23 -> keyAutoArmor = GLFW.GLFW_KEY_UNKNOWN;
                        case 24 -> keyFastBow = GLFW.GLFW_KEY_UNKNOWN; case 25 -> keyAirJump = GLFW.GLFW_KEY_UNKNOWN;
                        case 26 -> keyNoFall = GLFW.GLFW_KEY_UNKNOWN; case 27 -> keyJesus = GLFW.GLFW_KEY_UNKNOWN;
                        case 28 -> keyEsp = GLFW.GLFW_KEY_UNKNOWN;
                    }
                    bindingTarget = -1;
                    rebuild();
                    return true;
                }
            }
            if (button != 0) return super.mouseClicked(mx, my, button);
            for (int i = 0; i < 4; i++) {
                if (mx >= cX[i] && mx <= cX[i] + 150 && my >= cY[i] && my <= cY[i] + 20) {
                    dragging = i;
                    dragX = (int)mx - cX[i];
                    dragY = (int)my - cY[i];
                    return true;
                }
            }
            return super.mouseClicked(mx, my, button);
        }

        @Override
        public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
            if (dragging >= 0) {
                // Keep the whole window on screen while dragging.
                cX[dragging] = Math.max(2, Math.min((int) mx - dragX, width - 152));
                cY[dragging] = Math.max(24, Math.min((int) my - dragY, Math.max(24, height - 44)));
                rebuild();
                return true;
            }
            return super.mouseDragged(mx, my, button, dx, dy);
        }

        @Override
        public boolean mouseReleased(double mx, double my, int button) {
            dragging = -1;
            return super.mouseReleased(mx, my, button);
        }

        @Override
        public boolean mouseScrolled(double mx, double my, double amount) {
            int col = -1;
            for (int i = 0; i < 4; i++) {
                if (mx >= cX[i] && mx <= cX[i] + 150 && my >= cY[i] - 4) { col = i; break; }
            }
            int[] targets = col >= 0 ? new int[]{col} : new int[]{0, 1, 2, 3};
            for (int i : targets) {
                scroll[i] += (int) (amount * 22);
                if (scroll[i] > 0) scroll[i] = 0;
                int minScroll = Math.min(0, height - 4 - cY[i] - contentHeight[i]);
                if (scroll[i] < minScroll) scroll[i] = minScroll;
            }
            rebuild();
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (bindingTarget != -1) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    bindingTarget = -1;
                    rebuild();
                    return true;
                }
                switch (bindingTarget) {
                    case 0 -> keySpeed = keyCode; case 1 -> keyAura = keyCode;
                    case 2 -> keyHitbox = keyCode; case 3 -> keyFly = keyCode;
                    case 4 -> keyXray = keyCode; case 5 -> keyVelocity = keyCode;
                    case 6 -> keyAim = keyCode; case 7 -> keyStrafe = keyCode;
                    case 8 -> keyVisuals = keyCode; case 9 -> keyMenu = keyCode;
                    case 10 -> keyAutoClicker = keyCode; case 11 -> keyNoclip = keyCode;
                    case 12 -> keyAntiFlag = keyCode; case 13 -> keyTriggerBot = keyCode;
                    case 14 -> keyPanic = keyCode; case 15 -> keyFreecam = keyCode;
                    case 16 -> keyAutoTotem = keyCode; case 17 -> keyAimLock = keyCode;
                    case 18 -> keySpider = keyCode; case 19 -> keyNoWeb = keyCode;
                    case 20 -> keyFreeze = keyCode; case 21 -> keyCrasher = keyCode;
                    case 22 -> keyGodMode = keyCode; case 23 -> keyAutoArmor = keyCode;
                    case 24 -> keyFastBow = keyCode; case 25 -> keyAirJump = keyCode;
                    case 26 -> keyNoFall = keyCode; case 27 -> keyJesus = keyCode;
                    case 28 -> keyEsp = keyCode;
                }
                bindingTarget = -1;
                rebuild();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            String[] titles = {"§c⚔ Combat", "§b✈ Movement", "§d✨ Visuals", "§7⚙ Misc"};
            for (int i = 0; i < 4; i++) {
                ctx.fill(cX[i], cY[i], cX[i] + 150, cY[i] + 20, 0xFF222222); // Header
                int bottom = Math.min(height - 2, cY[i] + curY[i] + scroll[i] + 4);
                if (bottom > cY[i] + 20) {
                    ctx.fill(cX[i], cY[i] + 20, cX[i] + 150, bottom, 0xAA000000); // Body
                }
                ctx.drawTextWithShadow(textRenderer, Text.literal(titles[i]), cX[i] + 5, cY[i] + 6, 0xFFFFFF);

                int minScroll = Math.min(0, height - 4 - cY[i] - contentHeight[i]);
                if (contentHeight[i] > height - cY[i] - 24 && minScroll < 0) {
                    int track = height - 4 - (cY[i] + 20);
                    int thumb = Math.max(18, track * (height - 4 - cY[i]) / Math.max(1, contentHeight[i]));
                    float t = (float) scroll[i] / minScroll;
                    int top = cY[i] + 20 + (int) (t * (track - thumb));
                    ctx.fill(cX[i] + 147, cY[i] + 20, cX[i] + 150, height - 4, 0x44000000);
                    ctx.fill(cX[i] + 147, top, cX[i] + 150, top + thumb, 0xFF55FF55);
                }
            }
            super.render(ctx, mx, my, d);
        }

        @Override public boolean shouldPause() { return false; }
        private void rebuild() { clearChildren(); init(); }
    }

    private static SliderWidget slider(int x, int y, int w, String label, float min, float max, float cur, java.util.function.Consumer<Float> cb) {
        return new SliderWidget(x, y, w, 20, Text.literal(String.format("%s: %.1f", label, cur)), (cur - min) / (max - min)) {
            @Override protected void updateMessage() { setMessage(Text.literal(String.format("%s: %.1f", label, min + value * (max - min)))); }
            @Override protected void applyValue() { cb.accept((float)(min + value * (max - min))); }
        };
    }
}
