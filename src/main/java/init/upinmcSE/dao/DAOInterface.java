package init.upinmcSE.dao;

import java.util.List;

public interface DAOInterface<T> {
    public int insertOne(T object);
    public int insertMany(List<T> objects);
    public int updateOne(T object);
    public int deleteOne(int id);
    public T getOne(int id);
    public T getByID(int id);
    public List<T> getAll();
    public List<T> getByCondition();
}
