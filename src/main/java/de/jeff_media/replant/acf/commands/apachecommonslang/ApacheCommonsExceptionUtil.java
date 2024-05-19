package de.jeff_media.replant.acf.commands.apachecommonslang;

import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsLangUtil;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class ApacheCommonsExceptionUtil {
    private static final String LINE_SEPARATOR;
    static final String WRAPPED_MARKER = " [wrapped] ";
    private static String[] CAUSE_METHOD_NAMES;
    private static final Method THROWABLE_CAUSE_METHOD;
    private static final Method THROWABLE_INITCAUSE_METHOD;

    public static void addCauseMethodName(String string) {
        ArrayList arrayList;
        if (string != null && !string.isEmpty() && !ApacheCommonsExceptionUtil.isCauseMethodName(string) && (arrayList = ApacheCommonsExceptionUtil.getCauseMethodNameList()).add(string)) {
            CAUSE_METHOD_NAMES = ApacheCommonsExceptionUtil.toArray(arrayList);
        }
    }

    public static void removeCauseMethodName(String string) {
        ArrayList arrayList;
        if (string != null && !string.isEmpty() && (arrayList = ApacheCommonsExceptionUtil.getCauseMethodNameList()).remove(string)) {
            CAUSE_METHOD_NAMES = ApacheCommonsExceptionUtil.toArray(arrayList);
        }
    }

    public static boolean setCause(Throwable throwable, Throwable throwable2) {
        if (throwable == null) {
            throw new IllegalArgumentException("target");
        }
        Object[] objectArray = new Object[]{throwable2};
        boolean bl = false;
        if (THROWABLE_INITCAUSE_METHOD != null) {
            try {
                THROWABLE_INITCAUSE_METHOD.invoke((Object)throwable, objectArray);
                bl = true;
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
        try {
            Method method = throwable.getClass().getMethod("setCause", Throwable.class);
            method.invoke((Object)throwable, objectArray);
            bl = true;
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        return bl;
    }

    private static String[] toArray(List list) {
        return list.toArray(new String[list.size()]);
    }

    private static ArrayList getCauseMethodNameList() {
        return new ArrayList<String>(Arrays.asList(CAUSE_METHOD_NAMES));
    }

    public static boolean isCauseMethodName(String string) {
        return ApacheCommonsLangUtil.indexOf(CAUSE_METHOD_NAMES, string) >= 0;
    }

    public static Throwable getCause(Throwable throwable) {
        return ApacheCommonsExceptionUtil.getCause(throwable, CAUSE_METHOD_NAMES);
    }

    public static Throwable getCause(Throwable throwable, String[] stringArray) {
        if (throwable == null) {
            return null;
        }
        Throwable throwable2 = ApacheCommonsExceptionUtil.getCauseUsingWellKnownTypes(throwable);
        if (throwable2 == null) {
            String string;
            if (stringArray == null) {
                stringArray = CAUSE_METHOD_NAMES;
            }
            for (int i = 0; i < stringArray.length && ((string = stringArray[i]) == null || (throwable2 = ApacheCommonsExceptionUtil.getCauseUsingMethodName(throwable, string)) == null); ++i) {
            }
            if (throwable2 == null) {
                throwable2 = ApacheCommonsExceptionUtil.getCauseUsingFieldName(throwable, "detail");
            }
        }
        return throwable2;
    }

    public static Throwable getRootCause(Throwable throwable) {
        List list = ApacheCommonsExceptionUtil.getThrowableList(throwable);
        return list.size() < 2 ? null : (Throwable)list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
        if (throwable instanceof Nestable) {
            return throwable.getCause();
        }
        if (throwable instanceof SQLException) {
            return ((SQLException)throwable).getNextException();
        }
        if (throwable instanceof InvocationTargetException) {
            return ((InvocationTargetException)throwable).getTargetException();
        }
        return null;
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String string) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(string, null);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke((Object)throwable, new Object[0]);
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
        return null;
    }

    private static Throwable getCauseUsingFieldName(Throwable throwable, String string) {
        Field field = null;
        try {
            field = throwable.getClass().getField(string);
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (field != null && Throwable.class.isAssignableFrom(field.getType())) {
            try {
                return (Throwable)field.get(throwable);
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return null;
    }

    public static boolean isThrowableNested() {
        return THROWABLE_CAUSE_METHOD != null;
    }

    public static boolean isNestedThrowable(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof Nestable) {
            return true;
        }
        if (throwable instanceof SQLException) {
            return true;
        }
        if (throwable instanceof InvocationTargetException) {
            return true;
        }
        if (ApacheCommonsExceptionUtil.isThrowableNested()) {
            return true;
        }
        Class<?> clazz = throwable.getClass();
        int n = CAUSE_METHOD_NAMES.length;
        for (int i = 0; i < n; ++i) {
            try {
                Method method = clazz.getMethod(CAUSE_METHOD_NAMES[i], null);
                if (method == null || !Throwable.class.isAssignableFrom(method.getReturnType())) continue;
                return true;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                continue;
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        try {
            Field field = clazz.getField("detail");
            if (field != null) {
                return true;
            }
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return false;
    }

    public static int getThrowableCount(Throwable throwable) {
        return ApacheCommonsExceptionUtil.getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List list = ApacheCommonsExceptionUtil.getThrowableList(throwable);
        return list.toArray(new Throwable[list.size()]);
    }

    public static List getThrowableList(Throwable throwable) {
        ArrayList<Throwable> arrayList = new ArrayList<Throwable>();
        while (throwable != null && !arrayList.contains(throwable)) {
            arrayList.add(throwable);
            throwable = ApacheCommonsExceptionUtil.getCause(throwable);
        }
        return arrayList;
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz) {
        return ApacheCommonsExceptionUtil.indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz, int n) {
        return ApacheCommonsExceptionUtil.indexOf(throwable, clazz, n, false);
    }

    public static int indexOfType(Throwable throwable, Class clazz) {
        return ApacheCommonsExceptionUtil.indexOf(throwable, clazz, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class clazz, int n) {
        return ApacheCommonsExceptionUtil.indexOf(throwable, clazz, n, true);
    }

    private static int indexOf(Throwable throwable, Class clazz, int n, boolean bl) {
        Throwable[] throwableArray;
        if (throwable == null || clazz == null) {
            return -1;
        }
        if (n < 0) {
            n = 0;
        }
        if (n >= (throwableArray = ApacheCommonsExceptionUtil.getThrowables(throwable)).length) {
            return -1;
        }
        if (bl) {
            for (int i = n; i < throwableArray.length; ++i) {
                if (!clazz.isAssignableFrom(throwableArray[i].getClass())) continue;
                return i;
            }
        } else {
            for (int i = n; i < throwableArray.length; ++i) {
                if (!clazz.equals(throwableArray[i].getClass())) continue;
                return i;
            }
        }
        return -1;
    }

    public static void removeCommonFrames(List list, List list2) {
        if (list == null || list2 == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        int n = list.size() - 1;
        for (int i = list2.size() - 1; n >= 0 && i >= 0; --n, --i) {
            String string;
            String string2 = (String)list.get(n);
            if (!string2.equals(string = (String)list2.get(i))) continue;
            list.remove(n);
        }
    }

    public static String getFullStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter((Writer)stringWriter, true);
        Throwable[] throwableArray = ApacheCommonsExceptionUtil.getThrowables(throwable);
        for (int i = 0; i < throwableArray.length; ++i) {
            throwableArray[i].printStackTrace(printWriter);
            if (ApacheCommonsExceptionUtil.isNestedThrowable(throwableArray[i])) break;
        }
        return stringWriter.getBuffer().toString();
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter((Writer)stringWriter, true);
        throwable.printStackTrace(printWriter);
        return stringWriter.getBuffer().toString();
    }

    static List getStackFrameList(Throwable throwable) {
        String string = ApacheCommonsExceptionUtil.getStackTrace(throwable);
        String string2 = LINE_SEPARATOR;
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2);
        ArrayList<String> arrayList = new ArrayList<String>();
        boolean bl = false;
        while (stringTokenizer.hasMoreTokens()) {
            String string3 = stringTokenizer.nextToken();
            int n = string3.indexOf("at");
            if (n != -1 && string3.substring(0, n).trim().length() == 0) {
                bl = true;
                arrayList.add(string3);
                continue;
            }
            if (!bl) continue;
            break;
        }
        return arrayList;
    }

    static {
        Method method;
        LINE_SEPARATOR = System.getProperty("line.separator");
        CAUSE_METHOD_NAMES = new String[]{"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
        try {
            method = Throwable.class.getMethod("getCause", null);
        }
        catch (Exception exception) {
            method = null;
        }
        THROWABLE_CAUSE_METHOD = method;
        try {
            method = Throwable.class.getMethod("initCause", Throwable.class);
        }
        catch (Exception exception) {
            method = null;
        }
        THROWABLE_INITCAUSE_METHOD = method;
    }

    public static interface Nestable {
        public Throwable getCause();

        public String getMessage();

        public String getMessage(int var1);

        public String[] getMessages();

        public Throwable getThrowable(int var1);

        public int getThrowableCount();

        public Throwable[] getThrowables();

        public int indexOfThrowable(Class var1);

        public int indexOfThrowable(Class var1, int var2);

        public void printStackTrace(PrintWriter var1);

        public void printStackTrace(PrintStream var1);

        public void printPartialStackTrace(PrintWriter var1);
    }
}

