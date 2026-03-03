package com.newtermux.features;

import android.content.Context;
import android.content.SharedPreferences;

import com.termux.shared.termux.TermuxConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages terminal color themes for NewTermux.
 * Themes are written to ~/.termux/colors.properties and reloaded by the terminal emulator.
 */
public class NewTermuxColorTheme {

    private static final String PREFS_NAME = "newtermux_color_theme";
    private static final String KEY_THEME = "selected_theme";
    public static final String DEFAULT_THEME = "default_dark";

    public static final String[] THEME_KEYS = {
        "default_dark", "rgui", "amber", "low_contrast", "solarized_dark", "dracula"
    };

    public static final String[] THEME_NAMES = {
        "Default Dark", "RGUI", "Amber (Migraine-Friendly)", "Low Contrast", "Solarized Dark", "Dracula"
    };

    // Default Termux dark — black bg, white text
    private static final String THEME_DEFAULT_DARK =
        "background=#000000\n" +
        "foreground=#ffffff\n" +
        "cursor=#ffffff\n" +
        "color0=#000000\n" +
        "color1=#cc0000\n" +
        "color2=#4e9a06\n" +
        "color3=#c4a000\n" +
        "color4=#3465a4\n" +
        "color5=#75507b\n" +
        "color6=#06989a\n" +
        "color7=#d3d7cf\n" +
        "color8=#555753\n" +
        "color9=#ef2929\n" +
        "color10=#8ae234\n" +
        "color11=#fce94f\n" +
        "color12=#729fcf\n" +
        "color13=#ad7fa8\n" +
        "color14=#34e2e2\n" +
        "color15=#eeeeec\n";

    // RetroArch RGUI-inspired — dark purple-grey bg, muted palette with violet accents
    private static final String THEME_RGUI =
        "background=#1a1422\n" +
        "foreground=#c8c8c8\n" +
        "cursor=#c0a0ff\n" +
        "color0=#1a1422\n" +
        "color1=#d44942\n" +
        "color2=#5a9e4e\n" +
        "color3=#c99a2e\n" +
        "color4=#4e82b4\n" +
        "color5=#9264b4\n" +
        "color6=#4e9e9e\n" +
        "color7=#c8c8c8\n" +
        "color8=#3a3050\n" +
        "color9=#e06060\n" +
        "color10=#70c060\n" +
        "color11=#e0c060\n" +
        "color12=#70a0d0\n" +
        "color13=#c080e0\n" +
        "color14=#70c0c0\n" +
        "color15=#e8e8e8\n";

    // Amber — warm orange tones, zero blue light, for migraine sufferers
    private static final String THEME_AMBER =
        "background=#1a0f00\n" +
        "foreground=#ffb347\n" +
        "cursor=#ff8c00\n" +
        "color0=#1a0f00\n" +
        "color1=#cc4400\n" +
        "color2=#887722\n" +
        "color3=#cc8800\n" +
        "color4=#885500\n" +
        "color5=#aa6622\n" +
        "color6=#bb7722\n" +
        "color7=#ffb347\n" +
        "color8=#3a2000\n" +
        "color9=#ff6600\n" +
        "color10=#bbaa00\n" +
        "color11=#ffcc00\n" +
        "color12=#cc8800\n" +
        "color13=#dd9944\n" +
        "color14=#ddbb00\n" +
        "color15=#ffcc88\n";

    // Low Contrast — desaturated slate palette, easy on eyes for long sessions
    private static final String THEME_LOW_CONTRAST =
        "background=#1e2030\n" +
        "foreground=#808898\n" +
        "cursor=#4a5568\n" +
        "color0=#1e2030\n" +
        "color1=#805060\n" +
        "color2=#507060\n" +
        "color3=#706040\n" +
        "color4=#4060a0\n" +
        "color5=#705080\n" +
        "color6=#407080\n" +
        "color7=#808898\n" +
        "color8=#2a3040\n" +
        "color9=#906070\n" +
        "color10=#608070\n" +
        "color11=#807050\n" +
        "color12=#5070b0\n" +
        "color13=#806090\n" +
        "color14=#508090\n" +
        "color15=#a0a8b8\n";

    // Solarized Dark — research-backed palette designed for readability
    private static final String THEME_SOLARIZED_DARK =
        "background=#002b36\n" +
        "foreground=#839496\n" +
        "cursor=#2aa198\n" +
        "color0=#073642\n" +
        "color1=#dc322f\n" +
        "color2=#859900\n" +
        "color3=#b58900\n" +
        "color4=#268bd2\n" +
        "color5=#d33682\n" +
        "color6=#2aa198\n" +
        "color7=#eee8d5\n" +
        "color8=#002b36\n" +
        "color9=#cb4b16\n" +
        "color10=#586e75\n" +
        "color11=#657b83\n" +
        "color12=#839496\n" +
        "color13=#6c71c4\n" +
        "color14=#93a1a1\n" +
        "color15=#fdf6e3\n";

    // Dracula — popular dark purple theme
    private static final String THEME_DRACULA =
        "background=#282a36\n" +
        "foreground=#f8f8f2\n" +
        "cursor=#f8f8f2\n" +
        "color0=#21222c\n" +
        "color1=#ff5555\n" +
        "color2=#50fa7b\n" +
        "color3=#f1fa8c\n" +
        "color4=#bd93f9\n" +
        "color5=#ff79c6\n" +
        "color6=#8be9fd\n" +
        "color7=#f8f8f2\n" +
        "color8=#6272a4\n" +
        "color9=#ff6e6e\n" +
        "color10=#69ff94\n" +
        "color11=#ffffa5\n" +
        "color12=#d6acff\n" +
        "color13=#ff92df\n" +
        "color14=#a4ffff\n" +
        "color15=#ffffff\n";

    public static String getCurrentTheme(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, DEFAULT_THEME);
    }

    public static void applyTheme(Context context, String themeKey) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, themeKey).apply();
        writeColorsFile(themeKey);
    }

    private static void writeColorsFile(String themeKey) {
        String content = getThemeContent(themeKey);
        File dir = TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE.getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
        try (FileWriter fw = new FileWriter(TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE)) {
            fw.write(content);
        } catch (IOException ignored) {}
    }

    private static String getThemeContent(String key) {
        switch (key) {
            case "rgui":          return THEME_RGUI;
            case "amber":         return THEME_AMBER;
            case "low_contrast":  return THEME_LOW_CONTRAST;
            case "solarized_dark":return THEME_SOLARIZED_DARK;
            case "dracula":       return THEME_DRACULA;
            default:              return THEME_DEFAULT_DARK;
        }
    }
}
