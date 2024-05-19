package de.jeff_media.replant.jefflib.internal.cherokee;

import java.util.Arrays;

public final class StringUtils {
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    private static final int PAD_LIMIT = 8192;

    public static String leftPad(String string, int n) {
        return StringUtils.leftPad(string, n, ' ');
    }

    public static String leftPad(String string, int n, char c) {
        if (string == null) {
            return null;
        }
        int n2 = n - string.length();
        if (n2 <= 0) {
            return string;
        }
        if (n2 > 8192) {
            return StringUtils.leftPad(string, n, String.valueOf(c));
        }
        return StringUtils.repeat(c, n2).concat(string);
    }

    public static String leftPad(String string, int n, String string2) {
        if (string == null) {
            return null;
        }
        String string3 = string2;
        if (StringUtils.isEmpty(string3)) {
            string3 = SPACE;
        }
        int n2 = string3.length();
        int n3 = string.length();
        int n4 = n - n3;
        if (n4 <= 0) {
            return string;
        }
        if (n2 == 1 && n4 <= 8192) {
            return StringUtils.leftPad(string, n, string3.charAt(0));
        }
        if (n4 == n2) {
            return string3.concat(string);
        }
        if (n4 < n2) {
            return string3.substring(0, n4).concat(string);
        }
        char[] cArray = new char[n4];
        char[] cArray2 = string3.toCharArray();
        for (int i = 0; i < n4; ++i) {
            cArray[i] = cArray2[i % n2];
        }
        return new String(cArray).concat(string);
    }

    public static String repeat(String string, String string2, int n) {
        if (string == null || string2 == null) {
            return StringUtils.repeat(string, n);
        }
        String string3 = StringUtils.repeat(string + string2, n);
        return StringUtils.removeEnd(string3, string2);
    }

    public static String repeat(String string, int n) {
        if (string == null) {
            return null;
        }
        if (n <= 0) {
            return EMPTY;
        }
        int n2 = string.length();
        if (n == 1 || n2 == 0) {
            return string;
        }
        if (n2 == 1 && n <= 8192) {
            return StringUtils.repeat(string.charAt(0), n);
        }
        int n3 = n2 * n;
        switch (n2) {
            case 1: {
                return StringUtils.repeat(string.charAt(0), n);
            }
            case 2: {
                char c = string.charAt(0);
                char c2 = string.charAt(1);
                char[] cArray = new char[n3];
                for (int i = n * 2 - 2; i >= 0; --i) {
                    cArray[i] = c;
                    cArray[i + 1] = c2;
                    --i;
                }
                return new String(cArray);
            }
        }
        StringBuilder stringBuilder = new StringBuilder(n3);
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    public static String removeEnd(String string, String string2) {
        if (StringUtils.isEmpty(string) || StringUtils.isEmpty(string2)) {
            return string;
        }
        if (string.endsWith(string2)) {
            return string.substring(0, string.length() - string2.length());
        }
        return string;
    }

    public static String repeat(char c, int n) {
        if (n <= 0) {
            return EMPTY;
        }
        char[] cArray = new char[n];
        Arrays.fill(cArray, c);
        return new String(cArray);
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static String center(String string, int n) {
        return StringUtils.center(string, n, ' ');
    }

    public static String center(String string, int n, char c) {
        if (string == null || n <= 0) {
            return string;
        }
        int n2 = string.length();
        int n3 = n - n2;
        if (n3 <= 0) {
            return string;
        }
        string = StringUtils.leftPad(string, n2 + n3 / 2, c);
        string = StringUtils.rightPad(string, n, c);
        return string;
    }

    public static String center(String string, int n, String string2) {
        int n2;
        int n3;
        String string3 = string2;
        if (string == null || n <= 0) {
            return string;
        }
        if (StringUtils.isEmpty(string3)) {
            string3 = SPACE;
        }
        if ((n3 = n - (n2 = string.length())) <= 0) {
            return string;
        }
        string = StringUtils.leftPad(string, n2 + n3 / 2, string3);
        string = StringUtils.rightPad(string, n, string3);
        return string;
    }

    public static String rightPad(String string, int n) {
        return StringUtils.rightPad(string, n, ' ');
    }

    public static String rightPad(String string, int n, char c) {
        if (string == null) {
            return null;
        }
        int n2 = n - string.length();
        if (n2 <= 0) {
            return string;
        }
        if (n2 > 8192) {
            return StringUtils.rightPad(string, n, String.valueOf(c));
        }
        return string.concat(StringUtils.repeat(c, n2));
    }

    public static String rightPad(String string, int n, String string2) {
        String string3 = string2;
        if (string == null) {
            return null;
        }
        if (StringUtils.isEmpty(string3)) {
            string3 = SPACE;
        }
        int n2 = string3.length();
        int n3 = string.length();
        int n4 = n - n3;
        if (n4 <= 0) {
            return string;
        }
        if (n2 == 1 && n4 <= 8192) {
            return StringUtils.rightPad(string, n, string3.charAt(0));
        }
        if (n4 == n2) {
            return string.concat(string3);
        }
        if (n4 < n2) {
            return string.concat(string3.substring(0, n4));
        }
        char[] cArray = new char[n4];
        char[] cArray2 = string3.toCharArray();
        for (int i = 0; i < n4; ++i) {
            cArray[i] = cArray2[i % n2];
        }
        return string.concat(new String(cArray));
    }
}

