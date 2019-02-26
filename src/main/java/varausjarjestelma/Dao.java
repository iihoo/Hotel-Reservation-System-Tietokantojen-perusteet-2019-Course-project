
package varausjarjestelma;

import java.sql.*;
import java.util.*;

public interface Dao<T, K> {
    Integer create(T object) throws SQLException;   // void
    T read(K key) throws SQLException;
    T update(T object) throws SQLException;
    void delete(K key) throws SQLException;
    List<T> list() throws SQLException;
}
