package com.commerzinfo.input.csv;

@SuppressWarnings("unused")
public class CSVBean {
    private String buchungstag;
    private String wertstellung;
    private String buchungstext;
    private String betrag;
    private String waehrung;

    public String getBuchungstag() {
        return buchungstag;
    }

    public String getWertstellung() {
        return wertstellung;
    }

    public String getBuchungstext() {
        return buchungstext;
    }

    public String getBetrag() {
        return betrag;
    }

    @SuppressWarnings("unused")
    public String getWaehrung() {
        return waehrung;
    }

}
