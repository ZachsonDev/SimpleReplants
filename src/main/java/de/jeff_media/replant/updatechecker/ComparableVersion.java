package de.jeff_media.replant.updatechecker;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ComparableVersion
implements Comparable<ComparableVersion> {
    private static final int MAX_INTITEM_LENGTH = 9;
    private static final int MAX_LONGITEM_LENGTH = 18;
    private String value;
    private String canonical;
    private ListItem items;

    public ComparableVersion(String string) {
        this.parseVersion(string);
    }

    public final void parseVersion(String string) {
        this.value = string;
        this.items = new ListItem();
        string = string.toLowerCase(Locale.ENGLISH);
        ListItem listItem = this.items;
        ArrayDeque<ListItem> arrayDeque = new ArrayDeque<ListItem>();
        arrayDeque.push(listItem);
        boolean bl = false;
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '.') {
                if (i == n) {
                    listItem.add(IntItem.ZERO);
                } else {
                    listItem.add(ComparableVersion.parseItem(bl, string.substring(n, i)));
                }
                n = i + 1;
                continue;
            }
            if (c == '-') {
                if (i == n) {
                    listItem.add(IntItem.ZERO);
                } else {
                    listItem.add(ComparableVersion.parseItem(bl, string.substring(n, i)));
                }
                n = i + 1;
                ListItem listItem2 = listItem;
                listItem = new ListItem();
                listItem2.add(listItem);
                arrayDeque.push(listItem);
                continue;
            }
            if (Character.isDigit(c)) {
                if (!bl && i > n) {
                    listItem.add(new StringItem(string.substring(n, i), true));
                    n = i;
                    ListItem listItem3 = listItem;
                    listItem = new ListItem();
                    listItem3.add(listItem);
                    arrayDeque.push(listItem);
                }
                bl = true;
                continue;
            }
            if (bl && i > n) {
                listItem.add(ComparableVersion.parseItem(true, string.substring(n, i)));
                n = i;
                ListItem listItem4 = listItem;
                listItem = new ListItem();
                listItem4.add(listItem);
                arrayDeque.push(listItem);
            }
            bl = false;
        }
        if (string.length() > n) {
            listItem.add(ComparableVersion.parseItem(bl, string.substring(n)));
        }
        while (!arrayDeque.isEmpty()) {
            listItem = (ListItem)arrayDeque.pop();
            listItem.normalize();
        }
    }

    private static Item parseItem(boolean bl, String string) {
        if (!bl) {
            return new StringItem(string, false);
        }
        if ((string = ComparableVersion.stripLeadingZeroes(string)).length() <= 9) {
            return new IntItem(string);
        }
        if (string.length() <= 18) {
            return new LongItem(string);
        }
        return new BigIntegerItem(string);
    }

    private static String stripLeadingZeroes(String string) {
        if (string == null || string.isEmpty()) {
            return "0";
        }
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '0') continue;
            return string.substring(i);
        }
        return string;
    }

    @Override
    public int compareTo(ComparableVersion comparableVersion) {
        return this.items.compareTo(comparableVersion.items);
    }

    public String toString() {
        return this.value;
    }

    public String getCanonical() {
        if (this.canonical == null) {
            this.canonical = this.items.toString();
        }
        return this.canonical;
    }

    public boolean equals(Object object) {
        return object instanceof ComparableVersion && this.items.equals(((ComparableVersion)object).items);
    }

    public int hashCode() {
        return this.items.hashCode();
    }

    public static void main(String ... stringArray) {
        System.out.println("Display parameters as parsed by Maven (in canonical form) and comparison result:");
        if (stringArray.length == 0) {
            return;
        }
        ComparableVersion comparableVersion = null;
        int n = 1;
        for (String string : stringArray) {
            ComparableVersion comparableVersion2 = new ComparableVersion(string);
            if (comparableVersion != null) {
                int n2 = comparableVersion.compareTo(comparableVersion2);
                System.out.println("   " + comparableVersion.toString() + ' ' + (n2 == 0 ? "==" : (n2 < 0 ? "<" : ">")) + ' ' + string);
            }
            System.out.println(String.valueOf(n++) + ". " + string + " == " + comparableVersion2.getCanonical());
            comparableVersion = comparableVersion2;
        }
    }

    private static class ListItem
    extends ArrayList<Item>
    implements Item {
        private ListItem() {
        }

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public boolean isNull() {
            return this.size() == 0;
        }

        void normalize() {
            for (int i = this.size() - 1; i >= 0; --i) {
                Item item = (Item)this.get(i);
                if (item.isNull()) {
                    this.remove(i);
                    continue;
                }
                if (!(item instanceof ListItem)) break;
            }
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                if (this.size() == 0) {
                    return 0;
                }
                Item item2 = (Item)this.get(0);
                return item2.compareTo(null);
            }
            switch (item.getType()) {
                case 0: 
                case 3: 
                case 4: {
                    return -1;
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    Iterator iterator = this.iterator();
                    Iterator iterator2 = ((ListItem)item).iterator();
                    while (iterator.hasNext() || iterator2.hasNext()) {
                        Item item3;
                        Item item4 = iterator.hasNext() ? (Item)iterator.next() : null;
                        Item item5 = item3 = iterator2.hasNext() ? (Item)iterator2.next() : null;
                        int n = item4 == null ? (item3 == null ? 0 : -1 * item3.compareTo(item4)) : item4.compareTo(item3);
                        if (n == 0) continue;
                        return n;
                    }
                    return 0;
                }
            }
            throw new IllegalStateException("invalid item: " + item.getClass());
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (Item item : this) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(item instanceof ListItem ? (char)'-' : '.');
                }
                stringBuilder.append(item);
            }
            return stringBuilder.toString();
        }
    }

    private static class IntItem
    implements Item {
        private final int value;
        public static final IntItem ZERO = new IntItem();

        private IntItem() {
            this.value = 0;
        }

        IntItem(String string) {
            this.value = Integer.parseInt(string);
        }

        @Override
        public int getType() {
            return 3;
        }

        @Override
        public boolean isNull() {
            return this.value == 0;
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return this.value != 0 ? 1 : 0;
            }
            switch (item.getType()) {
                case 3: {
                    int n = ((IntItem)item).value;
                    return this.value < n ? -1 : (this.value == n ? 0 : 1);
                }
                case 0: 
                case 4: {
                    return -1;
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    return 1;
                }
            }
            throw new IllegalStateException("invalid item: " + item.getClass());
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            IntItem intItem = (IntItem)object;
            return this.value == intItem.value;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }

    private static interface Item {
        public static final int INT_ITEM = 3;
        public static final int LONG_ITEM = 4;
        public static final int BIGINTEGER_ITEM = 0;
        public static final int STRING_ITEM = 1;
        public static final int LIST_ITEM = 2;

        public int compareTo(Item var1);

        public int getType();

        public boolean isNull();
    }

    private static class StringItem
    implements Item {
        private static final List<String> QUALIFIERS = Arrays.asList("alpha", "beta", "milestone", "rc", "snapshot", "", "sp");
        private static final Properties ALIASES = new Properties();
        private static final String RELEASE_VERSION_INDEX;
        private final String value;

        StringItem(String string, boolean bl) {
            if (bl && string.length() == 1) {
                switch (string.charAt(0)) {
                    case 'a': {
                        string = "alpha";
                        break;
                    }
                    case 'b': {
                        string = "beta";
                        break;
                    }
                    case 'm': {
                        string = "milestone";
                    }
                }
            }
            this.value = ALIASES.getProperty(string, string);
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        public boolean isNull() {
            return StringItem.comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX) == 0;
        }

        public static String comparableQualifier(String string) {
            int n = QUALIFIERS.indexOf(string);
            return n == -1 ? QUALIFIERS.size() + "-" + string : String.valueOf(n);
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return StringItem.comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX);
            }
            switch (item.getType()) {
                case 0: 
                case 3: 
                case 4: {
                    return -1;
                }
                case 1: {
                    return StringItem.comparableQualifier(this.value).compareTo(StringItem.comparableQualifier(((StringItem)item).value));
                }
                case 2: {
                    return -1;
                }
            }
            throw new IllegalStateException("invalid item: " + item.getClass());
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            StringItem stringItem = (StringItem)object;
            return this.value.equals(stringItem.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value;
        }

        static {
            ALIASES.put("ga", "");
            ALIASES.put("final", "");
            ALIASES.put("release", "");
            ALIASES.put("cr", "rc");
            RELEASE_VERSION_INDEX = String.valueOf(QUALIFIERS.indexOf(""));
        }
    }

    private static class LongItem
    implements Item {
        private final long value;

        LongItem(String string) {
            this.value = Long.parseLong(string);
        }

        @Override
        public int getType() {
            return 4;
        }

        @Override
        public boolean isNull() {
            return this.value == 0L;
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return this.value != 0L ? 1 : 0;
            }
            switch (item.getType()) {
                case 3: {
                    return 1;
                }
                case 4: {
                    long l = ((LongItem)item).value;
                    return this.value < l ? -1 : (this.value == l ? 0 : 1);
                }
                case 0: {
                    return -1;
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    return 1;
                }
            }
            throw new IllegalStateException("invalid item: " + item.getClass());
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            LongItem longItem = (LongItem)object;
            return this.value == longItem.value;
        }

        public int hashCode() {
            return (int)(this.value ^ this.value >>> 32);
        }

        public String toString() {
            return Long.toString(this.value);
        }
    }

    private static class BigIntegerItem
    implements Item {
        private final BigInteger value;

        BigIntegerItem(String string) {
            this.value = new BigInteger(string);
        }

        @Override
        public int getType() {
            return 0;
        }

        @Override
        public boolean isNull() {
            return BigInteger.ZERO.equals(this.value);
        }

        @Override
        public int compareTo(Item item) {
            if (item == null) {
                return BigInteger.ZERO.equals(this.value) ? 0 : 1;
            }
            switch (item.getType()) {
                case 3: 
                case 4: {
                    return 1;
                }
                case 0: {
                    return this.value.compareTo(((BigIntegerItem)item).value);
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    return 1;
                }
            }
            throw new IllegalStateException("invalid item: " + item.getClass());
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            BigIntegerItem bigIntegerItem = (BigIntegerItem)object;
            return this.value.equals(bigIntegerItem.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value.toString();
        }
    }
}

