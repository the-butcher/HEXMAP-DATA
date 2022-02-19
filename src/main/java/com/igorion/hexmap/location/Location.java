package com.igorion.hexmap.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;
import com.igorion.util.impl.Storage;

public class Location {

    public static Map<String, String> KEYSET_PROVINCE = new LinkedHashMap<>();
    static {
        KEYSET_PROVINCE.put("#", "Österreich");
        KEYSET_PROVINCE.put("1", "Burgenland");
        KEYSET_PROVINCE.put("2", "Kärnten");
        KEYSET_PROVINCE.put("3", "Niederösterreich");
        KEYSET_PROVINCE.put("4", "Oberösterreich");
        KEYSET_PROVINCE.put("5", "Salzburg");
        KEYSET_PROVINCE.put("6", "Steiermark");
        KEYSET_PROVINCE.put("7", "Tirol");
        KEYSET_PROVINCE.put("8", "Vorarlberg");
        KEYSET_PROVINCE.put("9", "Wien");
    }

    public static Map<String, String> KEYSET_DISTRICT = new LinkedHashMap<>();
    static {
        KEYSET_DISTRICT.put("###", "Österreich");
        KEYSET_DISTRICT.put("1##", "Burgenland");
        KEYSET_DISTRICT.put("101", "Eisenstadt(Stadt)");
        KEYSET_DISTRICT.put("102", "Rust(Stadt)");
        KEYSET_DISTRICT.put("103", "Eisenstadt-Umgebung");
        KEYSET_DISTRICT.put("104", "Güssing");
        KEYSET_DISTRICT.put("105", "Jennersdorf");
        KEYSET_DISTRICT.put("106", "Mattersburg");
        KEYSET_DISTRICT.put("107", "Neusiedl am See");
        KEYSET_DISTRICT.put("108", "Oberpullendorf");
        KEYSET_DISTRICT.put("109", "Oberwart");
        KEYSET_DISTRICT.put("2##", "Kärnten");
        KEYSET_DISTRICT.put("201", "Klagenfurt Stadt");
        KEYSET_DISTRICT.put("202", "Villach Stadt");
        KEYSET_DISTRICT.put("203", "Hermagor");
        KEYSET_DISTRICT.put("204", "Klagenfurt Land");
        KEYSET_DISTRICT.put("205", "Sankt Veit an der Glan");
        KEYSET_DISTRICT.put("206", "Spittal an der Drau");
        KEYSET_DISTRICT.put("207", "Villach Land");
        KEYSET_DISTRICT.put("208", "Völkermarkt");
        KEYSET_DISTRICT.put("209", "Wolfsberg");
        KEYSET_DISTRICT.put("210", "Feldkirchen");
        KEYSET_DISTRICT.put("3##", "Niederösterreich");
        KEYSET_DISTRICT.put("301", "Krems an der Donau(Stadt)");
        KEYSET_DISTRICT.put("302", "Sankt Pölten(Stadt)");
        KEYSET_DISTRICT.put("303", "Waidhofen an der Ybbs(Stadt)");
        KEYSET_DISTRICT.put("304", "Wiener Neustadt(Stadt)");
        KEYSET_DISTRICT.put("305", "Amstetten");
        KEYSET_DISTRICT.put("306", "Baden");
        KEYSET_DISTRICT.put("307", "Bruck an der Leitha");
        KEYSET_DISTRICT.put("308", "Gänserndorf");
        KEYSET_DISTRICT.put("309", "Gmünd");
        KEYSET_DISTRICT.put("310", "Hollabrunn");
        KEYSET_DISTRICT.put("311", "Horn");
        KEYSET_DISTRICT.put("312", "Korneuburg");
        KEYSET_DISTRICT.put("313", "Krems(Land)");
        KEYSET_DISTRICT.put("314", "Lilienfeld");
        KEYSET_DISTRICT.put("315", "Melk");
        KEYSET_DISTRICT.put("316", "Mistelbach");
        KEYSET_DISTRICT.put("317", "Mödling");
        KEYSET_DISTRICT.put("318", "Neunkirchen");
        KEYSET_DISTRICT.put("319", "Sankt Pölten(Land)");
        KEYSET_DISTRICT.put("320", "Scheibbs");
        KEYSET_DISTRICT.put("321", "Tulln");
        KEYSET_DISTRICT.put("322", "Waidhofen an der Thaya");
        KEYSET_DISTRICT.put("323", "Wiener Neustadt(Land)");
        KEYSET_DISTRICT.put("325", "Zwettl");
        KEYSET_DISTRICT.put("4##", "Oberösterreich");
        KEYSET_DISTRICT.put("401", "Linz(Stadt)");
        KEYSET_DISTRICT.put("402", "Steyr(Stadt)");
        KEYSET_DISTRICT.put("403", "Wels(Stadt)");
        KEYSET_DISTRICT.put("404", "Braunau am Inn");
        KEYSET_DISTRICT.put("405", "Eferding");
        KEYSET_DISTRICT.put("406", "Freistadt");
        KEYSET_DISTRICT.put("407", "Gmunden");
        KEYSET_DISTRICT.put("408", "Grieskirchen");
        KEYSET_DISTRICT.put("409", "Kirchdorf an der Krems");
        KEYSET_DISTRICT.put("410", "Linz-Land");
        KEYSET_DISTRICT.put("411", "Perg");
        KEYSET_DISTRICT.put("412", "Ried im Innkreis");
        KEYSET_DISTRICT.put("413", "Rohrbach");
        KEYSET_DISTRICT.put("414", "Schärding");
        KEYSET_DISTRICT.put("415", "Steyr-Land");
        KEYSET_DISTRICT.put("416", "Urfahr-Umgebung");
        KEYSET_DISTRICT.put("417", "Vöcklabruck");
        KEYSET_DISTRICT.put("418", "Wels-Land");
        KEYSET_DISTRICT.put("5##", "Salzburg");
        KEYSET_DISTRICT.put("501", "Salzburg(Stadt)");
        KEYSET_DISTRICT.put("502", "Hallein");
        KEYSET_DISTRICT.put("503", "Salzburg-Umgebung");
        KEYSET_DISTRICT.put("504", "Sankt Johann im Pongau");
        KEYSET_DISTRICT.put("505", "Tamsweg");
        KEYSET_DISTRICT.put("506", "Zell am See");
        KEYSET_DISTRICT.put("6##", "Steiermark");
        KEYSET_DISTRICT.put("601", "Graz(Stadt)");
        KEYSET_DISTRICT.put("603", "Deutschlandsberg");
        KEYSET_DISTRICT.put("606", "Graz-Umgebung");
        KEYSET_DISTRICT.put("610", "Leibnitz");
        KEYSET_DISTRICT.put("611", "Leoben");
        KEYSET_DISTRICT.put("612", "Liezen");
        KEYSET_DISTRICT.put("614", "Murau");
        KEYSET_DISTRICT.put("616", "Voitsberg");
        KEYSET_DISTRICT.put("617", "Weiz");
        KEYSET_DISTRICT.put("620", "Murtal");
        KEYSET_DISTRICT.put("621", "Bruck-Mürzzuschlag");
        KEYSET_DISTRICT.put("622", "Hartberg-Fürstenfeld");
        KEYSET_DISTRICT.put("623", "Südoststeiermark");
        KEYSET_DISTRICT.put("7##", "Tirol");
        KEYSET_DISTRICT.put("701", "Innsbruck-Stadt");
        KEYSET_DISTRICT.put("702", "Imst");
        KEYSET_DISTRICT.put("703", "Innsbruck-Land");
        KEYSET_DISTRICT.put("704", "Kitzbühel");
        KEYSET_DISTRICT.put("705", "Kufstein");
        KEYSET_DISTRICT.put("706", "Landeck");
        KEYSET_DISTRICT.put("707", "Lienz");
        KEYSET_DISTRICT.put("708", "Reutte");
        KEYSET_DISTRICT.put("709", "Schwaz");
        KEYSET_DISTRICT.put("8##", "Vorarlberg");
        KEYSET_DISTRICT.put("801", "Bludenz");
        KEYSET_DISTRICT.put("802", "Bregenz");
        KEYSET_DISTRICT.put("803", "Dornbirn");
        KEYSET_DISTRICT.put("804", "Feldkirch");
        KEYSET_DISTRICT.put("9##", "Wien");

    }

    protected static Map<String, String> KEYSET_MUNICIPALITY = new LinkedHashMap<>();
    static {
        KEYSET_MUNICIPALITY.put("#####", "Österreich");
        KEYSET_MUNICIPALITY.put("1####", "Burgenland");
        KEYSET_MUNICIPALITY.put("2####", "Kärnten");
        KEYSET_MUNICIPALITY.put("3####", "Niederösterreich");
        KEYSET_MUNICIPALITY.put("4####", "Oberösterreich");
        KEYSET_MUNICIPALITY.put("5####", "Salzburg");
        KEYSET_MUNICIPALITY.put("6####", "Steiermark");
        KEYSET_MUNICIPALITY.put("7####", "Tirol");
        KEYSET_MUNICIPALITY.put("8####", "Vorarlberg");
        KEYSET_MUNICIPALITY.put("9####", "Wien");
    }

    public static final File FILE_POP_GKZ = new File(Storage.FOLDER___WORK, "endgueltige_bevoelkerungszahl_fuer_das_finanzjahr_2022_je_gemeinde.csv");

    public static synchronized Map<String, String> getKeysetMunicipality() throws Exception {

        if (KEYSET_MUNICIPALITY.size() <= 10) {

            try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_POP_GKZ), StandardCharsets.UTF_8))) {

                IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
                IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
                List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();

                for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                    String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                    String pg = caseCsvRecord.optValue("pg", FieldTypes.STRING).orElseThrow();

                    KEYSET_MUNICIPALITY.put(gkz, pg);

                }

            }

        }
        return KEYSET_MUNICIPALITY;

    }

}
