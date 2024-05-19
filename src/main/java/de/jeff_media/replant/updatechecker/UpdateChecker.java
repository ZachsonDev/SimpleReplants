package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.DefaultArtifactVersion;
import de.jeff_media.replant.updatechecker.InternalUpdateCheckListener;
import de.jeff_media.replant.updatechecker.UpdateCheckEvent;
import de.jeff_media.replant.updatechecker.UpdateCheckResult;
import de.jeff_media.replant.updatechecker.UpdateCheckSuccess;
import de.jeff_media.replant.updatechecker.UserAgentBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateChecker {
    protected static final String VERSION = "1.0.0";
    private static final String SPIGOT_CHANGELOG_SUFFIX = "/history";
    private static final String SPIGOT_DOWNLOAD_LINK = "https://www.spigotmc.org/resources/";
    private static final String SPIGOT_UPDATE_API = "https://api.spigotmc.org/legacy/update.php?resource=";
    private static UpdateChecker instance = null;
    private static boolean listenerAlreadyRegistered = false;
    private final String spigotUserId = "0";
    private String apiLink = null;
    private String changelogLink = null;
    private boolean checkedAtLeastOnce = false;
    private boolean coloredConsoleOutput = false;
    private String donationLink = null;
    private String freeDownloadLink = null;
    private String latestVersion = null;
    private Plugin main = null;
    private String nameFreeVersion = "Paid";
    private String namePaidVersion = "Paid";
    private boolean notifyOpsOnJoin = true;
    private String notifyPermission = null;
    private boolean notifyRequesters = true;
    private boolean suppressUpToDateMessage = false;
    private BiConsumer<CommandSender[], Exception> onFail = (commandSenderArray, exception) -> exception.printStackTrace();
    private BiConsumer<CommandSender[], String> onSuccess = (commandSenderArray, string) -> {};
    private String paidDownloadLink = null;
    private int taskId = -1;
    private int timeout = 0;
    private String usedVersion = null;
    private String userAgentString = null;
    private boolean usingPaidVersion = false;

    private UpdateChecker() {
    }

    public static UpdateChecker getInstance() {
        if (instance == null) {
            instance = new UpdateChecker();
        }
        return instance;
    }

    public static UpdateChecker init(@NotNull Plugin plugin, int n) {
        return UpdateChecker.init(plugin, SPIGOT_UPDATE_API + n);
    }

    public static UpdateChecker init(@NotNull Plugin plugin, @NotNull String string) {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(string, "API Link cannot be null.");
        UpdateChecker updateChecker = UpdateChecker.getInstance();
        updateChecker.main = plugin;
        updateChecker.usedVersion = plugin.getDescription().getVersion().trim();
        updateChecker.apiLink = string;
        if (updateChecker.detectPaidVersion()) {
            updateChecker.usingPaidVersion = true;
        }
        if (!listenerAlreadyRegistered) {
            Bukkit.getPluginManager().registerEvents((Listener)new InternalUpdateCheckListener(), plugin);
            listenerAlreadyRegistered = true;
        }
        return updateChecker;
    }

    public static boolean isOtherVersionNewer(String string, String string2) {
        DefaultArtifactVersion defaultArtifactVersion = new DefaultArtifactVersion(string);
        DefaultArtifactVersion defaultArtifactVersion2 = new DefaultArtifactVersion(string2);
        return defaultArtifactVersion.compareTo(defaultArtifactVersion2) < 0;
    }

    public boolean isSuppressUpToDateMessage() {
        return this.suppressUpToDateMessage;
    }

    public UpdateChecker checkEveryXHours(double d) {
        double d2 = d * 60.0;
        double d3 = d2 * 60.0;
        long l = (int)d3 * 20;
        this.stop();
        this.taskId = l > 0L ? Bukkit.getScheduler().scheduleSyncRepeatingTask(this.main, () -> this.checkNow(new CommandSender[]{Bukkit.getConsoleSender()}), l, l) : -1;
        return this;
    }

    public UpdateChecker checkNow() {
        this.checkNow(new CommandSender[]{Bukkit.getConsoleSender()});
        return this;
    }

    public UpdateChecker checkNow(CommandSender ... commandSenderArray) {
        if (this.main == null) {
            throw new IllegalStateException("Plugin has not been set.");
        }
        if (this.apiLink == null) {
            throw new IllegalStateException("API Link has not been set.");
        }
        this.checkedAtLeastOnce = true;
        if (this.userAgentString == null) {
            this.userAgentString = UserAgentBuilder.getDefaultUserAgent().build();
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {
            UpdateCheckEvent updateCheckEvent;
            Object object;
            try {
                object = (HttpURLConnection)new URL(this.apiLink).openConnection();
                object.addRequestProperty("User-Agent", this.userAgentString);
                if (this.timeout > 0) {
                    object.setConnectTimeout(this.timeout);
                }
                InputStreamReader inputStreamReader = new InputStreamReader(object.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                this.latestVersion = bufferedReader.readLine().trim();
                bufferedReader.close();
                if (!this.isUsingLatestVersion() && !UpdateChecker.isOtherVersionNewer(this.usedVersion, this.latestVersion)) {
                    this.latestVersion = this.usedVersion;
                }
                updateCheckEvent = new UpdateCheckEvent(UpdateCheckSuccess.SUCCESS);
            }
            catch (Exception exception) {
                updateCheckEvent = new UpdateCheckEvent(UpdateCheckSuccess.FAIL);
                Bukkit.getScheduler().runTask(this.main, () -> this.getOnFail().accept(commandSenderArray, exception));
            }
            object = updateCheckEvent.setRequesters(commandSenderArray);
            Bukkit.getScheduler().runTask(this.main, () -> this.lambda$checkNow$4((UpdateCheckEvent)((Object)((Object)object)), commandSenderArray));
        });
        return this;
    }

    private void checkRelocation() {
        String string = new String(new byte[]{100, 101, 46, 106, 101, 102, 102, 95, 109, 101, 100, 105, 97, 46, 117, 112, 100, 97, 116, 101, 99, 104, 101, 99, 107, 101, 114});
        String string2 = new String(new byte[]{121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101});
        if (this.getClass().getPackage().getName().startsWith(string) || this.getClass().getPackage().getName().startsWith(string2)) {
            throw new IllegalStateException("UpdateChecker class has not been relocated correctly!");
        }
    }

    private boolean detectPaidVersion() {
        return "0".matches("^[0-9]+$");
    }

    public List<String> getAppropriateDownloadLinks() {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (this.usingPaidVersion) {
            if (this.paidDownloadLink != null) {
                arrayList.add(this.paidDownloadLink);
            } else if (this.freeDownloadLink != null) {
                arrayList.add(this.freeDownloadLink);
            }
        } else {
            if (this.paidDownloadLink != null) {
                arrayList.add(this.paidDownloadLink);
            }
            if (this.freeDownloadLink != null) {
                arrayList.add(this.freeDownloadLink);
            }
        }
        return arrayList;
    }

    public String getChangelogLink() {
        return this.changelogLink;
    }

    public UpdateChecker setChangelogLink(int n) {
        return this.setChangelogLink(SPIGOT_DOWNLOAD_LINK + n + SPIGOT_CHANGELOG_SUFFIX);
    }

    public UpdateChecker setChangelogLink(@Nullable String string) {
        this.changelogLink = string;
        return this;
    }

    public String getDonationLink() {
        return this.donationLink;
    }

    public UpdateChecker setDonationLink(@Nullable String string) {
        this.donationLink = string;
        return this;
    }

    public UpdateCheckResult getLastCheckResult() {
        if (this.latestVersion == null) {
            return UpdateCheckResult.UNKNOWN;
        }
        if (this.latestVersion.equals(this.usedVersion)) {
            return UpdateCheckResult.RUNNING_LATEST_VERSION;
        }
        return UpdateCheckResult.NEW_VERSION_AVAILABLE;
    }

    public String getLatestVersion() {
        return this.latestVersion;
    }

    public String getNameFreeVersion() {
        return this.nameFreeVersion;
    }

    public UpdateChecker setNameFreeVersion(String string) {
        this.nameFreeVersion = string;
        return this;
    }

    public String getNamePaidVersion() {
        return this.namePaidVersion;
    }

    public UpdateChecker setNamePaidVersion(String string) {
        this.namePaidVersion = string;
        return this;
    }

    @Nullable
    public String getNotifyPermission() {
        return this.notifyPermission;
    }

    public BiConsumer<CommandSender[], Exception> getOnFail() {
        return this.onFail;
    }

    public BiConsumer<CommandSender[], String> getOnSuccess() {
        return this.onSuccess;
    }

    protected Plugin getPlugin() {
        return this.main;
    }

    public String getSpigotUserId() {
        return "%%__USER__%%";
    }

    public String getUsedVersion() {
        return this.usedVersion;
    }

    public boolean isCheckedAtLeastOnce() {
        return this.checkedAtLeastOnce;
    }

    public boolean isColoredConsoleOutput() {
        return this.coloredConsoleOutput;
    }

    public UpdateChecker setColoredConsoleOutput(boolean bl) {
        this.coloredConsoleOutput = bl;
        return this;
    }

    public boolean isNotifyOpsOnJoin() {
        return this.notifyOpsOnJoin;
    }

    public UpdateChecker setNotifyOpsOnJoin(boolean bl) {
        this.notifyOpsOnJoin = bl;
        return this;
    }

    public boolean isNotifyRequesters() {
        return this.notifyRequesters;
    }

    public UpdateChecker setNotifyRequesters(boolean bl) {
        this.notifyRequesters = bl;
        return this;
    }

    public boolean isUsingLatestVersion() {
        return this.usedVersion.equals(UpdateChecker.instance.latestVersion);
    }

    public boolean isUsingPaidVersion() {
        return this.usingPaidVersion;
    }

    public UpdateChecker setUsingPaidVersion(boolean bl) {
        this.usingPaidVersion = bl;
        return this;
    }

    public UpdateChecker onFail(BiConsumer<CommandSender[], Exception> biConsumer) {
        this.onFail = biConsumer == null ? (commandSenderArray, exception) -> exception.printStackTrace() : biConsumer;
        return this;
    }

    public UpdateChecker onSuccess(BiConsumer<CommandSender[], String> biConsumer) {
        this.onSuccess = biConsumer == null ? (commandSenderArray, string) -> {} : biConsumer;
        return this;
    }

    public UpdateChecker setDownloadLink(int n) {
        return this.setDownloadLink(SPIGOT_DOWNLOAD_LINK + n);
    }

    public UpdateChecker setDownloadLink(@Nullable String string) {
        this.paidDownloadLink = null;
        this.freeDownloadLink = string;
        return this;
    }

    public UpdateChecker suppressUpToDateMessage(boolean bl) {
        this.suppressUpToDateMessage = bl;
        return this;
    }

    public UpdateChecker setFreeDownloadLink(int n) {
        return this.setFreeDownloadLink(SPIGOT_DOWNLOAD_LINK + n);
    }

    public UpdateChecker setFreeDownloadLink(@Nullable String string) {
        this.freeDownloadLink = string;
        return this;
    }

    public UpdateChecker setNotifyByPermissionOnJoin(@Nullable String string) {
        this.notifyPermission = string;
        return this;
    }

    public UpdateChecker setPaidDownloadLink(int n) {
        return this.setPaidDownloadLink(SPIGOT_DOWNLOAD_LINK + n);
    }

    public UpdateChecker setPaidDownloadLink(@NotNull String string) {
        this.paidDownloadLink = string;
        return this;
    }

    public UpdateChecker setTimeout(int n) {
        this.timeout = n;
        return this;
    }

    public UpdateChecker setUserAgent(@NotNull UserAgentBuilder userAgentBuilder) {
        this.userAgentString = userAgentBuilder.build();
        return this;
    }

    public UpdateChecker setUserAgent(@Nullable String string) {
        this.userAgentString = string;
        return this;
    }

    public UpdateChecker stop() {
        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
        }
        this.taskId = -1;
        return this;
    }

    private /* synthetic */ void lambda$checkNow$4(UpdateCheckEvent updateCheckEvent, CommandSender[] commandSenderArray) {
        if (updateCheckEvent.getSuccess() == UpdateCheckSuccess.SUCCESS) {
            this.getOnSuccess().accept(commandSenderArray, this.latestVersion);
        }
        Bukkit.getPluginManager().callEvent((Event)updateCheckEvent);
    }
}

