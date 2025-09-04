package init.upinmcSE.repository.custom.impl.hibernate;

import init.upinmcSE.repository.custom.AuthorRepository;
import init.upinmcSE.model.Author;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class AuthorHibernateRepository implements AuthorRepository<Session> {
    @Override
    public Optional<Author> getByName(String name, Session conn) throws Exception {
        Author author = conn.createQuery("FROM Author a WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .uniqueResult();
        return Optional.ofNullable(author);
    }

    @Override
    public Author insertOne(Author object, Session conn) throws Exception {
        conn.persist(object);
        return object;
    }

    @Override
    public Author updateOne(Author object, Session conn) throws Exception {
        return (Author) conn.merge(object);
    }

    @Override
    public void deleteOne(Author object, Session conn) throws Exception {
        conn.remove(object);
    }

    @Override
    public List<Author> getAll(Session conn) throws Exception {
        return conn.createQuery("FROM Author", Author.class)
                .getResultList();
    }
}
