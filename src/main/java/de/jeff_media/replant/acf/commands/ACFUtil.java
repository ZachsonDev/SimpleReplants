package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsLangUtil;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public final class ACFUtil {
    public static final Random RANDOM = new Random();

    private ACFUtil() {
    }

    public static String padRight(String string, int n) {
        return String.format("%1$-" + n + "s", string);
    }

    public static String padLeft(String string, int n) {
        return String.format("%1$" + n + "s", string);
    }

    public static String formatNumber(Integer n) {
        return NumberFormat.getInstance().format(n);
    }

    public static <T extends Enum> T getEnumFromName(T[] TArray, String string) {
        return (T)ACFUtil.getEnumFromName(TArray, (String)string, null);
    }

    public static <T extends Enum> T getEnumFromName(T[] TArray, String string, T t) {
        for (T t2 : TArray) {
            if (!((Enum)t2).name().equalsIgnoreCase(string)) continue;
            return t2;
        }
        return t;
    }

    public static <T extends Enum> T getEnumFromOrdinal(T[] TArray, int n) {
        for (T t : TArray) {
            if (((Enum)t).ordinal() != n) continue;
            return t;
        }
        return null;
    }

    public static String ucfirst(String string) {
        return ApacheCommonsLangUtil.capitalizeFully(string);
    }

    public static Double parseDouble(String string) {
        return ACFUtil.parseDouble(string, null);
    }

    public static Double parseDouble(String string, Double d) {
        if (string == null) {
            return d;
        }
        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException numberFormatException) {
            return d;
        }
    }

    public static Float parseFloat(String string) {
        return ACFUtil.parseFloat(string, null);
    }

    public static Float parseFloat(String string, Float f) {
        if (string == null) {
            return f;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        }
        catch (NumberFormatException numberFormatException) {
            return f;
        }
    }

    public static Long parseLong(String string) {
        return ACFUtil.parseLong(string, null);
    }

    public static Long parseLong(String string, Long l) {
        if (string == null) {
            return l;
        }
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException numberFormatException) {
            return l;
        }
    }

    public static Integer parseInt(String string) {
        return ACFUtil.parseInt(string, null);
    }

    public static Integer parseInt(String string, Integer n) {
        if (string == null) {
            return n;
        }
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return n;
        }
    }

    public static boolean randBool() {
        return RANDOM.nextBoolean();
    }

    public static <T> T nullDefault(Object object, Object object2) {
        return (T)(object != null ? object : object2);
    }

    public static String join(Collection<String> collection) {
        return ApacheCommonsLangUtil.join(collection, " ");
    }

    public static String join(Collection<String> collection, String string) {
        return ApacheCommonsLangUtil.join(collection, string);
    }

    public static String join(String[] stringArray) {
        return ACFUtil.join(stringArray, 0, ' ');
    }

    public static String join(String[] stringArray, String string) {
        return ApacheCommonsLangUtil.join((Object[])stringArray, string);
    }

    public static String join(String[] stringArray, char c) {
        return ACFUtil.join(stringArray, 0, c);
    }

    public static String join(String[] stringArray, int n) {
        return ACFUtil.join(stringArray, n, ' ');
    }

    public static String join(String[] stringArray, int n, char c) {
        return ApacheCommonsLangUtil.join((Object[])stringArray, c, n, stringArray.length);
    }

    public static String simplifyString(String string) {
        if (string == null) {
            return null;
        }
        return ACFPatterns.NON_ALPHA_NUMERIC.matcher(string.toLowerCase(Locale.ENGLISH)).replaceAll("");
    }

    public static double round(double d, int n) {
        try {
            return new BigDecimal(Double.toString(d)).setScale(n, 4).doubleValue();
        }
        catch (NumberFormatException numberFormatException) {
            if (Double.isInfinite(d)) {
                return d;
            }
            return Double.NaN;
        }
    }

    public static int roundUp(int n, int n2) {
        if (n2 == 0) {
            return n;
        }
        int n3 = n % n2;
        if (n3 == 0) {
            return n;
        }
        return n + n2 - n3;
    }

    public static String limit(String string, int n) {
        return string.length() > n ? string.substring(0, n) : string;
    }

    public static String replace(String string, Pattern pattern, String string2) {
        return pattern.matcher(string).replaceAll(Matcher.quoteReplacement(string2));
    }

    public static String replacePattern(String string, Pattern pattern, String string2) {
        return pattern.matcher(string).replaceAll(string2);
    }

    public static String replace(String string, String string2, String string3) {
        return ACFUtil.replace(string, ACFPatterns.getPattern(Pattern.quote(string2)), string3);
    }

    public static String replacePattern(String string, String string2, String string3) {
        return ACFUtil.replace(string, ACFPatterns.getPattern(string2), string3);
    }

    public static String replacePatternMatch(String string, Pattern pattern, String string2) {
        return pattern.matcher(string).replaceAll(string2);
    }

    public static String replacePatternMatch(String string, String string2, String string3) {
        return ACFUtil.replacePatternMatch(string, ACFPatterns.getPattern(string2), string3);
    }

    public static String replaceStrings(String string, String ... stringArray) {
        if (stringArray.length < 2 || stringArray.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Replacements");
        }
        for (int i = 0; i < stringArray.length; i += 2) {
            String string2 = stringArray[i];
            String string3 = stringArray[i + 1];
            if (string3 == null) {
                string3 = "";
            }
            string = ACFUtil.replace(string, string2, string3);
        }
        return string;
    }

    public static String replacePatterns(String string, String ... stringArray) {
        if (stringArray.length < 2 || stringArray.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Replacements");
        }
        for (int i = 0; i < stringArray.length; i += 2) {
            String string2 = stringArray[i];
            String string3 = stringArray[i + 1];
            if (string3 == null) {
                string3 = "";
            }
            string = ACFUtil.replacePattern(string, string2, string3);
        }
        return string;
    }

    public static String capitalize(String string, char[] cArray) {
        return ApacheCommonsLangUtil.capitalize(string, cArray);
    }

    private static boolean isDelimiter(char c, char[] cArray) {
        return ApacheCommonsLangUtil.isDelimiter(c, cArray);
    }

    public static <T> T random(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T random(T[] TArray) {
        if (TArray == null || TArray.length == 0) {
            return null;
        }
        return TArray[RANDOM.nextInt(TArray.length)];
    }

    @Deprecated
    public static <T extends Enum<?>> T random(Class<? extends T> clazz) {
        return (T)ACFUtil.random((Enum[])clazz.getEnumConstants());
    }

    public static String normalize(String string) {
        if (string == null) {
            return null;
        }
        return ACFPatterns.NON_PRINTABLE_CHARACTERS.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).replaceAll("");
    }

    public static int indexOf(String string, String[] stringArray) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (!(string == null ? stringArray[i] == null : string.equals(stringArray[i]))) continue;
            return i;
        }
        return -1;
    }

    public static String capitalizeFirst(String string) {
        return ACFUtil.capitalizeFirst(string, '_');
    }

    public static String capitalizeFirst(String string, char c) {
        string = string.toLowerCase(Locale.ENGLISH);
        String[] stringArray = string.split(Character.toString(c));
        StringBuilder stringBuilder = new StringBuilder(3);
        for (String string2 : stringArray) {
            stringBuilder.append(Character.toUpperCase(string2.charAt(0))).append(string2.substring(1)).append(' ');
        }
        return stringBuilder.toString().trim();
    }

    public static String ltrim(String string) {
        int n;
        for (n = 0; n < string.length() && Character.isWhitespace(string.charAt(n)); ++n) {
        }
        return string.substring(n);
    }

    public static String rtrim(String string) {
        int n;
        for (n = string.length() - 1; n >= 0 && Character.isWhitespace(string.charAt(n)); --n) {
        }
        return string.substring(0, n + 1);
    }

    public static List<String> enumNames(Enum<?>[] enumArray) {
        return Stream.of(enumArray).map(Enum::name).collect(Collectors.toList());
    }

    public static List<String> enumNames(Class<? extends Enum<?>> clazz) {
        return ACFUtil.enumNames(clazz.getEnumConstants());
    }

    public static String combine(String[] stringArray) {
        return ACFUtil.combine(stringArray, 0);
    }

    public static String combine(String[] stringArray, int n) {
        int n2 = 0;
        for (int i = n; i < stringArray.length; ++i) {
            n2 += stringArray[i].length();
        }
        StringBuilder stringBuilder = new StringBuilder(n2);
        for (int i = n; i < stringArray.length; ++i) {
            stringBuilder.append(stringArray[i]);
        }
        return stringBuilder.toString();
    }

    @Nullable
    public static <E extends Enum<E>> E simpleMatch(Class<? extends Enum<?>> clazz, String string) {
        if (string == null) {
            return null;
        }
        string = ACFUtil.simplifyString(string);
        for (Enum<?> enum_ : clazz.getEnumConstants()) {
            String string2 = ACFUtil.simplifyString(enum_.name());
            if (!string.equals(string2)) continue;
            return (E)enum_;
        }
        return null;
    }

    public static boolean isTruthy(String string) {
        switch (string) {
            case "t": 
            case "true": 
            case "on": 
            case "y": 
            case "yes": 
            case "1": {
                return true;
            }
        }
        return false;
    }

    public static Number parseNumber(String string, boolean bl) {
        if (ACFPatterns.getPattern("^0x([0-9A-Fa-f]*)$").matcher(string).matches()) {
            return Long.parseLong(string.substring(2), 16);
        }
        if (ACFPatterns.getPattern("^0b([01]*)$").matcher(string).matches()) {
            return Long.parseLong(string.substring(2), 2);
        }
        ApplyModifierToNumber applyModifierToNumber = new ApplyModifierToNumber(string, bl).invoke();
        string = applyModifierToNumber.getNum();
        double d = applyModifierToNumber.getMod();
        return Double.parseDouble(string) * d;
    }

    public static BigDecimal parseBigNumber(String string, boolean bl) {
        ApplyModifierToNumber applyModifierToNumber = new ApplyModifierToNumber(string, bl).invoke();
        string = applyModifierToNumber.getNum();
        double d = applyModifierToNumber.getMod();
        BigDecimal bigDecimal = new BigDecimal(string);
        return d == 1.0 ? bigDecimal : bigDecimal.multiply(new BigDecimal(d));
    }

    public static <T> boolean hasIntersection(Collection<T> collection, Collection<T> collection2) {
        for (T t : collection) {
            if (!collection2.contains(t)) continue;
            return true;
        }
        return false;
    }

    public static <T> Collection<T> intersection(Collection<T> collection, Collection<T> collection2) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T t : collection) {
            if (!collection2.contains(t)) continue;
            arrayList.add(t);
        }
        return arrayList;
    }

    public static int rand(int n, int n2) {
        return n + RANDOM.nextInt(n2 - n + 1);
    }

    public static int rand(int n, int n2, int n3, int n4) {
        return ACFUtil.randBool() ? ACFUtil.rand(n, n2) : ACFUtil.rand(n3, n4);
    }

    public static double rand(double d, double d2) {
        return RANDOM.nextDouble() * (d2 - d) + d;
    }

    public static boolean isNumber(String string) {
        return ApacheCommonsLangUtil.isNumeric(string);
    }

    public static String intToRoman(int n) {
        if (n == 1) {
            return "I";
        }
        if (n == 2) {
            return "II";
        }
        if (n == 3) {
            return "III";
        }
        if (n == 4) {
            return "IV";
        }
        if (n == 5) {
            return "V";
        }
        if (n == 6) {
            return "VI";
        }
        if (n == 7) {
            return "VII";
        }
        if (n == 8) {
            return "VIII";
        }
        if (n == 9) {
            return "IX";
        }
        if (n == 10) {
            return "X";
        }
        return null;
    }

    public static boolean isInteger(String string) {
        return ACFPatterns.INTEGER.matcher(string).matches();
    }

    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isBetween(float f, double d, double d2) {
        return (double)f >= d && (double)f <= d2;
    }

    public static double precision(double d, int n) {
        double d2 = Math.pow(10.0, n);
        return (double)Math.round(d * d2) / d2;
    }

    public static void sneaky(Throwable throwable) {
        throw (RuntimeException)ACFUtil.superSneaky(throwable);
    }

    private static <T extends Throwable> T superSneaky(Throwable throwable) {
        throw throwable;
    }

    public static <T> List<T> preformOnImmutable(List<T> list, Consumer<List<T>> consumer) {
        try {
            consumer.accept(list);
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            list = new ArrayList<T>(list);
            consumer.accept(list);
        }
        return list;
    }

    public static <T> T getFirstElement(Iterable<T> iterable) {
        if (iterable == null) {
            return null;
        }
        Iterator<T> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    private static class ApplyModifierToNumber {
        private String num;
        private boolean suffixes;
        private double mod;

        public ApplyModifierToNumber(String string, boolean bl) {
            this.num = string;
            this.suffixes = bl;
        }

        public String getNum() {
            return this.num;
        }

        public double getMod() {
            return this.mod;
        }

        public ApplyModifierToNumber invoke() {
            this.mod = 1.0;
            if (this.suffixes) {
                switch (this.num.charAt(this.num.length() - 1)) {
                    case 'M': 
                    case 'm': {
                        this.mod = 1000000.0;
                        this.num = this.num.substring(0, this.num.length() - 1);
                        break;
                    }
                    case 'K': 
                    case 'k': {
                        this.mod = 1000.0;
                        this.num = this.num.substring(0, this.num.length() - 1);
                    }
                }
            }
            return this;
        }
    }
}

