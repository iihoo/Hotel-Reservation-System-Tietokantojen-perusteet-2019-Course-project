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
public class HuoneDao implements Dao<Huone, Integer> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Samalla kun lisätään uuden huoneen tiedot tietokantataulukkoon (kyseinen huone on tässä metodin parametrina),
    // niin metodi palauttaa automaattisesti luodun (primary key) indeksin, jota voidaan sitten käyttää tarpeen mukaan.
    @Override
    public Integer create(Huone huone) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Huone (numero, hinta, tyyppi) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, huone.getNumero());
                    stmt.setInt(2, huone.getPaivahinta());
                    stmt.setString(3, huone.getTyyppi());
                    return stmt;
        }, keyHolder);
        
        int id = (int) keyHolder.getKey();
        return id;
    }

    @Override
    public Huone read(Integer key) throws SQLException {
        
        List<Huone> huoneet = jdbcTemplate.query("SELECT * FROM Huone WHERE id = ?", (rs, rowNum) -> 
                new Huone(rs.getInt("id"), rs.getInt("numero"), rs.getInt("hinta"), rs.getString("tyyppi")), key);
        
        if (huoneet.isEmpty()) {
            return null;
        }
        
        return huoneet.get(0);
        
    }

    @Override
    public Huone update(Huone huone) throws SQLException {
        jdbcTemplate.update("UPDATE Huone SET hinta = ?, tyyppi = ?, numero = ? WHERE id = ?",
                huone.getPaivahinta(), huone.getTyyppi(), huone.getNumero(), huone.getId());
        return huone;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM Huone WHERE id = ?", key);
    }

    @Override
    public List<Huone> list() throws SQLException {
        return jdbcTemplate.query("SELECT id, numero, hinta, tyyppi FROM Huone;", (rs, rowNum) -> new Huone(rs.getInt("id"), rs.getInt("numero"), rs.getInt("hinta"), rs.getString("tyyppi")));
    }
}
