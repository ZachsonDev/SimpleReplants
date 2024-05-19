package de.jeff_media.replant.daddy;

import java.util.concurrent.TimeUnit;

/*
 * Renamed from de.jeff_media.replant.daddy.c
 */
public final class c_0 {
    public static long i(long l, TimeUnit timeUnit) {
        return timeUnit.toSeconds(l) * 20L;
    }

    public static String i(Object object) {
        Object object2 = object;
        object2 = (String)object2;
        int n = ((String)object2).length();
        int n2 = n - 1;
        char[] cArray = new char[n];
        int n3 = 5 << 3;
        int cfr_ignored_0 = (2 ^ 5) << 4 ^ (2 << 2 ^ 3);
        int n4 = n2;
        int n5 = 5 << 3 ^ (3 ^ 5);
        while (n4 >= 0) {
            int n6 = n2--;
            cArray[n6] = (char)(((String)object2).charAt(n6) ^ n5);
            if (n2 < 0) break;
            int n7 = n2--;
            cArray[n7] = (char)(((String)object2).charAt(n7) ^ n3);
            n4 = n2;
        }
        return new String(cArray);
    }
}

