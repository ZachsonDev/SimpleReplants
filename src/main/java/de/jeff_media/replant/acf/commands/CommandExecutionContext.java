package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.UnstableAPI;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandExecutionContext<CEC extends CommandExecutionContext, I extends CommandIssuer> {
    private final RegisteredCommand cmd;
    private final CommandParameter param;
    protected final I issuer;
    private final List<String> args;
    private final int index;
    private final Map<String, Object> passedArgs;
    private final Map<String, String> flags;
    private final CommandManager manager;

    CommandExecutionContext(RegisteredCommand registeredCommand, CommandParameter commandParameter, I i, List<String> list, int n, Map<String, Object> map) {
        this.cmd = registeredCommand;
        this.manager = registeredCommand.scope.manager;
        this.param = commandParameter;
        this.issuer = i;
        this.args = list;
        this.index = n;
        this.passedArgs = map;
        this.flags = commandParameter.getFlags();
    }

    public String popFirstArg() {
        return !this.args.isEmpty() ? this.args.remove(0) : null;
    }

    public String popLastArg() {
        return !this.args.isEmpty() ? this.args.remove(this.args.size() - 1) : null;
    }

    public String getFirstArg() {
        return !this.args.isEmpty() ? this.args.get(0) : null;
    }

    public String getLastArg() {
        return !this.args.isEmpty() ? this.args.get(this.args.size() - 1) : null;
    }

    public boolean isLastArg() {
        return this.cmd.parameters.length - 1 == this.index;
    }

    public int getNumParams() {
        return this.cmd.parameters.length;
    }

    public boolean canOverridePlayerContext() {
        return this.cmd.requiredResolvers >= this.args.size();
    }

    public Object getResolvedArg(String string) {
        return this.passedArgs.get(string);
    }

    public Object getResolvedArg(Class<?> ... classArray) {
        for (Class<?> clazz : classArray) {
            for (Object object : this.passedArgs.values()) {
                if (!clazz.isInstance(object)) continue;
                return object;
            }
        }
        return null;
    }

    public <T> T getResolvedArg(String string, Class<?> ... classArray) {
        Object object = this.passedArgs.get(string);
        for (Class<?> clazz : classArray) {
            if (!clazz.isInstance(object)) continue;
            return (T)object;
        }
        return null;
    }

    public Set<String> getParameterPermissions() {
        return this.param.getRequiredPermissions();
    }

    public boolean isOptional() {
        return this.param.isOptional();
    }

    public boolean hasFlag(String string) {
        return this.flags.containsKey(string);
    }

    public String getFlagValue(String string, String string2) {
        return this.flags.getOrDefault(string, string2);
    }

    public Integer getFlagValue(String string, Integer n) {
        return ACFUtil.parseInt(this.flags.get(string), n);
    }

    public Long getFlagValue(String string, Long l) {
        return ACFUtil.parseLong(this.flags.get(string), l);
    }

    public Float getFlagValue(String string, Float f) {
        return ACFUtil.parseFloat(this.flags.get(string), f);
    }

    public Double getFlagValue(String string, Double d) {
        return ACFUtil.parseDouble(this.flags.get(string), d);
    }

    public Integer getIntFlagValue(String string, Number number) {
        return ACFUtil.parseInt(this.flags.get(string), number != null ? Integer.valueOf(number.intValue()) : null);
    }

    public Long getLongFlagValue(String string, Number number) {
        return ACFUtil.parseLong(this.flags.get(string), number != null ? Long.valueOf(number.longValue()) : null);
    }

    public Float getFloatFlagValue(String string, Number number) {
        return ACFUtil.parseFloat(this.flags.get(string), number != null ? Float.valueOf(number.floatValue()) : null);
    }

    public Double getDoubleFlagValue(String string, Number number) {
        return ACFUtil.parseDouble(this.flags.get(string), number != null ? Double.valueOf(number.doubleValue()) : null);
    }

    public Boolean getBooleanFlagValue(String string) {
        return this.getBooleanFlagValue(string, false);
    }

    public Boolean getBooleanFlagValue(String string, Boolean bl) {
        String string2 = this.flags.get(string);
        if (string2 == null) {
            return bl;
        }
        return ACFUtil.isTruthy(string2);
    }

    public Double getFlagValue(String string, Number number) {
        return ACFUtil.parseDouble(this.flags.get(string), number != null ? Double.valueOf(number.doubleValue()) : null);
    }

    @Deprecated
    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return this.param.getParameter().getAnnotation(clazz);
    }

    public <T extends Annotation> String getAnnotationValue(Class<T> clazz) {
        return this.manager.getAnnotations().getAnnotationValue(this.param.getParameter(), clazz);
    }

    public <T extends Annotation> String getAnnotationValue(Class<T> clazz, int n) {
        return this.manager.getAnnotations().getAnnotationValue(this.param.getParameter(), clazz, n);
    }

    public <T extends Annotation> boolean hasAnnotation(Class<T> clazz) {
        return this.manager.getAnnotations().hasAnnotation(this.param.getParameter(), clazz);
    }

    public RegisteredCommand getCmd() {
        return this.cmd;
    }

    @UnstableAPI
    CommandParameter getCommandParameter() {
        return this.param;
    }

    @Deprecated
    public Parameter getParam() {
        return this.param.getParameter();
    }

    public I getIssuer() {
        return this.issuer;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public int getIndex() {
        return this.index;
    }

    public Map<String, Object> getPassedArgs() {
        return this.passedArgs;
    }

    public Map<String, String> getFlags() {
        return this.flags;
    }

    public String joinArgs() {
        return ACFUtil.join(this.args, " ");
    }

    public String joinArgs(String string) {
        return ACFUtil.join(this.args, string);
    }
}

