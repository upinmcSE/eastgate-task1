package init.upinmcSE.repository;

import java.util.List;


public interface CrudRepository<T, C> {
    T insertOne(T object, C conn) throws Exception;
    T updateOne(T object, C conn) throws Exception;
    void deleteOne(T object, C conn) throws Exception;
    List<T> getAll(C conn) throws Exception;
}
