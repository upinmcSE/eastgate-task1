package init.upinmcSE.repository.custom.impl.hibernate;

import init.upinmcSE.repository.custom.PatronRepository;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PatronHibernateRepository implements PatronRepository<Session> {
    @Override
    public Optional<Patron> getByName(String name, Session conn) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean checkBorrowBook(Patron patron, Book book, Session conn) throws Exception {
        return false;
    }

    @Override
    public Patron insertOne(Patron object, Session conn) throws Exception {
        return null;
    }

    @Override
    public Patron updateOne(Patron object, Session conn) throws Exception {
        return null;
    }

    @Override
    public void deleteOne(Patron object, Session conn) throws Exception {

    }

    @Override
    public List<Patron> getAll(Session conn) throws Exception {
        return List.of();
    }
}
