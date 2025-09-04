package init.upinmcSE.service.v2;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.HibernateUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;

    public BookService(BookDAO bookDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
    }

    public Integer insertBook(Book book){
        if(Objects.isNull(book)) {
            LOGGER.warning("book is null");
            return 0;
        }
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            Optional<Book> bookSearch = bookDAO.getByName(book.getName(), session);
            int bookId;
            if(bookSearch.isPresent()) {
                LOGGER.warning("book already exists");
                return bookSearch.get().getId();
            }else{
                Book insertBook = (Book) bookDAO.insertOne(book, session);
                bookId = insertBook.getId();
                LOGGER.info(bookId + " inserted");
            }

            if (bookId == 0) {
                tx.rollback();
                LOGGER.warning("Add new book failed");
                return 0;
            }

            for(Author author : book.getAuthors()){
                int authorId;
                Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), session);

                if(existingAuthor.isPresent()) {
                    authorId = existingAuthor.get().getId();
                }else{
                    Author insertAuthor = (Author) authorDAO.insertOne(author, session);
                    authorId = insertAuthor.getId();
                }

                if (authorId == 0) {
                    tx.rollback();
                    LOGGER.warning("Add author failed");
                    return 0;
                }

                int relationResult = bookDAO.insertRelation(bookId, authorId, session);
                LOGGER.info(relationResult + " insertedRelation");
                if (relationResult <= 0) {
                    tx.rollback();
                    LOGGER.warning("Add relative book-author failed");
                    return 0;
                }
            }
            tx.commit();
            return bookId;
        }catch (Exception e){
            if(tx != null) tx.rollback();
            HibernateUtil.printHibernateException(e);
            return 0;
        }
    }

    public Optional<Book> getBookByName(String name){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return bookDAO.getByName(name, session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return Optional.empty();
    }

    public List<Book> getAllBooks(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return bookDAO.getAll(session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return List.of();
    }

    public void deleteBook(String name){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Optional<Book> book = getBookByName(name);
            if(book.isEmpty()) {
                LOGGER.warning("book not exists");
                return;
            }
            bookDAO.deleteOne(book.get(), session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
    }

    public List<Book> getBookByAuthor(Author author){
        List<Book> books = new ArrayList<>();
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), session);
            if(existingAuthor.isEmpty()) {
                LOGGER.warning("Author not found");
                return books;
            }

            books = bookDAO.getBookByAuthor(existingAuthor.get().getId(), session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return books;
    }
}
