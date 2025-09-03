package init.upinmcSE.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public interface CrudDAO<T, ID> {
    public T insertOne(T object, Connection conn) throws SQLException;
    public T updateOne(T object, Connection conn) throws SQLException;
    public void deleteOne(ID id, Connection conn) throws SQLException;
    public List<T> getAll(Connection conn) throws SQLException;
}
