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
public class HuoneVarausDao implements Dao<HuoneVaraus, Integer> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    VarausDao varausDao;

    @Autowired
    HuoneDao huoneDao;
    
    // Samalla kun lisätään uuden huonevarauksen tiedot tietokantataulukkoon (kyseinen huonevaraus on tässä metodin parametrina),
    // niin metodi palauttaa automaattisesti luodun (primary key) indeksin, jota voidaan sitten käyttää tarpeen mukaan.
    @Override
    public Integer create(HuoneVaraus huonevaraus) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO HuoneVaraus (varaus_id, huone_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, huonevaraus.getVaraus().getId());
            stmt.setInt(2, huonevaraus.getHuone().getId());
            return stmt;
        }, keyHolder);

        int id = (int) keyHolder.getKey();
        return id;
    }

    @Override
    public HuoneVaraus read(Integer key) throws SQLException {
        List<HuoneVaraus> huonevaraukset = jdbcTemplate.query("SELECT * FROM HuoneVaraus WHERE id = ?", (rs, rowNum)
                -> new HuoneVaraus(rs.getInt("id"), varausDao.read(rs.getInt("varaus_id")), huoneDao.read(rs.getInt("huone_id"))), key);

        if (huonevaraukset.isEmpty()) {
            return null;
        }

        return huonevaraukset.get(0);

    }

    @Override
    public HuoneVaraus update(HuoneVaraus huonevaraus) throws SQLException {
        jdbcTemplate.update("UPDATE HuoneVaraus SET varaus_id = ?, huone_id = ? WHERE id = ?",
                huonevaraus.getVaraus().getId(), huonevaraus.getHuone().getId(), huonevaraus.getId());
        return huonevaraus;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM HuoneVaraus WHERE id = ?", key);
    }
    
    // Luetaan HuoneVaraus-taulukosta tiedot. Taulukon tietojen mukaisesti luodaan jokaista HuoneVaraus-taulukon riviä kohti uusi HuoneVaraus-olio,
    // jonka konstruktorissa käytetään hyväksi varausDao.read()- ja huoneDao.read()-metodeja.
    @Override                                                                       
    public List<HuoneVaraus> list() throws SQLException {
        return jdbcTemplate.query("SELECT id, varaus_id, huone_id FROM HuoneVaraus;", (rs, rowNum) -> new HuoneVaraus(rs.getInt("id"), varausDao.read(rs.getInt("varaus_id")), huoneDao.read(rs.getInt("huone_id"))));
    }

}
