

package varausjarjestelma;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Huone implements Comparable <Huone> {
    Integer id;
    Integer numero;
    Integer paivahinta;
    String tyyppi;
    List<HuoneVaraus> huonevaraukset;
     
    public Huone(Integer id, Integer numero, Integer paivahinta, String tyyppi) {
        this.id = id;
        this.numero = numero;
        this.paivahinta = paivahinta;
        this.tyyppi = tyyppi;
        this.huonevaraukset = new ArrayList<>();
    }
    
    public Huone(Integer numero, Integer paivahinta, String tyyppi) {
        this.numero = numero;
        this.paivahinta = paivahinta;
        this.tyyppi = tyyppi;
        this.huonevaraukset = new ArrayList<>();
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getNumero() {
        return this.numero;
    }
    
    public void setNumero(Integer numero) {
        this.numero = numero;
    }
    
    public Integer getPaivahinta() {
        return this.paivahinta;
    }
    
    public void setPaivahinta(Integer hinta) {
        this.paivahinta = hinta;
    }
    
    public String getTyyppi() {
        return this.tyyppi;
    }
    
    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }
    
    public List<HuoneVaraus> getHuoneVaraukset() {
        return this.huonevaraukset;
    }
    
    public void setHuoneVaraukset(List<HuoneVaraus> huonevaraukset) {
        this.huonevaraukset = huonevaraukset;
    }
    
    @Override
    public String toString() {
        String apuNumero = "";
        if (this.numero < 10) {
            apuNumero = "00" + this.numero;
        } else if (this.numero < 100) {
            apuNumero = "0" + this.numero;
        } else {
            apuNumero = "" + this.numero;
        }
        return this.tyyppi + ", "  + apuNumero + ", " + this.paivahinta + " euroa";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.id);
        hash = 11 * hash + Objects.hashCode(this.numero);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Huone other = (Huone) obj;
        if (!Objects.equals(this.numero, other.numero)) {
            return false;
        } else if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        
        return true;
    }

    // Tämän avulla voidaan järjestää huoneet järjestykseen kalleimmasta halvimpaan
    @Override
    public int compareTo(Huone o) {
        if (this.getPaivahinta() < o.getPaivahinta()) {
            return 1;
        } else if (this.getPaivahinta() > o.getPaivahinta()) {
            return -1;
        } else {
            return 0;
        }
    }
   
    
}
