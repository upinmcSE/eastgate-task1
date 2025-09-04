package init.upinmcSE.repository;

import java.util.List;


public interface CrudRepository<T, C> {
    public T insertOne(T object, C conn) throws Exception;
    public T updateOne(T object, C conn) throws Exception;
    public void deleteOne(T object, C conn) throws Exception;
    public List<T> getAll(C conn) throws Exception;
}
