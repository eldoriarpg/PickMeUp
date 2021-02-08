package de.eldoria.pickmeup.util;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;

// proudly stolen from
// https://github.com/rmct/AutoReferee/blob/master/src/main/java/org/mctourney/autoreferee/util/ColorConverter.java
public final class ColorConverter {
    private static final Map<DyeColor, String> DYE_HEX_MAP;
    private static final ChatColor[] BUNGEE_CHAT_COLORS = {ChatColor.BLACK, ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD,
            ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED,
            ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
    private static final Map<Color, org.bukkit.ChatColor> COLOR_BUKKIT_COLOR;

    static {
        DYE_HEX_MAP = Maps.newHashMap();
        DYE_HEX_MAP.put(DyeColor.BLACK, "#181414");
        DYE_HEX_MAP.put(DyeColor.BLUE, "#253193");
        DYE_HEX_MAP.put(DyeColor.BROWN, "#56331c");
        DYE_HEX_MAP.put(DyeColor.CYAN, "#267191");
        DYE_HEX_MAP.put(DyeColor.GRAY, "#414141");
        DYE_HEX_MAP.put(DyeColor.GREEN, "#364b18");
        DYE_HEX_MAP.put(DyeColor.LIGHT_BLUE, "#6387d2");
        DYE_HEX_MAP.put(DyeColor.LIME, "#39ba2e");
        DYE_HEX_MAP.put(DyeColor.MAGENTA, "#be49c9");
        DYE_HEX_MAP.put(DyeColor.ORANGE, "#ea7e35");
        DYE_HEX_MAP.put(DyeColor.PINK, "#d98199");
        DYE_HEX_MAP.put(DyeColor.PURPLE, "#7e34bf");
        DYE_HEX_MAP.put(DyeColor.RED, "#9e2b27");
        DYE_HEX_MAP.put(DyeColor.LIGHT_GRAY, "#a0a7a7");
        DYE_HEX_MAP.put(DyeColor.WHITE, "#a4a4a4");
        DYE_HEX_MAP.put(DyeColor.YELLOW, "#c2b51c");
    }

    static {
        COLOR_BUKKIT_COLOR = new HashMap<>();
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(0, 0, 0), org.bukkit.ChatColor.BLACK);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(0, 0, 170), org.bukkit.ChatColor.DARK_BLUE);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(0, 170, 0), org.bukkit.ChatColor.DARK_GREEN);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(0, 170, 170), org.bukkit.ChatColor.DARK_AQUA);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(170, 0, 0), org.bukkit.ChatColor.DARK_RED);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(170, 0, 170), org.bukkit.ChatColor.DARK_PURPLE);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(255, 170, 0), org.bukkit.ChatColor.GOLD);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(170, 170, 170), org.bukkit.ChatColor.GRAY);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(85, 85, 85), org.bukkit.ChatColor.DARK_GRAY);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(85, 85, 255), org.bukkit.ChatColor.BLUE);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(85, 255, 85), org.bukkit.ChatColor.GREEN);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(85, 255, 255), org.bukkit.ChatColor.AQUA);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(255, 85, 85), org.bukkit.ChatColor.RED);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(255, 85, 255), org.bukkit.ChatColor.LIGHT_PURPLE);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(255, 255, 85), org.bukkit.ChatColor.YELLOW);
        COLOR_BUKKIT_COLOR.put(Color.fromRGB(255, 255, 255), org.bukkit.ChatColor.WHITE);
    }

    private ColorConverter() {
    }

    public static String dyeToHex(DyeColor clr) {
        if (DYE_HEX_MAP.containsKey(clr)) {
            return DYE_HEX_MAP.get(clr);
        }
        return "#000";
    }

    public static Color hexToColor(String hex) {
        // get rid of typical hex color cruft
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.contains("x")) hex = hex.substring(hex.indexOf('x'));

        // if the length isn't the standard 0xRRGGBB or 0xRGB, just quit
        if (hex.length() != 6 && hex.length() != 3) return null;

        // construct and return color object
        int sz = hex.length() / 3, mult = 1 << ((2 - sz) * 4), x = 0;
        for (int i = 0, z = 0; z < hex.length(); ++i, z += sz) {
            x |= (mult * Integer.parseInt(hex.substring(z, z + sz), 16)) << (i * 8);
        }
        return Color.fromBGR(x & 0xffffff);
    }

    public static Color rgbToColor(String rgb) {
        String[] parts = rgb.split("[^0-9]+");
        if (parts.length < 3) return null;

        int x = 0, i;
        for (i = 0; i < 3; ++i) {
            x |= Integer.parseInt(parts[i]) << (i * 8);
        }
        return Color.fromBGR(x & 0xffffff);
    }

    public static ChatColor getNearestBungeeChatColor(java.awt.Color color) {
        ChatColor nearest = ChatColor.WHITE;
        double dist = distanceSquared(ChatColor.WHITE.getColor(), color);
        for (ChatColor c : BUNGEE_CHAT_COLORS) {
            double currdist = distanceSquared(c.getColor(), color);
            if (currdist < dist) {
                nearest = c;
                dist = currdist;
            }
        }
        return nearest;
    }

    public static org.bukkit.ChatColor getNearestBukkitChatColor(java.awt.Color color) {
        Color nearest = Color.fromRGB(0, 0, 0);
        double dist = distanceSquared(toAwtColor(nearest), color);
        for (Color c : COLOR_BUKKIT_COLOR.keySet()) {
            double currdist = distanceSquared(toAwtColor(c), color);
            if (currdist < dist) {
                nearest = c;
                dist = currdist;
            }
        }
        return COLOR_BUKKIT_COLOR.get(nearest);
    }

    public static Color toBukkitColor(java.awt.Color color) {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static java.awt.Color toAwtColor(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    private static double distanceSquared(java.awt.Color c1, java.awt.Color c2) {
        return NumberConversions.square(c2.getRed() - c1.getRed())
                + NumberConversions.square(c2.getGreen() - c1.getGreen())
                + NumberConversions.square(c2.getBlue() - c1.getBlue());
    }
}