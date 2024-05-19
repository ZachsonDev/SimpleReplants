package de.jeff_media.replant.jefflib.data;

import de.jeff_media.replant.jefflib.internal.cherokee.Validate;
import java.util.Objects;
import org.bukkit.ChatColor;

public final class HexColor {
    private static final String REGEX_COLOR_COMPONENT = "[0-9a-zA-Z][0-9a-zA-Z]";
    private int red;
    private int green;
    private int blue;

    public HexColor(int n, int n2, int n3) {
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n));
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n2));
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n3));
        this.setRed(n);
        this.setGreen(n2);
        this.setBlue(n3);
    }

    public HexColor(String string) {
        this(string.substring(0, 2), string.substring(2, 4), string.substring(4, 6));
    }

    public HexColor(String string, String string2, String string3) {
        Validate.matchesPattern(string, REGEX_COLOR_COMPONENT);
        Validate.matchesPattern(string2, REGEX_COLOR_COMPONENT);
        Validate.matchesPattern(string3, REGEX_COLOR_COMPONENT);
        this.setRed(Integer.parseInt(string, 16));
        this.setGreen(Integer.parseInt(string2, 16));
        this.setBlue(Integer.parseInt(string3, 16));
    }

    public static String applyGradient(String string, HexColor hexColor, HexColor hexColor2) {
        char[] cArray = string.toCharArray();
        int n = string.length();
        StringBuilder stringBuilder = new StringBuilder();
        String string2 = "";
        for (int i = 0; i < n; ++i) {
            if (string2.length() % 2 == 1) {
                if (cArray[i] == 'r' || cArray[i] == 'R') {
                    stringBuilder.append(ChatColor.translateAlternateColorCodes((char)'&', (String)"&r"));
                    string2 = "";
                    continue;
                }
                string2 = string2 + cArray[i];
                continue;
            }
            if (cArray[i] == '&' || cArray[i] == '\u00a7') {
                string2 = string2 + "&";
                continue;
            }
            stringBuilder.append(HexColor.getHexAtPositionInGradient(hexColor, hexColor2, n, i).toColorCode()).append(ChatColor.translateAlternateColorCodes((char)'&', (String)string2)).append(cArray[i]);
        }
        return stringBuilder.toString();
    }

    public static HexColor getHexAtPositionInGradient(HexColor hexColor, HexColor hexColor2, int n, int n2) {
        if (n2 == 0) {
            return hexColor;
        }
        if (n2 == n - 1) {
            return hexColor2;
        }
        int n3 = n - 1;
        int n4 = HexColor.getSingleValueAtPositionInGradient(hexColor.getRed(), hexColor2.getRed(), n3, n2);
        int n5 = HexColor.getSingleValueAtPositionInGradient(hexColor.getGreen(), hexColor2.getGreen(), n3, n2);
        int n6 = HexColor.getSingleValueAtPositionInGradient(hexColor.getBlue(), hexColor2.getBlue(), n3, n2);
        return new HexColor(n4, n5, n6);
    }

    private static int getSingleValueAtPositionInGradient(int n, int n2, int n3, int n4) {
        if (n4 == 0) {
            return n;
        }
        if (n4 == n3) {
            return n2;
        }
        if (n == n2) {
            return n;
        }
        int n5 = n - n2;
        return n - n5 / n3 * n4;
    }

    public String toColorCode() {
        char[] cArray;
        StringBuilder stringBuilder = new StringBuilder("&x");
        char[] cArray2 = cArray = this.toHex().toCharArray();
        int n = cArray2.length;
        for (int i = 0; i < n; ++i) {
            Character c = Character.valueOf(cArray2[i]);
            stringBuilder.append('&').append(c);
        }
        return stringBuilder.toString();
    }

    public String toHex() {
        return String.format("%02x", this.getRed()) + String.format("%02x", this.getGreen()) + String.format("%02x", this.getBlue());
    }

    public int getRed() {
        return this.red;
    }

    public void setRed(int n) {
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n));
        this.red = n;
    }

    public int getGreen() {
        return this.green;
    }

    public void setGreen(int n) {
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n));
        this.green = n;
    }

    public int getBlue() {
        return this.blue;
    }

    public void setBlue(int n) {
        Validate.inclusiveBetween(0, 255, Integer.valueOf(n));
        this.blue = n;
    }

    public int hashCode() {
        return Objects.hash(this.red, this.green, this.blue);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        HexColor hexColor = (HexColor)object;
        return this.red == hexColor.red && this.green == hexColor.green && this.blue == hexColor.blue;
    }

    public String toString() {
        return "HexColor{r=" + this.red + ", g=" + this.green + ", b=" + this.blue + '}';
    }
}

