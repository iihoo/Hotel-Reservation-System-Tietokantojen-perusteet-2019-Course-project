
package varausjarjestelma;

public class HuoneVaraus {
    Integer id;
    Varaus varaus;
    Huone huone;
    
    public HuoneVaraus(Integer id, Varaus varaus, Huone huone) {
        this.id = id;
        this.varaus = varaus;
        this.huone = huone;
    }
    
    public HuoneVaraus(Varaus v, Huone h) {
        this.varaus = v;
        this.huone = h;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer i) {
        this.id = i;
    }
    
    public Varaus getVaraus() {
        return this.varaus;
    }
    
    public void setVaraus(Varaus varaus) {
        this.varaus = varaus;
    } 
    
    public Huone getHuone() {
        return this.huone;
    }
    
    public void setHuone(Huone huone) {
        this.huone = huone;
    }
}
