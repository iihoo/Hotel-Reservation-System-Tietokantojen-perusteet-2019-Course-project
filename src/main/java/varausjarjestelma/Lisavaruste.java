package varausjarjestelma;

import java.util.ArrayList;
import java.util.List;

public class Lisavaruste implements Comparable<Lisavaruste> {

    Integer id;
    String nimi;
    Integer varausten_maara;
    List<VarausLisavaruste> lisavarusteet;

    public Lisavaruste(Integer id, String nimi, Integer varausten_maara) {
        this.id = id;
        this.nimi = nimi;
        this.varausten_maara = varausten_maara;
        this.lisavarusteet = new ArrayList<>();
    }

    public Lisavaruste(String nimi) {
        this.nimi = nimi;
        this.varausten_maara = 1;
        this.lisavarusteet = new ArrayList<>();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return this.nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Integer getVaraustenMaara() {
        return this.varausten_maara;
    }

    public void setVaraustenMaara(Integer varaustenMaara) {
        this.varausten_maara = varaustenMaara;
    }

    public List<VarausLisavaruste> getLisavarusteet() {
        return this.lisavarusteet;
    }

    public void setLisavarusteet(List<VarausLisavaruste> l) {
        this.lisavarusteet = l;
    }
    
    @Override
    public String toString() {
        String varausta = " varausta";
        if (this.varausten_maara == 1) {
            varausta = " varaus";
        }
        return this.nimi + ", " + this.varausten_maara + varausta;
    }
    
    @Override
    public int compareTo(Lisavaruste o) {
        if (this.getVaraustenMaara() > o.getVaraustenMaara()) {
            return -1;
        } else if (this.getVaraustenMaara() < o.getVaraustenMaara()) {
            return 1;
        } else {
            return 0;
        }
    }
}
