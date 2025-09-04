package init.upinmcSE.repository.custom.impl.hibernate;

import init.upinmcSE.repository.custom.PatronRepository;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PatronHibernateRepository implements PatronRepository<Session> {
    @Override
    public Optional<Patron> getByName(String name, Session conn) throws Exception {
        Query<Patron> query = conn.createQuery(
                "FROM Patron p WHERE p.name = :name", Patron.class);
        query.setParameter("name", name);
        Patron patron = query.uniqueResult();
        return Optional.ofNullable(patron);
    }

    @Override
    public boolean checkBorrowBook(Patron patron, Book book, Session conn) throws Exception {
        Long count = conn.createQuery("select count(pb) from PatronBook pb " +
                        "where pb.patron = :patron and pb.book = :book and pb.status = 'ON'",
                 Long.class)
                .setParameter("patron", patron)
                .setParameter("book", book)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public Patron insertOne(Patron object, Session conn) throws Exception {
        conn.save(object);
        return object;
    }

    @Override
    public Patron updateOne(Patron object, Session conn) throws Exception {
        conn.saveOrUpdate(object);
        return object;
    }

    @Override
    public void deleteOne(Patron object, Session conn) throws Exception {
        conn.delete(object);
    }

    @Override
    public List<Patron> getAll(Session conn) throws Exception {
        return conn.createQuery("from Patron", Patron.class).list();
    }
}
