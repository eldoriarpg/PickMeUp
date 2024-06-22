package de.eldoria.pickmeup.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.eldoria.pickmeup.PickMeUp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

public class HeadUtil {
    public static String getEntityTexture(EntityType entityType) {
        switch (entityType) {
            case ELDER_GUARDIAN:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0MGEyNjhmMjVmZDVjYzI3NmNhMTQ3YTg0NDZiMjYzMGE1NTg2N2EyMzQ5ZjdjYTEwN2MyNmViNTg5OTEifX19";
            case WITHER_SKELETON:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVlYzk2NDY0NWE4ZWZhYzc2YmUyZjE2MGQ3Yzk5NTYzNjJmMzJiNjUxNzM5MGM1OWMzMDg1MDM0ZjA1MGNmZiJ9fX0=";
            case STRAY:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM1MDk3OTE2YmMwNTY1ZDMwNjAxYzBlZWJmZWIyODcyNzdhMzRlODY3YjRlYTQzYzYzODE5ZDUzZTg5ZWRlNyJ9fX0=";
            case HUSK:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY3NGM2M2M4ZGI1ZjRjYTYyOGQ2OWEzYjFmOGEzNmUyOWQ4ZmQ3NzVlMWE2YmRiNmNhYmI0YmU0ZGIxMjEifX19";
            case ZOMBIE_VILLAGER:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlMDhhODc3NmMxNzY0YzNmZTZhNmRkZDQxMmRmY2I4N2Y0MTMzMWRhZDQ3OWFjOTZjMjFkZjRiZjNhYzg5YyJ9fX0=";
            case SKELETON_HORSE:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdlZmZjZTM1MTMyYzg2ZmY3MmJjYWU3N2RmYmIxZDIyNTg3ZTk0ZGYzY2JjMjU3MGVkMTdjZjg5NzNhIn19fQ==";
            case ZOMBIE_HORSE:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIyOTUwZjJkM2VmZGRiMThkZTg2ZjhmNTVhYzUxOGRjZTczZjEyYTZlMGY4NjM2ZDU1MWQ4ZWI0ODBjZWVjIn19fQ==";
            case DONKEY:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk5YmI1MGQxYTIxNGMzOTQ5MTdlMjViYjNmMmUyMDY5OGJmOThjYTcwM2U0Y2MwOGI0MjQ2MmRmMzA5ZDZlNiJ9fX0=";
            case MULE:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZkY2RhMjY1ZTU3ZTRmNTFiMTQ1YWFjYmY1YjU5YmRjNjA5OWZmZDNjY2UwYTY2MWIyYzAwNjVkODA5MzBkOCJ9fX0=";
            case EVOKER:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk1NDEzNWRjODIyMTM5NzhkYjQ3ODc3OGFlMTIxMzU5MWI5M2QyMjhkMzZkZDU0ZjFlYTFkYTQ4ZTdjYmE2In19fQ==";
            case VEX:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJlYzVhNTE2NjE3ZmYxNTczY2QyZjlkNWYzOTY5ZjU2ZDU1NzVjNGZmNGVmZWZhYmQyYTE4ZGM3YWI5OGNkIn19fQ==";
            case VINDICATOR:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRlYWVjMzQ0YWIwOTViNDhjZWFkNzUyN2Y3ZGVlNjFiMDYzZmY3OTFmNzZhOGZhNzY2NDJjODY3NmUyMTczIn19fQ==";
            case ILLUSIONER:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEyNTEyZTdkMDE2YTIzNDNhN2JmZjFhNGNkMTUzNTdhYjg1MTU3OWYxMzg5YmQ0ZTNhMjRjYmViODhiIn19fQ==";
            case MINECART_COMMAND:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE5MDUzZDIxNjNkMGY1NjExNDVkMzNhNTEzMTQ1ZDRhYzFmOGE0NThiYWE3OTZiZTM4M2U3NTI1YTA1ZjQ1In19fQ==";
            case BOAT:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkZjU4MjM4YWRmZDE3NzAxMGZmYTI0MzhjNzA3ZWNjNDBiNDZiZDJlZjkzNDlmYmE2NWM3NzE3NTlhNSJ9fX0=";
            case MINECART:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQyMDcwYWNjODE0YmM5NDZlNTk4NzllYzdkYTQ1ZGU5ODRkM2VlOWExNTkzOTNkZWZiNTk4NTNhYmUzYjYifX19";
            case MINECART_CHEST:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlZDM0MjExZmVkNDAxMGE4Yzg1NzI0YTI3ZmE1ZmIyMDVkNjc2ODRiM2RhNTE3YjY4MjEyNzljNmI2NWQzZiJ9fX0=";
            case MINECART_FURNACE:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTA3OWFiYmFmYjk4MWM3OTVhOWEyZjgyYmFiM2ZiZDlmMTY2YjhjMGRiZjlhMTc1MWQ3NjliZWFjNjY3YjYifX19";
            case MINECART_TNT:
                return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRkN2ZjOGUzYTk1OWFkZTdkOWNmNjYzZjFlODJkYjc5NzU1NDNlMjg4YWI4ZDExZWIyNTQxODg4MjEzNTI2In19fQ==";
            case MINECART_HOPPER:
                return "";
            case MINECART_MOB_SPAWNER:
                return "";
            case CREEPER:
                return "";
            case SKELETON:
                return "";
            case SPIDER:
                return "";
            case GIANT:
                return "";
            case ZOMBIE:
                return "";
            case SLIME:
                return "";
            case GHAST:
                return "";
            case ZOMBIFIED_PIGLIN:
                return "";
            case ENDERMAN:
                return "";
            case CAVE_SPIDER:
                return "";
            case SILVERFISH:
                return "";
            case BLAZE:
                return "";
            case MAGMA_CUBE:
                return "";
            case ENDER_DRAGON:
                return "";
            case WITHER:
                return "";
            case BAT:
                return "";
            case WITCH:
                return "";
            case ENDERMITE:
                return "";
            case GUARDIAN:
                return "";
            case SHULKER:
                return "";
            case PIG:
                return "";
            case SHEEP:
                return "";
            case COW:
                return "";
            case CHICKEN:
                return "";
            case SQUID:
                return "";
            case WOLF:
                return "";
            case MUSHROOM_COW:
                return "";
            case SNOWMAN:
                return "";
            case OCELOT:
                return "";
            case IRON_GOLEM:
                return "";
            case HORSE:
                return "";
            case RABBIT:
                return "";
            case POLAR_BEAR:
                return "";
            case LLAMA:
                return "";
            case PARROT:
                return "";
            case VILLAGER:
                return "";
            case TURTLE:
                return "";
            case PHANTOM:
                return "";
            case COD:
                return "";
            case SALMON:
                return "";
            case PUFFERFISH:
                return "";
            case TROPICAL_FISH:
                return "";
            case DROWNED:
                return "";
            case DOLPHIN:
                return "";
            case CAT:
                return "";
            case PANDA:
                return "";
            case PILLAGER:
                return "";
            case RAVAGER:
                return "";
            case TRADER_LLAMA:
                return "";
            case WANDERING_TRADER:
                return "";
            case FOX:
                return "";
            case BEE:
                return "";
            case HOGLIN:
                return "";
            case PIGLIN:
                return "";
            case STRIDER:
                return "";
            case ZOGLIN:
                return "";
            //case PIGLIN_BRUTE:
              //  return "";
            case FISHING_HOOK:
                return "";
            case PLAYER:
                return "";
        }
        return null;
    }

    public boolean hasHead(EntityType type) {
        switch (type) {
            case ELDER_GUARDIAN:
            case WITHER_SKELETON:
            case STRAY:
            case HUSK:
            case ZOMBIE_VILLAGER:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
            case DONKEY:
            case MULE:
            case EVOKER:
            case VEX:
            case VINDICATOR:
            case ILLUSIONER:
            case MINECART_COMMAND:
            case BOAT:
            case MINECART:
            case MINECART_CHEST:
            case MINECART_FURNACE:
            case MINECART_TNT:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case GIANT:
            case ZOMBIE:
            case SLIME:
            case GHAST:
            case ZOMBIFIED_PIGLIN:
            case ENDERMAN:
            case CAVE_SPIDER:
            case SILVERFISH:
            case BLAZE:
            case MAGMA_CUBE:
            case ENDER_DRAGON:
            case WITHER:
            case BAT:
            case WITCH:
            case ENDERMITE:
            case GUARDIAN:
            case SHULKER:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case SQUID:
            case WOLF:
            case MUSHROOM_COW:
            case SNOWMAN:
            case OCELOT:
            case IRON_GOLEM:
            case HORSE:
            case RABBIT:
            case POLAR_BEAR:
            case LLAMA:
            case PARROT:
            case VILLAGER:
            case TURTLE:
            case PHANTOM:
            case COD:
            case SALMON:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case DROWNED:
            case DOLPHIN:
            case CAT:
            case PANDA:
            case PILLAGER:
            case RAVAGER:
            case TRADER_LLAMA:
            case WANDERING_TRADER:
            case FOX:
            case BEE:
            case HOGLIN:
            case PIGLIN:
            case STRIDER:
            case ZOGLIN:
            //case PIGLIN_BRUTE:
            case FISHING_HOOK:
            case PLAYER:
                return true;
        }
        return false;
    }

    public @Nullable ItemStack getSkull(EntityType type) {
        if (hasHead(type)) {
            return getSkull(getEntityTexture(type));
        }
        return null;
    }

    public ItemStack getSkull(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        UUID uuid = UUID.randomUUID();
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

        GameProfile profile = new GameProfile(uuid, null);

        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException e) {
            PickMeUp.logger().log(Level.WARNING, "Could not create head", e);
            return null;
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
