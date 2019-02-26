
package varausjarjestelma;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class VarausDao implements Dao<Varaus, Integer> {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    HuoneVarausDao huonevarausDao;
    
    @Autowired
    VarausLisavarusteDao varauslisavarusteDao;
    
    @Autowired
    AsiakasDao asiakasDao;

    // Samalla kun lisätään uuden varauksen tiedot tietokantataulukkoon (kyseinen varaus on tässä metodin parametrina),
    // niin metodi palauttaa automaattisesti luodun (primary key) indeksin, jota voidaan sitten käyttää tarpeen mukaan.
    @Override
    public Integer create(Varaus varaus) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Varaus (alkupvm, loppupvm, asiakas_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                    stmt.setDate(1, varaus.getAlkupvm());
                    stmt.setDate(2, varaus.getLoppupvm());
                    stmt.setInt(3, varaus.getAsiakas().getId());
                    return stmt;
        }, keyHolder);
        
        int id = (int) keyHolder.getKey();
        return id;
    }

    @Override
    public Varaus read(Integer key) throws SQLException {
        
        List<Varaus> varaukset = jdbcTemplate.query("SELECT id, alkupvm, loppupvm, asiakas_id FROM Varaus WHERE id = ?", (rs, rowNum) -> 
                new Varaus(rs.getInt("id"), rs.getDate("alkupvm"), rs.getDate("loppupvm"), asiakasDao.read(rs.getInt("asiakas_id"))), key);
        
        if (varaukset.isEmpty()) {
            return null;
        }
        
        return varaukset.get(0);
    }

    @Override
    public Varaus update(Varaus varaus) throws SQLException {
        jdbcTemplate.update("UPDATE Varaus SET alkupvm = ?, loppupvm = ?, asiakas_id = ? WHERE id = ?",
                varaus.getAlkupvm(), varaus.getLoppupvm(), varaus.getAsiakas().getId(), varaus.getId());    
        return varaus;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM Varaus WHERE id = ?",
                key);
    }

    @Override
    public List<Varaus> list() throws SQLException {
        
        List<Varaus> varauksetIlmanHuoneitaJaVarusteita = jdbcTemplate.query("SELECT id, alkupvm, loppupvm, asiakas_id FROM Varaus", 
                (rs, rowNum) -> new Varaus(rs.getInt("id"), rs.getDate("alkupvm"), rs.getDate("loppupvm"), asiakasDao.read(rs.getInt("asiakas_id"))));
        
        List<HuoneVaraus> huonevaraukset = huonevarausDao.list();
        
        List<VarausLisavaruste> varauslisavarusteet = varauslisavarusteDao.list();
        
        List<Varaus> varaukset = varauksetIlmanHuoneitaJaVarusteita;
        
        for (Varaus v : varaukset) {
            for (HuoneVaraus hv : huonevaraukset) {
                int a = hv.getVaraus().getId();
                int b = v.getId();
                if (a == b) {
                    List<HuoneVaraus> huoneet = v.getHuoneVaraukset();
                    huoneet.add(hv);
                    v.setHuoneVaraukset(huoneet);
                }
            }
            
            for (VarausLisavaruste vlV : varauslisavarusteet) {
                int a = vlV.getVaraus().getId();
                int b = v.getId();
                if (a == b) {
                    List<VarausLisavaruste> lisavarusteet = v.getVarausLisavarusteet();
                    lisavarusteet.add(vlV);
                    v.setVarausLisavarusteet(lisavarusteet);
                }
            }
        }
        
        return varaukset;
    }
    
}
