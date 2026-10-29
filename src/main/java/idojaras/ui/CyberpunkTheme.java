package idojaras.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Cyberpunk colors + fonts for the whole app.
 * <p>
 * {@link #apply()} sets the Swing defaults (UIManager) once, before any window
 * is created, so every component - also the error popup and the
 * JOptionPane popups - gets the dark background and the neon colors.
 */
public final class CyberpunkTheme {

    public static final Color BACKGROUND = new Color(0x0D0221);   // almost black purple
    public static final Color PANEL = new Color(0x1A0B33);        // a bit lighter
    public static final Color CYAN = new Color(0x05D9E8);
    public static final Color MAGENTA = new Color(0xFF2A6D);
    public static final Color YELLOW = new Color(0xF9F002);
    public static final Color PURPLE = new Color(0xB967FF);
    public static final Color DIM = new Color(0x6B6B8A);
    public static final Color TEXT = new Color(0xD1F7FF);

    public static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);

    private CyberpunkTheme() {
        // only static members
    }

    /** Call this in main(), before the first window is created. */
    public static void apply() {
        try {
            // Metal is the same on every system, so our colors really show up
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            // the default look and feel stays, only the colors are missing
        }

        put("Panel.background", BACKGROUND);
        put("OptionPane.background", BACKGROUND);
        put("OptionPane.messageForeground", TEXT);
        put("Label.foreground", TEXT);

        put("Button.background", PANEL);
        put("Button.foreground", CYAN);
        put("Button.select", MAGENTA);
        put("Button.focus", PANEL);
        // Metal paints buttons with a light gradient -> make it one flat dark color
        UIManager.put("Button.gradient", Arrays.asList(0.3f, 0f, PANEL, PANEL, PANEL));
        UIManager.put("Button.border", neonBorder(CYAN));

        put("TextField.background", PANEL);
        put("TextField.foreground", YELLOW);
        put("TextField.caretForeground", MAGENTA);
        put("TextField.selectionBackground", MAGENTA);
        UIManager.put("TextField.border", neonBorder(PURPLE));

        put("ComboBox.background", PANEL);
        put("ComboBox.foreground", YELLOW);
        put("ComboBox.selectionBackground", MAGENTA);
        put("ComboBox.selectionForeground", BACKGROUND);
        put("ComboBox.buttonBackground", PANEL);
        put("ComboBox.buttonDarkShadow", CYAN);

        FontUIResource font = new FontUIResource(FONT);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
    }

    /** Thin neon line + some empty space inside. */
    public static Border neonBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /** Gives one button its own neon color (e.g. a red "danger" button). */
    public static void neonButton(JButton button, Color color) {
        button.setForeground(color);
        button.setBackground(PANEL);
        button.setBorder(neonBorder(color));
        button.setFocusPainted(false);
    }

    // UIManager wants "UIResource" colors, otherwise some components ignore them
    private static void put(String key, Color color) {
        UIManager.put(key, new ColorUIResource(color));
    }
}
