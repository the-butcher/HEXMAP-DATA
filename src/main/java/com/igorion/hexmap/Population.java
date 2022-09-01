package com.igorion.hexmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class Population {

    public static final String TOTAL = "alle Altersgruppen";

    public static Map<String, String> KEYSET_AGE_GROUP = new LinkedHashMap<>();
    static {
        KEYSET_AGE_GROUP.put("#", TOTAL);
        KEYSET_AGE_GROUP.put("0", "<= 05");
        KEYSET_AGE_GROUP.put("1", "05-14");
        KEYSET_AGE_GROUP.put("2", "15-24");
        KEYSET_AGE_GROUP.put("3", "25-34");
        KEYSET_AGE_GROUP.put("4", "35-44");
        KEYSET_AGE_GROUP.put("5", "45-54");
        KEYSET_AGE_GROUP.put("6", "55-64");
        KEYSET_AGE_GROUP.put("7", "65-74");
        KEYSET_AGE_GROUP.put("8", "75-84");
        KEYSET_AGE_GROUP.put("9", ">= 85");
    }

    public static Map<String, String> KEYSET_AGE_GROUP_VACC = new LinkedHashMap<>();
    static {
        KEYSET_AGE_GROUP_VACC.put("#", TOTAL);
        KEYSET_AGE_GROUP_VACC.put("0", "<= 11");
        KEYSET_AGE_GROUP_VACC.put("1", "12-14");
        KEYSET_AGE_GROUP_VACC.put("2", "15-24");
        KEYSET_AGE_GROUP_VACC.put("3", "25-34");
        KEYSET_AGE_GROUP_VACC.put("4", "35-44");
        KEYSET_AGE_GROUP_VACC.put("5", "45-54");
        KEYSET_AGE_GROUP_VACC.put("6", "55-64");
        KEYSET_AGE_GROUP_VACC.put("7", "65-74");
        KEYSET_AGE_GROUP_VACC.put("8", "75-84");
        KEYSET_AGE_GROUP_VACC.put("9", ">= 85");
    }

    public static Map<String, String> KEYSET_GKZ_OVERRIDE = new LinkedHashMap<>();
    static {

        KEYSET_GKZ_OVERRIDE.put("900", "9##");

        KEYSET_GKZ_OVERRIDE.put("70370", "70327");

//        KEYSET_GKZ_OVERRIDE.put("90101", "90001");
//        KEYSET_GKZ_OVERRIDE.put("90201", "90002");
//        KEYSET_GKZ_OVERRIDE.put("90301", "90003");
//        KEYSET_GKZ_OVERRIDE.put("90401", "90004");
//        KEYSET_GKZ_OVERRIDE.put("90501", "90005");
//        KEYSET_GKZ_OVERRIDE.put("90601", "90006");
//        KEYSET_GKZ_OVERRIDE.put("90701", "90007");
//        KEYSET_GKZ_OVERRIDE.put("90801", "90008");
//        KEYSET_GKZ_OVERRIDE.put("90901", "90009");
//        KEYSET_GKZ_OVERRIDE.put("91001", "90010");
//        KEYSET_GKZ_OVERRIDE.put("91101", "90011");
//        KEYSET_GKZ_OVERRIDE.put("91201", "90012");
//        KEYSET_GKZ_OVERRIDE.put("91301", "90013");
//        KEYSET_GKZ_OVERRIDE.put("91401", "90014");
//        KEYSET_GKZ_OVERRIDE.put("91501", "90015");
//        KEYSET_GKZ_OVERRIDE.put("91601", "90016");
//        KEYSET_GKZ_OVERRIDE.put("91701", "90017");
//        KEYSET_GKZ_OVERRIDE.put("91801", "90018");
//        KEYSET_GKZ_OVERRIDE.put("91901", "90019");
//        KEYSET_GKZ_OVERRIDE.put("92001", "90020");
//        KEYSET_GKZ_OVERRIDE.put("92101", "90021");
//        KEYSET_GKZ_OVERRIDE.put("92201", "90022");
//        KEYSET_GKZ_OVERRIDE.put("92301", "90023");
    }

}
