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

    public void setBuchungstag(String buchungstag) {
        this.buchungstag = buchungstag;
    }

    public String getWertstellung() {
        return wertstellung;
    }

    public void setWertstellung(String wertstellung) {
        this.wertstellung = wertstellung;
    }

    public String getBuchungstext() {
        return buchungstext;
    }

    public void setBuchungstext(String buchungstext) {
        this.buchungstext = buchungstext;
    }

    public String getBetrag() {
        return betrag;
    }

    public void setBetrag(String betrag) {
        this.betrag = betrag;
    }

    public String getWaehrung() {
        return waehrung;
    }

    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }
}
