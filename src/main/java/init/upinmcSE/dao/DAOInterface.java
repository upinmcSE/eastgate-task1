package init.upinmcSE.dao;

import java.sql.Connection;
import java.util.List;

public interface DAOInterface<T> {
    public int insertOne(T object, Connection conn) throws Exception;
    public int updateOne(T object, Connection conn) throws Exception;
    public int deleteOne(String name, Connection conn) throws Exception;
    public T getByName(String name, Connection conn) throws Exception;
    public List<T> getAll(Connection conn) throws Exception;
    public List<T> getByCondition(Connection conn) throws Exception;
}
