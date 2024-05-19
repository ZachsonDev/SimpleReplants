package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.UpdateCheckResult;
import de.jeff_media.replant.updatechecker.UpdateCheckSuccess;
import de.jeff_media.replant.updatechecker.UpdateChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateCheckEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UpdateChecker instance = UpdateChecker.getInstance();
    private final UpdateCheckResult result;
    private final UpdateCheckSuccess success;
    @Nullable
    private CommandSender[] requesters = null;

    protected UpdateCheckEvent(UpdateCheckSuccess updateCheckSuccess) {
        this.success = updateCheckSuccess;
        this.result = updateCheckSuccess == UpdateCheckSuccess.FAIL && this.instance.getLatestVersion() == null ? UpdateCheckResult.UNKNOWN : (this.instance.isUsingLatestVersion() ? UpdateCheckResult.RUNNING_LATEST_VERSION : UpdateCheckResult.NEW_VERSION_AVAILABLE);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Nullable
    public String getLatestVersion() {
        return this.instance.getLatestVersion();
    }

    @Nullable
    public CommandSender[] getRequesters() {
        if (this.requesters == null || this.requesters.length == 0) {
            return null;
        }
        return this.requesters;
    }

    protected UpdateCheckEvent setRequesters(CommandSender ... commandSenderArray) {
        this.requesters = commandSenderArray;
        return this;
    }

    public UpdateCheckResult getResult() {
        return this.result;
    }

    public UpdateCheckSuccess getSuccess() {
        return this.success;
    }

    @NotNull
    public String getUsedVersion() {
        return this.instance.getUsedVersion();
    }
}

