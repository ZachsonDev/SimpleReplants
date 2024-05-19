package de.jeff_media.replant.config;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.utils.TimeUtils;

public class Config {
    public static final String CONFIG_PLUGIN_VERSION = "plugin-version";
    public static final String CONFIG_VERSION = "config-version";
    public static final String CROP_REPLANT_COST_AMOUNT = "crop-replant-cost-amount";
    public static final String USE_PERMISSIONS = "use-permissions";
    public static final String LANGUAGE = "language";
    public static final String CROP_REPLANT_DELAY = "crop-replant-delay";
    public static final String CROP_REPLANT_COSTS = "crop-replant-costs";
    public static final String CROP_REPLANT_ENABLED_BY_DEFAULT = "crop-replant-enabled-by-default";
    public static final String SAPLING_REPLANT = "plant-fallen-saplings";
    public static final String SAPLING_REPLANT_DELAY = "plant-fallen-saplings-delay";
    public static final String SAPLING_REPLANT_THROWN_BY_PLAYER = "plant-fallen-saplings-thrown-by-player";
    public static final String SAPLING_REPLANT_SEARCH_NEARBY = "search-nearby-when-block-is-occupied";
    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String CHECK_FOR_UPDATES_INTERVAL = "check-for-updates-interval";
    public static final String CALL_BLOCK_PLACE_EVENT = "call-block-place-event";
    public static final String USE_WORLDGUARD = "use-worldguard";
    public static final String DEBUG = "debug";
    public static final String SAPLING_SPREAD = "sapling-spread";
    public static final String CROP_REPLANT_DROPS_SEEDS = "crop-replant-drops-seeds";
    public static final String CROP_REPLANT_REQUIRES_HOE = "crop-replant-requires-hoe";
    public static final String LEATHER_PREVENTS_TRAMPLING = "leather-armor-prevents-trampling";
    public static final String ONLY_REQUIRE_BOOTS = "only-require-boots";
    public static final String CROP_REPLANT_REQUIRES_RIGHTCLICK = "crop-replant-requires-rightclick";
    public static final String MAX_SAPLINGS = "max-saplings";
    public static final String TREE_TWERKING = "tree-twerking";
    public static final String TWERK_RADIUS = "twerk-radius";
    public static final String REPLANT_PRIORITY = "replant-priority";
    public static final String CROP_REPLANT_ONLY_FULLY_GROWN = "crop-replant-only-fully-grown";
    public static final String CROP_REPLANT_REQUIRES_BOTH = "crop-replant-requires-both";
    public static final String DONT_REPLANT_WITH_BONEMEAL = "dont-replant-when-harvesting-with-bonemeal";
    public static final String PREVENT_HARVESTING_WITHOUT_HOE = "prevent-harvesting-without-hoe";

    public static void init() {
        Config.addDefault(PREVENT_HARVESTING_WITHOUT_HOE, false);
        Config.addDefault(DONT_REPLANT_WITH_BONEMEAL, false);
        Config.addDefault(CROP_REPLANT_REQUIRES_BOTH, false);
        Config.addDefault(USE_PERMISSIONS, false);
        Config.addDefault(LANGUAGE, "en");
        Config.addDefault(CROP_REPLANT_COST_AMOUNT, 1);
        Config.addDefault(MAX_SAPLINGS, 0);
        Config.addDefault(CROP_REPLANT_DELAY, 1.0);
        Config.addDefault(CROP_REPLANT_ONLY_FULLY_GROWN, true);
        Config.addDefault(REPLANT_PRIORITY, "NORMAL");
        Config.addDefault(CROP_REPLANT_COSTS, true);
        Config.addDefault(CROP_REPLANT_ENABLED_BY_DEFAULT, false);
        Config.addDefault(SAPLING_REPLANT, true);
        Config.addDefault(SAPLING_REPLANT_DELAY, 120.0);
        Config.addDefault(SAPLING_REPLANT_THROWN_BY_PLAYER, true);
        Config.addDefault(SAPLING_REPLANT_SEARCH_NEARBY, true);
        Config.addDefault(CHECK_FOR_UPDATES, true);
        Config.addDefault(CHECK_FOR_UPDATES_INTERVAL, 4);
        Config.addDefault(CALL_BLOCK_PLACE_EVENT, true);
        Config.addDefault(USE_WORLDGUARD, true);
        Config.addDefault(DEBUG, false);
        Config.addDefault(SAPLING_SPREAD, 1);
        Config.addDefault(CROP_REPLANT_DROPS_SEEDS, true);
        Config.addDefault(CROP_REPLANT_REQUIRES_HOE, false);
        Config.addDefault(LEATHER_PREVENTS_TRAMPLING, false);
        Config.addDefault(ONLY_REQUIRE_BOOTS, true);
        Config.addDefault(CROP_REPLANT_REQUIRES_RIGHTCLICK, false);
        Config.addDefault(TREE_TWERKING, true);
        Config.addDefault(TWERK_RADIUS, 3);
    }

    private static void addDefault(String string, Object object) {
        Main.getInstance().getConfig().addDefault(string, object);
    }

    public static int getCropReplantDelayInTicks() {
        double d = Main.getInstance().getConfig().getDouble(CROP_REPLANT_DELAY);
        return TimeUtils.secondsToTicks(d);
    }

    public static int getSaplingReplantDelayInTicks() {
        double d = Main.getInstance().getConfig().getDouble(SAPLING_REPLANT_DELAY);
        d = Math.min(d, 300.0);
        return TimeUtils.secondsToTicks(d);
    }
}

