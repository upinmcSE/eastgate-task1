package init.upinmcSE.service.v2;

import init.upinmcSE.db.HibernateUtil;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.model.PatronBook;
import init.upinmcSE.model.Status;
import init.upinmcSE.repository.custom.BookRepository;
import init.upinmcSE.repository.custom.PatronRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class PatronService {
    private static final Logger LOGGER = Logger.getLogger(PatronService.class.getName());
    private PatronRepository patronDAO;
    private BookRepository bookDAO;

    public PatronService() {}

    public PatronService(PatronRepository patronDAO, BookRepository bookDAO) {
        this.patronDAO = patronDAO;
        this.bookDAO = bookDAO;
    }

    public Integer insertPatron(Patron patron) {
        if(Objects.isNull(patron)) {
            LOGGER.warning("patron is null");
            return 0;
        }
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            Optional<Patron> patranCheck = patronDAO.getByName(patron.getName(), session);
            if(patranCheck.isPresent()) {
                LOGGER.warning("patron already exists");
                return 0;
            }

            Patron newPatron = (Patron) patronDAO.insertOne(patron, session);

            tx.commit();
            return newPatron.getId();
        }catch (Exception e){
            if(tx != null) tx.rollback();
            HibernateUtil.printHibernateException(e);
        }
        return 0;
    }

    public Optional<Patron> getPatronByName(String name) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Optional<Patron> patranCheck = patronDAO.getByName(name, session);
            if(patranCheck.isPresent()) {
                return Optional.of(patranCheck.get());
            }
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return Optional.empty();
    }

    public List<Patron> getAllPatrons() {
        List<Patron> patrans = new ArrayList<>();
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            patrans = patronDAO.getAll(session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return patrans;
    }

    public String borrowBook(Patron patron, Book book) {
        return "";
    }

    public String returnBook(Patron patron, Book book) {
        if (Objects.isNull(patron) || Objects.isNull(book)) {
            LOGGER.warning("Inputs is null");
            return "Inputs is null";
        }

        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            Optional<Book> bookCheck = bookDAO.getByName(book.getName(), session);
            if (bookCheck.isEmpty()) {
                return "Book not found";
            }

            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), session);
            if (patronCheck.isEmpty()) {
                return "Patron not found";
            }

            Query query = session.createQuery(
                    "update PatronBook pb set pb.status = 'OFF' " +
                            "where pb.patron = :patron and pb.book = :book and pb.status = 'ON'");
            query.setParameter("patron", patronCheck.get());
            query.setParameter("book", bookCheck.get());
            int updated = query.executeUpdate();

            if (updated <= 0) {
                return "Cannot find debit note for return";
            }

            Book b = bookCheck.get();
            b.setAvailableCount(b.getAvailableCount() + 1);
            b.setBorrowedCount(b.getBorrowedCount() - 1);
            session.update(b);
            return "Return successfully";
        }catch (Exception e){
            if(tx != null) tx.rollback();
            HibernateUtil.printHibernateException(e);
        }

        return "Return failed";
    }

    public String borrowBookOptimistic(Patron patron, Book book) {
        if (Objects.isNull(patron) || Objects.isNull(book)) {
            return "Inputs is null";
        }
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), session);
            if (patronCheck.isEmpty()) {
                return "Patron not found";
            }

            Book existingBook = session.createQuery("FROM Book WHERE name = :name", Book.class)
                    .setParameter("name", book.getName())
                    .setLockMode(LockModeType.OPTIMISTIC)
                    .uniqueResult();

            if (existingBook == null) {
                return "Book not found";
            }

            boolean alreadyBorrowed = session.createQuery(
                            "SELECT COUNT(*) > 0 FROM PatronBook WHERE patron.id = :patronId AND book.id = :bookId AND status = 'ON'",
                            Boolean.class)
                    .setParameter("patronId", patronCheck.get().getId())
                    .setParameter("bookId", existingBook.getId())
                    .uniqueResult();

            if (alreadyBorrowed) {
                return "Book already borrowed by you";
            }

            if (existingBook.getAvailableCount() <= 0) {
                return "Book not available";
            }

            existingBook.setAvailableCount(existingBook.getAvailableCount() - 1);
            existingBook.setBorrowedCount(existingBook.getBorrowedCount() + 1);
            session.merge(existingBook);

            PatronBook patronBook = new PatronBook();
            patronBook.setPatron(patronCheck.get());
            patronBook.setBook(existingBook);
            patronBook.setStatus(Status.ON);
            session.persist(patronBook);

            transaction.commit();
            return "Borrowed book successfully";
        }catch (Exception e){
            if(transaction != null) transaction.rollback();
            HibernateUtil.printHibernateException(e);
        }
        return "";
    }

    public String borrowBookPessimistic(Patron patron, Book book) {
        if (Objects.isNull(patron) || Objects.isNull(book)) {
            return "Inputs is null";
        }
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            Optional<Patron> existingPatron = patronDAO.getByName(patron.getName(), session);

            if (existingPatron.isEmpty()) {
                return "Patron not found";
            }

            Book lockedBook = session.createQuery("FROM Book WHERE name = :name", Book.class)
                    .setParameter("name", book.getName())
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setHint("jakarta.persistence.lock.timeout", 2000) // 2 second timeout
                    .uniqueResult();

            if (lockedBook == null) {
                return "Book with name " + book.getName() + " not found";
            }

            boolean alreadyBorrowed = session.createQuery(
                            "SELECT COUNT(*) > 0 FROM PatronBook WHERE patron.id = :patronId AND book.id = :bookId AND status = 'ON'",
                            Boolean.class)
                    .setParameter("patronId", existingPatron.get().getId())
                    .setParameter("bookId", lockedBook.getId())
                    .uniqueResult();

            if (alreadyBorrowed) {
                return "This book already borrowed by you";
            }

            if (lockedBook.getAvailableCount() <= 0) {
                return "unavailable book";
            }

            lockedBook.setAvailableCount(lockedBook.getAvailableCount() - 1);
            lockedBook.setBorrowedCount(lockedBook.getBorrowedCount() + 1);
            session.merge(lockedBook);


            PatronBook patronBook = new PatronBook();
            patronBook.setPatron(existingPatron.get());
            patronBook.setBook(lockedBook);
            patronBook.setStatus(Status.ON);
            session.persist(patronBook);

            tx.commit();
            return "Borrowed book successfully";
        }catch (Exception e){
            if(tx != null) tx.rollback();
            HibernateUtil.printHibernateException(e);
        }
        return "Borrow book failed";
    }
}
