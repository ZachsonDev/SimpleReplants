package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.UpdateChecker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class UserAgentBuilder {
    private final StringBuilder builder = new StringBuilder("JEFF-Media-GbR-SpigotUpdateChecker/").append("1.0.0");
    private final UpdateChecker instance = UpdateChecker.getInstance();
    private final List<String> list = new ArrayList<String>();
    private final Plugin plugin = this.instance.getPlugin();

    public static UserAgentBuilder getDefaultUserAgent() {
        return new UserAgentBuilder().addPluginNameAndVersion().addServerVersion().addBukkitVersion();
    }

    public UserAgentBuilder addBukkitVersion() {
        this.list.add("BukkitVersion/" + Bukkit.getBukkitVersion());
        return this;
    }

    public UserAgentBuilder addKeyValue(String string, String string2) {
        this.list.add(string + "/" + string2);
        return this;
    }

    public UserAgentBuilder addPlaintext(String string) {
        this.list.add(string);
        return this;
    }

    public UserAgentBuilder addPluginNameAndVersion() {
        this.list.add(this.plugin.getName() + "/" + this.plugin.getDescription().getVersion());
        return this;
    }

    public UserAgentBuilder addServerVersion() {
        this.list.add("ServerVersion/" + Bukkit.getVersion());
        return this;
    }

    public UserAgentBuilder addSpigotUserId() {
        String string = this.instance.isUsingPaidVersion() ? this.instance.getSpigotUserId() : "none";
        this.list.add("SpigotUID/" + string);
        return this;
    }

    public UserAgentBuilder addUsingPaidVersion() {
        this.list.add("Paid/" + this.instance.isUsingPaidVersion());
        return this;
    }

    protected String build() {
        if (this.list.size() > 0) {
            this.builder.append(" (");
            Iterator<String> iterator = this.list.iterator();
            while (iterator.hasNext()) {
                this.builder.append(iterator.next());
                if (!iterator.hasNext()) continue;
                this.builder.append(", ");
            }
            this.builder.append(")");
        }
        return this.builder.toString();
    }
}

