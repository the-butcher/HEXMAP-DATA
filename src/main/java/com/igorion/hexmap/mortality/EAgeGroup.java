package com.igorion.hexmap.mortality;

import java.util.Arrays;
import java.util.Optional;

public enum EAgeGroup {

    /**
     * https://data.statistik.gv.at/data/OGD_gest_kalwo_alter_GEST_KALWOCHE_5J_100_C-ALTER5-0.csv
     */

    ETOTAL("TOTAL", "TOTAL"),
    E00_04("00_04", "ALTER5-1"),
    E05_09("05_09", "ALTER5-2"),
    E10_14("10_14", "ALTER5-3"),
    E15_19("15_19", "ALTER5-4"),
    E20_24("20_24", "ALTER5-5"),
    E25_29("25_29", "ALTER5-6"),
    E30_34("30_34", "ALTER5-7"),
    E35_39("35_39", "ALTER5-8"),
    E40_44("40_44", "ALTER5-9"),
    E45_49("45_49", "ALTER5-10"),
    E50_54("50_54", "ALTER5-11"),
    E55_59("55_59", "ALTER5-12"),
    E60_64("60_64", "ALTER5-13"),
    E65_69("65_69", "ALTER5-14"),
    E70_74("70_74", "ALTER5-15"),
    E75_79("75_79", "ALTER5-16"),
    E80_84("80_84", "ALTER5-17"),
    E85_89("85_89", "ALTER5-18"),

//    E90_00("90_00", "ALTER5-XX");
    E90_94("90_94", "ALTER5-19"),
    E95_00("95_00", "ALTER5-20");

    private final String name;
    private final String cAlter5;

    private EAgeGroup(String name, String cAlter5) {
        this.name = name;
        this.cAlter5 = cAlter5;
    }

    public String getName() {
        return this.name;
    }

    public String getCAlter5() {
        return this.cAlter5;
    }

    public static Optional<EAgeGroup> optAgeGroupByCAlter5(String cAlter5) {
        return Arrays.stream(EAgeGroup.values()).filter(g -> g.getCAlter5().equals(cAlter5)).findFirst();
    }

    public static Optional<EAgeGroup> optAgeGroupByName(String name) {
        return Arrays.stream(EAgeGroup.values()).filter(g -> g.getName().equals(name)).findFirst();
    }

}
