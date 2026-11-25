package pypsen.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/** Compatibility helpers for the 1.21+ networking/component API. */
public final class Compat {
    private Compat() {}

    public static Vec3d entityPos(Entity e) {
        return new Vec3d(e.getX(), e.getY(), e.getZ());
    }

    public static Vec3d cameraPos(Camera camera) {
        return camera.getCameraPos();
    }

    public static void addParticle(ClientWorld world, ParticleEffect effect,
                                   double x, double y, double z,
                                   double vx, double vy, double vz) {
        world.addParticleClient(effect, false, false, x, y, z, vx, vy, vz);
    }

    public static float moveForward(ClientPlayerEntity p) {
        var i = p.input.playerInput;
        return (i.forward() ? 1f : 0f) - (i.backward() ? 1f : 0f);
    }

    public static float moveSideways(ClientPlayerEntity p) {
        var i = p.input.playerInput;
        return (i.right() ? 1f : 0f) - (i.left() ? 1f : 0f);
    }

    public static boolean isSword(ItemStack stack) {
        return stack.isIn(ItemTags.SWORDS);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.isIn(ItemTags.AXES);
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.isIn(ItemTags.HEAD_ARMOR) || stack.isIn(ItemTags.CHEST_ARMOR)
            || stack.isIn(ItemTags.LEG_ARMOR) || stack.isIn(ItemTags.FOOT_ARMOR);
    }

    public static int armorSlot(ItemStack stack) {
        if (stack.isIn(ItemTags.HEAD_ARMOR)) return 0;
        if (stack.isIn(ItemTags.CHEST_ARMOR)) return 1;
        if (stack.isIn(ItemTags.LEG_ARMOR)) return 2;
        if (stack.isIn(ItemTags.FOOT_ARMOR)) return 3;
        return -1;
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static ItemStack armorStack(PlayerEntity p, int type) {
        if (type < 0 || type >= ARMOR_SLOTS.length) return ItemStack.EMPTY;
        return p.getEquippedStack(ARMOR_SLOTS[type]);
    }

    public static int enchantLevel(ItemStack stack, String id) {
        var world = MinecraftClient.getInstance().world;
        if (world == null || stack.isEmpty()) return 0;
        var registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return registry.getEntry(Identifier.of("minecraft", id))
            .map(entry -> EnchantmentHelper.getLevel(entry, stack))
            .orElse(0);
    }

    private static int armorProtection(ItemStack stack) {
        String path = Registries.ITEM.getId(stack.getItem()).getPath();
        if (path.contains("netherite")) return 5;
        if (path.contains("diamond")) return 4;
        if (path.contains("iron")) return 3;
        if (path.contains("chain")) return 2;
        if (path.contains("turtle")) return 2;
        return 1;
    }

    public static int getArmorScore(ItemStack stack) {
        if (!isArmor(stack)) return -1;
        return armorProtection(stack) + enchantLevel(stack, "protection");
    }

    public static float weaponDamage(ItemStack stack) {
        float damage = isSword(stack) ? 4f : isAxe(stack) ? 7f : 0f;
        if (damage <= 0f) return -1f;
        return damage + enchantLevel(stack, "sharpness") * 0.5f;
    }

    public static int findBestWeapon(ClientPlayerEntity p, boolean preferAxe) {
        int bestSlot = -1;
        float bestDamage = -1f;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            float damage = weaponDamage(stack);
            if (damage < 0f) continue;
            if (preferAxe && isAxe(stack)) damage += 1000f;
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
