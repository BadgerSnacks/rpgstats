package com.bsnacks.rpgstats.permissions;

public final class RpgStatsPermissions {

    public static final String ROOT = "rpgstats";

    public static final String STATS_VIEW = ROOT + ".view";
    public static final String STATS_ADD = ROOT + ".add";
    public static final String STATS_ADD_ABILITY = ROOT + ".add.ability";
    public static final String STATS_SET = ROOT + ".set";
    public static final String STATS_SET_OTHERS = ROOT + ".set.others";
    public static final String STATS_RESET = ROOT + ".reset";
    public static final String STATS_RESET_OTHERS = ROOT + ".reset.others";

    private RpgStatsPermissions() {
    }
}
