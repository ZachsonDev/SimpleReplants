package de.jeff_media.replant.acf.commands.apachecommonslang;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Locale;

public class ApacheCommonsLangUtil {
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    public static <T> T[] clone(T[] TArray) {
        if (TArray == null) {
            return null;
        }
        return (Object[])TArray.clone();
    }

    public static <T> T[] addAll(T[] TArray, T ... TArray2) {
        if (TArray == null) {
            return ApacheCommonsLangUtil.clone(TArray2);
        }
        if (TArray2 == null) {
            return ApacheCommonsLangUtil.clone(TArray);
        }
        Class<?> clazz = TArray.getClass().getComponentType();
        Object[] objectArray = (Object[])Array.newInstance(clazz, TArray.length + TArray2.length);
        System.arraycopy(TArray, 0, objectArray, 0, TArray.length);
        try {
            System.arraycopy(TArray2, 0, objectArray, TArray.length, TArray2.length);
        }
        catch (ArrayStoreException arrayStoreException) {
            Class<?> clazz2 = TArray2.getClass().getComponentType();
            if (!clazz.isAssignableFrom(clazz2)) {
                throw new IllegalArgumentException("Cannot store " + clazz2.getName() + " in an array of " + clazz.getName(), arrayStoreException);
            }
            throw arrayStoreException;
        }
        return objectArray;
    }

    public static String capitalizeFully(String string) {
        return ApacheCommonsLangUtil.capitalizeFully(string, null);
    }

    public static String capitalizeFully(String string, char ... cArray) {
        int n;
        int n2 = n = cArray == null ? -1 : cArray.length;
        if (string == null || string.isEmpty() || n == 0) {
            return string;
        }
        string = string.toLowerCase(Locale.ENGLISH);
        return ApacheCommonsLangUtil.capitalize(string, cArray);
    }

    public static String capitalize(String string) {
        return ApacheCommonsLangUtil.capitalize(string, null);
    }

    public static String capitalize(String string, char ... cArray) {
        int n;
        int n2 = n = cArray == null ? -1 : cArray.length;
        if (string == null || string.isEmpty() || n == 0) {
            return string;
        }
        char[] cArray2 = string.toCharArray();
        boolean bl = true;
        for (int i = 0; i < cArray2.length; ++i) {
            char c = cArray2[i];
            if (ApacheCommonsLangUtil.isDelimiter(c, cArray)) {
                bl = true;
                continue;
            }
            if (!bl) continue;
            cArray2[i] = Character.toTitleCase(c);
            bl = false;
        }
        return new String(cArray2);
    }

    public static boolean isDelimiter(char c, char[] cArray) {
        if (cArray == null) {
            return Character.isWhitespace(c);
        }
        for (char c2 : cArray) {
            if (c != c2) continue;
            return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T> String join(T ... TArray) {
        return ApacheCommonsLangUtil.join((Object[])TArray, null);
    }

    public static String join(Object[] objectArray, char c) {
        if (objectArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(objectArray, c, 0, objectArray.length);
    }

    public static String join(long[] lArray, char c) {
        if (lArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(lArray, c, 0, lArray.length);
    }

    public static String join(int[] nArray, char c) {
        if (nArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(nArray, c, 0, nArray.length);
    }

    public static String join(short[] sArray, char c) {
        if (sArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(sArray, c, 0, sArray.length);
    }

    public static String join(byte[] byArray, char c) {
        if (byArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(byArray, c, 0, byArray.length);
    }

    public static String join(char[] cArray, char c) {
        if (cArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(cArray, c, 0, cArray.length);
    }

    public static String join(float[] fArray, char c) {
        if (fArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(fArray, c, 0, fArray.length);
    }

    public static String join(double[] dArray, char c) {
        if (dArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(dArray, c, 0, dArray.length);
    }

    public static String join(Object[] objectArray, char c, int n, int n2) {
        if (objectArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            if (objectArray[i] == null) continue;
            stringBuilder.append(objectArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(long[] lArray, char c, int n, int n2) {
        if (lArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(lArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(int[] nArray, char c, int n, int n2) {
        if (nArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(nArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(byte[] byArray, char c, int n, int n2) {
        if (byArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(byArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(short[] sArray, char c, int n, int n2) {
        if (sArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(sArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(char[] cArray, char c, int n, int n2) {
        if (cArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(cArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(double[] dArray, char c, int n, int n2) {
        if (dArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(dArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(float[] fArray, char c, int n, int n2) {
        if (fArray == null) {
            return null;
        }
        int n3 = n2 - n;
        if (n3 <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(c);
            }
            stringBuilder.append(fArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(Object[] objectArray, String string) {
        if (objectArray == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(objectArray, string, 0, objectArray.length);
    }

    public static String join(Object[] objectArray, String string, int n, int n2) {
        int n3;
        if (objectArray == null) {
            return null;
        }
        if (string == null) {
            string = EMPTY;
        }
        if ((n3 = n2 - n) <= 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(n3 * 16);
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                stringBuilder.append(string);
            }
            if (objectArray[i] == null) continue;
            stringBuilder.append(objectArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(Iterator<?> iterator, char c) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object obj = iterator.next();
        if (!iterator.hasNext()) {
            String string = obj != null ? obj.toString() : EMPTY;
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder(256);
        if (obj != null) {
            stringBuilder.append(obj);
        }
        while (iterator.hasNext()) {
            stringBuilder.append(c);
            Object obj2 = iterator.next();
            if (obj2 == null) continue;
            stringBuilder.append(obj2);
        }
        return stringBuilder.toString();
    }

    public static String join(Iterator<?> iterator, String string) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object obj = iterator.next();
        if (!iterator.hasNext()) {
            String string2 = obj != null ? obj.toString() : EMPTY;
            return string2;
        }
        StringBuilder stringBuilder = new StringBuilder(256);
        if (obj != null) {
            stringBuilder.append(obj);
        }
        while (iterator.hasNext()) {
            Object obj2;
            if (string != null) {
                stringBuilder.append(string);
            }
            if ((obj2 = iterator.next()) == null) continue;
            stringBuilder.append(obj2);
        }
        return stringBuilder.toString();
    }

    public static String join(Iterable<?> iterable, char c) {
        if (iterable == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(iterable.iterator(), c);
    }

    public static String join(Iterable<?> iterable, String string) {
        if (iterable == null) {
            return null;
        }
        return ApacheCommonsLangUtil.join(iterable.iterator(), string);
    }

    public static boolean isNumeric(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() == 0) {
            return false;
        }
        int n = charSequence.length();
        for (int i = 0; i < n; ++i) {
            if (Character.isDigit(charSequence.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean startsWith(CharSequence charSequence, CharSequence charSequence2) {
        return ApacheCommonsLangUtil.startsWith(charSequence, charSequence2, false);
    }

    public static boolean startsWithIgnoreCase(CharSequence charSequence, CharSequence charSequence2) {
        return ApacheCommonsLangUtil.startsWith(charSequence, charSequence2, true);
    }

    private static boolean startsWith(CharSequence charSequence, CharSequence charSequence2, boolean bl) {
        if (charSequence == null || charSequence2 == null) {
            return charSequence == null && charSequence2 == null;
        }
        if (charSequence2.length() > charSequence.length()) {
            return false;
        }
        return ApacheCommonsLangUtil.regionMatches(charSequence, bl, 0, charSequence2, 0, charSequence2.length());
    }

    static boolean regionMatches(CharSequence charSequence, boolean bl, int n, CharSequence charSequence2, int n2, int n3) {
        if (charSequence instanceof String && charSequence2 instanceof String) {
            return ((String)charSequence).regionMatches(bl, n, (String)charSequence2, n2, n3);
        }
        int n4 = n;
        int n5 = n2;
        int n6 = n3;
        int n7 = charSequence.length() - n;
        int n8 = charSequence2.length() - n2;
        if (n < 0 || n2 < 0 || n3 < 0) {
            return false;
        }
        if (n7 < n3 || n8 < n3) {
            return false;
        }
        while (n6-- > 0) {
            char c;
            char c2;
            if ((c2 = charSequence.charAt(n4++)) == (c = charSequence2.charAt(n5++))) continue;
            if (!bl) {
                return false;
            }
            if (Character.toUpperCase(c2) == Character.toUpperCase(c) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
            return false;
        }
        return true;
    }

    public static int indexOf(Object[] objectArray, Object object) {
        return ApacheCommonsLangUtil.indexOf(objectArray, object, 0);
    }

    public static int indexOf(Object[] objectArray, Object object, int n) {
        if (objectArray == null) {
            return -1;
        }
        if (n < 0) {
            n = 0;
        }
        if (object == null) {
            for (int i = n; i < objectArray.length; ++i) {
                if (objectArray[i] != null) continue;
                return i;
            }
        } else {
            for (int i = n; i < objectArray.length; ++i) {
                if (!object.equals(objectArray[i])) continue;
                return i;
            }
        }
        return -1;
    }
}

