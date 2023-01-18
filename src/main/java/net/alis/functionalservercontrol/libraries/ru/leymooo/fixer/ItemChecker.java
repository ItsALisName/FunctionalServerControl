package net.alis.functionalservercontrol.libraries.ru.leymooo.fixer;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class ItemChecker {

    private final HashSet<String> nbt = new HashSet<>();
    private final HashSet<Material> tiles = new HashSet<>();

    public ItemChecker() {
        nbt.addAll(Arrays.asList("ActiveEffects", "Command", "CustomName", "AttributeModifiers", "Unbreakable"));
        getProtectionSettings().getItemFixerIgnoredTags().forEach(nbt::remove);
        tiles.addAll(Arrays.asList(
                Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.DROPPER, Material.DISPENSER, Material.LEGACY_COMMAND_MINECART, Material.HOPPER_MINECART,
                Material.HOPPER, Material.LEGACY_BREWING_STAND_ITEM, Material.BEACON, Material.LEGACY_SIGN, Material.LEGACY_MOB_SPAWNER, Material.NOTE_BLOCK, Material.LEGACY_COMMAND, Material.JUKEBOX
        ));
    }

    @SuppressWarnings("rawtypes")
    public boolean isCrashSkull(NbtCompound tag) {
        if (tag.containsKey("SkullOwner")) {
            NbtCompound skullOwner = tag.getCompound("SkullOwner");
            if (skullOwner.containsKey("Properties")) {
                NbtCompound properties = skullOwner.getCompound("Properties");
                if (properties.containsKey("textures")) {
                    NbtList<NbtBase> textures = properties.getList("textures");
                    for (NbtBase texture : textures.asCollection()) {
                        if (texture instanceof NbtCompound) {
                            if (((NbtCompound) texture).containsKey("Value")) {
                                if (((NbtCompound) texture).getString("Value").trim().length() > 0) {
                                    String decoded;
                                    try {
                                        decoded = new String(Base64.getDecoder().decode(((NbtCompound) texture).getString("Value")));
                                    } catch (Exception e) {
                                        tag.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded.isEmpty()) {
                                        tag.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded.contains("textures") && decoded.contains("SKIN")) {
                                        if (decoded.contains("url")) {
                                            String headUrl = null;
                                            try {
                                                headUrl = decoded.split("url\":")[1].replace("}", "").replace("\"", "");
                                            } catch (ArrayIndexOutOfBoundsException e) {
                                                tag.remove("SkullOwner");
                                                return true;
                                            }
                                            if (headUrl.isEmpty() || headUrl.trim().length() == 0) {
                                                tag.remove("SkullOwner");
                                                return true;
                                            }
                                            if (headUrl.startsWith("http://textures.minecraft.net/texture/") || headUrl.startsWith("https://textures.minecraft.net/texture/")) {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tag.remove("SkullOwner");
                return true;
            }
        }
        return false;
    }

    private boolean checkEnchants(ItemStack stack, Player player) {
        boolean cheat = false;
        if (getProtectionSettings().isCheckEnchants() && !player.hasPermission("functionalservercontrol.itemfixer.enchantments.bypass") && stack.hasItemMeta() && stack.getItemMeta().hasEnchants()) {
            final ItemMeta meta = stack.getItemMeta();
            Map<Enchantment, Integer> enchantments;
            try {
                enchantments = meta.getEnchants();
            } catch (Exception e) {
                clearData(stack);
                player.updateInventory();
                return true;
            }
            for (Map.Entry<Enchantment, Integer> mEnchant : enchantments.entrySet()) {
                Enchantment enchant = mEnchant.getKey();
                String perm = "functionalservercontrol.itemfixer." + enchant.getName() + ".bypass";
                if (getProtectionSettings().isRemoveInvalidEnchants() && !enchant.canEnchantItem(stack) && !player.hasPermission(perm) ) {
                    TaskManager.preformAsync(() -> {
                        meta.removeEnchant(enchant);
                        stack.setItemMeta(meta);
                    });
                    cheat = true;
                }
                if ((mEnchant.getValue() > enchant.getMaxLevel() || mEnchant.getValue() < 0) && !player.hasPermission(perm)) {
                    TaskManager.preformAsync(() -> {
                        meta.removeEnchant(enchant);
                        stack.setItemMeta(meta);
                    });
                    cheat = true;
                }
            }
        }
        return cheat;
    }

    private boolean checkNbt(ItemStack stack, Player p) {
        boolean cheat = false;
        try {
            if (p.hasPermission("functionalservercontrol.itemfixer.nbt.bypass")) return false;
            Material mat = stack.getType();
            NbtCompound tag = (NbtCompound) MiniNBTFactory.fromItemTag(stack);
            if (tag == null) return false;
            if(this.isCrashItem(stack, tag, mat)) {
                tag.getKeys().clear();
                stack.setAmount(1);
                return true;
            }
            final String tagS = tag.toString();
            for (String nbt1 : nbt) {
                if (tag.containsKey(nbt1)) {
                    tag.remove(nbt1);
                    cheat = true;
                }
            }
            if (tag.containsKey("BlockEntityTag") && !isShulkerBox(stack, stack) && !needIgnore(stack) && !getProtectionSettings().getItemFixerIgnoredTags().contains("BlockEntityTag") ) {
                tag.remove("BlockEntityTag");
                cheat = true;
            } else if (mat == Material.WRITTEN_BOOK && ((!getProtectionSettings().getItemFixerIgnoredTags().contains("ClickEvent") && tagS.contains("ClickEvent"))
                    || (!getProtectionSettings().getItemFixerIgnoredTags().contains("run_command") && tagS.contains("run_command")))) {
                tag.getKeys().clear();
                cheat = true;
            } else if (mat == Material.LEGACY_MONSTER_EGG && !getProtectionSettings().getItemFixerIgnoredTags().contains("EntityTag") && tag.containsKey("EntityTag") && fixEgg(tag)) {
                cheat = true;
            } else if (mat == Material.ARMOR_STAND && !getProtectionSettings().getItemFixerIgnoredTags().contains("EntityTag") && tag.containsKey("EntityTag")) {
                tag.remove("EntityTag");
                cheat = true;
            } else if ((mat == Material.LEGACY_SKULL || mat == Material.LEGACY_SKULL_ITEM && stack.getDurability() == 3)) {
                if (isCrashSkull(tag)) {
                    cheat = true;
                }
            } else if (mat == Material.LEGACY_FIREWORK && !getProtectionSettings().getItemFixerIgnoredTags().contains("Explosions") && checkFireWork(stack)) {
                cheat = true;
            } else if (mat == Material.LEGACY_BANNER && checkBanner(stack)) {
                cheat = true;
            } else if (isPotion(stack) && !getProtectionSettings().getItemFixerIgnoredTags().contains("CustomPotionEffects") && tag.containsKey("CustomPotionEffects")
                    && (checkPotion(stack, p) || checkCustomColor(tag.getCompound("CustomPotionEffects")))) {
                cheat = true;
            }
        } catch (Exception ignored) {}
        return cheat;
    }

    private boolean needIgnore(ItemStack stack) {
        Material material = stack.getType();
        return (material == Material.LEGACY_BANNER || material == Material.SHIELD);
    }

    private void checkShulkerBox(ItemStack stack, Player p) {
        if (!isShulkerBox(stack, stack)) return;
        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        for (ItemStack is : box.getInventory().getContents()) {
            if (isShulkerBox(is, stack) || isHackedItem(is, p)) {
                TaskManager.preformAsync(() -> {
                    box.getInventory().clear();
                    meta.setBlockState(box);
                    stack.setItemMeta(meta);
                });
                return;
            }
        }
    }

    private boolean isPotion(ItemStack stack) {
        try {
            return stack.hasItemMeta() && stack.getItemMeta() instanceof PotionMeta;
        } catch (IllegalArgumentException e) {
            clearData(stack);
            return false;
        }
    }

    private boolean checkCustomColor(NbtCompound tag) {
        if (tag.containsKey("CustomPotionColor")) {
            int color = tag.getInteger("CustomPotionColor");
            try {
                Color.fromBGR(color);
            } catch (IllegalArgumentException e) {
                tag.remove("CustomPotionColor");
                return true;
            }
        }
        return false;
    }

    private boolean checkPotion(ItemStack stack, Player p) {
        boolean cheat = false;
        if (!p.hasPermission("functionalservercontrol.itemfixer.potion.bypass")) {
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            for (PotionEffect ef : meta.getCustomEffects()) {
                String perm = "itemfixer.allow.".concat(ef.getType().toString()).concat(".").concat(String.valueOf(ef.getAmplifier()+1));
                if (!p.hasPermission(perm)) {
                    TaskManager.preformAsync(() -> {
                        meta.removeCustomEffect(ef.getType());
                        stack.setItemMeta(meta);
                    });
                    cheat = true;
                }
            }
        }
        return cheat;
    }

    private boolean isShulkerBox(ItemStack stack, ItemStack rootStack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (!stack.hasItemMeta()) return false;
        try {
            if (!(stack.getItemMeta() instanceof BlockStateMeta)) return false;
        } catch (IllegalArgumentException e) {
            clearData(rootStack);
            return false;
        }
        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        return meta.getBlockState() instanceof ShulkerBox;
    }

    public boolean isHackedItem(ItemStack stack, Player player) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        this.checkShulkerBox(stack, player);
        if (this.checkNbt(stack, player)) {

            checkEnchants(stack, player);
            return true;
        }
        return checkEnchants(stack, player);
    }

    private boolean checkBanner(@NotNull ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        boolean cheat = false;
        if (meta instanceof BannerMeta) {
            BannerMeta bmeta = (BannerMeta) meta;
            List<org.bukkit.block.banner.Pattern> patterns = new ArrayList<>();
            for (Pattern pattern : bmeta.getPatterns()) {
                pattern.getPattern();
                patterns.add(pattern);
            }
        }
        return cheat;
    }

    public boolean checkFireWork(ItemStack stack) {
        boolean changed = false;
        FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
        if (meta.getPower() > 3) {
            meta.setPower(3);
            changed = true;
        }
        if (meta.getEffectsSize() > 8) {
            List<FireworkEffect> list = meta.getEffects().stream().limit(8).collect(Collectors.toList());
            meta.clearEffects();
            meta.addEffects(list);
            changed = true;
        }
        if (changed) {
            stack.setItemMeta(meta);
        }
        return changed;
    }

    private boolean isCrashItem(ItemStack stack, NbtCompound tag, Material mat) {
        if (stack.getAmount() <1 || stack.getAmount() > 64 || tag.getKeys().size() > 20) {
            return true;
        }
        int tagL = tag.toString().length();
        if ((mat == Material.NAME_TAG || tiles.contains(mat)) && tagL > 600) {
            return true;
        }
        if (isShulkerBox(stack, stack)) return false;
        return mat == Material.WRITTEN_BOOK ? (tagL >= 22000) : (tagL >= 13000);
    }

    private boolean fixEgg(NbtCompound tag) {
        NbtCompound enttag = tag.getCompound("EntityTag");
        int size = enttag.getKeys().size();
        if (size >= 2 ) {
            Object id = enttag.getObject("id");
            Object color = enttag.getObject("Color");
            enttag.getKeys().clear();
            if (id instanceof String) {
                enttag.put("id", (String) id);
            }
            if (color instanceof Byte) {
                enttag.put("Color", (byte) color);
            }
            tag.put("EntityTag", enttag);
            return color == null || size >= 3;
        }
        return false;
    }

    private void clearData(ItemStack stack) {
        NbtCompound tag = (NbtCompound) MiniNBTFactory.fromItemTag(stack);
        if (tag == null) return;
        tag.getKeys().clear();
    }

    private static final ItemChecker itemChecker = new ItemChecker();
    public static ItemChecker getItemChecker() {
        return itemChecker;
    }
}
