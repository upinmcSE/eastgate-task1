package init.upinmcSE.dao;

import java.util.List;


public interface CrudDAO<T, C, ID> {
    public T insertOne(T object, C conn) throws Exception;
    public T updateOne(T object, C conn) throws Exception;
    public void deleteOne(T object, C conn) throws Exception;
    public List<T> getAll(C conn) throws Exception;
}
