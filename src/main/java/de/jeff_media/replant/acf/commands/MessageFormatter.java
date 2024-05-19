package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

public abstract class MessageFormatter<FT> {
    private final List<FT> colors = new ArrayList<FT>();

    @SafeVarargs
    public MessageFormatter(FT ... FTArray) {
        this.colors.addAll(Arrays.asList(FTArray));
    }

    public FT setColor(int n, FT FT) {
        n = n > 0 ? --n : 0;
        if (this.colors.size() <= n) {
            int n2 = n - this.colors.size();
            if (n2 > 0) {
                this.colors.addAll(Collections.nCopies(n2, null));
            }
            this.colors.add(FT);
            return null;
        }
        return this.colors.set(n, FT);
    }

    public FT getColor(int n) {
        n = n > 0 ? --n : 0;
        FT FT = this.colors.get(n);
        if (FT == null) {
            FT = this.getDefaultColor();
        }
        return FT;
    }

    public FT getDefaultColor() {
        return this.getColor(1);
    }

    abstract String format(FT var1, String var2);

    public String format(int n, String string) {
        return this.format(this.getColor(n), string);
    }

    public String format(String string) {
        String string2 = this.format(1, "");
        Matcher matcher = ACFPatterns.FORMATTER.matcher(string);
        StringBuffer stringBuffer = new StringBuffer(string.length());
        while (matcher.find()) {
            Integer n = ACFUtil.parseInt(matcher.group("color"), 1);
            String string3 = this.format(n, matcher.group("msg")) + string2;
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(string3));
        }
        matcher.appendTail(stringBuffer);
        return string2 + stringBuffer.toString();
    }
}

