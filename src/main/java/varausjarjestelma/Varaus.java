package varausjarjestelma;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Varaus implements Comparable<Varaus> {

    Integer id;
    Date alku;
    Date loppu;
    Asiakas asiakas;
    List<HuoneVaraus> huonevaraukset;
    List<VarausLisavaruste> varauslisavarusteet;

    public Varaus(Integer id, Date alku, Date loppu, Asiakas asiakas) {
        this.id = id;
        this.alku = alku;
        this.loppu = loppu;
        this.asiakas = asiakas;
        this.huonevaraukset = new ArrayList<>();
        this.varauslisavarusteet = new ArrayList<>();
    }

    public Varaus(Date alku, Date loppu, Asiakas asiakas) {
        this.alku = alku;
        this.loppu = loppu;
        this.asiakas = asiakas;
        this.huonevaraukset = new ArrayList<>();
        this.varauslisavarusteet = new ArrayList<>();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getAlkupvm() {
        return this.alku;
    }

    public void setAlkupvm(Date alku) {
        this.alku = alku;
    }

    public Date getLoppupvm() {
        return this.loppu;
    }

    public void setLoppupvm(Date loppu) {
        this.loppu = loppu;
    }

    public Asiakas getAsiakas() {
        return this.asiakas;
    }

    public void setAsiakas(Asiakas asiakas) {
        this.asiakas = asiakas;
    }

    public List<HuoneVaraus> getHuoneVaraukset() {
        return this.huonevaraukset;
    }

    public void setHuoneVaraukset(List<HuoneVaraus> huonevaraukset) {
        this.huonevaraukset = huonevaraukset;
    }

    public List<VarausLisavaruste> getVarausLisavarusteet() {
        return this.varauslisavarusteet;
    }

    public void setVarausLisavarusteet(List<VarausLisavaruste> vlV) {
        this.varauslisavarusteet = vlV;
    }

    public String toString() {
        int paivienMaara = (int) ((this.loppu.getTime() - this.alku.getTime()) / 1000 / 60 / 60 / 24);
        String paiva = " päivää";
        String huoneTeksti = " huonetta.";

        if (paivienMaara == 1) {
            paiva = " päivä";
        }

        if (this.huonevaraukset.size() == 1) {
            huoneTeksti = " huone.";
        }

        int paivasumma = 0;
        String huoneet = "";
        for (HuoneVaraus hv : this.huonevaraukset) {
            huoneet += "\n" + "\t" + hv.getHuone();
            paivasumma += hv.getHuone().getPaivahinta();
        }

        return this.asiakas.getNimi() + ", " + this.asiakas.getSahkoposti() + ", "
                + this.alku + ", " + this.loppu + ", " + paivienMaara
                + paiva + ", " + this.varauslisavarusteet.size() + " lisävarustetta, " + this.huonevaraukset.size()
                + huoneTeksti + " Huoneet:" + huoneet + "\n" + "\t" + "Yhteensä: " + (paivasumma * paivienMaara) + " euroa";
    }

    @Override
    public int compareTo(Varaus o) {
        if (this.getAlkupvm().before(o.getAlkupvm())) {
            return -1;
        } else if (this.getAlkupvm().after(o.getAlkupvm())) {
            return 1;
        } else {
            return 0;
        }
    }

}
