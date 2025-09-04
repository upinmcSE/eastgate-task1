package init.upinmcSE.repository.custom.impl.hibernate;

import init.upinmcSE.repository.custom.BookRepository;
import init.upinmcSE.model.Book;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class BookHibernateRepository implements BookRepository<Session> {

    public BookHibernateRepository() {}

    @Override
    public Optional<Book> getByName(String name, Session conn) throws Exception {
        Book book = conn.createQuery("FROM Book WHERE name = :name", Book.class)
                .setParameter("name", name)
                .uniqueResult();
        return Optional.ofNullable(book);
    }

    @Override
    public Integer insertRelation(int bookID, int authorID, Session conn) throws Exception {
        String sql = "INSERT INTO book_author (book_id, author_id, status) VALUES (:bookId, :authorId, :status)";
        return conn.createNativeQuery(sql)
                .setParameter("bookId", bookID)
                .setParameter("authorId", authorID)
                .setParameter("status", "ON")
                .executeUpdate();
    }

    @Override
    public boolean checkAvailability(int bookID, Session conn) throws Exception {
        Book book = conn.get(Book.class, bookID);
        return book.getAvailableCount() > 0;
    }

    @Override
    public List<Book> getBookByAuthor(int authorID, Session conn) throws Exception {
        String hql = "SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorID";
        return conn.createQuery(hql, Book.class)
                .setParameter("authorID", authorID)
                .list();
    }

    @Override
    public Book insertOne(Book object, Session conn) throws Exception {
        conn.persist(object);
        return object;
    }

    @Override
    public Book updateOne(Book object, Session conn) throws Exception {
        return (Book) conn.merge(object);
    }

    @Override
    public void deleteOne(Book object, Session conn) throws Exception {
        conn.remove(object);
    }

    @Override
    public List<Book> getAll(Session conn) throws Exception {
        return conn.createQuery("FROM Book", Book.class)
                .getResultList();
    }
}
