package de.jeff_media.replant.daddy;

import com.google.gson.Gson;
import de.jeff_media.replant.daddy.C;
import de.jeff_media.replant.daddy.H;
import de.jeff_media.replant.daddy.c_0;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/*
 * Duplicate member names - consider using --renamedupmembers true
 */
public final class Daddy_Stepsister
implements Listener {
    public static Plugin l;
    public static NamespacedKey c;
    public static String G;
    private static String M;
    public static String m;
    private static String A;
    private static final String d;
    private static Boolean D;
    private static String J;

    private static byte[] i(String object) {
        String string = object;
        object = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream((OutputStream)object);
        Object object2 = object;
        GZIPOutputStream gZIPOutputStream2 = gZIPOutputStream;
        gZIPOutputStream2.write(string.getBytes());
        gZIPOutputStream2.close();
        object = ((ByteArrayOutputStream)object2).toByteArray();
        ((ByteArrayOutputStream)object2).close();
        return object;
    }

    public static void init(Plugin plugin) {
        Plugin plugin2 = plugin;
        if (l != null) {
            throw new IllegalStateException(c_0.i("OD\\MOLW\bGFG\\GIBATMJ"));
        }
        l = plugin2;
        D = null;
        c = new NamespacedKey(plugin2, c_0.i("@O[]MKFXM\\AHAMIZAAFCM][OOK"));
        if (!Daddy_Stepsister.allows(new Object())) {
            int n;
            String[] stringArray = new String[19];
            stringArray[0] = c_0.i("\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013");
            stringArray[1] = c_0.i("\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\bgDBMIIB\b^D[OGF\u000eKAXW\b\u0014\u0005\u0006");
            stringArray[2] = "";
            stringArray[3] = new StringBuilder().insert(0, c_0.i("qA]\tZK\b[[GFI\bOF\u000eABDKOOD\u000eKAXW\bAN\u000e")).append(plugin2.getDescription().getName()).append(c_0.i("\b\u0014\u0005\u0006")).toString();
            stringArray[4] = "";
            stringArray[5] = c_0.i("cKM^\bGF\u000eEGFJ\bZ@O\\\u000eQA]\u000e_AF\t\\\u000eZKKKAXM\u000eI@Q\u000e[[X^G\\\\\u000eNAZ\u000e\\FA]\bGDBMIIB");
            stringArray[6] = c_0.i("XM\\[GG@\u0004\u000eI@L\u000e\\FIZ\bG\\\u000eEGOF\\\u000eKAFZIGF\u000eEODYI\\M\u000eG\\\bA\\FM\\\bFI\\EH]B\bMGJM\u0000");
            stringArray[7] = "";
            stringArray[8] = c_0.i("~DKI]M\u000eKAF]AJM\\\bL]WA@O\u000eI\u000eDKOG\\GEO\\K\u0004\u000eGHNGKGIB\bMG^Q\u000eIZ\b}XGOA\\ck\u000eA@[ZMOL\u0014");
            stringArray[9] = "";
            stringArray[10] = c_0.i("@Z\\^[\u0014\u0007\u0001_Y_\u0000[^AIGZEM\u0006AZI\u0007\\M]G[ZMM]\u0007O]Z@AZ]\u0007CN@IBMV\u0006\u001f\u001f\u001b\u001a\u001d\u0010\u0001");
            stringArray[11] = c_0.i("\u0000`GZM\u000e\\FIZ\bWG[\bC]]\\\u000eJK\bBGIOKL\u000eA@\bZG\u000e[KM\u000eX\\MCA[E\u000eXB]IA@[\u0007");
            stringArray[12] = "";
            stringArray[13] = c_0.i("aH\bWG[\bMI@FA\\\u000eIHNAZJ\bZ@K\bH]BD\u000eX\\AMM\u0002\bWG[\bMI@\bOD]G\u000eKAFZIM\\\u000eEK");
            stringArray[14] = c_0.i("G@\bjA]KAZJ\bZG\u000eOK\\\u000eI\u000eLG[MG[FZMJ\b^ZGKK\u0004\u000eG\\\bK^KF\u000eI\u000eN\\MK\bMG^Q\u0014");
            stringArray[15] = "";
            stringArray[16] = c_0.i("jA]KAZJ\u0012\u000e@Z\\^[\u0014\u0007\u0001LG[MG\\L\u0000BKNH\u0005CMJAO\u0006MGC");
            stringArray[17] = c_0.i("\u000e\bAZ\u000elc\u0012\u000eEHFODKP\r\u0018\u001e\u0018\u001f");
            stringArray[18] = c_0.i("\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013");
            String[] stringArray2 = stringArray;
            int n2 = stringArray.length;
            int n3 = n = 0;
            while (n3 < n2) {
                String string = stringArray2[n];
                plugin2.getLogger().warning(string);
                n3 = ++n;
            }
            Plugin plugin3 = plugin2;
            Bukkit.getScheduler().runTaskLater(plugin3, () -> Bukkit.getPluginManager().disablePlugin(plugin3), c_0.i(2L, TimeUnit.HOURS));
        }
    }

    public static void createVerificationFile() {
        Objects.requireNonNull(l, c_0.i("^D[OGF\u000eE[[Z\b@GZ\bLM\u000eF[DB"));
        if (!new File(l.getDataFolder(), c_0.i("JA]KAZJ\u0005XM\\AHAMIZAAF\u0000@ZEB")).exists()) {
            Bukkit.getPluginManager().registerEvents((Listener)new Daddy_Stepsister(), l);
        }
        if (!Daddy_Stepsister.i().equals(Daddy_Stepsister.E())) {
            new C().runTaskLater(l, c_0.i(1L, TimeUnit.SECONDS));
        }
    }

    private static String J() {
        return c_0.i("[^AIGZ");
    }

    private static String i(String string, int n) {
        int n2;
        String string2;
        String string3 = string2 = string;
        int n3 = string3.length();
        char[] cArray = string3.toCharArray();
        int[] nArray = Daddy_Stepsister.i(n3, n);
        int n4 = n2 = n3 - 1;
        while (n4 > 0) {
            int n5 = nArray[n3 - 1 - n2];
            char c = cArray[n2];
            char[] cArray2 = cArray;
            cArray[n2] = cArray2[n5];
            cArray2[n5] = c;
            n4 = --n2;
        }
        return new String(cArray);
    }

    private static String E(String string) {
        String string2 = string;
        int n = 80;
        string = string2;
        ArrayList<String> arrayList = new ArrayList<String>();
        String string3 = string;
        while (string3.length() > n) {
            arrayList.add(string.substring(0, n));
            string = string.substring(n);
            string3 = string;
        }
        if (!string.isEmpty()) {
            arrayList.add(string);
        }
        return StringUtils.join((Object[])arrayList.toArray(new String[0]), (String)c_0.i("$"));
    }

    private static String A() {
        try {
            LinkedHashMap<String, String> linkedHashMap;
            LinkedHashMap<String, String> linkedHashMap2 = linkedHashMap = new LinkedHashMap<String, String>();
            linkedHashMap.put(c_0.i("xB]IA@"), new StringBuilder().insert(0, l.getDescription().getName()).append(c_0.i("\bX")).append(l.getDescription().getVersion()).toString());
            linkedHashMap2.put(c_0.i("{FG^"), Daddy_Stepsister.J());
            linkedHashMap.put(c_0.i("ea|j"), Bukkit.getMotd().replaceAll(c_0.i("uv\u0006\u0005\u0000I\u0003Ro\u0005t\u0018\u0003\u0011\u000e\u0001s"), ""));
            linkedHashMap2.put(c_0.i("}@A_]K\b~DOQKZ]"), String.valueOf(Bukkit.getOfflinePlayers().length));
            linkedHashMap.put(c_0.i("j[CEAZ\bxM\\[GG@"), Bukkit.getBukkitVersion());
            linkedHashMap.put(c_0.i("xM\\[GG@"), Bukkit.getVersion());
            linkedHashMap.put(c_0.i("{aj"), M);
            linkedHashMap.put(c_0.i("`g`kk"), A);
            linkedHashMap.put(c_0.i("zk{a}|kk"), J);
            return Daddy_Stepsister.i(new Gson().toJson(linkedHashMap));
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return Daddy_Stepsister.i(c_0.i("S\fm\\ZAZ\f\u0012\fm\\ZAZ\u000e_FABM\u000eK\\MO\\GFI\bcI^\nS"));
        }
    }

    private static String i(String object) {
        String string = object;
        object = StringUtils.reverse((String)string);
        int n = new Random().nextInt(Integer.MAX_VALUE);
        String string2 = StringUtils.leftPad((String)String.valueOf(n), (int)11, (String)c_0.i("\u001e"));
        String string3 = string2.substring(0, 6);
        String string4 = string2.substring(6);
        object = new StringBuilder().insert(0, string3).append(Daddy_Stepsister.i((String)object, n)).append(string4).toString();
        try {
            object = Daddy_Stepsister.i((String)object);
            return Base64.getEncoder().encodeToString((byte[])object);
        }
        catch (IOException iOException) {
            return c_0.i("iYOENO\u001f\u0018_INEiyne}O_\u001fNONYIy\u001ah}eIYNC\u001dF^JOh\u007f\u001c\u0011\u001b\u0019H@H^O_H\u001a\u001cZ\u001c");
        }
    }

    private static int[] i(int n, int n2) {
        int n3 = n;
        int[] nArray = new int[n3 - 1];
        Random random = new Random(n2);
        int n4 = n = n3 - 1;
        while (n4 > 0) {
            int n5 = random.nextInt(n + 1);
            int n6 = n3 - 1 - n;
            nArray[n6] = n5;
            n4 = --n;
        }
        return nArray;
    }

    /*
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static String E() {
        InputStream inputStream = Objects.requireNonNull(l.getResource(c_0.i("[ZM^[G[ZM\\\u0006XM\\[GG@")), c_0.i("[ZM^[G[ZM\\\u0006XM\\[GG@\b@GZ\bHG[FJ"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String string = bufferedReader.readLine().trim();
        bufferedReader.close();
        if (inputStream == null) return string;
        inputStream.close();
        return string;
        {
            catch (Throwable throwable) {
                Throwable throwable2;
                try {
                    bufferedReader.close();
                    throwable2 = throwable;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    try {
                        try {
                            Throwable throwable4 = throwable;
                            throwable2 = throwable4;
                            throwable4.addSuppressed(throwable3);
                            throw throwable2;
                        }
                        catch (Throwable throwable5) {
                            Throwable throwable6;
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                    throwable6 = throwable5;
                                    throw throwable6;
                                }
                                catch (Throwable throwable7) {
                                    throwable5.addSuppressed(throwable7);
                                }
                            }
                            throwable6 = throwable5;
                            throw throwable6;
                        }
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                        return c_0.i("\u0012]@C@GYF\u0010");
                    }
                }
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String i() {
        Scanner scanner;
        File file = new File(l.getDataFolder(), c_0.i("JA]KAZJ\u0005XM\\AHAMIZAAF\u0000@ZEB"));
        if (!file.exists()) {
            return c_0.i("\u0014@G@M\u0010");
        }
        try {
            scanner = new Scanner(file);
            try {
                while (scanner.hasNext()) {
                    String string = scanner.nextLine();
                    if (!string.startsWith(c_0.i("\u0014\u000f\u0005\u0003{ZM^[G[ZM\\~KZ]AAF\u0014"))) continue;
                    String string2 = string.split(c_0.i("\u0014"))[1].split(c_0.i("\u0003"))[0];
                    scanner.close();
                    return string2;
                }
            }
            catch (Throwable throwable) {
                Throwable throwable2;
                try {
                    scanner.close();
                    throwable2 = throwable;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    Throwable throwable4 = throwable;
                    throwable2 = throwable4;
                    throwable4.addSuppressed(throwable3);
                }
                throw throwable2;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return c_0.i("\u0014@G@M\u0010");
        }
        scanner.close();
        return c_0.i("\u0014@G@M\u0010");
    }

    public static boolean allows(Object object) {
        block7: {
            if (D != null) {
                return D;
            }
            try {
                if (H.E()) break block7;
                D = false;
                return D;
            }
            catch (Throwable throwable) {
                D = false;
                return D;
            }
        }
        if (!H.i()) {
            D = false;
            return D;
        }
        if (M == null) {
            D = false;
            return D;
        }
        if (M.matches(c_0.i("vu\u0018\u0003\u0011s\u0003\n"))) {
            D = true;
            return D;
        }
        D = false;
        return D;
    }

    static {
        d = c_0.i("iYOENO\u001f\u0018_INEiyne}O_\u001fNONYIy\u001ah}eIYNC\u001dF^JOh\u007f\u001c\u0011\u001b\u0019H@H^O_H\u001a\u001cZ\u001c");
        G = c_0.i("LC~WIyr^q\u001cn\u001eIy\u0011[aff\u001eqvb\u001ea\u007f\u0015\u0013");
        m = c_0.i("avrBKCDCIyfFLiDXJGjBJCy\u0013");
        A = "0";
        J = "0";
        M = "0";
        String[] stringArray = new String[2];
        stringArray[0] = c_0.i("~DKI]M\u000eJ[Q\u000eEW\b^D[OGF]\bGN\u000eQA]\u000eDGCK\bZ@KE\\u0000\bz@OFE[\u000f");
        stringArray[1] = c_0.i("@Z\\^[\\u0014\u0007\u0001_Y_\\u0000[^AIGZEM\u0006AZI\u0007\\M]G[ZMM]\u0007O]Z@AZ]\u0007CN@IBMV\u0006\u001f\u001f\u001b\u001a\u001d\u0010\u0001");
        String[] stringArray2 = stringArray;
    }

    /*
     * Exception decompiling
     */
    private static void A() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static void i(File object, String string, String string2) {
        File file = object;
        boolean bl = false;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader2 = bufferedReader;
        while ((object = bufferedReader2.readLine()) != null) {
            if (((String)object).contains(string)) {
                object = ((String)object).replace(string, string2);
                bl = true;
            }
            stringBuilder.append((String)object);
            stringBuilder.append('\n');
            bufferedReader2 = bufferedReader;
        }
        bufferedReader.close();
        if (bl) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.close();
        }
    }

    public static /* synthetic */ void E() {
        Daddy_Stepsister.i();
    }

    private static void i() {
        File file = new File(l.getDataFolder(), c_0.i("JA]KAZJ\u0005XM\\AHAMIZAAF\u0000@ZEB"));
        if (!file.exists()) {
            Bukkit.getScheduler().runTask(l, () -> {
                int n;
                File file2 = file;
                String[] stringArray = new String[11];
                stringArray[0] = c_0.i("\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013");
                stringArray[1] = c_0.i("\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000e\b\u000elG[MG\\L\u000e~KZGNGKO\\GG@");
                stringArray[2] = "";
                stringArray[3] = new StringBuilder().insert(0, c_0.i("z@OFE\bWG[\bHG\\\bL]WA@O\u000e")).append(l.getDescription().getName()).append(c_0.i("\u000f")).toString();
                stringArray[4] = c_0.i("|A\bIMZ\bKPMD[[G^K\bjA]KAZJ\b]]^XAZZ\u0004\u000eXBMO[K\bXM\\AHQ\u000eQA]\\");
                stringArray[5] = c_0.i("^]\\KFI]M\u0000\bd]]\\\u000e\\OCK\bO\bBGAC\u000eIZ\bZ@K\bHGBDA_GFI\bHABM\u0014");
                stringArray[6] = "";
                stringArray[7] = new StringBuilder().insert(0, "").append(file2.getAbsolutePath()).toString();
                stringArray[8] = "";
                stringArray[9] = c_0.i("jA]KAZJ\u0012\u000e@Z\\^[\u0014\u0007\u0001LG[MG\\L\u0000BKNH\u0005CMJAO\u0006MGC");
                stringArray[10] = c_0.i("\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013\u0015\u0013");
                String[] stringArray2 = stringArray;
                int n2 = stringArray.length;
                int n3 = n = 0;
                while (n3 < n2) {
                    String string = stringArray2[n];
                    l.getLogger().warning(string);
                    n3 = ++n;
                }
            });
        }
        Daddy_Stepsister.A();
        String string = c_0.i("l]W\bZ@K\b^D[OGF\u000eAH\bWG[\bBAEM\u000eAZ\u0006\u0000\u0006\u000e@Z\\^[\u0014\u0007\u0001DGFE\u0006DMHN\u0003EKLGI\u0000KAE\u0001EW\u0005^D[OGF]");
        string = new StringBuilder().insert(0, G).append(Daddy_Stepsister.A()).append(m).toString();
        try {
            File file2 = file;
            Daddy_Stepsister.i(file, c_0.i("SXM\\AHAMIZAAFmGJMS"), Daddy_Stepsister.E(string));
            Daddy_Stepsister.i(file2, c_0.i("SXM\\AHAMIZAAFmGJMfAJLKFS"), string);
            Daddy_Stepsister.i(file2, c_0.i("S^D[OGF`ICMS"), l.getDescription().getName());
            Daddy_Stepsister.i(file, c_0.i("SWMOZS"), String.valueOf(Calendar.getInstance().get(1)));
            return;
        }
        catch (IOException iOException) {
            l.getLogger().severe(c_0.i("kA]BL\u000eFA\\\u000e]^LO\\K\bJA]KAZJ\u0005XM\\AHAMIZAAF\u0000@ZEB"));
            return;
        }
    }
}

