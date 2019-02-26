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
public class VarausLisavarusteDao implements Dao<VarausLisavaruste, Integer> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    VarausDao varausDao;

    @Autowired
    LisavarusteDao lisavarusteDao;

    // Samalla kun lisätään uuden varauslisävarusteen tiedot tietokantataulukkoon (kyseinen varauslisävaruste on tässä metodin parametrina),
    // niin metodi palauttaa automaattisesti luodun (primary key) indeksin, jota voidaan sitten käyttää tarpeen mukaan.
    @Override
    public Integer create(VarausLisavaruste vlV) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO VarausLisavaruste (varaus_id, lisavaruste_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, vlV.varaus.getId());
            stmt.setInt(2, vlV.lisavaruste.getId());
            return stmt;
        }, keyHolder);

        int id = (int) keyHolder.getKey();
        return id;
    }

    @Override
    public VarausLisavaruste read(Integer key) throws SQLException {
        List<VarausLisavaruste> vl_varusteet = jdbcTemplate.query("SELECT * FROM VarausLisavaruste WHERE id = ?", (rs, rowNum)
                -> new VarausLisavaruste(rs.getInt("id"), varausDao.read(rs.getInt("varaus_id")), lisavarusteDao.read(rs.getInt("lisavaruste_id"))), key);

        if (vl_varusteet.isEmpty()) {
            return null;
        }

        return vl_varusteet.get(0);
    }

    @Override
    public VarausLisavaruste update(VarausLisavaruste vlV) throws SQLException {
        jdbcTemplate.update("UPDATE VarausLisavaruste SET varaus_id = ?, lisavaruste_id = ? WHERE id = ?",
                vlV.getVaraus().getId(), vlV.getLisavaruste().getId(), vlV.getId());
        return vlV;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM VarausLisavaruste WHERE id = ?", key);
    }

    @Override
    public List<VarausLisavaruste> list() throws SQLException {
        return jdbcTemplate.query("SELECT id, varaus_id, lisavaruste_id FROM VarausLisavaruste;",(rs, rowNum) -> 
                new VarausLisavaruste(rs.getInt("id"), varausDao.read(rs.getInt("varaus_id")), lisavarusteDao.read(rs.getInt("lisavaruste_id"))));
    }

}
