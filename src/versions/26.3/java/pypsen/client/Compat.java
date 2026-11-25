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

/** Compatibility helpers for the 26.3 Mojang-name API. */
public final class Compat {
    private Compat() {}

    public static Vec3d entityPos(Entity e) {
        return new Vec3d(e.getX(), e.getY(), e.getZ());
    }

    public static Vec3d cameraPos(Camera camera) {
        return camera.position();
    }

    public static void addParticle(ClientWorld world, ParticleEffect effect,
                                   double x, double y, double z,
                                   double vx, double vy, double vz) {
        world.addParticle(effect, false, false, x, y, z, vx, vy, vz);
    }

    public static float moveForward(ClientPlayerEntity p) {
        var i = p.getLastSentInput();
        return (i.forward() ? 1f : 0f) - (i.backward() ? 1f : 0f);
    }

    public static float moveSideways(ClientPlayerEntity p) {
        var i = p.getLastSentInput();
        return (i.right() ? 1f : 0f) - (i.left() ? 1f : 0f);
    }

    public static boolean isSword(ItemStack stack) {
        return stack.typeHolder().is(ItemTags.SWORDS);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.typeHolder().is(ItemTags.AXES);
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.typeHolder().is(ItemTags.HEAD_ARMOR) || stack.typeHolder().is(ItemTags.CHEST_ARMOR)
            || stack.typeHolder().is(ItemTags.LEG_ARMOR) || stack.typeHolder().is(ItemTags.FOOT_ARMOR);
    }

    public static int armorSlot(ItemStack stack) {
        if (stack.typeHolder().is(ItemTags.HEAD_ARMOR)) return 0;
        if (stack.typeHolder().is(ItemTags.CHEST_ARMOR)) return 1;
        if (stack.typeHolder().is(ItemTags.LEG_ARMOR)) return 2;
        if (stack.typeHolder().is(ItemTags.FOOT_ARMOR)) return 3;
        return -1;
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static ItemStack armorStack(PlayerEntity p, int type) {
        if (type < 0 || type >= ARMOR_SLOTS.length) return ItemStack.EMPTY;
        return p.getItemBySlot(ARMOR_SLOTS[type]);
    }

    public static int enchantLevel(ItemStack stack, String id) {
        var world = MinecraftClient.getInstance().level;
        if (world == null || stack.isEmpty()) return 0;
        var lookup = world.registryAccess().lookupOrThrow(RegistryKeys.ENCHANTMENT);
        var key = net.minecraft.registry.RegistryKey.create(RegistryKeys.ENCHANTMENT,
            Identifier.fromNamespaceAndPath("minecraft", id));
        return lookup.get(key)
            .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, stack))
            .orElse(0);
    }

    private static int armorProtection(ItemStack stack) {
        String path = Registries.ITEM.getKey(stack.getItem()).getPath();
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
            ItemStack stack = p.getInventory().getItem(i);
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
