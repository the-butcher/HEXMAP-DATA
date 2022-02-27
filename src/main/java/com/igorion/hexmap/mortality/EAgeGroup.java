package com.igorion.hexmap.mortality;

import java.util.Arrays;
import java.util.Optional;

public enum EAgeGroup {

    // ETOTAL("TOTAL"),
    E00_04("00_04"),
    E05_09("05_09"),
    E10_14("10_14"),
    E15_19("15_19"),
    E20_24("20_24"),
    E25_29("25_29"),
    E30_34("30_34"),
    E35_39("35_39"),
    E40_44("40_44"),
    E45_49("45_49"),
    E50_54("50_54"),
    E55_59("55_59"),
    E60_64("60_64"),
    E65_69("65_69"),
    E70_74("70_74"),
    E75_79("75_79"),
    E80_84("80_84"),
    E85_89("85_89"),
    E90_00("90_00");

    private final String name;

    private EAgeGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Optional<EAgeGroup> optAgeGroup(String name) {
        return Arrays.stream(EAgeGroup.values()).filter(g -> g.getName().equals(name)).findFirst();
    }

}
