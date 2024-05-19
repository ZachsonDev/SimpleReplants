package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.WorldUtils;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import java.io.File;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class ProfileUtils {
    public static UUID getUUIDFromString(@NotNull String string) {
        if (string.length() == 36) {
            return UUID.fromString(string);
        }
        if (string.length() == 32) {
            return ProfileUtils.fromStringWithoutDashes(string);
        }
        throw new IllegalArgumentException("Not a valid UUID.");
    }

    private static UUID fromStringWithoutDashes(@NotNull String string) {
        return UUID.fromString(string.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    public static boolean isValidUUID(@NotNull String string) {
        return string.replace("-", "").matches("^\\p{XDigit}{32}$");
    }

    public static boolean isValidAccountName(@NotNull String string) {
        return string.matches("^\\w{3,16}$");
    }

    @NMS
    @NotNull
    public static File getPlayerDataFile(UUID uUID) {
        File file = new File(WorldUtils.getDefaultWorld().getWorldFolder(), "playerdata");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, uUID.toString() + ".dat");
    }
}

