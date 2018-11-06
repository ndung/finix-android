package id.co.icg.reload.util;

import java.util.ArrayList;
import java.util.List;

import id.co.icg.reload.model.Tuple;

public class Provinces {

    private static List<Tuple> provinces = new ArrayList<>();

    static {
        provinces.add(new Tuple("11","Aceh"));
        provinces.add(new Tuple("12","Sumatera Utara"));
        provinces.add(new Tuple("13","Sumatera Barat"));
        provinces.add(new Tuple("14","Riau"));
        provinces.add(new Tuple("15","Jambi"));
        provinces.add(new Tuple("16","Sumatera Selatan"));
        provinces.add(new Tuple("17","Bengkulu"));
        provinces.add(new Tuple("18","Lampung"));
        provinces.add(new Tuple("19","Bangka Belitung"));
        provinces.add(new Tuple("21","Kep.Riau"));
        provinces.add(new Tuple("31","DKI Jakarta"));
        provinces.add(new Tuple("32","Jawa Barat"));
        provinces.add(new Tuple("33","Jawa Tengah"));
        provinces.add(new Tuple("34","DI Yogyakarta"));
        provinces.add(new Tuple("35","Jawa Timur"));
        provinces.add(new Tuple("36","Banten"));
        provinces.add(new Tuple("51","Bali"));
        provinces.add(new Tuple("52","Nusa Tenggara Barat"));
        provinces.add(new Tuple("53","Nusa Tenggara Timur"));
        provinces.add(new Tuple("61","Kalimantan Barat"));
        provinces.add(new Tuple("62","Kalimantan Tengah"));
        provinces.add(new Tuple("63","Kalimantan Selatan"));
        provinces.add(new Tuple("64","Kalimantan Timur"));
        provinces.add(new Tuple("65","Kalimantan Utara"));
        provinces.add(new Tuple("71","Sulawesi Utara"));
        provinces.add(new Tuple("72","Sulawesi Tengah"));
        provinces.add(new Tuple("73","Sulawesi Selatan"));
        provinces.add(new Tuple("74","Sulawesi Tenggara"));
        provinces.add(new Tuple("75","Gorontalo"));
        provinces.add(new Tuple("76","Sulawesi Barat"));
        provinces.add(new Tuple("81","Maluku"));
        provinces.add(new Tuple("82","Maluku Utara"));
        provinces.add(new Tuple("91","Papua Barat"));
        provinces.add(new Tuple("94","Papua"));
    }

    public static List<Tuple> getList(){
        return provinces;
    }
}
