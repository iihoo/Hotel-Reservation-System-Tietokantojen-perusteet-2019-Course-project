package varausjarjestelma;

import java.util.ArrayList;
import java.util.List;

public class Asiakas implements Comparable<Asiakas> {

    Integer id;
    String nimi;
    String numero;
    String email;
    List<Varaus> varaukset;

    public Asiakas(Integer id, String nimi, String numero, String email) {
        this.id = id;
        this.nimi = nimi;
        this.numero = numero;
        this.email = email;
        this.varaukset = new ArrayList<>();
    }

    public Asiakas(String nimi, String numero, String email) {
        this.nimi = nimi;
        this.numero = numero;
        this.email = email;
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

    public String getNumero() {
        return this.numero;
    }

    public void setPuhelinnumero(String numero) {
        this.numero = numero;
    }

    public String getSahkoposti() {
        return this.email;
    }

    public void setSahkoposti(String email) {
        this.email = email;
    }
    
    // Tällä lasketaan paljonko asiakas on yhteensä kuluttanut hotellivarauksiin
    public Integer varaustenSumma() {
        int varausten_summa = 0;
        for (Varaus v : this.varaukset) {
            int paivasumma = 0;
            int paivienMaara = (int) ((v.loppu.getTime() - v.alku.getTime()) / 1000 / 60 / 60 / 24);
            for (HuoneVaraus hv : v.huonevaraukset) {
                paivasumma += hv.getHuone().getPaivahinta();
            }
            varausten_summa += paivasumma * paivienMaara;
        }
        return varausten_summa;
    }
    
    @Override
    public String toString() {
        return this.nimi + ", " + this.email + ", " + this.numero + ", " + this.varaustenSumma() + " euroa";
    }
    
    @Override
    public int compareTo(Asiakas o) {
        int summa_a = this.varaustenSumma();
        int summa_o = o.varaustenSumma();
        
        if (summa_a < summa_o) {
            return 1;
        } else if (summa_a > summa_o) {
            return -1;
        } else {
            return 0;
        }
    }

}
