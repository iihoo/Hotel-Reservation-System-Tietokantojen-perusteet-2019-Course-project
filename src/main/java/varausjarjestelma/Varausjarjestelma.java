package varausjarjestelma;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Varausjarjestelma {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    AsiakasDao asiakasDao;

    @Autowired
    HuoneDao huoneDao;

    @Autowired
    VarausDao varausDao;

    @Autowired
    HuoneVarausDao huonevarausDao;

    @Autowired
    LisavarusteDao lisavarusteDao;

    @Autowired
    VarausLisavarusteDao varauslisavarusteDao;

    public void lisaaHuone(Integer numero, Integer hinta, String tyyppi) throws SQLException {
        Huone h = new Huone(numero, hinta, tyyppi);
        huoneDao.create(h);
    }

    public List<Huone> listaaHuoneet() throws SQLException {
        List<Huone> huoneet = huoneDao.list();
        return huoneet;

    }

    public void lisaaVaraus(Date alku, Date loppu, Asiakas a, List<Huone> vapaatHuoneet, Integer huoneita, List<String> lisavarusteet) throws SQLException {
        // lisätään asiakas Asiakas-tauluun, samalla tuodaan lisätyn asiakkaan indeksi
        int asiakasId = asiakasDao.create(a);
        a.setId(asiakasId);
        
        // luodaan varaus eli lisätään varaus Varaus-taulukkoon, samalla tuodaan lisätyn varauksen indeksi
        Varaus v = new Varaus(alku, loppu, a);
        int varausId = varausDao.create(v);
        v.setId(varausId);

        // Sitten lisätään varaukseen liittyvät huoneet HuoneVaraus-taulukkoon
        // (Järjestetään huoneet kalleimmasta halvimpaan ensin)
        Collections.sort(vapaatHuoneet);
        int i = 0;
        while (huoneita > 0) {
            Huone varattava = vapaatHuoneet.get(i);
            HuoneVaraus hv = new HuoneVaraus(v, varattava);
            huonevarausDao.create(hv);
            huoneita--;
            i++;
        }

        // Lisätään uudet lisävarusteet Lisävaruste-taulukkoon ja jo olemassa oleville lisävarusteille kasvatetaan sitä lukua,
        // joka kertoo kuinka monessa varauksessa kutakin lisävarustetta on käytetty.
        // Sitten lisätään varaukseen liittyvät lisävarusteet VarausLisävaruste-taulukkoon.
        lisaaLisavarusteet(lisavarusteet, v);

    }

    public void lisaaLisavarusteet(List<String> lisavarusteet, Varaus varaus) throws SQLException {
        List<Lisavaruste> olemassaOlevatLisavarusteet = lisavarusteDao.list();

        // Jo olemassa oleville lisävarusteille kasvatetaan sitä lukua,
        // joka kertoo kuinka monessa varauksessa kutakin lisävarustetta on käytetty.
        for (Lisavaruste l : olemassaOlevatLisavarusteet) {
            if (lisavarusteet.contains(l.getNimi())) {
                l.setVaraustenMaara(l.getVaraustenMaara() + 1);
                lisavarusteDao.update(l);

                VarausLisavaruste vlV = new VarausLisavaruste(varaus, l);
                varauslisavarusteDao.create(vlV);
                lisavarusteet.remove(l.getNimi());
            }
        }

        // Lisätään uudet lisävarusteet Lisävaruste-taulukkoon
        for (String nimi : lisavarusteet) {
            Lisavaruste uusiL = new Lisavaruste(nimi);
            int id = lisavarusteDao.create(uusiL);
            uusiL.setId(id);

            VarausLisavaruste vlV = new VarausLisavaruste(varaus, uusiL);
            varauslisavarusteDao.create(vlV);
        }

    }

    public List<Varaus> listaaVaraukset() throws SQLException {
        List<Varaus> varaukset = varausDao.list();
        return varaukset;
    }

    public List<HuoneVaraus> listaaHuoneVaraukset() throws SQLException {
        List<HuoneVaraus> hv = huonevarausDao.list();
        return hv;
    }

    public List<Huone> haeVapaatHuoneet(Date alku, Date loppu, String tyyppi, String hinta) throws SQLException {

        // Haetaan kaikki varaukset ja kaikki huoneet
        List<Varaus> varaukset = listaaVaraukset();
        List<Huone> huoneet = listaaHuoneet();
        List<HuoneVaraus> huonevaraukset = listaaHuoneVaraukset();

        // Tehdään uusi lista mihin tallennetaan vapaat huoneet
        List<Huone> vapaatHuoneet = new ArrayList<>();

        if (!tyyppi.equals("") && !hinta.equals("")) {
            for (Huone h : huoneet) {
                if (h.getTyyppi().equals(tyyppi) && h.getPaivahinta() <= Integer.parseInt(hinta)) {
                    vapaatHuoneet.add(h);
                }
            }
        } else if (tyyppi.equals("") && !hinta.equals("")) {
            for (Huone h : huoneet) {
                if (h.getPaivahinta() <= Integer.parseInt(hinta)) {
                    vapaatHuoneet.add(h);
                }
            }
        } else if (!tyyppi.equals("") && hinta.equals("")) {
            for (Huone h : huoneet) {
                if (h.getTyyppi().equals(tyyppi)) {
                    vapaatHuoneet.add(h);
                }
            }
        } else {
            for (Huone h : huoneet) {
                vapaatHuoneet.add(h);
            }
        }

        for (HuoneVaraus hv : huonevaraukset) {
            if ((hv.getVaraus().getAlkupvm().after(alku) && hv.getVaraus().getAlkupvm().before(loppu)) || (hv.getVaraus().getLoppupvm().after(alku) && hv.getVaraus().getLoppupvm().before(loppu)) || hv.getVaraus().getAlkupvm().before(alku) && hv.getVaraus().getLoppupvm().after(loppu)) {
                vapaatHuoneet.remove(hv.getHuone());
            }
        }

        return vapaatHuoneet;

    }

    public void tulostaVaraukset() throws SQLException {
        List<Varaus> varaukset = varausDao.list();
        Collections.sort(varaukset);
        for (Varaus v : varaukset) {
            System.out.println(v);
            System.out.println("");
        }
        
        if (varaukset.isEmpty()) {
            System.out.println("Ei varauksia.");
            System.out.println("");
        }
    }
    
    public void tulostaLisavarusteet() throws SQLException {
        List<Lisavaruste> lisavarusteet = lisavarusteDao.list();
        Collections.sort(lisavarusteet);
        
        if (lisavarusteet.size() > 10) {
            for (int i = 0; i < 10; i++) {
                System.out.println(lisavarusteet.get(i));
            }
        } else {
            for (int i = 0; i < lisavarusteet.size(); i++) {
                System.out.println(lisavarusteet.get(i));
            }
        }
    }
    
    public void tulostaAsiakkaat() throws SQLException {
        List<Asiakas> asiakkaat = asiakasDao.list();
        Collections.sort(asiakkaat);
        
        if (asiakkaat.size() > 10) {
            for (int i = 0; i < 10; i++) {
                System.out.println(asiakkaat.get(i));
            }
        } else {
            for (int i = 0; i < asiakkaat.size(); i++) {
                System.out.println(asiakkaat.get(i));
            }
        }
    }

}
