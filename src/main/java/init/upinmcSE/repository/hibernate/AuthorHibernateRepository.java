package init.upinmcSE.repository.hibernate;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.model.Author;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class AuthorHibernateRepository implements AuthorDAO<Session> {
    @Override
    public Optional<Author> getByName(String name, Session conn) throws Exception {
        Author author = conn.createQuery(
                        "FROM Author a WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .uniqueResult();
        return Optional.ofNullable(author);
    }

    @Override
    public Author insertOne(Author object, Session conn) throws Exception {
        Transaction tx = null;
        try {
            tx = conn.beginTransaction();
            conn.save(object);
            tx.commit();
            return object;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Author updateOne(Author object, Session conn) throws Exception {
        Transaction tx = null;
        try{
            tx = conn.beginTransaction();
            conn.saveOrUpdate(object);
            tx.commit();
            return object;
        }catch (Exception e){
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void deleteOne(Author object, Session conn) throws Exception {
        Transaction tx = null;
        try{
            tx = conn.beginTransaction();
            conn.delete(object);
            tx.commit();
        }catch (Exception e){
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Author> getAll(Session conn) throws Exception {
        return conn.createQuery("FROM Author", Author.class)
                .getResultList();
    }
}
